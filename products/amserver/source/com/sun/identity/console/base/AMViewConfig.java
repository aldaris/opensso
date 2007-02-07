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
 * $Id: AMViewConfig.java,v 1.1 2007-02-07 20:19:36 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.base;

import com.iplanet.jato.view.html.OptionList;
import com.sun.identity.common.DisplayUtils;
import com.sun.identity.console.base.model.AMAdminConstants;
import com.sun.identity.console.base.model.AMAdminUtils;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMFormatUtils;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AccessControlModel;
import com.sun.identity.console.base.model.AccessControlModelImpl;
import com.sun.identity.console.idm.EntitiesViewBean;
import com.sun.web.ui.model.CCTabsModel;
import com.sun.web.ui.model.CCNavNode;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AMViewConfig {
    private static final AMViewConfig instance = new AMViewConfig();
    private static final String CONFIG_FILENAME = "amConsoleConfig.xml";

    private List tabs = new ArrayList();
    private Map profileTabs = new HashMap();
    private Map showMenus = new HashMap();
    private Map services = new HashMap();

    private static final String TABS = "tabs";
    private static final String TAB_ENTRY = "tab";
    private static final String PROFILE_TABS = "profiletabs";
    private static final String SERVICES_CONFIG = "servicesconfig";
    private static final String HIDDEN_SERVICES = "hidden";
    private static final String CONSOLE_SERVICE = "consoleservice";
    private static final String REALM_ENABLE_HIDE_ATTRS =
        "realmEnableHideAttrName";
    private static final String IDENTITY_SERVICE = "identityservice";

    private AMViewConfig() {
        Document doc = parseDocument(CONFIG_FILENAME);
        configTabs(doc);
        configMenus(doc);
        configProfileTabs(doc);
        configServices(doc);
    }

    public static AMViewConfig getInstance() {
        return instance;
    }

    public CCTabsModel getTabsModel(String realmName, HttpServletRequest req) {
        return getTabsModel(TABS, realmName, req);
    }

    public CCTabsModel getTabsModel(
        String type,
        String realmName,
        HttpServletRequest req
    ) {
        CCTabsModel tabModel = new CCTabsModel();
        AccessControlModel model = new AccessControlModelImpl(req);
        List tabs = getTabList(type);
        for (Iterator iter = tabs.iterator(); iter.hasNext(); ) {
            AMTabEntry entry = (AMTabEntry)iter.next();
            CCNavNode navNode = entry.getNavNode(model, realmName);
            if (navNode != null) {
                tabModel.addNode(navNode);
            }
        }
        return tabModel;
    }

    public boolean addEntityTabs(
        CCTabsModel tabModel,
        String realmName,
        AMModel model
    ) {
        boolean added = false;
        List supported = getSupportedEntityTypes(realmName, model);

        if (!supported.isEmpty()) {
            CCNavNode subjectNode = (CCNavNode)tabModel.getNodeById(
                AMAdminConstants.SUBJECTS_NODE_ID);

            if (subjectNode != null) {
                for (Iterator i = supported.iterator(); i.hasNext(); ) {
                    String t = (String)i.next();
                    subjectNode.addChild(new CCNavNode(t.hashCode(), t, t, t));
                }
                added = true;
            }
        }

        return added;
    }

    public List getSupportedEntityTypes(String realmName, AMModel model) {
        Map supported = getSupportedEntityTypesMap(realmName, model);
        List ordered = null;

        if ((supported != null) && !supported.isEmpty()) {
            ordered = new ArrayList(supported.size());
            Set basket = new HashSet();
            basket.addAll(supported.keySet());
            List predefinedOrder = getIdentityDisplayOrder();

            for (Iterator i = predefinedOrder.iterator(); i.hasNext(); ) {
                String wildcard = (String)i.next();
                List matched = matchIdentityType(basket, wildcard, model);
                if (!matched.isEmpty()) {
                    ordered.addAll(matched);
                }
            }

            /*
             * This handles identity types that are not pre-ordered.
             */
            if (!basket.isEmpty()) {
                ordered.addAll(
                    AMFormatUtils.sortItems(basket, model.getUserLocale()));
            }
        }

        return (ordered == null) ? Collections.EMPTY_LIST : ordered;
    }

    private List matchIdentityType(Set basket, String wildcard, AMModel model) {
        Set matched = new HashSet();
        for (Iterator i = basket.iterator(); i.hasNext(); ) {
            String type = (String)i.next();
            if (DisplayUtils.wildcardMatch(type, wildcard)) {
                matched.add(type);
                i.remove();
            }
        }
        return AMFormatUtils.sortItems(matched, model.getUserLocale());
    }


    public Map getSupportedEntityTypesMap(String realmName, AMModel model) {
        Map supported = null;
        AccessControlModel accessModel = new AccessControlModelImpl(
            model.getUserSSOToken());
        Set permission = new HashSet(2);
        permission.add(AMAdminConstants.IDREPO_SERVICE_NAME);

        if (accessModel.canView(permission, null, realmName, false)) {
            supported = model.getSupportedEntityTypes(realmName);
        }

        return (supported == null) ? Collections.EMPTY_MAP : supported;
    }

    /**
     * Adds a set of subtabs to the specified parent tab. The data for 
     * creating a subtab is a list of Map entries, each with the following
     * data.
     * <ul>
     * <li>label - name displayed for the tab</li>
     * <li>tooltip - tip information displayed when mouse over tab</li>
     * <li>status - text displayed in the status line of browser</li>
     * <li>url - url to invoke when tab is selected</li>
     * <li>viewbean - viewbean that is displayed</li>
     * </ul>
     *
     * @param parentID for the parent tab
     * @param List of subtabs to add to the parent
     */
    public void setTabViews(int parentID, List items) {
        AMTabEntry parent = null;
        try {
            parent = getTabEntry(parentID);
            parent.removeChildren();
        } catch (AMConsoleException a) {
            AMModelBase.debug.error("couldn't get the parent tab ");
            return;
        }    

        int id = 1;
        for (Iterator i = items.iterator(); i.hasNext(); ) {
            Map tab = (Map)i.next();
            String label = (String)tab.get("label");
            String tooltip = (String)tab.get("tooltip");
            String status = (String)tab.get("status");
            String url = (String)tab.get("url");
            String permissions = (String)tab.get("permissions");
            String viewbean = (String)tab.get("viewbean");

            AMTabEntry child = new AMTabEntry(
                id, label, tooltip, status, url,
                AMAdminUtils.getDelimitedValues(permissions, ","), viewbean);
            parent.addChild(child);

            id++;
        }
    }

    public String getDefaultViewBeanURL(
        String realmName,
        HttpServletRequest req
    ) {
        return getDefaultViewBeanURL(TABS, realmName, req);
    }

    public String getDefaultViewBeanURL(
        String type,
        String realmName,
        HttpServletRequest req
    ) {
        AccessControlModel model = new AccessControlModelImpl(req);
        List list = getTabList(type);
        String url = null;
        for (Iterator i = tabs.iterator(); i.hasNext() && (url == null); ) {
            AMTabEntry entry = (AMTabEntry)i.next();
            url = entry.getURL(model, realmName);
        }
        return url;
    }

    public int getDefaultTabId(String realmName, HttpServletRequest req) {
        return getDefaultTabId(TABS, realmName, req);
    }
                                                                                
    public int getDefaultTabId(
        String type,
        String realmName,
        HttpServletRequest req
    ) {
        AccessControlModel model = new AccessControlModelImpl(req);
        List list = getTabList(type);
        int id = -1;
        for (Iterator i = tabs.iterator(); i.hasNext() && (id == -1); ) {
            AMTabEntry entry = (AMTabEntry)i.next();
            id = entry.getID(model, realmName);
        }
        return id;
    }

    public AMViewBeanBase getTabViewBean(
        AMViewBeanBase vb,
        String realmName,
        AMModel model,
        int idx,
        int childIdx
    ) throws AMConsoleException {
        return getTabViewBean(vb, realmName, model, TABS, idx, childIdx);
    }

    public AMViewBeanBase getTabViewBean(
        AMViewBeanBase vb,
        String realmName,
        AMModel model,
        String type,
        int idx,
        int childIdx
    ) throws AMConsoleException {
        Class clazz = getTabViewBeanClass(
            vb, realmName, model, type, idx, childIdx);
        return (AMViewBeanBase)vb.getViewBean(clazz);
    }

    private Class getTabViewBeanClass(
        AMViewBeanBase vb,
        String realmName,
        AMModel model,
        String type,
        int idx,
        int childIdx
    ) throws AMConsoleException {
        List list = getTabList(type);
        Class clazz = null;
        AccessControlModel accessModel = new AccessControlModelImpl(
            model.getUserSSOToken());

        for (Iterator i = list.iterator(); i.hasNext() && (clazz == null); ) {
            AMTabEntry entry = (AMTabEntry)i.next();

            if (entry.canView(accessModel, realmName)) {
            if (idx == -1) {
                clazz = entry.getTabClass();
            } else {
                clazz = entry.getTabClass(idx);
            }

            if (clazz == null) {
                // may be entity type
                Map supported = model.getSupportedEntityTypes(realmName);

                for (Iterator it = supported.keySet().iterator();
                    it.hasNext() && (clazz == null);
                ) {
                    String t = (String)it.next();
                    if (idx == t.hashCode()) {
                        clazz =
                            com.sun.identity.console.idm.EntitiesViewBean.class;
                        vb.setPageSessionAttribute(
                            EntitiesViewBean.PG_SESSION_ENTITY_TYPE, t);
                    }
                }
            } else if (BlankTabViewBean.class.equals(clazz)) {
                switch (childIdx) {
                case -1:
                    clazz = null;
                    break;
                default:
                    clazz = getTabViewBeanClass(
                        vb, realmName, model, type, childIdx, -1);
                    break;
                }
            }
            }
        }

        if (clazz == null) {
            throw new AMConsoleException(
                "AMViewConfig.getTabClass: no action class for node ID, " +idx);
        }

        return clazz;
    }

    public AMTabEntry getTabEntry(int idx)
        throws AMConsoleException
    {
        return getTabEntry(TABS, idx);
    }

    private AMTabEntry getTabEntry(String type, int idx)
        throws AMConsoleException
    {
        List list = getTabList(type);
        AMTabEntry entry = null;

        for (Iterator iter = list.iterator(); 
             iter.hasNext() && (entry == null);        ) 
        {
            AMTabEntry e = (AMTabEntry)iter.next();
            entry = e.matchedID(idx);
        }

        if (entry == null) {
            throw new AMConsoleException(
                "AMViewConfig.getTabEntry: not found, id = " + idx);
        }

        return entry;
    }

    public OptionList getShowMenus(String name) {
        OptionList optList = new OptionList();
        List list = (List)showMenus.get(name);

        if ((list != null) && !list.isEmpty()) {
            for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                AMShowMenuEntry menu = (AMShowMenuEntry)iter.next();
                optList.add(menu.getLabel(), menu.getID());
            }
        }

        return optList;
    }

    public Class getShowMenuClass(String name, String idx)
        throws AMConsoleException {
        Class clazz = null;
        List list = (List)showMenus.get(name);

        if ((list != null) && !list.isEmpty()) {
            for (Iterator iter = list.iterator();
                iter.hasNext() && (clazz == null);
            ) {
                AMShowMenuEntry menu = (AMShowMenuEntry)iter.next();
                if (menu.getID().equals(idx)) {
                    try {
                        clazz = Class.forName(menu.getViewBean());
                    } catch (ClassNotFoundException e) {
                        throw new AMConsoleException(e.getMessage());
                    }
                }
            }
        }

        if (clazz == null) {
            throw new AMConsoleException(
            "AMViewConfig.getShowMenuClass: not action class for ID, " +idx);
        }

        return clazz;
    }

    public boolean isServiceVisible(String serviceName) {
        Set hidden = (Set)services.get(HIDDEN_SERVICES);
        return !hidden.contains(serviceName);
    }

    public Set getRealmEnableHiddenConsoleAttrNames() {
        return (Set)services.get(REALM_ENABLE_HIDE_ATTRS);
    }

    public List getIdentityDisplayOrder() {
        return (List)services.get(IDENTITY_SERVICE);
    }

    private Document parseDocument(String fileName) {
        Document document = null;
        InputStream is = getClass().getClassLoader().getResourceAsStream(
            fileName);

        try {
            DocumentBuilderFactory dbFactory =
                DocumentBuilderFactory.newInstance();
            dbFactory.setValidating(false);
            dbFactory.setNamespaceAware(false);

            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(new DefaultHandler());
            document = documentBuilder.parse(is);
        } catch (UnsupportedEncodingException e) {
            AMModelBase.debug.error("AMViewConfig.parseDocument", e);
        } catch (ParserConfigurationException e) {
            AMModelBase.debug.error("AMViewConfig.parseDocument", e);
        } catch (SAXException e) {
            AMModelBase.debug.error("AMViewConfig.parseDocument", e);
        } catch (IOException e) {
            AMModelBase.debug.error("AMViewConfig.parseDocument", e);
        }

        return document;
    }

    private void configTabs(Document doc) {
        NodeList nodes = doc.getElementsByTagName(TABS);

        if ((nodes != null) && (nodes.getLength() == 1)) {
            Node root = nodes.item(0);
            NodeList children = root.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);

                if (child.getNodeName().equalsIgnoreCase(TAB_ENTRY)) {
                    try {
                        AMTabEntry entry = new AMTabEntry(child);
                        tabs.add(entry);
                    } catch (AMConsoleException e) {
                        AMModelBase.debug.error("AMViewConfig.configTabs", e);
                    }
                }
            }
        } else {
            AMModelBase.debug.error(
                "AMViewConfig.configTabs TabConfig.xml is incorrect.");
        }
    }

    private List getProfileTabs(Node parent) {
        List entries = new ArrayList();
        NodeList children = parent.getChildNodes();

        int length = children.getLength();
        for (int i = 0; i < length; i++) {
            Node child = children.item(i);

            if (child.getNodeName().equalsIgnoreCase(TAB_ENTRY)) {
                try {
                    AMTabEntry entry = new AMTabEntry(child);
                    entries.add(entry);
                } catch (AMConsoleException e) {
                    AMModelBase.debug.error("AMViewConfig.getProfileTabs", e);
                }
            }
        }
        return entries;
    }

    private void configServices(Document doc) {
        NodeList nodes = doc.getElementsByTagName(SERVICES_CONFIG);

        if ((nodes != null) && (nodes.getLength() == 1)) {
            Node root = nodes.item(0);
            NodeList children = root.getChildNodes();

            if (children != null) {
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);

                    if (child.getNodeName().equals(HIDDEN_SERVICES)) {
                        try {
                            Set set = new HashSet();
                            String names = getAttribute(child, "names");
                            StringTokenizer st = new StringTokenizer(
                                names, ",");
                            while (st.hasMoreTokens()) {
                                set.add(st.nextToken().trim());
                            }
                            services.put(HIDDEN_SERVICES, set);
                        } catch (AMConsoleException e) {
                            AMModelBase.debug.error(
                                "AMViewConfig.configServices", e);
                        }
                    } else if (child.getNodeName().equals(CONSOLE_SERVICE)) {
                        try {
                            Set set = new HashSet();
                            String names = getAttribute(
                                child, REALM_ENABLE_HIDE_ATTRS);
                            StringTokenizer st = new StringTokenizer(
                                names, ",");
                            while (st.hasMoreTokens()) {
                                set.add(st.nextToken().trim());
                            }
                            services.put(REALM_ENABLE_HIDE_ATTRS, set);
                        } catch (AMConsoleException e) {
                            AMModelBase.debug.error(
                                "AMViewConfig.configServices", e);
                        }
                    } else if (child.getNodeName().equals(IDENTITY_SERVICE)) {
                        List list = new ArrayList();
                        services.put(IDENTITY_SERVICE, list);
                        try {
                            String order = getAttribute(child, "order");
                            StringTokenizer st = new StringTokenizer(
                                order, ",");
                            while (st.hasMoreTokens()) {
                                list.add(st.nextToken().trim());
                            }
                        } catch (AMConsoleException e) {
                            AMModelBase.debug.error(
                                "AMViewConfig.configServices", e);
                        }
                    }
                }
            }
        }
    }

    private void configProfileTabs(Document doc) {
        NodeList nodes = doc.getElementsByTagName(PROFILE_TABS);

        // there will be only 1 entry for the profiletab definitions
        if ((nodes != null) && (nodes.getLength() == 1)) {
            Node root = nodes.item(0);
            NodeList children = root.getChildNodes();

            // there can be 0 or more profile tabs defined
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);

                // get the tabs defined for the profile object
                if (child.getNodeName().equalsIgnoreCase("profile")) {
                    try {
                        // id is the name of the profile object, ie realms, 
                        // users, groups, ...
                        String id = getAttribute(child, "id");
                        if ((id != null) && (id.length() > 0)) {
                            profileTabs.put(id, getProfileTabs(child));
                        }
                    } catch (AMConsoleException e) {
                        AMModelBase.debug.error("AMViewConfig.configMenus", e);
                    }
                }
            }
        } else {
            AMModelBase.debug.error(
                "AMViewConfig.configProfileTabs, config xml is incorrect.");
        }
    }

    private void configMenus(Document doc) {
        NodeList nodes = doc.getElementsByTagName("showmenus");

        if ((nodes != null) && (nodes.getLength() == 1)) {
            Node root = nodes.item(0);
            NodeList children = root.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);

                if (child.getNodeName().equalsIgnoreCase("showmenu")) {
                    try {
                        String id = getAttribute(child, "id");
                        if ((id != null) && (id.length() > 0)) {
                            showMenus.put(id, getShowMenus(child));
                        }
                    } catch (AMConsoleException e) {
                        AMModelBase.debug.error("AMViewConfig.configMenus", e);
                    }
                }
            }
        }
    }

    private List getShowMenus(Node node)
        throws AMConsoleException {
        List list = new ArrayList();
        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equalsIgnoreCase("menu")) {
                try {
                    list.add(new AMShowMenuEntry(child));
                } catch (AMConsoleException e) {
                    AMModelBase.debug.error("AMViewConfig.getShowMenus", e);
                }
            }
        }

        return list;
    }

    private String getAttribute(Node node, String attrName)
        throws AMConsoleException {
        String value = null;
        NamedNodeMap attrs = node.getAttributes();
        Node nodeID = attrs.getNamedItem(attrName);
        if (nodeID != null) {
            value = nodeID.getNodeValue();
            value = value.trim();
        }
        return value;
    }

    private List getTabList(String type) {
        List tmp = null;
        if (type.equals(TABS)) {
            tmp = tabs;
        } else {
            tmp = (List)profileTabs.get(type);
        }
        return tmp;
    }
}
