/*
 * Message.java
 *
 * Created on 31 juillet 2006, 22:17
 *
 */
package com.adlitteram.emailcruncher;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Message {

    private static String baseName = "com.adlitteram.emailcruncher.resources.bundle";
    private static ResourceBundle resource = ResourceBundle.getBundle(baseName);

    public static final String get(String key) {
        try {
            return resource.getString(key);
        }
        catch (MissingResourceException e) {
            return key;
        }
    }

    public static final String get(String key, Object arg1) {
        return get(key, new Object[]{arg1});
    }

    public static final String get(String key, Object arg1, Object arg2) {
        return get(key, new Object[]{arg1, arg2});
    }

    public static final String get(String key, Object[] args) {
        if (args == null) {
            return get(key);
        }

        try {
            return MessageFormat.format(resource.getString(key), args);
        }
        catch (MissingResourceException e) {
            return key;
        }
    }
}
