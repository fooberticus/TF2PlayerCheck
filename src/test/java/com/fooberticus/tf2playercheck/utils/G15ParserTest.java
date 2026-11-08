package com.fooberticus.tf2playercheck.utils;

import com.fooberticus.tf2playercheck.models.server.ServerPlayer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class G15ParserTest {

    /** Same layout as a real g15_dumpplayer reply, cut down to a few slots. */
    private static final String DUMP = """
            (localplayer)
            m_iHealth integer (125)
            (localteam)
            m_iPing integer (0)
            m_iTeamNum integer (2)
            (playerresource)
            m_iPing[0] integer (0)
            m_szName[0] string (unconnected)
            m_bConnected[0] bool (false)
            m_iTeam[0] integer (0)
            m_iAccountID[0] integer (0)
            m_iUserID[0] integer (0)
            m_flEncodedController[1] float (0.000000)
            m_iAmmo[1] integer (200)
            m_szName[1] string (Hlava)
            m_iPing[1] integer (32)
            m_iScore[1] integer (0)
            m_iDeaths[1] integer (2)
            m_bConnected[1] bool (true)
            m_iTeam[1] integer (2)
            m_bAlive[1] bool (false)
            m_iHealth[1] integer (1)
            m_iAccountID[1] integer (1170385381)
            m_bValid[1] bool (true)
            m_iUserID[1] integer (315)
            m_szName[2] string (Маг (водки))
            m_iPing[2] integer (117)
            m_bConnected[2] bool (true)
            m_iTeam[2] integer (3)
            m_iAccountID[2] integer (1556121672)
            m_iUserID[2] integer (264)
            m_szName[3] string (BOT Heavy)
            m_iPing[3] integer (0)
            m_bConnected[3] bool (true)
            m_iTeam[3] integer (3)
            m_iAccountID[3] integer (0)
            m_iUserID[3] integer (270)
            (localplayerweapon)
            m_iPing[1] integer (999)
            Other replacements:
            """;

    @Test
    void parse_returnsConnectedHumansOnlyWithSteamIdPingAndTeam() {
        List<ServerPlayer> players = G15Parser.parse(DUMP);

        assertEquals(2, players.size());
        assertEquals(SteamUtils.STEAM_64_BASE + 1170385381L, players.get(0).getSteam64Id());
        assertEquals("32", players.get(0).getPing());
        assertEquals("RED", players.get(0).getTeam());
        assertEquals("--", players.get(0).getTimeOnServer());
        assertEquals(SteamUtils.STEAM_64_BASE + 1556121672L, players.get(1).getSteam64Id());
        assertEquals("117", players.get(1).getPing());
        assertEquals("BLU", players.get(1).getTeam());
    }

    @Test
    void parse_handlesWindowsLineEndings() {
        assertEquals(2, G15Parser.parse(DUMP.replace("\n", "\r\n")).size());
    }

    @Test
    void parse_returnsEmptyForEmptyOrMissingInput() {
        assertTrue(G15Parser.parse(null).isEmpty());
        assertTrue(G15Parser.parse("").isEmpty());
        assertTrue(G15Parser.parse("(playerresource)\n").isEmpty());
    }

    @Test
    void isG15Disabled_detectsMissingLaunchOption() {
        assertTrue(G15Parser.isG15Disabled(" Must run with -g15 to enable support for the LCD Keyboard\n"));
        assertFalse(G15Parser.isG15Disabled(DUMP));
        assertFalse(G15Parser.isG15Disabled(null));
    }
}
