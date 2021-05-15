package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.gui.MainFrame;
import com.adlitteram.emailcruncher.utils.GuiUtils;
import java.awt.EventQueue;

public class Main {

    private static Cruncher cruncher;       // Model
    private static MainFrame mainframe;     // View

    public Main() {
    }

    public static MainFrame getMainframe() {
        return mainframe;
    }

    public static void main(String[] args) {
        cruncher = Cruncher.create();
        ActionController controller = ActionController.create(cruncher);
        GuiUtils.setDefaultLookAndFeel();
        EventQueue.invokeLater(() -> mainframe = MainFrame.create(cruncher, controller));
    }

    public static void quit() {
        cruncher.save();
        System.exit(0);
    }

}
