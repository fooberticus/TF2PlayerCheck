package com.fooberticus.tf2playercheck.gui.frames;

import com.fooberticus.tf2playercheck.gui.panels.AllUsersPanel;
import com.fooberticus.tf2playercheck.gui.panels.CommunityBanPanel;
import com.fooberticus.tf2playercheck.gui.panels.RentAMedicPanel;
import com.fooberticus.tf2playercheck.gui.panels.VACBanPanel;
import com.fooberticus.tf2playercheck.models.CheckResults;
import com.fooberticus.tf2playercheck.utils.GuiUtil;

import javax.swing.*;

/**
 * @author Fooberticus
 */
public class ResultsWindow extends JFrame {

    public ResultsWindow(CheckResults results) {
        initComponents();

        if (!results.steamPlayerBanMap().isEmpty()) {
            resultsTabbedPane.add("Steam Bans", new VACBanPanel(results));
        }
        if (!results.sourceBanMap().isEmpty()) {
            resultsTabbedPane.add("Community Bans", new CommunityBanPanel(results));
        }
        if (!results.rentAMedicResultMap().isEmpty()) {
            resultsTabbedPane.add("Rent-A-Medic", new RentAMedicPanel(results));
        }
        resultsTabbedPane.add("All Players", new AllUsersPanel(results));
    }

    public static void startResultsWindow(CheckResults results) {
        GuiUtil.initWindow(new ResultsWindow(results), "Results");
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
