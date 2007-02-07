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
 * $Id: AMPropertySheetModel.java,v 1.1 2007-02-07 20:19:42 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.base.model;

import com.iplanet.jato.view.ContainerViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.DisplayFieldImpl;
import com.iplanet.jato.view.html.OptionList;
import com.sun.identity.console.property.PropertyTemplate;
import com.sun.web.ui.common.CCDescriptor;
import com.sun.web.ui.common.CCTagClass;
import com.sun.web.ui.model.CCActionTableModel;
import com.sun.web.ui.model.CCAddRemoveModel;
import com.sun.web.ui.model.CCPropertySheetModel;
import com.sun.web.ui.model.CCEditableListModel;
import com.sun.web.ui.view.addremove.CCAddRemove;
import com.sun.web.ui.view.editablelist.CCEditableList;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* - NEED NOT LOG - */

public class AMPropertySheetModel
    extends CCPropertySheetModel
{
    private static String UTF_8 = "UTF-8";
    private static String NON_BREAK_SPACE = "\u00A0";

    public static final String TBL_SUB_CONFIG = "tblSubConfig";
    public static final String TBL_SUB_CONFIG_BUTTON_ADD =
        "tblSubConfigButtonAdd";
    public static final String TBL_SUB_CONFIG_BUTTON_DELETE =
        "tblSubConfigButtonDelete";
    public static final String TBL_SUB_CONFIG_COL_NAME =
        "tblSubConfigColName";
    public static final String TBL_SUB_CONFIG_COL_TYPE =
        "tblSubConfigColType";
    public static final String TBL_SUB_CONFIG_DATA_NAME =
        "tblSubConfigDataName";
    public static final String TBL_SUB_CONFIG_HREF_NAME =
        "tblSubConfigHrefName";
    public static final String TBL_SUB_CONFIG_DATA_TYPE =
        "tblSubConfigDataType";
    private Set children = new HashSet();
    private Set passwordComponents;
    private Map radioDefaultValue;
    private Set dateComponents;
    private boolean hasSubConfigTable;

    public AMPropertySheetModel() {
        super();
    }

    public Set getChildrenNames() {
        return children;
    }

    public AMPropertySheetModel(String name) {

        // Lockhart is using platform's native encoding to construct
        // an InputStream object from the xml document passed as a string
        // to this constructor. Fix for processing non-ascii
        // characters in the xml document using UTF-8.
        try {
            setDocument(new ByteArrayInputStream(
                name.getBytes(UTF_8)));
        } catch(UnsupportedEncodingException uee) {
            setDocument(new ByteArrayInputStream(
                name.getBytes()));
        }
    }

    public AMPropertySheetModel(ServletContext sc, String file) {
        super(sc, file);
    }

    public AMPropertySheetModel(InputStream is) {
        super(is);
    }

    public void setDocument(InputStream is) {
        super.setDocument(is);
        passwordComponents = new HashSet();
        dateComponents = new HashSet();
        radioDefaultValue = new HashMap();
        hackToGetChildViews();
    }

    public void registerChildren(ContainerViewBase view) {
        super.registerChildren(view);
        if (hasSubConfigTable) {
            CCActionTableModel model = createSubConfigActionTableModel();
            model.registerChildren(view);
        }
    }

    private void hackToGetChildViews() {
        Document document = getDocument();
        if (document != null) {
            parseNodeList(
                document.getElementsByTagName(CCDescriptor.CC_ELEMENT));
        }
    }

    private void parseNodeList(NodeList nodeList) {
        if (nodeList != null) {
            int length = nodeList.getLength();
            for (int i = 0; i < length; i++) {
                Node node = nodeList.item(i);

                // Check node for name attribute.
                if (node.hasAttributes()) {
                    NamedNodeMap nodeAttrs = node.getAttributes();
                    Node nameNode = nodeAttrs.getNamedItem(NAME_ATTRIBUTE);
                    Node tagclassNode = nodeAttrs.getNamedItem(
                        CCDescriptor.CC_ELEMENT_TAGCLASS);
                                                                                
                    if ((nameNode != null) && (tagclassNode != null)) {
                        String name = nameNode.getNodeValue();
                        String v = tagclassNode.getNodeValue();

                        if (name.startsWith(PropertyTemplate.DATE_MARKER_NAME)){
                            String dateName = name.substring(
                                PropertyTemplate.DATE_MARKER_NAME.length());
                            dateComponents.add(dateName);
                        } else if (v != null) {
                            if (v.equals(CCTagClass.EDITABLELIST)) {
                                setModel(name, new CCEditableListModel());
                            } else if (v.equals(CCTagClass.PASSWORD)) {
                                passwordComponents.add(name);
                            } else if (v.equals(CCTagClass.RADIOBUTTON)) {
                                String def = getNodeDefaultValue(node);
                                if (def != null) {
                                    radioDefaultValue.put(name, def);
                                }
                            }
                        }

                        if (name.equals(TBL_SUB_CONFIG)) {
                            hasSubConfigTable = true;
                        }
                    }
                }
            }
        }
    }

    private String getNodeDefaultValue(Node n) {
        String defaultValue = null;
        NodeList children = n.getChildNodes() ;
        if (children != null) {
            int length = children.getLength();
            for (int i = 0; (i < length) && (defaultValue == null); i++) {
                Node node = children.item(i);
                String tagName = node.getNodeName();
                if (tagName.equals("attribute")) {
                    NamedNodeMap nodeAttrs = node.getAttributes();
                    Node nameNode = nodeAttrs.getNamedItem(NAME_ATTRIBUTE);
                    if (nameNode != null) {
                        String name = nameNode.getNodeValue();
                        if (name.equals("defaultValue")) {
                            Node valueNode = nodeAttrs.getNamedItem("value");
                            defaultValue = valueNode.getNodeValue();
                        }
                    }
                }
            }
        }
        return defaultValue;
    }

    public void setValues(String name, Object[] values, AMModel model) {
        Object m = getModel(name);

        /*
         * Need to replace " " with "" because we hack the Service XML
         * to have required attribute values as " "
         */
        if ((values != null) && (values.length == 1)) {
            if (values[0] instanceof String) {
            String val = (String)values[0];
                if ((val == null) || val.equals(NON_BREAK_SPACE)) {
                    values[0] = "";
                }
            }
        }
        if ((m != null) && CCEditableListModel.class.isInstance(m)) {
            ((CCEditableListModel)m).setOptionList(AMAdminUtils.toSet(values));
        }

        if (passwordComponents.contains(name)) {
            super.setValues(name + PropertyTemplate.PWD_CONFIRM_SUFFIX, values);
            super.setValues(name, values);
        } else if (dateComponents.contains(name)) {
            super.setValues(name, getDateInUserLocale(values, model));
        } else {
            super.setValues(name, values);
        }
    }

    protected Object[] getDateInUserLocale(Object[] dates, AMModel model) {
        Object[] values = null;
        if ((dates != null) && (dates.length > 0)) {
            values = new Object[dates.length];
            for (int i = 0; i < dates.length; i++) {
                String tmp = (String)dates[i];
                if (tmp == null) {
                    values[i] = tmp;
                } else {
                    Date dt =
                        com.sun.identity.shared.locale.Locale.parseNormalizedDateString(
                            tmp);
                    String dateString =
                        com.sun.identity.shared.locale.Locale.getDateString(
                            dt, model.getUserLocale());
                    if (dateString != null) {
                        values[i] = dateString;
                    } else {
                        values[i] = tmp;
                    }
                }
            }
        }
        return (values == null) ? dates : values;
    }

    /**
     * Returns the default values of radio buttons. Map of radio button name
     * to default value (String).
     *
     * @return the default values of radio buttons.
     */
    public Map getRadioDefaultValues() {
        return radioDefaultValue;
    }
 
    public View createChild(View parent, String name, AMModel model) {
        View view = super.createChild(parent, name);

        if (CCEditableList.class.isInstance(view)) {
            CCEditableList editable = (CCEditableList)view;
            CCEditableListModel m = (CCEditableListModel)editable.getModel();
            m.setAddBtnLabel(
                model.getLocalizedString("editableList.addButtonLabel"));
            m.setRemoveBtnLabel(
                model.getLocalizedString("editableList.deleteButtonLabel"));
        }

        children.add(view);
        return view;
    }

    private CCActionTableModel createSubConfigActionTableModel() {
        CCActionTableModel tblModel = new CCActionTableModel(
            getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/tblSubConfig.xml"));
        tblModel.setTitleLabel("label.items");
        tblModel.setActionValue(TBL_SUB_CONFIG_BUTTON_ADD, "button.new");
        tblModel.setActionValue(TBL_SUB_CONFIG_BUTTON_DELETE, "button.delete");
        tblModel.setActionValue(TBL_SUB_CONFIG_COL_NAME,
            "subconfig.table.column.name");
        tblModel.setActionValue(TBL_SUB_CONFIG_COL_NAME,
            "subconfig.table.column.name");
        tblModel.setActionValue(TBL_SUB_CONFIG_COL_TYPE,
            "subconfig.table.column.type");
        setModel(TBL_SUB_CONFIG, tblModel);
        return tblModel;
    }

    public void clear() {
        super.clear();

        for (Iterator iter = children.iterator(); iter.hasNext(); ) {
            View view = (View)iter.next();

            if (CCAddRemove.class.isInstance(view)) {
                CCAddRemoveModel m = (CCAddRemoveModel)getModel(
                    view.getName());
                m.clear();
                m.setAvailableOptionList(new OptionList());
                m.setSelectedOptionList(new OptionList());
            } else if (CCEditableList.class.isInstance(view)) {
                CCEditableListModel m = (CCEditableListModel)getModel(
                    view.getName());
                m.setOptionList(new OptionList());
                ((CCEditableList)view).resetStateData();
            } else {
                if (DisplayFieldImpl.class.isInstance(view)) {
                    ((DisplayFieldImpl)view).setValues(null);
                }
            }
        }
    }
}
