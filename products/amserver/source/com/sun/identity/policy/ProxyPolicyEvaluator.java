/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2006 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: ProxyPolicyEvaluator.java,v 1.2 2008-06-25 05:43:44 qcheng Exp $
 *
 */



package com.sun.identity.policy;
import com.iplanet.am.sdk.AMException;
import com.iplanet.am.sdk.AMUser;
import com.iplanet.am.sdk.AMStoreConnection;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;

import com.sun.identity.authentication.server.AuthContextLocal;
import com.sun.identity.authentication.service.AuthException;
import com.sun.identity.authentication.service.AuthUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.login.LoginException;

import netscape.ldap.util.DN;

/**
 * Class that lets a priviliged user to compute policy results for 
 * another user.
 * Only privileged users can get <code>ProxyPolicyEvaluator</code>
 *  - only top level administrator, realm level policy administrator, 
 * realm administrator or realm policy administrator can get
 * <code>ProxyPolicyEvaluator</code>. Top level administrator can compute policy
 * results for any user. Realm administrator or policy administrator can
 * compute policy results only for users who are members of the realm
 * (including sub realm) that they manage. If they try to compute policys
 * result for any other user, they would get a <code>PolicyException</code>.
 * This class can be used only within the web container running policy server.
 *
 * @supported.all.api
 */
public class ProxyPolicyEvaluator {

    private static final String TOP_LEVEL_ADMIN_ROLE 
            = "cn=Top-level Admin Role,";

    private static final String TOP_LEVEL_POLICY_ADMIN_ROLE 
            = "cn=Top-level Policy Admin Role,";
    private static final String ORG_ADMIN_ROLE 
            = "cn=Organization Admin Role,";
    private static final String ORG_POLICY_ADMIN_ROLE 
            = "cn=Organization Policy Admin Role,";

    private SSOToken adminToken;
    private AMUser adminUser;
    private String serviceType;
    private PolicyEvaluator policyEvaluator;
    private static String baseDNString;
    private static DN baseDN;

    static {
        baseDNString = com.sun.identity.sm.ServiceManager.getBaseDN();
        baseDN= new DN(baseDNString);
    }

    /**
     * Constructs a <code>ProxyPolicyEvaluator</code> instance.
     * Only privileged users can create <code>ProxyPolicyEvaluator</code>.
     *
     * @param token single sign on token used to construct the proxy policy
     *        evaluator.
     * @param serviceType service type for which construct the proxy policy 
     *                    evaluator 
     * @throws NoPermissionException if the token does not have privileges 
     *         to create proxy policy evaluator
     * @throws NameNotFoundException if the serviceType is not found in
     *         registered service types
     * @throws PolicyException any policy exception coming from policy 
     *                         framework
     * @throws SSOException if the token is invalid
     */
    ProxyPolicyEvaluator(SSOToken token, String serviceType) 
        throws NoPermissionException, NameNotFoundException, 
        PolicyException, SSOException 
    {
        SSOTokenManager.getInstance().validateToken(token);
        this.adminToken = token;
        this.serviceType = serviceType;
        AMStoreConnection conn = new AMStoreConnection(adminToken);
        this.adminUser = conn.getUser(adminToken.getPrincipal().getName());
        this.policyEvaluator 
                = PolicyEvaluatorFactory.getInstance()
                .getPolicyEvaluator(token, serviceType);
    }

    /**
     * Evaluates a simple privilege of boolean type. The privilege indicates
     * if the user identified by the <code>principalName</code> 
     * can perform specified action on the specified resource.
     *
     * @param principalName principal name for whom to compute the privilege.
     * @param resourceName name of the resource for which to compute 
     *                     policy result.
     * @param actionName name of the action the user is trying to perform on
     * the resource
     * @param env run time environment parameters
     *
     * @return the result of the evaluation as a boolean value
     *
     * @throws PolicyException exception form policy framework 
     * @throws SSOException if single sign on token is invalid
     * 
     */
    public boolean isAllowed(String principalName, String resourceName, 
        String actionName, Map env) throws PolicyException, SSOException 
    {
        SSOToken token = getProxyToken(principalName);
        boolean allowed = policyEvaluator.isAllowed(token, resourceName,
                actionName, env);
        return allowed;
    }

