package com.adlitteram.emailcruncher.gui.widgets;

import com.adlitteram.emailcruncher.Main;
import com.adlitteram.emailcruncher.Message;
import com.adlitteram.emailcruncher.utils.Plateform;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import javax.swing.JFileChooser;

public class DirChooser {

    private static final int LOAD = FileDialog.LOAD;
    private static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
    public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;

    private final boolean isNative;
    private FileDialog awtChooser;
    private DirectoryDialog swingChooser;
    private File selectedDir;

    public DirChooser(String dirname) {
        this(null, dirname);
    }

    public DirChooser(Component cmp, String dirname) {
        this(cmp, dirname, Message.get("DirectoryDialog.Title"));
    }

    public DirChooser(Component cmp, String dirname, String title) {
        String dirname1 = dirname;
        if (dirname == null) {
            dirname = System.getProperty("user.home");
        }

        Frame frame = (cmp instanceof Frame) ? (Frame) cmp : Main.getMainframe();
        isNative = Plateform.isMacOSX();

        if (isNative) {
            awtChooser = new FileDialog(frame, title);
            awtChooser.setMode(LOAD);
            awtChooser.setDirectory(dirname);
            awtChooser.setLocationRelativeTo(frame);
        }
        else {
            swingChooser = new DirectoryDialog(frame, dirname, title);
            swingChooser.setLocationRelativeTo(frame);
        }
    }

    private int showAwtDialog() {
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        awtChooser.setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");

        String filename = awtChooser.getFile();
        if (filename != null) {
            selectedDir = new File(awtChooser.getDirectory(), filename);
            return APPROVE_OPTION;
        }
        return CANCEL_OPTION;
    }

    private int showSwingDialog() {
        if (swingChooser.showDialog() == APPROVE_OPTION) {
            selectedDir = swingChooser.getSelectedDirectory();
            return APPROVE_OPTION;
        }
        return CANCEL_OPTION;
    }

    public boolean isNative() {
        return isNative;
    }

    public int showDialog() {
        selectedDir = null;
        return (isNative) ? showAwtDialog() : showSwingDialog();
    }

    public Component getChooser() {
        if (isNative) {
            return awtChooser;
        }
        else {
            return swingChooser;
        }
    }

    public File getSelectedDirectory() {
        return selectedDir;
    }
}
