package com.adlitteram.emailcruncher.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger("Utils");
    
    public static void sleep(long s) {
        try {
            Thread.sleep(s);
        } catch (InterruptedException ignored) {
        }
    }

    public static void closeQuietly(Reader in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void closeQuietly(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void execBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException ex) {
            LOGGER.warn("Unable to exec browser: " + url, ex);
        }
    }
}
