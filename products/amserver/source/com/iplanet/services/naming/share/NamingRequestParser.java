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
 * $Id: NamingRequestParser.java,v 1.1 2005-11-01 00:30:23 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.services.naming.share;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.iplanet.am.util.Debug;
import com.iplanet.am.util.XMLUtils;

class NamingRequestParser {

    /**
     * Document to be parsed
     */
    private Document document;

    static Debug debug = Debug.getInstance("amNaming");

    public NamingRequestParser(Document xmlDoc) {
        document = xmlDoc;
    }

    public NamingRequestParser(String xmlString) {
        document = XMLUtils.toDOMDocument(xmlString, debug);
    }

    /**
     * Parses the naming request xml document. Please see file NamingRequest.dtd
     * for the corresponding DTD of the request.
     * 
     * @return a NamingRequest object.
     */
    public NamingRequest parseXML() {
        if (document == null) {
            return null;
        }

        // get naming request element
        Element elem = document.getDocumentElement();
        NamingRequest namingRequest = new NamingRequest();

        // set naming request attributes
        String temp = elem.getAttribute("vers");
        if (temp != null) {
            namingRequest.setRequestVersion(temp);
        }
        temp = elem.getAttribute("reqid");
        if (temp != null) {
            namingRequest.setRequestID(temp);
        }
        temp = elem.getAttribute("sessid");
        if ((temp != null) && ((temp.trim()).length() != 0)) {
            namingRequest.setSessionId(temp);
        } else {
            namingRequest.setSessionId(null);
        }

        return namingRequest;
    }
}
