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
 * $Id: AMOrganization.java,v 1.2 2005-12-08 01:16:03 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk;

import java.util.Map;
import java.util.Set;

import com.iplanet.sso.SSOException;

/**
 * This interface provides methods to manage organization.
 * <code>AMOrganization</code> objects can be obtained by using
 * <code>AMStoreConnection</code>. A handle to this object can be obtained by
 * using the DN of the object.
 * 
 * <PRE>
 * 
 * AMStoreConnection amsc = new AMStoreConnection(ssotoken); 
 * if (amsc.doesEntryExist(oDN)) { 
 *     AMOrganization org = amsc.getOrganization(oDN); 
 * }
 * 
 * </PRE>
 * @supported.all.api
 */

public interface AMOrganization extends AMObject {
    /**
     * Creates sub-organizations.
     * 
     * @param subOrganizations
     *            The set of sub-organizations names to be created.
     * @return Set set of sub Organization objects created.
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set createSubOrganizations(Set subOrganizations) throws AMException,
            SSOException;

    /**
     * Creates sub-organizations and initializes their attributes.
     * 
     * @param subOrganizations
     *            Map where the key is the name of the sub organization, and the
     *            value is a Map to represent Attribute-Value Pairs
     * @return Set set of sub Organization objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set createSubOrganizations(Map subOrganizations) throws AMException,
            SSOException;

    /**
     * Creates sub-organizations and initializes their attributes. Initializes
     * service <code>objectclasses</code> and attributes as provided in the
     * <code>serviceNameAndAttrs</code> map.
     * 
     * @param orgName
     *            name of organization to be created under this organization.
     * @param domainName
     *            name of the domain (
     *            example <code>sun.com, iplanet.com</code>).
     * @param attrMap
     *            Map of attribute-value pairs to be set on the entry.
     * @param serviceNamesAndAttrs
     *            Map of service names and attribute-values for that service to
     *            be set in the organization entry.
     *            <code>serviceNameAndAttrs</code> has service names keys and
     *            map of attribute-values (values are in a Set).
     * @return DN of organization created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public String createOrganization(String orgName, String domainName,
            Map attrMap, Map serviceNamesAndAttrs) throws AMException,
            SSOException;

    /**
     * Deletes sub organizations.
     * 
     * @param subOrganizations
     *            The set of sub organization DNs to be deleted.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public void deleteSubOrganizations(Set subOrganizations)
            throws AMException, SSOException;

    /**
     * Returns the sub-organization by DN
     * 
     * @param dn
     *            distinguished name.
     * @return The sub Organization object
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public AMOrganization getSubOrganization(String dn) throws AMException,
            SSOException;

    /**
     * Gets the sub organizations within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return Set of sub organizations DNs within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set getSubOrganizations(int level) throws AMException, SSOException;

    /**
     * Gets number of sub organizations within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return Number of sub organizations within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public long getNumberOfSubOrganizations(int level) throws AMException,
            SSOException;

    /**
     * Searches for sub organizations in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * @return Set Set of DNs of Sub Organizations matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchSubOrganizations(String wildcard, int level)
            throws AMException, SSOException;

    /**
     * Searches for sub organizations in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set of DNs of sub
     *         Organizations matching the search
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchSubOrganizations(String wildcard,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for sub organizations in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of sub organizations with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching sub
     *            organizations
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * @return Set Set of DNs of sub organizations matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchSubOrganizations(String wildcard, Map avPairs, int level)
            throws AMException, SSOException;

    /**
     * Searches for sub organizations in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of sub organizations with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search.
     * @param avPairs
     *            attribute-value pairs to match when searching sub
     *            organizations.
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a Set of DNs of sub
     *         organizations matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchSubOrganizations(String wildcard, Map avPairs,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Creates organizational units.
     * 
     * @param organizationalUnits
     *            The set of organizational units names to be created.
     * @return set of sub <code>OrganizationalUnit</code> objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set createOrganizationalUnits(Set organizationalUnits)
            throws AMException, SSOException;

    /**
     * Creates organizational units and initializes their attributes.
     * 
     * @param organizationalUnits
     *            Map where the key is the name of the organizational unit, and
     *            the value is a Map to represent Attribute-Value Pairs
     * @return Set set of <code>OrganizationalUnit</code> objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set createOrganizationalUnits(Map organizationalUnits)
            throws AMException, SSOException;

    /**
     * Deletes organizational units
     * 
     * @param organizationalUnits
     *            The set of organizational units DNs to be deleted.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public void deleteOrganizationalUnits(Set organizationalUnits)
            throws AMException, SSOException;

    /**
     * Gets the organizational unit by DN.
     * 
     * @param dn
     *            distinguished name.
     * @return The <code>OrganizationalUnit</code> object
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public AMOrganizationalUnit getOrganizationalUnit(String dn)
            throws AMException, SSOException;

    /**
     * Gets the organizational units within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return The Set of organizational units DNs within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set getOrganizationalUnits(int level) throws AMException,
            SSOException;

    /**
     * Gets number of organizational units within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return Number of organizational units within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public long getNumberOfOrganizationalUnits(int level) throws AMException,
            SSOException;

    /**
     * Searches for organizational units in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * @return Set Set of DNs of organizational units matching the search
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchOrganizationalUnits(String wildcard, int level)
            throws AMException, SSOException;

    /**
     * Searches for organizational units in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.,
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         organizational units matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public AMSearchResults searchOrganizationalUnits(String wildcard,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for organizational units in this organization using wildcards
     * and attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of organizational units with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching organizational
     *            units
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * @return Set Set of DNs of organizational units matching the search
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchOrganizationalUnits(
            String wildcard, Map avPairs, int level)
            throws AMException, SSOException;

    /**
     * Searches for organizational units in this organization using wildcards
     * and attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of organizational units with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching organizational
     *            units
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.,
     * 
     * @return <code>AMSearchResults</code> which contains a Set of DNs of
     *         organizational units matching the search.
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public AMSearchResults searchOrganizationalUnits(String wildcard,
            Map avPairs, AMSearchControl searchControl) throws AMException,
            SSOException;

    /**
     * Creates roles.
     * 
     * @param roles
     *            The set of Roles' names to be created.
     * @return Set set of Role objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set createRoles(Set roles) throws AMException, SSOException;

    /**
     * Creates roles.
     * 
     * @param roles
     *            Map where the key is the name of the role, and the value is a
     *            Map to represent Attribute-Value Pairs
     * @return Set set of Role objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set createRoles(Map roles) throws AMException, SSOException;

    /**
     * Deletes roles.
     * 
     * @param roles
     *            The set of roles' DNs to be deleted.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public void deleteRoles(Set roles) throws AMException, SSOException;

    /**
     * Gets the roles within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return The Set of Roles' DNs within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set getRoles(int level) throws AMException, SSOException;

    /**
     * Gets number of roles within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return Number of roles within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public long getNumberOfRoles(int level) throws AMException, SSOException;

    /**
     * Searches for roles in this organization using wildcards. Wildcards can be
     * specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * @return Set Set of DNs of roles matching the search
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchRoles(String wildcard, int level) throws AMException,
            SSOException;

    /**
     * Searches for roles in this organization using wildcards. Wildcards can be
     * specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search.
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         roles matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public AMSearchResults searchRoles(String wildcard,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for roles in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a. To further refine
     * the search, attribute-value pairs can be specified so that DNs of roles
     * with matching attribute-value pairs will be returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching roles
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set Set of DNs of roles matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchRoles(String wildcard, Map avPairs, int level)
            throws AMException, SSOException;

    /**
     * Searches for roles in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a. To further refine
     * the search, attribute-value pairs can be specified so that DNs of roles
     * with matching attribute-value pairs will be returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching roles
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.,
     * 
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         roles matching the search.
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public AMSearchResults searchRoles(String wildcard, Map avPairs,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Creates filtered roles.
     * 
     * @param roles
     *            The set of filtered roles' names to be created.
     * @return Set set of <code>FilteredRole</code> objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set createFilteredRoles(Set roles) throws AMException, SSOException;

    /**
     * Creates filtered roles.
     * 
     * @param roles
     *            Map where the key is the name of the filtered role, and the
     *            value is a Map to represent Attribute-Value Pairs
     * @return set of <code>FilteredRole</code> objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set createFilteredRoles(Map roles) throws AMException, SSOException;

    /**
     * Deletes filtered roles.
     * 
     * @param roles
     *            The set of filtered roles' DNs to be deleted.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public void deleteFilteredRoles(Set roles) throws AMException, SSOException;

    /**
     * Gets the filtered roles within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return The Set of filtered roles' DNs within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set getFilteredRoles(int level) throws AMException, SSOException;

    /**
     * Gets number of filtered roles within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return Number of filtered roles within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public long getNumberOfFilteredRoles(int level) throws AMException,
            SSOException;

    /**
     * Searches for filtered roles in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set Set of DNs of filtered roles matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchFilteredRoles(String wildcard, int level)
            throws AMException, SSOException;

    /**
     * Searches for filtered roles in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         filtered roles matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchFilteredRoles(String wildcard,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for filtered roles in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of filtered roles with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching filtered roles
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set Set of DNs of filtered roles matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchFilteredRoles(String wildcard, Map avPairs, int level)
            throws AMException, SSOException;

    /**
     * Searches for filtered roles in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of filtered roles with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            pattern to be used in the search.
     * @param avPairs
     *            attribute-value pairs to match when searching filtered roles.
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         filtered roles matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchFilteredRoles(String wildcard, Map avPairs,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for all roles in this organization using wildcards. Wildcards
     * can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set of DNs of all roles matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchAllRoles(String wildcard, int level) throws AMException,
            SSOException;

    /**
     * Searches for all roles in this organization using wildcards. Wildcards
     * can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a Set of DNs of all
     *         roles matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchAllRoles(String wildcard,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for all roles in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a. To further refine
     * the search, attribute-value pairs can be specified so that DNs of all
     * roles with matching attribute-value pairs will be returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching all roles
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set of DNs of all roles matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchAllRoles(String wildcard, Map avPairs, int level)
            throws AMException, SSOException;

    /**
     * Searches for all roles in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a. To further refine
     * the search, attribute-value pairs can be specified so that DNs of all
     * roles with matching attribute-value pairs will be returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching all roles
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set of DNs of all
     *         roles matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchAllRoles(String wildcard, Map avPairs,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Creates assignable dynamic groups.
     * 
     * @param assignableDynamicGroups
     *            The set of assignable dynamic groups's names to be created.
     * @return set of <code>AssignableDynamicGroup</code> objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set createAssignableDynamicGroups(Set assignableDynamicGroups)
            throws AMException, SSOException;

    /**
     * Creates assignable dynamic group. Takes <code>serviceNameAndAttr</code>
     * map so that services can be assigned to the group which is just created.
     * 
     * @param name
     *            of group to be created
     * @param attributes
     *            attribute-value pairs to be set
     * @param serviceNameAndAttrs
     *            service name and attribute map where the map is like this:
     *            <code>&lt;serviceName>&lt;AttrMap>
     *      (attrMap=&lt;attrName>&lt;Set of attrvalues>)</code>
     * @return <code>AMGroup</code> object of newly created group.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public AMGroup createAssignableDynamicGroup(String name, Map attributes,
            Map serviceNameAndAttrs) throws AMException, SSOException;

    /**
     * Deletes assignable dynamic groups.
     * 
     * @param assignableDynamicGroups
     *            The set of assignable dynamic groups's DNs to be deleted.
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public void deleteAssignableDynamicGroups(Set assignableDynamicGroups)
            throws AMException, SSOException;

    /**
     * Returns the assignable dynamic groups within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return Set of DNs of <code>AssignableDynamicGroups</code> within the
     *         specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set getAssignableDynamicGroups(int level) throws AMException,
            SSOException;

    /**
     * Gets number of assignable dynamic groups within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return Number of assignable dynamic groups within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public long getNumberOfAssignableDynamicGroups(int level)
            throws AMException, SSOException;

    /**
     * Searches for assignable dynamic groups in this organization using
     * wildcards. Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set Set of DNs of assignable dynamic groups matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchAssignableDynamicGroups(String wildcard, int level)
            throws AMException, SSOException;

    /**
     * Searches for assignable dynamic groups in this organization using
     * wildcards. Wildcards can be specified such as a*, *, *a. Uses the
     * <code>groupSearchTemplate</code>, if provided. Otherwise the default
     * search template is used.
     * 
     * @param wildcard
     *            pattern to be used in the search.
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>).
     * @param groupSearchTemplate
     *            name of the search template to be used to perform this search.
     * @param avPairs
     *            This option can be used to further qualify the search filter.
     *            The attribute-value pairs provided by this map are appended to
     *            the search filter.
     * @return Set of DNs of assignable dynamic groups matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set searchAssignableDynamicGroups(String wildcard, int level,
            String groupSearchTemplate, Map avPairs) throws AMException,
            SSOException;

    /**
     * Searches for assignable dynamic groups in this organization using
     * wildcards. Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         assignable dynamic groups matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchAssignableDynamicGroups(String wildcard,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for assignable dynamic groups in this organization using
     * wildcards and attribute values. Wildcards can be specified such as a*, *,
     * *a. To further refine the search, attribute-value pairs can be specified
     * so that DNs of dynamic groups with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching assignable
     *            dynamic groups
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set Set of DNs of assignable dynamic groups matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchAssignableDynamicGroups(String wildcard, Map avPairs,
            int level) throws AMException, SSOException;

    /**
     * Searches for assignable dynamic groups in this organization using
     * wildcards and attribute values. Wildcards can be specified such as a*, *,
     * *a. To further refine the search, attribute-value pairs can be specified
     * so that DNs of dynamic groups with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching assignable
     *            dynamic groups.
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         assignable dynamic groups matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchAssignableDynamicGroups(String wildcard,
            Map avPairs, AMSearchControl searchControl) throws AMException,
            SSOException;

    /**
     * Searches for assignable dynamic groups in this organization using
     * wildcards and attribute values. Wildcards can be specified such as a*, *,
     * *a. To further refine the search, attribute-value pairs can be specified
     * so that DNs of dynamic groups with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching assignable
     *            dynamic groups
     * @param groupSearchTemplate
     *            Name of search template to be used to perform
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc., the
     *            search.
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         assignable dynamic groups matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchAssignableDynamicGroups(String wildcard,
            Map avPairs, String groupSearchTemplate,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Creates dynamic groups and initializes their attributes.
     * 
     * @param dynamicGroups
     *            Map where the key is the name of the dynamic group, and the
     *            value is a Map to represent Attribute-Value Pairs.
     * @return Set of <code>AMDynamicGroup</code> objects created
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set createDynamicGroups(Map dynamicGroups) throws AMException,
            SSOException;

    /**
     * Creates dynamic group. Takes <code>serviceNameAndAttr<code> map
     * so that services can be assigned to the group which is just created.
     *
     * @param name of group to be created
     * @param attributes to be set in group 
     * @param serviceNameAndAttrs service name and attribute map where the map
     *        is like this:
     * <code>&lt;serviceName>&lt;AttrMap>
     *       (attrMap=&lt;attrName>&lt;Set of attrvalues>)</code>
     * @return <code>AMGroup</code> object of newly created group.
     * @throws AMException if an error is encountered when trying to
     *         access/retrieve data from the data store.
     * @throws SSOException if the single sign on token is no longer valid.
     */
    public AMGroup createDynamicGroup(String name, Map attributes,
            Map serviceNameAndAttrs) throws AMException, SSOException;

