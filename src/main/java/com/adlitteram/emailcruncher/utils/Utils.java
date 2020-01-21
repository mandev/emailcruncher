package com.adlitteram.emailcruncher.utils;

import com.adlitteram.emailcruncher.log.Log;
import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Utils {

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(120, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

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
            Log.getLogger().log(Level.WARNING, "Unable to exec browser : ", ex + " - " + url);
        }
    }
}
