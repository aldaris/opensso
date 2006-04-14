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
 * $Id: IdUtils.java,v 1.7 2006-04-14 09:06:38 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.idm;

import java.security.AccessController;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import netscape.ldap.util.DN;

import com.iplanet.am.sdk.AMDirectoryManager;
import com.iplanet.am.sdk.AMDirectoryWrapper;
import com.iplanet.am.sdk.AMException;
import com.iplanet.am.sdk.AMObject;
import com.iplanet.am.sdk.AMOrganization;
import com.iplanet.am.sdk.AMStoreConnection;
import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.common.CaseInsensitiveHashMap;
import com.sun.identity.common.Constants;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.DNMapper;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;
import com.sun.identity.sm.ServiceManager;
import com.sun.identity.sm.ServiceSchemaManager;

/**
 * The class defines some static utilities used by other components like policy
 * and auth
 *
 * @supported.api
 */
public final class IdUtils {
    private static Debug debug = AMIdentityRepository.debug;

    private static Map mapSupportedTypes = new CaseInsensitiveHashMap(10);

    public static Set supportedTypes = new HashSet();

    private static Map mapTypesToServiceNames = new CaseInsensitiveHashMap();

    protected static Map typesCanBeMemberOf = new CaseInsensitiveHashMap();

    protected static Map typesCanHaveMembers = new CaseInsensitiveHashMap();

    protected static Map typesCanAddMembers = new CaseInsensitiveHashMap();

    // Static map to cache "orgIdentifier" and organization DN
    private static Map orgIdentifierToOrgName = new CaseInsensitiveHashMap();

    // ServiceConfigManager for sunidentityrepository service
    private static ServiceConfigManager serviceConfigManager;

    private static String notificationId;

    static {
        initialize();
    }

    protected static void initialize() {
        if (ServiceManager.isConfigMigratedTo70()) {
            // IdRepo service schema exists. Read the supported
            // entities from there
            try {
                SSOToken adminToken = (SSOToken) AccessController
                        .doPrivileged(AdminTokenAction.getInstance());
                serviceConfigManager = new ServiceConfigManager(adminToken,
                        IdConstants.REPO_SERVICE, "1.0");
                ServiceConfig ss = serviceConfigManager.getGlobalConfig(null);
                Set typeSchemaNames = ss.getSubConfigNames("*",
                        IdConstants.SUPPORTED_TYPES);
                if (typeSchemaNames == null || typeSchemaNames.isEmpty()) {
                    loadDefaultTypes();
                } else {
                    Iterator it = typeSchemaNames.iterator();
                    while (it.hasNext()) {
                        String typeSchema = (String) it.next();
                        IdType idType = new IdType(typeSchema);
                        supportedTypes.add(idType);
                        mapSupportedTypes.put(idType.getName(), idType);
                        ServiceConfig tsc = ss.getSubConfig(typeSchema);
                        Map attributes = tsc.getAttributes();
                        Set serviceNameSet = (Set) attributes
                                .get(IdConstants.SERVICE_NAME);
                        Set canBeMembersOf = (Set) attributes
                                .get(IdConstants.ATTR_MEMBER_OF);
                        Set canHaveMembers = (Set) attributes
                                .get(IdConstants.ATTR_HAVE_MEMBERS);
                        Set canAddMembers = (Set) attributes
                                .get(IdConstants.ATTR_ADD_MEMBERS);
                        if (serviceNameSet != null && !serviceNameSet.isEmpty())
                        {
                            mapTypesToServiceNames.put(typeSchema,
                                    serviceNameSet.iterator().next());
                        }
                        if (canBeMembersOf != null && !canBeMembersOf.isEmpty())
                        {
                            Set memberOfSet = getMemberSet(canBeMembersOf);
                            typesCanBeMemberOf.put(typeSchema, memberOfSet);
                        }
                        if (canHaveMembers != null && !canHaveMembers.isEmpty())
                        {
                            Set memberSet = getMemberSet(canHaveMembers);
                            typesCanHaveMembers.put(typeSchema, memberSet);
                        }
                        if (canAddMembers != null && !canAddMembers.isEmpty()) {
                            Set memberSet = getMemberSet(canAddMembers);
                            typesCanAddMembers.put(typeSchema, memberSet);
                        }
                    }
                }
            } catch (SMSException e) {
                debug.error("IdUtils: Loading default types. " +
                        "Caught exception..", e);
                loadDefaultTypes();
            } catch (SSOException ssoe) {
                debug.error(
                        "IdUtils: Loading default types. Caught exception..",
                        ssoe);
                loadDefaultTypes();
            }
        } else {
            loadDefaultTypes();
        }

        // Register for SMS notifications to root realm
        if (notificationId == null) {
            try {
                SSOToken adminToken = (SSOToken) AccessController
                    .doPrivileged(AdminTokenAction.getInstance());
                OrganizationConfigManager rootOrg =
                    new OrganizationConfigManager(adminToken, "/");
                notificationId = rootOrg.addListener(new IdUtilsListener());
            } catch (SMSException e) {
                debug.error("IdUtils: Register notification:exception", e);
            }
        }
    }

