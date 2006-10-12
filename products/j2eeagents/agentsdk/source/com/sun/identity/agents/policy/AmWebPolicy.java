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
 * $Id: AmWebPolicy.java,v 1.2 2006-10-12 06:24:04 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.iplanet.sso.SSOToken;
import com.sun.identity.agents.arch.AgentBase;
import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.arch.Manager;
import com.sun.identity.agents.arch.ServiceFactory;
import com.sun.identity.agents.util.NameValuePair;
import com.sun.identity.policy.ActionDecision;
import com.sun.identity.policy.PolicyDecision;
import com.sun.identity.policy.client.PolicyEvaluator;
import com.sun.identity.policy.client.PolicyEvaluatorFactory;
import com.sun.identity.policy.plugins.IPCondition;
import com.sun.identity.shared.Constants;

/**
 * The class handles and evaluates URL policies
 */
public class AmWebPolicy extends AgentBase implements IAmWebPolicy {
    
    public AmWebPolicy(Manager manager) {
        super(manager);
    }
    
    public void initialize() throws AgentException {
        try {
            setAppSSOProvider(
                    ServiceFactory.getAmWebPolicyAppSSOProvider(getManager()));
            setPolicyEvaluator(PolicyEvaluatorFactory.getInstance().
                    getPolicyEvaluator(AM_WEB_SERVICE_NAME,
                    getAppSSOProvider()));
        } catch (Exception ex) {
            throw new AgentException("Failed to initialize AmWebPolicy", ex);
        }
    }
    
    public AmWebPolicyResult checkPolicyForResource(
            SSOToken ssoToken, String resource, String action,
            String ipAddress, String hostName) {
        AmWebPolicyResult result = AmWebPolicyResult.DENY;
        
        try {
            HashSet        actionNameSet = new HashSet(1);
            actionNameSet.add(action);
            
            PolicyDecision policyDecision =
                    getPolicyEvaluator().getPolicyDecision(ssoToken,
                    resource, actionNameSet,
                    getPolicyEnvironmentMap(ipAddress, hostName));
            
            if (policyDecision == null) {
                if (isLogWarningEnabled()) {
                    logWarning("AmWebPolicy: null policy decision for resource:"
                            + resource + ", action: " + action);
                }
            } else {
                if (isLogMessageEnabled()) {
                    logMessage("AmWebPolicy: XML policy decision for resource:"
                            + resource + ", action=" + action
                            + ", XML=" + policyDecision.toXML());
                }
                
                ActionDecision actionDecision = (ActionDecision)
                policyDecision.getActionDecisions().get(action);
                
                if (actionDecision != null) {
                    result = processActionDecision(actionDecision, resource,
                            action);
                } else {
                    if (isLogWarningEnabled()) {
                        logWarning(
                            "AmWebPolicy: empty action decision for resource:"
                            + resource + ", action=" + action);
                    }
                }
                
                // Only for AM70 and above
                if (ServiceFactory.getisIDMAvailable()) {
                    result.setResponseAttributes(
                            policyDecision.getResponseAttributes());
                }
            }
        } catch (Exception ex) {
            logError("AmWebPolicy: Unable to check polisy for resource: "
                    + resource + ", action: " + action
                    + "; Access will be denied", ex);
            result = AmWebPolicyResult.DENY;
        }
        
        return result;
    }
    
    private AmWebPolicyResult processActionDecision(
            ActionDecision decision, String resource, String action)
            throws Exception {
        AmWebPolicyResult result = AmWebPolicyResult.DENY;
        Set set = (Set) decision.getValues();
        boolean resultStatus = false;
        
        if (set != null && set.size() > 0) {
            Iterator it = set.iterator();
            while (it.hasNext()) {
                String value = (String) it.next();
                if (value.equals(ALLOW_VALUE)) {
                    if (isLogMessageEnabled()) {
                        logMessage(
                                "AmWebPolicy : Policy for resource " + resource
                                + " with action " + action + " : " + value);
                    }
                    result = getAllowResult(getAdviceNVP(decision));
                    resultStatus = true;
                } else {
                    if (isLogMessageEnabled()) {
                        logMessage(
                                "AmWebPolicy : Policy for resource " + resource
                                + " with action "   + action + " : " + value);
                    }
                    
                    NameValuePair[] advicevals = getAdviceNVP(decision);
                    
                    if((advicevals != null) && (advicevals.length > 0)) {
                        result = getInsufficientCredentialResult(advicevals);
                    } else {
                        result = getDenyResult();
                    }
                    resultStatus = true;
                }
            }
        } else {
            Map advices = decision.getAdvices();
            
            if (!resultStatus && advices != null && advices.size() > 0) {
                if (isLogMessageEnabled()) {
                    logMessage("AmWebPolicy : No Policy found for resource "
                            + resource + " with action " + action);
                }
                result = 
                        getInsufficientCredentialResult(getAdviceNVP(decision));
                resultStatus = true;
            }
        }
        
        return result;
    }
    
