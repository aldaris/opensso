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
 * $Id: DefaultNameIdentifierMapper.java,v 1.1 2006-11-30 02:31:21 bina Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.saml.plugins;

import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.plugin.session.SessionException;

import com.sun.identity.saml.assertion.NameIdentifier;
import com.sun.identity.saml.common.SAMLException;
import com.sun.identity.saml.common.SAMLUtils;


/**
 * The class <code>NameIdentifierMapper</code> is an interface
 * that is implemented to map user account to name identifier in
 * assertion subject.
 */
public class DefaultNameIdentifierMapper implements NameIdentifierMapper {

    /**
     * Returns name identifier for assertion subject based on user account.
     *
     * @param session the session of the user performing the operation.
     * @param sourceID source ID for the site from which the assertion
     *        originated.
     * @param destID destination ID for the site for which the assertion will be
     *     created.
     * @return a <code>NameIdentifier</code> for assertion subject.
     * @exception SAMLException if an error occurs
     */
    public NameIdentifier getNameIdentifier(Object session, String sourceID,
         String destID) throws SAMLException {

        if (SAMLUtils.debug.messageEnabled()) {
            SAMLUtils.debug.message("DefaultNameIdentifierMapper." +
                "getNameIdentifier: sourceID = " + sourceID + ", destID = " +
                destID);
        }

        try {
            String nameQualifier = XMLUtils.escapeSpecialCharacters(
                (SessionManager.getProvider()
                               .getProperty(session,"Organization")[0]));
            String name = XMLUtils.escapeSpecialCharacters(
                SessionManager.getProvider().getPrincipalName(session));
            return new NameIdentifier(name, nameQualifier);
        } catch (SessionException sx) {
            SAMLUtils.debug.error("DefaultNameIdentifierMapper." +
                "getNameIdentifier: Invalid Session ", sx);
            return null;
        } catch (Exception ex) {
            SAMLUtils.debug.error("DefaultNameIdentifierMapper." +
                "getNameIdentifier:", ex);
            return null;
        }
    }
}
