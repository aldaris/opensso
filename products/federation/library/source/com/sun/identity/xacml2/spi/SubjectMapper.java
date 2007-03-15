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
 * $Id: SubjectMapper.java,v 1.1 2007-03-15 06:23:00 bhavnab Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.spi;
import com.sun.identity.xacml2.context.Subject;
import com.sun.identity.xacml2.common.XACML2Exception;
import java.util.Map;

    /**
     * This is an interface which provides an SPI to be able to map the <code>
     * Subject</code> in the XACML <code>Request</code> to an Object which
     * represents the "subject" in the Federation manager context. A plugin 
     * implementing this SPI needs to be defined  and configured at the PDP 
     * end for each trusted PEP ( as part of the metadata). A default 
     * mapper has been provided out-of-box which would map the XACML 
     * <code>Subject</code> to a <code>SSOToken</code>,
     * the <code>SSOToken</code>  being  the representation of the 
     * <code>Subject</code> in federation manager.
     */

public interface SubjectMapper {

    /**
     * Initializes the configuration data needed by this mapper.  It uses the
     * the entity IDs passed as parameters as index to the local metadata.
     * It can also consume a  generic <code>Map</code> of key-value pairs
     * to define its configuration in addition to the metadata.
     * @param pdpEntityId entity id of the PDP which is doing this subject 
     *        mapping and who has received the XACML request
     * @param pepEntityId entity id of the PEP ( requester) of the 
     *        policy decision.
     * @param properties <code>Map</code> of other properties which can be
     *        consumed by this mapper to do the subject mapping.
     * @exception XACML2Exception if the configration intialization 
     *        encounters an error condition.
     */

    public void initialize(String pdpEntityId, String pepEntityId, 
        Map properties) throws XACML2Exception;

    /**
     * This is the main API which does the mapping of XACML <code>Subject</code>
     * to native subject ( native being subject in the context of the federation
     * manager).
     * @param xacmlContextSubject <code>xacml-context:Subject</code> from the
     * <code>xacml-context:Request</code> object.
     * @return <code>Object</code> representing the native mapped subject 
     * which in the default implementation would be a <code>SSOToken</code>.
     * if mapping is successful. In case of failure to match on the part of the
     * PDP, then null would be returned.
     * @exception XACML2Exception if an error conditions occurs.
     */
    public Object mapToNativeSubject(Subject xacmlContextSubject) 
            throws XACML2Exception;

}