    /**
     * Deletes dynamic groups.
     * 
     * @param dynamicGroups
     *            The set of dynamic groups's DNs to be deleted.
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public void deleteDynamicGroups(Set dynamicGroups) throws AMException,
            SSOException;

    /**
     * Gets the dynamic groups within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return The Set of DNs of <code>DynamicGroups</code> within the
     *         specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set getDynamicGroups(int level) throws AMException, SSOException;

    /**
     * Gets number of dynamic groups within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return Number of dynamic groups within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public long getNumberOfDynamicGroups(int level) throws AMException,
            SSOException;

    /**
     * Searches for dynamic groups in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set Set of DNs of dynamic groups matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchDynamicGroups(String wildcard, int level)
            throws AMException, SSOException;

    /**
     * Searches for dynamic groups in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a. Uses the
     * <code>groupSearchTemplate</code>, if provided. Otherwise uses the the
     * default <code>GroupSearch</code> template.
     * 
     * @param wildcard
     *            pattern to be used in the search.
     * @param level
     *            the search level that needs to be used.
     * @param groupSearchTemplate
     *            name of the search template to be used to perform this search.
     * @param avPairs
     *            This option can be used to further qualify the search filter.
     *            The attribute-value pairs provided by this map are appended to
     *            the search filter. (<code>AMConstants.SCOPE_ONE</code> or
     *            <code>AMConstants.SCOPE_SUB</code>)
     * @return set of DNs of dynamic groups matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set searchDynamicGroups(String wildcard, int level,
            String groupSearchTemplate, Map avPairs) throws AMException,
            SSOException;

    /**
     * Searches for dynamic groups in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.,
     * 
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         dynamic groups matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchDynamicGroups(String wildcard,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for dynamic groups in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of dynamic groups with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching dynamic groups
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set Set of DNs of dynamic groups matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchDynamicGroups(String wildcard, Map avPairs, int level)
            throws AMException, SSOException;

    /**
     * Searches for dynamic groups in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of dynamic groups with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching dynamic groups
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.,
     * 
     * @return <code>AMSearchResults</code> which contains set a of DNs of
     *         dynamic groups matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchDynamicGroups(String wildcard, Map avPairs,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for dynamic groups in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of dynamic groups with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching dynamic groups
     * @param groupSearchTemplate
     *            Name of search template to be used to perform the search.
     * @param searchControl
     *            specifies the search scope to be used
     * @return <code>AMSearchResults</code> which contains a Set of DNs of
     *         dynamic groups matching the search
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchDynamicGroups(String wildcard, Map avPairs,
            String groupSearchTemplate, AMSearchControl searchControl)
            throws AMException, SSOException;

    /**
     * Creates static groups.
     * 
     * @param groups
     *            The set of static groups's names to be created.
     * @return set of <code>AMStaticGroup</code> objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set createStaticGroups(Set groups) throws AMException, SSOException;

    /**
     * Creates static group. Takes <code>serviceNameAndAttr</code> map so that
     * services can be assigned to the group which is just created.
     * 
     * @param name
     *            of group to be created.
     * @param attributes
     *            to be set in group node.
     * @param serviceNameAndAttrs
     *            service name and attribute map where the map is like this:
     *            <code>&lt;serviceName>&lt;AttrMap>
     *              (attrMap=&lt;attrName>&lt;Set of attrvalues>)</code>.
     * @return <code>AMGroup</code> object of newly created group.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMGroup createStaticGroup(String name, Map attributes,
            Map serviceNameAndAttrs) throws AMException, SSOException;

    /**
     * Deletes static groups.
     * 
     * @param groups
     *            The set of static groups's DNs to be deleted.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     */
    public void deleteStaticGroups(Set groups) throws AMException, SSOException;

