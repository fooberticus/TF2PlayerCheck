package com.fooberticus.tf2playercheck.gui.frames;

import com.fooberticus.tf2playercheck.gui.panels.LoadingPanel;
import com.fooberticus.tf2playercheck.models.CheckResults;
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
import com.fooberticus.tf2playercheck.utils.G15Parser;
import com.fooberticus.tf2playercheck.utils.GuiUtil;
import com.fooberticus.tf2playercheck.utils.RconClient;
import com.fooberticus.tf2playercheck.utils.SteamUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Fooberticus
 */
@Slf4j
public class MainWindow extends JFrame {

    private final CustomRestClient client = new CustomRestClient();

    public MainWindow() {
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

    private void openRCONConfiguration() {
        RCONConfigWindow.startRCONConfigWindow();
    }

    private void clear() {
        statusTextArea.setText("");
        statusTextArea.requestFocusInWindow();
    }

    private void loadSteamHistory(List<ServerPlayer> serverPlayers, Map<Long, List<SourceBan>> sourceBanMap) {
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

    private void loadSteamBans(List<ServerPlayer> serverPlayers, Map<Long, SteamPlayerBan> steamPlayerBanMap) {
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

    private void loadSteamSummaries(List<ServerPlayer> serverPlayers, Map<Long, SteamPlayerSummary> steamPlayerSummaryMap) {
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

    private void loadRentAMedicCheaters(List<ServerPlayer> serverPlayers, Map<Long, RentAMedicResult> rentAMedicResultMap) {
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

    private void loadBackpackPlayers(List<ServerPlayer> serverPlayers, Map<Long, BackpackUsersResponse.BackpackPlayer> backpackPlayerMap) {
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

    private void showLoadingDialog(SwingWorker<?, ?> mySwingWorker, String labelText) {
        final JDialog dialog = new JDialog(this, null, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.add(new LoadingPanel(labelText));
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!mySwingWorker.isDone()) {
                    mySwingWorker.cancel(true);
                }
            }
        });

        mySwingWorker.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("state")) {
                if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                    dialog.dispose();
                }
            }
        });

        mySwingWorker.execute();
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

