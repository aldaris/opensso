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
 * $Id: TabBean.java,v 1.1 2006-11-30 00:44:42 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.base;

import com.sun.identity.console.base.model.AccessControlModel;
import com.sun.identity.console.base.model.AccessControlModelImpl;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.ConsoleException;
import com.sun.identity.console.controller.TabController;
import com.sun.web.ui.component.Tab;
import com.sun.web.ui.component.TabSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;
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


/**
 * This class is responsible to constructing the tab set.
 */
public class TabBean {
    private static final String TABS = "tabs";
    private static final String TAB_ENTRY = "tab";
    private static final String TAB_XML = "ConsoleConfiguration.xml";
    
    private TabSet tabSet = new TabSet();
    
    /**
     * Creates a new instance of <code>TabBean</code>
     */
    public TabBean() {
        Document doc = parseDocument(TAB_XML);
        configTabs(doc);
    }

    /**
     * Returns the set of tabs for the administrator/user. This set is
     * constructed with his/her privileges. This is binded to the
     * tabSet ui component in JSP.
     *
     * @return Set of tabs.
     */
    public TabSet getTabSet() {
        return tabSet;
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
            AMModelBase.debug.error("TabBean.parseDocument", e);
        } catch (ParserConfigurationException e) {
            AMModelBase.debug.error("TabBean.parseDocument", e);
        } catch (SAXException e) {
            AMModelBase.debug.error("TabBean.parseDocument", e);
        } catch (IOException e) {
            AMModelBase.debug.error("TabBean.parseDocument", e);
        }
        