    /**
     * Gets the static groups within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return The Set of DNs of Groups within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     */
    public Set getStaticGroups(int level) throws AMException, SSOException;

    /**
     * Gets number of static groups within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return Number of static groups within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     */
    public long getNumberOfStaticGroups(int level) throws AMException,
            SSOException;

    /**
     * Searches for static groups in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set Set of DNs of static groups matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchStaticGroups(String wildcard, int level)
            throws AMException, SSOException;

    /**
     * Searches for static groups in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a. Uses the
     * <code>groupSearchTemplate</code>, if provided. If it is null, default
     * search templates are used.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * @param groupSearchTemplate
     *            name of the search template to be used to perform this search.
     * @param avPairs
     *            This option can be used to further qualify the search filter.
     *            The attribute-value pairs provided by this map are appended to
     *            the search filter.
     * 
     * @return Set Set of DNs of static groups matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchStaticGroups(String wildcard, int level,
            String groupSearchTemplate, Map avPairs) throws AMException,
            SSOException;

    /**
     * Searches for static groups in this organization using wildcards.
     * Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         static groups matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchStaticGroups(String wildcard,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for static groups in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of static groups with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching groups
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * @return Set Set of DNs of static groups matching the search
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchStaticGroups(String wildcard, Map avPairs, int level)
            throws AMException, SSOException;

    /**
     * Searches for static groups in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of static groups with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching groups
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         static groups matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchStaticGroups(String wildcard, Map avPairs,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for static groups in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of static groups with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search.
     * @param avPairs
     *            attribute-value pairs to match when searching static groups.
     * @param groupSearchTemplate
     *            Name of search template to be used to perform the search.
     * @param searchControl
     *            specifies the search scope to be used.
     * @return <code>AMSearchResults</code> which contains a Set of DNs of
     *         static groups matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchStaticGroups(String wildcard, Map avPairs,
            String groupSearchTemplate, AMSearchControl searchControl)
            throws AMException, SSOException;

    /**
     * Searches for groups in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching groups
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set DNs of groups matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchGroups(String wildcard, Map avPairs, int level)
            throws AMException, SSOException;

    /**
     * Searches for groups in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search.
     * @param avPairs
     *            attribute-value pairs to match when searching groups.
     * @param searchControl
     *            specifies the search scope to be used.
     * @return <code>AMSearchResults</code> which contains set a of DNs of
     *         groups matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchGroups(String wildcard, Map avPairs,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Creates people containers.
     * 
     * @param peopleContainers
     *            The set of people containers' names to be created
     * @return set of <code>PeopleContainer</code> objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set createPeopleContainers(Set peopleContainers) throws AMException,
            SSOException;

    /**
     * Creates people containers and initializes their attributes.
     * 
     * @param peopleContainers
     *            Map where the key is the name of the people container, and the
     *            value is a Map to represent attribute-value pairs.
     * @return set of <code>PeopleContainer</code> objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set createPeopleContainers(Map peopleContainers) throws AMException,
            SSOException;

    /**
     * Deletes people containers.
     * 
     * @param peopleContainers
     *            The set of people containers' DN to be deleted.
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public void deletePeopleContainers(Set peopleContainers)
            throws AMException, SSOException;

    /**
     * Gets the people containers within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return Set of people containers within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set getPeopleContainers(int level) throws AMException, SSOException;

    /**
     * Gets number of people containers within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return long Number of people containers within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public long getNumberOfPeopleContainers(int level) throws AMException,
            SSOException;

    /**
     * Searches for people containers in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set DNs of people containers matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchPeopleContainers(String wildcard, int level)
            throws AMException, SSOException;

    /**
     * Searches for people containers in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search.
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set DNs of people
     *         containers matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchPeopleContainers(String wildcard,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for people containers in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of people containers with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching people
     *            containers
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set DNs of people containers matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchPeopleContainers(String wildcard, Map avPairs, int level)
            throws AMException, SSOException;

    /**
     * Searches for people containers in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of people containers with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search.
     * @param avPairs
     *            attribute-value pairs to match when searching people
     *            containers.
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set DNs of people
     *         containers matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchPeopleContainers(String wildcard, Map avPairs,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Creates group containers.
     * 
     * @param groupContainers
     *            The set of group containers' names to be created.
     * @return set of group container objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set createGroupContainers(Set groupContainers) throws AMException,
            SSOException;

    /**
     * Creates group containers and initializes their attributes.
     * 
     * @param groupContainers
     *            Map where the key is the name of the group container, and the
     *            value is a Map to represent attribute-value pairs.
     * @return set of group container objects created.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set createGroupContainers(Map groupContainers) throws AMException,
            SSOException;

    /**
     * Deletes group containers.
     * 
     * @param groupContainers
     *            The set of group containers' DN to be deleted.
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public void deleteGroupContainers(Set groupContainers) throws AMException,
            SSOException;

    /**
     * Gets the group containers within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return set of group containers within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set getGroupContainers(int level) throws AMException, SSOException;

    /**
     * Gets number of group containers within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return long Number of group containers within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public long getNumberOfGroupContainers(int level) throws AMException,
            SSOException;

    /**
     * Searches for group containers in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of group containers with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching group containers
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set DNs of group containers matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchGroupContainers(String wildcard, Map avPairs, int level)
            throws AMException, SSOException;

    /**
     * Searches for group containers in this organization using wildcards and
     * attribute values. Wildcards can be specified such as a*, *, *a. To
     * further refine the search, attribute-value pairs can be specified so that
     * DNs of group containers with matching attribute-value pairs will be
     * returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search.
     * @param avPairs
     *            attribute-value pairs to match when searching group
     *            containers.
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set DNs of group
     *         containers matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchGroupContainers(String wildcard, Map avPairs,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Creates users in this organization. For each user the, object classes
     * specified by organization type attribute
     * <code>iplanet-am-required-services</code> of the service
     * <code>iPlanetAMAdminConsoleService</code> template are added. If a
     * corresponding template does not exist, the default values are picked up
     * from schema.
     * 
     * @param users
     *            The set of user names to be created in this organization.
     * @return Set Set of User objects created
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set createUsers(Set users) throws AMException, SSOException;

    /**
     * Creates users and initializes their attributes. For each user the, object
     * classes specified by organization type attribute
     * <code>iplanet-am-required-services</code> of the service
     * <code>iPlanetAMAdminConsoleService</code> template are added. If a
     * corresponding template does not exist, the default values are picked up
     * from schema.
     * 
     * @param users
     *            Map where the key is the name of the user, and the value is a
     *            Map to represent Attribute-Value Pairs
     * @return Set Set of User objects created
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set createUsers(Map users) throws AMException, SSOException;

    /**
     * Create user and initializes the attributes. For each user the, object
     * classes specified by organization type attribute
     * <code>iplanet-am-required-services</code> of the service
     * <code>iPlanetAMAdminConsoleService</code> template are added. If a
     * corresponding template does not exist, the default values are picked up
     * from schema. Also services as defined in the arguments, are assigned to
     * the user, with default values being picked up from the service schema if
     * none are provided for required attributes of the service.
     * 
     * @param uid
     *            value of naming attribute for user.
     * @param attrMap
     *            attribute-values to be set in the user entry.
     * @param serviceNameAndAttrs
     *            service names and attributes to be assigned to the user.
     * @return AMUser object of newly created user.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public AMUser createUser(String uid, Map attrMap, Map serviceNameAndAttrs)
            throws AMException, SSOException;

    /**
     * Deletes users from this organization.
     * 
     * @param users
     *            The set of user DN's to be deleted from the organization.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public void deleteUsers(Set users) throws AMException, SSOException;

    /**
     * Returns the names (DNs) of users in the organization.
     * 
     * @return Set The names(DNs) of users in the organization.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set getUserDNs() throws AMException, SSOException;

    /**
     * Returns <code>AMUser</code> object of user in this organization (or in
     * sub organizations), whose naming attribute exactly matches with
     * <code>uid</code>. If <code>userSearchTemplate</code> is not null,
     * then this search template is used otherwise the
     * <code>BasicUserSearchTemplate</code> is used. Any <code>%U</code> in
     * the search filter are replaced with <code>uid</code>. If the search
     * returns more than one user, an exception is thrown because this is a
     * violation of the name space constraint.
     * 
     * @param uid
     *            naming attribute value for user.
     * @param userSearchTemplate
     *            search template.
     * @return <code>AMUser</code> object of user found.
     * @throws AMException
     * @throws SSOException
     */
    AMUser getUser(String uid, String userSearchTemplate) throws AMException,
            SSOException;

