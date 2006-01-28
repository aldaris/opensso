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
 * $Id: AMAuthUtils.java,v 1.1 2006-01-28 09:17:10 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.authentication.util;

import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import com.sun.identity.sm.DNMapper;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class provides utility methods to Policy and Administration console
 * service to get realm qualified Authentication data.
 */
public class AMAuthUtils {
    private static Debug utilDebug = Debug.getInstance("amAMAuthUtils");
    
    private AMAuthUtils() {
    }
    
    /**
     * Returns the set of all authenticated Realm names.
     *
     * @param token valid user <code>SSOToken</code>
     * @return Set containing String values representing Realm names.
     * @throws SSOException if <code>token.getProperty()</code> fails.
     */
    public static Set getAuthenticatedRealms(SSOToken token)
            throws SSOException {
        Set returnRealms = new HashSet();
        String ssoRealm = token.getProperty(ISAuthConstants.ORGANIZATION);
        returnRealms.add(DNMapper.orgNameToRealmName(ssoRealm));
        Set realmsFromScheme =
        parseData(token.getProperty(ISAuthConstants.AUTH_TYPE), true);
        returnRealms.addAll(realmsFromScheme);
        Set realmsFromLevel =
        parseData(token.getProperty(ISAuthConstants.AUTH_LEVEL), true);
        returnRealms.addAll(realmsFromLevel);
        Set realmsFromService =
        parseData(token.getProperty(ISAuthConstants.SERVICE), true);
        returnRealms.addAll(realmsFromService);
        if (utilDebug.messageEnabled()) {
            utilDebug.message("Realms from SSO Org : " + ssoRealm );
            utilDebug.message("Realms from Auth Type : " + realmsFromScheme );
            utilDebug.message("Realms from Auth Level : " + realmsFromLevel );
            utilDebug.message("Realms from Service : " + realmsFromService );
            utilDebug.message("Return getAuthenticatedRealms : "
            + returnRealms);
        }
        return returnRealms;
    }
    
    /**
     * Returns the set of all authenticated Scheme names.
     *
     * @param token valid user <code>SSOToken</code>
     * @return Set containing String values representing Scheme names.
     * @throws SSOException if <code>token.getProperty()</code> fails.
     */
    public static Set getAuthenticatedSchemes(SSOToken token)
            throws SSOException {
        return (parseData(token.getProperty(ISAuthConstants.AUTH_TYPE), false));
    }
    
    /**
     * Returns the set of all authenticated Service names.
     *
     * @param token valid user <code>SSOToken</code>
     * @return Set containing String values representing Service names.
     * @throws SSOException if <code>token.getProperty()</code> fails.
     */
    public static Set getAuthenticatedServices(SSOToken token)
            throws SSOException {
        return (parseData(token.getProperty(ISAuthConstants.SERVICE), false));
    }
    
    /**
     * Returns the set of all authenticated levels.
     *
     * @param token valid user <code>SSOToken</code>
     * @return Set containing String values representing levels.
     * @throws SSOException if <code>token.getProperty()</code> fails.
     */
    public static Set getAuthenticatedLevels(SSOToken token)
            throws SSOException {
        return (parseData(token.getProperty(ISAuthConstants.AUTH_LEVEL),false));
    }
    
    /**
     * Returns the set of all authenticated realm qualified scheme names.
     *
     * @param token valid user <code>SSOToken</code>
     * @return Set containing String values representing
     * realm qualified scheme names.
     * @throws SSOException if <code>token.getProperty()</code> fails.
     */
    public static Set getRealmQualifiedAuthenticatedSchemes(SSOToken token)
            throws SSOException {
        return (parseRealmData(token.getProperty(ISAuthConstants.AUTH_TYPE)));
    }
    
    /**
     * Returns the set of all authenticated realm qualified service names.
     *
     * @param token valid user <code>SSOToken</code>
     * @return Set containing String values representing
     * realm qualified service names.
     * @throws SSOException if <code>token.getProperty()</code> fails.
     */
    public static Set getRealmQualifiedAuthenticatedServices(SSOToken token)
            throws SSOException {
        return (parseRealmData(token.getProperty(ISAuthConstants.SERVICE)));
    }
    
    /**
     * Returns the set of all authenticated realm qualified authentication
     * levels.
     *
     * @param token valid user <code>SSOToken</code>
     * @return Set containing String values representing
     * realm qualified authentication levels.
     * @throws SSOException if <code>token.getProperty()</code> fails.
     */
    public static Set getRealmQualifiedAuthenticatedLevels(SSOToken token)
            throws SSOException {
        return (parseRealmData(token.getProperty(ISAuthConstants.AUTH_LEVEL)));
    }
    
