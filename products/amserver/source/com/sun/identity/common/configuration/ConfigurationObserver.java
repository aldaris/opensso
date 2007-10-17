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
 * $Id: ConfigurationObserver.java,v 1.1 2007-10-17 23:00:31 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.common.configuration;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.Constants;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceListener;
import java.io.IOException;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class listens to changes in configuration changes
 */
public class ConfigurationObserver implements ServiceListener {
    private static int PARENT_LEN = ConfigurationBase.CONFIG_SERVERS.length() 
        + 2;
    
    /**
     * This method will be invoked when a service's schema has been changed.
     * 
     * @param serviceName Name of the service.
     * @param version Version of the service.
     */
    public void schemaChanged(String serviceName, String version) {
        //no-op
    }
    
    /**
     * This method will be invoked when a service's global configuration data
     * has been changed. The parameter <code>groupName</code> denote the name
     * of the configuration grouping (e.g. default) and
     * <code>serviceComponent</code> denotes the service's sub-component that
     * changed (e.g. <code>/NamedPolicy</code>, <code>/Templates</code>).
     * 
     * @param serviceName Name of the service.
     * @param version Version of the service.
     * @param groupName Name of the configuration grouping.
     * @param serviceComponent Name of the service components that changed.
     * @param type change type, i.e., ADDED, REMOVED or MODIFIED.
     */
    public void globalConfigChanged(
        String serviceName, 
        String version,
        String groupName, 
        String serviceComponent, 
        int type
    ) {
        if (serviceName.equals(Constants.SVC_NAME_PLATFORM)) {            
            String serverName = serviceComponent.substring(PARENT_LEN);
            
            if (serverName.equals(ServerConfiguration.DEFAULT_SERVER_CONFIG) ||
                serverName.equals(SystemProperties.getServerInstanceName())
            ) {
                SSOToken adminToken = (SSOToken)AccessController.doPrivileged(
                    AdminTokenAction.getInstance());
                try {
                    Properties newProp = ServerConfiguration.getServerInstance(
                        adminToken, serverName);
                    SystemProperties.initializeProperties(newProp, true, true);
                    
                } catch (SSOException ex) {
                    //ingored
                } catch (IOException ex) {
                    //ingored
                } catch (SMSException ex) {
                    //ingored
                }
            }
        }
    }
    
    /**
     * This method will be invoked when a service's organization configuration
     * data has been changed. The parameters <code>orgName</code>,
     * <code>groupName</code> and <code>serviceComponent</code> denotes the
     * organization name, configuration grouping name and service's
     * sub-component that are changed respectively.
     * 
     * @param serviceName Name of the service.
     * @param version Version of the service.
     * @param orgName Organization name as DN.
     * @param groupName Name of the configuration grouping
     * @param serviceComponent Name of the service components that changed.
     * @param type Change type, i.e., ADDED, REMOVED or MODIFIED
     */
    public void organizationConfigChanged(
        String serviceName, 
        String version,
        String orgName, 
        String groupName, 
        String serviceComponent,
        int type
     ) {
        // no-op
     }
}