    /*
     * This function does the flip between the 70 composite advice
     * and the 63 advice schemes
     * @param actionDecision
     * @return NameValuePair[] array of name value pair objects
     * @throws Exception
     */
    private NameValuePair[] getAdviceNVP(ActionDecision actionDecision)
    throws Exception {
        
        NameValuePair[] result = null;
        if (ServiceFactory.getisIDMAvailable()) {
            // For 70 and upwards
            result = getCompositeAdviceNVP(actionDecision);
        } else {
            // Till 63
            result = createAdviceNameValueArray(actionDecision);
        }
        
        return result;
    }
    
    private NameValuePair[] getCompositeAdviceNVP(ActionDecision actionDecision)
    throws Exception {
        NameValuePair[] result = null;
        
        String adviceName = Constants.COMPOSITE_ADVICE;
        String compositeAdvice =
                getPolicyEvaluator().getCompositeAdvice(actionDecision);
        
        if (compositeAdvice == null) {
            if (isLogMessageEnabled()) {
                logMessage("AmWebPolicy: No Advices found");
            }
        } else {
            result = new NameValuePair[1];
            result[0] = new NameValuePair(adviceName, compositeAdvice);
            if (isLogMessageEnabled()) {
                logMessage("AmWebPolicy: Found composite advice: " + result[0]);
            }
        }
        
        return result;
    }
    
