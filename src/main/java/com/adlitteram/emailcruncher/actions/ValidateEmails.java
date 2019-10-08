package com.adlitteram.emailcruncher.actions;

import com.adlitteram.emailcruncher.Cruncher;
import com.adlitteram.emailcruncher.utils.EmailSyntaxValidator;
import com.adlitteram.emailcruncher.log.Log;

import java.awt.event.ActionEvent;
import javax.swing.DefaultListModel;

public class ValidateEmails extends XAction {

    private final Cruncher cruncher;

    public ValidateEmails(Cruncher cruncher) {
        super("ValidateEmails");
        this.cruncher = cruncher;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultListModel model = cruncher.getEmailListModel();
        for (int i = model.getSize() - 1; i >= 0; i--) {
            String email = (String) model.getElementAt(i);
            if (EmailSyntaxValidator.howValid(email) < 3) {
                Log.getLogger().info("Removing invalid email: " + email);
                model.removeElementAt(i);
                cruncher.decEmailCount();
            }
        }
    }
}
