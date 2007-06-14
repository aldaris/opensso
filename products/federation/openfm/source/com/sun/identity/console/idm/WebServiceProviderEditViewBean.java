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
 * $Id: WebServiceProviderEditViewBean.java,v 1.2 2007-06-14 21:02:50 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.idm;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.Option;
import com.iplanet.jato.view.html.OptionList;
import com.sun.identity.console.base.AMPropertySheet;
import com.sun.identity.console.base.model.AMAdminConstants;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import com.sun.identity.console.components.view.html.SerializedField;
import com.sun.identity.console.idm.model.EntitiesModel;
import com.sun.identity.wss.security.SecurityMechanism;
import com.sun.web.ui.model.CCActionTableModel;
import com.sun.web.ui.model.CCPropertySheetModel;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.web.ui.view.html.CCSelectableList;
import com.sun.web.ui.view.table.CCActionTable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class WebServiceProviderEditViewBean 
    extends WebServiceEditViewBean {
    private static final String EDIT_LINK_TRACKER = "WebServiceEditTracker";
    private static final String PAGE_NAME = "WebServiceProviderEdit";
    private static final String TBL_USER_CRED = "tblUserCredential";

    // table
    private static final String TBL_BUTTON_ADD = "tblButtonAdd";
    private static final String TBL_BUTTON_DELETE = "tblButtonDelete";
    private static final String TBL_COL_NAME = "tblColName";
    private static final String TBL_COL_PWD = "tblColPassword";
    private static final String TBL_DATA_ACTION_HREF = "tblDataActionHref";
    private static final String TBL_DATA_NAME = "tblDataName";
    private static final String TBL_DATA_PWD = "tblDataPassword";

    private static final String ATTR_NAME_AUTH_CHAIN = "authenticationChain";
    private static final String CHILD_NAME_AUTH_CHAIN = "authenticationchain";
    
    public static final String DEFAULT_DISPLAY_URL =
        "/console/idm/WebServiceProviderEdit.jsp";

    private Set providerUIProperties = parseExternalizeUIProperties(
        "webServiceProviderUI");
    private CCActionTableModel tblUserCredential;
    
    public WebServiceProviderEditViewBean() {
        super(PAGE_NAME, DEFAULT_DISPLAY_URL, false,
            "com/sun/identity/console/propertyWebServiceProviderEdit.xml");
        createTableModel();
    }
    
    protected void registerChildren() {
        super.registerChildren();
        registerChild(TBL_USER_CRED, CCActionTable.class);
        tblUserCredential.registerChildren(this);
    }

    protected View createChild(String name) {
        View view = null;
        if (tblUserCredential.isChildSupported(name)) {
            view = tblUserCredential.createChild(this, name);
        } else if (name.equals(TBL_USER_CRED)) {
            SerializedField szCache = (SerializedField)getChild(SZ_CACHE);
            populateTableModel((List)szCache.getSerializedObj());
            view = new CCActionTable(this, tblUserCredential, name);
        } else {
            view = super.createChild(name);
        }
        return view;
    }

    protected void setExtendedDefaultValues(Map attrValues)
        throws AMConsoleException {
        Set values = (Set)attrValues.get(
            EntitiesModel.ATTR_NAME_DEVICE_KEY_VALUE);
        populateTableModel(getUserCredentials(values));

        String authChains = getAttributeFromSet(values, ATTR_NAME_AUTH_CHAIN);
        if (authChains == null) {
            authChains = "";
        }
        CCSelectableList cb = (CCSelectableList)getChild(CHILD_NAME_AUTH_CHAIN);
        cb.setOptions(getAuthChainOptionList());
        propertySheetModel.setValue(CHILD_NAME_AUTH_CHAIN, authChains);

        setExternalizeUIValues(providerUIProperties, values);

        values.add(EntitiesViewBean.ATTR_NAME_AGENT_TYPE + "WSP");

        setPageSessionAttribute(EDIT_LINK_TRACKER, (Serializable)attrValues);
    }

    private OptionList getAuthChainOptionList()
        throws AMConsoleException {
        String curRealm = (String)getPageSessionAttribute(
            AMAdminConstants.CURRENT_REALM);
        Set config = ((EntitiesModel)getModel()).getAuthenticationChains(
            curRealm);
        OptionList optList = new OptionList();
        optList.add("web.services.profile.authenticationchain-none", "");
        if ((config != null) && !config.isEmpty()) {
            for (Iterator iter = config.iterator(); iter.hasNext(); ) {
                String c = (String)iter.next();
                optList.add(c, c);
            }
        }
        return optList;
    }
    
    private void populateTableModel(List list) {
        Map map = new HashMap();
        
        if (list != null) {
            for (Iterator i = list.iterator(); i.hasNext(); ) {
                String uc = (String)i.next();
                String[] userpwd = splitUserCredToken(uc);
                if (userpwd != null) {
                    map.put(userpwd[0], userpwd[1]);
                }
            }
        }
        populateTableModel(map);
    }
    
    private void populateTableModel(Map nameToPassword) {
        tblUserCredential.clearAll();
        SerializedField szCache = (SerializedField)getChild(SZ_CACHE);
                                                                                
        if ((nameToPassword != null) && !nameToPassword.isEmpty()) {
            boolean firstEntry = true;
            int counter = 0;
            List cache = new ArrayList(nameToPassword.size()); 
            
            for (Iterator i = nameToPassword.keySet().iterator(); i.hasNext();
                counter++
            ) {
                if (firstEntry) {
                    firstEntry = false;
                } else {
                    tblUserCredential.appendRow();
                }
                
                String name = (String)i.next();
                String token = WebServiceEditViewBean.formUserCredToken(
                    name, (String)nameToPassword.get(name));
                tblUserCredential.setSelectionVisible(counter, true);
                tblUserCredential.setValue(TBL_DATA_ACTION_HREF, token);
                tblUserCredential.setValue(TBL_DATA_NAME, name);

                // mask password
                tblUserCredential.setValue(TBL_DATA_PWD, "********");
                cache.add(token);
            }
            szCache.setValue((ArrayList)cache);
        } else {
            szCache.setValue(null);
        }
    }

    protected void getExtendedFormsValues(Set deviceKeyValue)
        throws AMConsoleException {

        String authChain = (String)propertySheetModel.getValue(
            CHILD_NAME_AUTH_CHAIN);
        if ((authChain != null) && (authChain.length() > 0)) {
            deviceKeyValue.add(ATTR_NAME_AUTH_CHAIN + "=" + authChain);
        }

        getExternalizeUIValues(providerUIProperties, deviceKeyValue);
        deviceKeyValue.add(EntitiesViewBean.ATTR_NAME_AGENT_TYPE + "WSP");

        try {
            CCActionTable table = (CCActionTable)getChild(TBL_USER_CRED);
            table.restoreStateData();
            
            SerializedField szCache = (SerializedField)getChild(SZ_CACHE);
            List list = (List)szCache.getSerializedObj();
            
            if ((list != null) && !list.isEmpty()) {
                StringBuffer buff = new StringBuffer();
                boolean first = true;
                for (int i = 0; i < list.size(); i++) {
                    if (!first) {
                        buff.append(",");
                    } else {
                        first = false;
                    }
                    buff.append((String)list.get(i));
                }
                deviceKeyValue.add(ATTR_NAME_USERCREDENTIAL + "=" +
                    buff.toString());
            }
        } catch (ModelControlException ex) {
            throw new AMConsoleException(ex.getMessage());
        }
    }
    
    private void createTableModel() {
        tblUserCredential = new CCActionTableModel(
            getClass().getClassLoader().getResourceAsStream(
            "com/sun/identity/console/tblWebServiceUserCred.xml"));
        tblUserCredential.setTitleLabel("label.items");
        tblUserCredential.setActionValue(TBL_BUTTON_ADD, 
            "web.services.profile.username-token-tbl-add-btn");
        tblUserCredential.setActionValue(TBL_BUTTON_DELETE, 
            "web.services.profile.username-token-tbl-remove-btn");
        tblUserCredential.setActionValue(TBL_COL_NAME, 
            "web.services.profile.username-token-tbl-col-name");
        tblUserCredential.setActionValue(TBL_COL_PWD,
            "web.services.profile.username-token-tbl-password-name");
    }

    /**
     * Forwards request to add user credential view bean.
     *
     * @param event Request Invocation Event.
     */
    public void handleTblButtonAddRequest(RequestInvocationEvent event) 
        throws ModelControlException
    {
        try {
            setPageSessionAttribute(TRACKER_ATTR, (Serializable)getFormValues()
                );
            WebServiceUserCredAddViewBean vb = (WebServiceUserCredAddViewBean)
                getViewBean(WebServiceUserCredAddViewBean.class);
            unlockPageTrail();
            passPgSessionMap(vb);
            vb.forwardTo(getRequestContext());
        } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                e.getMessage());
            forwardTo();
        }
    }

    /**
     * Handles edit user token credential request.
     *
     * @param event Request Invocation Event.
     * @throws ModelControlException if table model cannot be restored.
     */
    public void handleTblDataActionHrefRequest(RequestInvocationEvent event)
        throws ModelControlException
    {
        Map attrValues = (Map)removePageSessionAttribute(EDIT_LINK_TRACKER);
        setPageSessionAttribute(TRACKER_ATTR, (Serializable)attrValues);
        WebServiceUserCredEditViewBean vb = (WebServiceUserCredEditViewBean)
            getViewBean(WebServiceUserCredEditViewBean.class);
        unlockPageTrail();
        passPgSessionMap(vb);
        String token = (String)getDisplayFieldValue(TBL_DATA_ACTION_HREF);
        vb.setDisplayFieldValue(WebServiceUserCredEditViewBean.HIDDEN_TOKEN,
            token);
        String[] userpwd = splitUserCredToken(token);
        vb.setDisplayFieldValue("username", userpwd[0]);
        vb.setDisplayFieldValue("password", userpwd[1]);
        vb.forwardTo(getRequestContext());
    }


    /**
     * Deletes user token credential.
     *
     * @param event Request Invocation Event.
     * @throws ModelControlException if table model cannot be restored.
     */
    public void handleTblButtonDeleteRequest(RequestInvocationEvent event)
        throws ModelControlException
    {
        submitCycle = true;
        CCActionTable table = (CCActionTable)getChild(TBL_USER_CRED);
        table.restoreStateData();
        tblUserCredential = (CCActionTableModel)table.getModel();
        Integer[] selected = tblUserCredential.getSelectedRows();
        SerializedField szCache = (SerializedField)getChild(SZ_CACHE);
        List list = (List)szCache.getSerializedObj();
        Set tokens = new HashSet(selected.length *2);
        
        for (int i = 0; i < selected.length; i++) {
            String sel = (String)list.get(selected[i].intValue());
            String[] userpwd = splitUserCredToken(sel);
            tokens.add(userpwd[0]);
        }

        try {
            Map map = getFormValues();
            Set values = (Set)map.get(
                EntitiesModel.ATTR_NAME_DEVICE_KEY_VALUE);
            WebServiceEditViewBean.removeUserCredTokenAttr(tokens, values);
            populateTableModel(getUserCredentials(values));
            setPageSessionAttribute(TRACKER_ATTR, (Serializable)map);
            setInlineAlertMessage(CCAlert.TYPE_INFO, "message.information",
                "web.services.profile.click-to-save");
        } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                e.getMessage());
        }
        forwardTo();
    }

    protected List getMessageLevelSecurityMech() {
        return SecurityMechanism.getAllWSPSecurityMechanisms();
    }
} 
