package com.adlitteram.emailcruncher.actions;

import com.adlitteram.emailcruncher.Cruncher;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.DefaultListModel;

public class SortEmails extends XAction {

    private final Cruncher cruncher;

    public SortEmails(Cruncher cruncher) {
        super("SortEmails");
        this.cruncher = cruncher;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultListModel model = cruncher.getEmailListModel();
        Object[] emails = model.toArray();
        Arrays.sort(emails);

        for (int i = 0; i < emails.length; i++) {
            model.setElementAt(emails[i], i);
        }
    }
}
