package com.fooberticus.tf2playercheck.gui.frames;

import com.fooberticus.tf2playercheck.utils.GuiUtil;

import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Fooberticus
 */
public class AppearanceWindow extends JFrame {
    public AppearanceWindow() {
        initComponents();
        updateFontSizeLabel();
        closeButton.requestFocusInWindow();
    }

    public static void startAppearanceWindow() {
        GuiUtil.initWindow(new AppearanceWindow(), "AppearanceConfiguration");
    }

    private void increaseFontSize() {
        GuiUtil.increaseGlobalFontSize();
        updateFontSizeLabel();
    }

    private void decreaseFontSize() {
        GuiUtil.decreaseGlobalFontSize();
        updateFontSizeLabel();
    }

    private void updateFontSizeLabel() {
        fontSizeLabel.setText( GuiUtil.getGlobalFontSize() + "" );
    }

    private void changeTheme() {
        GuiUtil.showThemeChangeDialog();
    }

    private void close() {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        decreaseFontSizeButton = new JButton();
        increaseFontSizeButton = new JButton();
        label2 = new JLabel();
        changeThemeButton = new JButton();
        fontSizeLabel = new JLabel();
        closeButton = new JButton();

        //======== this ========
        setTitle("Appearance Configuration"); //NON-NLS
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        var contentPane = getContentPane();

        //---- label1 ----
        label1.setText("Font Size"); //NON-NLS

        //---- decreaseFontSizeButton ----
        decreaseFontSizeButton.setText("-"); //NON-NLS
        decreaseFontSizeButton.addActionListener(e -> decreaseFontSize());

        //---- increaseFontSizeButton ----
        increaseFontSizeButton.setText("+"); //NON-NLS
        increaseFontSizeButton.addActionListener(e -> increaseFontSize());

        //---- label2 ----
        label2.setText("Theme"); //NON-NLS

        //---- changeThemeButton ----
        changeThemeButton.setText("Change Theme"); //NON-NLS
        changeThemeButton.addActionListener(e -> changeTheme());

        //---- fontSizeLabel ----
        fontSizeLabel.setText("fontSize"); //NON-NLS

        //---- closeButton ----
        closeButton.setText("Close"); //NON-NLS
        closeButton.addActionListener(e -> close());

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(label1)
                        .addComponent(label2))
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(decreaseFontSizeButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(increaseFontSizeButton)
                            .addGap(18, 18, 18)
                            .addComponent(fontSizeLabel))
                        .addComponent(changeThemeButton))
                    .addContainerGap(266, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap(397, Short.MAX_VALUE)
                    .addComponent(closeButton)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label1)
                        .addComponent(decreaseFontSizeButton)
                        .addComponent(increaseFontSizeButton)
                        .addComponent(fontSizeLabel))
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(label2)
                        .addComponent(changeThemeButton))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 202, Short.MAX_VALUE)
                    .addComponent(closeButton)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JButton decreaseFontSizeButton;
    private JButton increaseFontSizeButton;
    private JLabel label2;
    private JButton changeThemeButton;
    private JLabel fontSizeLabel;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
