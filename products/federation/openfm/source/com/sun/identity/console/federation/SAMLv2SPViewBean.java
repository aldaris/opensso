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
 * $Id: SAMLv2SPViewBean.java,v 1.1 2007-08-03 22:29:03 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import com.sun.identity.console.federation.model.EntityModel;
import com.sun.web.ui.view.alert.CCAlert;

public class SAMLv2SPViewBean extends SAMLv2Base {

    public static final String DEFAULT_DISPLAY_URL =
	"/console/federation/SAMLv2SP.jsp";
 
    public SAMLv2SPViewBean() {
	super("SAMLv2SP");
	setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
    }

    public void beginDisplay(DisplayEvent event)
	throws ModelControlException
    {
	super.beginDisplay(event);
    }
     
    protected void createPropertyModel() {
        retrieveCommonProperties();
	if (isHosted()) {
            psModel = new AMPropertySheetModel(
                getClass().getClassLoader().getResourceAsStream(
                    "com/sun/identity/console/propertySAMLv2SPHosted.xml"));   
        } else {    
            psModel = new AMPropertySheetModel(
                getClass().getClassLoader().getResourceAsStream(
                    "com/sun/identity/console/propertySAMLv2SPRemote.xml"));
        }
	psModel.clear();
    }
    
    public void handleButton1Request(RequestInvocationEvent event)
	throws ModelControlException
    {            
        forwardTo();
    }
}
