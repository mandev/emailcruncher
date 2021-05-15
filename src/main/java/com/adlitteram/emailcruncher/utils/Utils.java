package com.adlitteram.emailcruncher.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {

    public static void sleep(long s) {
        try {
            Thread.sleep(s);
        }
        catch (InterruptedException ignored) {
        }
    }

    public static void closeQuietly(Reader in) {
        if (in != null) {
            try {
                in.close();
            }
            catch (IOException ignored) {
            }
        }
    }

    public static void closeQuietly(InputStream in) {
        if (in != null) {
            try {
                in.close();
            }
            catch (IOException ignored) {
            }
        }
    }

    public static void execBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        }
        catch (IOException | URISyntaxException ex) {
            log.warn("Unable to exec browser: " + url, ex);
        }
    }
}
