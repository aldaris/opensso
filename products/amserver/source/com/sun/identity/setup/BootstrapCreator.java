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
 * $Id: BootstrapCreator.java,v 1.1 2008-02-26 01:21:23 veiming Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.setup;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.services.ldap.DSConfigMgr;
import com.iplanet.services.ldap.DSConfigMgrBase;
import com.iplanet.services.ldap.IDSConfigMgr;
import com.iplanet.services.ldap.LDAPServiceException;
import com.iplanet.services.ldap.LDAPUser;
import com.iplanet.services.ldap.ServerGroup;
import com.iplanet.services.ldap.ServerInstance;
import com.iplanet.services.util.Crypt;
import com.iplanet.services.util.XMLException;
import com.iplanet.sso.SSOException;
import com.sun.identity.common.configuration.ConfigurationException;
import com.sun.identity.sm.SMSException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.StringTokenizer;

/**
 * This class is responsible for creating bootstrap file based on the
 * information in <code>serverconfig.xml</code>.
 */
public class BootstrapCreator {
    private static BootstrapCreator instance = new BootstrapCreator();
    
    private static final String template =
        "ldap://@DS_HOST@/@INSTANCE_NAME@" +
        "?pwd=@DSAMEUSER_PWD@" +
        "&dsbasedn=@BASE_DN@" +
        "&dsmgr=@BIND_DN@" +
        "&dspwd=@BIND_PWD@";
     
    private BootstrapCreator() {
    }

    public static BootstrapCreator getInstance() {
        return instance;
    }
    
    public static void updateBootstrap()
        throws ConfigurationException {
        try {
            DSConfigMgrBase dsCfg = new DSConfigMgrBase();
            dsCfg.parseServiceConfigXML();
            instance.update(dsCfg);
        } catch (XMLException e) {
            throw new ConfigurationException(e.getMessage());
        } catch (SMSException e) {
            throw new ConfigurationException(e.getMessage());
        } catch (SSOException e) {
            throw new ConfigurationException(e.getMessage());
        }
    }
    
    public static void createBootstrap()
        throws ConfigurationException {
        try {
            instance.update(DSConfigMgr.getDSConfigMgr());
        } catch (LDAPServiceException e) {
            throw new ConfigurationException(e.getMessage());
        }
    }

    private void update(IDSConfigMgr dsCfg)
        throws ConfigurationException {
        try {
            ServerGroup sg = dsCfg.getServerGroup("sms");
            String hostNames;
            ServerInstance svrCfg;

            if (sg == null) {
                hostNames = dsCfg.getHostName("default");
                svrCfg = dsCfg.getServerInstance(LDAPUser.Type.AUTH_ADMIN);
            } else {
                hostNames = dsCfg.getHostName("sms");
                svrCfg = sg.getServerInstance(LDAPUser.Type.AUTH_ADMIN);
            }

            ServerGroup defaultGroup = dsCfg.getServerGroup("default");
            ServerInstance userInstance = defaultGroup.getServerInstance(
                LDAPUser.Type.AUTH_ADMIN);
            String dsameUserPwd = Crypt.encode(userInstance.getPasswd(),
                Crypt.getHardcodedKeyEncryptor());

            String connDN = svrCfg.getAuthID();
            String connPwd = Crypt.encode(svrCfg.getPasswd(),
                Crypt.getHardcodedKeyEncryptor());
            String rootSuffix = svrCfg.getBaseDN();

            StringTokenizer st = new StringTokenizer(hostNames);
            StringBuffer bootstrap = new StringBuffer();

            while (st.hasMoreElements()) {
                String token = st.nextToken();
                token = token.trim();
                String url = template.replaceAll("@DS_HOST@", token);
                url = url.replaceAll("@INSTANCE_NAME@",
                    URLEncoder.encode(SystemProperties.getServerInstanceName(),
                    "UTF-8"));
                url = url.replaceAll("@DSAMEUSER_PWD@",
                    URLEncoder.encode(dsameUserPwd, "UTF-8"));
                url = url.replaceAll("@BASE_DN@",
                    URLEncoder.encode(rootSuffix, "UTF-8"));
                url = url.replaceAll("@BIND_DN@",
                    URLEncoder.encode(connDN, "UTF-8"));
                url = url.replaceAll("@BIND_PWD@",
                    URLEncoder.encode(connPwd, "UTF-8"));
                bootstrap.append(url).append("\n");
            }
            AMSetupServlet.writeToFile(AMSetupServlet.getBootStrapFile(),
                bootstrap.toString());
        } catch (IOException e) {
            throw new ConfigurationException(e.getMessage());
        }
    }        
}
