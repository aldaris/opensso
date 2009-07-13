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
import com.icesoft.faces.component.menubar.MenuItem;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

/**
 * This class is an event listener. It listens to the event change triggered by
 * HtmlSelectMenu UI COmponent in server.jsp which are bound to this class
 */
public class ServerEventListener implements ValueChangeListener {

/**
 * This method gets implemented whenever there is a change of value in a
 * UIComponent bound to this listener
 * @param event valueChangeEvent triggered by bound UI COmponent
 */
    public void processValueChange(ValueChangeEvent event)
            throws AbortProcessingException {
        PhaseId phaseId = event.getPhaseId();
        if (phaseId.equals(PhaseId.ANY_PHASE)) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
        } else if (phaseId.equals(PhaseId.INVOKE_APPLICATION)) {
            PanelTabSet tabset = new PanelTabSet();
            tabset = (PanelTabSet) event.getComponent().getParent().
                    getParent().getParent();                       
            int selectedTab = tabset.getSelectedIndex();                     
            if (event.getNewValue().equals("embedded") &&
                    event.getNewValue() != null) {
                HtmlPanelGrid hm = (HtmlPanelGrid) tabset.findComponent("server"
                        + selectedTab).
                        findComponent("menuGrid" + selectedTab);
                MenuItem mi = (MenuItem)hm.findComponent("menuBar" + 
                        selectedTab).findComponent("ummenu" + selectedTab );
                mi.setDisabled(true);
            }
            if (event.getNewValue().equals("dirServer") && 
                    event.getNewValue() != null) {          
                HtmlPanelGrid hm = (HtmlPanelGrid) tabset.findComponent("server"
                        + selectedTab).
                        findComponent("menuGrid" + selectedTab);
                MenuItem mi = (MenuItem)hm.findComponent("menuBar" +
                        selectedTab).findComponent("ummenu" +selectedTab );
                mi.setDisabled(false);
            }
            FacesContext.getCurrentInstance().renderResponse();
        }
    }
}
