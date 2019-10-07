package com.adlitteram.emailcruncher.gui;

import com.adlitteram.emailcruncher.Cruncher;
import com.adlitteram.emailcruncher.Main;
import com.adlitteram.emailcruncher.Message;
import com.adlitteram.emailcruncher.ActionController;
import com.adlitteram.emailcruncher.log.LogAreaHandler;
import com.adlitteram.emailcruncher.log.Log;
import com.adlitteram.emailcruncher.utils.GuiUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.PatternSyntaxException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author manu
 */
public class MainFrame extends JFrame implements PropertyChangeListener {

    private final Cruncher cruncher;
    private final ActionController controller;

    /**
     * Creates new form MainFrame
     */
    public MainFrame(Cruncher cruncher, ActionController controller) {
        this.cruncher = cruncher;
        this.controller = controller;

        initComponents();

        setIconImage(GuiUtils.loadImage("resources/icons/cruncher.png"));
        cruncher.addPropertyChangeListener(this);
        Log.addHandler(new LogAreaHandler(logArea));

        getRootPane().setDefaultButton(goButton);
        stopButton.setEnabled(false);
        urlCombo.requestFocusInWindow();

        setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if ("emailCount".equals(propertyName)) {
            emailCountLabel.setText(evt.getNewValue().toString());
        }
        else if ("status".equals(propertyName)) {
            final int status = ((Integer) evt.getNewValue());
            if (SwingUtilities.isEventDispatchThread()) {
                updateGuiStatus(status);
            }
            else {
                SwingUtilities.invokeLater(() -> {
                    updateGuiStatus(status);
                });
            }
        }
    }

