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
 * "Portions Copyrighted [year] [name of copyright owner]
 *
 * $Id: WSFedGeneralBase.java,v 1.1 2007-07-26 22:10:15 babysunil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.view.View;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.identity.console.base.AMPropertySheet;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import com.sun.identity.console.base.AMViewBeanBase;
import com.sun.identity.console.base.AMViewConfig;
import com.sun.identity.console.federation.model.WSFedPropertiesModelImpl;
import com.sun.identity.console.base.AMPrimaryMastHeadViewBean;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMSystemConfig;
import com.sun.identity.console.base.model.AMFormatUtils;
import com.sun.identity.console.base.model.AMAdminConstants;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.tabs.CCTabs;
import java.text.MessageFormat;
import javax.servlet.http.HttpServletRequest;

public abstract class WSFedGeneralBase
        extends AMPrimaryMastHeadViewBean {
    
    //PROFILE_TAB is the name of the tabset defined in amConsoleConfig.xml
    private static final String PROFILE_TAB = "entities";
    private static final String PAGE_TITLE = "pgtitle";
    private static final String PROPERTY_ATTRIBUTES = "propertyAttributes";
    protected CCPageTitleModel ptModel = null;
    protected AMPropertySheetModel psModel = null;
    private boolean initialized = false;
    
    public WSFedGeneralBase(String name) {
        super(name);
    }
    
    protected void registerChildren() {
        registerChild(PROPERTY_ATTRIBUTES, AMPropertySheet.class);
        psModel.registerChildren(this);
        ptModel.registerChildren(this);
        registerChild(TAB_COMMON, CCTabs.class);
        super.registerChildren();
    }
    
    protected View createChild(String name) {
        View view = null;
        
        if (name.equals(PAGE_TITLE)) {
            view = new CCPageTitle(this, ptModel, name);
        } else if (name.equals(PROPERTY_ATTRIBUTES)) {
            view = new AMPropertySheet(this, psModel, name);
        } else if (psModel.isChildSupported(name)) {
            view = psModel.createChild(this, name);
        } else if (ptModel.isChildSupported(name)) {
            view = ptModel.createChild(this, name);
        } else {
            view = super.createChild(name);
        }
        
        return view;
    }
    
    protected void initialize() {
        if (!initialized) {
            
            // get the type of entity selected, and name
            String name = (String)getPageSessionAttribute("entityName");
            if (name != null) {
                super.initialize();
                initialized = true;
                createPageTitleModel();
                createPropertyModel(name);
                registerChildren();
            }
        }
    }
    
    protected void setPageTitle(String title) {
        ptModel.setPageTitleText(title);
    }
    
    protected void createPageTitleModel() {
        ptModel = new CCPageTitleModel(
                getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/threeBtnsPageTitle.xml"));
        ptModel.setValue("button1", "button.save");
        ptModel.setValue("button2", "button.reset");
        ptModel.setValue("button3", "button.back");
    }
    
    protected void createTabModel() {
        if (tabModel == null) {
            AMViewConfig amconfig = AMViewConfig.getInstance();
            tabModel = amconfig.getTabsModel(PROFILE_TAB, "/",
                    getRequestContext().getRequest());
        }
    }
    
    protected AMModel getModelInternal() {
        
        //for WSFed
        HttpServletRequest req = getRequestContext().getRequest();
        return new WSFedPropertiesModelImpl(req, getPageSessionAttributes());
    }
    
    protected abstract void createPropertyModel(String name);
    
    /************************************************************************
     *
     * Event Handlers for the following events:
     * tab selection, save button, reset button, back button.
     *
     ************************************************************************/
    
    public void nodeClicked(RequestInvocationEvent event, int nodeID) {
        try {
            // get the entity tab that was selected and forward the
            // request to that vb
            AMViewBeanBase vb = getTabNodeAssociatedViewBean(
                    PROFILE_TAB, nodeID);
            passPgSessionMap(vb);
            vb.forwardTo(getRequestContext());
        } catch (AMConsoleException e) {
            forwardTo();
        }
    }
    
    public void handleButton1Request(RequestInvocationEvent event)
    throws ModelControlException {
       forwardTo();
    }
    
    public void handleButton2Request(RequestInvocationEvent event) {
        forwardTo();
    }
    
    public void handleButton3Request(RequestInvocationEvent event) {
        FederationViewBean vb = (FederationViewBean)
        getViewBean(FederationViewBean.class);
        passPgSessionMap(vb);
        vb.forwardTo(getRequestContext());
    }
    
}
