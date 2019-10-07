package com.adlitteram.emailcruncher.actions;

import com.adlitteram.emailcruncher.Cruncher;
import java.awt.event.ActionEvent;

public class StopScan extends XAction {

    private final Cruncher cruncher;

    public StopScan(Cruncher cruncher) {
        super("StopScan");
        this.cruncher = cruncher;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        cruncher.stop();
    }
}