    /**
     * Returns a handle of the Identity object based on
     * the SSO Token passed in (<code>AMIdentity</code> object of the user
     * who is authenticated).
     * 
     * @param token
     *            Single sign on token of user.
     * @return Identity object.
     * @throws IdRepoException
     *             if there are repository related error conditions.
     * @throws SSOException
     *             if user's single sign on token is invalid.
     *
     * @supported.api
     */
    public static AMIdentity getIdentity(SSOToken token)
            throws IdRepoException, SSOException {
        String principal = token.getProperty(Constants.UNIVERSAL_IDENTIFIER);
        if (principal == null) {
            String dn = token.getPrincipal().getName();
            if (dn == null || !DN.isDN(dn)) {
                Object[] args = { principal };
                throw new IdRepoException(IdRepoBundle.getString("215", args),
                        "215", args);
            } else {
                principal = "id=" + ((new DN(dn))).explodeDN(true)[0] + ",ou="
                        + IdType.USER.getName() + ","
                        + token.getProperty(Constants.ORGANIZATION)
                        + ",amsdkdn=" + dn;

            }
        }
        return getIdentity(token, principal);
    }

    /**
     * Returns a string which uniquely represents this identity object.
     * 
     * @param id
     *            <code>AMIdentity</code> object whose string represenation is
     *            needed.
     * @return universal identifier of <code>id</code>.
     *
     * @supported.api
     */
    public static String getUniversalId(AMIdentity id) {
        return id.getUniversalId();
    }

    /**
     * Returns an <code>AMIdentity</code> object, if
     * provided with a string identifier for the object.
     * 
     * @param token
     *            Single sign on token of the user.
     * @param univId
     *            String represenation of the identity.
     * @return Identity object
     * @throws IdRepoException
     *             if the identifier provided is wrong.
     *
     * @supported.api
     */
    public static AMIdentity getIdentity(SSOToken token, String univId)
            throws IdRepoException {
        if (univId.startsWith("id=")) {
            // This is a universal id...
            String pureId = univId;
            String amsdkDN = null;
            if (!DN.isDN(univId)) {
                Object[] args = { univId };
                throw new IdRepoException(
                        IdRepoBundle.BUNDLE_NAME, "215", args);
            }
            int dnIndex = univId.indexOf(",amsdkdn=");
            if (dnIndex > 0) {
                pureId = univId.substring(0, dnIndex);
                amsdkDN = univId.substring(dnIndex + 9);
            }
            DN dnObject = new DN(pureId);
            String[] array = dnObject.explodeDN(true);
            String name = array[0];
            if (!supportedType(array[1])) {
                Object[] args = { univId };
                throw new IdRepoException(
                        IdRepoBundle.BUNDLE_NAME, "215", args);
            }
            IdType type = new IdType(array[1]);
            String orgName = dnObject.getParent().getParent().toRFCString();
            return new AMIdentity(token, name, type, orgName, amsdkDN);
        } else {
            // this could be a AM SDK DN.
            return getIdentity(token, univId, DNMapper.orgNameToDN("/"));
        }
    }

