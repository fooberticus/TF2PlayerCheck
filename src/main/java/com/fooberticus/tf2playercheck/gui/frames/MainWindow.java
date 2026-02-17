package com.fooberticus.tf2playercheck.gui.frames;

import com.fooberticus.tf2playercheck.gui.panels.LoadingPanel;
import com.fooberticus.tf2playercheck.models.backpack.BackpackUsersResponse;
import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResponse;
import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResult;
import com.fooberticus.tf2playercheck.models.server.ServerPlayer;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerBan;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerBansResponse;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerSummary;
import com.fooberticus.tf2playercheck.models.steam.SteamPlayerSummaryResponse;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBanResponse;
import com.fooberticus.tf2playercheck.utils.CustomRestClient;
import com.fooberticus.tf2playercheck.utils.GuiUtil;
import com.fooberticus.tf2playercheck.utils.SteamUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Fooberticus
 */
@Slf4j
public class MainWindow extends JFrame {

    private final CustomRestClient client = new CustomRestClient();

    Map<Long, List<SourceBan>> sourceBanMap;
    Map<Long, SteamPlayerBan> steamPlayerBanMap;
    Map<Long, SteamPlayerSummary> steamPlayerSummaryMap;
    Map<Long, RentAMedicResult> rentAMedicResultMap;
    Map<Long, BackpackUsersResponse.BackpackPlayer> backpackPlayerMap;

