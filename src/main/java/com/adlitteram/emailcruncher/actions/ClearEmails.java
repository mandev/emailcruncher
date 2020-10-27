package com.adlitteram.emailcruncher.actions;

import com.adlitteram.emailcruncher.Cruncher;
import com.adlitteram.emailcruncher.log.LogManager;
import java.awt.event.ActionEvent;

public class ClearEmails extends XAction {

    private final Cruncher cruncher;

    public ClearEmails(Cruncher cruncher) {
        super("ClearEmails");
        this.cruncher = cruncher;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        cruncher.clearEmails();
        LogManager.resetAppenders();
    }
}
