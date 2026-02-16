package com.fooberticus.tf2playercheck.utils;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialDarkerIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialOceanicIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTNightOwlIJTheme;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

@Slf4j
public final class GuiUtil {

    public static final String APPLICATION_NAME = "TF2 Player Check";
    public static final String DEFAULT_THEME = "Material Dark";
    private static final String NAMESPACE = "fooberticus.TF2PlayerCheck";
    private static final int DEFAULT_FONT_SIZE = 15;

    private static final String[] themeNames;
    private static final Preferences prefs;
    private static final Map<String, Class<? extends IntelliJTheme.ThemeLaf>> themeMap;
    private static final Image trayIconImage;

    private static final String PROPERTY_FONT_SIZE = "globalFontSize";
    private static final String PROPERTY_THEME_NAME = "themeName";
    private static final String PROPERTY_WINDOW_WIDTH = "windowWidth";
    private static final String PROPERTY_WINDOW_HEIGHT = "windowHeight";
    private static final String PROPERTY_WINDOW_X = "windowX";
    private static final String PROPERTY_WINDOW_Y = "windowY";

    private static final String PROPERTY_STEAM_HISTORY_KEY = "steamHistoryKey";
    private static final String PROPERTY_STEAM_API_KEY = "steamApiKey";
    private static final String PROPERTY_BACKPACKTF_API_KEY = "backpackTfApiKey";

    static {
        prefs = Preferences.userRoot().node(NAMESPACE);

        themeMap = new HashMap<>();
        themeMap.put("Nord", FlatNordIJTheme.class);
        themeMap.put("Material Dark", FlatMaterialDesignDarkIJTheme.class);
        themeMap.put("Arc Dark Orange", FlatArcDarkOrangeIJTheme.class);
        themeMap.put("Spacegray", FlatSpacegrayIJTheme.class);
        themeMap.put("Night Owl", FlatMTNightOwlIJTheme.class);
        themeMap.put("Material Oceanic", FlatMTMaterialOceanicIJTheme.class);
        themeMap.put("XCode Dark", FlatXcodeDarkIJTheme.class);
        themeMap.put("Monokai Pro", FlatMonokaiProIJTheme.class);
        themeMap.put("High Contrast", FlatHighContrastIJTheme.class);
        themeMap.put("Material Darker", FlatMTMaterialDarkerIJTheme.class);
        themeMap.put("Dracula", FlatDraculaIJTheme.class);
        themeMap.put("Vuesion", FlatVuesionIJTheme.class);
        themeMap.put("Hiberbee", FlatHiberbeeDarkIJTheme.class);

        themeNames = themeMap.keySet().toArray(new String[0]);
        Arrays.sort(themeNames);

        trayIconImage = Toolkit.getDefaultToolkit()
                .createImage( GuiUtil.class.getResource("/images/app_icon.svg") );
    }

    private GuiUtil() {}

