package com.fooberticus.tf2playercheck.gui.panels;

import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResult;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerBan;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerSummary;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;
import com.fooberticus.tf2playercheck.utils.GuiUtil;
import com.fooberticus.tf2playercheck.utils.SteamUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

@Slf4j
public class BaseResultsPanel extends JPanel {

    public static final String STEAM_COMMUNITY_URI = "https://steamcommunity.com/profiles/";
    public static final String STEAM_HISTORY_URI = "https://steamhistory.net/id/";
    public static final String RENT_A_MEDIC_URI = "https://rentamedic.org/cheaters/";
    public static final String BACKPACKTF_PROFILE_URI = "https://backpack.tf/profiles/";

    JScrollPane scrollPane;

    public BaseResultsPanel() {
        initComponents();
    }

    private void initComponents() {
        scrollPane = new JScrollPane();

        setVisible(true);

        GroupLayout contentPaneLayout = new GroupLayout(this);
        setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 861, Short.MAX_VALUE))));

        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addContainerGap())
        );
    }

    /** Right click context menu for all results screens. */
    static class PopUpMenu extends JPopupMenu implements ActionListener {

        Map<Long, SteamPlayerSummary> playerSummaryMap;
        Map<Long, SteamPlayerBan> steamPlayerBanMap;
        Map<Long, List<SourceBan>> steamHistoryMap;
        Map<Long, RentAMedicResult> rentAMedicResultMap;
        Long playerId;

        public PopUpMenu(Map<Long, SteamPlayerSummary> playerSummaryMap,
                         Map<Long, SteamPlayerBan> steamPlayerBanMap,
                         Map<Long, List<SourceBan>> steamHistoryMap,
                         Map<Long, RentAMedicResult> rentAMedicResultMap,
                         Long steam64Id) {
            this.playerSummaryMap = playerSummaryMap;
            this.steamPlayerBanMap = steamPlayerBanMap;
            this.steamHistoryMap = steamHistoryMap;
            this.rentAMedicResultMap = rentAMedicResultMap;
            this.playerId = steam64Id;
            initMenus();
        }

        private void initMenus() {
            JLabel menuHeader = new JLabel( "Actions for " + playerSummaryMap.get(playerId).getPersonaname() );
            add(menuHeader);

            JMenuItem steamProfileMenuItem = new JMenuItem("Steam Profile");
            steamProfileMenuItem.setActionCommand("steamProfile");
            steamProfileMenuItem.addActionListener(this);
            add(steamProfileMenuItem);
            
            JMenuItem steamHistoryMenuItem = new JMenuItem("Community History");
            steamHistoryMenuItem.setActionCommand("communityHistory");
            steamHistoryMenuItem.addActionListener(this);
            add(steamHistoryMenuItem);

            JMenuItem rentAMedicMenuItem = new JMenuItem("Rent a Medic");
            rentAMedicMenuItem.setActionCommand("rentAMedic");
            rentAMedicMenuItem.addActionListener(this);
            add(rentAMedicMenuItem);

            JMenuItem backpackMenuItem = new JMenuItem("Backpack.tf Profile");
            backpackMenuItem.setActionCommand("backpacktfProfile");
            backpackMenuItem.addActionListener(this);
            add(backpackMenuItem);

            JMenu copyMenu = new JMenu( "Copy" );
            add(copyMenu);

            JMenuItem copyPlayerMenuItem = new JMenuItem( "Player Name" );
            copyPlayerMenuItem.setActionCommand("copyPlayer");
            copyPlayerMenuItem.addActionListener( this );
            copyMenu.add(copyPlayerMenuItem);

            JMenuItem copySteam64IDMenuItem = new JMenuItem( "Steam64 ID" );
            copySteam64IDMenuItem.setActionCommand("copySteam64ID");
            copySteam64IDMenuItem.addActionListener( this );
            copyMenu.add(copySteam64IDMenuItem);

            JMenuItem copySteam32IDMenuItem = new JMenuItem( "Steam32 ID" );
            copySteam32IDMenuItem.setActionCommand("copySteam32ID");
            copySteam32IDMenuItem.addActionListener( this );
            copyMenu.add(copySteam32IDMenuItem);

            JMenuItem copySteamProfileURL = new JMenuItem( "Steam Profile URL" );
            copySteamProfileURL.setActionCommand("copySteamProfileURL");
            copySteamProfileURL.addActionListener( this );
            copyMenu.add(copySteamProfileURL);

            JMenuItem copySteamHistoryURL = new JMenuItem( "Steam History URL" );
            copySteamHistoryURL.setActionCommand("copySteamHistoryURL");
            copySteamHistoryURL.addActionListener( this );
            copyMenu.add(copySteamHistoryURL);

            JMenuItem copyRentAMedicURL = new JMenuItem( "Rent a Medic URL" );
            copyRentAMedicURL.setActionCommand("copyRentAMedicURL");
            copyRentAMedicURL.addActionListener( this );
            copyMenu.add(copyRentAMedicURL);

            JMenuItem copyBackpackTfURL = new JMenuItem( "Backpack.tf Profile URL" );
            copyBackpackTfURL.setActionCommand("copyBackpackTfURL");
            copyBackpackTfURL.addActionListener( this );
            copyMenu.add(copyBackpackTfURL);
        }

        private void openSteamProfile() {
            log.info("opening steam profile for {}", playerId);
            GuiUtil.openURLInBrowser(STEAM_COMMUNITY_URI + playerId);
        }

        private void openSteamHistory() {
            log.info("opening steam history for {}", playerId);
            GuiUtil.openURLInBrowser(STEAM_HISTORY_URI + playerId);
        }

        private void openRentAMedic() {
            log.info("opening rent a medic for {}", playerId);
            GuiUtil.openURLInBrowser(RENT_A_MEDIC_URI + playerId);
        }

        private void openBackpackTFProfile() {
            log.info("opening backpack.tf profile for {}", playerId);
            GuiUtil.openURLInBrowser(BACKPACKTF_PROFILE_URI + playerId);
        }

        private void copyUserName() {
            log.info("copying username for {}", playerId);
            GuiUtil.copyToClipboard( playerSummaryMap.get( playerId ).getPersonaname() );
        }

        private void copySteam64ID() {
            log.info("copying steam64ID for {}", playerId);
            GuiUtil.copyToClipboard( playerId.toString() );
        }

        private void copySteam32ID() {
            log.info("copying steam32ID for {}", playerId);
            GuiUtil.copyToClipboard( SteamUtils.getSteamID32FromSteamID64( playerId ) );
        }

        private void copySteamProfileURL() {
            log.info("copying steam profile url for {}", playerId);
            GuiUtil.copyToClipboard( STEAM_COMMUNITY_URI + playerId.toString() );
        }

        private void copySteamHistoryProfileURL() {
            log.info("copying steam history profile url for {}", playerId);
            GuiUtil.copyToClipboard( STEAM_HISTORY_URI + playerId.toString() );
        }

        private void copyRentAMedicURL() {
            log.info("copying rent a medic url for {}", playerId);
            GuiUtil.copyToClipboard( RENT_A_MEDIC_URI + playerId.toString() );
        }

        private void copyBackpackTFProfileURL() {
            log.info("copying backpack.tf profile url for {}", playerId);
            GuiUtil.copyToClipboard( BACKPACKTF_PROFILE_URI + playerId.toString() );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "steamProfile": openSteamProfile(); break;
                case "communityHistory": openSteamHistory(); break;
                case "rentAMedic": openRentAMedic(); break;
                case "backpacktfProfile": openBackpackTFProfile(); break;
                case "copyPlayer": copyUserName(); break;
                case "copySteam64ID": copySteam64ID(); break;
                case "copySteam32ID": copySteam32ID(); break;
                case "copySteamProfileURL": copySteamProfileURL(); break;
                case "copySteamHistoryURL": copySteamHistoryProfileURL(); break;
                case "copyRentAMedicURL": copyRentAMedicURL(); break;
                case "copyBackpackTfURL": copyBackpackTFProfileURL(); break;
                default: break;
            }
        }
    }

    /** MouseAdapter to add to JTables, to handle right-click context menu. */
    static class PopUpMenuClickListener extends MouseAdapter {
        Map<Long, SteamPlayerSummary> playerSummaryMap;
        Map<Long, SteamPlayerBan> steamPlayerBanMap;
        Map<Long, List<SourceBan>> steamHistoryMap;
        Map<Long, RentAMedicResult> rentAMedicResultMap;

        public PopUpMenuClickListener(Map<Long, SteamPlayerSummary> playerSummaryMap,
                                      Map<Long, SteamPlayerBan> steamPlayerBanMap,
                                      Map<Long, List<SourceBan>> steamHistoryMap,
                                      Map<Long, RentAMedicResult> rentAMedicResultMap) {
            this.playerSummaryMap = playerSummaryMap;
            this.steamPlayerBanMap = steamPlayerBanMap;
            this.steamHistoryMap = steamHistoryMap;
            this.rentAMedicResultMap = rentAMedicResultMap;
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                doPop(e);
            }
        }

        private void doPop(MouseEvent e) {
            JTable jTable = (JTable) e.getSource();
            Point point = e.getPoint();
            int row = jTable.rowAtPoint(point);
            int col = jTable.columnAtPoint(point);
            jTable.changeSelection( row, col, !( jTable.isRowSelected( row ) ), false );

            // this relies on the 2nd column to always be the Steam64 ID
            Long id = Long.valueOf( (String) jTable.getValueAt(row, 1) );

            PopUpMenu menu = new PopUpMenu(playerSummaryMap, steamPlayerBanMap, steamHistoryMap, rentAMedicResultMap, id);
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

}
