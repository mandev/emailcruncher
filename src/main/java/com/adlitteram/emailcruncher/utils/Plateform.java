package com.adlitteram.emailcruncher.utils;

public class Plateform {

    public static final String OS_NAME = System.getProperty("os.name");
    public static final String OS_VERSION = System.getProperty("os.version");
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final String JAVA_VENDOR = System.getProperty("java.vendor");
    public static final boolean MAC_OSX = (System.getProperty("mrj.version") != null);
    public static final boolean WINDOWS = (OS_NAME.toLowerCase().startsWith("win"));
    public static final boolean LINUX = (OS_NAME.toLowerCase().contains("linux"));
    public static final boolean SUNOS = (OS_NAME.toLowerCase().contains("sunos"));

    public static boolean isWindows() {
        return WINDOWS;
    }

    public static boolean isLinux() {
        return LINUX;
    }

    public static boolean isSunOS() {
        return SUNOS;
    }

    public static boolean isMacOSX() {
        return MAC_OSX;
    }
}
