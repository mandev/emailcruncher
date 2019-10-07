package com.adlitteram.emailcruncher.gui.widgets;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DirectoryChooser extends JTree implements TreeSelectionListener, MouseListener {

    private static FileSystemView fsv = FileSystemView.getFileSystemView();

    public DirectoryChooser() {
        this(null);
    }

    public DirectoryChooser(File dir) {
        super(new DirNode(fsv.getRoots()[0]));
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setSelectedDirectory(dir);
        addTreeSelectionListener(this);
        addMouseListener(this);
    }

    public void setSelectedDirectory(File dir) {
        if ((dir == null) || !dir.exists()) {
            dir = fsv.getDefaultDirectory();
        }
        setSelectionPath(mkPath(dir));
    }

    public File getSelectedDirectory() {
        DirNode node = (DirNode) getLastSelectedPathComponent();
        if (node != null) {
            File dir = node.getDir();
            if (fsv.isFileSystem(dir)) {
                return dir;
            }
        }
        return null;
    }

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    public ActionListener[] getActionListeners() {
        return (ActionListener[]) listenerList.getListeners(ActionListener.class);
    }

    @Override
    public void valueChanged(TreeSelectionEvent ev) {
        File oldDir = null;
        TreePath oldPath = ev.getOldLeadSelectionPath();
        if (oldPath != null) {
            oldDir = ((DirNode) oldPath.getLastPathComponent()).getDir();

            if (!fsv.isFileSystem(oldDir)) {
                oldDir = null;
            }
        }
        File newDir = getSelectedDirectory();
        firePropertyChange("selectedDirectory", oldDir, newDir);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 2) {
            TreePath path = getPathForLocation(e.getX(), e.getY());
            if ((path != null) && path.equals(getSelectionPath()) && (getSelectedDirectory() != null)) {

                fireActionPerformed("dirSelected", e);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private TreePath mkPath(File dir) {
        if ((dir == null) || !dir.exists()) {
            return null;
        }

        DirNode root = (DirNode) getModel().getRoot();
        if (root.getDir().equals(dir)) {
            return new TreePath(root);
        }

        TreePath parentPath = mkPath(fsv.getParentDirectory(dir));
        DirNode parentNode = (DirNode) parentPath.getLastPathComponent();
        Enumeration enumeration = parentNode.children();
        while (enumeration.hasMoreElements()) {
            DirNode child = (DirNode) enumeration.nextElement();
            if (child.getDir().equals(dir)) {
                return parentPath.pathByAddingChild(child);
            }
        }
        return null;
    }

    private void fireActionPerformed(String command, InputEvent evt) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command, evt.getWhen(), evt.getModifiers());
        ActionListener[] listeners = getActionListeners();
        for (int i = listeners.length - 1; i >= 0; i--) {
            listeners[i].actionPerformed(e);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
        }

        final JDialog dialog = new JDialog((JFrame) null, true);
        final DirectoryChooser dc = new DirectoryChooser();
        final JButton okButton = new JButton("OK");
        final JButton cancelButton = new JButton("Cancel");

        dialog.getContentPane().add(new JScrollPane(dc), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        ActionListener actionListener = (ActionEvent e) -> {
            Object c = e.getSource();
            if ((c == okButton) || (c == dc)) {
                System.out.println("You selected: " + dc.getSelectedDirectory());
            }
            dialog.hide();
        };

        dc.addActionListener(actionListener);
        okButton.addActionListener(actionListener);
        cancelButton.addActionListener(actionListener);

        dc.addPropertyChangeListener((PropertyChangeEvent ev) -> {
            if (ev.getPropertyName().equals("selectedDirectory")) {
                okButton.setEnabled(dc.getSelectedDirectory() != null);
            }
        });

        dialog.setBounds(200, 200, 300, 350);
        dc.scrollRowToVisible(Math.max(0, dc.getMinSelectionRow() - 4));
        dialog.show();
        System.exit(0);
    }

    private static class DirNode extends DefaultMutableTreeNode {

        DirNode(File dir) {
            super(dir);
        }

        public File getDir() {
            return (File) userObject;
        }

        @Override
        public int getChildCount() {
            populateChildren();
            return super.getChildCount();
        }

        @Override
        public Enumeration children() {
            populateChildren();
            return super.children();
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

        private void populateChildren() {
            if (children == null) {
                File[] files = fsv.getFiles(getDir(), true);
                Arrays.sort(files);

                for (File f : files) {
                    if (fsv.isTraversable(f)) {
                        insert(new DirNode(f), (children == null) ? 0 : children.size());
                    }
                }
            }
        }

        @Override
        public String toString() {
            return fsv.getSystemDisplayName(getDir());
        }

        @Override
        public boolean equals(Object o) {
            return ((o instanceof DirNode) && userObject.equals(((DirNode) o).userObject));
        }
    }
}
