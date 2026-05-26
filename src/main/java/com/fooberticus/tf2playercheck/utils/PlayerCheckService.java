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
        SourceBan latest = bans.get(0);
        LocalDate latestDate = SteamUtils.getLocalDateFromTimestamp(latest.getBanTimestamp());
        for (int i = 1; i < bans.size(); i++) {
            SourceBan ban = bans.get(i);
            LocalDate banDate = SteamUtils.getLocalDateFromTimestamp(ban.getBanTimestamp());
            if (banDate != null && (latestDate == null || banDate.isAfter(latestDate))) {
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
        if (timestamp == null || timestamp.isEmpty()) return "--";
        int tIdx = timestamp.indexOf("T");
        return tIdx >= 0 ? timestamp.substring(0, tIdx) : timestamp;
    }
}