    /**
     * Returns an <code>AMIdentity</code> object, given the DN of an
     * authenticated identity, realm name and identity type. This interface is
     * mainly for authentication component to get back the identity of the user.
     * 
     * @param token
     *            SSOToken of the administrator
     * @param amsdkdn
     *            DN of the authenticated user
     * @param realm
     *            realm name where the user was authenticated
     * @return Identity object or <code>null</code>
     * @throws IdRepoException
     *             if the underly components throws exception while obtaining
     *             the identity object
     */
    public static AMIdentity getIdentity(SSOToken token, String amsdkdn,
            String realm) throws IdRepoException {

        if (amsdkdn == null || !DN.isDN(amsdkdn)) {
            // Invalid universal id
            return (null);
        }

        try {
            // Check if the DN is a valid AMSDK DN
            if (ServiceManager.isCoexistenceMode()) {
                // In coexistence/legacy mode, AM SDK is not configured but
                // is always enabled by default
                AMIdentity iden = getIdentityFromAMSDKDN(token, amsdkdn, realm);
                return iden;
            }
            // Check if AMSDK is configured for the realm
            ServiceConfig s = serviceConfigManager.getOrganizationConfig(realm,
                    null);
            if (s != null) {
                Iterator items = s.getSubConfigNames().iterator();
                while (items.hasNext()) {
                    ServiceConfig sc1 = s.getSubConfig((String) items.next());
                    if (sc1.getSchemaID().equalsIgnoreCase("amSDK")) {
                        return getIdentityFromAMSDKDN(token, amsdkdn, realm);
                    }
                }
            }
        } catch (AMException ame) {
            // Debug the message and return null
            if (debug.messageEnabled()) {
                debug.message("IdUtils.getIdentity: Unable to resolve "
                        + "AMSDK DN", ame);
            }
        } catch (SSOException ssoe) {
            // Debug the message and return null
            if (debug.messageEnabled()) {
                debug.message("IdUtils.getIdentity: Unable to resolve "
                        + "AMSDK DN. Got SSOException", ssoe);
            }
        } catch (IdRepoException ide) {
            // Debug the message and return null
            if (debug.messageEnabled()) {
                debug.message("IdUtils.getIdentity: Unable to resolve "
                        + "AMSDK DN. Got IdRepoException", ide);
            }
        } catch (SMSException smse) {
            // Debug the message and return null
            if (debug.messageEnabled()) {
                debug.message("IdUtils.getIdentity: Unable to resolve "
                        + "AMSDK DN. Got SMSException", smse);
            }
        }
        return (null);
    }

    /**
     * Returns the name of service which defines the profile information for
     * this type. Returns null, if nothing is defined.
     * 
     * @param type
     *            IdType whose service name is needed.
     * @return Name of the service.
     */
    public static String getServiceName(IdType type) {
        return (String) mapTypesToServiceNames.get(type.getName());
    }

    /**
     * Returns corresponding <code>IdType</code> object given a type.
     * 
     * @param typeType
     *            of object to return.
     * @throws IdRepoException
     *             if there are no corresponding types.
     */
    public static IdType getType(String type) throws IdRepoException {
        IdType returnType = (IdType) mapSupportedTypes.get(type);
        if (returnType == null) {
            Object args[] = { type };
            throw new IdRepoException(IdRepoBundle.BUNDLE_NAME, "217", args);
        }
        return returnType;
    }

