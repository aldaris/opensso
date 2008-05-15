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
 * $Id: UserIdRepo.java,v 1.3 2008-05-15 04:51:02 kevinserwin Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.setup;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.common.LDAPUtils;
import com.sun.identity.idm.IdConstants;
import com.sun.identity.sm.AttributeSchema;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;
import com.sun.identity.sm.ServiceSchema;
import com.sun.identity.sm.ServiceSchemaManager;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;

/**
 * This class does Directory Server related tasks for 
 * Access Manager deployed as single web-application. 
 */
class UserIdRepo {
   
    private UserIdRepo() {
    }

    static void configure(
        Map userRepo, 
        String basedir,
        ServletContext servletCtx,
        SSOToken adminToken
    ) throws SMSException, SSOException, LDAPException, IOException {
        String type = (String)userRepo.get(SetupConstants.USER_STORE_TYPE);
        if (type == null) {
            type = "LDAPv3ForAMDS";
        }
        
        boolean bFAMUserSchema = type.equals("LDAPv3ForAMDS");
        if (bFAMUserSchema) {
            loadSchema(userRepo, basedir, servletCtx);
        }

        addSubConfig(userRepo, type, bFAMUserSchema, adminToken);
    }

    private static void addSubConfig(
        Map userRepo, 
        String type, 
        boolean bFAMUserSchema,
        SSOToken adminToken
    ) throws SMSException, SSOException {
        ServiceConfig cfg = getOrgConfig(adminToken);
        
        Map values = new HashMap();
        
        addValueToMap("sun-idrepo-ldapv3-config-authid", getBindDN(userRepo),
            values);
        addValueToMap("sun-idrepo-ldapv3-config-authpw", 
            getBindPassword(userRepo), values);
        addValueToMap("sun-idrepo-ldapv3-config-organization_name", 
            userRepo.get(SetupConstants.USER_STORE_ROOT_SUFFIX), values);
        addValueToMap("sun-idrepo-ldapv3-config-psearchbase", 
            userRepo.get(SetupConstants.USER_STORE_ROOT_SUFFIX), values);

        String host = getHost(userRepo);
        addValueToMap("sun-idrepo-ldapv3-config-ldap-server", 
            host + ":" + getPort(userRepo), values);

        String ssl = (String)userRepo.get(SetupConstants.USER_STORE_SSL);
        if ((ssl != null) && ssl.equals("SSL")) {
            Set sslSet = new HashSet(2);
            sslSet.add("true");
            values.put("sun-idrepo-ldapv3-config-ssl-enabled", sslSet);
        }

        if (!bFAMUserSchema) {
            values.put("sun-idrepo-ldapv3-config-group-container-name",
                Collections.EMPTY_SET);
            values.put("sun-idrepo-ldapv3-config-group-container-value",
                Collections.EMPTY_SET);

            Set objectClasses = new HashSet(12);
            objectClasses.add("inetadmin");
            objectClasses.add("inetorgperson");
            objectClasses.add("inetuser");
            objectClasses.add("organizationalperson");
            objectClasses.add("person");
            objectClasses.add("top");
            values.put("sun-idrepo-ldapv3-config-user-objectclass", 
                objectClasses);
        }
        
        cfg.addSubConfig(host, type, 0, values);
    }
    
    private static void addValueToMap(String key, Object val, Map values) {
        Set set = new HashSet(2);
        set.add(val);
        values.put(key, set);
    }

