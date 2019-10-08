package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.gui.MainFrame;
import com.adlitteram.emailcruncher.utils.GuiUtils;

public class Main {

    private static Cruncher cruncher;              // Model
    private static MainFrame mainframe;            // View
    private static ActionController controller;    // Conroller

    public Main() {
    }

    public static MainFrame getMainframe() {
        return mainframe;
    }

    public static void quit() {
        cruncher.updatePreferences();
        System.exit(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        GuiUtils.setDefaultLookAndFeel();

        cruncher = new Cruncher();
        controller = new ActionController(cruncher);

        java.awt.EventQueue.invokeLater(() -> mainframe = new MainFrame(cruncher, controller));
    }
}
