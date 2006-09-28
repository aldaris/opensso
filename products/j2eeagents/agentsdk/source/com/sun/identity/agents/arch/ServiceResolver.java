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
 * $Id: ServiceResolver.java,v 1.1 2006-09-28 23:22:59 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.arch;

import java.util.ArrayList;

import com.sun.identity.agents.common.AmSDKProfileAttributeHelper;
import com.sun.identity.agents.common.ApplicationSSOTokenProvider;
import com.sun.identity.agents.common.CookieResetHelper;
import com.sun.identity.agents.common.FQDNHelper;
import com.sun.identity.agents.common.HttpServletRequestHelper;
import com.sun.identity.agents.common.NotenforcedIPHelper;
import com.sun.identity.agents.common.NotenforcedURIHelper;
import com.sun.identity.agents.common.PatternMatcher;
import com.sun.identity.agents.common.ProfileAttributeHelper;
import com.sun.identity.agents.common.SSOTokenValidator;
import com.sun.identity.agents.common.URLFailoverHelper;
import com.sun.identity.agents.filter.AmFilter;
import com.sun.identity.agents.filter.AmFilterMode;
import com.sun.identity.agents.filter.AmFilterModule;
import com.sun.identity.agents.filter.AmJ2EESSOCache;
import com.sun.identity.agents.filter.AmWebSSOCache;
import com.sun.identity.agents.filter.ApplicationLogoutHandler;
import com.sun.identity.agents.filter.AuditResultHandler;
import com.sun.identity.agents.filter.ErrorPageTaskHandler;
import com.sun.identity.agents.filter.FQDNTaskHandler;
import com.sun.identity.agents.filter.FormLoginTaskHandler;
import com.sun.identity.agents.filter.InboundLegacyUserAgentTaskHandler;
import com.sun.identity.agents.filter.LocalAuthTaskHandler;
import com.sun.identity.agents.filter.LocalLogoutTaskHandler;
import com.sun.identity.agents.filter.NotenforcedIPTaskHandler;
import com.sun.identity.agents.filter.NotenforcedListTaskHandler;
import com.sun.identity.agents.filter.NotificationTaskHandler;
import com.sun.identity.agents.filter.OutboundLegacyUserAgentTaskHandler;
import com.sun.identity.agents.filter.PortCheckTaskHandler;
import com.sun.identity.agents.filter.ProfileAttributeTaskHandler;
import com.sun.identity.agents.filter.RedirectCheckResultHandler;
import com.sun.identity.agents.filter.ResponseAttributeTaskHandler;
import com.sun.identity.agents.filter.ResponseHeadersTaskHandler;
import com.sun.identity.agents.filter.SSOContext;
import com.sun.identity.agents.filter.SSOTaskHandler;
import com.sun.identity.agents.filter.SessionAttributeTaskHandler;
import com.sun.identity.agents.filter.URLPolicyTaskHandler;
import com.sun.identity.agents.filter.WSSUsernameTokenAuthenticator;
import com.sun.identity.agents.filter.WebServiceTaskHandler;
import com.sun.identity.agents.log.AmAgentLocalLog;
import com.sun.identity.agents.log.AmAgentLog;
import com.sun.identity.agents.log.AmAgentLogModule;
import com.sun.identity.agents.log.AmAgentRemoteLog;
import com.sun.identity.agents.policy.AmWebPolicy;
import com.sun.identity.agents.policy.AmWebPolicyAppSSOProvider;
import com.sun.identity.agents.policy.AmWebPolicyModule;
import com.sun.identity.agents.realm.AmRealm;
import com.sun.identity.agents.realm.AmRealmMemebershipCache;
import com.sun.identity.agents.realm.AmRealmModule;
import com.sun.identity.agents.realm.AmSDKRealm;



/**
 * The <code>ServiceResolver</code> provides the necessary means to access
 * the read-only configuration information for the Agent runtime. 
 */
public abstract class ServiceResolver {
    
