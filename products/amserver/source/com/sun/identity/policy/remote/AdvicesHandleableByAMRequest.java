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
 * $Id: AdvicesHandleableByAMRequest.java,v 1.1 2006-04-26 05:15:09 dillidorai Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */



package com.sun.identity.policy.remote;
import org.w3c.dom.Node;


/**
 * This <code>AdvicesHandleableByAMRequest</code> class represents a 
 * AdvicesHandleableByAMRequest XML element. The AdvicesHandleableByAMRequest DTD is 
 * defined as the following:
 * <p>
 * <pre>
 *    <!-- AdvicesHandleableByAMRequest element requests the server to return
 *         the names of policy advices that could be handled by Access Manager
 *         if PEP redirects the user agent to Access Manager
 *    -->
 *
 *    <!ELEMENT    AdvicesHandleableByAMRequest    EMPTY >
 * </pre>
 * <p>
 */
public class AdvicesHandleableByAMRequest {
    static final String ADVICES_HANDLEABLE_BY_AM_REQUEST_XML =
        "<AdvicesHandleableByAMRequest/>";
    
    /** 
     * Default constructor for <code>AdvicesHandleableByAMRequest</code>.
     */
    public AdvicesHandleableByAMRequest() {
    }

    /**
     * Returns an <code>AdvicesHandleableByAMRequest</code>
     * object based on the XML string.
     *
     * @param node the XML DOM node for the
     *        <code>AdvicesHandleableByAMRequest</code> object.
     * @return <code>AdvicesHandleableByAMRequest</code> object constructed
     *         from the XML string.
     */
    public static AdvicesHandleableByAMRequest parseXML(Node node)
        throws PolicyEvaluationException {
        return new AdvicesHandleableByAMRequest(); 
    }

    /**
     * Returns a string representation of an
     * <code>AdvicesHandleableByAMRequest</code> object.
     *
     * @return a XML string representation of this object.
     */
    public String toXMLString() {
        return ADVICES_HANDLEABLE_BY_AM_REQUEST_XML;
    }
}
