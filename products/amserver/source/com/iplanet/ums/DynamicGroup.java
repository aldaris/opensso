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
 * $Id: DynamicGroup.java,v 1.1 2005-11-01 00:30:35 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.ums;

import java.security.Principal;

import netscape.ldap.LDAPDN;
import netscape.ldap.LDAPUrl;
import netscape.ldap.LDAPv2;

import com.iplanet.am.util.AMURLEncDec;
import com.iplanet.am.util.Debug;
import com.iplanet.services.ldap.Attr;
import com.iplanet.services.ldap.AttrSet;
import com.iplanet.services.ldap.ModSet;
import com.iplanet.services.util.I18n;

/**
 * Represents a dynnamic group entry.
 */
public class DynamicGroup extends PersistentObject implements
        IDynamicMembership {

    private static I18n i18n = I18n.getInstance(IUMSConstants.UMS_PKG);

    private static Debug debug;
    static {
        debug = Debug.getInstance(IUMSConstants.UMS_DEBUG);
    }

    /**
     * Default constructor
     */
    protected DynamicGroup() {
    }

    /**
     * Constructs a group object from an ID by reading from persistent storage.
     * 
     * @param session
     *            authenticated session
     * @param guid
     *            globally unique identifier for the group entry
     * @exception UMSException
     *                on failure to instantiate from persistent storage
     * @deprecated
     */
    DynamicGroup(Principal principal, Guid guid) throws UMSException {
        super(principal, guid);
        verifyClass();
    }

    /**
     * Constructs a DynamicGroup in memory using the default registered template
     * for DynamicGroup. This is an in-memory representation of a new object and
     * one needs to call the save method to save this new object to persistent
     * storage.
     * 
     * @param attrSet
     *            Attribute/value set, which should contain memberUrl
     * @exception UMSException
     *                on failure to instantiate from persistent storage
     */
    DynamicGroup(AttrSet attrSet) throws UMSException {
        this(TemplateManager.getTemplateManager().getCreationTemplate(_class,
                null), attrSet);
    }

    /**
     * iPlanet-PUBLIC-CONSTRUCTOR Constructs a DynamicGroup in memory with a
     * given template for DynamicGroup. This is an in-memory representation of a
     * new object; the save method must be called to save this new object to
     * persistent storage.
     * 
     * @param template
     *            Template for creating a group
     * @param attrSet
     *            Attribute/value set, which should contain memberUrl
     * @exception UMSException
     *                on failure to instantiate from persistent storage
     */
    public DynamicGroup(CreationTemplate template, AttrSet attrSet)
            throws UMSException {
        super(template, attrSet);
    }

    /**
     * Constructs a DynamicGroup in memory using the default registered template
     * for DynamicGroup. This is an in memory representation of a new object and
     * the save method must be called to save this new object to persistent
     * storage.
     * 
     * @param attrSet
     *            Attribute/value set, which should not contain memberUrl; any
     *            values of memberUrl will be overwritten by the explicit search
     *            criteria arguments.
     * @param base
     *            Search base for evaluating members of the group
     * @param filter
     *            Search filter for evaluating members of the group
     * @param scope
     *            Search scope for evaluating members of the group
     * @exception UMSException
     *                on failure to instantiate from persistent storage
     */
    DynamicGroup(AttrSet attrSet, Guid baseGuid, String filter, int scope)
            throws UMSException {
        this(TemplateManager.getTemplateManager().getCreationTemplate(_class,
                null), attrSet, baseGuid, filter, scope);
    }

    /**
     * iPlanet-PUBLIC-CONSTRUCTOR Constructs a DynamicGroup in memory given a
     * template for DynamicGroup. This is an in-memory representation of a new
     * object and the save method must be called to save this new object to
     * persistent storage.
     * 
     * @param template
     *            Template for creating a group
     * @param attrSet
     *            Attribute/value set, which should not contain memberUrl; any
     *            values of memberUrl will be overwritten by the explicit search
     *            criteria arguments.
     * @param base
     *            Search base for evaluating members of the group
     * @param filter
     *            Search filter for evaluating members of the group
     * @param scope
     *            Search scope for evaluating members of the group has to be
     *            LDAPv2.SCOPE_ONE or LDAPv2.SCOPE_SUB
     * 
     * @exception UMSException
     *                on failure to instantiate from persistent storage
     */
    public DynamicGroup(CreationTemplate template, AttrSet attrSet,
            Guid baseGuid, String filter, int scope) throws UMSException {
        super(template, attrSet);
        try {
            setUrl(baseGuid, filter, scope);
        } catch (Exception e) {
            // TODO - Log Exception
            debug.error("DynamicGroup : Exception : " + e.getMessage());
        }
    }

    /**
     * iPlanet-PUBLIC-METHOD Sets the search filter used to evaluate this
     * dynamic group.
     * 
     * @param filter
     *            Search filter for evaluating members of the group
     */
    public void setSearchFilter(String filter) {
        LDAPUrl url = getUrl();
        int scope = url.getScope();

        Guid baseGuid = new Guid(url.getDN());
        try {
            setUrl(baseGuid, filter, scope);
        } catch (Exception e) {
            // TODO - Log Exception
            debug.error("DynamicGroup.setSearchFilter : Exception : "
                    + e.getMessage());
        }
    }

    /**
     * iPlanet-PUBLIC-METHOD Gets the search filter used to evaluate this
     * dynamic group.
     * 
     * @return Search filter for evaluating members of the group the scope in
     *         the filter has to be LDAPv2.SCOPE_ONE or LDAPv2.SCOPE_SUB
     * 
     */
    public String getSearchFilter() {
        return getUrl().getFilter();
    }

    /**
     * iPlanet-PUBLIC-METHOD Sets the search base used to evaluate this dynamic
     * group.
     * 
     * @param base
     *            Search base for evaluating members of the group
     */
    public void setSearchBase(Guid baseGuid) {
        LDAPUrl url = getUrl();
        int scope = url.getScope();
        String filter = url.getFilter();
        try {
            setUrl(baseGuid, filter, scope);
        } catch (Exception e) {
            // TODO - Log Exception
            debug.error("DynamicGroup.setSearchFilter : Exception : "
                    + e.getMessage());
        }
    }

    /**
     * iPlanet-PUBLIC-METHOD Gets the search base used to evaluate this dynamic
     * group.
     * 
     * @return Search base for evaluating members of the group
     */
    public Guid getSearchBase() {
        return new Guid(getUrl().getDN());
    }

    /**
     * iPlanet-PUBLIC-METHOD Sets the search scope used to evaluate this dynamic
     * group.
     * 
     * @param scope
     *            Search scope for evaluating members of the group. Use one of
     *            the search scope SCOPE_BASE, SCOPE_ONE, or SCOPE_SUB.
     */
    public void setSearchScope(int scope) {
        LDAPUrl url = getUrl();
        Guid baseGuid = new Guid(url.getDN());
        String filter = url.getFilter();
        try {
            setUrl(baseGuid, filter, scope);
        } catch (Exception e) {
            // TODO - Log Exception
            debug.error("DynamicGroup.setSearchFilter : Exception : "
                    + e.getMessage());
        }
    }

    /**
     * iPlanet-PUBLIC-METHOD Gets the search scope used to evaluate this dynamic
     * group.
     * 
     * @return Search scope for evaluating members of the group
     */
    public int getSearchScope() {
        return getUrl().getScope();
    }

    /**
     * Convert the given parameters into an LDAP URL string. No LDAP host, port,
     * and attribute to return are present in the LDAP URL. Only search base,
     * filter and scope are given.
     * 
     * @param base
     *            Search Base DN in the LDAP URL.
     * @param filter
     *            Search filter in LDAP URL.
     * @param scope
     *            Search scope in LDAP URL.
     * @return String representation of LDAP URL.
     */
    protected String toUrlStr(String base, String filter, int scope) {
        StringBuffer urlBuf = new StringBuffer();
        urlBuf.append("ldap:///" + base + "?");

        switch (scope) {
        case LDAPv2.SCOPE_BASE:
            urlBuf.append("?base");
            break;
        case LDAPv2.SCOPE_ONE:
            urlBuf.append("?one");
            break;
        default:
        case LDAPv2.SCOPE_SUB:
            urlBuf.append("?sub");
            break;
        }

        if (filter != null && filter.length() > 0) {
            urlBuf.append("?" + filter);
        } else {
            urlBuf.append("?");
        }

        return urlBuf.toString();
    }

    /**
     * Creates a new search definition; the change is not persistent until
     * save() is called.
     * 
     * @param base
     *            Search base for evaluating members of the group
     * @param filter
     *            Search filter for evaluating members of the group
     * @param scope
     *            Search scope for evaluating members of the group
     */
    protected void setUrl(Guid baseGuid, String filter, int scope) {
        // Only valid scope is "sub" and "one"
        //
        if (scope != LDAPv2.SCOPE_ONE && scope != LDAPv2.SCOPE_SUB) {
            String msg = i18n.getString(IUMSConstants.ILLEGAL_GROUP_SCOPE);
            throw new IllegalArgumentException(msg);
        }

        String urlStr = toUrlStr(baseGuid.getDn(), filter, scope);

        // Sanity check on the url
        //
        try {
            new LDAPUrl(urlStr);
        } catch (java.net.MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        // TODO: Need to support multiple values of memberUrl? If so, do
        // an ADD instead of a replace.
        //
        modify(new Attr(MEMBER_URL_NAME, urlStr), ModSet.REPLACE);
    }

    /**
     * Gets the native LDAP URL used to evaluate this dynamic group.
     * 
     * @return LDAP URL for evaluating members of the group
     */
    protected LDAPUrl getUrl() {
        Attr attr = getAttribute(MEMBER_URL_NAME);
        LDAPUrl url = null;
        try {
            // TODO: Need to support multiple values of memberUrl?
            if ((attr != null) && (attr.getStringValues().length > 0)) {

                // Converting the url string to
                // application/x-www-form-urlencoded as expected by
                // LDAPUrl constructor.
                url = new LDAPUrl(AMURLEncDec.encodeLDAPUrl(attr
                        .getStringValues()[0]));
            }
            if (url == null) {
                url = new LDAPUrl(null, 0, "", (String[]) null,
                        LDAPv2.SCOPE_ONE, "");
            }
        } catch (java.net.MalformedURLException ex) {
            debug.error("DynamicGroup.setSearchFilter : Exception : "
                    + ex.getMessage());
            throw new IllegalArgumentException(ex.getMessage());
        }
        return url;
    }

    /**
     * Sets the native LDAP URL used to evaluate this dynamic group.
     * 
     * @param url
     *            LDAP URL for evaluating members of the group search scope in
     *            the url has to be LDAPv2.SCOPE_ONE or LDAPv2.SCOPE_SUB
     */
    protected void setUrl(LDAPUrl url) {
        String ldapurl = url.toString();
        try {
            ldapurl = LDAPUrl.decode(ldapurl);
        } catch (Exception ex) {
            if (debug.messageEnabled()) {
                debug.message("DynamicGroup.setUrl : " +
                        "Exception:" + ex.getMessage());
            }
        }
        if (url.getScope() != LDAPv2.SCOPE_ONE
                && url.getScope() != LDAPv2.SCOPE_SUB) {
            String msg = i18n.getString(IUMSConstants.ILLEGAL_GROUP_SCOPE);
            throw new IllegalArgumentException(msg);
        }
        // TODO: Need to support multiple values of memberUrl? If so, do
        // an ADD instead of a replace.
        modify(new Attr(MEMBER_URL_NAME, ldapurl), ModSet.REPLACE);
        // modify( new Attr( MEMBER_URL_NAME, url.toString() ), ModSet.ADD );
    }

    /**
     * Gets the members of the group.
     * 
     * @param attributes
     *            Attributes to return
     * @return Iterator for unique identifiers for members of the group
     * @exception UMSException
     *                on failure to search
     */
    protected SearchResults getMemberIDs(String[] attributes)
            throws UMSException {
        return DataLayer.getInstance().search(getPrincipal(), getSearchBase(),
                getSearchScope(), getSearchFilter(), attributes, false, null);
    }

    /**
     * iPlanet-PUBLIC-METHOD Gets the members of the group.
     * 
     * @return Iterator for unique identifiers for members of the group
     * @exception UMSException
     *                on failure to search
     */
    public SearchResults getMemberIDs() throws UMSException {
        String[] attributesToGet = { "objectclass" };
        return getMemberIDs(attributesToGet);
    }

    /**
     * iPlanet-PUBLIC-METHOD Gets the member count.
     * 
     * @return Number of members of the group
     * @exception UMSException
     *                on failure to search
     */
    public int getMemberCount() throws UMSException {
        int count = 0;
        String[] attributesToGet = { "dn" };
        SearchResults searchResults = getMemberIDs(attributesToGet);
        while (searchResults.hasMoreElements()) {
            searchResults.next().getDN();
            count++;
        }
        return count;
    }

    /**
     * iPlanet-PUBLIC-METHOD Gets a member given an index (zero-based).
     * 
     * @param index
     *            Zero-based index into the group container
     * @return Unique identifier for a member
     * @exception UMSException
     *                on failure to search
     */
    public Guid getMemberIDAt(int index) throws UMSException {
        if (index < 0) {
            throw new IllegalArgumentException(Integer.toString(index));
        }
        String filter = getSearchFilter();
        if (filter == null) {
            return null;
        }
        String[] attributesToGet = { "dn" };
        SearchResults searchResults = getMemberIDs(attributesToGet);
        while (searchResults.hasMoreElements()) {
            String s = searchResults.next().getDN();
            if (index == 0) {
                searchResults.abandon();
                return new Guid(s);
            }
            index--;
        }
        throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
    }

    /**
     * iPlanet-PUBLIC-METHOD Checks if a given identifier is a member of the
     * group.
     * 
     * @param guid
     *            Identity of member to be checked for membership
     * @return <code>true if it is a member
     * @exception       UMSException on failure to evaluate group
     */
    public boolean hasMember(Guid guid) throws UMSException {
        String filter = getSearchFilter();
        if (filter == null) {
            return false;
        }
        // Narrow the filter by using the RDN of the target
        // TODO: Should not have to use a DN here
        String dn = guid.getDn();
        String rdn = LDAPDN.explodeDN(dn, false)[0];
        filter = "(&" + filter + "(" + rdn + "))";
        String[] attributesToGet = { "dn" };
        SearchResults searchResults = DataLayer.getInstance().search(
                getPrincipal(), getSearchBase(), getSearchScope(), filter,
                attributesToGet, false, null);
        while (searchResults.hasMoreElements()) {
            String s = searchResults.next().getDN();
            if (Guid.equals(s, dn)) {
                searchResults.abandon();
                return true;
            }
        }
        return false;
    }

    private static final String MEMBER_URL_NAME = "memberurl";

    private static final Class _class = com.iplanet.ums.DynamicGroup.class;
}