    /**
     * Returns the matching DN from the AM SDK for this entry. This utility is
     * required by auth.
     * 
     * @param id
     *            <code>AMIdentity</code> object.
     * @return <code>DN</code> of the object, as represented in the datastore.
     */
    public static String getDN(AMIdentity id) {
        if (id.getDN() != null) {
            return id.getDN();
        } else {
            return id.getUniversalId();
        }
    }

    /**
     * Returns an organization which maps to the identifier used by application
     * in order to.
     * 
     * @param orgIdentifier
     *            Organization identifier
     * @return Organization mapping to that identifier.
     *
     * @supported.api
     */
    public static String getOrganization(SSOToken token, String orgIdentifier)
            throws IdRepoException, SSOException {
        // Check in cache first
        String id = null;
        if ((id = (String) orgIdentifierToOrgName.get(orgIdentifier)) != null) {
            return (id);
        }

        // Compute the organization name
        if (debug.messageEnabled()) {
            debug.message("IdUtils:getOrganization Input orgname: "
                    + orgIdentifier);
        }
        if (orgIdentifier == null || orgIdentifier.length() == 0
                || orgIdentifier.equals("/")) {
            // Return base DN
            id = DNMapper.orgNameToDN("/");
        } else if (orgIdentifier.startsWith("/")) {
            // If orgIdentifier is in "/" format covert to DN and return
            id = DNMapper.orgNameToDN(orgIdentifier);
        } else if (DN.isDN(orgIdentifier)) {
            id = orgIdentifier;
        } else if (ServiceManager.isCoexistenceMode()) {
            // Return the org DN as determined by AMStoreConnection
            if (debug.messageEnabled()) {
                debug.message("IdUtils.getOrganization: getting from AMSDK");
            }
            try {
                AMStoreConnection amsc = new AMStoreConnection(token);
                id = amsc.getOrganizationDN(orgIdentifier, null);
            } catch (AMException ame) {
                if (debug.messageEnabled()) {
                    debug.message("IdUtils.getOrganization Exception in "
                            + "getting org name from AMSDK", ame);
                }
                throw convertAMException(ame);
            }
        } else {
            // Get the realm name from SMS
            if (debug.messageEnabled()) {
                debug.message(
                        "IdUtils.getOrganization: getting from SMS realms");
            }
            try {
                ServiceManager sm = new ServiceManager(token);
                // First search for realms with orgIdentifier name
                OrganizationConfigManager ocm = sm
                        .getOrganizationConfigManager("/");
                Set subOrgNames = ocm.getSubOrganizationNames(orgIdentifier,
                        true);
                if (subOrgNames != null && !subOrgNames.isEmpty()) {
                    int count = subOrgNames.size();
                    if (count == 1) {
                        id = DNMapper.orgNameToDN((String) subOrgNames
                                .iterator().next());
                    } else
                        for (Iterator items = subOrgNames.iterator(); items
                                .hasNext();) {
                            // check for orgIdentifier
                            String subRealmName = (String) items.next();
                            StringTokenizer st = new StringTokenizer(
                                    subRealmName, "/");
                            while (st.hasMoreTokens()) {
                                if (st.nextToken().equalsIgnoreCase(
                                        orgIdentifier)) {
                                    id = DNMapper.orgNameToDN(subRealmName);
                                    break;
                                }
                            }
                        }
                }

                // Check if organization name has been determined
                if (id == null) {
                    // perform organization alias search
                    Set vals = new HashSet();
                    vals.add(orgIdentifier);
                    Set orgAliases = sm.searchOrganizationNames(
                            IdConstants.REPO_SERVICE,
                            IdConstants.ORGANIZATION_ALIAS_ATTR, vals);
                    if (orgAliases == null || orgAliases.isEmpty()) {
                        if (debug.messageEnabled()) {
                            debug.message("IdUtils.getOrganization Unable to " +
                                    "find Org name for: " + orgIdentifier);
                        }
                        Object[] args = { orgIdentifier };
                        throw new IdRepoException(IdRepoBundle.BUNDLE_NAME,
                                "401", args);
                    }
                    String tmpS = (String) orgAliases.iterator().next();
                    id = DNMapper.orgNameToDN(tmpS);
                }
            } catch (SMSException smse) {
                // debug message here.
                if (debug.messageEnabled()) {
                    debug.message("IdUtils.getOrganization Exception in "
                            + "getting org name from SMS", smse);
                }
                Object[] args = { orgIdentifier };
                throw new IdRepoException(
                        IdRepoBundle.BUNDLE_NAME, "401", args);
            }
        }

        if (debug.messageEnabled()) {
            debug.message("IdUtils: Returning base DN: " + id);
        }

        // Add to cache and return id
        orgIdentifierToOrgName.put(orgIdentifier, id);
        return id;
    }

