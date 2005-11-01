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
 * $Id: AMEntityImpl.java,v 1.1 2005-11-01 00:29:02 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk;

import java.util.Map;
import java.util.Set;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;

public class AMEntityImpl extends AMObjectImpl implements AMEntity {

    protected AMEntityImpl(SSOToken ssotoken, String dn) {
        super(ssotoken, dn, AMObject.UNDETERMINED_OBJECT_TYPE);
    }

    protected AMEntityImpl(SSOToken ssotoken, String dn, int type) {
        super(ssotoken, dn, type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#delete(boolean)
     */
    public void delete(boolean recursive) throws AMException, SSOException {
        int type = dsManager.getObjectType(token, entryDN);
        profileType = type;
        super.delete(recursive);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#getAttributes()
     */
    public Map getAttributes() throws AMException, SSOException {
        int type = dsManager.getObjectType(token, entryDN);
        profileType = type;
        return super.getAttributes();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#getDN()
     */
    public String getDN() {
        return super.getDN();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#getOrganizationDN()
     */
    public String getOrganizationDN() throws AMException, SSOException {
        return super.getOrganizationDN();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#getParentDN()
     */
    public String getParentDN() {
        return super.getParentDN();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#isExists()
     */
    public boolean isExists() throws SSOException {
        return super.isExists();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#purge(boolean, int)
     */
    public void purge(boolean recursive, int graceperiod) throws AMException,
            SSOException {
        int type = dsManager.getObjectType(token, entryDN);
        profileType = type;
        super.purge(recursive, graceperiod);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#removeAttributes(java.util.Set)
     */
    public void removeAttributes(Set attributes) throws AMException,
            SSOException {
        super.removeAttributes(attributes);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#setAttributes(java.util.Map)
     */
    public void setAttributes(Map attributes) throws AMException, SSOException {
        super.setAttributes(attributes);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#delete()
     */
    public void delete() throws AMException, SSOException {
        delete(false);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#store()
     */
    public void store() throws AMException, SSOException {
        int type = dsManager.getObjectType(token, entryDN);
        profileType = type;
        super.store();
    }

    /*
     * (non-javadoc) This method is used to create the entity (so far only in
     * memory) in the LDAP data store. A string identifying the type of entry
     * being created, has to be passed. The types supported are the ones defined
     * in the configuration of DAI service. Some examples are: "user", "agent".
     */
    public void create(String stype) throws AMException, SSOException {
        String type = (String) AMCommonUtils.supportedTypes.get(stype
                .toLowerCase());
        if (type != null) {
            profileType = Integer.parseInt(type);
            super.create();
        } else {
            throw new AMException(AMSDKBundle.getString("156"), "156");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#activate()
     */
    public void activate() throws AMException, SSOException {
        int type = dsManager.getObjectType(token, entryDN);
        String stype = Integer.toString(type);
        String stAttrName = (String) AMCommonUtils.statusAttributeMap
                .get(stype);
        if (stAttrName != null) {
            profileType = type;
            setStringAttribute(stAttrName, "active");
            store();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#deactivate()
     */
    public void deactivate() throws AMException, SSOException {

        int type = dsManager.getObjectType(token, entryDN);
        String stype = Integer.toString(type);
        String stAttrName = (String) AMCommonUtils.statusAttributeMap
                .get(stype);
        if (stAttrName != null) {
            profileType = type;
            setStringAttribute(stAttrName, "inactive");
            store();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMEntity#isActivated()
     */
    public boolean isActivated() throws AMException, SSOException {
        int type = dsManager.getObjectType(token, entryDN);
        String stype = Integer.toString(type);
        String stAttrName = (String) AMCommonUtils.statusAttributeMap
                .get(stype);
        String stAttrValue = null;
        if (stAttrName != null) {
            profileType = type;
            stAttrValue = getStringAttribute(stAttrName);
        }
        if (stAttrValue == null || stAttrValue.length() == 0
                || stAttrValue.equalsIgnoreCase("active")) {
            return (true);
        } else {
            return (false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.am.sdk.AMObject#getAttributes(java.util.Set)
     */
    public Map getAttributes(Set attributeNames) throws AMException,
            SSOException {
        int type = dsManager.getObjectType(token, entryDN);
        profileType = type;
        return super.getAttributes(attributeNames);
    }

}
