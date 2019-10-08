package com.adlitteram.emailcruncher.utils;

import com.adlitteram.emailcruncher.Main;
import com.adlitteram.emailcruncher.Message;
import com.adlitteram.emailcruncher.gui.widgets.DirChooser;
import com.adlitteram.emailcruncher.gui.widgets.FileChooser;
import com.adlitteram.emailcruncher.gui.widgets.MultilineLabel;
import com.adlitteram.emailcruncher.log.Log;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.logging.Level;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GuiUtils {

    public static final String INFO = Message.get("Information");
    public static final String ERROR = Message.get("Error");
    public static final String ICON_DIR = "resources/icons/";
    public static final Icon HELP_ICON = null;
    public static final Icon OPEN_ICON = null;

    public static void setDefaultLookAndFeel() {
        try {
            System.setProperty("swing.boldMetal", "false");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ignored) {
        }
    }

    public static JButton createBrowseButton(Component parent, JTextField textField) {
        return createBrowseButton(parent, textField, null, "save");
    }

    public static JButton createBrowseButton(Component parent, JTextField textField, javax.swing.filechooser.FileFilter filter) {
        return createBrowseButton(parent, textField, null, "save");
    }

    // DIRECTORIES_ONLY / FILES_AND_DIRECTORIES / FILES_ONLY
    public static JButton createBrowseButton(Component parent, JTextField textField, javax.swing.filechooser.FileFilter filter, String mode) {

        JButton browseButton = new JButton(OPEN_ICON);
        browseButton.putClientProperty("BrowseTextField", textField);
        browseButton.putClientProperty("BrowseParent", parent);
        browseButton.putClientProperty("BrowseFilter", filter);
        browseButton.putClientProperty("BrowseMode", mode);

        browseButton.setMargin(new Insets(0, 5, 0, 5));
        browseButton.addActionListener((ActionEvent e) -> {
            JButton button = (JButton) e.getSource();
            JTextField textField1 = (JTextField) button.getClientProperty("BrowseTextField");
            FileChooser fc = (FileChooser) button.getClientProperty("BrowseFileChooser");
            if (fc == null) {
                Component parent1 = (Component) button.getClientProperty("Parent");
                fc = new FileChooser(parent1, Message.get("Select"));
                fc.setFile(textField1.getText());
                javax.swing.filechooser.FileFilter filter1 = (javax.swing.filechooser.FileFilter) button.getClientProperty("BrowseFilter");
                if (filter1 != null) {
                    fc.addFileFilter(filter1);
                }
                // int mode = ((Integer)button.getClientProperty("BrowseMode")).intValue() ;
                // fc.setFileSelectionMode(mode) ;
                button.putClientProperty("BrowseFileChooser", fc);
            }
            int status = ("save".equals(button.getClientProperty("BrowseMode"))) ? fc.showSaveDialog() : fc.showOpenDialog();
            if (status == FileChooser.APPROVE_OPTION) {
                textField1.setText(fc.getSelectedFile().getPath());
                textField1.postActionEvent();
            }
        });
        return browseButton;
    }

    public static JButton createDirButton(Component parent, JTextField textField) {

        Frame frame = (parent instanceof Frame) ? (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        JButton browseButton = new JButton(OPEN_ICON);
        browseButton.putClientProperty("BrowseTextField", textField);
        browseButton.putClientProperty("BrowseParent", frame);

        browseButton.setMargin(new Insets(0, 5, 0, 5));
        browseButton.addActionListener((ActionEvent e) -> {
            JButton button = (JButton) e.getSource();
            JTextField textField1 = (JTextField) button.getClientProperty("BrowseTextField");
            DirChooser fc = (DirChooser) button.getClientProperty("BrowseFileChooser");
            if (fc == null) {
                fc = new DirChooser((Frame) button.getClientProperty("BrowseParent"), textField1.getText());
                button.putClientProperty("BrowseFileChooser", fc);
            }
            if (fc.showDialog() == DirChooser.APPROVE_OPTION) {
                textField1.setText(fc.getSelectedDirectory().getPath());
                textField1.postActionEvent();
            }
        });
        return browseButton;
    }

    static public JPanel concat(JComponent cp1, JComponent cp2) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(cp1);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(cp2);
        return panel;
    }

    static public JPanel addLabel(String str, JComponent cp) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(new JLabel(str));
        panel.add(Box.createHorizontalStrut(5));
        panel.add(cp);
        return panel;
    }

    static public JPanel addLabel(JComponent cp, String str) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(cp);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(new JLabel(str));
        return panel;
    }

    // Load Icon from Resource File
    public static ImageIcon loadIcon(String fileName) {
        try {
            URL url = Main.class.getResource(ICON_DIR + fileName);
            if (url == null) {
                Log.getLogger().info("Not valid icon URL (url=null) - " + fileName);
                return null;
            }
            return new ImageIcon(url);
        }
        catch (Exception e) {
            Log.getLogger().log(Level.WARNING, "", e);
            return null;
        }
    }

    // Load Image from Resource File
    public static Image loadImage(String fileName) {
        try {
            URL url = Main.class.getResource(fileName);
            if (url == null) {
                Log.getLogger().info("Not valid image URL (url=null) - " + fileName);
                return null;
            }
            return Toolkit.getDefaultToolkit().getImage(url);
        }
        catch (Exception e) {
            Log.getLogger().log(Level.WARNING, "", e);
            return null;
        }
    }

    // Warning Message Dialog
    public static void showMessage(Object message) {
        Main.getMainframe().toFront();
        JOptionPane.showMessageDialog(Main.getMainframe(), message, INFO, JOptionPane.INFORMATION_MESSAGE);
    }

    // Error Message Dialog
    public static void showError(Object message) {
        Main.getMainframe().toFront();
        JOptionPane.showMessageDialog(Main.getMainframe(), message, ERROR, JOptionPane.ERROR_MESSAGE);
    }

    // Change cursor Icon
    public static void setCursorOnWait(Component cmpt, boolean on) {
        if (cmpt != null) {
            if (on) {
                cmpt.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }
            else {
                cmpt.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    public static boolean showYesNoOptionDialog(Component parent, String title, String text, int width) {
        MultilineLabel label = new MultilineLabel(text);
        label.setMaxWidth(width);
        Object[] options = {Message.get("Ok"), Message.get("Cancel")};
        return (JOptionPane.showOptionDialog(parent, label, title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0])
                == JOptionPane.YES_OPTION);
    }

    public static void deepEnabled(Container container, boolean b) {
        Component[] children = container.getComponents();
        for (Component child : children) {
            if (child instanceof Container) {
                deepEnabled((Container) child, b);
            }
            if (child != null) {
                child.setEnabled(b);
            }
        }
    }
}
