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
 * $Id: FSAuthDomainsViewBean.java,v 1.1 2007-03-14 19:33:21 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.components.view.html.SerializedField;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.table.CCActionTable;
import com.sun.web.ui.model.CCActionTableModel;
import com.sun.web.ui.model.CCPageTitleModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;


public class FSAuthDomainsViewBean
    extends FSViewBeanBase
{
    public static final String DEFAULT_DISPLAY_URL =
	"/console/federation/FSAuthDomains.jsp";

    // table properties
    private static final String TBL_SEARCH = "tblSearch";
    private static final String TBL_BUTTON_ADD = "tblButtonAdd";
    private static final String TBL_BUTTON_DELETE = "tblButtonDelete";
    private static final String TBL_COL_NAME = "tblColName";
    private static final String TBL_DATA_NAME = "tblDataName";
    private static final String TBL_DATA_ACTION_HREF = "tblDataActionHref";
    private CCActionTableModel tblModel = null;


    /**
     * Creates a authentication domains view bean.
     */
    public FSAuthDomainsViewBean() {
	super("FSAuthDomains");
	setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
    }

    protected void initialize() {
	if (!initialized) {
	    super.initialize();
            createTableModel();
            registerChildren();
	    initialized = true;
	}
    }

    protected void registerChildren() {
	super.registerChildren();
        registerChild(TBL_SEARCH, CCActionTable.class);
        tblModel.registerChildren(this);
    }

    protected View createChild(String name) {
        View view = null;
        if (name.equals(TBL_SEARCH)) {
            SerializedField szCache = (SerializedField)getChild(SZ_CACHE);
            populateTable((List)szCache.getSerializedObj());
            view = new CCActionTable(this, tblModel, name);
        } else if (tblModel.isChildSupported(name)) {
            view = tblModel.createChild(this, name);
        } else { 
            view = super.createChild(name);
        }

        return view;
    }

    public void beginDisplay(DisplayEvent event)
	throws ModelControlException
    {
	super.beginDisplay(event);
	populateTable();
    }
 
    protected AMModel getModelInternal() {
        HttpServletRequest req =
            RequestManager.getRequestContext().getRequest();
        return new AMModelBase(req, getPageSessionAttributes());
    }

    private void createTableModel() {
        tblModel = new CCActionTableModel(
            getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/tblFSAuthDomains.xml"));
        tblModel.setMaxRows(getModel().getPageSize());
        tblModel.setTitleLabel("label.items");
        tblModel.setActionValue(TBL_BUTTON_ADD, "table.authDomains.button.new");        tblModel.setActionValue(TBL_BUTTON_DELETE, "button.delete");
        tblModel.setActionValue(TBL_COL_NAME,
            "table.authDomains.name.column.name");
    }

    private void populateTable() { 
/*
        FSAuthDomainsModel model = (FSAuthDomainsModel)getModel();
        String filter = ((String)getDisplayFieldValue(TF_FILTER)); 
 
        if ((filter == null) || (filter.length() == 0)) { 
            filter = "*"; 
            setDisplayFieldValue(TF_FILTER, "*"); 
        } else { 
            filter = filter.trim(); 
        }
 
        populateTable(model.getAuthenticationDomains(filter)); 
*/
    } 

    private void populateTable(Collection authDomains) {
/*
        tblModel.clearAll();
        SerializedField szCache = (SerializedField)getChild(SZ_CACHE);
 
        FSAuthDomainsModel model = (FSAuthDomainsModel)getModel();
 
        if ((authDomains != null) && !authDomains.isEmpty()) {
            List cache = new ArrayList(authDomains.size());
            boolean first = true;
 
            for (Iterator iter = authDomains.iterator(); iter.hasNext(); ) {
                if (first) {
                    first = false;
                } else {
                    tblModel.appendRow();
                }
// get the providers for each domain here
 
                String name = (String)iter.next();
                tblModel.setValue(TBL_DATA_NAME, name);
                tblModel.setValue(TBL_DATA_ACTION_HREF, name);
                cache.add(name);
            }
 
            szCache.setValue((ArrayList)cache);
        } else {
            szCache.setValue(null);
        }
*/
    }

    protected String getBreadCrumbDisplayName() {
        return "breadcrumbs.federation.authdomains";
    }

}
