package com.fooberticus.tf2playercheck.gui.panels;

import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicCommunityBan;
import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResult;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerBan;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerSummary;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;
import com.fooberticus.tf2playercheck.utils.GuiUtil;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RentAMedicPanel extends BaseResultsPanel {

    private static final String[] HEADER_ROW = {"User Name", "Steam64 ID", "Active Cheater", "Cheater Type", "Date Added", "Active Bans", "Total Bans"};

    private final Map<Long, SteamPlayerSummary> steamPlayerSummaryMap;
    private final Map<Long, SteamPlayerBan> steamPlayerBanMap;
    private final Map<Long, List<SourceBan>> sourceBanMap;
    private final Map<Long, RentAMedicResult> rentAMedicResultMap;

    public RentAMedicPanel(Map<Long, SteamPlayerSummary> steamPlayerSummaryMap, Map<Long, SteamPlayerBan> steamPlayerBanMap, Map<Long, List<SourceBan>> sourceBanMap, Map<Long, RentAMedicResult> rentAMedicResultMap) {
        super();
        this.steamPlayerSummaryMap = steamPlayerSummaryMap;
        this.steamPlayerBanMap = steamPlayerBanMap;
        this.sourceBanMap = sourceBanMap;
        this.rentAMedicResultMap = rentAMedicResultMap;
        formatResults();
    }

    private void formatResults() {
        List<Long> ids = rentAMedicResultMap.keySet().stream().toList();

        String[][] tableContents = new String[rentAMedicResultMap.size()][HEADER_ROW.length];

        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            String[] values = {
                    steamPlayerSummaryMap.get(id).getPersonaname(),
                    id.toString(),
                    rentAMedicResultMap.get(id).isCheater() ? "Yes" : "--",
                    rentAMedicResultMap.get(id).getCheaterType(),
                    getDateFromTimestamp( rentAMedicResultMap.get(id).getCheaterDate() ),
                    String.valueOf( getActiveBanCount(id) ),
                    String.valueOf( rentAMedicResultMap.get(id).getCommunityBans().size() ) };
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
        sortKeys.add(new RowSorter.SortKey(5, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(6, SortOrder.DESCENDING));
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                JTable jTable = (JTable) event.getSource();
                Point point = event.getPoint();
                int row = jTable.rowAtPoint(point);
                if (event.getClickCount() == 2 && jTable.getSelectedRow() != -1) {
                    String url = RENT_A_MEDIC_URI + table.getValueAt(row, 1);
                    GuiUtil.openURLInBrowser(url);
                }
            }
        });

        table.addMouseListener(new PopUpMenuClickListener(steamPlayerSummaryMap, steamPlayerBanMap, sourceBanMap, rentAMedicResultMap));

        scrollPane.setViewportView(table);
    }

    private int getActiveBanCount(Long id) {
        int banCount = 0;

        List<RentAMedicCommunityBan> communityBans = rentAMedicResultMap.get(id).getCommunityBans();

        if (communityBans == null || communityBans.isEmpty()) {
            return banCount;
        }

        for (RentAMedicCommunityBan ban : communityBans) {
            if (ban.isActive()) {
                banCount++;
            }
        }

        return banCount;
    }

    private String getDateFromTimestamp(String timestamp) {
        return timestamp == null ? "--" : timestamp.substring( 0, timestamp.indexOf( "T" ) );
    }
}