   /**
    * Returns a String array of class names that represent the registered
    * modules in the Agent runtime.
    * 
    * @return a String array of class names that represent the registered 
    * modules in the Agent runtime.
    */
    public String[] getModuleList() {
        
        return new String[] {
                AmAgentLogModule.class.getName(),
                AmWebPolicyModule.class.getName(),
                AmFilterModule.class.getName(),
                AmRealmModule.class.getName()
        };
    }
    
    public String getPatternMatcherImpl() {
        return PatternMatcher.class.getName();
    }
    
    public String getNotenforcedIPHelperImpl() {
        return NotenforcedIPHelper.class.getName();
    }
    
    public String getCookieResetHelperImpl() {
        return CookieResetHelper.class.getName();
    }
    
    public String getNotenforcedURIHelperImpl() {
        return NotenforcedURIHelper.class.getName();
    }
    
    public String getFQDNHelperImpl() {
        return FQDNHelper.class.getName();
    }
    
    public String getHttpServletRequestHelperImpl() {
        return HttpServletRequestHelper.class.getName();
    }
    
    public String getURLFailoverHelperImpl() {
        return URLFailoverHelper.class.getName();
    }
    
    public String getSSOTokenValidatorImpl() {
        return SSOTokenValidator.class.getName();
    }
    
    public String getProfileAttributeHelperImpl() {
        String result = null;
        if (isIDMAvailable()) {
            result = ProfileAttributeHelper.class.getName();
        } else {
            result = AmSDKProfileAttributeHelper.class.getName();
        }
        return result;
    }
    
    public String getApplicationSSOTokenProviderImpl() {
        return ApplicationSSOTokenProvider.class.getName();
    }
    
    public String getAmAgentLocalLogImpl() {
        return AmAgentLocalLog.class.getName();
    }
    
    public String getAmAgentRemoteLogImpl() {
        return AmAgentRemoteLog.class.getName();
    }
    
    public String getAmAgentLogImpl() {
        return AmAgentLog.class.getName();
    }
    
    public String getAmWebPolicyImpl() {
        return AmWebPolicy.class.getName();
    }
    
    public String getAmWebPolicyAppSSOProviderImpl() {
        return AmWebPolicyAppSSOProvider.class.getName();
    }
    
    public String getSSOContextImpl() {
        return SSOContext.class.getName();
    }
    
    public String getAmSSOCacheImpl() {
        String result = null;
        if (isEJBContextAvailable()) {
            result = AmJ2EESSOCache.class.getName();
        } else {
            result = AmWebSSOCache.class.getName();
        }
        return result;
    }

    public abstract String getGlobalJ2EEAuthHandlerImpl();
    
    public abstract boolean getSessionBindingFlag();
    
    public abstract String getGlobalJ2EELogoutHandlerImpl();
    
    public abstract String getGlobalVerificationHandlerImpl();
    
    public String getNotificationTaskHandlerImpl() {
        return NotificationTaskHandler.class.getName();
    }
    
    public String getWebServiceTaskHandlerImpl() {
        return WebServiceTaskHandler.class.getName();
    }
    
    public String getDefaultWebServiceAuthenticatorImpl() {
        return WSSUsernameTokenAuthenticator.class.getName();
    }
    
    public String getPortCheckTaskHandlerImpl() {
        return PortCheckTaskHandler.class.getName();
    }
    
    public String getFQDNTaskHandlerImpl() {
        return FQDNTaskHandler.class.getName();
    }

    public String getInboundLegacyUserAgentTaskHandlerImpl() {
        return InboundLegacyUserAgentTaskHandler.class.getName();
    }
    
    public String getNotenforcedIPTaskHandlerImpl() {
        return NotenforcedIPTaskHandler.class.getName();
    }
    
    public String getNotenforcedListTaskHandlerImpl() {
        return NotenforcedListTaskHandler.class.getName();
    }
    
    public ArrayList getFirstCustomInboundTaskHandlerImpls(AmFilterMode mode) {
        return new ArrayList();
    }
    
    public ArrayList getLastCustomInboundTaskHandlerImpls(AmFilterMode mode) {
        return new ArrayList();
    }
    
    public ArrayList getPrimaryInboundTaskHandlerImpls(AmFilterMode mode) {
        ArrayList result = new ArrayList();
        result.add(getNotificationTaskHandlerImpl());
        return result;
    }
    
