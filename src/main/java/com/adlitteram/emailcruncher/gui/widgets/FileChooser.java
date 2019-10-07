package com.adlitteram.emailcruncher.gui.widgets;

import com.adlitteram.emailcruncher.Main;
import com.adlitteram.emailcruncher.Message;
import com.adlitteram.emailcruncher.utils.GuiUtils;
import com.adlitteram.emailcruncher.utils.Plateform;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class FileChooser {

    static final int LOAD = FileDialog.LOAD;
    static final int SAVE = FileDialog.SAVE;
    public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
    public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;

    private boolean isNative;
    private FileDialog awtChooser;
    private JFileChooser swingChooser;
    private Component parent;
    private String title;
    private int mode;
    private File selectedFile;

    public FileChooser() {
        this(null, "");
    }

    public FileChooser(String title) {
        this(null, title);
    }

    public FileChooser(Component cmp, String title) {
        this.title = title;

        isNative = Plateform.isMacOSX();

        if (isNative) {
            Frame frame = (cmp instanceof Frame) ? (Frame) cmp : Main.getMainframe();
            awtChooser = new FileDialog(frame, title);
            awtChooser.setLocationRelativeTo(frame);
        }
        else {
            parent = (cmp != null) ? cmp : Main.getMainframe();
            swingChooser = new JFileChooser() {
                public void approveSelection() {
                    if (mode == SAVE) {
                        File file = swingChooser.getSelectedFile();
                        if (file.exists()) {

                            // switch ( JOptionPane.showOptionDialog(Main.getMainframe(),
                            switch (JOptionPane.showOptionDialog(swingChooser, Message.get("Overwrite") + " : \n" + file.getPath(), swingChooser.getDialogTitle(),
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null)) {
                                case JOptionPane.CLOSED_OPTION:
                                case JOptionPane.CANCEL_OPTION:
                                case JOptionPane.NO_OPTION:
                                    return;
                            }
                        }
                    }
                    super.approveSelection();
                }
            };

            swingChooser.setDialogTitle(title);
        }
    }

    private int showAwtDialog(int mode) {
        awtChooser.setMode(mode);

        if (mode == LOAD) {
            awtChooser.setVisible(true);
            if (awtChooser.getFile() == null) {
                return CANCEL_OPTION;
            }
            selectedFile = new File(awtChooser.getDirectory(), awtChooser.getFile());
            return APPROVE_OPTION;
        }
        else {
            for (;;) {
                awtChooser.setVisible(true);
                if (awtChooser.getFile() == null) {
                    return CANCEL_OPTION;
                }
                selectedFile = new File(awtChooser.getDirectory(), awtChooser.getFile());
                if (selectedFile.getParentFile().isDirectory()) {
                    return APPROVE_OPTION;
                }

                GuiUtils.showError(Message.get("IsNotDir"));
            }
        }
    }

    private int showSwingDialog(int mode, String text) {
        this.mode = mode;
        int status;

        if (mode == LOAD) {
            status = (text == null) ? swingChooser.showOpenDialog(parent) : swingChooser.showDialog(parent, text);
            if (status == APPROVE_OPTION) {
                selectedFile = swingChooser.getSelectedFile();
            }
        }
        else {
            for (;;) {
                status = (text == null) ? swingChooser.showSaveDialog(parent) : swingChooser.showDialog(parent, text);
                if (status != APPROVE_OPTION) {
                    break;
                }

                selectedFile = swingChooser.getSelectedFile();
                if (selectedFile.getParentFile().isDirectory()) {
                    break;
                }
                GuiUtils.showError(Message.get("IsNotDir"));
            }
        }
        return status;
    }

    public boolean isNative() {
        return isNative;
    }

    public int showOpenDialog() {
        return showOpenDialog(null);
    }

    public int showSaveDialog() {
        return showSaveDialog(null);
    }

    public int showOpenDialog(String text) {
        selectedFile = null;
        return (isNative) ? showAwtDialog(LOAD) : showSwingDialog(LOAD, text);
    }

    public int showSaveDialog(String text) {
        selectedFile = null;
        return (isNative) ? showAwtDialog(SAVE) : showSwingDialog(SAVE, text);
    }

    public Component getChooser() {
        if (isNative) {
            return awtChooser;
        }
        else {
            return swingChooser;
        }
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setAccessory(JComponent accessory) {

        // this.accessory = accessory ;
        if (!isNative) {
            swingChooser.setAccessory(accessory);
        }
    }

    public void setDirectory(String dirname) {
        File file = new File(dirname);
        if (isNative) {
            awtChooser.setDirectory(file.getPath());
        }
        else {
            swingChooser.setCurrentDirectory(file);
        }
    }

    public void setFile(String filename) {
        File file = new File(filename);
        if (isNative) {
            awtChooser.setFile(file.getName());
        }
        else {
            swingChooser.setSelectedFile(file);
        }
    }

    public void addFileFilter(FileFilter filter) {
        final FileFilter ff = filter;

        if (isNative) {
            FilenameFilter namefilter = (File dir, String name) -> ff.accept(new File(dir, name));
            awtChooser.setFilenameFilter(namefilter);
        }
        else {
            swingChooser.addChoosableFileFilter(filter);
        }

    }
}