    /**
     * Clears the cache containing orgIdentifiers to organization names
     */
    protected static void clearOrganizationNamesCache() {
        orgIdentifierToOrgName = new CaseInsensitiveHashMap();
        if (debug.messageEnabled()) {
            debug.message("IdUtils.clearOrganizationNamesCache called");
        }
    }

    /**
     * Returs true or false, depending on if this organization is enabled or
     * not. The organization string passed to this method should be an
     * identifier returned from the method
     * <code> IdUtils.getOrganization </code>. In the default mode, where
     * relams are enabled but backward comaptibility is required, this checks
     * for organization status in the AM enabled Sun DS. Otherwise, it checks
     * for organization status from the realms tree.
     * 
     * @param token
     * @param org
     * @return
     * @throws IdRepoException
     * @throws SSOException
     */
    public static boolean isOrganizationActive(SSOToken token, String org)
            throws IdRepoException, SSOException {
        boolean isActive = true;
        // Need to initialize ServiceManager by creating the constructor
        if (!ServiceManager.isCoexistenceMode()) {
            // Pick it up from the realms tree.
            try {
                OrganizationConfigManager ocm = new OrganizationConfigManager(
                        token, org);
                if (ocm == null) {
                    Object[] args = { org };
                    throw new IdRepoException(IdRepoBundle.BUNDLE_NAME, "401",
                            args);
                }
                Map attributes = ocm.getAttributes(IdConstants.REPO_SERVICE);
                Set vals = (Set) attributes
                        .get(IdConstants.ORGANIZATION_STATUS_ATTR);
                if (vals == null || vals.isEmpty()) {
                    isActive = true;
                } else {
                    String stringActive = (String) vals.iterator().next();
                    isActive = stringActive.equalsIgnoreCase("Active");
                }
            } catch (SMSException smse) {
                Object args[] = { org };
                throw new IdRepoException(
                        IdRepoBundle.BUNDLE_NAME, "401", args);
            }
        } else {
            // Return the org DN as determined by AMStoreConnection.
            try {
                AMStoreConnection amsc = new AMStoreConnection(token);
                AMOrganization orgObj = amsc.getOrganization(org);
                isActive = orgObj.isActivated();
            } catch (AMException ame) {
                throw convertAMException(ame);
            }
        }
        return isActive;
    }

    /**
     * Private method to return AMIdentity object for an AM SDK DN. Throws all
     * exceptions since the method calling this one takes care of catching them
     * and behaving appropriately
     * 
     * @param token
     * @param amsdkdn
     * @param realm
     * @return
     * @throws AMException
     * @throws SSOException
     * @throws IdRepoException
     */
    private static AMIdentity getIdentityFromAMSDKDN(SSOToken token,
            String amsdkdn, String realm) throws AMException, SSOException,
            IdRepoException {
        // Since we would using AMSDK, get AMDirectoryManager preload
        // all the attributes and check if it exists
        AMDirectoryManager amdm = AMDirectoryWrapper.getInstance();
        amdm.getAttributes(token, amsdkdn, AMObject.USER);
        // Getting object type would use the cached attributes
        int sdkType = amdm.getObjectType(token, amsdkdn);
        // Convert the sdkType to IdRepo type
        IdType type = getType(AMStoreConnection.getObjectName(sdkType));
        String name = (new DN(amsdkdn)).explodeDN(true)[0];
        if (ServiceManager.isCoexistenceMode()) {
            // Get the organization from the object dn
            realm = amdm.getOrganizationDN(token, amsdkdn);
        }
        return (new AMIdentity(token, name, type, realm, amsdkdn));
    }