    /**
     * Gets number of users within the specified level.
     * 
     * @param level
     *            The search level starting from the organization.
     * @return Number of users within the specified level.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public long getNumberOfUsers(int level) throws AMException, SSOException;

    /**
     * Searches for users in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set DNs of Users matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchUsers(String wildcard, int level) throws AMException,
            SSOException;

    /**
     * Searches for users in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a. SDK users the
     * <code>userSearchTemplate</code>, if provided. Otherwise, it uses the
     * <code>BasicUserSearchTemplate</code>. Any <code>%U</code> in the
     * search template are replaced with the wildcard.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * @param userSearchTemplate
     *            Name of search template to be used. If null is passed then the
     *            default search template <code>BasicUserSearch</code> will be
     *            used.
     * @param avPairs
     *            This option can be used to further qualify the search filter.
     *            The attribute-value pairs provided by this map are appended to
     *            the search filter.
     * @return Set DNs of Users matching the search
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set searchUsers(String wildcard, int level,
            String userSearchTemplate, Map avPairs) throws AMException,
            SSOException;

    /**
     * Searches for users in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search.
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set DNs of users
     *         matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchUsers(String wildcard,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for users in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a. To further refine
     * the search, attribute-value pairs can be specified so that DNs of users
     * with matching attribute-value pairs will be returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching users
     * @param level
     *            the search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>)
     * 
     * @return Set DNs of Users matching the search
     * 
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set searchUsers(String wildcard, Map avPairs, int level)
            throws AMException, SSOException;

    /**
     * Searches for users in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a. To further refine
     * the search, attribute-value pairs can be specified so that DNs of users
     * with matching attribute-value pairs will be returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search.
     * @param avPairs
     *            attribute-value pairs to match when searching users.
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.,
     * @return <code>AMSearchResults</code> which contains a set DNs of users
     *         matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchUsers(String wildcard, Map avPairs,
            AMSearchControl searchControl) throws AMException, SSOException;

    /**
     * Searches for users in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a. To further refine
     * the search, attribute-value pairs can be specified so that DNs of users
     * with matching attribute-value pairs will be returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search
     * @param avPairs
     *            attribute-value pairs to match when searching users
     * @param userSearchTemplate
     *            Name of user search template to be used.
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @return <code>AMSearchResults</code> which contains a set DNs of users
     *         matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchUsers(String wildcard, Map avPairs,
            String userSearchTemplate, AMSearchControl searchControl)
            throws AMException, SSOException;

    /**
     * Searches for users in this organization using wildcards and attribute
     * values. Wildcards can be specified such as a*, *, *a. To further refine
     * the search, attribute-value pairs can be specified so that DNs of users
     * with matching attribute-value pairs will be returned.
     * 
     * @param wildcard
     *            wildcard pattern to be used in the search.
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @param avfilter
     *            this attribute-value pairs filter will be logical AND with
     *            user search filter.
     * @return <code>AMSearchResults</code> which contains a Set DNs of users
     *         matching the search.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public AMSearchResults searchUsers(String wildcard,
            AMSearchControl searchControl, String avfilter) throws AMException,
            SSOException;

    /**
     * Searches for users in this organization using attribute values. Wildcards
     * such as can be specified for the attribute values. The DNs of users with
     * matching attribute-value pairs will be returned.
     * 
     * @param searchControl
     *            specifies the search scope to be used, VLV ranges etc.
     * @param avfilter
     *            this attribute-value pairs filter will be logical AND with
     *            user search filter.
     * @return <code>AMSearchResults</code> which contains a set of DNs of
     *         users matching the search.
     * @throws AMException
     *             if there is an internal error in the access management data
     *             store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public AMSearchResults searchUsers(AMSearchControl searchControl,
            String avfilter) throws AMException, SSOException;

    /**
     * Returns the number of services.
     * 
     * @return number of services.
     * @throws AMException
     *             if there is an internal error in the access management data
     *             store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public long getNumberOfServices() throws AMException, SSOException;

    /**
     * Gets the names of registered services.
     * 
     * @return The Set of the names of registered services.
     * @throws AMException
     *             if there is an internal error in the access management data
     *             store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public Set getRegisteredServiceNames() throws AMException, SSOException;

    /**
     * Register a service for this organization.
     * 
     * @param serviceName
     *            The name of service to be registered
     * @param createTemplate
     *            true if to create default template
     * @param activate
     *            true if to activate the service
     * @throws AMException
     *             if the service does not exist or could not be registered.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public void registerService(String serviceName, boolean createTemplate,
            boolean activate) throws AMException, SSOException;

    /**
     * Unregisters a service for this organization.
     * 
     * @param serviceName
     *            service name to be unregistered.
     * @throws AMException
     *             if the service does not exist or could not be unregistered.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public void unregisterService(String serviceName) throws AMException,
            SSOException;

    /**
     * Unassigns the given policies from this organization and its roles.
     * 
     * @param serviceName
     *            service name.
     * @param policyDNs
     *            Set of policy DN string.
     * @throws AMException
     *             if there is an internal problem with access management data
     *             store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public void unassignAllPolicies(String serviceName, Set policyDNs)
            throws AMException, SSOException;

    /**
     * Modifies all the templates under this organization that contain any
     * <code>policyDN</code> in given <code>policyDNs</code>.
     * 
     * @param serviceName
     *            service name.
     * @param policyDNs
     *            Set of policy DN string
     * 
     * @throws AMException
     *             if there is an internal problem with access management data
     *             store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public void modifyAllPolicyTemplates(String serviceName, Set policyDNs)
            throws AMException, SSOException;

    /**
     * Deletes all the named policy templates for this Organization
     * corresponding to the given policy. This includes organizational based and
     * role based policy templates. This is a convenience method.
     * 
     * @param policyDN
     *            a policy DN string
     * @throws AMException
     *             if there is an internal problem with access management data
     *             store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     * @return true if policy templates were found and deleted.
     */
    public boolean deleteAllNamedPolicyTemplates(String policyDN)
            throws AMException, SSOException;

