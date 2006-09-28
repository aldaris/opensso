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
 * $Id: AmSDKProfileAttributeHelper.java,v 1.1 2006-09-28 23:24:04 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.iplanet.am.sdk.AMStoreConnection;
import com.iplanet.am.sdk.AMUser;
import com.iplanet.sso.SSOToken;
import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.arch.Module;
import com.sun.identity.agents.arch.SurrogateBase;

/**
 * The class fetches the user profile attributes from AmSDK
 */
public class AmSDKProfileAttributeHelper extends SurrogateBase implements
        IProfileAttributeHelper {
    
    public AmSDKProfileAttributeHelper(Module module) {
        super(module);
    }

    public void initialize() {
        if (isLogMessageEnabled()) {
            logMessage("AmSDKProfileAttributeHelper: Will use AM APIs");
        }
    }

    public String getAttribute(SSOToken token, String attributeName)
            throws AgentException {
        String result = null;
        String userDN = null;
        try {
            AMUser user = getUser(token);
            userDN = token.getPrincipal().toString();
            if (user != null) {
                Set attributeKeySet = new HashSet();
                attributeKeySet.add(attributeName);
                Map userAttributesMap = user.getAttributes(attributeKeySet);
                if ((userAttributesMap != null) && 
                    (!userAttributesMap.isEmpty())) {
                    Iterator it = userAttributesMap.keySet().iterator();
                    if (it.hasNext()) {
                        Set attributeSet = (Set)userAttributesMap
                                        .get(attributeName);
                        if (attributeSet != null && attributeSet.size() > 0) {
                            result = (String) attributeSet.iterator().next();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new AgentException("Failed to find user attribute", ex);
        }
        if (isLogMessageEnabled()) {
            logMessage("AmSDKProfileAttributeHelper: user: " + userDN 
                    + ", attributeName: " + attributeName 
                    + ", value: " + result);
        }
        return result;
    }

    public String getAttribute(SSOValidationResult ssoValidationResult,
            String attributeName) throws AgentException {
        return getAttribute(ssoValidationResult.getSSOToken(), attributeName);
    }

    public Map getAttributeMap(SSOToken token, Map queryMap)
            throws AgentException {
        Map attributeMap = new HashMap();
        String userDN = null;
        try {
            AMUser user = getUser(token);
            userDN = token.getPrincipal().toString();
            Map userAttributesMap = user.getAttributes(queryMap.keySet());
            if ((userAttributesMap != null) && 
                (!userAttributesMap.isEmpty())) {
                Iterator it = userAttributesMap.keySet().iterator();
                while (it.hasNext()) {
                    String nextAttributeName = (String) it.next();
                    String mappedName = (String) queryMap
                                .get(nextAttributeName);
                    attributeMap.put(mappedName, userAttributesMap
                                .get(nextAttributeName));
                }
            }
        } catch (Exception ex) {
            throw new AgentException("Unable to obtain attributes: " 
                    + queryMap + ", for user: " 
                    + userDN, ex);
        }
        
        if (isLogMessageEnabled()) {
            logMessage("AmSDKProfileAttributeHelper: user: " 
                    + userDN + ", attributeMap: " + attributeMap);
        }
        
        return attributeMap;
    }

    public Map getAttributeMap(SSOValidationResult ssoValidationResult,
            Map queryMap) throws AgentException {
        return getAttributeMap(ssoValidationResult.getSSOToken(), queryMap);
    }
    
    private AMUser getUser(SSOToken token) throws Exception {
        AMStoreConnection connection = new AMStoreConnection(token);
        return connection.getUser(token.getPrincipal().toString());
    }

}
