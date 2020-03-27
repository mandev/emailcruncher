package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.actions.About;
import com.adlitteram.emailcruncher.actions.ClearEmails;
import com.adlitteram.emailcruncher.actions.ExportEmails;
import com.adlitteram.emailcruncher.actions.Settings;
import com.adlitteram.emailcruncher.actions.XAction;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

public class ActionController {

    private final InputMap inputMap = new InputMap();
    private final ActionMap actionMap = new ActionMap();

    private ActionController() {
    }

    public static ActionController create(Cruncher cruncher) {
        var ac = new ActionController();
        ac.addAction(new About(cruncher));    // or if we need : addAction(new About(), "control A");
        ac.addAction(new ClearEmails(cruncher));
        ac.addAction(new ExportEmails(cruncher));
        ac.addAction(new Settings(cruncher));
        return ac;
    }

    public void addAction(XAction action) {
        actionMap.put(action.getName(), action);
        var key = (KeyStroke) action.getValue(AbstractAction.ACCELERATOR_KEY);
        if (key != null) {
            inputMap.put(key, action.getName());
        }
    }

    public void addAction(XAction action, KeyStroke key) {
        actionMap.put(action.getName(), action);
        if (key != null) {
            inputMap.put(key, action.getName());
        }
    }

    public void addAction(XAction action, String key) {
        actionMap.put(action.getName(), action);
        if (key != null) {
            inputMap.put(KeyStroke.getKeyStroke(key), action.getName());
        }
    }

    public XAction getAction(String action) {
        return (XAction) actionMap.get(action);
    }

    public InputMap getInputMap() {
        return inputMap;
    }

    public ActionMap getActionMap() {
        return actionMap;
    }
}
