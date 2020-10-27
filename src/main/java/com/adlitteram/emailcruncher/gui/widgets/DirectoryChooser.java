package com.adlitteram.emailcruncher.gui.widgets;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.logging.Logger;
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

    private static final FileSystemView fsv = FileSystemView.getFileSystemView();

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
        var node = (DirNode) getLastSelectedPathComponent();
        if (node != null) {
            var dir = node.getDir();
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
        return listenerList.getListeners(ActionListener.class);
    }

    @Override
    public void valueChanged(TreeSelectionEvent ev) {
        File oldDir = null;
        var oldPath = ev.getOldLeadSelectionPath();
        if (oldPath != null) {
            oldDir = ((DirNode) oldPath.getLastPathComponent()).getDir();
            if (!fsv.isFileSystem(oldDir)) {
                oldDir = null;
            }
        }
        var newDir = getSelectedDirectory();
        firePropertyChange("selectedDirectory", oldDir, newDir);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 2) {
            var path = getPathForLocation(e.getX(), e.getY());
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

        var root = (DirNode) getModel().getRoot();
        if (root.getDir().equals(dir)) {
            return new TreePath(root);
        }

        var parentPath = mkPath(fsv.getParentDirectory(dir));
        var parentNode = (DirNode) parentPath.getLastPathComponent();
        var enumeration = parentNode.children();
        while (enumeration.hasMoreElements()) {
            var child = (DirNode) enumeration.nextElement();
            if (child.getDir().equals(dir)) {
                return parentPath.pathByAddingChild(child);
            }
        }
        return null;
    }

    private void fireActionPerformed(String command, InputEvent evt) {
        var e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command, evt.getWhen(), evt.getModifiers());
        var listeners = getActionListeners();
        for (var i = listeners.length - 1; i >= 0; i--) {
            listeners[i].actionPerformed(e);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ignored) {
        }

        final var dialog = new JDialog((JFrame) null, true);
        final var dc = new DirectoryChooser();
        final var okButton = new JButton("OK");
        final var cancelButton = new JButton("Cancel");

        dialog.getContentPane().add(new JScrollPane(dc), BorderLayout.CENTER);

        var buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        ActionListener actionListener = e -> {
            var c = e.getSource();
            if ((c == okButton) || (c == dc)) {
                System.out.println("You selected: " + dc.getSelectedDirectory());
            }
            dialog.setVisible(false);
        };

        dc.addActionListener(actionListener);
        okButton.addActionListener(actionListener);
        cancelButton.addActionListener(actionListener);

        dc.addPropertyChangeListener(ev -> {
            if (ev.getPropertyName().equals("selectedDirectory")) {
                okButton.setEnabled(dc.getSelectedDirectory() != null);
            }
        });

        dialog.setBounds(200, 200, 300, 350);
        dc.scrollRowToVisible(Math.max(0, dc.getMinSelectionRow() - 4));
        dialog.setVisible(true);
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
                var files = fsv.getFiles(getDir(), true);
                Arrays.sort(files);

                for (var f : files) {
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

        @Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }

    }
}