        checkServerPlayers(serverPlayers);
    }

    /** Looks up every player and opens the results window. */
    private void checkServerPlayers(List<ServerPlayer> serverPlayers) {
        if (serverPlayers.size() > 100) {
            int result = JOptionPane.showConfirmDialog(this,
                    serverPlayers.size() + " Steam IDs found. Requests will be processed in batches of 100 until complete.",
                    "Confirm", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }
        }

        disableButtons();

        Map<Long, List<SourceBan>> sourceBanMap = new HashMap<>();
        Map<Long, SteamPlayerBan> steamPlayerBanMap = new HashMap<>();
        Map<Long, SteamPlayerSummary> steamPlayerSummaryMap = new HashMap<>();
        Map<Long, RentAMedicResult> rentAMedicResultMap = new HashMap<>();
        Map<Long, BackpackUsersResponse.BackpackPlayer> backpackPlayerMap = new HashMap<>();

        int chunkSize = 100;
        int size = serverPlayers.size();
        for (int i = 0; i < size; i += chunkSize) {
            List<ServerPlayer> subList = serverPlayers.subList(i, Math.min(i + chunkSize, size));
            loadSteamSummaries(subList, steamPlayerSummaryMap);
            loadSteamBans(subList, steamPlayerBanMap);
            loadRentAMedicCheaters(subList, rentAMedicResultMap);
            loadSteamHistory(subList, sourceBanMap);
            loadBackpackPlayers(subList, backpackPlayerMap);
        }

        ResultsWindow.startResultsWindow(new CheckResults(
                new HashMap<>(sourceBanMap),
                new HashMap<>(steamPlayerBanMap),
                new HashMap<>(steamPlayerSummaryMap),
                new HashMap<>(rentAMedicResultMap),
                new HashMap<>(backpackPlayerMap),
                List.copyOf(serverPlayers)));

        enableButtons();
    }

    private void importFromFile() {
        JFileChooser fileChooser = new JFileChooser( GuiUtil.getLastFilePath() );

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt", "text", "log");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            SwingWorker<Void, Void> mySwingWorker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    File selectedFile = fileChooser.getSelectedFile();
                    GuiUtil.setLastFilePath( selectedFile.getParent() );

                    StringBuilder stringBuilder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader( new FileReader( selectedFile ) )) {
                        String line;
                        while ( (line = reader.readLine()) != null ) {
                            stringBuilder.append(line).append("\n");
                            if ( isCancelled() ) {
                                log.info("import cancelled");
                                break;
                            }
                        }
                    } catch (IOException e) {
                        log.error("Error reading file: {}", e.getMessage());
                        GuiUtil.showSystemTrayError("Error reading file: " + e.getMessage());
                    }

                    if ( !stringBuilder.isEmpty() ) {
                        statusTextArea.setText( stringBuilder.toString() );
                    }

                    return null;
                }
            };

            showLoadingDialog(mySwingWorker, "importing file...");
        }

    }


    private void enableButtons() {
        checkUsersButton.setEnabled(true);
        clearButton.setEnabled(true);
    }

    private void disableButtons() {
        checkUsersButton.setEnabled(false);
        clearButton.setEnabled(false);
    }

    private void getServerStatusClicked() {
        if (!RconClient.hasValidSavedSettings()) {
            JOptionPane.showMessageDialog(this,
                    "Get Server Status reads the player list straight from TF2, so you don't\n"
                            + "have to copy and paste the output of the status command yourself.\n\n"
                            + "It uses RCON, a remote console feature built into TF2. To set it up:\n"
                            + "1. Open Settings > RCON Config.\n"
                            + "2. Enter a port and a password, then click Save.\n"
                            + "3. Click Copy Launcher Params and follow the steps to add them to TF2.\n"
                            + "4. Restart TF2 and join a server, then click Get Server Status again.",
                    "RCON Not Configured",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return RconClient.fetchG15Dump();
            }
        };
        // Modal: returns once the worker is done.
        showLoadingDialog(worker, "getting player list from TF2...");

        String dump;
        try {
            dump = worker.get();
        } catch (ExecutionException e) {
            showRconError(e.getCause() != null ? e.getCause() : e);
            return;
        } catch (InterruptedException | CancellationException e) {
            log.info("get server status cancelled");
            return;
        }

        if (G15Parser.isG15Disabled(dump)) {
            JOptionPane.showMessageDialog(this,
                    "TF2 was started without the -g15 launch option, which Get Server Status\n"
                            + "needs to read the player list.\n\n"
                            + "Add -g15 to TF2's launch options, or copy them again from\n"
                            + "Settings > RCON Config, then restart TF2.",
                    "TF2 Needs -g15",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<ServerPlayer> players = G15Parser.parse(dump);
        if (players.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Connected to TF2, but no players were found.\n\n"
                            + "Join a server, then try again.",
                    "No Players Found",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        checkServerPlayers(players);
    }

    private void showRconError(Throwable cause) {
        log.error("Unable to get server status over RCON: {}", cause.toString());

        String reason = cause instanceof SocketTimeoutException
                ? "TF2 did not respond in time."
                : Objects.requireNonNullElse(cause.getMessage(), cause.getClass().getSimpleName());

        JOptionPane.showMessageDialog(this,
                "Couldn't get the server status from TF2.\n\n"
                        + "Reason: " + reason + "\n\n"
                        + "Check that:\n"
                        + "- TF2 is running and was started with the launch options from Settings > RCON Config.\n"
                        + "- The port and password in Settings > RCON Config match those launch options.",
                "RCON Connection Failed",
                JOptionPane.ERROR_MESSAGE);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        menuBar1 = new JMenuBar();
        menu2 = new JMenu();
        menuItem3 = new JMenuItem();
        menu1 = new JMenu();
        menuItem1 = new JMenuItem();
        menuItem2 = new JMenuItem();
        menuItem4 = new JMenuItem();
        clearButton = new JButton();
        checkUsersButton = new JButton();
        scrollPane1 = new JScrollPane();
        statusTextArea = new JTextArea();
        button1 = new JButton();

        //======== this ========
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("TF2 Player Check");
        var contentPane = getContentPane();

        //======== menuBar1 ========
        {

            //======== menu2 ========
            {
                menu2.setText("File");

                //---- menuItem3 ----
                menuItem3.setText("Import...");
                menuItem3.addActionListener(e -> importFromFile());
                menu2.add(menuItem3);
            }
            menuBar1.add(menu2);

            //======== menu1 ========
            {
                menu1.setText("Settings");

                //---- menuItem1 ----
                menuItem1.setText("API Keys");
                menuItem1.addActionListener(e -> openAPIKeysConfiguration());
                menu1.add(menuItem1);

                //---- menuItem2 ----
                menuItem2.setText("Appearance");
                menuItem2.addActionListener(e -> openAppearanceConfiguration());
                menu1.add(menuItem2);

                //---- menuItem4 ----
                menuItem4.setText("RCON Config");
                menuItem4.addActionListener(e -> openRCONConfiguration());
                menu1.add(menuItem4);
            }
            menuBar1.add(menu1);
        }
        setJMenuBar(menuBar1);

        //---- clearButton ----
        clearButton.setText("Clear");
        clearButton.addActionListener(e -> clear());

        //---- checkUsersButton ----
        checkUsersButton.setText("Check Status Text");
        checkUsersButton.addActionListener(e -> checkUsers());

        //======== scrollPane1 ========
        {

            //---- statusTextArea ----
            statusTextArea.setFont(new Font("Lucida Console", Font.PLAIN, 14));
            scrollPane1.setViewportView(statusTextArea);
        }

        //---- button1 ----
        button1.setText("Check Current Server");
        button1.addActionListener(e -> getServerStatusClicked());

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addComponent(button1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                            .addComponent(checkUsersButton)
                            .addGap(18, 18, 18)
                            .addComponent(clearButton)
                            .addGap(14, 14, 14))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                            .addContainerGap())))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(clearButton)
                        .addComponent(checkUsersButton)
                        .addComponent(button1))
                    .addGap(14, 14, 14))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JMenuBar menuBar1;
    private JMenu menu2;
    private JMenuItem menuItem3;
    private JMenu menu1;
    private JMenuItem menuItem1;
    private JMenuItem menuItem2;
    private JMenuItem menuItem4;
    private JButton clearButton;
    private JButton checkUsersButton;
    private JScrollPane scrollPane1;
    private JTextArea statusTextArea;
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