    public static void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static String getClipboardText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            return (String) clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            log.error("Tried to retrieve non-text data from clipboard as text.");
        }
        return "";
    }

    public static void openURLInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI( url ) );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /** Call this when the app first starts, to set up FlatLaf for the entire application. */
    public static void initGui() {
        FlatAnimatedLafChange.duration = 300;
        swapTheme(getCurrentTheme());
        UIManager.put("defaultFont", getSavedFont());
        FlatLaf.updateUI();
    }

    /** Call this when creating a new window so the JFrame is created with saved preferences. */
    public static void initWindow(JFrame frame, String windowLabel) {
        frame.setIconImages(FlatSVGUtils.createWindowIconImages( "/images/app_icon.svg" ));

        if (getSavedWindowWidth(windowLabel) > 0) {
            frame.setSize(new Dimension(getSavedWindowWidth(windowLabel), getSavedWindowHeight(windowLabel)));
        }

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Component c = (Component) e.getSource();
                saveWindowWidth(c.getWidth(), windowLabel);
                saveWindowHeight(c.getHeight(), windowLabel);
            }

            public void componentMoved(ComponentEvent e) {
                Component c = (Component) e.getSource();
                saveWindowX(c.getX(), windowLabel);
                saveWindowY(c.getY(), windowLabel);
            }
        });

        if (getSavedWindowX(windowLabel) < 0) {
            frame.setLocationRelativeTo(null);
        } else {
            frame.setLocation(getSavedWindowX(windowLabel), getSavedWindowY(windowLabel));
        }
    }

    /** Display a notification using the native System Tray. */
    public static void showSystemTrayNotification(String msg) {
        showSystemTrayNotification(msg, TrayIcon.MessageType.INFO);
    }

    /** Display an error notification using the native System Tray. */
    public static void showSystemTrayError(String msg) {
        showSystemTrayNotification(msg, TrayIcon.MessageType.ERROR);
    }

    private static void showSystemTrayNotification(String msg, TrayIcon.MessageType type) {
        SystemTray tray = SystemTray.getSystemTray();

        TrayIcon trayIcon = new TrayIcon(trayIconImage, APPLICATION_NAME);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            JOptionPane
                    .showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        trayIcon.displayMessage(APPLICATION_NAME, msg, type);
    }

    /* FONT UTILITIES */

    public static void increaseGlobalFontSize() {
        setGlobalFontSizeOffset(1);
    }

    public static void decreaseGlobalFontSize() {
        setGlobalFontSizeOffset(-1);
    }

    private static Font getSavedFont() {
        Font font = UIManager.getFont("defaultFont");
        return font.deriveFont((float) prefs.getInt(PROPERTY_FONT_SIZE, DEFAULT_FONT_SIZE));
    }

    private static void setGlobalFontSizeOffset(int offSet) {
        Font newFont = getDefaultFontWithSizeOffset(offSet);
        UIManager.put("defaultFont", newFont);
        FlatLaf.updateUI();
        prefs.putInt(PROPERTY_FONT_SIZE, newFont.getSize());
    }

    private static Font getDefaultFontWithSizeOffset(int offSet) {
        Font font = UIManager.getFont("defaultFont");
        return font.deriveFont((float) (font.getSize() + offSet));
    }

    /* THEME UTILITIES */

    public static void showThemeChangeDialog() {
        String themeName = (String) JOptionPane.showInputDialog(
                null,
                "Choose theme:",
                "Change Theme",
                JOptionPane.QUESTION_MESSAGE,
                null,
                themeNames,
                getCurrentTheme());
        if (themeName != null) {
            updateTheme(themeName);
        }
    }

    private static String getCurrentTheme() {
        return prefs.get(PROPERTY_THEME_NAME, DEFAULT_THEME);
    }

    private static void updateTheme(String themeName) {
        if (!themeMap.containsKey(themeName)) {
            log.error("invalid theme name passed to GuiUtil::updateTheme - {}", themeName);
            return;
        }
        FlatAnimatedLafChange.showSnapshot();
        swapTheme(themeName);
        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
        prefs.put(PROPERTY_THEME_NAME, themeName);
    }

    private static void swapTheme(String themeName) {
        try {
            themeMap.get(themeName).getDeclaredMethod("setup").invoke(null);
        } catch (Exception e) {
            log.error("well that didn't work: {}", e.getMessage());
        }
    }

    /* MAIN WINDOW SIZE UTILITIES */

    public static int getSavedWindowWidth(String windowName) {
        return prefs.getInt(PROPERTY_WINDOW_WIDTH + windowName, -1);
    }

    public static void saveWindowWidth(int width, String windowName) {
        prefs.putInt(PROPERTY_WINDOW_WIDTH + windowName, width);
    }

    public static int getSavedWindowHeight(String windowName) {
        return prefs.getInt(PROPERTY_WINDOW_HEIGHT + windowName, -1);
    }

    public static void saveWindowHeight(int height, String windowName) {
        prefs.putInt(PROPERTY_WINDOW_HEIGHT + windowName, height);
    }

    public static int getSavedWindowX(String windowName) {
        return prefs.getInt(PROPERTY_WINDOW_X + windowName, -1);
    }

    public static void saveWindowX(int x, String windowName) {
        prefs.putInt(PROPERTY_WINDOW_X + windowName, x);
    }

    public static int getSavedWindowY(String windowName) {
        return prefs.getInt(PROPERTY_WINDOW_Y + windowName, -1);
    }

    public static void saveWindowY(int y, String windowName) {
        prefs.putInt(PROPERTY_WINDOW_Y + windowName, y);
    }

    /* Steam Utilities */

    public static String getSavedSteamHistoryApiKey() {
        return prefs.get(PROPERTY_STEAM_HISTORY_KEY, null);
    }

    public static void saveSteamHistoryApiKey(String key) {
        prefs.put(PROPERTY_STEAM_HISTORY_KEY, key);
    }

    public static String getSavedSteamApiKey() {
        return prefs.get(PROPERTY_STEAM_API_KEY, null);
    }

    public static void saveSteamApiKey(String key) {
        prefs.put(PROPERTY_STEAM_API_KEY, key);
    }

    public static String getSavedBackpackTfApiKey() {
        return prefs.get(PROPERTY_BACKPACKTF_API_KEY, null);
    }

    public static void saveBackpackTfApiKey(String key) {
        prefs.put(PROPERTY_BACKPACKTF_API_KEY, key);
    }

}
