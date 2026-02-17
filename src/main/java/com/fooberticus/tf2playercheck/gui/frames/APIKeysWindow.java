package com.fooberticus.tf2playercheck.gui.frames;

import com.fooberticus.tf2playercheck.utils.GuiUtil;

import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Fooberticus
 */
public class APIKeysWindow extends JFrame {
    public APIKeysWindow() {
        initComponents();
        steamHistoryApiKeyField.setText(GuiUtil.getSavedSteamHistoryApiKey());
        steamApiKeyField.setText(GuiUtil.getSavedSteamApiKey());
        backpacktfApiKeyField.setText(GuiUtil.getSavedBackpackTfApiKey());
        checkFields();
        cancelButton.requestFocusInWindow();
    }

    public static void startAPIKeysWindow() {
        GuiUtil.initWindow(new APIKeysWindow(), "APIKeyConfiguration");
    }

    private boolean checkFields() {
        boolean isValid = true;

        if (steamApiKeyField.getText() == null || steamApiKeyField.getText().isEmpty()) {
            steamApiKeyField.putClientProperty("JComponent.outline", "error");
            isValid = false;
        } else {
            steamApiKeyField.putClientProperty("JComponent.outline", "");
        }

        if (steamHistoryApiKeyField.getText() == null || steamHistoryApiKeyField.getText().isEmpty()) {
            steamHistoryApiKeyField.putClientProperty("JComponent.outline", "error");
            isValid = false;
        } else {
            steamHistoryApiKeyField.putClientProperty("JComponent.outline", "");
        }

        if (backpacktfApiKeyField.getText() == null || backpacktfApiKeyField.getText().isEmpty()) {
            backpacktfApiKeyField.putClientProperty("JComponent.outline", "error");
            isValid = false;
        } else {
            backpacktfApiKeyField.putClientProperty("JComponent.outline", "");
        }

        return isValid;
    }

    private void saveSettings() {
        GuiUtil.saveSteamHistoryApiKey(steamHistoryApiKeyField.getText());
        GuiUtil.saveSteamApiKey(steamApiKeyField.getText());
        GuiUtil.saveBackpackTfApiKey(backpacktfApiKeyField.getText());

        if (checkFields()) {
            JOptionPane.showMessageDialog(null, "Settings saved.");
        } else {
            JOptionPane.showMessageDialog(null, "API Keys are required for the program to work correctly!");
        }
    }

    private boolean hasUnsavedChanges() {
        return !steamHistoryApiKeyField.getText().equals(GuiUtil.getSavedSteamHistoryApiKey())
                || !steamApiKeyField.getText().equals(GuiUtil.getSavedSteamApiKey())
                || !backpacktfApiKeyField.getText().equals(GuiUtil.getSavedBackpackTfApiKey());
    }

    private void close() {
        if ( hasUnsavedChanges() ) {
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
                saveSettings();
                dispose();
            } else if (selection == 1) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        steamApiKeyField = new JTextField();
        label2 = new JLabel();
        steamHistoryApiKeyField = new JTextField();
        label3 = new JLabel();
        backpacktfApiKeyField = new JTextField();
        cancelButton = new JButton();
        saveButton = new JButton();

        //======== this ========
        setTitle("API Key Configuration"); //NON-NLS
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        var contentPane = getContentPane();

        //---- label1 ----
        label1.setText("Steam API"); //NON-NLS

        //---- label2 ----
        label2.setText("SteamHistory API"); //NON-NLS

        //---- label3 ----
        label3.setText("Backpack.tf API"); //NON-NLS

        //---- cancelButton ----
        cancelButton.setText("Close"); //NON-NLS
        cancelButton.addActionListener(e -> close());

        //---- saveButton ----
        saveButton.setText("Save"); //NON-NLS
        saveButton.addActionListener(e -> saveSettings());

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(steamApiKeyField, GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                        .addComponent(steamHistoryApiKeyField, GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(label1)
                                .addComponent(label2)
                                .addComponent(label3))
                            .addGap(0, 371, Short.MAX_VALUE))
                        .addComponent(backpacktfApiKeyField, GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGap(0, 278, Short.MAX_VALUE)
                            .addComponent(saveButton)
                            .addGap(18, 18, 18)
                            .addComponent(cancelButton)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(label1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(steamApiKeyField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(label2)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(steamHistoryApiKeyField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(label3)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(backpacktfApiKeyField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(cancelButton)
                        .addComponent(saveButton))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField steamApiKeyField;
    private JLabel label2;
    private JTextField steamHistoryApiKeyField;
    private JLabel label3;
    private JTextField backpacktfApiKeyField;
    private JButton cancelButton;
    private JButton saveButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