    /**
     * Create Name value pair for advice map and return to caller
     *
     * @param actionDecision
     *
     * @return Name Value pair for advices
     *
     *
     * @throws Exception
     */
    private NameValuePair[] createAdviceNameValueArray(
            ActionDecision actionDecision) throws Exception {
        
        if(isLogMessageEnabled()) {
            logMessage(
                    "AmWebPolicy : Creating Name Value Pairs for Advice maps");
        }
        
        // Only one advice name value will be returned
        ArrayList advicevals = new ArrayList();
        int       count      = 0;
        Map       advices    = actionDecision.getAdvices();
        
        if((advices != null) && !advices.isEmpty()) {
            if(isLogMessageEnabled()) {
                logMessage(
                        "AmWebPolicy : Policy Decision has Advices " +
                        " associated with it");
            }
            
            Set keys = advices.keySet();
            
            if(keys != null) {
                Iterator keyIter = keys.iterator();
                
                while(keyIter.hasNext() && !keys.isEmpty()) {
                    String adviceName  = (String) keyIter.next();
                    Set    adviceValue = (Set) advices.get(adviceName);
                    int    len         = adviceValue.size();
                    
                    if(len > 0) {
                        Object[] array = adviceValue.toArray();
                        
                        for(int i = 0; i < array.length; i++) {
                            if(adviceName != null) {
                                String advice =
                                        translateAdviceName(adviceName);
                                
                                if((array[i] != null) && (advice != null)) {
                                    if(array[i].toString() != null) {
                                        advicevals.add(
                                                new NameValuePair(
                                                advice, array[i].toString()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if(isLogMessageEnabled()) {
                logMessage("AmWebPolicy : Policy Decision has no Advices "
                        + " associated with it");
            }
        }
        
        return returnAdviceMap(advicevals);
    }
    
    /**
     * Return advice map giving priority to advice scheme or level
     *
     *
     * @param advicevals
     *
     * @return NameValuePair[] array
     *
     * @see
     */
    private NameValuePair[] returnAdviceMap(ArrayList advicevals) {
        
        NameValuePair[] schemereturn = new NameValuePair[1];
        NameValuePair[] levelreturn  = new NameValuePair[1];
        
        if(advicevals.size() > 0) {
            for(int i = 0; i < advicevals.size(); i++) {
                NameValuePair obj = (NameValuePair) advicevals.get(i);
                
                if(obj.getName().equals(AUTH_SCHEME_URL_PREFIX)) {
                    schemereturn[0] = obj;
                } else {
                    levelreturn[0] = obj;
                }
            }
        }
        
        if(schemereturn[0] != null) {
            if(isLogMessageEnabled()) {
                logMessage("AmWebPolicy : Found Advice for Auth Scheme");
                
                if(schemereturn[0].toString() != null) {
                    logMessage("AmWebPolicy: "
                            + getAdviceMessage(schemereturn[0]));
                }
            }
            
            return schemereturn;
        } else if(levelreturn[0] != null) {
            if(isLogMessageEnabled()) {
                logMessage("AmWebPolicy : Found Advice for Auth Level");
                
                if(levelreturn[0].toString() != null) {
                    logMessage("AmWebPolicy: "
                            + getAdviceMessage(levelreturn[0]));
                }
            }
            
            return levelreturn;
        }
        
        return null;
    }
    
    private String getAdviceMessage(NameValuePair nvp) {
        
        String adviceNameValueStr = null;
        
        if(nvp != null) {
            if((nvp.getName() != null) && (nvp.getValue() != null)) {
                adviceNameValueStr = "Advice Name = [" + nvp.getName()
                + "] Advice Value = [" + nvp.getValue()
                + "]";
            }
        }
        
        return adviceNameValueStr;
    }
    
    /**
     * Translate advices, except for auth scheme and value, we will
     * ignore/discard all other advices
     *
     * @param adviceName
     *
     * @return advice string returned for url prefix
     * while redirecting
     *
     */
    private String translateAdviceName(String adviceName) {
        
        String transName = null;
        
        try {
            if (adviceName.equals(AUTH_SCHEME_ADVICE_RESPONSE)) {
                transName = AUTH_SCHEME_URL_PREFIX;
            } else if (adviceName.equals(AUTH_LEVEL_ADVICE_RESPONSE)) {
                transName = AUTH_LEVEL_URL_PREFIX;
            } else {
                // return null in this case since we do not want
                // to process any other advice
                if(isLogWarningEnabled()) {
                    logWarning(
                        "AmWebPolicy : Ignoring advice map : " + adviceName);
                }
                
            }
        } catch (Exception ex) {
            logError("AmWebPolicy: Failed translate advice name", ex);
        }
        
        return transName;
    }
    
    
    
    private AmWebPolicyResult getDenyResult() {
        return getDenyResult(null);
    }
    
    private AmWebPolicyResult getDenyResult(NameValuePair[] adviceList) {
        return new AmWebPolicyResult(AmWebPolicyResultStatus.STATUS_DENY,
                adviceList);
    }
    
    private AmWebPolicyResult getAllowResult() {
        return getAllowResult(null);
    }
    
    private AmWebPolicyResult getAllowResult(NameValuePair[] adviceList) {
        return new AmWebPolicyResult(AmWebPolicyResultStatus.STATUS_ALLOW,
                adviceList);
    }
    
    private AmWebPolicyResult getInsufficientCredentialResult() {
        return getInsufficientCredentialResult(null);
    }
    
    private AmWebPolicyResult getInsufficientCredentialResult(
            NameValuePair[] adviceList) {
        return new AmWebPolicyResult(
                AmWebPolicyResultStatus.STATUS_INSUFFICIENT_CREDENTIALS,
                adviceList);
    }
    
    private PolicyEvaluator getPolicyEvaluator() {
        return _policyEvaluator;
    }
    
    private void setPolicyEvaluator(PolicyEvaluator policyEvaluator) {
        _policyEvaluator = policyEvaluator;
    }
    
    private Map getPolicyEnvironmentMap(String ipAddress, String hostName) {
        Map result = new HashMap();
        
        Set dnsNameSet = new HashSet();
        dnsNameSet.add(hostName);
        
        Set ipAddressSet = new HashSet();
        ipAddressSet.add(ipAddress);
        
        result.put(IPCondition.REQUEST_IP, ipAddressSet);
        result.put(IPCondition.REQUEST_DNS_NAME, dnsNameSet);
        return result;
    }
    
    private IAmWebPolicyAppSSOProvider getAppSSOProvider() {
        return _appSSOProvider;
    }
    
    private void setAppSSOProvider(IAmWebPolicyAppSSOProvider appSSOProvider) {
        _appSSOProvider = appSSOProvider;
    }
    
    private PolicyEvaluator _policyEvaluator;
    private IAmWebPolicyAppSSOProvider _appSSOProvider;
}
