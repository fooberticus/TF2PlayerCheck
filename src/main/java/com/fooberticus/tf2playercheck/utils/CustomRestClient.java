package com.fooberticus.tf2playercheck.utils;

import com.fooberticus.tf2playercheck.models.backpack.BackpackUsersResponse;
import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResponse;
import com.fooberticus.tf2playercheck.models.server.ServerPlayer;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerBansResponse;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerSummaryResponse;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBanResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.juneau.rest.client.RestCallException;
import org.apache.juneau.rest.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

@Slf4j
public final class CustomRestClient {

    private static final Gson gson = new Gson();

    public SourceBanResponse getSourceBans(List<ServerPlayer> serverPlayers) {
        String url = ApiEndpoint.STEAM_HISTORY.url + "?key=" + GuiUtil.getSavedSteamHistoryApiKey() + "&steamids=" + buildSteamIdString(serverPlayers);
        return fetchWithErrorHandling(url, SourceBanResponse.class,
                "Error making SteamHistory.net api call, try again. If you continue to see this error, check your API key settings!");
    }

    public SteamPlayerBansResponse getSteamPlayerBans(List<ServerPlayer> serverPlayers) {
        String url = ApiEndpoint.STEAM_BANS.url + "?key=" + GuiUtil.getSavedSteamApiKey() + "&steamids=" + buildSteamIdString(serverPlayers);
        return fetchWithErrorHandling(url, SteamPlayerBansResponse.class,
                "Error making Steam api call, try again. If you continue to see this error, check your API key settings!");
    }

    public SteamPlayerSummaryResponse getSteamPlayerSummaries(List<ServerPlayer> serverPlayers) {
        String url = ApiEndpoint.STEAM_SUMMARIES.url + "?key=" + GuiUtil.getSavedSteamApiKey() + "&steamids=" + buildSteamIdString(serverPlayers);
        return fetchWithErrorHandling(url, SteamPlayerSummaryResponse.class,
                "Error making Steam api call, try again. If you continue to see this error, check your API key settings!");
    }

    public RentAMedicResponse getRentAMedicCheaters(List<ServerPlayer> serverPlayers) {
        String url = ApiEndpoint.RENT_A_MEDIC.url + "?steamids=" + buildSteamIdString(serverPlayers);
        return fetchWithErrorHandling(url, RentAMedicResponse.class,
                "Error making Rent-a-Medic api call, try again. If you continue to see this error, check your API key settings!");
    }

    public BackpackUsersResponse getBackpackUsers(List<ServerPlayer> serverPlayers) {
        try {
            String encodedIds = URLEncoder.encode(buildSteamIdString(serverPlayers), StandardCharsets.UTF_8);
            String url = ApiEndpoint.BACKPACK_TF.url + "?key=" + GuiUtil.getSavedBackpackTfApiKey() + "&steamids=" + encodedIds;
            return getRequest(url, BackpackUsersResponse.class);
        } catch (Exception e) {
            GuiUtil.showSystemTrayError("Error making Backpack.tf api call, try again. If you continue to see this error, check your API key settings!");
            log.error("Unable to make backpacktf api call: {}", e.getMessage());
            return buildEmptyBackpackResponse(serverPlayers);
        }
    }

    private String buildSteamIdString(List<ServerPlayer> serverPlayers) {
        return StringUtils.join(SteamUtils.getPlayerIdsFromServerPlayerList(serverPlayers), ",");
    }

    private <T> T fetchWithErrorHandling(String url, Class<T> responseType, String errorMsg) {
        try {
            return getRequest(url, responseType);
        } catch (RestCallException e) {
            GuiUtil.showSystemTrayError(errorMsg);
            log.error("API call failed [{}]: {}", url, e.getMessage());
            return null;
        }
    }

    private BackpackUsersResponse buildEmptyBackpackResponse(List<ServerPlayer> serverPlayers) {
        BackpackUsersResponse response = new BackpackUsersResponse();
        BackpackUsersResponse.ResponseData responseData = new BackpackUsersResponse.ResponseData();
        responseData.setPlayers(new HashMap<>());
        BackpackUsersResponse.BackpackPlayer emptyPlayer = new BackpackUsersResponse.BackpackPlayer();
        response.setResponse(responseData);
        serverPlayers.forEach(p -> responseData.getPlayers().put(p.getSteam64Id().toString(), emptyPlayer));
        return response;
    }

    protected <T> T getRequest(String url, Class<T> responseType) throws RestCallException {
        log.info("GET {}", url);
        T response = null;
        try {
            RestClient client = RestClient.create().build();
            String rawResponse = client
                    .get(url)
                    .run()
                    .assertStatus().asCode().is(200)
                    .getContent().asString();
            log.info("RAW RESPONSE: {}", rawResponse);
            response = gson.fromJson(rawResponse, responseType);
            client.close();
        } catch (RestCallException e) {
            log.error("#############################################");
            log.error("REST CALL EXCEPTION: {}", e.getMessage(), e);
            log.error("#############################################");
            throw e;
        } catch (Exception e) {
            log.error("#############################################");
            log.error("EXCEPTION CAUGHT: {}", e.getMessage(), e);
            log.error("#############################################");
        }
        return response;
    }
}
