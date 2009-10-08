/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 * 
 * The contents of this file are subject to the terms
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
 * $Id: WssProfileDao.java,v 1.2 2009-10-08 16:21:06 ggennaro Exp $
 */

package com.sun.identity.admin.dao;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.admin.model.WscProfileBean;
import com.sun.identity.admin.model.WspProfileBean;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceSchema;
import com.sun.identity.sm.ServiceSchemaManager;
import com.sun.identity.wss.security.PasswordCredential;
import com.sun.identity.wss.security.WSSUtils;

public class WssProfileDao {

    private static final String SERVICE_NAME = "AgentService";
    private static final String SUB_SCHEMA_WSC = "WSCAgent";
    private static final String SUB_SCHEMA_WSP = "WSPAgent";

    private static final String USERNAME_CREDENTIALS = "UserCredential";

    
    WssProfileDao() {
        // do nothing to force use of static methods
    }
    
    //--------------------------------------------------------------------------


    @SuppressWarnings("unchecked")
    static public WscProfileBean getDefaultWscProfileBean() {
        Map defaults = getServiceAttributeDefaults(SUB_SCHEMA_WSC);
        return getWscProfileBeanFromMap(defaults);
    }
    
    @SuppressWarnings("unchecked")
    static public WspProfileBean getDefaultWspProfileBean() {
        Map defaults = getServiceAttributeDefaults(SUB_SCHEMA_WSP);
        return getWspProfileBeanFromMap(defaults);
    }
    
    //--------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    static private Map getServiceAttributeDefaults(String subSchemaType) {

        SSOToken adminToken = WSSUtils.getAdminToken();
        
        try {
            
            ServiceSchemaManager scm 
                = new ServiceSchemaManager(SERVICE_NAME, adminToken);
            ServiceSchema orgSchema = scm.getOrganizationSchema();
            ServiceSchema subOrgSchema = orgSchema.getSubSchema(subSchemaType);

            return subOrgSchema.getAttributeDefaults();
            
        } catch (SSOException ssoEx) {
            throw new RuntimeException(ssoEx);
        } catch (SMSException smsEx) {
            throw new RuntimeException(smsEx);
        }
    
    }
    
    
    @SuppressWarnings("unchecked")
    static private WscProfileBean getWscProfileBeanFromMap(Map map) {
        WscProfileBean bean = new WscProfileBean();
        
        
        return bean;
    }
    
    @SuppressWarnings("unchecked")
    static private WspProfileBean getWspProfileBeanFromMap(Map map) {
        WspProfileBean bean = new WspProfileBean();
        
        
        return bean;
    }
    
    
    //--------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    static private boolean getBooleanValue(String keyName, Map map) {
        String value = getStringValue(keyName, map);
        return Boolean.valueOf(value).booleanValue();
    }

    @SuppressWarnings("unchecked")
    static private int getIntValue(String keyName, Map map) {
        String value = getStringValue(keyName, map);
        return value == null ? -1 : Integer.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    static private String getStringValue(String keyName, Map map) {
        
        if( map != null && map.get(keyName) instanceof Set ) {
            
            Set<String> values = (Set<String>) map.get(keyName);
            if( values != null && !values.isEmpty() )
                return (String)values.iterator().next();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static private ArrayList<String> getListValue(String keyName, Map map) {
        ArrayList<String> a = new ArrayList<String>();
        if( map != null && map.get(keyName) instanceof Set ) {

            Set<String> values = (Set<String>) map.get(keyName);
            if( values != null ) {
                a.addAll(values);
            }
        }
        return a;
    }

    /**
     * Retrieves a list of password credentials obtained from the profile
     * configuration under USERNAME_CREDENTIALS stored in following raw format
     * for each entry:
     * 
     *  UserName:test|UserPassword:test 
     *  
     * @param map   Service attribute map
     * @return ArrayList of PasswordCredential objects
     */
    @SuppressWarnings("unchecked")
    static private ArrayList<PasswordCredential> getPasswordCredentialsValue(Map map) {
        ArrayList<PasswordCredential> a = new ArrayList<PasswordCredential>();

        if( map.get(USERNAME_CREDENTIALS) != null ) {
            Set<String> values = (Set<String>)map.get(USERNAME_CREDENTIALS);
            Pattern p = Pattern.compile("UserName:(.+?)\\|UserPassword:(.+?)");
            
            for(String v : values) {
                StringTokenizer st = new StringTokenizer(v, ",");
                while( st.hasMoreTokens() ) {
                    String creds = st.nextToken();
                    Matcher m = p.matcher(creds);
                    if( m.matches() ) {
                        String username = m.group(1);
                        String password = m.group(2);
                        if( username != null && password != null ) {
                            PasswordCredential pc = new PasswordCredential(username, password);
                            a.add(pc);
                        }
                    }
                }
            }
        }
        return a;
    }

}
