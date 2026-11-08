package com.fooberticus.tf2playercheck.models.server;

import lombok.Data;

@Data
public class ServerPlayer {
    Long steam64Id;
    String timeOnServer;
    Integer ping;
    /** RED, BLU or SPEC. Only known when read from g15_dumpplayer; status output has no team. */
    String team;

    public String getPing() {
        return ping == null ? "--" : ping.toString();
    }

    public String getTeam() {
        return team == null ? "--" : team;
    }

    public String getTimeOnServer() {
        if (timeOnServer != null) {
            if (timeOnServer.length() == 5) {
                timeOnServer = "00:" + timeOnServer;
            } else if (timeOnServer.length() == 7) {
                timeOnServer = "0" + timeOnServer;
            }
        } else {
            timeOnServer = "--";
        }
        return timeOnServer;
    }
}
