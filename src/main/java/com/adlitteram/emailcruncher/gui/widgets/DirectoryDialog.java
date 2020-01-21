package com.adlitteram.emailcruncher.gui.widgets;

import com.adlitteram.emailcruncher.Message;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

public class DirectoryDialog extends JDialog {

    private static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
    private static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;

    private int status;
    private final String dirname;
    private DirectoryChooser dc;
    private JButton okButton;

    private final ActionListener approveListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            dispose();
            if (dc.getSelectedDirectory() != null) {
                status = APPROVE_OPTION;
            } else {
                status = CANCEL_OPTION;
            }
        }
    };

    public DirectoryDialog(Frame frame, String dirname, String title) {
        super(frame, title, true);

        this.dirname = dirname;
        status = CANCEL_OPTION;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(buildDirectoryPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        setLocationRelativeTo(frame);
        pack();
    }

    private JComponent buildDirectoryPanel() {
        File dir = new File(dirname);
        if (!dir.isDirectory()) {
            dc = new DirectoryChooser();
        } else {
            dc = new DirectoryChooser(dir);
        }

        dc.addActionListener(approveListener);
        dc.addPropertyChangeListener((PropertyChangeEvent ev) -> {
            if (ev.getPropertyName().equals("selectedDirectory")) {
                okButton.setEnabled(dc.getSelectedDirectory() != null);
            }
        });

        dc.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JScrollPane sp = new JScrollPane(dc);
        sp.setPreferredSize(new Dimension(250, 300));
        return sp;
    }

    private JPanel buildButtonPanel() {
        okButton = new JButton(Message.get("Ok"));

        // okButton.setEnabled(false) ;
        okButton.addActionListener(approveListener);

        JButton cancelButton = new JButton(Message.get("Cancel"));
        cancelButton.addActionListener((ActionEvent e) -> {
            dispose();
            status = CANCEL_OPTION;
        });

        JPanel panel = new JPanel(new GridLayout(1, 0));
        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }

    public int showDialog() {
        dc.scrollRowToVisible(Math.max(0, dc.getMinSelectionRow() - 4));
        setVisible(true);
        return status;
    }

    public File getSelectedDirectory() {
        return dc.getSelectedDirectory();
    }
}
