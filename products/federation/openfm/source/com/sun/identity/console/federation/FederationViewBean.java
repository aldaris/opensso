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
 * $Id: FederationViewBean.java,v 1.1 2007-04-30 18:48:31 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.OptionList;

import com.sun.identity.console.base.AMPropertySheet;
import com.sun.identity.console.base.AMPrimaryMastHeadViewBean;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import com.sun.identity.shared.datastruct.OrderedSet;

import com.sun.web.ui.model.CCActionTableModel;
import com.sun.web.ui.view.alert.CCAlert;

import javax.servlet.http.HttpServletRequest;

public  class FederationViewBean
    extends AMPrimaryMastHeadViewBean
{
    public static final String DEFAULT_DISPLAY_URL = 
        "/console/federation/Federation.jsp";

    public static final String MESSAGE_TEXT = "displayMessage";
    
    protected static final String PROPERTY_ATTRIBUTE = "propertyAttributes";
    private boolean tablePopulated = false;
    private boolean initialized = false;

    private AMPropertySheetModel propertySheetModel;
    
    // cot table properties
    private static final String COT_TABLE = "cotTable";
    private static final String COT_NAME_VALUE = "cotNameValue";
    private static final String COT_NAME_HREF = "cotNameHref";
    private static final String COT_ENTITY_VALUE = "cotEntityValue";

    // entity table properties
    private static final String ENTITY_TABLE = "entityTable";
    private static final String ENTITY_NAME_VALUE =  "entityNameValue";
    private static final String ENTITY_NAME_HREF =  "entityNameHref";
    private static final String ENTITY_PROTOCOL_VALUE =  "protocolValue";
    private static final String ENTITY_ROLE_VALUE =  "roleValue";
    private static final String ENTITY_LOCATION_VALUE =  "locationValue";
    
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
        registerChild(PROPERTY_ATTRIBUTE, AMPropertySheet.class);
        propertySheetModel.registerChildren(this);
        super.registerChildren();
    }

    protected View createChild(String name) {
        if (!tablePopulated) {
            populateCOTTable();
            populateEntityTable();                     
            populateSAMLTable();
        }
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
        RequestContext rc = RequestManager.getRequestContext();
        HttpServletRequest req = rc.getRequest();
        return new AMModelBase(req, getPageSessionAttributes());
    }
    
    private void populateSAMLTable() {
        //TBD;
    }
   
    private void populateEntityTable() {
        // TDB
    }

    private void populateCOTTable() {   
        // TBD
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
        CCActionTableModel tableModel =new CCActionTableModel(
            getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/cotTable.xml"));
        
        tableModel.setMaxRows(getModel().getPageSize());
        tableModel.setTitleLabel("label.items");
        tableModel.setActionValue("addCOTButton", "cot.new.button");        
        tableModel.setActionValue("deleteCOTButton", "cot.delete.button");
        tableModel.setActionValue("cotNameColumn", "cot.name.column.label");
        tableModel.setActionValue("cotEntityColumn", "cot.entity.column.label");
        tableModel.setActionValue("realmColumn", "cot.realm.column.label");
        tableModel.setActionValue("statusColumn", "cot.status.column.label");

        propertySheetModel.setModel(COT_TABLE, tableModel);
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
        // TBD forward the request to the view to create a COT.
        CreateCOTViewBean vb = 
            (CreateCOTViewBean)getViewBean(CreateCOTViewBean.class);
        passPgSessionMap(vb);
        vb.forwardTo(getRequestContext());
        forwardTo();
    }

    public void handleDeleteCOTButtonRequest(RequestInvocationEvent event) {
        // TBD delete the COT's that are selected.
        forwardTo();
    }
    
    public void handleCotNameHrefRequest(RequestInvocationEvent event) {
        // TBD open the view to modify the COT properties
        forwardTo();
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
