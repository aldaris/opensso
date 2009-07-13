/* The contents of this file are subject to the terms
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
 * $Id:
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.qaweb.beans;

import com.icesoft.faces.component.ext.HtmlPanelGrid;
import com.icesoft.faces.component.paneltabset.PanelTab;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

/**
 * This class changes the menu on user action. The three menus present in
 * server.jsp are server information , config store information and UM
 * datastore Information.
 * 
 */
public class MenuChangeListener implements ActionListener {

    public void processAction(ActionEvent event) {
        String componentId = event.getComponent().getId();

        /*
         * Get the PanelTabeSet UI component. Event has been triggered by
         * htmlCommand Button which is residing in a grid which inturn is a
         * child of PanelTab . PanelTabset is the parent of Paneltab
         */
        PanelTabSet panelTabSet = (PanelTabSet) event.getComponent().
                getParent().getParent().getParent().getParent();
        PanelTab paneltab = (PanelTab) event.getComponent().getParent().
                getParent().getParent();
        int selectedTab = panelTabSet.getSelectedIndex();
        if (componentId.equals("servermenu" + selectedTab)) {
            HtmlPanelGrid serverGrid = (HtmlPanelGrid) paneltab.
                    findComponent("serverGrid" + selectedTab);
            HtmlPanelGrid configGrid = (HtmlPanelGrid) paneltab.
                    findComponent("configGrid" + selectedTab);
            HtmlPanelGrid umGrid = (HtmlPanelGrid) paneltab.
                    findComponent("umGrid" + selectedTab);
            serverGrid.setVisible(true);
            configGrid.setVisible(false);
            umGrid.setVisible(false);
        } else if (componentId.equals("configmenu" + selectedTab)) {
            HtmlPanelGrid serverGrid = (HtmlPanelGrid) paneltab.
                    findComponent("serverGrid" + selectedTab);
            HtmlPanelGrid configGrid = (HtmlPanelGrid) paneltab.
                    findComponent("configGrid" + selectedTab);
            HtmlPanelGrid umGrid = (HtmlPanelGrid) paneltab.
                    findComponent("umGrid" + selectedTab);
            serverGrid.setVisible(false);
            configGrid.setVisible(true);
            umGrid.setVisible(false);
        } else if (componentId.equals("ummenu" + selectedTab)) {
            HtmlPanelGrid serverGrid = (HtmlPanelGrid) paneltab.
                    findComponent("serverGrid" + selectedTab);
            HtmlPanelGrid configGrid = (HtmlPanelGrid) paneltab.
                    findComponent("configGrid" + selectedTab);
            HtmlPanelGrid umGrid = (HtmlPanelGrid) paneltab.
                    findComponent("umGrid" + selectedTab);
            serverGrid.setVisible(false);
            configGrid.setVisible(false);
            umGrid.setVisible(true);
        }
        FacesContext.getCurrentInstance().renderResponse();
    }
}
