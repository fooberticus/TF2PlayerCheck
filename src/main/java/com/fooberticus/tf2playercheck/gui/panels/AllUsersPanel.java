package com.fooberticus.tf2playercheck.gui.panels;

import com.fooberticus.tf2playercheck.models.backpack.BackpackUsersResponse;
import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResult;
import com.fooberticus.tf2playercheck.models.server.ServerPlayer;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerBan;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerSummary;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;
import com.fooberticus.tf2playercheck.utils.CurrencyStringComparator;
import com.fooberticus.tf2playercheck.utils.GuiUtil;
import com.fooberticus.tf2playercheck.utils.IntegerStringComparator;
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
public class AllUsersPanel extends BaseResultsPanel {

    private static final String[] HEADER_ROW = {"User Name", "Steam64 ID", "Time on Server", "Ping", "Profile Visibility", "Profile Created", "Backpack Value"};

    private final Map<Long, SteamPlayerSummary> playerSummaryMap;
    private final Map<Long, SteamPlayerBan> steamPlayerBanMap;
    private final Map<Long, List<SourceBan>> sourceBanMap;
    private final Map<Long, ServerPlayer> serverPlayerMap;
    private final Map<Long, RentAMedicResult> rentAMedicResultMap;
    private final Map<Long, BackpackUsersResponse.BackpackPlayer> backpackPlayerMap;

    public AllUsersPanel (Map<Long, SteamPlayerSummary> steamPlayerSummaryMap, Map<Long, SteamPlayerBan> steamPlayerBanMap, Map<Long, List<SourceBan>> sourceBanMap, Map<Long, RentAMedicResult> rentAMedicResultMap, Map<Long, BackpackUsersResponse.BackpackPlayer> backpackPlayerMap, List<ServerPlayer> serverPlayers) {
        super();
        this.playerSummaryMap = steamPlayerSummaryMap;
        this.steamPlayerBanMap = steamPlayerBanMap;
        this.sourceBanMap = sourceBanMap;
        this.rentAMedicResultMap = rentAMedicResultMap;
        this.backpackPlayerMap = backpackPlayerMap;

        serverPlayerMap = new HashMap<>();
        serverPlayers.forEach(serverPlayer -> serverPlayerMap.put( serverPlayer.getSteam64Id(), serverPlayer ) );

        formatResults();
    }

    private void formatResults() {
        String[][] tableContents = new String[playerSummaryMap.size()][HEADER_ROW.length];

        List<Long> ids = playerSummaryMap.keySet().stream().toList();

        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            Long timeCreated = playerSummaryMap.get(id).getTimecreated();
            LocalDate createdDate = SteamUtils.getLocalDateFromTimestamp(timeCreated);
            String[] values = { playerSummaryMap.get(id).getPersonaname(),
                    id.toString(),
                    serverPlayerMap.get(id).getTimeOnServer(),
                    serverPlayerMap.get(id).getPing(),
                    playerSummaryMap.get(id).getCommunityvisibilitystate() == 3 ? "public" : "PRIVATE",
                    createdDate == null ? "--" : createdDate.toString(),
                    backpackPlayerMap.get(id).getDollarValue()
            };
            System.arraycopy( values, 0, tableContents[i], 0, HEADER_ROW.length );
        }

        JTable table = new JTable(tableContents, HEADER_ROW);
        table.setEnabled(true);
        table.setDefaultEditor(Object.class, null);
        table.setShowGrid(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());

        sorter.setComparator(3, IntegerStringComparator.INSTANCE);
        sorter.setComparator(6, CurrencyStringComparator.INSTANCE);

        table.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                JTable jTable = (JTable) event.getSource();
                Point point = event.getPoint();
                int row = jTable.rowAtPoint(point);
                if (event.getClickCount() == 2 && jTable.getSelectedRow() != -1) {
                    String url = STEAM_COMMUNITY_URI + table.getValueAt(row, 1);
                    GuiUtil.openURLInBrowser(url);
                }
            }
        });

        table.addMouseListener(new PopUpMenuClickListener(playerSummaryMap, steamPlayerBanMap, sourceBanMap, rentAMedicResultMap));

        scrollPane.setViewportView(table);
    }
}
