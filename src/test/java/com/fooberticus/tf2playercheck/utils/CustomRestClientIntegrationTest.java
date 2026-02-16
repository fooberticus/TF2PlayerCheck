package com.fooberticus.tf2playercheck.utils;

import com.fooberticus.tf2playercheck.models.server.ServerPlayer;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBanResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class CustomRestClientIntegrationTest {
    private CustomRestClient client = new CustomRestClient();

    static {
        if (GuiUtil.getSavedSteamHistoryApiKey() == null || GuiUtil.getSavedSteamHistoryApiKey().isEmpty()) {
            System.out.println("YOU NEED TO CONFIGURE A STEAM HISTORY KEY");
            System.out.println("RUN THE APPLICATION TO CONFIGURE THIS VALUE");
        }
    }

    @Test
    public void testMakingRequestsToSteamHistory() throws Exception {
        List<ServerPlayer> serverPlayers = SteamUtils.getServerPlayersFromStatusText( SteamUtilsTest.STATUS_CHONK );

        assertEquals(20, serverPlayers.size());

        SourceBanResponse response = client.getSourceBans( serverPlayers );

        assertNotNull(response);

        HashSet<SourceBan> sourceBans = response.getResponse();

        assertNotNull(sourceBans);
        assertFalse(sourceBans.isEmpty());

        sourceBans.forEach(System.out::println);
    }
}
