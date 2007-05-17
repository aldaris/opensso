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
 * $Id: FMSubjectMapper.java,v 1.2 2007-05-17 22:42:23 pawand Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.plugins;
import com.sun.identity.xacml2.context.Subject;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.context.Attribute;
import com.sun.identity.xacml2.spi.SubjectMapper;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.iplanet.sso.SSOException;
import com.sun.identity.shared.xml.XMLUtils;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * This class implements SubjectMapper to map between XACML context 
 * Subject and native subject
 * This mapper recognises only the following XACML specification defined
 * attributeId
 * <pre>
 * urn:oasis:names:tc:xacml:1.0:subject:subject-id
 * </pre>
 * Only following dataTypes would be understood for subject-id
 * <pre>
 * urn:oasis:names:tc:xacml:1.0:data-type:x500Name
 * urn:sun:names:xacml:2.0:data-type:opensso-session-id
 * urn:sun:names:xacml:2.0:data-type:openfm-sp-nameid
 * </pre>
 * Only following value would be accepted for subject-category attribute 
 * of Subject
 * <pre>
 * urn:oasis:names:tc:xacml:1.0:subject-category:access-subject
 * </pre>
 * If the attribute or the value is not specified in the request, it would
 * default to this value. The Subject would map to null if a different
 * value has been specified
 * in error condition.
 *
 */
public class FMSubjectMapper implements SubjectMapper {

    /**
     * Initializes the mapper implementation. This would be called immediately 
     * after constructing an instance of the implementation.
     *
     * @param pdpEntityId EntityID of PDP
     * @param pepEntityId EntityID of PEP
     * @param properties configuration properties
     * @exception XACML2Exception if can not initialize
     */
    public void initialize(String pdpEntityId, String pepEntityId, Map properties) 
            throws XACML2Exception {
    }

    /**
     * Returns native subject, OpenSSO SSOToken
     * @param xacmlContextSubjects XACML  context Subject(s) from the
     * xacml-context:Request
     * @return native subject, OpenSSO SSOToken, returns null if 
     *         Subject did not match
     * @exception XACML2Exception if can not map to native subject
     */
    public Object mapToNativeSubject(Subject[] xacmlContextSubjects) 
            throws XACML2Exception {

        // Method curently supports only 
        // urn:sun:names:xacml:2.0:data-type:opensso-session-id
        // TODO : Support for
        // urn:oasis:names:tc:xacml:1.0:data-type:x500Name
        // urn:sun:names:xacml:2.0:data-type:openfm-sp-nameid

        if (xacmlContextSubjects == null) {
            return null;
        }
        String sid = null;
        for (int subCount=0;subCount<xacmlContextSubjects.length;subCount++) {
            Subject subject = xacmlContextSubjects[subCount];
            if (subject == null) {
                continue;
            }
            URI subjectCategory = subject.getSubjectCategory();
            if ((subjectCategory != null) && (!subjectCategory.toString().
                equals(XACML2Constants.ACCESS_SUBJECT))) {
                continue;
            }
            List attributes = subject.getAttributes();
            if (attributes != null) {
                for (int count = 0; count < attributes.size(); count++) {
                    Attribute attr = (Attribute) attributes.get(count);
                    if (attr != null) {
                        URI tmpURI = attr.getAttributeID();
                        if (tmpURI.toString().equals(XACML2Constants.
                            SUBJECT_ID)) {
                            tmpURI = attr.getDataType();
                            if (tmpURI.toString().equals(XACML2Constants.
                                OPENSSO_SESSION_ID)) {
                                Element sidElement = (Element)attr.getAttributeValues().get(0);
                                sid = XMLUtils.getElementValue(sidElement);
                            }
                        }
                    }
            	}
            }
            if (sid != null) {
                break;
            }
        }
        SSOToken ssoToken = null;
        if (sid != null) {
            try {
                SSOTokenManager tokenManager = SSOTokenManager.getInstance();
                ssoToken = tokenManager.createSSOToken(sid);
            } catch (SSOException ssoExp) {
                throw  new XACML2Exception(ssoExp);
            }
        }
        return ssoToken;
    }
}