    public ArrayList getPreSSOCommonInboundTaskHandlerImpls(AmFilterMode mode) {
        ArrayList result = new ArrayList();
        result.add(getPortCheckTaskHandlerImpl());
        result.add(getFQDNTaskHandlerImpl());
        result.add(getInboundLegacyUserAgentTaskHandlerImpl());
        result.add(getNotenforcedIPTaskHandlerImpl());
        result.add(getNotenforcedListTaskHandlerImpl());
        result.add(getWebServiceTaskHandlerImpl());
        
        return result;
    }
    
    
    public String getSSOTaskHandlerImpl() {
        return SSOTaskHandler.class.getName();
    }
    
    public ArrayList getSSOCommonInboundTaskHandlerImpls(AmFilterMode mode) {
        ArrayList handlers = new ArrayList();
        handlers.add(getSSOTaskHandlerImpl());
        
        return handlers;
    }

    
    public String getApplicationLogoutHandlerImpl() {
        return ApplicationLogoutHandler.class.getName();
    }
    
    public String getProfileAttributeTaskHandlerImpl() {
        return ProfileAttributeTaskHandler.class.getName();
    }
    
    public String getSessionAttributeTaskHandlerImpl() {
        return SessionAttributeTaskHandler.class.getName();
    }
    
    public String getResponseHeadersTaskHandlerImpl() {
        return ResponseHeadersTaskHandler.class.getName();
    }
    
    public ArrayList getPostSSOCommonInboundTaskHandlerImpls(
            AmFilterMode mode) {
        ArrayList handlers = new ArrayList();
        handlers.add(getApplicationLogoutHandlerImpl());
        handlers.add(getProfileAttributeTaskHandlerImpl());
        handlers.add(getSessionAttributeTaskHandlerImpl());
        handlers.add(getResponseHeadersTaskHandlerImpl());
        
        return handlers;
    }
    
    public ArrayList getCommonInboundTaskHandlers(AmFilterMode mode) 
    {
        ArrayList handlers = new ArrayList();        
        if (!mode.equals(AmFilterMode.MODE_NONE)) {
            handlers.addAll(getPreSSOCommonInboundTaskHandlerImpls(mode));
            handlers.addAll(getSSOCommonInboundTaskHandlerImpls(mode));
            handlers.addAll(getPostSSOCommonInboundTaskHandlerImpls(mode));
            }
        
        return handlers;
    }
    
    public ArrayList getFilterInboundTaskHandlerImpls(AmFilterMode mode) {
        ArrayList handlers = new ArrayList();
        handlers.addAll(getFirstCustomInboundTaskHandlerImpls(mode));
        handlers.addAll(getPrimaryInboundTaskHandlerImpls(mode));
        handlers.addAll(getCommonInboundTaskHandlers(mode));
        switch(mode.getIntValue()) {
                case AmFilterMode.INT_MODE_J2EE_POLICY:
                    handlers.addAll(getJ2EEPolicyTaskHandlerImpls(mode));
                    break;
                case AmFilterMode.INT_MODE_URL_POLICY:
                    handlers.addAll(getURLPolicyTaskHandlerImpls(mode));
                    break;
                case AmFilterMode.INT_MODE_ALL:
                    handlers.addAll(getJ2EEPolicyTaskHandlerImpls(mode));
                        handlers.addAll(getURLPolicyTaskHandlerImpls(mode));
                    break;
        }
        handlers.addAll(getLastCustomInboundTaskHandlerImpls(mode));
        return handlers;
    }
        
    public String getErrorPageTaskHandlerImpl() {
        return ErrorPageTaskHandler.class.getName();
    }
    
    public String getLocalLogoutTaskHandlerImpl() {
        return LocalLogoutTaskHandler.class.getName();
    }

    public String getFormLoginTaskHandlerImpl() {
        return FormLoginTaskHandler.class.getName();
    }
    
    public String getLocalAuthTaskHandlerImpl() {
        return LocalAuthTaskHandler.class.getName();
    }
    
