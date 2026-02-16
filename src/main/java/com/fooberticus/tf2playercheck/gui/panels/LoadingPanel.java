package com.fooberticus.tf2playercheck.gui.panels;

import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author Fooberticus
 */
public class LoadingPanel extends JPanel {
    public LoadingPanel(String labelText) {
        initComponents();
        statusMessageLabel.setText(labelText);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        statusMessageLabel = new JLabel();
        statusProgressBar = new JProgressBar();

        //======== this ========

        //---- statusMessageLabel ----
        statusMessageLabel.setText("text"); //NON-NLS
        statusMessageLabel.putClientProperty("FlatLaf.styleClass", "large"); //NON-NLS

        //---- statusProgressBar ----
        statusProgressBar.setIndeterminate(true);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(statusMessageLabel, GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                        .addComponent(statusProgressBar, GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(19, 19, 19)
                    .addComponent(statusMessageLabel)
                    .addGap(18, 18, 18)
                    .addComponent(statusProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(33, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JLabel statusMessageLabel;
    private JProgressBar statusProgressBar;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
