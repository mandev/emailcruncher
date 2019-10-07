package com.adlitteram.emailcruncher.log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static final Logger LOGGER;

    static {
        LOGGER = Logger.getLogger("EmailCruncher");
        LOGGER.setLevel(Level.ALL);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void addHandler(Handler handler) {
        LOGGER.addHandler(handler);
    }

    public static void info(String str) {
        LOGGER.info(str);
    }

    public static void resetHandlers() {
        Handler[] handlers = LOGGER.getHandlers();
        for (Handler handler : handlers) {
            if (handler instanceof LogAreaHandler) {
                ((LogAreaHandler) handler).reset();
            }
        }
    }

    public static void printHandlers() {
        Handler[] handlers = LOGGER.getHandlers();
        for (Handler handler : handlers) {
            System.err.println("handler : " + handler.getClass().getName());
        }
    }
}
