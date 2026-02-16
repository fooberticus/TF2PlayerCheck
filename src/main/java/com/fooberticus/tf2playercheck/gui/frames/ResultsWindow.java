package com.fooberticus.tf2playercheck.gui.frames;

import com.fooberticus.tf2playercheck.gui.panels.AllUsersPanel;
import com.fooberticus.tf2playercheck.gui.panels.CommunityBanPanel;
import com.fooberticus.tf2playercheck.gui.panels.RentAMedicPanel;
import com.fooberticus.tf2playercheck.gui.panels.VACBanPanel;
import com.fooberticus.tf2playercheck.models.backpack.BackpackUsersResponse;
import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResult;
import com.fooberticus.tf2playercheck.models.server.ServerPlayer;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerBan;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerSummary;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;
import com.fooberticus.tf2playercheck.utils.GuiUtil;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * @author Fooberticus
 */
public class ResultsWindow extends JFrame {

    public ResultsWindow(Map<Long, List<SourceBan>> sourceBanMap, Map<Long, SteamPlayerBan> steamPlayerBanMap, Map<Long, RentAMedicResult> rentAMedicResultMap, Map<Long, SteamPlayerSummary> steamPlayerSummaryMap, Map<Long, BackpackUsersResponse.BackpackPlayer> backpackPlayerMap, List<ServerPlayer> serverPlayers) {
        initComponents();

        if (!steamPlayerBanMap.isEmpty()) {
            resultsTabbedPane.add( "Steam Bans", new VACBanPanel( steamPlayerSummaryMap, steamPlayerBanMap, sourceBanMap, rentAMedicResultMap ) );
        }

        if (!sourceBanMap.isEmpty()) {
            resultsTabbedPane.add("Community Bans", new CommunityBanPanel( steamPlayerSummaryMap, steamPlayerBanMap, sourceBanMap, rentAMedicResultMap ) );
        }

        if (!rentAMedicResultMap.isEmpty()) {
            resultsTabbedPane.add("Rent-A-Medic", new RentAMedicPanel( steamPlayerSummaryMap, steamPlayerBanMap, sourceBanMap, rentAMedicResultMap ) );
        }

        resultsTabbedPane.add("All Players", new AllUsersPanel(steamPlayerSummaryMap, steamPlayerBanMap, sourceBanMap, rentAMedicResultMap, backpackPlayerMap, serverPlayers));
    }

    public static void startResultsWindow(Map<Long, List<SourceBan>> sourceBanMap, Map<Long, SteamPlayerBan> steamPlayerBanMap, Map<Long, RentAMedicResult> rentAMedicResultMap, Map<Long, SteamPlayerSummary> steamPlayerSummaryMap, Map<Long, BackpackUsersResponse.BackpackPlayer> backpackPlayerMap, List<ServerPlayer> serverPlayers) {
        GuiUtil.initWindow( new ResultsWindow(sourceBanMap, steamPlayerBanMap, rentAMedicResultMap, steamPlayerSummaryMap, backpackPlayerMap, serverPlayers), "Results" );
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        resultsTabbedPane = new JTabbedPane();

        //======== this ========
        setVisible(true);
        setTitle("Results"); //NON-NLS
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        var contentPane = getContentPane();

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(resultsTabbedPane, GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(resultsTabbedPane, GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JTabbedPane resultsTabbedPane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
