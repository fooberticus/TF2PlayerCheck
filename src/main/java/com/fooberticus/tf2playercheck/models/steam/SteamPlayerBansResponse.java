package com.fooberticus.tf2playercheck.models.steam;

import lombok.Data;

import java.util.List;

@Data
public class SteamPlayerBansResponse {
    List<SteamPlayerBan> players;
}