    /**
     * Gets policy decision for the user identified by the
     * <code>principalName</code> for the given resource
     *
     * @param principalName principal name for whom to compute the policy 
     *                      decision
     * @param resourceName name of the resource for which to compute policy 
     *                      decision
     * @param env run time environment parameters
     *
     * @return the policy decision for the principal for the given resource
     *
     * @throws PolicyException exception form policy framework
     * @throws SSOException if single sign on token is invalid
     * 
     */
    public PolicyDecision getPolicyDecision(String principalName, 
        String resourceName, Map env) 
        throws PolicyException, SSOException 
    {
        SSOToken token = getProxyToken(principalName);
        PolicyDecision pd = policyEvaluator.getPolicyDecision(
                token, resourceName, null, env); //null actionNames
        // Let us log all policy evaluation results
        if (PolicyUtils.logStatus) {
            String decision = pd.toString();
            if (decision != null && decision.length() != 0) {
                String[] objs = { adminToken.getPrincipal().getName(), 
                                principalName, resourceName,
                                decision };
                PolicyUtils.logAccessMessage("PROXIED_POLICY_EVALUATION",
                            objs, adminToken);
            }
        }

        if (PolicyManager.debug.messageEnabled()) {
            PolicyManager.debug.message(
                    " Admin: " + adminToken.getPrincipal().getName()
                    + " got policy decision "
                    + " for principal: " + token.getPrincipal().getName() 
                    + " for resourceName:" + resourceName
                    + " for serviceType :" + serviceType
                    + " is " + pd);
        }

        return pd;
    }

    /**
     * Gets policy decision for a resource, skipping subject evaluation. 
     * Conditions would be evaluated and would include applicable advices 
     * in policy decisions. Hence, you could get details such as
     * <code>AuthLevel</code>, <code>AuthScheme</code> that would be required to
     * access the resource.  
     *
     * @param resourceName name of the resource for which to compute policy 
     *                      decision
     * @param actionNames names of the actions the user is trying to perform on
     *                   the resource
     *
     * @param env run time environment parameters
     *
     * @return the policy decision for the principal for the given resource
     *
     * @throws PolicyException exception form policy framework
     * @throws SSOException if single sign on token is invalid
     * 
     */
    public PolicyDecision getPolicyDecisionIgnoreSubjects(String resourceName, 
            Set actionNames, Map env) throws PolicyException, SSOException 
    {
        PolicyDecision pd = policyEvaluator.getPolicyDecisionIgnoreSubjects(
                resourceName, actionNames, env); 
        // Let us log all policy evaluation results
        if (PolicyUtils.logStatus) {
            String decision = pd.toString();
            if (decision != null && decision.length() != 0) {
                String[] objs =
                    {adminToken.getPrincipal().getName(), resourceName,
                        decision};
                PolicyUtils.logAccessMessage(
                     "PROXIED_POLICY_EVALUATION_IGNORING_SUBJECTS", 
                      objs, adminToken);
            }
        }

        if (PolicyManager.debug.messageEnabled()) {
            PolicyManager.debug.message(
                    " Admin: " + adminToken.getPrincipal().getName()
                    + " got policy decision "
                    + " ignoring subjects "
                    + " for resourceName:" + resourceName
                    + " for serviceType :" + serviceType
                    + " is " + pd);
        }

        return pd;
    }

    /**
     * Gets protected resources for a user identified by the
     * <code>principalName</code>.  Conditions defined  in the policies are
     * ignored while computing protected resources. 
     * Only resources that are  sub resources of the  given 
     * <code>rootResource</code> or equal to the given <code>rootResource</code>
     * would be returned.
     * If all policies applicable to a resource are 
     * only referral policies, no <code>ProtectedResource</code> would be
     * returned for such a resource.
     *
     * @param principalName principal name for whom to compute the privilege.
     * @param rootResource  only resources that are sub resources of the  
     *                      given <code>rootResource</code> or equal to the
     *                      given <code>rootResource</code> would be returned.
     *                      If <code>PolicyEvaluator.ALL_RESOURCES</code> is 
     *                      passed as <code>rootResource</code>, resources under
     *                      all root  resources of the service 
     *                      type are considered while computing protected 
     *                      resources.
     *
     * @return set of protected resources. The set contains
     *         <code>ProtectedResource</code> objects. 
     *
     * @throws PolicyException exception form policy framework
     * @throws SSOException if single sign on token is invalid
     * @see ProtectedResource
     * 
     */
    public Set getProtectedResourcesIgnoreConditions(String principalName, 
        String rootResource)  throws PolicyException, SSOException 
    {
        SSOToken token = getProxyToken(principalName);
        return policyEvaluator.getProtectedResourcesIgnoreConditions(
                token, rootResource);
    }

