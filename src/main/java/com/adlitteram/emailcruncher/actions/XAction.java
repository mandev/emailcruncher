package com.adlitteram.emailcruncher.actions;

import com.adlitteram.emailcruncher.Message;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

abstract public class XAction extends AbstractAction {

    public XAction(String actionName) {
        super(actionName);

        // Text (label) & Mnemonic
        var text = Message.get(actionName.concat(".text"));
        if (text == null) {
            text = actionName;
        }

        var index = text.indexOf('$');
        if ((index != -1) && ((text.length() - index) > 1)) {
            text = text.substring(0, index).concat(text.substring(++index));
            putValue(MNEMONIC_KEY, (int) (text.charAt(index - 1)));
        }
        putValue("TEXT", text);

        // Label
        var labelName = Message.get(actionName + ".label");
        if (labelName != null) {
            putValue("LABEL", labelName);
        }

        // Tooltip
        var tipName = Message.get(actionName.concat(".tip"));
        if (tipName != null) {
            putValue(SHORT_DESCRIPTION, tipName);
        }
    }

    public String getName() {
        return (String) getValue(NAME);
    }

    public String getText() {
        return (String) getValue("TEXT");
    }

    public String getLabel() {
        return (String) getValue("LABEL");
    }

    public KeyStroke getAccelerator() {
        return (KeyStroke) getValue(ACCELERATOR_KEY);
    }

    public Icon getIcon() {
        return (Icon) getValue(SMALL_ICON);
    }

    public int getMnemonic() {
        var n = (Integer) getValue(MNEMONIC_KEY);
        return n == null ? -1 : n ;
    }

    public String getToolTipText() {
        return (String) getValue(SHORT_DESCRIPTION);
    }
}
