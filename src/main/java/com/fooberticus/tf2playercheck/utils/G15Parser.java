package com.fooberticus.tf2playercheck.utils;

import com.fooberticus.tf2playercheck.models.server.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the output of TF2's {@code g15_dumpplayer} console command.
 * <p>
 * TF2 must be launched with {@code -g15} for the command to work. The dump has several sections
 * such as {@code (localplayer)} and {@code (playerresource)}. Only {@code (playerresource)} holds
 * per-player data, as numbered fields:
 * <pre>
 * m_iAccountID[1] integer (1170385381)
 * m_iPing[1] integer (32)
 * m_bConnected[1] bool (true)
 * </pre>
 */
public final class G15Parser {

    /** TF2 prints this instead of the dump when it was started without {@code -g15}. */
    public static final String G15_DISABLED_MESSAGE = "Must run with -g15";

    private static final String PLAYER_RESOURCE_SECTION = "(playerresource)";
    /** e.g. {@code m_iPing[1] integer (32)}. */
    private static final Pattern FIELD_PATTERN = Pattern.compile("^(m_\\w+)\\[(\\d+)]\\s+\\w+\\s+\\((.*)\\)$");

    private G15Parser() {}

    public static boolean isG15Disabled(String dump) {
        return dump != null && dump.contains(G15_DISABLED_MESSAGE);
    }

    /**
     * Returns the connected human players in the dump, in slot order, with Steam ID, ping and team set.
     * The dump has no connected time, so it is left empty. Empty slots and bots (account id 0)
     * are skipped. Returns an empty list when not on a server.
     */
    public static List<ServerPlayer> parse(String dump) {
        if (dump == null || dump.isEmpty()) {
            return List.of();
        }

        // If the section header is missing (format change), fall back to reading the whole dump.
        boolean hasSection = dump.contains(PLAYER_RESOURCE_SECTION);
        boolean inSection = !hasSection;

        Map<Integer, Map<String, String>> slots = new TreeMap<>();
        for (String rawLine : dump.split("\\R")) {
            String line = rawLine.strip();
            if (hasSection && line.startsWith("(") && line.endsWith(")")) {
                inSection = line.equals(PLAYER_RESOURCE_SECTION);
                continue;
            }
            if (!inSection) {
                continue;
            }
            Matcher m = FIELD_PATTERN.matcher(line);
            if (m.matches()) {
                slots.computeIfAbsent(Integer.parseInt(m.group(2)), k -> new HashMap<>())
                        .putIfAbsent(m.group(1), m.group(3));
            }
        }

        List<ServerPlayer> players = new ArrayList<>();
        for (Map<String, String> fields : slots.values()) {
            long accountId = parseLong(fields.get("m_iAccountID"));
            if (!"true".equals(fields.get("m_bConnected")) || accountId <= 0) {
                continue;
            }
            ServerPlayer player = new ServerPlayer();
            player.setSteam64Id(SteamUtils.STEAM_64_BASE + accountId);
            player.setPing((int) parseLong(fields.get("m_iPing")));
            player.setTeam(teamName(fields.get("m_iTeam")));
            players.add(player);
        }
        return players;
    }

    /** Maps TF2 team numbers to names. Unassigned or missing gives null. */
    private static String teamName(String team) {
        if (team == null) {
            return null;
        }
        return switch (team) {
            case "1" -> "SPEC";
            case "2" -> "RED";
            case "3" -> "BLU";
            default -> null;
        };
    }

    private static long parseLong(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
