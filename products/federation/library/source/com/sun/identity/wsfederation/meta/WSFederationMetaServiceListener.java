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
 * $Id: WSFederationMetaServiceListener.java,v 1.1 2007-06-21 23:01:32 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.wsfederation.meta;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.wsfederation.common.WSFederationUtils;
import com.sun.identity.wsfederation.meta.WSFederationMetaCache;
import com.sun.identity.wsfederation.profile.SPCache;
import com.sun.identity.plugin.configuration.ConfigurationListener;
import com.sun.identity.plugin.configuration.ConfigurationActionEvent;

/**
 * The <code>SAML2MetaServiceListener</code> implements
 * <code>ConfigurationListener</code> interface and is
 * used for maintaining the metadata cache.
 */
class WSFederationMetaServiceListener implements ConfigurationListener
{
    private static Debug debug = WSFederationUtils.debug;

    WSFederationMetaServiceListener() {
    }

    /**
     * This method will be invoked when a service's organization
     * configuation data has been changed.
     *
     * @param e the configuaration action event
     */
    public void configChanged(ConfigurationActionEvent e) {
	String configName = e.getConfigurationName();
	
	if (configName == null || configName.equals("WS-Federation") ||
	    configName.equals("WS-FederationCOT")) {
	    
	    if (debug.messageEnabled()) {
		debug.message(
                    "WSFederationMetaServiceListener.configChanged:");
	    }
	    WSFederationMetaCache.clear();
	    if (e.getRealm() == null) {
		SPCache.clear();
	    } else {
		SPCache.clear(e.getRealm());
	    }
	}
    }
}