    /**
     * Returns the given data in Realm qualified format.
     *
     * @param realm valid Realm
     * @param data data which qualifies for Realm qualified data. This could
     * be authentication scheme or authentication level or service.
     * @return String representing realm qualified authentication data.
     */
    public static String toRealmQualifiedAuthnData(String realm, String data) {
        String realmQualifedData = data;
        if (realm != null && realm.length() != 0) {
            realmQualifedData = 
                realm.trim() + ISAuthConstants.COLON + data.trim();
        }
        return realmQualifedData;
    }
    
    /**
     * Returns the Realm name from Realm qualified data.
     *
     * @param realmQualifedData Realm qualified data. This could be Realm
     * qualified authentication scheme or authentication level or service.
     * @return String representing realm name.
     */
    public static String getRealmFromRealmQualifiedData(
        String realmQualifedData) {
        String realm = null;
        if (realmQualifedData != null && realmQualifedData.length() != 0) {
            int index = realmQualifedData.indexOf(ISAuthConstants.COLON);
            if (index != -1) {
                realm = realmQualifedData.substring(0, index).trim();
            }
        }
        if (utilDebug.messageEnabled()) {
            utilDebug.message("realmQualifedData : " + realmQualifedData );
            utilDebug.message("RealmFromRealmQualifiedData : " + realm );
        }
        return realm;
    }
    
    /**
     * Returns the data from Realm qualified data. This could be authentication
     * scheme or authentication level or service.
     *
     * @param realmQualifedData Realm qualified data. This could be Realm
     * qualified authentication scheme or authentication level or service.
     * @return String representing data. This could be authentication
     * scheme or authentication level or service.
     */
    public static String getDataFromRealmQualifiedData(
        String realmQualifedData) {
        String data = null;
        if (realmQualifedData != null && realmQualifedData.length() != 0) {
            int index = realmQualifedData.indexOf(ISAuthConstants.COLON);
            if (index != -1) {
                data = realmQualifedData.substring(index + 1).trim();
            } else {
                data = realmQualifedData;
            }
        }
        if (utilDebug.messageEnabled()) {
            utilDebug.message("realmQualifedData : " + realmQualifedData );
            utilDebug.message("DataFromRealmQualifiedData : " + data );
        }
        return data;
    }
    
    /**
     * Returns the set of all authenticated Realm names or Scheme names or
     * levels or Service names.
     *
     * @param data Realm qualified data. This could be Realm
     * qualified authentication scheme or authentication level or service.
     * @param realm Boolean indicator to get Realm names if true; otherwise
     * get schemes or levels or services names.
     * @return the set of all authenticated Realm names or Scheme names or
     * levels or Service names.
     */
    private static Set parseData(String data, boolean realm) {
        Set returnData = Collections.EMPTY_SET;
        if (data != null && data.length() != 0) {
            StringTokenizer stz = new StringTokenizer(data,
            ISAuthConstants.PIPE_SEPARATOR);
            returnData = new HashSet();
            while (stz.hasMoreTokens()) {
                String nameValue = (String)stz.nextToken();
                int index = nameValue.indexOf(ISAuthConstants.COLON);
                if ((index == -1) && (realm)){
                    continue;
                } else if (index == -1) {
                    returnData.add(nameValue);
                    continue;
                }
                String name = nameValue.substring(0, index).trim();
                String value = nameValue.substring(index + 1).trim();
                if (realm) {
                    returnData.add(name);
                } else {
                    returnData.add(value);
                }
            }
        }
        if (utilDebug.messageEnabled()) {
            utilDebug.message("parseData:Input data : " + data );
            utilDebug.message("parseData:returnData : " + returnData );
        }
        return returnData;
    }
    
    /**
     * Returns the set of all authenticated realm qualified Scheme names or
     * levels or Service names.
     *
     * @param data Realm qualified data. This could be Realm
     * qualified authentication scheme or authentication level or service.
     * @return the set of all authenticated realm qualified Scheme names or
     * levels or Service names.
     */
    private static Set parseRealmData(String data) {
        Set returnData = Collections.EMPTY_SET;
        if (data != null && data.length() != 0) {
            StringTokenizer stz = new StringTokenizer(data,
            ISAuthConstants.PIPE_SEPARATOR);
            returnData = new HashSet();
            while (stz.hasMoreTokens()) {
                returnData.add((String)stz.nextToken());
            }
        }
        if (utilDebug.messageEnabled()) {
            utilDebug.message("parseRealmData:Input data : " + data );
            utilDebug.message("parseRealmData:returnData : " + returnData );
        }
        return returnData;
    }
}
