package com.adlitteram.emailcruncher.actions;

import com.adlitteram.emailcruncher.Cruncher;
import com.adlitteram.emailcruncher.gui.AboutDialog;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;

public class About extends XAction {

    private final Cruncher cruncher;

    public About(Cruncher cruncher) {
        super("About");
        this.cruncher = cruncher;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var c = e.getSource();
        var window = (c instanceof Component) ? SwingUtilities.getWindowAncestor((Component) c) : null;
        new AboutDialog((Frame) window, true);
    }
}