    public ArrayList getJ2EEPolicyTaskHandlerImpls(AmFilterMode modeboolean) 
    {
        ArrayList handlers = new ArrayList();
        handlers.add(getErrorPageTaskHandlerImpl());
        handlers.add(getLocalLogoutTaskHandlerImpl());
        handlers.add(getFormLoginTaskHandlerImpl());
        handlers.add(getLocalAuthTaskHandlerImpl());

        return handlers;
    }
    
    public String getURLPolicyTaskHandlerImpl() {
        return URLPolicyTaskHandler.class.getName();
    }
    
    public String getResponseAttributeTaskHandlerImpl() {
        return ResponseAttributeTaskHandler.class.getName();
    }
    
    public ArrayList getURLPolicyTaskHandlerImpls(AmFilterMode mode) 
    {
        ArrayList handlers = new ArrayList();
            handlers.add(getURLPolicyTaskHandlerImpl());
        handlers.add(getResponseAttributeTaskHandlerImpl());
        return handlers;
    }

    public String getRedirectCheckResultHandlerImpl() {
        return RedirectCheckResultHandler.class.getName();
    }
    
    public String getAuditResultHandlerImpl() {
        return AuditResultHandler.class.getName();
    }
    
    public ArrayList getFirstCustomResultHandlerImpls(AmFilterMode mode) {
        return new ArrayList();
    }
    
    public ArrayList getLastCustomResultHandlerImpls(AmFilterMode mode) {
        return new ArrayList();
    }
    
    public ArrayList getFilterResultHandlerImpls(AmFilterMode mode) {        
        ArrayList handlers = new ArrayList();
        handlers.addAll(getFirstCustomResultHandlerImpls(mode));
        handlers.add(getRedirectCheckResultHandlerImpl());
        handlers.add(getAuditResultHandlerImpl());
        handlers.addAll(getLastCustomResultHandlerImpls(mode));
        return handlers;
    }
    
    public String getOutboundLegacyUserAgentTaskHandlerImpl() {
        return OutboundLegacyUserAgentTaskHandler.class.getName();
    }
    
    public ArrayList getFirstCustomSelfRedirectTaskHandlerImpls(
            AmFilterMode mode)
    {
        return new ArrayList();
    }
    
    public ArrayList getLastCustomSelfRedirectTaskHandlerImpls(
            AmFilterMode mode)
    {
        return new ArrayList();
    }
    
    public ArrayList getFilterSelfRedirectTaskHandlerImpls(
            AmFilterMode mode) 
    {
        ArrayList handlers = new ArrayList();
        handlers.addAll(getFirstCustomSelfRedirectTaskHandlerImpls(mode));
        if (!mode.equals(AmFilterMode.MODE_NONE)) {
            handlers.add(getOutboundLegacyUserAgentTaskHandlerImpl());
        }
        handlers.addAll(getLastCustomSelfRedirectTaskHandlerImpls(mode));
        return handlers;
    }
    
    public String getAmFilterImpl() {
        return AmFilter.class.getName();
    }
    
    public String getAmRealmImpl() {
        return (isIDMAvailable()) ? 
           AmRealm.class.getName() : AmSDKRealm.class.getName();
    }
    
    public String getAmRealmMembershipCacheImpl() {
        return AmRealmMemebershipCache.class.getName();
    }
    
    public String getCryptImpl() {
            return (isIDMAvailable()) ?
            AM70Crypt.class.getName() : AM63Crypt.class.getName(); 
    }
    
    protected boolean isIDMAvailable() {
        return _idmAvailable;
    }
    
    protected boolean isEJBContextAvailable() {
        return _ejbContextAvailable;
    }
    
    
    private static boolean _idmAvailable = false;
    private static boolean _ejbContextAvailable = false;
    
    static {
        try {
            Class c = Class.forName("com.sun.identity.idm.AMIdentity");
            if (c != null) {
                _idmAvailable = true;
            }
        } catch (Exception ex) {
            // No handling required
        }
        
        try {
            Class c = Class.forName("javax.ejb.EJBContext");
            if (c != null) {
                _ejbContextAvailable = true;
            }
        } catch (Exception ex) {
            // No handling required
        }
    }
}