    static ServiceConfig getOrgConfig(SSOToken adminToken) 
        throws SMSException, SSOException {
        ServiceConfigManager svcCfgMgr = new ServiceConfigManager(
            IdConstants.REPO_SERVICE, adminToken);
        ServiceConfig cfg = svcCfgMgr.getOrganizationConfig("", null);
        Map values = new HashMap();
        if (cfg == null) {
            OrganizationConfigManager orgCfgMgr =
                new OrganizationConfigManager(adminToken, "/");
            ServiceSchemaManager schemaMgr = new ServiceSchemaManager(
                IdConstants.REPO_SERVICE, adminToken);
            ServiceSchema orgSchema = schemaMgr.getOrganizationSchema();
            Set attrs = orgSchema.getAttributeSchemas();

            for (Iterator iter = attrs.iterator(); iter.hasNext();) {
                AttributeSchema as = (AttributeSchema) iter.next();
                values.put(as.getName(), as.getDefaultValues());
            }
            cfg = orgCfgMgr.addServiceConfig(IdConstants.REPO_SERVICE,
                values);
        }
        return cfg;
    }
    
    private static String getHost(Map userRepo) {
        return (String)userRepo.get(SetupConstants.USER_STORE_HOST);
    }
    
    private static String getPort(Map userRepo) {
        return (String)userRepo.get(SetupConstants.USER_STORE_PORT);
    }
    
    private static String getBindDN(Map userRepo) {
        return (String) userRepo.get(SetupConstants.USER_STORE_LOGIN_ID);
    }
    
    private static String getBindPassword(Map userRepo) {
        return (String) userRepo.get(SetupConstants.USER_STORE_LOGIN_PWD);
    }
    
    private static void loadSchema(
        Map userRepo, 
        String basedir,
        ServletContext servletCtx
    ) throws LDAPException, IOException {
        LDAPConnection ld = null;
        try {
            ld = getLDAPConnection(userRepo);
            List schemas = writeSchemaFiles(basedir, servletCtx);
            for (Iterator i = schemas.iterator(); i.hasNext(); ) {
                String file = (String)i.next();
                LDAPUtils.createSchemaFromLDIF(file, ld);
            }
        } finally {
            disconnectDServer(ld);
        }
    }
    
    private static List writeSchemaFiles(
        String basedir, 
        ServletContext servletCtx
    ) throws IOException {
        List files = new ArrayList();
        ResourceBundle rb = ResourceBundle.getBundle(
            SetupConstants.SCHEMA_PROPERTY_FILENAME);
        String strFiles = rb.getString(
            SetupConstants.SDK_PROPERTY_FILENAME);

        StringTokenizer st = new StringTokenizer(strFiles);
        while (st.hasMoreTokens()) {
            String file = st.nextToken();
            InputStreamReader fin = new InputStreamReader(
                servletCtx.getResourceAsStream(file));
            StringBuffer sbuf = new StringBuffer();
            char[] cbuf = new char[1024];
            int len;
            while ((len = fin.read(cbuf)) > 0) {
                sbuf.append(cbuf, 0, len);
            }
            FileWriter fout = null;
            try {
                int idx = file.lastIndexOf("/");
                String absFile = (idx != -1) ? file.substring(idx+1) : file;
                String outfile = basedir + "/" + absFile;
                fout = new FileWriter(outfile);
                String inpStr = sbuf.toString();
                fout.write(ServicesDefaultValues.tagSwap(inpStr));
                files.add(outfile);
            } finally {
                if (fin != null) {
                    try {
                        fin.close();
                    } catch (Exception ex) {
                        //No handling requried
                    }
                }
                if (fout != null) {
                    try {
                        fout.close();
                    } catch (Exception ex) {
                        //No handling requried
                    }
                }
            }
        }
        return files;
    }
    
    private static void disconnectDServer(LDAPConnection ld)
        throws LDAPException {
        if ((ld != null) && ld.isConnected()) {
            ld.disconnect();
        }
    }
    
    private static LDAPConnection getLDAPConnection(Map userRepo)
        throws LDAPException {
        LDAPConnection ld = new LDAPConnection();
        ld.setConnectTimeout(300);
        int port = Integer.parseInt(getPort(userRepo));
        ld.connect(3, getHost(userRepo), port,
            getBindDN(userRepo), getBindPassword(userRepo));
        return ld;
    }
}
