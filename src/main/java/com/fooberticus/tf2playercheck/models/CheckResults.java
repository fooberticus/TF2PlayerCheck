package com.fooberticus.tf2playercheck.models;

import com.fooberticus.tf2playercheck.models.backpack.BackpackUsersResponse;
import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResult;
import com.fooberticus.tf2playercheck.models.server.ServerPlayer;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerBan;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerSummary;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;

import java.util.List;
import java.util.Map;

public record CheckResults(
        Map<Long, List<SourceBan>> sourceBanMap,
        Map<Long, SteamPlayerBan> steamPlayerBanMap,
        Map<Long, SteamPlayerSummary> steamPlayerSummaryMap,
        Map<Long, RentAMedicResult> rentAMedicResultMap,
        Map<Long, BackpackUsersResponse.BackpackPlayer> backpackPlayerMap,
        List<ServerPlayer> serverPlayers
) {}
