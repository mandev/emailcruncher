package com.adlitteram.emailcruncher;

import com.adlitteram.emailcruncher.actions.*;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

public class ActionController {

    private final Cruncher cruncher;

    private final InputMap inputMap = new InputMap();
    private final ActionMap actionMap = new ActionMap();

    public ActionController(Cruncher cruncher) {
        this.cruncher = cruncher;

        addAction(new About(cruncher));    // or if we need : addAction(new About(), "control A");
        addAction(new ClearEmails(cruncher));
        addAction(new ExportEmails(cruncher));
        addAction(new Settings(cruncher));
    }

    public void addAction(XAction action) {
        actionMap.put(action.getName(), action);
        KeyStroke key = (KeyStroke) action.getValue(AbstractAction.ACCELERATOR_KEY);
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
