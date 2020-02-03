package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.gui.MainFrame;
import com.adlitteram.emailcruncher.log.Log;
import com.adlitteram.emailcruncher.utils.GuiUtils;
import java.awt.EventQueue;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Main {

    private static Cruncher cruncher;               // Model
    private static MainFrame mainframe;             // View
    private static ActionController controller;     // Conroller
    private static Preferences prefs;               // Configuration

    public Main() {
    }

    public static MainFrame getMainframe() {
        return mainframe;
    }
    
    // Start
    public static void main(String[] args) {
        Log.info("main");

        prefs = Preferences.userNodeForPackage(Cruncher.class);
        cruncher = Cruncher.create(prefs);
        controller = ActionController.create(cruncher);

        GuiUtils.setDefaultLookAndFeel();
        EventQueue.invokeLater(() -> mainframe = MainFrame.create(cruncher, controller));
    }

    // Quit
    public static void quit() {
        Log.info("quit");
        try {
            prefs = cruncher.update(prefs);
            prefs.flush();
        }
        catch (BackingStoreException ex) {
        }
        System.exit(0);
    }

}
