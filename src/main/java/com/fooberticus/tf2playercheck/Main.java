package com.fooberticus.tf2playercheck;

import com.fooberticus.tf2playercheck.gui.frames.MainWindow;
import com.fooberticus.tf2playercheck.utils.GuiUtil;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        GuiUtil.initGui();
        SwingUtilities.invokeLater(MainWindow::startMainWindow);
    }

}
