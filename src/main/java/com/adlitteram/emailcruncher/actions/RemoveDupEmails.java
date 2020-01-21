package com.adlitteram.emailcruncher.actions;

import com.adlitteram.emailcruncher.Cruncher;
import com.adlitteram.emailcruncher.log.Log;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.LinkedHashSet;
import javax.swing.DefaultListModel;

public class RemoveDupEmails extends XAction {

    private final Cruncher cruncher;

    public RemoveDupEmails(Cruncher cruncher) {
        super("RemoveDupEmails");
        this.cruncher = cruncher;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultListModel model = cruncher.getEmailListModel();
        LinkedHashSet set = new LinkedHashSet(Arrays.asList(model.toArray()));    // Remove duplicates

        model.clear();
        set.forEach(o -> model.addElement(o));
        Log.getLogger().info("Unique emails: " + model.getSize());
    }
}
