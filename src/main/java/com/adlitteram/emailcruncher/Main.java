/*
 * Main.java
 *
 * Created on 27 avril 2007, 09:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.gui.MainFrame;
import com.adlitteram.emailcruncher.utils.GuiUtils;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author manu
 */
public class Main {
    //~--- static fields -------------------------------------------------------

    private static Cruncher cruncher;              // Model
    private static MainFrame mainframe;            // View
    private static ActionController controller;    // Conroller

    //~--- constructors --------------------------------------------------------
    /**
     * Creates a new instance of Main
     */
    public Main() {

    }

    //~--- methods -------------------------------------------------------------
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

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mainframe = new MainFrame(cruncher, controller);
            }
        });

    }
}