        return document;
    }
    
    private void configTabs(Document doc) {
        AccessControlModel model = new AccessControlModelImpl(
            (HttpServletRequest)FacesContext.getCurrentInstance()
                .getExternalContext().getRequest());
        NodeList nodes = doc.getElementsByTagName(TABS);
        
        if ((nodes != null) && (nodes.getLength() == 1)) {
            Node root = nodes.item(0);
            NodeList children = root.getChildNodes();
            
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                
                if (child.getNodeName().equalsIgnoreCase(TAB_ENTRY)) {
                    try {
                        TabEntry entry = new TabEntry(this, child);
                        /* TODO: Hardcode realm as "/"
                         * Need to track the realm on the page and pass it
                         * down to this method.
                         */
                        Tab tab = entry.getTabComponent(model, "/");
                        if (tab != null) {
                            tabSet.getChildren().add(tab);
                        }
                    } catch (ConsoleException e) {
                        AMModelBase.debug.error("TabBean.configTabs", e);
                    }
                }
            }
        } else {
            AMModelBase.debug.error("TabBean.configTabs: XML is incorrect.");
        }
    }

    /**
     * Returns the localized string of an expression e.g.
     * <code>#{common['tab.realms.label']}</code>.
     *
     * @param expr Expression.
     * @return Localized string of the expression.
     */
    private String resolveTabLabel(String expr) {
        String result = "";
        ApplicationFactory factory = (ApplicationFactory)
            FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        ValueBinding vb = factory.getApplication().createValueBinding(expr);
        try {
            result = (String) vb.getValue(FacesContext.getCurrentInstance());
        } catch (PropertyNotFoundException e) {
            AMModelBase.debug.error("TabBean.resolveTabLabel, expr=" + expr, e);
        } catch (EvaluationException e) {
            AMModelBase.debug.error("TabBean.resolveTabLabel, expr=" + expr, e);
        }
        return result;
    }

    /*
     * This inner class parses a XML node to get the tab information.
     * Then it determines if the tab is viewable to the user or not.
     * if not, getTabComponent method will return null.
     */
    private class TabEntry {
        private TabBean bean;
        private String id;
        private String label;
        private String url;
        private String tooltip;
        private String accessLevel;
        private Set permissions;
        private boolean delegateUI;
        private Class controller;
        private List<TabEntry> children = new ArrayList<TabEntry>();

        private TabEntry(TabBean bean, Node root)
            throws ConsoleException {
            this.bean = bean;
            NamedNodeMap attrs = root.getAttributes();
            if (attrs == null) {
                throw new ConsoleException(
                    "TabEntry.<init> incorrect XML format");
            }

            Node nodeID = attrs.getNamedItem("id");
            if (nodeID == null) {
                throw new ConsoleException(
                    "TabEntry.setID: missing id attribute");
            }
            id = nodeID.getNodeValue();

            label = getAttribute(attrs, "label",
                "TabEntry.<init> missing label attribute");
            url = getAttribute(attrs, "url",
                "TabEntry.<init> missing url attribute");
            tooltip = getAttribute(attrs, "tooltip",
                "TabEntry.<init> missing tooltip attribute");
            accessLevel = getAttribute(attrs, "accesslevel", null);
            permissions = getAttributes(attrs, "permissions",
                "TabEntry.<init> missing permissions attribute" + label);
            delegateUI = getBoolAttribute(attrs, "delegateUI");
            controller = getController(attrs);
            setChildren(bean, root);
        }

        private String getAttribute(
            NamedNodeMap attrs,
            String attrName,
            String exceptionMsg
        ) throws ConsoleException {
            String value = null;
            Node nodeID = attrs.getNamedItem(attrName);
            if (nodeID == null) {
                if (exceptionMsg != null) {
                    throw new ConsoleException(exceptionMsg);
                }
            } else {
                value = nodeID.getNodeValue().trim();
                if (value.length() == 0) {
                    value = null;
                    if (exceptionMsg != null) {
                        throw new ConsoleException(exceptionMsg);
                    }
                }
            }
            return value;
        }

        private Set getAttributes(
            NamedNodeMap attrs,
            String attrName,
            String exceptionMsg
        ) throws ConsoleException {
            Node nodeID = attrs.getNamedItem(attrName);
            if (nodeID == null) {
                throw new ConsoleException(exceptionMsg);
            }
            String strValue = nodeID.getNodeValue().trim();

            if (strValue.length() == 0) {
                throw new ConsoleException(exceptionMsg);
            } else {
                Set values = new HashSet();
                StringTokenizer st = new StringTokenizer(strValue, ",");
                while (st.hasMoreTokens()) {
                    values.add(st.nextToken().trim());
                }
                return values;
            }
        }


        private boolean getBoolAttribute(NamedNodeMap attrs, String attrName) {
            boolean boolVal = false;
            Node nodeID = attrs.getNamedItem(attrName);
            if (nodeID != null) {
                String value = nodeID.getNodeValue().trim();
                boolVal = value.equals("true");
            }
            return boolVal;
        }

        private Class getController(NamedNodeMap attrs) {
            Class clazz = null;
            Node nodeID = attrs.getNamedItem("controller");
            if (nodeID != null) {
                String value = nodeID.getNodeValue().trim();
                if (value.length() > 0) {
                    try {
                        clazz = Class.forName(value);
                    } catch (ClassNotFoundException e) {
                        AMModelBase.debug.error("TabEntry.getController", e);
                    }
                }
            }
            return clazz;
        }

        private void setChildren(TabBean bean, Node root)
            throws ConsoleException {
            NodeList childrenNodes = root.getChildNodes();
            for (int i = 0; i < childrenNodes.getLength(); i++) {
                Node child = childrenNodes.item(i);
                if (child.getNodeName().equalsIgnoreCase("tab")) {
                    TabEntry childTab = new TabEntry(bean, child);
                    children.add(childTab);
                }
            }
        }

        private Tab getTabComponent(AccessControlModel model, String realmName){
            Tab tab = null;
            if (canView() &&
                model.canView(permissions, accessLevel, realmName, delegateUI)
            ) {
                tab = new Tab();
                tab.setId(id);
                tab.setText(bean.resolveTabLabel(label));
                tab.setToolTip(bean.resolveTabLabel(tooltip));
                tab.setUrl(url);

                for (TabEntry te : children) {
                    Tab childTab = te.getTabComponent(model, realmName);
                    if (childTab != null) {
                        tab.getChildren().add(childTab);
                    }
                }            
            }
            return tab;
        }

        private boolean canView() {
            boolean can = true;
            if (controller != null) {
                try {
                    TabController c = (TabController)controller.newInstance();
                    can = c.isVisible();
                } catch (InstantiationException e) {
                    AMModelBase.debug.error("TabEntry.canView", e);
                } catch (IllegalAccessException e) {
                    AMModelBase.debug.error("TabEntry.canView", e);
                }
            }
            return can;
        }
    }
}
