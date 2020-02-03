package com.adlitteram.emailcruncher;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Message {

    private static final String BUNDLE = "com.adlitteram.emailcruncher.resources.bundle";
    private static final ResourceBundle RESOURCE = ResourceBundle.getBundle(BUNDLE);

    public static String get(String key) {
        try {
            return RESOURCE.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public static String get(String key, Object arg1) {
        return get(key, new Object[]{arg1});
    }

    public static String get(String key, Object arg1, Object arg2) {
        return get(key, new Object[]{arg1, arg2});
    }

    public static String get(String key, Object[] args) {
        if (args == null) {
            return get(key);
        }

        try {
            return MessageFormat.format(RESOURCE.getString(key), args);
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
