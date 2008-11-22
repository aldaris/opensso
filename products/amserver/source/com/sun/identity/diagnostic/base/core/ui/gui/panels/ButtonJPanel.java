/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: ButtonJPanel.java,v 1.1 2008-11-22 02:19:57 ak138937 Exp $
 *
 */

package com.sun.identity.diagnostic.base.core.ui.gui.panels;

import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class ButtonJPanel extends javax.swing.JPanel {
    
    private JComponent parent;
    
    /** Creates new form ButtonJPanel */
    public ButtonJPanel(JComponent parent) {
        this.parent = parent;
        initComponents();
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        saveAlljButton = new javax.swing.JButton();
        clearAlljButton = new javax.swing.JButton();
        quitjButton = new javax.swing.JButton();

        saveAlljButton.setText("Save All Results");
        saveAlljButton.setEnabled(false);
        add(saveAlljButton);

        clearAlljButton.setText("Clear All");
        clearAlljButton.setEnabled(false);
        add(clearAlljButton);

        quitjButton.setText("Quit");
        quitjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitjButtonActionPerformed(evt);
            }
        });
        add(quitjButton);
    }

    private void quitjButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (JOptionPane.showConfirmDialog(parent, "Exit program?",
            "Confirm Exit", JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    public void addClearAllButtonActionListener(ActionListener listener) {
        clearAlljButton.addActionListener(listener);
    }
    
    public void enableClearAllButton() {
        clearAlljButton.setEnabled(true);
    }
    
    public void disableClearAllButton() {
        clearAlljButton.setEnabled(false);
    }
    
    public void addSaveAllButtonActionListener(ActionListener listener) {
        saveAlljButton.addActionListener(listener);
    }
    
    public void enableSaveAllButton() {
        saveAlljButton.setEnabled(true);
    }
    
    public void disableSaveAllButton() {
        saveAlljButton.setEnabled(false);
    }
    
    public void enableComponentsAfterRunning() {
        enableSaveAllButton();
        enableClearAllButton();
    }
    
    public void disableComponentsWhileRunning() {
        disableSaveAllButton();
        disableClearAllButton();
    }
    
    private javax.swing.JButton clearAlljButton;
    private javax.swing.JButton quitjButton;
    private javax.swing.JButton saveAlljButton;
}
