/*
 * SettingsDialog.java
 *
 * Created on 1 mai 2007, 14:55
 */
package com.adlitteram.emailcruncher.gui;

import com.adlitteram.emailcruncher.Cruncher;
import com.adlitteram.emailcruncher.Message;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author manu
 */
public class SettingsDialog extends JDialog {

    private final Cruncher cruncher;

    public SettingsDialog(Window window, Cruncher cruncher) {
        super((Frame) window);
        this.cruncher = cruncher;

        initComponents();
        getRootPane().setDefaultButton(okButton);
        setLocationRelativeTo(window);
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        timeoutLabel = new javax.swing.JLabel();
        timeoutField = new javax.swing.JTextField();
        threadsLabel = new javax.swing.JLabel();
        threadsField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        useProxyCheck = new javax.swing.JCheckBox();
        proxyPortLabel = new javax.swing.JLabel();
        proxyAddressLabel = new javax.swing.JLabel();
        proxyHostField = new javax.swing.JTextField();
        proxyPortField = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/adlitteram/emailcruncher/resources/bundle"); // NOI18N
        setTitle(bundle.getString("Cruncher_Settings")); // NOI18N
        setModal(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("Connection"))); // NOI18N

        timeoutLabel.setText(bundle.getString("TimeOut")); // NOI18N

        timeoutField.setColumns(4);
        timeoutField.setText(String.valueOf(cruncher.getTimeOut()));

        threadsLabel.setText(bundle.getString("Threads")); // NOI18N

        threadsField.setColumns(4);
        threadsField.setText(String.valueOf(cruncher.getThreadMax()));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(timeoutLabel)
                    .addComponent(threadsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(threadsField)
                    .addComponent(timeoutField))
                .addContainerGap(200, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeoutLabel)
                    .addComponent(timeoutField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(threadsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(threadsLabel))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("Proxy"))); // NOI18N

        useProxyCheck.setSelected(cruncher.isUseProxy());
        useProxyCheck.setText(bundle.getString("UseProxy")); // NOI18N
        useProxyCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        useProxyCheck.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                useProxyCheckStateChanged(evt);
            }
        });

        proxyPortLabel.setText(bundle.getString("ProxyPort")); // NOI18N
        proxyPortLabel.setEnabled(useProxyCheck.isSelected());

        proxyAddressLabel.setText(bundle.getString("ProxyAddress")); // NOI18N
        proxyAddressLabel.setEnabled(useProxyCheck.isSelected());

        proxyHostField.setText(cruncher.getProxyHost());
        proxyHostField.setEnabled(useProxyCheck.isSelected());

        proxyPortField.setColumns(8);
        proxyPortField.setText(String.valueOf(cruncher.getProxyPort()));
        proxyPortField.setEnabled(useProxyCheck.isSelected());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(proxyHostField)
                    .addComponent(useProxyCheck)
                    .addComponent(proxyAddressLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(proxyPortField, 0, 67, Short.MAX_VALUE)
                    .addComponent(proxyPortLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(useProxyCheck)
                        .addGap(10, 10, 10)
                        .addComponent(proxyAddressLabel))
                    .addComponent(proxyPortLabel))
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(proxyHostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(proxyPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        okButton.setText(bundle.getString("Ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("Cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, cancelButton, okButton);

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
       dispose();
   }//GEN-LAST:event_cancelButtonActionPerformed

   private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
       try {
           int threads = Integer.parseInt(threadsField.getText());
           int timeout = Integer.parseInt(timeoutField.getText());
           boolean useProxy = useProxyCheck.isSelected();
           String host = proxyHostField.getText();
           int port = Integer.parseInt(proxyPortField.getText());

           cruncher.setThreadMax(threads);
           cruncher.setTimeOut(timeout);
           cruncher.setUseProxy(useProxy);
           cruncher.setProxyHost(host);
           cruncher.setProxyPort(port);

           dispose();
       }
       catch (NumberFormatException e) {
           JOptionPane.showMessageDialog(this, Message.get("Error") + e.getLocalizedMessage());
       }
   }//GEN-LAST:event_okButtonActionPerformed

   private void useProxyCheckStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_useProxyCheckStateChanged
       boolean b = useProxyCheck.isSelected();
       proxyAddressLabel.setEnabled(b);
       proxyHostField.setEnabled(b);
       proxyPortLabel.setEnabled(b);
       proxyPortField.setEnabled(b);
   }//GEN-LAST:event_useProxyCheckStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel proxyAddressLabel;
    private javax.swing.JTextField proxyHostField;
    private javax.swing.JTextField proxyPortField;
    private javax.swing.JLabel proxyPortLabel;
    private javax.swing.JTextField threadsField;
    private javax.swing.JLabel threadsLabel;
    private javax.swing.JTextField timeoutField;
    private javax.swing.JLabel timeoutLabel;
    private javax.swing.JCheckBox useProxyCheck;
    // End of variables declaration//GEN-END:variables

}