    /**
     * Gets all the assigned policies for this Organization
     * 
     * @return Set a set of assigned policy DNs
     * @throws AMException
     *             if there is an internal problem with access management data
     *             store.
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public Set getAssignedPolicyDNs() throws AMException, SSOException;

    /**
     * Returns true if a <code>policyDN</code> is assigned to an organization
     * or a role.
     * 
     * @param policyDN
     *            a policy DN string
     * @param serviceName
     *            service name
     * @return true if policy is assigned to an organization or role.
     * @throws AMException
     *             if there is an internal error in the access management data
     *             store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public boolean isPolicyAssigned(String policyDN, String serviceName)
            throws AMException, SSOException;

    /**
     * Returns true if an organizational template exists for the service.
     * 
     * @param serviceName
     *            service name
     * @return true if the organizational template exists.
     * @throws AMException
     *             if there is an internal error in the access management data
     *             store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public boolean orgTemplateExists(String serviceName) throws AMException,
            SSOException;

    /**
     * Unassigns services from the organization. Also removes service specific
     * attributes, if defined in the user entry.
     * 
     * @param serviceNames
     *            Set of service names
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid
     */
    public void unassignServices(Set serviceNames) throws AMException,
            SSOException;

    /**
     * This method creates the entities of the given type. The entity is created
     * under a default container, if specified in the <code>DAI</code>
     * service. If the specified default container does not exist, then it is
     * created under the current organization and then the entities are created
     * under it. If no specified container is specified, then the entities are
     * created directly under the organization.
     * 
     * @param type
     *            Type of entity being created. The types supported by SDK are
     *            configured in the list of Managed Objects in the
     *            <code>DAI</code> service.
     * @param entityNames
     *            Set of names of entities to be created
     * @return Set of <code>AMEntity</code> objects.
     * @throws AMException
     *             if an error encountered when trying to set/access the data
     *             store.
     * @throws SSOException
     *             if the single sign on token of user is no longer valid.
     */
    public Set createEntities(int type, Set entityNames) throws AMException,
            SSOException;

