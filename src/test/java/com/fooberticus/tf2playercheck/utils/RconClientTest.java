package com.fooberticus.tf2playercheck.utils;

import com.fooberticus.tf2playercheck.models.server.ServerPlayer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.fooberticus.tf2playercheck.utils.RconClient.*;
import static org.junit.jupiter.api.Assertions.*;

public class RconClientTest {

    private static final String PASSWORD = "hunter2";

    private ServerSocket serverSocket;
    private Thread serverThread;
    private final AtomicReference<Throwable> serverError = new AtomicReference<>();

    @AfterEach
    void tearDown() throws Exception {
        if (serverSocket != null) serverSocket.close();
        if (serverThread != null) serverThread.join(2000);
        if (serverError.get() != null) fail("Fake server failed", serverError.get());
    }

    @Test
    void writeAndReadPacket_roundTrip() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        writePacket(bytes, new Packet(42, SERVERDATA_EXECCOMMAND, "status"));

        // size(4) + id(4) + type(4) + "status"(6) + two nulls(2)
        assertEquals(20, bytes.size());

        Packet p = readPacket(new ByteArrayInputStream(bytes.toByteArray()));
        assertEquals(42, p.id());
        assertEquals(SERVERDATA_EXECCOMMAND, p.type());
        assertEquals("status", new String(p.body(), StandardCharsets.UTF_8));
    }

    @Test
    void buildLaunchParameters_includesPortAndPassword() {
        assertEquals("-usercon -g15 +ip 0.0.0.0 +sv_rcon_whitelist_address 127.0.0.1 +hostport 40434"
                        + " +rcon_password s3cret +net_start",
                RconClient.buildLaunchParameters(40434, "s3cret"));
    }

    @Test
    void isValidPort_acceptsOnlyTcpPortRange() {
        assertFalse(RconClient.isValidPort(0));
        assertTrue(RconClient.isValidPort(1));
        assertTrue(RconClient.isValidPort(65535));
        assertFalse(RconClient.isValidPort(65536));
    }

    @Test
    void isValidPassword_rejectsEmptyAndLaunchLineBreakingChars() {
        assertTrue(RconClient.isValidPassword("s3cret!"));
        assertFalse(RconClient.isValidPassword(null));
        assertFalse(RconClient.isValidPassword(""));
        assertFalse(RconClient.isValidPassword("two words"));
        assertFalse(RconClient.isValidPassword("quo\"te"));
        assertFalse(RconClient.isValidPassword("semi;colon"));
    }

    @Test
    void readPacket_rejectsInvalidSize() {
        byte[] badSize = {5, 0, 0, 0};
        assertThrows(IOException.class, () -> readPacket(new ByteArrayInputStream(badSize)));
    }

    @Test
    void runCommand_joinsSplitPacketsAndDecodesSplitMultibyteChars() throws Exception {
        byte[] status = SteamUtilsTest.STATUS_CHONK.getBytes(StandardCharsets.UTF_8);
        // Split inside the first Cyrillic character (2 bytes in UTF-8) to prove bytes are joined before decoding.
        int split = SteamUtilsTest.STATUS_CHONK.substring(0, SteamUtilsTest.STATUS_CHONK.indexOf('М'))
                .getBytes(StandardCharsets.UTF_8).length + 1;
        startFakeTf2(List.of(Arrays.copyOfRange(status, 0, split), Arrays.copyOfRange(status, split, status.length)));

        String result = RconClient.runCommand("127.0.0.1", serverSocket.getLocalPort(), PASSWORD, G15_COMMAND);

        assertEquals(SteamUtilsTest.STATUS_CHONK, result);
        List<ServerPlayer> players = SteamUtils.getServerPlayersFromStatusText(result);
        assertEquals(20, players.size());
    }

    @Test
    void runCommand_wrongPasswordThrows() throws Exception {
        startFakeTf2(List.of());

        IOException e = assertThrows(IOException.class,
                () -> RconClient.runCommand("127.0.0.1", serverSocket.getLocalPort(), "wrong", G15_COMMAND));
        assertTrue(e.getMessage().contains("authentication failed"));
    }

    @Test
    void runCommand_blankPasswordThrows() throws Exception {
        startFakeTf2(List.of());

        IOException e = assertThrows(IOException.class,
                () -> RconClient.runCommand("127.0.0.1", serverSocket.getLocalPort(), "", G15_COMMAND));
        assertTrue(e.getMessage().contains("not set"));
    }

    @Test
    void runCommand_noListenerThrowsConnectException() throws Exception {
        int freePort;
        try (ServerSocket s = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            freePort = s.getLocalPort();
        }
        IOException e = assertThrows(IOException.class,
                () -> RconClient.runCommand("127.0.0.1", freePort, PASSWORD, G15_COMMAND));
        assertTrue(e.getMessage().contains("-usercon"));
    }

    /**
     * Accepts one connection and behaves like the game: auth handshake, then replies to the
     * command with the given chunks, then mirrors the terminator packet the way srcds does.
     */
    private void startFakeTf2(List<byte[]> responseChunks) throws IOException {
        serverSocket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
        serverThread = new Thread(() -> {
            try (Socket client = serverSocket.accept()) {
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();

                Packet auth = readPacket(in);
                assertEquals(SERVERDATA_AUTH, auth.type());
                boolean ok = PASSWORD.equals(new String(auth.body(), StandardCharsets.UTF_8));
                writePacket(out, new Packet(auth.id(), SERVERDATA_RESPONSE_VALUE, ""));
                writePacket(out, new Packet(ok ? auth.id() : -1, SERVERDATA_AUTH_RESPONSE, ""));
                if (!ok) return;

                Packet command = readPacket(in);
                Packet terminator = readPacket(in);
                assertEquals(SERVERDATA_EXECCOMMAND, command.type());
                assertEquals(G15_COMMAND, new String(command.body(), StandardCharsets.UTF_8));
                assertEquals(SERVERDATA_RESPONSE_VALUE, terminator.type());

                for (byte[] chunk : responseChunks) {
                    writePacket(out, new Packet(command.id(), SERVERDATA_RESPONSE_VALUE, chunk));
                }
                writePacket(out, new Packet(terminator.id(), SERVERDATA_RESPONSE_VALUE, ""));
                writePacket(out, new Packet(terminator.id(), SERVERDATA_RESPONSE_VALUE, new byte[]{0, 0, 0, 1}));
            } catch (IOException ignored) {
                // Client hung up first; fine for these tests.
            } catch (Throwable t) {
                serverError.set(t);
            }
        });
        serverThread.start();
    }
}
