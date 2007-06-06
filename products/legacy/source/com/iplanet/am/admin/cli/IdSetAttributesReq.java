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
 * $Id: IdSetAttributesReq.java,v 1.1 2007-06-06 05:55:56 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.admin.cli;

import com.iplanet.am.util.PrintUtils;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class IdSetAttributesReq extends AdminReq {
    private Map attrMap = null;
    private String realmPath = null;
    private String idName = null;
    private IdType idType;

    /**
     * Constructs a new IdSetAttributesReq.
     *
     * @param  targetDN the parent Realm DN. 
     */        
    IdSetAttributesReq(String targetDN) {
        super(targetDN);
        realmPath = targetDN;
    }

    /**
     * sets the Identity Name for this request
     *
     * @param identName the Name of the Identity
     */
    void setIdName(String identName) {
        idName = identName;
    }

    /**
     * sets the Identity Type for this request
     *
     * @param identType the Type of the Identity
     */
    void setIdType(IdType identType) {
        idType = identType;
    }

    /**
     * sets the service attribute Map for this request
     *
     * @param svcAttrMap the Map of attribute value pairs
     */
    void setAttrMap(Map svcAttrMap) {
        attrMap = svcAttrMap;
    }

    /**
     * converts this object into a string.
     *
     * @return String. 
     */
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter prnWriter = new PrintWriter(stringWriter);
        PrintUtils prnUtl = new PrintUtils(prnWriter); 
        prnWriter.println(AdminReq.bundle.getString("requestdescription133") +
            " " + targetDN);
        if ((attrMap != null) && (!attrMap.isEmpty())) {
            Set set = attrMap.keySet();
            for (Iterator it=set.iterator(); it.hasNext(); ) {
                String key = (String)it.next();
                prnWriter.println("  " + key + " =");
                Set valSet = (Set)attrMap.get(key);
                for (Iterator it2=valSet.iterator(); it2.hasNext(); ) {
                    String val = (String)it2.next();
                    prnWriter.println("    " + val);
                }
            }
        }
        prnWriter.flush();
        return stringWriter.toString();    
    }
    
    void process(SSOToken ssoToken)
        throws AdminException
    {
        AdminReq.writer.println(bundle.getString("identity") + "\n" +
            bundle.getString("setAttrId") + " " +
            bundle.getString("foridentity") + " " +
            idName + " " +
            bundle.getString("of") + " " +
            idType.toString() + " " +
            bundle.getString("inrealm") + " " + realmPath);

        PrintUtils prnUtl = new PrintUtils(AdminReq.writer);
        prnUtl.printAVPairs(attrMap, 1);

        String[] args = {idType.toString(), idName, realmPath};

        try {
            AMIdentity ai2use = new AMIdentity(ssoToken,
                idName, idType, realmPath, null);
            doLog(args, AdminUtils.SET_ATTRIBUTES_IDENTITY_ATTEMPT);
            ai2use.setAttributes(attrMap);
            ai2use.store();
            doLog(args, AdminUtils.SET_ATTRIBUTES_IDENTITY);
        } catch (IdRepoException ire) {
            throw new AdminException(ire);
        } catch (SSOException ssoex) {
            throw new AdminException(ssoex);
        }
    }
}

