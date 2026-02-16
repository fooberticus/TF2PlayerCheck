package com.fooberticus.tf2playercheck.models.steam;

import lombok.Data;

@Data
public class SteamPlayerSummary {
    private String steamid;
    private Integer communityvisibilitystate;
    private Integer profilestate;
    private String personaname;
    private Integer commentpermission;
    private String profileurl;
    private String avatar;
    private String avatarmedium;
    private String avatarfull;
    private String avatarhash;
    private Integer personastate;
    private String realname;
    private String primaryclanid;
    private Long timecreated;
    private Integer personastateflags;
    private String loccountrycode;
}
