package com.fooberticus.tf2playercheck.utils;

public enum ApiEndpoint {
    STEAM_BANS("https://api.steampowered.com/ISteamUser/GetPlayerBans/v1/"),
    STEAM_SUMMARIES("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/"),
    STEAM_HISTORY("https://steamhistory.net/api/sourcebans"),
    RENT_A_MEDIC("https://rentamedic.org/api/cheaters/lookup"),
    BACKPACK_TF("https://backpack.tf/api/IGetUsers/v3");

    public final String url;

    ApiEndpoint(String url) {
        this.url = url;
    }
}