    /**
     * Creates the entities of the given type. The entity is created under a
     * default container, if specified in the <code>DAI</code> service. If the
     * specified default container does not exist, then it is created under the
     * current organization and then the entities are created under it. If no
     * specified container is specified, then the entities are created directly
     * under the organization.
     * 
     * @param type
     *            Type of entity being created. The types supported by SDK are
     *            configured in the list of Managed Objects in the
     *            <code>DAI</code> service.
     * @param entityNamesAndAttrs
     *            Map of entity name to a map of attribute name to a set of
     *            values.
     * @return Set of <code>AMEntity</code> objects.
     * @throws AMException
     *             if an error encountered when trying to set/access the data
     *             store.
     * @throws SSOException
     *             if the single sign on token of user is no longer valid.
     */
    public Set createEntities(int type, Map entityNamesAndAttrs)
            throws AMException, SSOException;

    /**
     * Searches for entities of the given type. The basic search filter is used
     * from the search template as defined in the <code>DAI</code> service.
     * The map of attribute-value pairs is <code>or-ed</code> to the basic
     * search filter along with the wildcard, which is used to specify the
     * naming attribute in the final search filter. The search is conducted
     * under the specified container, if it exists. It the specified container
     * does not exist, then an exception is thrown. If there is no specified
     * container, then the search is performed directly under the organization.
     * 
     * @param type
     *            Type of entity being created. The types supported by SDK are
     *            configured in the list of Managed Objects in the
     *            <code>DAI</code> service.
     * @param wildcard
     *            Pattern for naming attribute when performing the search.
     * @param scope
     *            Search level that needs to be used (
     *            <code>AMConstants.SCOPE_ONE</code>
     *            or <code>AMConstants.SCOPE_SUB</code>).
     * @param avPairs
     *            Map of attribute-value pairs.
     * @return Set of matching entity distinguished names.
     * @throws AMException
     *             If there is an error trying to access the data store.
     * @throws SSOException
     *             If the user's single sign on token is invalid.
     */
    public Set searchEntities(int type, String wildcard, int scope, Map avPairs)
            throws AMException, SSOException;