    /**
     * Gets proxy session token
     * @param principalName user to proxy as
     * @return proxy session token
     * @throws PolicyException if proxy session token can not be obtained
     * @throws SSOException if the session token of the administrative user
     * is invalid
     */
    private SSOToken getProxyToken(String principalName) 
        throws PolicyException, SSOException 
    {
        SSOTokenManager.getInstance().validateToken(adminToken);
        SSOToken token = null;
        boolean proxyPermission = false;

        try {
            AuthContextLocal ac = AuthUtils.getAuthContext(baseDNString);
            ac.login(
                com.sun.identity.authentication.AuthContext.IndexType.USER, 
                    principalName, true);
            token = ac.getSSOToken();
        } catch (AuthException ae) {
            throw new PolicyException(ae);
        } catch (LoginException le) {
            throw new PolicyException(le);
        } 
        if (token ==  null) {
            throw new SSOException(new PolicyException(ResBundleUtils.rbName,
                    "can_not_get_proxy_sso_token", null, null));
        }

        try {
            AMStoreConnection conn = new AMStoreConnection(adminToken);
            AMUser user 
                    = conn.getUser(token.getPrincipal().getName());
            String userOrg = user.getOrganizationDN();
            DN userOrgDN = new DN(userOrg);
            Set adminRoles = adminUser.getRoleDNs();
            String roleOrg = null;
            DN roleOrgDN = null;
            Iterator roleIter = adminRoles.iterator();
            while (roleIter.hasNext()) {
                String role = (String)roleIter.next();
                if (role.startsWith(TOP_LEVEL_ADMIN_ROLE)) {
                    roleOrg =
                            role.substring(
                            TOP_LEVEL_ADMIN_ROLE.length());
                    roleOrgDN = new DN(roleOrg);
                    if (roleOrgDN.equals(baseDN)) {
                        proxyPermission = true;
                        break;
                    }
                } else if (role.startsWith(TOP_LEVEL_POLICY_ADMIN_ROLE)) {
                    roleOrg =
                            role.substring(
                            TOP_LEVEL_POLICY_ADMIN_ROLE.length());
                    roleOrgDN = new DN(roleOrg);
                    if (roleOrgDN.equals(baseDN)) {
                        proxyPermission = true;
                        break;
                    }
                } else if (role.startsWith(ORG_ADMIN_ROLE)) {
                    roleOrg =
                            role.substring(
                            ORG_ADMIN_ROLE.length());
                    roleOrgDN = new DN(roleOrg);
                    if (userOrgDN.equals(roleOrgDN) 
                                || userOrgDN.isDescendantOf(roleOrgDN)) {
                        proxyPermission = true;
                        break;
                    }
                } else if (role.startsWith(ORG_POLICY_ADMIN_ROLE)) {
                    roleOrg =
                            role.substring(
                            ORG_POLICY_ADMIN_ROLE.length());
                    roleOrgDN = new DN(roleOrg);
                    if (userOrgDN.equals(roleOrgDN) 
                                || userOrgDN.isDescendantOf(roleOrgDN)) {
                        proxyPermission = true;
                        break;
                    }
                }
            }
            if (!proxyPermission) {
                SSOTokenManager.getInstance().destroyToken(token);
                if (PolicyManager.debug.warningEnabled()) {
                    PolicyManager.debug.warning("Admin : " 
                            + adminToken.getPrincipal().getName()
                            + " can not create proxy sso token for user "
                            + principalName);

                }
                throw new PolicyException(ResBundleUtils.rbName,
                        "no_permission_to_create_proxy_sso_token", null, null);
            }
                
        } catch(AMException ae) {
            throw new PolicyException(ae);
        }
        return token;
    }

}