    public MainWindow() {
        sourceBanMap = new HashMap<>();
        steamPlayerBanMap = new HashMap<>();
        steamPlayerSummaryMap = new HashMap<>();
        rentAMedicResultMap = new HashMap<>();
        backpackPlayerMap = new HashMap<>();

        initComponents();

        statusTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    statusTextArea.requestFocusInWindow();
                    statusTextArea.setText(GuiUtil.getClipboardText());
                }
            }
        });
    }

    public static void startMainWindow() {
        GuiUtil.initWindow(new MainWindow(), "Main");

        if (GuiUtil.getSavedSteamHistoryApiKey() == null || GuiUtil.getSavedSteamHistoryApiKey().isEmpty()
            || GuiUtil.getSavedSteamApiKey() == null || GuiUtil.getSavedSteamApiKey().isEmpty()
            || GuiUtil.getSavedBackpackTfApiKey() == null || GuiUtil.getSavedBackpackTfApiKey().isEmpty()) {
            APIKeysWindow.startAPIKeysWindow();
        }
    }

    private void openAPIKeysConfiguration() {
        APIKeysWindow.startAPIKeysWindow();
    }

    private void openAppearanceConfiguration() {
        AppearanceWindow.startAppearanceWindow();
    }

    private void clear() {
        statusTextArea.setText("");
        statusTextArea.requestFocusInWindow();
    }

    private void loadSteamHistory(List<ServerPlayer> serverPlayers) {
        SwingWorker<Void, Void> mySwingWorker = new SwingWorker<>(){
            @Override
            protected Void doInBackground() throws Exception {
                SourceBanResponse steamHistoryResponse = client.getSourceBans( serverPlayers );
                if (steamHistoryResponse != null
                        && steamHistoryResponse.getResponse() != null
                        && !steamHistoryResponse.getResponse().isEmpty()) {
                    steamHistoryResponse.getResponse().forEach(response -> {
                        Long id = Long.valueOf(response.getSteamID());
                        sourceBanMap.computeIfAbsent(id, k -> new ArrayList<>());
                        sourceBanMap.get(id).add(response);
                    });
                }
                return null;
            }
        };

        showLoadingDialog(mySwingWorker, "checking for community bans...");
    }

    private void loadSteamBans(List<ServerPlayer> serverPlayers) {
        SwingWorker<Void, Void> mySwingWorker = new SwingWorker<>(){
            @Override
            protected Void doInBackground() throws Exception {
                SteamPlayerBansResponse steamPlayerBansResponse = client.getSteamPlayerBans( serverPlayers );
                if (steamPlayerBansResponse != null
                        && steamPlayerBansResponse.getPlayers() != null
                        && !steamPlayerBansResponse.getPlayers().isEmpty()) {
                    steamPlayerBansResponse.getPlayers().forEach(response -> {
                        Long id = Long.valueOf( response.getSteamId() );
                        if (response.getVACBanned() || response.getNumberOfGameBans() > 0) {
                            steamPlayerBanMap.put(id, response);
                        }
                    });
                }
                return null;
            }
        };

        showLoadingDialog(mySwingWorker, "checking for steam bans...");
    }

    private void loadSteamSummaries(List<ServerPlayer> serverPlayers) {
        SwingWorker<Void, Void> mySwingWorker = new SwingWorker<>(){
            @Override
            protected Void doInBackground() throws Exception {
                SteamPlayerSummaryResponse steamPlayerSummaryResponse = client.getSteamPlayerSummaries( serverPlayers );
                if (steamPlayerSummaryResponse != null
                        && steamPlayerSummaryResponse.getResponse() != null
                        && !steamPlayerSummaryResponse.getResponse().players().isEmpty()) {
                    steamPlayerSummaryResponse.getResponse().players().forEach(response ->
                            steamPlayerSummaryMap.put( Long.valueOf( response.getSteamid() ), response ) );
                }
                return null;
            }
        };

        showLoadingDialog(mySwingWorker, "retrieving user info...");
    }

    private void loadRentAMedicCheaters(List<ServerPlayer> serverPlayers) {
        SwingWorker<Void, Void> mySwingWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                RentAMedicResponse response = client.getRentAMedicCheaters(serverPlayers);
                if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
                    response.getResults().forEach(result -> {
                        if (result.isCheater()) {
                            rentAMedicResultMap.put(Long.valueOf(result.getId()), result);
                        }
                    });
                }
                return null;
            }
        };

        showLoadingDialog(mySwingWorker, "checking rent-a-medic for cheaters...");
    }

    private void loadBackpackPlayers(List<ServerPlayer> serverPlayers) {
        SwingWorker<Void, Void> mySwingWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                BackpackUsersResponse response = client.getBackpackUsers(serverPlayers);
                if (response != null && response.getResponse() != null
                        && response.getResponse().getPlayers() != null
                        && !response.getResponse().getPlayers().isEmpty()) {
                    serverPlayers.forEach(serverPlayer -> {
                        BackpackUsersResponse.BackpackPlayer backpackPlayer =
                                response.getResponse().getPlayers().get(serverPlayer.getSteam64Id().toString());
                        backpackPlayerMap.put(serverPlayer.getSteam64Id(), backpackPlayer);
                    });
                }
                return null;
            }
        };

        showLoadingDialog(mySwingWorker, "retrieving backpack values...");
    }

    private void showLoadingDialog(SwingWorker<Void, Void> mySwingWorker, String labelText) {
        final JDialog dialog = new JDialog(this, null, Dialog.ModalityType.APPLICATION_MODAL);

        mySwingWorker.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("state")) {
                if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                    dialog.dispose();
                }
            }
        });
        mySwingWorker.execute();

        dialog.add(new LoadingPanel(labelText));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void checkUsers() {
        if (statusTextArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "While in game in TF2, open the console and type 'status'. " +
                            "Copy the text that prints in the console, then paste it into " +
                            "this text box to analyze users for cheating.");
            return;
        }

        List<ServerPlayer> serverPlayers = SteamUtils.getServerPlayersFromStatusText( statusTextArea.getText() );
        if (serverPlayers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No user IDs found in text.");
            return;
        }

        disableButtons();

        loadSteamSummaries(serverPlayers);
        loadSteamBans(serverPlayers);
        loadRentAMedicCheaters(serverPlayers);
        loadSteamHistory(serverPlayers);
        loadBackpackPlayers(serverPlayers);

        ResultsWindow.startResultsWindow(new HashMap<>(sourceBanMap), new HashMap<>(steamPlayerBanMap), new HashMap<>(rentAMedicResultMap), new HashMap<>(steamPlayerSummaryMap), new HashMap<>(backpackPlayerMap), serverPlayers);

        sourceBanMap.clear();
        steamPlayerBanMap.clear();
        rentAMedicResultMap.clear();
        steamPlayerSummaryMap.clear();
        backpackPlayerMap.clear();

        enableButtons();
    }

    private void enableButtons() {
        checkUsersButton.setEnabled(true);
        clearButton.setEnabled(true);
    }

    private void disableButtons() {
        checkUsersButton.setEnabled(false);
        clearButton.setEnabled(false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menuItem1 = new JMenuItem();
        menuItem2 = new JMenuItem();
        clearButton = new JButton();
        checkUsersButton = new JButton();
        scrollPane1 = new JScrollPane();
        statusTextArea = new JTextArea();

        //======== this ========
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("TF2 Player Check"); //NON-NLS
        var contentPane = getContentPane();

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText("Settings"); //NON-NLS

                //---- menuItem1 ----
                menuItem1.setText("API Keys"); //NON-NLS
                menuItem1.addActionListener(e -> openAPIKeysConfiguration());
                menu1.add(menuItem1);

                //---- menuItem2 ----
                menuItem2.setText("Appearance"); //NON-NLS
                menuItem2.addActionListener(e -> openAppearanceConfiguration());
                menu1.add(menuItem2);
            }
            menuBar1.add(menu1);
        }
        setJMenuBar(menuBar1);

        //---- clearButton ----
        clearButton.setText("Clear"); //NON-NLS
        clearButton.addActionListener(e -> clear());

        //---- checkUsersButton ----
        checkUsersButton.setText("Check Users"); //NON-NLS
        checkUsersButton.addActionListener(e -> checkUsers());

        //======== scrollPane1 ========
        {

            //---- statusTextArea ----
            statusTextArea.setFont(new Font("Lucida Console", Font.PLAIN, 14)); //NON-NLS
            scrollPane1.setViewportView(statusTextArea);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addGap(0, 261, Short.MAX_VALUE)
                    .addComponent(checkUsersButton)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(clearButton)
                    .addGap(14, 14, 14))
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(clearButton)
                        .addComponent(checkUsersButton))
                    .addGap(14, 14, 14))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuItem1;
    private JMenuItem menuItem2;
    private JButton clearButton;
    private JButton checkUsersButton;
    private JScrollPane scrollPane1;
    private JTextArea statusTextArea;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
