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
 * $Id: SCPlatformClientCharSetsAddViewBean.java,v 1.1 2007-02-07 20:26:36 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.service;

import com.iplanet.am.util.OrderedSet;
import com.sun.identity.console.service.model.SCPlatformModelImpl;
import java.util.Map;
import java.util.Set;

public class SCPlatformClientCharSetsAddViewBean
    extends SCPlatformClientCharSetsViewBeanBase
{
    public static final String DEFAULT_DISPLAY_URL =
        "/console/service/SCPlatformClientCharSetsAdd.jsp";

    public SCPlatformClientCharSetsAddViewBean() {
        super("SCPlatformClientCharSetsAdd", DEFAULT_DISPLAY_URL);
    }

    protected String getButtonlLabel() {
        return "button.ok";
    }

    protected String getPageTitleText() {
        return "platform.service.clientCharSets.create.page.title";
    }

    protected void handleButton1Request(Map values) {
        SCPlatformViewBean vb = (SCPlatformViewBean)getViewBean(
            SCPlatformViewBean.class);
        Map attrValues = (Map)getPageSessionAttribute(
            SCPlatformViewBean.PROPERTY_ATTRIBUTE);
        Set clientCharSets = (Set)attrValues.get(
            SCPlatformModelImpl.ATTRIBUTE_NAME_CLIENT_CHAR_SETS);

        if ((clientCharSets == null) || clientCharSets.isEmpty()) {
            clientCharSets = new OrderedSet();
            attrValues.put(SCPlatformModelImpl.ATTRIBUTE_NAME_CLIENT_CHAR_SETS,
                (OrderedSet)clientCharSets);
        }

        String val = (String)values.get(ATTR_CLIENT_TYPE) + "|" +
            (String)values.get(ATTR_CHARSET);
        clientCharSets.add(val);
        setPageSessionAttribute(SCPlatformViewBean.PAGE_MODIFIED, "1");
        backTrail();
        unlockPageTrailForSwapping();
        passPgSessionMap(vb);
        vb.forwardTo(getRequestContext());
    }

    protected String getBreadCrumbDisplayName() {
      return "breadcrumbs.services.platform.client.charsets.add";
    }

    protected boolean startPageTrail() {
      return false;
    }
}
