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
 * $Id: AccountMapper.java,v 1.1 2006-10-30 23:15:44 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml.plugins;

import com.sun.identity.saml.assertion.Subject;

import java.util.Map;

/**
 * The class <code>AccountMapper</code> is an interface
 * that is implemented to map partner account to user account
 * in Sun Java System Access Manager.  
 * <p>
 * Different partner would need to have a different implementation
 * of the interface. The mappings between the partner source ID and 
 * the implementation class are configured at the
 * <code>Partner URLs</code> 
 * field in SAML service.
 * @deprecated This class has been deprecated. Please use 
 *     <code>PartnerAccountMapper</code> instead.
 * @see PartnerAccountMapper
 *
 * @supported.all.api
 */
public interface AccountMapper {

    /** 
     * Key to hold user DN in returned map
     */
    public static final String NAME = "name";
    /** 
     * Key to hold organization DN in returned map
     */
    public static final String ORG = "org";

    /**
     * Return user account in Sun Java System Access Manager to which the subject
     * is mapped.
     * The returned Map is subject to changes per SAML specification.
     *
     * @param subject  Subject to be mapped
     * @param sourceID source ID for the site from which the subject originated.
     * @return Map which contains <code>NAME</code> and <code>ORG</code> keys,
     *         value of the <code>NAME</code> key is the user DN, value of thes
     *         <code>ORG</code> is the user organization  DN. Return empty map
     *         if the mapped user could not be obtained from the subject.
     */
    public Map getUser(com.sun.identity.saml.assertion.Subject subject,
                       String sourceID);
}
