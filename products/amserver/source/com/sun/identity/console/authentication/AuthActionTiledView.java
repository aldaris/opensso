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
 * $Id: AuthActionTiledView.java,v 1.1 2007-02-07 20:18:49 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.authentication;

import com.iplanet.jato.view.View;
import com.iplanet.jato.view.html.OptionList;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.sun.identity.console.base.AMTableTiledView;
import com.sun.web.ui.model.CCActionTableModel;
import com.sun.web.ui.view.html.CCDropDownMenu;

public class AuthActionTiledView
    extends AMTableTiledView {

    public AuthActionTiledView(
        View parent, 
        CCActionTableModel model, 
        String name
    ) {
        super(parent, model, name);
    }

    public boolean beginChildDisplay(ChildDisplayEvent event) {
        super.endDisplay(event);
        boolean display = true;
        int rowIndex = model.getRowIndex();
                                                                                
        if (rowIndex < model.getNumRows()) {
            String childName = event.getChildName();

            if (childName.indexOf(AuthConfigViewBean.MODULE_NAME) != -1) {
                display = displayModuleNameMenu(childName, rowIndex);
            } else if (childName.indexOf(AuthConfigViewBean.CRITERIA) != -1) {
                display = displayCriteriaMenu(childName,rowIndex);
            } else if (
                childName.indexOf(AuthConfigViewBean.OPTION_FIELD) != -1)
            {
                display = displayTextFieldAction(childName, rowIndex);
            }
        }

        return display;
    }

    private boolean displayCriteriaMenu(
        String childName,
        int index
    ) {
        boolean display = false;
        AuthConfigViewBean parentVB = (AuthConfigViewBean)getParentViewBean();

        CCDropDownMenu list = (CCDropDownMenu)getChild(childName);
        OptionList optList = parentVB.getCriteriaNameChoiceValues();
        list.setOptions(optList);

        if ((optList != null) && (optList.size() > 0)) {
            String value = parentVB.getModuleFlag(index);
            if ((value != null) && (value.length() != 0)) {
                list.setValue(value);
                display = true;
            }
        }

        return display;
    }

    private boolean displayModuleNameMenu(
        String childName,
        int index
    ) {
        boolean display = false;
        AuthConfigViewBean parentVB = (AuthConfigViewBean)getParentViewBean();

        CCDropDownMenu list = (CCDropDownMenu)getChild(childName);
        OptionList optList = parentVB.getModuleNameChoiceValues();
        list.setOptions(optList);

        if ((optList != null) && (optList.size() > 0)) {
            String value = parentVB.getModuleName(index);
            if ((value != null) && (value.length() != 0)) {
                list.setValue(value);
                display = true;
            }
        }

        return display;
    }

    private boolean displayTextFieldAction(
        String childName,
        int index
    ) {
        boolean display = true;
        AuthConfigViewBean parent = (AuthConfigViewBean)getParentViewBean();
        String value = parent.getOptionFieldValue(index);
        model.setValue(childName,value);
        return display;
    }
}
