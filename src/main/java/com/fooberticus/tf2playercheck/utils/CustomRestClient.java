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

    public static final String STEAM_HISTORY_ENDPOINT = "https://steamhistory.net/api/sourcebans";
    public static final String STEAM_API_PLAYER_BANS_ENDPOINT = "https://api.steampowered.com/ISteamUser/GetPlayerBans/v1/";
    public static final String STEAM_API_PLAYER_SUMMARY_ENDPOINT = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/";
    public static final String RENT_A_MEDIC_ENDPOINT = "https://rentamedic.org/api/cheaters/lookup";
    public static final String BACKPACK_USERS_ENDPOINT = "https://backpack.tf/api/IGetUsers/v3";

    private static final Gson gson;

    static {
        gson = new Gson();
    }

    public SourceBanResponse getSourceBans(List<ServerPlayer> serverPlayers) {
        List<Long> steam64Ids = SteamUtils.getPlayerIdsFromServerPlayerList(serverPlayers);
        String ids = StringUtils.join(steam64Ids, ",");
        String url = STEAM_HISTORY_ENDPOINT + "?key=" + GuiUtil.getSavedSteamHistoryApiKey() + "&steamids=" + ids;
        try {
            return getRequest(url, SourceBanResponse.class);
        } catch (RestCallException e) {
            GuiUtil.showSystemTrayError("Error making SteamHistory.net api call, try again. If you continue to see this error, check your API key settings!");
            log.error("Unable to make steam history api call: {}", e.getMessage());
        }
        return null;
    }

    public SteamPlayerBansResponse getSteamPlayerBans(List<ServerPlayer> serverPlayers) {
        List<Long> steam64Ids = SteamUtils.getPlayerIdsFromServerPlayerList(serverPlayers);
        String ids = StringUtils.join(steam64Ids, ",");
        String url = STEAM_API_PLAYER_BANS_ENDPOINT + "?key=" + GuiUtil.getSavedSteamApiKey() + "&steamids=" + ids;
        try {
            return getRequest(url, SteamPlayerBansResponse.class);
        } catch (RestCallException e) {
            GuiUtil.showSystemTrayError("Error making Steam api call, try again. If you continue to see this error, check your API key settings!");
            log.error("Unable to make steam api player bans api call: {}", e.getMessage());
        }
        return null;
    }

    public SteamPlayerSummaryResponse getSteamPlayerSummaries(List<ServerPlayer> serverPlayers) {
        List<Long> steam64Ids = SteamUtils.getPlayerIdsFromServerPlayerList(serverPlayers);
        String ids = StringUtils.join(steam64Ids, ",");
        String url = STEAM_API_PLAYER_SUMMARY_ENDPOINT + "?key=" + GuiUtil.getSavedSteamApiKey() + "&steamids=" + ids;
        try {
            return getRequest(url, SteamPlayerSummaryResponse.class);
        } catch (RestCallException e) {
            GuiUtil.showSystemTrayError("Error making Steam api call, try again. If you continue to see this error, check your API key settings!");
            log.error("Unable to make steam api player summaries api call: {}", e.getMessage());
        }
        return null;
    }

    public RentAMedicResponse getRentAMedicCheaters(List<ServerPlayer> serverPlayers) {
        List<Long> steam64Ids = SteamUtils.getPlayerIdsFromServerPlayerList(serverPlayers);
        String ids = StringUtils.join(steam64Ids, ",");
        String url = RENT_A_MEDIC_ENDPOINT + "?steamids=" + ids;
        try {
            return getRequest(url, RentAMedicResponse.class);
        } catch (RestCallException e) {
            GuiUtil.showSystemTrayError("Error making Rent-a-Medic api call, try again. If you continue to see this error, check your API key settings!");
            log.error("Unable to make rentamedic api call: {}", e.getMessage());
        }
        return null;
    }

    public BackpackUsersResponse getBackpackUsers(List<ServerPlayer> serverPlayers) {
        BackpackUsersResponse response;
        List<Long> steam64Ids = SteamUtils.getPlayerIdsFromServerPlayerList(serverPlayers);
        String ids = StringUtils.join(steam64Ids, ",");
        try {
            String encodedIds = URLEncoder.encode(ids, StandardCharsets.UTF_8);
            String url = BACKPACK_USERS_ENDPOINT + "?key=" + GuiUtil.getSavedBackpackTfApiKey() + "&steamids=" + encodedIds;
            response = getRequest(url, BackpackUsersResponse.class);
        } catch (Exception e) {
            // backpack.tf api throws a lot of 502 errors, if it does, return an empty response
            GuiUtil.showSystemTrayError("Error making Backpack.tf api call, try again. If you continue to see this error, check your API key settings!");
            log.error("Unable to make backpacktf api call: {}", e.getMessage());
            response = new BackpackUsersResponse();
            BackpackUsersResponse.ResponseData responseData = new BackpackUsersResponse.ResponseData();
            responseData.setPlayers(new HashMap<>());
            BackpackUsersResponse.BackpackPlayer emptyPlayer = new BackpackUsersResponse.BackpackPlayer();
            response.setResponse(responseData);
            serverPlayers.forEach(serverPlayer -> {
                responseData.getPlayers().put(serverPlayer.getSteam64Id().toString(), emptyPlayer);
            });
        }
        return response;
    }

    /** Reusable method for making GET requests.
     *
     * @param url String url of the endpoint being called.
     * @param responseType Type representing the desired response type.
     * @return Response object of responseType returned from the rest call.
     */
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
