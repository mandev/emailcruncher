package com.adlitteram.emailcruncher.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

public class XFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter, FilenameFilter {

    public static final XFileFilter IMG = new XFileFilter("gif|jpg|jpeg|png", "Images files");
    public static final XFileFilter XLS = new XFileFilter("xls|text", "Excel files");
    public static final XFileFilter TXT = new XFileFilter("txt|csv|text", "Text files");

    private final String[] exts;
    private final String desc;
    private final boolean acceptDir;

    private XFileFilter(String ext, String desc) {
        this(ext, desc, true);
    }

    private XFileFilter(String ext, String desc, boolean ad) {
        var st = new StringTokenizer(ext, "|");
        exts = new String[st.countTokens()];

        for (var i = 0; i < exts.length; i++) {
            exts[i] = st.nextToken();
        }

        this.desc = desc;
        this.acceptDir = ad;
    }

    @Override
    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return acceptDir;
        }

        String ext = getSuffix(file.getName());
        for (String e : exts) {
            if (e.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    public String[] getExtension() {
        return exts;
    }

    private static String getSuffix(String str) {
        var i = str.lastIndexOf('.');
        if ((i > 0) && (i < (str.length() - 1))) {
            return str.substring(i + 1);
        }
        return null;
    }
}
