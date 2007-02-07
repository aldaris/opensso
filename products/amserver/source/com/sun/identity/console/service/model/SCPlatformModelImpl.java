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
 * $Id: SCPlatformModelImpl.java,v 1.1 2007-02-07 20:26:47 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.service.model;

import com.sun.identity.console.base.model.AMAdminConstants;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMServiceProfileModel;
import com.sun.identity.console.base.model.AMServiceProfileModelImpl;
import com.sun.identity.console.property.PropertyXMLBuilder;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/* - NEED NOT LOG - */

public class SCPlatformModelImpl
    extends AMServiceProfileModelImpl
    implements AMServiceProfileModel
{
    public static final String SERVICE_NAME =
        AMAdminConstants.PLATFORM_SERVICE;
    public static final String ATTRIBUTE_NAME_SITE_LIST =
        "iplanet-am-platform-site-list";
    public static final String ATTRIBUTE_NAME_SERVER_LIST =
        "iplanet-am-platform-server-list";
    public static final String ATTRIBUTE_NAME_CLIENT_CHAR_SETS =
        "iplanet-am-platform-client-charsets";

    /**
     * Creates a simple model using default resource bundle.
     *
     * @param req HTTP Servlet Request
     * @param map of user information
     */
    public SCPlatformModelImpl(HttpServletRequest req, Map map
        ) throws AMConsoleException {
        super(req, SERVICE_NAME, map);
    }

    /**
     * Creates a simple model using default resource bundle.
     *
     * @param req HTTP Servlet Request
     * @param serviceName Name of Service.
     * @param map of user information
     */
    public SCPlatformModelImpl(
        HttpServletRequest req,
        String serviceName,
        Map map
    ) throws AMConsoleException {
        super(req, serviceName, map);
    }

    /**
     * Returns the XML for property sheet view component.
     *
     * @param realmName Name of Realm.
     * @param viewbeanClassName Class Name of View Bean.
     * @param serviceName Name of Service.
     * @return the XML for property sheet view component.
     * @throws AMConsoleException if XML cannot be created.
     */
    public String getPropertySheetXML(
        String realmName,
        String viewbeanClassName,
        String serviceName
    ) throws AMConsoleException {
        String xml = super.getPropertySheetXML(
            realmName, viewbeanClassName, serviceName);
        xml = PropertyXMLBuilder.removeSubSection(
            xml, ATTRIBUTE_NAME_SITE_LIST, TBL_SITE_LIST_XML);
        xml = PropertyXMLBuilder.removeSubSection(
            xml, ATTRIBUTE_NAME_SERVER_LIST, TBL_SERVER_LIST_XML);
        return PropertyXMLBuilder.removeSubSection(
            xml, ATTRIBUTE_NAME_CLIENT_CHAR_SETS, TBL_CLIENT_CHAR_SETS_XML);
    }

    private static String TBL_SITE_LIST_XML =
        "<property span=\"true\"><cc name=\"iplanet-am-platform-site-list\" tagclass=\"com.sun.web.ui.taglib.table.CCActionTableTag\" > <attribute name=\"title\" value=\"platform.service.table.siteList.name\" /><attribute name=\"empty\" value=\"platform.service.table.siteList.noentries\" /><attribute name=\"showPaginationControls\" value=\"false\" /><attribute name=\"selectionType\" value=\"multiple\" /><attribute name=\"selectionJavascript\" value=\"toggleTblButtonState('SCPlatform', 'SCPlatform.iplanet-am-platform-site-list', 'siteListCount', 'SCPlatform.tblSiteListButtonDelete', this)\" /><attribute name=\"showAdvancedSortingIcon\" value=\"false\" /><attribute name=\"showLowerActions\" value=\"false\" /><attribute name=\"showPaginationControls\" value=\"false\" /><attribute name=\"showPaginationIcon\" value=\"false\" /><attribute name=\"showSelectionIcons\" value=\"true\" /><attribute name=\"showSelectionSortIcon\" value=\"false\" /><attribute name=\"showSortingRow\" value=\"true\" /></cc></property>";

    private static String TBL_SERVER_LIST_XML =
        "<property span=\"true\"><cc name=\"iplanet-am-platform-server-list\" tagclass=\"com.sun.web.ui.taglib.table.CCActionTableTag\" > <attribute name=\"title\" value=\"platform.service.table.serverList.name\" /><attribute name=\"empty\" value=\"platform.service.table.serverList.noentries\" /><attribute name=\"showPaginationControls\" value=\"false\" /><attribute name=\"selectionType\" value=\"multiple\" /><attribute name=\"selectionJavascript\" value=\"toggleTblButtonState('SCPlatform', 'SCPlatform.iplanet-am-platform-server-list', 'serverListCount', 'SCPlatform.tblServerListButtonDelete', this)\" /><attribute name=\"showAdvancedSortingIcon\" value=\"false\" /><attribute name=\"showLowerActions\" value=\"false\" /><attribute name=\"showPaginationControls\" value=\"false\" /><attribute name=\"showPaginationIcon\" value=\"false\" /><attribute name=\"showSelectionIcons\" value=\"true\" /><attribute name=\"showSelectionSortIcon\" value=\"false\" /><attribute name=\"showSortingRow\" value=\"true\" /></cc></property>";
    private static String TBL_CLIENT_CHAR_SETS_XML =
        "<property span=\"true\"><cc name=\"iplanet-am-platform-client-charsets\" tagclass=\"com.sun.web.ui.taglib.table.CCActionTableTag\" > <attribute name=\"title\" value=\"platform.service.table.clientCharSets.name\" /><attribute name=\"empty\" value=\"platform.service.table.clientCharSets.noentries\" /><attribute name=\"showPaginationControls\" value=\"false\" /><attribute name=\"selectionType\" value=\"multiple\" /><attribute name=\"selectionJavascript\" value=\"toggleTblButtonState('SCPlatform', 'SCPlatform.iplanet-am-platform-client-charsets', 'clientCharSetsCount', 'SCPlatform.tblClientCharSetsButtonDelete', this)\" /><attribute name=\"showAdvancedSortingIcon\" value=\"false\" /><attribute name=\"showLowerActions\" value=\"false\" /><attribute name=\"showPaginationControls\" value=\"false\" /><attribute name=\"showPaginationIcon\" value=\"false\" /><attribute name=\"showSelectionIcons\" value=\"true\" /><attribute name=\"showSelectionSortIcon\" value=\"false\" /><attribute name=\"showSortingRow\" value=\"true\" /></cc></property>";
}
