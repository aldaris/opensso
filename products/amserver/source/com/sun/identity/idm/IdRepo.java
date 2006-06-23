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
 * $Id: IdRepo.java,v 1.3 2006-06-23 00:48:05 arviranga Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.idm;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.callback.Callback;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.sm.SchemaType;

/**
 * 
 * This interface defines the methods which need to be implemented by plugins.
 * Two plugins are supported, <code> ldap </code> and <code> remote </code>.
 */
public abstract class IdRepo {

    /**
     * The constants used to define membership operations.
     */
    public static final int ADDMEMBER = 1;

    public static final int REMOVEMEMBER = 2;

    public Map configMap = Collections.EMPTY_MAP;

    public static final int NO_MOD = -1;

    public static final int OR_MOD = 0;

    public static final int AND_MOD = 1;

    /**
     * Initialization paramters as configred for a given plugin.
     * 
     * @param configParams
     */
    public void initialize(Map configParams) {
        configMap = Collections.unmodifiableMap(configParams);
    }

    /**
     * This method is invoked just before the plugin is removed from the IdRepo
     * cache of plugins. This helps the plugin clean up after itself
     * (connections, persistent searches etc.). This method should be overridden
     * by plugins that need to do this.
     * 
     */
    public void shutdown() {
        // do nothing
    }

    /**
     * Return supported operations for a given type in the specified
     * organization.
     * 
     * @param type
     * @param orgName
     * @return
     */
    public Set getSupportedOperations(IdType type) {
        Set set = new HashSet();
        set.add(IdOperation.READ);
        return set;
    }

    /**
     * Returns the supported types of identities for this plugin. If a plugin
     * does not override this method, it returns an empty set.
     * 
     * @return Returns a Set of IdTypes supported by this plugin.
     */
    public Set getSupportedTypes() {
        return Collections.EMPTY_SET;
    }

    /**
     * Returns true if the entry exists in the datastore.
     * 
     * @param token
     * @param type
     * @param name
     * @param orgName
     * @return
     */
    public abstract boolean isExists(SSOToken token, IdType type, String name)
            throws IdRepoException, SSOException;

    /**
     * Returns true if the entry's status is active or if the entry is not
     * usually marked
     * 
     * @param token
     * @param type
     * @param name
     * @return
     */
    public boolean isActive(SSOToken token, IdType type, String name)
            throws IdRepoException, SSOException {
        return false;
    }

