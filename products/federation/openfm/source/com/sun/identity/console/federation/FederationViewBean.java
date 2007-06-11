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
 * $Id: FederationViewBean.java,v 1.2 2007-06-11 22:00:59 asyhuang Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.OptionList;
import com.sun.web.ui.model.CCActionTableModel;
import com.sun.web.ui.view.table.CCActionTable;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.identity.console.base.AMPropertySheet;
import com.sun.identity.console.base.AMPrimaryMastHeadViewBean;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import com.sun.identity.console.components.view.html.SerializedField;
import com.sun.identity.console.federation.model.FSAuthDomainsModel;
import com.sun.identity.console.federation.model.FSAuthDomainsModelImpl;
import com.sun.identity.cot.CircleOfTrustDescriptor;
import com.sun.identity.shared.datastruct.OrderedSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public  class FederationViewBean
        extends AMPrimaryMastHeadViewBean 
{
    public static final String DEFAULT_DISPLAY_URL =
        "/console/federation/Federation.jsp";
    public static final String MESSAGE_TEXT = "displayMessage";    
    protected static final String PROPERTY_ATTRIBUTE = "propertyAttributes";    
    private boolean initialized = false;    
    private AMPropertySheetModel propertySheetModel;
    
    // cot table properties
    private static final String COT_TABLE = "cotTable";
    private static final String COT_NAME_VALUE = "cotNameValue";
    private static final String COT_NAME_HREF = "cotNameHref";
    private static final String COT_ENTITY_VALUE = "cotEntityValue";
    private static final String COT_REALM_VALUE = "realmValue";
    private static final String COT_STATUS_VALUE = "statusValue";
        
    // entity table properties
    private static final String ENTITY_TABLE = "entityTable";
    private static final String ENTITY_NAME_VALUE = "entityNameValue";
    private static final String ENTITY_NAME_HREF = "entityNameHref";
    private static final String ENTITY_PROTOCOL_VALUE = "protocolValue";
    private static final String ENTITY_ROLE_VALUE = "roleValue";
    private static final String ENTITY_LOCATION_VALUE = "locationValue";
    
    // SAML Configuration table
    private static final String SAML_TABLE = "samlTable";
    
    /**
     * Creates a authentication domains view bean.
     */
    public FederationViewBean() {
        super("Federation");
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
    }
    
    protected void initialize()  {
        if (!initialized) {
            initialized = true;
            super.initialize();
            createPropertyModel();
            createCOTTable();
            createEntityTable();
            createSAMLTable();
            registerChildren();
        }
    }
    
    protected void registerChildren() {
        super.registerChildren();
        registerChild(PROPERTY_ATTRIBUTE, AMPropertySheet.class);
        propertySheetModel.registerChildren(this);     
    }
    
    protected View createChild(String name) {        
        View view = null;
        
        if (name.equals(PROPERTY_ATTRIBUTE)) {
            view = new AMPropertySheet(this, propertySheetModel, name);
        } else if (propertySheetModel.isChildSupported(name)) {
            view = propertySheetModel.createChild(this, name, getModel());     
        } else {
            view = super.createChild(name);
        }
        return view;
    }
    
    public void beginDisplay(DisplayEvent event)
        throws ModelControlException 
    {
        super.beginDisplay(event);
        resetButtonState("deleteCOTButton");
        resetButtonState("deleteEntityButton");
        resetButtonState("deleteTPButton");
        
        AMPropertySheet ps =
            (AMPropertySheet)getChild(PROPERTY_ATTRIBUTE);
        
        populateCOTTable();
        populateEntityTable();
        populateSAMLTable();
        
        String msg = (String)removePageSessionAttribute(MESSAGE_TEXT);
        if (msg != null) {
            setInlineAlertMessage(
                CCAlert.TYPE_INFO, "message.information", msg);
        }
    }
    
    protected AMModel getModelInternal() {
        HttpServletRequest req = getRequestContext().getRequest();
        return new FSAuthDomainsModelImpl(req, getPageSessionAttributes());
    }
    
    private void populateSAMLTable() {
        //TBD;
    }
    
    private void populateEntityTable() {
        // TDB
    }
    
    private void populateCOTTable() {
        FSAuthDomainsModel model = (FSAuthDomainsModel)getModel();    
        Set CircleOfTrustDescriptors = model.getCircleOfTrustDescriptors();                
                
        CCActionTableModel tblModel = (CCActionTableModel)
            propertySheetModel.getModel(COT_TABLE);        
        tblModel.clearAll();
        
        SerializedField szCache = (SerializedField)getChild(SZ_CACHE);        
        
        if ((CircleOfTrustDescriptors != null)
            && (!CircleOfTrustDescriptors.isEmpty())) 
        {
            List cache = new ArrayList(CircleOfTrustDescriptors.size());
            boolean first = true;            
            for (Iterator iter = CircleOfTrustDescriptors.iterator();
            iter.hasNext(); ) {
                if (first) {
                    first = false;
                } else {
                    tblModel.appendRow();
                }                
                CircleOfTrustDescriptor desc =
                        (CircleOfTrustDescriptor)iter.next();
                String name = desc.getCircleOfTrustName();
                tblModel.setValue(COT_NAME_VALUE, name);
                tblModel.setValue(COT_NAME_HREF, name);
                
                // get entity/provider name                
                Set entitySet = desc.getTrustedProviders();
                if ((entitySet != null) && (!entitySet.isEmpty())) {
                    Iterator it = entitySet.iterator();
                    StringBuffer sb = new StringBuffer();                    
                    while(it.hasNext()) {
                        String entity = (String) it.next();
                        sb.append(entity).append("<br>");
                    }                    
                    tblModel.setValue(COT_ENTITY_VALUE, sb.toString());
                }else{
                    tblModel.setValue(COT_ENTITY_VALUE, "");
                }
                
                // get realm name
                String realm = desc.getCircleOfTrustRealm();
                tblModel.setValue(COT_REALM_VALUE, realm);
                               
                // get cot status
                String status = desc.getCircleOfTrustStatus();
                tblModel.setValue(COT_STATUS_VALUE, status);                
                StringBuffer sb = new StringBuffer();
                sb.append(name).append("|").append(realm);
                cache.add(sb);                
            }            
            szCache.setValue((ArrayList)cache);
        } else {
            szCache.setValue(null);
        }      
    }
    
    private void createPropertyModel() {
        propertySheetModel = new AMPropertySheetModel(
            getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/propertyFederationView.xml"));       
        propertySheetModel.clear();
    }
    
    /*
     * Responsible for creating the circle of trust table.
     */
    private void createCOTTable() {
        CCActionTableModel tblModel = new CCActionTableModel(
            getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/cotTable.xml"));        
        tblModel.setMaxRows(getModel().getPageSize());
        tblModel.setTitleLabel("label.items");
        tblModel.setActionValue("addCOTButton", "cot.new.button");
        tblModel.setActionValue("deleteCOTButton", "cot.delete.button");
        tblModel.setActionValue("cotNameColumn", "cot.name.column.label");
        tblModel.setActionValue("cotEntityColumn", "cot.entity.column.label");
        tblModel.setActionValue("realmColumn", "cot.realm.column.label");
        tblModel.setActionValue("statusColumn", "cot.status.column.label");        
        propertySheetModel.setModel(COT_TABLE, tblModel);
    }
    
    /*
     * Responsible for creating the entity table.
     */
    private void createEntityTable() {
        CCActionTableModel tableModel = new CCActionTableModel(
            getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/entityTable.xml"));        
        tableModel.setMaxRows(getModel().getPageSize());
        tableModel.setTitleLabel("label.items");
        tableModel.setActionValue(
            "addEntityButton", "entity.new.button");
        tableModel.setActionValue(
            "deleteEntityButton", "entity.delete.button");
        tableModel.setActionValue(
            "importEntityButton", "entity.import.button");
        tableModel.setActionValue(
            "entityNameColumn", "entity.name.column.label");
        tableModel.setActionValue(
            "roleColumn", "entity.role.column.label");
        tableModel.setActionValue(
            "protocolColumn", "entity.protocol.column.label");
        tableModel.setActionValue(
            "locationColumn", "entity.location.column.label");       
        propertySheetModel.setModel(ENTITY_TABLE, tableModel);
    }
    
    /*
     * Responsible for creating the entity table.
     */
    private void createSAMLTable() {
        CCActionTableModel tableModel = new CCActionTableModel(
            getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/samlTable.xml"));        
        tableModel.setMaxRows(getModel().getPageSize());
        tableModel.setTitleLabel("label.items");
        tableModel.setActionValue("addTPButton", "saml.new.button");
        tableModel.setActionValue("deleteTPButton", "saml.delete.button");
        tableModel.setActionValue("trustedPartnerColumn", "saml.name.column.label");        
        propertySheetModel.setModel(SAML_TABLE, tableModel);
    }
    
    /*****************************************************************
     *
     * SAML Event Handlers.
     * There are four events which may be generated from the SAML section.
     * For the trusted partners there are new, delete, and edit requests.
     * There is also a handler for editing the local properties.
     *
     *****************************************************************/
    
    /**
     * Handles the new trusted partner request. There is no real processing
     * done here. We are just forwarding the request onto the view bean
     * which will do the real work.
     *
     * @param event Request Invocation Event.
     */
    public void handleAddTPButtonRequest(RequestInvocationEvent event) {
        forwardTo();
    }
    
    /**
     * Handles the delete trusted partner request. The items which are
     * selected in the SAML table will be removed. After the processing is
     * complete a message will be displayed indicating the process succeeded,
     * or what failed if it didn't succeed.
     *
     * @param event Request Invocation Event.
     */
    public void handleDeleteTPButtonRequest(RequestInvocationEvent event) {
        // TBD delete  the trusted partners selected in the SAML TABLE
        forwardTo();
    }
    
    public void handleLocalSitePropertiesRequest(RequestInvocationEvent event) {
        forwardTo();
    }
    
    /*****************************************************************
     *
     * Circle of Trust Event Handlers
     *
     *****************************************************************/  
    public void handleAddCOTButtonRequest(RequestInvocationEvent event) {        
        CreateCOTViewBean vb =
            (CreateCOTViewBean)getViewBean(CreateCOTViewBean.class);        
        unlockPageTrail();
        passPgSessionMap(vb);
        vb.forwardTo(getRequestContext());        
    }

    public void handleDeleteCOTButtonRequest(RequestInvocationEvent event)
        throws ModelControlException 
    {
        // TBD delete the COT's that are selected.        
        CCActionTable tbl = (CCActionTable)getChild(COT_TABLE);
        tbl.restoreStateData();   
        
        CCActionTableModel tblModel = (CCActionTableModel)
            propertySheetModel.getModel(COT_TABLE);        
        
        // get selected rows here
        Integer[] selected = tblModel.getSelectedRows();
        
        SerializedField szCache = (SerializedField)getChild(SZ_CACHE);
        List list = (List)szCache.getSerializedObj();        
        List selectedList = new ArrayList(selected.length *2);
        
        for (int i = 0; i < selected.length; i++) {
            selectedList.add(list.get(selected[i].intValue()));
        }
                
        try {
            FSAuthDomainsModel model = (FSAuthDomainsModel)getModel();                        
            for ( int i = 0; i < selectedList.size(); i++) {
                String str = (String) selectedList.get(i);
                int pipeIndex = str.indexOf("|");                               
                model.deleteAuthenticationDomain( 
                    str.substring(pipeIndex+1, str.length()),
                    str.substring(0, pipeIndex-1));  
            }
            if (selected.length == 1) {
                setInlineAlertMessage(CCAlert.TYPE_INFO, "message.information",
                    "authDomain.message.deleted");
            } else {
                setInlineAlertMessage(CCAlert.TYPE_INFO, "message.information",
                    "authDomain.message.deleted.pural");
            }
        } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                e.getMessage());
        }        
        forwardTo();        
    }
    
    public void handleCotNameHrefRequest(RequestInvocationEvent event)
        throws ModelControlException 
    {
        String name = (String)getDisplayFieldValue(COT_NAME_HREF);
        FSAuthDomainsEditViewBean vb = (FSAuthDomainsEditViewBean)
        getViewBean(FSAuthDomainsEditViewBean.class);
        unlockPageTrail();
        passPgSessionMap(vb);
        vb.setPageSessionAttribute(FSAuthDomainsModel.TF_NAME, name);
        vb.forwardTo(getRequestContext());
    }
    
    /*****************************************************************
     *
     * Entity Event handlers
     *
     ******************************************************************/    
    public void handleAddEntityButtonRequest(RequestInvocationEvent event) {
        // TBD open the create entity view page
        forwardTo();
    }
    
    public void handleDeleteEntityButtonRequest(RequestInvocationEvent event) {
        //TBD remove the selected entities
        forwardTo();
    }
    
    public void handleEntityNameHrefRequest(RequestInvocationEvent event) {
        // TBD forward request onto the appropriate view bean based on the
        // entity selected.
        forwardTo();
    }
    
    public void handleBtnSearchRequest(RequestInvocationEvent event) {
        // use the action value to determine which view we will forward to
        String actionValue = (String)getDisplayFieldValue("actionMenu");
        forwardTo();        
    }
    
    public void handleImportEntityButtonRequest(RequestInvocationEvent event) {
        // TBD open the view to import entities
        forwardTo();
    }
}
