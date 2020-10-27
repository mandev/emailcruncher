package com.adlitteram.emailcruncher.log;

import ch.qos.logback.core.Appender;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class LogManager {

    private static final Logger ROOT_LOGGER;

    static {
        ROOT_LOGGER = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        ROOT_LOGGER.setLevel(Level.ALL);
    }

    public static void addAppender(Appender appender) {
        ROOT_LOGGER.setAdditive(false);
        ROOT_LOGGER.addAppender(appender);
        appender.start();
    }

    public static void resetAppenders() {
        for (var i = ROOT_LOGGER.iteratorForAppenders(); i.hasNext();) {
            var appender = i.next();
            if (appender instanceof LogAreaAppender) {
                ((LogAreaAppender) appender).reset();
            }
        }
    }

    public static void printAppenders() {
        for (var i = ROOT_LOGGER.iteratorForAppenders(); i.hasNext();) {
            var appender = i.next();
            System.err.println("appender: " + appender.getClass().getName());
        }
    }
}