    /**
     * Searches for entities of the given type. The basic search filter is used
     * from the search template as defined in the <code>DAI</code> service.
     * The map of attribute-value pairs is <code>or-ed</code> to the basic
     * search filter along with the wildcard, which is used to specify the
     * naming attribute in the final search filter. The search is conducted
     * under the specified container, if it exists. It the specified container
     * does not exist, then an exception is thrown. If there is no specified
     * container, then the search is performed directly under the organization.
     * 
     * @param type
     *            The type of entity to be searched. The types are defined in
     *            the list of managed objects in the <code>DAI</code> service.
     * @param wildcard
     *            Pattern for naming attribute when performing the search.
     * @param avPairs
     *            Map of attribute-value pairs.
     * @param ctrls
     *            Search control object specifying various search parameter.
     * @return com.iplanet.am.sdk.AMSearchResults
     * @throws AMException
     *             If there is an error trying to access the data store.
     * @throws SSOException
     *             If the user's single sign on token is invalid.
     */
    public AMSearchResults searchEntities(int type, String wildcard,
            Map avPairs, AMSearchControl ctrls) throws AMException,
            SSOException;

    /**
     * 
     * Searches for entities of the given type. The basic search filter is used
     * from the search template as defined in the <code>DAI</code> service.
     * The map of attribute-value pairs is <code>or-ed</code> to the basic
     * search filter along with the wildcard, which is used to specify the
     * naming attribute in the final search filter. The search is conducted
     * under the specified container, if it exists. It the specified container
     * does not exist, then an exception is thrown. If there is no specified
     * container, then the search is performed directly under the organization.
     * 
     * @param type
     *            The type of entity to be searched. The types are defined in
     *            the list of managed objects in the <code>DAI</code> service.
     * @param wildcard
     *            Pattern for naming attribute when performing the search.
     * @param avfilter
     *            Search filter to add to the basic search filter.
     * @param ctrl
     *            Search control object specifying various search parameter.
     * @return <code>com.iplanet.am.sdk.AMSearchResults</code>
     * @throws AMException
     *             If there is an error trying to access the data store.
     * @throws SSOException
     *             If the user's single sign on token is invalid.
     */
    public AMSearchResults searchEntities(int type, String wildcard,
            String avfilter, AMSearchControl ctrl) throws AMException,
            SSOException;

    /**
     * Deletes the entities whose fully-qualified distinguished names are
     * provided in the set below.
     * 
     * @param type
     *            Type of entity being deleted.
     * @param entityDNs
     *            Set of the <code> FQDNs </code> of entities to be deleted.
     * @throws AMException
     *             If there is an error in trying to access the data store.
     * @throws SSOException
     *             If the user's single sign on token is invalid.
     */
    public void deleteEntities(int type, Set entityDNs) throws AMException,
            SSOException;

    /**
     * Returns true if the organization is activated.
     * 
     * @return true if the organization is activated.
     * @throws AMException
     *             if an error is encountered when trying to access/retrieve
     *             data from the data store
     * @throws SSOException
     *             if the single sign on token is no longer valid.
     */
    public boolean isActivated() throws AMException, SSOException;

}
