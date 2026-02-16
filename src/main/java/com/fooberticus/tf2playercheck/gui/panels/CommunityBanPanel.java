package com.fooberticus.tf2playercheck.gui.panels;

import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResult;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerBan;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerSummary;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;
import com.fooberticus.tf2playercheck.utils.GuiUtil;
import com.fooberticus.tf2playercheck.utils.SteamUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fooberticus.tf2playercheck.utils.BanStates.PERMANENT;
import static com.fooberticus.tf2playercheck.utils.BanStates.TEMP_BAN;

@Slf4j
public class CommunityBanPanel extends BaseResultsPanel {

    private static final String[] HEADER_ROW = {"User Name", "Steam64 ID", "Active Bans", "Total Bans", "Cheating Bans", "Last Ban Date", "Last Ban Reason"};

    private final Map<Long, SteamPlayerSummary> steamPlayerSummaryMap;
    private final Map<Long, SteamPlayerBan> steamPlayerBanMap;
    private final Map<Long, List<SourceBan>> sourceBanMap;
    private final Map<Long, RentAMedicResult> rentAMedicResultMap;

    public CommunityBanPanel(Map<Long, SteamPlayerSummary> steamPlayerSummaryMap, Map<Long, SteamPlayerBan> steamPlayerBanMap, Map<Long, List<SourceBan>> sourceBanMap, Map<Long, RentAMedicResult> rentAMedicResultMap) {
        super();
        this.steamPlayerSummaryMap = steamPlayerSummaryMap;
        this.steamPlayerBanMap = steamPlayerBanMap;
        this.sourceBanMap = sourceBanMap;
        this.rentAMedicResultMap = rentAMedicResultMap;
        formatResults();
    }

    private void formatResults() {
        Map<Long, CommunityUser> communityUserMap = new HashMap<>();

        Map<Long, SourceBan> latestBanMap = new HashMap<>();

        sourceBanMap.keySet().forEach(id -> {

            communityUserMap.computeIfAbsent(id, k -> new CommunityUser(
                            steamPlayerSummaryMap.get(id).getPersonaname(),
                            id,
                            0,
                            sourceBanMap.get(id).size(),
                            0,
                            null,
                            null,
                            STEAM_HISTORY_URI + id ) );

            sourceBanMap.get(id).forEach(ban -> {

                switch ( ban.getCurrentState() ) {
                    case PERMANENT:
                    case TEMP_BAN: communityUserMap.get(id).activeBans++; break;
                }

                if ( SteamUtils.isBanReasonCheating( ban.getBanReason() ) ) {
                    communityUserMap.get(id).cheatingBans += 1;
                }

                LocalDate banDate = SteamUtils.getLocalDateFromTimestamp(ban.getBanTimestamp());
                if (!latestBanMap.containsKey(id) || SteamUtils.getLocalDateFromTimestamp( latestBanMap.get(id).getBanTimestamp() ).isBefore(banDate) ) {
                    latestBanMap.put(id, ban);
                }

            });

        });

        String[][] tableContents = new String[communityUserMap.size()][HEADER_ROW.length];

        List<Long> ids = communityUserMap.keySet().stream().toList();

        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            String[] values = { steamPlayerSummaryMap.get(id).getPersonaname(),
                    id.toString(),
                    String.valueOf( communityUserMap.get(id).activeBans ),
                    String.valueOf( communityUserMap.get(id).totalBans ),
                    communityUserMap.get(id).cheatingBans > 0 ? communityUserMap.get(id).cheatingBans.toString() : "--",
                    SteamUtils.getLocalDateFromTimestamp( latestBanMap.get(id).getBanTimestamp() ).toString(),
                    latestBanMap.get(id).getBanReason()};
            System.arraycopy( values, 0, tableContents[i], 0, HEADER_ROW.length );
        }

        JTable table = new JTable(tableContents, HEADER_ROW);
        table.setEnabled(true);
        table.setDefaultEditor(Object.class, null);
        table.setShowGrid(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(4, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                JTable jTable = (JTable) event.getSource();
                Point point = event.getPoint();
                int row = jTable.rowAtPoint(point);
                if (event.getClickCount() == 2 && jTable.getSelectedRow() != -1) {
                    String url = STEAM_HISTORY_URI + table.getValueAt(row, 1);
                    GuiUtil.openURLInBrowser(url);
                }
            }
        });

        table.addMouseListener(new PopUpMenuClickListener(steamPlayerSummaryMap, steamPlayerBanMap, sourceBanMap, rentAMedicResultMap));

        scrollPane.setViewportView(table);
    }

    private static class CommunityUser {
        String userName;
        Long steam64Id;
        Integer activeBans;
        Integer totalBans;
        Integer cheatingBans;
        LocalDate lastBanDate;
        String lastBanReason;
        String steamHistoryUrl;

        CommunityUser(String userName, Long steam64Id, Integer activeBans, Integer totalBans, Integer cheatingBans, LocalDate lastBanDate, String lastBanReason, String steamHistoryUrl) {
            this.userName = userName;
            this.steam64Id = steam64Id;
            this.activeBans = activeBans;
            this.totalBans = totalBans;
            this.cheatingBans = cheatingBans;
            this.lastBanDate = lastBanDate;
            this.lastBanReason = lastBanReason;
            this.steamHistoryUrl = steamHistoryUrl;
        }
    }
}
