package com.fooberticus.tf2playercheck.utils;

import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResult;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;

import java.time.LocalDate;
import java.util.List;

import static com.fooberticus.tf2playercheck.utils.BanStates.PERMANENT;
import static com.fooberticus.tf2playercheck.utils.BanStates.TEMP_BAN;

public final class PlayerCheckService {

    private PlayerCheckService() {}

    public static int countActiveCommunityBans(List<SourceBan> bans) {
        int count = 0;
        for (SourceBan ban : bans) {
            if (PERMANENT.equals(ban.getCurrentState()) || TEMP_BAN.equals(ban.getCurrentState())) {
                count++;
            }
        }
        return count;
    }

    public static int countCheatingCommunityBans(List<SourceBan> bans) {
        int count = 0;
        for (SourceBan ban : bans) {
            if (SteamUtils.isBanReasonCheating(ban.getBanReason())) {
                count++;
            }
        }
        return count;
    }

    public static SourceBan findLatestBan(List<SourceBan> bans) {
        if (bans == null || bans.isEmpty()) {
            return null;
        }
        SourceBan latest = null;
        LocalDate latestDate = null;
        for (SourceBan ban : bans) {
            LocalDate banDate = SteamUtils.getLocalDateFromTimestamp(ban.getBanTimestamp());
            if (latestDate == null || (banDate != null && banDate.isAfter(latestDate))) {
                latest = ban;
                latestDate = banDate;
            }
        }
        return latest;
    }

    public static int countActiveRentAMedicBans(RentAMedicResult result) {
        if (result.getCommunityBans() == null || result.getCommunityBans().isEmpty()) {
            return 0;
        }
        return (int) result.getCommunityBans().stream().filter(b -> b.isActive()).count();
    }

    public static String formatCheaterDate(String timestamp) {
        return timestamp == null ? "--" : timestamp.substring(0, timestamp.indexOf("T"));
    }
}
