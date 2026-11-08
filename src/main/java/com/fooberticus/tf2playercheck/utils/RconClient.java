package com.fooberticus.tf2playercheck.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * RCON client for talking to the local TF2 game client.
 * <p>
 * TF2 must be launched with RCON enabled, for example:
 * <pre>
 * -usercon -g15 +ip 0.0.0.0 +sv_rcon_whitelist_address 127.0.0.1 +hostport 27015 +rcon_password yourpassword +net_start
 * </pre>
 * See {@link #buildLaunchParameters(int, String)}.
 * Protocol reference: <a href="https://developer.valvesoftware.com/wiki/Source_RCON_Protocol">...</a>
 * <p>
 * All calls block on network IO. Do not call them on the Swing event dispatch thread.
 */
@Slf4j
public final class RconClient implements AutoCloseable {

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 27015;

    static final int SERVERDATA_AUTH = 3;
    static final int SERVERDATA_AUTH_RESPONSE = 2;
    static final int SERVERDATA_EXECCOMMAND = 2;
    static final int SERVERDATA_RESPONSE_VALUE = 0;

    private static final int TIMEOUT_MS = 5000;
    /** id + type + two null terminators. */
    private static final int MIN_PACKET_SIZE = 10;
    /** Sanity cap so a garbage size field can't make us allocate gigabytes. */
    private static final int MAX_PACKET_SIZE = 1 << 20;

    private final Socket socket;
    private final DataInputStream in;
    private final OutputStream out;
    private int nextId = 1;

    record Packet(int id, int type, byte[] body) {
        Packet(int id, int type, String body) {
            this(id, type, body.getBytes(StandardCharsets.UTF_8));
        }
    }

    public RconClient(String host, int port) throws IOException {
        Socket s = new Socket();
        try {
            s.connect(new InetSocketAddress(host, port), TIMEOUT_MS);
            s.setSoTimeout(TIMEOUT_MS);
            this.in = new DataInputStream(s.getInputStream());
            this.out = s.getOutputStream();
        } catch (ConnectException e) {
            s.close();
            throw new ConnectException("Could not connect to TF2 RCON at " + host + ":" + port
                    + ". Is TF2 running with -usercon and +net_start?");
        } catch (IOException e) {
            s.close();
            throw e;
        }
        this.socket = s;
    }

    /** Dumps the scoreboard data TF2 keeps for each player. Needs the -g15 launch option. */
    public static final String G15_COMMAND = "g15_dumpplayer";

    /**
     * Connects to the local TF2 client using the saved RCON port and password, runs
     * {@code g15_dumpplayer}, and returns the raw output. Parse it with {@link G15Parser#parse(String)}.
     * <p>
     * {@code status} can't be used here: on a server, TF2 forwards it to the server and the reply
     * arrives later as console text, so the RCON reply is empty.
     *
     * @throws IOException if TF2 is unreachable, the password is wrong or missing, or the read times out
     */
    public static String fetchG15Dump() throws IOException {
        return runCommand(DEFAULT_HOST, GuiUtil.getSavedRconPort(), GuiUtil.getSavedRconPassword(), G15_COMMAND);
    }

    /** True if {@code port} is a usable TCP port. */
    public static boolean isValidPort(int port) {
        return port >= 1 && port <= 65535;
    }

    /**
     * True if {@code password} is non-empty and safe to put in TF2 launch options.
     * Spaces, quotes and semicolons would break the launch options line.
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.matches("[^\\s\";]+");
    }

    /** True if the saved RCON port and password are usable. */
    public static boolean hasValidSavedSettings() {
        return isValidPort(GuiUtil.getSavedRconPort()) && isValidPassword(GuiUtil.getSavedRconPassword());
    }

    /**
     * Builds the TF2 launch options that enable RCON on the game client.
     * <p>
     * {@code -g15} enables {@code g15_dumpplayer}, which {@link #fetchG15Dump()} needs.
     * <p>
     * {@code +ip 0.0.0.0} matches what other TF2 RCON tools use; the RCON listener then accepts
     * connections from other machines too, so the password is the only protection.
     * {@code sv_rcon_whitelist_address 127.0.0.1} does not restrict access: it only stops the game
     * from banning this app after failed password attempts.
     */
    public static String buildLaunchParameters(int port, String password) {
        return "-usercon -g15 +ip 0.0.0.0 +sv_rcon_whitelist_address 127.0.0.1 +hostport " + port
                + " +rcon_password " + password + " +net_start";
    }

    /** Connects, authenticates, runs one console command and returns its output. */
    public static String runCommand(String host, int port, String password, String command) throws IOException {
        try (RconClient client = new RconClient(host, port)) {
            client.authenticate(password);
            return client.execute(command);
        }
    }

    /**
     * Authenticates this connection. Note that repeated failures make the game temporarily
     * ban this IP from RCON (sv_rcon_maxfailures / sv_rcon_banpenalty).
     */
    public void authenticate(String password) throws IOException {
        if (password == null || password.isBlank()) {
            throw new IOException("RCON password is not set.");
        }

        int authId = nextId++;
        writePacket(out, new Packet(authId, SERVERDATA_AUTH, password));

        try {
            // The game may send an empty RESPONSE_VALUE before the AUTH_RESPONSE; skip it.
            while (true) {
                Packet p = readPacket(in);
                if (p.type() != SERVERDATA_AUTH_RESPONSE) {
                    continue;
                }
                if (p.id() == -1) {
                    throw new IOException("RCON authentication failed. Check the RCON password.");
                }
                if (p.id() == authId) {
                    return;
                }
            }
        } catch (EOFException e) {
            throw new IOException("TF2 closed the RCON connection during authentication. "
                    + "Check the RCON password.", e);
        }
    }

    /**
     * Runs a console command and returns its full output.
     * <p>
     * Long output is split across several packets. To find the end, we send an empty
     * RESPONSE_VALUE packet right after the command. The game echoes it back only after
     * all of the command's output, so its echo marks the end of the reply.
     */
    public String execute(String command) throws IOException {
        int commandId = nextId++;
        int terminatorId = nextId++;

        writePacket(out, new Packet(commandId, SERVERDATA_EXECCOMMAND, command));
        writePacket(out, new Packet(terminatorId, SERVERDATA_RESPONSE_VALUE, ""));

        // Collect raw bytes and decode once at the end: a multi-byte UTF-8 character in a
        // player name can be split across two packets.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while (true) {
            Packet p = readPacket(in);
            if (p.id() == terminatorId) {
                break;
            }
            if (p.id() == commandId && p.type() == SERVERDATA_RESPONSE_VALUE) {
                output.writeBytes(p.body());
            }
            // Anything else is a leftover from an earlier exchange; ignore it.
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    static void writePacket(OutputStream out, Packet packet) throws IOException {
        int size = 4 + 4 + packet.body().length + 2;
        ByteBuffer buf = ByteBuffer.allocate(4 + size).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(size);
        buf.putInt(packet.id());
        buf.putInt(packet.type());
        buf.put(packet.body());
        buf.put((byte) 0);
        buf.put((byte) 0);
        out.write(buf.array());
        out.flush();
    }

    static Packet readPacket(InputStream in) throws IOException {
        DataInputStream din = in instanceof DataInputStream d ? d : new DataInputStream(in);

        byte[] sizeBytes = new byte[4];
        din.readFully(sizeBytes);
        int size = ByteBuffer.wrap(sizeBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
        if (size < MIN_PACKET_SIZE || size > MAX_PACKET_SIZE) {
            throw new IOException("Invalid RCON packet size: " + size);
        }

        byte[] data = new byte[size];
        din.readFully(data);
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int id = buf.getInt();
        int type = buf.getInt();
        byte[] body = Arrays.copyOfRange(data, 8, size - 2);
        return new Packet(id, type, body);
    }
}
