package com.fooberticus.tf2playercheck.gui.panels;

import com.fooberticus.tf2playercheck.models.CheckResults;
import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResult;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerBan;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerSummary;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;
import com.fooberticus.tf2playercheck.utils.GuiUtil;
import com.fooberticus.tf2playercheck.utils.IntegerStringComparator;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
public class VACBanPanel extends BaseResultsPanel {

    private final Map<Long, SteamPlayerSummary> steamPlayerSummaryMap;
    private final Map<Long, SteamPlayerBan> steamPlayerBanMap;
    private final Map<Long, List<SourceBan>> sourceBanMap;
    private final Map<Long, RentAMedicResult> rentAMedicResultMap;

    private static final String[] HEADER_ROW = {"User Name", "Steam64 ID", "VAC Banned", "VAC Bans", "Game Bans", "Days Since Last"};
    private static final int COL_VAC_BANS = 3;
    private static final int COL_GAME_BANS = 4;
    private static final int COL_DAYS_SINCE = 5;

    public VACBanPanel(CheckResults results) {
        super();
        this.steamPlayerSummaryMap = results.steamPlayerSummaryMap();
        this.steamPlayerBanMap = results.steamPlayerBanMap();
        this.sourceBanMap = results.sourceBanMap();
        this.rentAMedicResultMap = results.rentAMedicResultMap();
        formatResults();
    }

    private void formatResults() {
        String[][] tableContents = new String[steamPlayerBanMap.size()][HEADER_ROW.length];

        List<Long> ids = steamPlayerBanMap.keySet().stream().toList();

        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            if (!steamPlayerSummaryMap.containsKey(id)) {
                log.info("steam64 id {} is in steamPlayerBanMap, but not in steamPlayerSummaryMap", id);
                continue;
            }
            int vacBans = steamPlayerBanMap.get(id).getNumberOfVACBans();
            int gameBans = steamPlayerBanMap.get(id).getNumberOfGameBans();
            String[] values = { steamPlayerSummaryMap.get(id).getPersonaname(),
                    id.toString(),
                    steamPlayerBanMap.get(id).getVACBanned() ? "Yes" : "--",
                    vacBans == 0 ? "--" : String.valueOf(vacBans),
                    gameBans == 0 ? "--" : String.valueOf(gameBans),
                    steamPlayerBanMap.get(id).getDaysSinceLastBan().toString() };
            System.arraycopy( values, 0, tableContents[i], 0, HEADER_ROW.length );
        }

        JTable table = new JTable(tableContents, HEADER_ROW);
        table.setEnabled(true);
        table.setDefaultEditor(Object.class, null);
        table.setShowGrid(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());

        sorter.setComparator(COL_VAC_BANS, IntegerStringComparator.INSTANCE);
        sorter.setComparator(COL_GAME_BANS, IntegerStringComparator.INSTANCE);
        sorter.setComparator(COL_DAYS_SINCE, IntegerStringComparator.INSTANCE);

        table.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(COL_VAC_BANS, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(COL_DAYS_SINCE, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                JTable jTable = (JTable) event.getSource();
                Point point = event.getPoint();
                int row = jTable.rowAtPoint(point);
                if (event.getClickCount() == 2 && jTable.getSelectedRow() != -1) {
                    String url = STEAM_COMMUNITY_URI + table.getValueAt(row, STEAM64_ID_COL);
                    GuiUtil.openURLInBrowser(url);
                }
            }
        });

        table.addMouseListener(new PopUpMenuClickListener(steamPlayerSummaryMap, steamPlayerBanMap, sourceBanMap, rentAMedicResultMap));

        scrollPane.setViewportView(table);
    }
}