    private void updateGuiStatus(int status) {
        if (status == Cruncher.RUN) {
            Log.resetHandlers();
            goButton.setEnabled(false);
            stopButton.setEnabled(true);
            getRootPane().setDefaultButton(stopButton);
        }
        else {
            stopButton.setEnabled(false);
            goButton.setEnabled(true);
            getRootPane().setDefaultButton(goButton);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        settingsButton = new javax.swing.JButton();
        sortEmailsButton = new javax.swing.JButton();
        validateEmailsButton = new javax.swing.JButton();
        clearEmailsButton = new javax.swing.JButton();
        exportToButton = new javax.swing.JButton();
        optionPanel = new javax.swing.JPanel();
        skipUrlLabel = new javax.swing.JLabel();
        skipUrlField = new javax.swing.JTextField();
        skipPageField = new javax.swing.JTextField();
        skipPageLabel = new javax.swing.JLabel();
        skipEmailField = new javax.swing.JTextField();
        skipEmailLabel = new javax.swing.JLabel();
        robotsCheck = new javax.swing.JCheckBox();
        limitCombo = new javax.swing.JComboBox();
        limitLabel = new javax.swing.JLabel();
        inDepthLabel = new javax.swing.JLabel();
        outDepthLabel = new javax.swing.JLabel();
        inDepthCombo = new javax.swing.JComboBox();
        outDepthCombo = new javax.swing.JComboBox();
        urlLabel = new javax.swing.JLabel();
        stopButton = new javax.swing.JButton();
        goButton = new javax.swing.JButton();
        urlCombo = new javax.swing.JComboBox();
        jSplitPane1 = new javax.swing.JSplitPane();
        emailsScrollPane = new javax.swing.JScrollPane();
        emailsList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        logScrollPane = new javax.swing.JScrollPane();
        logArea = new javax.swing.JTextArea();
        logWindowLabel = new javax.swing.JLabel();
        About = new org.jdesktop.swingx.JXHyperlink();
        emailLabel = new javax.swing.JLabel();
        emailCountLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Email Cruncher"); // NOI18N
        setLocationByPlatform(true);
        setName("null"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        settingsButton.setAction(controller.getAction("Settings"));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adlitteram/emailcruncher/resources/bundle"); // NOI18N
        settingsButton.setText(bundle.getString("Settings")); // NOI18N
        settingsButton.setFocusPainted(false);
        settingsButton.setMargin(new java.awt.Insets(10, 10, 10, 10));
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });

        sortEmailsButton.setAction(controller.getAction("SortEmails"));
        sortEmailsButton.setText(bundle.getString("SortEmails")); // NOI18N
        sortEmailsButton.setFocusPainted(false);
        sortEmailsButton.setMargin(new java.awt.Insets(10, 10, 10, 10));

        validateEmailsButton.setAction(controller.getAction("ValidateEmails"));
        validateEmailsButton.setText(bundle.getString("MainFrame.validateEmailsButton.text_1")); // NOI18N
        validateEmailsButton.setFocusPainted(false);
        validateEmailsButton.setMargin(new java.awt.Insets(10, 10, 10, 10));

        clearEmailsButton.setAction(controller.getAction("ClearEmails"));
        clearEmailsButton.setText(bundle.getString("ClearEmails")); // NOI18N
        clearEmailsButton.setFocusPainted(false);
        clearEmailsButton.setMargin(new java.awt.Insets(10, 10, 10, 10));

        exportToButton.setAction(controller.getAction("ExportEmails"));
        exportToButton.setText(bundle.getString("ExportTo")); // NOI18N
        exportToButton.setFocusPainted(false);
        exportToButton.setMargin(new java.awt.Insets(10, 10, 10, 10));

        optionPanel.setBorder(null);

        skipUrlLabel.setText(bundle.getString("SkipURL")); // NOI18N

        skipUrlField.setText(cruncher.getUrlFilter());

        skipPageField.setText(cruncher.getPageFilter());

        skipPageLabel.setText(bundle.getString("SkipPage")); // NOI18N

        skipEmailField.setText(cruncher.getEmailFilter());

        skipEmailLabel.setText(bundle.getString("SkipEmail")); // NOI18N

        robotsCheck.setSelected(cruncher.isUseRobots());
        robotsCheck.setText(bundle.getString("FollowRobot")); // NOI18N
        robotsCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        robotsCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                robotsCheckActionPerformed(evt);
            }
        });

        limitCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "local links", "all links" }));
        limitCombo.setSelectedIndex(cruncher.getSearchLimit());

        limitLabel.setText(bundle.getString("LimitSearch")); // NOI18N

        inDepthLabel.setText(bundle.getString("CurrentSiteLinkDepth")); // NOI18N

        outDepthLabel.setText(bundle.getString("OtherSiteLinkDepth")); // NOI18N

        inDepthCombo.setEditable(true);
        inDepthCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        outDepthCombo.setEditable(true);
        outDepthCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        javax.swing.GroupLayout optionPanelLayout = new javax.swing.GroupLayout(optionPanel);
        optionPanel.setLayout(optionPanelLayout);
        optionPanelLayout.setHorizontalGroup(
            optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(skipUrlLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(skipEmailLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(skipPageLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addComponent(robotsCheck)
                        .addGap(253, 253, 253))
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(skipPageField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                            .addComponent(skipEmailField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                            .addComponent(skipUrlField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE))
                        .addGap(20, 20, 20)
                        .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(limitLabel)
                            .addComponent(outDepthLabel)
                            .addComponent(inDepthLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(limitCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(outDepthCombo, 0, 1, Short.MAX_VALUE)
                            .addComponent(inDepthCombo, 0, 1, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        optionPanelLayout.setVerticalGroup(
            optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPanelLayout.createSequentialGroup()
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(skipUrlLabel)
                    .addComponent(skipUrlField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(skipEmailLabel)
                    .addComponent(skipEmailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(skipPageLabel)
                    .addComponent(skipPageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(robotsCheck))
            .addGroup(optionPanelLayout.createSequentialGroup()
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(limitLabel)
                    .addComponent(limitCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inDepthCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inDepthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outDepthCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outDepthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        urlLabel.setText(bundle.getString("URL")); // NOI18N

        stopButton.setText(bundle.getString("Stop")); // NOI18N
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        goButton.setText(bundle.getString("Go")); // NOI18N
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });

        urlCombo.setEditable(true);
        urlCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "http://" }));

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(130);
        jSplitPane1.setDividerSize(6);
        jSplitPane1.setForeground(new java.awt.Color(0, 0, 0));
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setLastDividerLocation(130);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        jSplitPane1.setOneTouchExpandable(true);

        emailsList.setModel(cruncher.getEmailListModel());
        emailsList.setDragEnabled(true);
        emailsList.setMinimumSize(new java.awt.Dimension(100, 100));
        emailsScrollPane.setViewportView(emailsList);

        jSplitPane1.setTopComponent(emailsScrollPane);

        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

        logArea.setEditable(false);
        logArea.setColumns(20);
        logArea.setFont(logArea.getFont().deriveFont(logArea.getFont().getSize()-2f));
        logArea.setRows(5);
        logScrollPane.setViewportView(logArea);

        logWindowLabel.setText(bundle.getString("LogWindow")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1022, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(logWindowLabel)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logWindowLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE))
        );

        jSplitPane1.setBottomComponent(jPanel1);

        About.setAction(controller.getAction("About"));
        About.setText(bundle.getString("MainFrame.About.text")); // NOI18N
        About.setClickedColor(new java.awt.Color(0, 51, 255));
        About.setFocusPainted(false);

        emailLabel.setText(bundle.getString("MainFrame.emailLabel.text")); // NOI18N

        emailCountLabel.setText(bundle.getString("MainFrame.emailCountLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(optionPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(emailLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emailCountLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 415, Short.MAX_VALUE)
                        .addComponent(goButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(urlLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(urlCombo, 0, 575, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(sortEmailsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(validateEmailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearEmailsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exportToButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(settingsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                        .addComponent(About, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {goButton, stopButton});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {clearEmailsButton, exportToButton, settingsButton, sortEmailsButton, validateEmailsButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(About, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sortEmailsButton)
                    .addComponent(validateEmailsButton)
                    .addComponent(clearEmailsButton)
                    .addComponent(exportToButton)
                    .addComponent(settingsButton))
                .addGap(18, 18, 18)
                .addComponent(optionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(urlLabel)
                    .addComponent(urlCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(goButton)
                    .addComponent(stopButton)
                    .addComponent(emailLabel)
                    .addComponent(emailCountLabel))
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
       dispose();
       Main.quit();
   }//GEN-LAST:event_formWindowClosing

   private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
       cruncher.stop();
   }//GEN-LAST:event_stopButtonActionPerformed

   private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed
       try {
           cruncher.setUrlFilter(skipUrlField.getText().trim());
           cruncher.setPageFilter(skipPageField.getText().trim());
           cruncher.setEmailFilter(skipEmailField.getText().trim());
           cruncher.setSearchLimit(limitCombo.getSelectedIndex());
           cruncher.setInLinkDepth(Integer.parseInt((String) inDepthCombo.getSelectedItem()));
           cruncher.setOutLinkDepth(Integer.parseInt((String) outDepthCombo.getSelectedItem()));
           cruncher.setUseRobots(robotsCheck.isSelected());

           String url = (String) urlCombo.getSelectedItem();
           cruncher.start(new URL((String) url));

           DefaultComboBoxModel model = (DefaultComboBoxModel) urlCombo.getModel();
           if (model.getIndexOf(url) == -1) {
               model.insertElementAt(url, 0);
           }
           if (urlCombo.getItemCount() > 12) {
               urlCombo.removeItemAt(12);
           }

       }
       catch (MalformedURLException e) {
           JOptionPane.showMessageDialog(this, Message.get("EnterValidURL"));
       }
       catch (NumberFormatException e) {
           JOptionPane.showMessageDialog(this, Message.get("EnterValidLinkDepth"));
       }
       catch (PatternSyntaxException e) {
           JOptionPane.showMessageDialog(this, Message.get("EnterValidRegexp"));
       }
   }//GEN-LAST:event_goButtonActionPerformed

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_settingsButtonActionPerformed

    private void robotsCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_robotsCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_robotsCheckActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXHyperlink About;
    private javax.swing.JButton clearEmailsButton;
    private javax.swing.JLabel emailCountLabel;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JList emailsList;
    private javax.swing.JScrollPane emailsScrollPane;
    private javax.swing.JButton exportToButton;
    private javax.swing.JButton goButton;
    private javax.swing.JComboBox inDepthCombo;
    private javax.swing.JLabel inDepthLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JComboBox limitCombo;
    private javax.swing.JLabel limitLabel;
    private javax.swing.JTextArea logArea;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JLabel logWindowLabel;
    private javax.swing.JPanel optionPanel;
    private javax.swing.JComboBox outDepthCombo;
    private javax.swing.JLabel outDepthLabel;
    private javax.swing.JCheckBox robotsCheck;
    private javax.swing.JButton settingsButton;
    private javax.swing.JTextField skipEmailField;
    private javax.swing.JLabel skipEmailLabel;
    private javax.swing.JTextField skipPageField;
    private javax.swing.JLabel skipPageLabel;
    private javax.swing.JTextField skipUrlField;
    private javax.swing.JLabel skipUrlLabel;
    private javax.swing.JButton sortEmailsButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JComboBox urlCombo;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JButton validateEmailsButton;
    // End of variables declaration//GEN-END:variables

}
