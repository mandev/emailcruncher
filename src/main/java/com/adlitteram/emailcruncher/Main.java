package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.gui.MainFrame;
import com.adlitteram.emailcruncher.log.Log;
import com.adlitteram.emailcruncher.utils.GuiUtils;
import java.awt.EventQueue;

public class Main {

    private static Cruncher cruncher;               // Model
    private static MainFrame mainframe;             // View
    private static ActionController controller;     // Conroller
    private static Configuration config;            // Configuration

    public Main() {
    }

    public static MainFrame getMainframe() {
        return mainframe;
    }

    public static void quit() {
        Log.info("quit");
        config.save();
        System.exit(0);
    }

    public static void main(String[] args) {
        Log.info("main");

        cruncher = new Cruncher();
        controller = new ActionController(cruncher);
        config = new Configuration(cruncher).load();

        GuiUtils.setDefaultLookAndFeel();
        EventQueue.invokeLater(() -> mainframe = new MainFrame(cruncher, controller));
    }
}
