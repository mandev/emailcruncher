package com.adlitteram.emailcruncher.actions;

import com.adlitteram.emailcruncher.Cruncher;
import com.adlitteram.emailcruncher.gui.SettingsDialog;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;

public class Settings extends XAction {

    private final Cruncher cruncher;

    public Settings(Cruncher cruncher) {
        super("Settings");
        this.cruncher = cruncher;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var c = e.getSource();
        var window = (c instanceof Component) ? SwingUtilities.getWindowAncestor((Component) c) : null;
        new SettingsDialog(window, cruncher);
    }
}
