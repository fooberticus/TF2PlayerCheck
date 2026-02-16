package com.fooberticus.tf2playercheck.models.steam;

import lombok.Data;

@Data
public class SteamPlayerBan {
    private String SteamId;
    private Boolean CommunityBanned;
    private Boolean VACBanned;
    private Integer NumberOfVACBans;
    private Integer DaysSinceLastBan;
    private Integer NumberOfGameBans;
    private String EconomyBan;
}
