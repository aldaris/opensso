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
 * $Id: CreateHostedIDPViewBean.java,v 1.2 2008-01-17 06:36:27 veiming Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.task;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildContentDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.html.OptionList;
import com.sun.identity.console.base.AMPrimaryMastHeadViewBean;
import com.sun.identity.console.base.AMPropertySheet;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import com.sun.identity.console.task.model.TaskModel;
import com.sun.identity.console.task.model.TaskModelImpl;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

/**
 * Create hosted identity provider UI.
 */
public class CreateHostedIDPViewBean
    extends AMPrimaryMastHeadViewBean
{
    private static final String TAG_TABLE =
        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" title=\"\">";
    public static final String DEFAULT_DISPLAY_URL =
        "/console/task/CreateHostedIDP.jsp";
    private static final String PAGETITLE = "pgtitle";
    private static final String PROPERTY_ATTRIBUTE = "propertyAttributes";

    private static final String ENTITY_ID = "tfEntityId";
    private static final String META_DATA_FILE = "tfMetadataFile";
    private static final String EXT_DATA_FILE = "tfExtendedFile";
    private static final String SIGN_KEY = "tfSigningKey";
    private static final String ENC_KEY = "tfEncKey";
    private static final String HAS_META_DATA = "radioHasMetaData";
    private static final String SELECT_COT  = "radioCOT";
    private static final String RADIO_META  = "radioMeta";
    private static final String RADIO_EXTENDED  = "radioExtendedData";
    private static final String REALM = "tfRealm";
    
    private CCPageTitleModel ptModel;
    private AMPropertySheetModel propertySheetModel;
    
    public CreateHostedIDPViewBean() {
        super("CreateHostedIDP");
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        createPageTitleModel();
        createPropertyModel();
        registerChildren();
    }

    protected void registerChildren() {
        ptModel.registerChildren(this);
        propertySheetModel.registerChildren(this);
        registerChild(PAGETITLE, CCPageTitle.class);
        super.registerChildren();
    }
    
    protected View createChild(String name) {
        View view = null;
        
        if (name.equals(PAGETITLE)) {
            view = new CCPageTitle(this, ptModel, name);
        } else if (ptModel.isChildSupported(name)) {
            view = ptModel.createChild(this, name);
        } else if (name.equals(PROPERTY_ATTRIBUTE)) {
            view = new AMPropertySheet(this, propertySheetModel, name);
        } else if (propertySheetModel.isChildSupported(name)) {
            view = propertySheetModel.createChild(this, name, getModel());
        } else {
            view = super.createChild(name);
        }
        
        return view;
    }

    private void createPageTitleModel() {
        ptModel = new CCPageTitleModel(
            getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/oneBtnPageTitle.xml"));
        ptModel.setValue("button1", "button.configure");
    }
    
    private void createPropertyModel() {
        propertySheetModel = new AMPropertySheetModel(
            getClass().getClassLoader().getResourceAsStream(
            "com/sun/identity/console/propertyCreateHostedIDP.xml"));
        propertySheetModel.clear();
    }
    
    public String endPropertyAttributesDisplay(
        ChildContentDisplayEvent event
    ) {
        String html = event.getContent();
        int idx = html.indexOf(META_DATA_FILE);
        if (idx != -1) {
            idx = html.lastIndexOf("<table ", idx);
            if (idx != -1) {
                html = html.substring(0, idx) +
                    "<div id=\"meta\" style=\"display:none\">" +
                    html.substring(idx);
                
                idx = html.indexOf("tfRealm");
                idx = html.lastIndexOf("<tr>", idx);
                html = html.substring(0, idx) + "</table></div>" + 
                    "<div id=\"info\">" + TAG_TABLE +
                    html.substring(idx);
                idx = html.indexOf("tfEncKey");
                idx = html.indexOf("</table>", idx);
                html = html.substring(0, idx+8) + "</div>" +
                    html.substring(idx+8);

                idx = html.indexOf("tfRealm");
                idx = html.lastIndexOf("<div ", idx);
                html = html.substring(0, idx+5) + "id=\"realmfld\" " +
                    html.substring(idx+5);
                idx = html.lastIndexOf("<div ", idx-10);
                html = html.substring(0, idx+5) + "id=\"realmlbl\" " +
                    html.substring(idx+5);

                idx = html.indexOf("radioCOT");
                idx = html.lastIndexOf("<table ", idx);
                idx = html.lastIndexOf("<div ", idx);
                html = html.substring(0, idx) + 
                    "<div id=\"cotsection\" style=\"display:none\">" +
                    html.substring(idx);
                idx = html.indexOf("</table>", idx);
                html = html.substring(0, idx +8) + "</div>" +
                    html.substring(idx+8);

                idx = html.indexOf("radioCOT");
                idx = html.lastIndexOf("<tr>", idx);
                html = html.substring(0, idx) + "</table>" + 
                     "<div id=\"cotq\" style=\"display:none\">" +
                     TAG_TABLE + html.substring(idx);

                idx = html.indexOf("choiceCOT");
                idx = html.lastIndexOf("<tr>", idx);
                html = html.substring(0, idx) + "</table></div>" +
                    "<div id=\"cotchoice\" style=\"display:none\">" +
                    TAG_TABLE + html.substring(idx);

                idx = html.indexOf("tfCOT");
                idx = html.lastIndexOf("<tr>", idx);
                html = html.substring(0, idx) + "</table></div>" +
                    "<div id=\"cottf\" style=\"display:none\">" +
                    TAG_TABLE + html.substring(idx);
                idx = html.indexOf("</table>", idx);
                html = html.substring(0, idx+8) + "</div>" +
                    html.substring(idx+8);

                idx = html.indexOf("tfMetadataFile\"");
                idx = html.lastIndexOf("<input ", idx);
                html = html.substring(0, idx) +
                    "<span id=\"metadatafilename\"></span>" +
                    html.substring(idx);

                idx = html.indexOf("tfExtendedFile\"");
                idx = html.lastIndexOf("<input ", idx);
                html = html.substring(0, idx) +
                    "<span id=\"extendedfilename\"></span>" +
                    html.substring(idx);
            }
        }
        return html;
    }

    protected AMModel getModelInternal() {
        HttpServletRequest req = getRequestContext().getRequest();
        return new TaskModelImpl(req, getPageSessionAttributes());
        
    }

    public void beginDisplay(DisplayEvent e) {
        String value = (String)getDisplayFieldValue(HAS_META_DATA);
        if ((value == null) || value.equals("")){
            setDisplayFieldValue(HAS_META_DATA, "no");
        }

        value = (String)getDisplayFieldValue(SELECT_COT);
        if ((value == null) || value.equals("")){
            setDisplayFieldValue(SELECT_COT, "no");
        }

        value = (String)getDisplayFieldValue(RADIO_EXTENDED);
        if ((value == null) || value.equals("")){
            setDisplayFieldValue(RADIO_EXTENDED, "file");
        }

        value = (String)getDisplayFieldValue(RADIO_META);
        if ((value == null) || value.equals("")){
            setDisplayFieldValue(RADIO_META, "file");
        }
        
        setDisplayFieldValue(ENTITY_ID,
            SystemProperties.getServerInstanceName());

        try {
            TaskModel model = (TaskModel)getModel();
            Set realms = model.getRealms();
            CCDropDownMenu menuRealm = (CCDropDownMenu)getChild(REALM);
            menuRealm.setOptions(createOptionList(realms));
            
            Set keys = model.getSigningKeys();
            OptionList optionList = createOptionList(keys);
            optionList.add(0, 
                model.getLocalizedString("configure.provider.keys.none"), "");
            CCDropDownMenu menuSignKeys = (CCDropDownMenu)getChild(SIGN_KEY);
            menuSignKeys.setOptions(optionList);
            CCDropDownMenu menuEncKeys = (CCDropDownMenu)getChild(ENC_KEY);
            menuEncKeys.setOptions(optionList);
        } catch (AMConsoleException ex) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                ex.getMessage());
        }
    }
}
