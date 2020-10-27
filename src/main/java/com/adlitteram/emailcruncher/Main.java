package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.gui.MainFrame;
import com.adlitteram.emailcruncher.utils.GuiUtils;
import java.awt.EventQueue;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger("Main");

    private static Cruncher cruncher;               // Model
    private static MainFrame mainframe;             // View
    private static ActionController controller;     // Conroller
    private static Preferences prefs;               // Configuration

    public Main() {
    }

    public static MainFrame getMainframe() {
        return mainframe;
    }

    public static void main(String[] args) {
        prefs = Preferences.userNodeForPackage(Cruncher.class);
        cruncher = Cruncher.create(prefs);
        controller = ActionController.create(cruncher);

        GuiUtils.setDefaultLookAndFeel();
        EventQueue.invokeLater(() -> mainframe = MainFrame.create(cruncher, controller));
    }

    public static void quit() {
        LOGGER.info("quit");
        try {
            prefs = cruncher.update(prefs);
            prefs.flush();
        }
        catch (BackingStoreException ex) {
        }
        System.exit(0);
    }

}
