package com.fooberticus.tf2playercheck.utils;

import com.fooberticus.tf2playercheck.models.server.ServerPlayer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class SteamUtilsTest {

    public final static String STATUS_CHONK = "ng soldier for 1 year :  yeah thats why\n" +
            "gondon freeman killed malmes3 with sword.\n" +
            "ᓚ₍ ^. .^₎ killed Маг_водки_из_СССР with tf_projectile_rocket.\n" +
            "maskata_wst killed gondon freeman with scattergun.\n" +
            "maining soldier for 1 year :  your voice may sound ass\n" +
            "lolglorns :  RUN LITTLE SHEEP\n" +
            "] status\n" +
            "hostname: Valve Matchmaking Server (Frankfurt srcds301-fra2 #231)\n" +
            "map     : plr_hightower at: 0 x, 0 y, 0 z\n" +
            "tags    : hidden,increased_maxplayers,payload,valve\n" +
            "account : not logged in  (No account specified)\n" +
            "players : 21 humans, 0 bots (32 max)\n" +
            "edicts  : 944 used of 2048 max\n" +
            "#    785 \"7_7\"               [U:1:1723584623]    39:18       87    0 active\n" +
            "# userid name                uniqueid            connected ping loss state\n" +
            "#    806 \"Meh\"               [U:1:101574366]     14:36       69    0 active\n" +
            "#    809 \"malmes3\"           [U:1:1242792523]    12:50       59    0 active\n" +
            "#    778 \"Sex is not real\"   [U:1:1201331187]    42:21       76    0 active\n" +
            "#    810 \"nuke\"              [U:1:1465980021]    12:42      112    0 active\n" +
            "#    802 \"maskata_wst\"       [U:1:1738458371]    17:59       52    0 active\n" +
            "#    793 \"lolglorns\"         [U:1:844054915]     34:07       59    0 active\n" +
            "#    753 \"ASD\"               [U:1:99965124]      54:35       97    0 active\n" +
            "#    815 \"maining soldier for 1 year\" [U:1:115879126] 05:25   67    0 active\n" +
            "#    762 \"QUYTD\"             [U:1:1786705094]    49:54       84    0 active\n" +
            "#    779 \"ᓚ₍ ^. .^₎\"   [U:1:1474407367]    41:54       65    0 active\n" +
            "#    812 \"ProGamer1337\"      [U:1:1525843434]    06:50       58    0 active\n" +
            "#    760 \"Макдрын\"    [U:1:1475705810]    50:52      122    0 active\n" +
            "#    758 \"Арчер\"        [U:1:1238880162]    50:54      116    0 active\n" +
            "#    781 \"Маг_водки_из_СССР\" [U:1:1556121672] 41:49  117    0 active\n" +
            "#    816 \"сынку\"        [U:1:888443912]     02:08       60    0 active\n" +
            "#    799 \"Stark777\"          [U:1:1480271345]    29:20       99    0 active\n" +
            "#    797 \"nekke45\"           [U:1:850933706]     30:38      100    0 active\n" +
            "#    813 \"BurgerSamurai\"     [U:1:1754383431]    06:31      105    0 active\n" +
            "#    789 \"gondon freeman\"    [U:1:1524905003]    39:03       80    0 active\n" +
            "lolglorns :  GET A HEAD START RIGHT MEOW\n";

    @Test
    public void getUserIdsFromText_returns_correct_list() {
        List<Long> userIds = SteamUtils.getUserIdsFromText(STATUS_CHONK);

        assertEquals(20, userIds.size());
        assertTrue(userIds.contains(SteamUtils.STEAM_64_BASE + 1525843434));
    }

    @Test
    public void getServerPlayersFromStatusText_returns_correct_list() {
        List<ServerPlayer> serverPlayers = SteamUtils.getServerPlayersFromStatusText(STATUS_CHONK);
        assertEquals(20, serverPlayers.size());
        for (ServerPlayer serverPlayer : serverPlayers) {
            assertNotNull(serverPlayer.getTimeOnServer());
            assertNotNull(serverPlayer.getSteam64Id());
            log.info("steam64id: {}, timeOnServer: {}", serverPlayer.getSteam64Id(), serverPlayer.getTimeOnServer());
        }
    }

}