    /**
     * Returns attributes of identity.
     * 
     * @param token
     * @param type
     * @param name
     * @param orgName
     * @return
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract Map getAttributes(SSOToken token, IdType type, String name)
            throws IdRepoException, SSOException;

    /**
     * 
     * @param token
     * @param type
     * @param name
     * @param orgName
     * @param attrNames
     * @return
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract Map getAttributes(SSOToken token, IdType type, String name,
            Set attrNames) throws IdRepoException, SSOException;

    /**
     * Returns binary attributes as an array of bytes.
     * 
     * @param token
     * @param type
     * @param name
     * @param attrNames
     * @return
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract Map getBinaryAttributes(SSOToken token, IdType type,
            String name, Set attrNames) throws IdRepoException, SSOException;

    /**
     * Creates the identity
     * 
     * @param token
     * @param type
     * @param name
     * @param orgName
     * @param attrMap
     * @return String representation of this identity
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract String create(SSOToken token, IdType type, String name,
            Map attrMap) throws IdRepoException, SSOException;

    /**
     * Deletes an identity.
     * 
     * @param token
     * @param type
     * @param name
     * @param orgName
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract void delete(SSOToken token, IdType type, String name)
            throws IdRepoException, SSOException;

    /**
     * Replaces attributes in the identity with the new ones.
     * 
     * @param token
     * @param type
     * @param name
     * @param orgName
     * @param attributes
     * @param isAdd
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract void setAttributes(SSOToken token, IdType type,
            String name, Map attributes, boolean isAdd) throws IdRepoException,
            SSOException;

    /**
     * 
     * @param token
     * @param type
     * @param name
     * @param attributes
     * @param isAdd
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract void setBinaryAttributes(SSOToken token, IdType type,
            String name, Map attributes, boolean isAdd) throws IdRepoException,
            SSOException;

    /**
     * Removes the attributes from the identity.
     * 
     * @param token
     * @param type
     * @param name
     * @param orgName
     * @param attrNames
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract void removeAttributes(SSOToken token, IdType type,
            String name, Set attrNames) throws IdRepoException, SSOException;

    /**
     * Search for specific type of identities.
     * 
     * @param token
     * @param type
     * @param pattern
     * @param maxTime
     * @param maxResults
     * @param returnAttrs
     * @param returnAllAttrs
     * @param filterOp
     * @param avPairs
     * @return
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract RepoSearchResults search(SSOToken token, IdType type,
            String pattern, int maxTime, int maxResults, Set returnAttrs,
            boolean returnAllAttrs, int filterOp, Map avPairs, 
            boolean recursive) throws IdRepoException, SSOException;

    /**
     * Modify membership of the identity. Set of members is a set of unique
     * identifiers of other identities.
     * 
     * @param token
     * @param type
     * @param name
     * @param orgName
     * @param members
     * @param operation
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract void modifyMemberShip(SSOToken token, IdType type,
            String name, Set members, IdType membersType, int operation)
            throws IdRepoException, SSOException;

    /**
     * Returns members of an identity. Applicable if identity is a group or a
     * role, for example.
     * 
     * @param token
     * @param type
     * @param name
     * @param orgName
     * @return
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract Set getMembers(SSOToken token, IdType type, String name,
            IdType membersType) throws IdRepoException, SSOException;

    /**
     * Returns the memberships of a identity. For example, returns the groups or
     * roles that a user belongs to.
     * 
     * @param token
     * @param type
     * @param name
     * @param orgName
     * @return
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract Set getMemberships(SSOToken token, IdType type,
            String name, IdType membershipType) throws IdRepoException,
            SSOException;

    /**
     * This method is used to assign a service to the given identity. The
     * behaviour of this method will be different, depending on how each plugin
     * will implement the services model. The map of attribute-values has
     * already been validated and default values have already been inherited by
     * the framework. The plugin has to verify if the service is assigned (in
     * which case it should throw an exception), and assign the service and the
     * attributes to the identity (if supported).
     * 
     * @param token
     * @param type
     * @param name
     * @param serviceName
     * @param attrMap
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract void assignService(SSOToken token, IdType type,
            String name, String serviceName, SchemaType stype, Map attrMap)
            throws IdRepoException, SSOException;

    /**
     * Returns the set of services assigned to this identity. The framework has
     * to check if the values are objectclasses, then map it to service names.
     * Or if they are servicenames, then there is no mapping needed.
     * 
     * @param token
     * @param type
     * @param name
     * @param objectClasses
     * @return
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract Set getAssignedServices(SSOToken token, IdType type,
            String name, Map mapOfServicesAndOCs) throws IdRepoException,
            SSOException;

    /**
     * If the service is already assigned to the entry, then this method
     * unassigns the service and removes the related attributes from the entry.
     * 
     * @param token
     * @param type
     * @param name
     * @param serviceName
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract void unassignService(SSOToken token, IdType type,
            String name, String serviceName, Map attrMap)
            throws IdRepoException, SSOException;

    /**
     * Returns the attribute values of the service attributes.
     * 
     * @param token
     * @param type
     * @param name
     * @param serviceName
     * @param attrNames
     * @return
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract Map getServiceAttributes(SSOToken token, IdType type,
            String name, String serviceName, Set attrNames)
            throws IdRepoException, SSOException;

    /**
     * Modifies the attribute values of the service attributes
     * 
     * @param token
     * @param type
     * @param name
     * @param serviceName
     * @param attrMap
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract void modifyService(SSOToken token, IdType type,
            String name, String serviceName, SchemaType sType, Map attrMap)
            throws IdRepoException, SSOException;

    /**
     * 
     * @param token
     * @param listener
     * @param configMap
     * @return
     * @throws IdRepoException
     * @throws SSOException
     */
    public abstract int addListener(SSOToken token, IdRepoListener listener)
            throws IdRepoException, SSOException;

    /**
     * Removes the listener added using <code> addListener </code> method. This
     * is called by the IdRepo framework when the plugin is being shutdown due
     * to configuration change, so that a new instance can be created with the
     * new configuration map.
     * 
     */
    public abstract void removeListener();

    /**
     * Return the configuration map
     * 
     * @return
     */
    public Map getConfiguration() {
        return configMap;
    }

    /**
     * Returns the fully qualified name for the identity. It is expected that
     * the fully qualified name would be unique, hence it is recommended to
     * prefix the name with the data store name or protocol. Used by IdRepo
     * framework to check for equality of two identities
     * 
     * @param token
     *            administrator SSOToken that can be used by the datastore to
     *            determine the fully qualified name
     * @param type
     *            type of the identity
     * @param name
     *            name of the identity
     * 
     * @return fully qualified name for the identity within the data store
     */
    public String getFullyQualifiedName(SSOToken token, IdType type, 
            String name) throws IdRepoException, SSOException {
        return ("default://" + type.toString() + "/" + name);
    }

    /**
     * Returns <code>true</code> if the data store supports authentication of
     * identities. Used by IdRepo framework to authenticate identities.
     * 
     * @return <code>true</code> if data store supports authentication of of
     *         identities; else <code>false</code>
     */
    public boolean supportsAuthentication() {
        return (false);
    }

    /**
     * Returns <code>true</code> if the data store successfully authenticates
     * the identity with the provided credentials. In case the data store
     * requires additional credentials, the list would be returned via the
     * <code>IdRepoException</code> exception.
     * 
     * @param credentials
     *            Array of callback objects containing information such as
     *            username and password.
     * 
     * @return <code>true</code> if data store authenticates the identity;
     *         else <code>false</code>
     */
    public boolean authenticate(Callback[] credentials) throws IdRepoException,
            com.sun.identity.authentication.spi.AuthLoginException {
        return (false);
    }
}
