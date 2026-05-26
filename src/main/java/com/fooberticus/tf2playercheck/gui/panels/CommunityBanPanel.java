package com.fooberticus.tf2playercheck.gui.panels;

import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResult;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerBan;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerSummary;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;
import com.fooberticus.tf2playercheck.utils.GuiUtil;
import com.fooberticus.tf2playercheck.utils.IntegerStringComparator;
import com.fooberticus.tf2playercheck.utils.PlayerCheckService;
import com.fooberticus.tf2playercheck.utils.SteamUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

@Slf4j
public class CommunityBanPanel extends BaseResultsPanel {

    private static final String[] HEADER_ROW = {"User Name", "Steam64 ID", "Active Bans", "Total Bans", "Cheating Bans", "Last Ban Date", "Last Ban Reason"};
    private static final int COL_ACTIVE_BANS = 2;
    private static final int COL_TOTAL_BANS = 3;
    private static final int COL_CHEATING_BANS = 4;

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

        sourceBanMap.forEach((id, bans) -> {
            SourceBan latestBan = PlayerCheckService.findLatestBan(bans);
            communityUserMap.put(id, new CommunityUser(
                    steamPlayerSummaryMap.get(id).getPersonaname(),
                    id,
                    PlayerCheckService.countActiveCommunityBans(bans),
                    bans.size(),
                    PlayerCheckService.countCheatingCommunityBans(bans),
                    latestBan));
        });

        String[][] tableContents = new String[communityUserMap.size()][HEADER_ROW.length];
        List<Long> ids = communityUserMap.keySet().stream().toList();

        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            CommunityUser user = communityUserMap.get(id);
            int activeBans = user.activeBans;
            int cheatingBans = user.cheatingBans;
            SourceBan latest = user.latestBan;
            LocalDate latestBanDate = latest != null ? SteamUtils.getLocalDateFromTimestamp(latest.getBanTimestamp()) : null;
            String[] values = {
                    steamPlayerSummaryMap.get(id).getPersonaname(),
                    id.toString(),
                    activeBans == 0 ? "--" : String.valueOf(activeBans),
                    String.valueOf(user.totalBans),
                    cheatingBans == 0 ? "--" : String.valueOf(cheatingBans),
                    latestBanDate != null ? latestBanDate.toString() : "--",
                    latest != null ? latest.getBanReason() : "--"
            };
            System.arraycopy(values, 0, tableContents[i], 0, HEADER_ROW.length);
        }

        JTable table = new JTable(tableContents, HEADER_ROW);
        table.setEnabled(true);
        table.setDefaultEditor(Object.class, null);
        table.setShowGrid(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        sorter.setComparator(COL_ACTIVE_BANS, IntegerStringComparator.INSTANCE);
        sorter.setComparator(COL_TOTAL_BANS, IntegerStringComparator.INSTANCE);
        sorter.setComparator(COL_CHEATING_BANS, IntegerStringComparator.INSTANCE);
        table.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(COL_ACTIVE_BANS, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(COL_CHEATING_BANS, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(COL_TOTAL_BANS, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                JTable jTable = (JTable) event.getSource();
                Point point = event.getPoint();
                int row = jTable.rowAtPoint(point);
                if (event.getClickCount() == 2 && jTable.getSelectedRow() != -1) {
                    GuiUtil.openURLInBrowser(STEAM_HISTORY_URI + table.getValueAt(row, 1));
                }
            }
        });
        table.addMouseListener(new PopUpMenuClickListener(steamPlayerSummaryMap, steamPlayerBanMap, sourceBanMap, rentAMedicResultMap));
        scrollPane.setViewportView(table);
    }

    private static class CommunityUser {
        String userName;
        Long steam64Id;
        int activeBans;
        int totalBans;
        int cheatingBans;
        SourceBan latestBan;

        CommunityUser(String userName, Long steam64Id, int activeBans, int totalBans, int cheatingBans, SourceBan latestBan) {
            this.userName = userName;
            this.steam64Id = steam64Id;
            this.activeBans = activeBans;
            this.totalBans = totalBans;
            this.cheatingBans = cheatingBans;
            this.latestBan = latestBan;
        }
    }
}
