package com.fooberticus.tf2playercheck.gui.frames;

import com.fooberticus.tf2playercheck.utils.GuiUtil;
import com.fooberticus.tf2playercheck.utils.RconClient;

import javax.swing.*;
import javax.swing.GroupLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Fooberticus
 */
public class RCONConfigWindow extends JFrame {

    private static final String OUTLINE = "JComponent.outline";

    public RCONConfigWindow() {
        initComponents();
        rconPortTextField.setText(String.valueOf(GuiUtil.getSavedRconPort()));
        rconPasswordTextField.setText(GuiUtil.getSavedRconPassword());
        checkFields();
    }

    public static void startRCONConfigWindow() {
        RCONConfigWindow window = new RCONConfigWindow();
        GuiUtil.initWindow(window, "RCONConfiguration");
        window.button1.requestFocusInWindow();
    }

    private void closeButtonClicked() {
        if (hasUnsavedChanges()) {
            String[] options = {"Save", "Don't Save", "Cancel"};

            int selection = JOptionPane.showOptionDialog(
                    this,
                    "You have unsaved changes. Do you want to save before closing?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[2]
            );

            if (selection == 0) {
                if (saveSettings()) {
                    dispose();
                }
            } else if (selection == 1) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    private void saveButtonClicked() {
        saveSettings();
    }

    private void copyLaunchParamsButtonClicked() {
        List<String> errors = checkFields();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid RCON port and password before copying the launch parameters.\n\n"
                            + String.join("\n", errors),
                    "RCON Settings Missing",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int port = parsePort();
        String password = rconPasswordTextField.getText();

        // Save first so the app always connects with the same settings TF2 is launched with.
        boolean savedNow = hasUnsavedChanges();
        if (savedNow) {
            persist(port, password);
        }

        String launchParams = RconClient.buildLaunchParameters(port, password);
        GuiUtil.copyToClipboard(launchParams);

        String message = "These TF2 launch options were copied to your clipboard:\n\n"
                + launchParams + "\n\n"
                + "To use them:\n"
                + "1. In Steam, right-click Team Fortress 2 and choose Properties.\n"
                + "2. On the General tab, paste them into Launch Options.\n"
                + "    If you already have launch options, add these after them, separated by a space.\n"
                + "3. Restart TF2.\n\n"
                + "If you change the RCON port or password later, copy and paste the launch options again.\n\n"
                + "Use a password you don't use anywhere else. Anyone on your network who knows it\n"
                + "can control your TF2 console.";
        if (savedNow) {
            message = "Your RCON settings were saved.\n\n" + message;
        }

        JOptionPane.showMessageDialog(this, message, "Launch Options Copied", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Saves both fields if they are valid. Returns false and shows the problems if not. */
    private boolean saveSettings() {
        List<String> errors = checkFields();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Settings not saved.\n\n" + String.join("\n", errors),
                    "Invalid RCON Settings",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        persist(parsePort(), rconPasswordTextField.getText());
        JOptionPane.showMessageDialog(this, "Settings saved.");
        return true;
    }

    private void persist(int port, String password) {
        GuiUtil.saveRconPort(port);
        GuiUtil.saveRconPassword(password);
        rconPortTextField.setText(String.valueOf(port));
    }

    /** Outlines invalid fields in red and returns one message per problem (empty if all valid). */
    private List<String> checkFields() {
        List<String> errors = new ArrayList<>();

        String portText = rconPortTextField.getText().trim();
        if (portText.isEmpty()) {
            errors.add("RCON port is required.");
        } else if (parsePort() == null) {
            errors.add("RCON port must be a number from 1 to 65535.");
        }
        rconPortTextField.putClientProperty(OUTLINE, parsePort() == null ? "error" : "");

        String password = rconPasswordTextField.getText();
        boolean passwordValid = false;
        if (password == null || password.isEmpty()) {
            errors.add("RCON password is required.");
        } else if (!RconClient.isValidPassword(password)) {
            // These characters would break the launch options line.
            errors.add("RCON password can't contain spaces, quotes (\") or semicolons (;).");
        } else {
            passwordValid = true;
        }
        rconPasswordTextField.putClientProperty(OUTLINE, passwordValid ? "" : "error");

        return errors;
    }

    /** Returns the port field as a valid TCP port, or null if it isn't one. */
    private Integer parsePort() {
        try {
            int port = Integer.parseInt(rconPortTextField.getText().trim());
            return RconClient.isValidPort(port) ? port : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean hasUnsavedChanges() {
        return !rconPortTextField.getText().trim().equals(String.valueOf(GuiUtil.getSavedRconPort()))
                || !rconPasswordTextField.getText().equals(Objects.toString(GuiUtil.getSavedRconPassword(), ""));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        rconPortTextField = new JTextField();
        label2 = new JLabel();
        rconPasswordTextField = new JTextField();
        button1 = new JButton();
        button2 = new JButton();
        button3 = new JButton();

        //======== this ========
        setTitle("RCON Configuration");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        var contentPane = getContentPane();

        //---- label1 ----
        label1.setText("RCON Port");

        //---- label2 ----
        label2.setText("RCON Password");

        //---- button1 ----
        button1.setText("Close");
        button1.addActionListener(e -> closeButtonClicked());

        //---- button2 ----
        button2.setText("Save");
        button2.addActionListener(e -> saveButtonClicked());

        //---- button3 ----
        button3.setText("Copy Launcher Params");
        button3.addActionListener(e -> copyLaunchParamsButtonClicked());

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(rconPortTextField, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(label1)
                                .addComponent(label2))
                            .addGap(0, 475, Short.MAX_VALUE))
                        .addComponent(rconPasswordTextField, GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addComponent(button3)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                            .addComponent(button2)
                            .addGap(18, 18, 18)
                            .addComponent(button1)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rconPortTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(label2)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rconPasswordTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(button1)
                            .addComponent(button2))
                        .addComponent(button3, GroupLayout.Alignment.TRAILING))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField rconPortTextField;
    private JLabel label2;
    private JTextField rconPasswordTextField;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