    /**
     * Non-javadoc, non-public methods
     * 
     * @param ame
     * @return
     */
    public static IdRepoException convertAMException(AMException ame) {
        Object[] args = ame.getMessageArgs();
        String eCode = ame.getErrorCode();
        IdRepoException ide = null;
        if (args == null) {
            ide = new IdRepoException("amProfile", eCode, null);
        } else {
            ide = new IdRepoException("amProfile", ame.getErrorCode(), args);
        }
        ide.setLDAPErrorCode(ame.getLDAPErrorCode());
        return ide;
    }

    /**
     * private method to check the type
     * 
     * @param typeName
     * @return
     */
    private static boolean supportedType(String typeName) {
        return (mapSupportedTypes.get(typeName) != null);
    }

    private static void loadDefaultTypes() {
        supportedTypes.add(IdType.AGENT);
        supportedTypes.add(IdType.USER);
        supportedTypes.add(IdType.ROLE);
        supportedTypes.add(IdType.GROUP);
        supportedTypes.add(IdType.FILTEREDROLE);
        mapSupportedTypes.put(IdType.USER.getName(), IdType.USER);
        mapSupportedTypes.put(IdType.ROLE.getName(), IdType.ROLE);
        mapSupportedTypes.put(IdType.FILTEREDROLE.getName(),
                IdType.FILTEREDROLE);
        mapSupportedTypes.put(IdType.AGENT.getName(), IdType.AGENT);
        mapSupportedTypes.put(IdType.GROUP.getName(), IdType.GROUP);
        Set memberSet = new HashSet();
        memberSet.add(IdType.ROLE);
        memberSet.add(IdType.GROUP);
        memberSet.add(IdType.FILTEREDROLE);
        typesCanBeMemberOf.put(IdType.USER.getName(), memberSet);
        Set memberShipSet = new HashSet();
        memberShipSet.add(IdType.USER);
        typesCanHaveMembers.put(IdType.ROLE.getName(), memberShipSet);
        typesCanHaveMembers.put(IdType.GROUP.getName(), memberShipSet);
        typesCanHaveMembers.put(IdType.FILTEREDROLE.getName(), memberShipSet);
        typesCanAddMembers.put(IdType.GROUP.getName(), memberShipSet);
        typesCanAddMembers.put(IdType.ROLE.getName(), memberShipSet);
    }

    private static Set getMemberSet(Set members) {
        Set memberSet = new HashSet(members.size() * 2);
        for (Iterator iter = members.iterator(); iter.hasNext();) {
            String currType = (String) iter.next();
            memberSet.add(new IdType(currType));
        }
        return memberSet;
    }

    // SMS service listener to reinitialize if IdRepo service changes
    static class IdUtilsListener implements com.sun.identity.sm.ServiceListener{

        public void schemaChanged(String serviceName, String version) {
            if (serviceName.equalsIgnoreCase(IdConstants.REPO_SERVICE)) {
                initialize();
            }
        }

        public void globalConfigChanged(
            String serviceName,
            String version,
            String groupName,
            String serviceComponent,
            int type) {
            if (serviceName.equalsIgnoreCase(IdConstants.REPO_SERVICE)) {
                initialize();
            }
        }

        public void organizationConfigChanged(
            String serviceName,
            String version,
            String orgName,
            String groupName,
            String serviceComponent,
            int type) {
            if (serviceName.equalsIgnoreCase(IdConstants.REPO_SERVICE) &&
                orgName.equalsIgnoreCase(ServiceManager.getBaseDN())) {
                initialize();
            }
        }
    }
}
