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
 * $Id: STSClientUserToken.java,v 1.1 2007-11-01 17:24:16 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.sts;

import com.sun.xml.ws.security.Token;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.iplanet.sso.SSOTokenManager;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import com.sun.identity.shared.xml.XMLUtils;
import javax.xml.parsers.ParserConfigurationException;


/**
 * This class implements JAX-WS Token for on behalf of token that will be used 
 * to pass end user FAM SSO Token to the STS service.
 */
public class STSClientUserToken implements Token {
        
    String tokenId = null;
    String tokenType = null;
    
    /** Creates a new instance of STSClientUserToken */
    public STSClientUserToken(SSOToken ssoToken) throws FAMSTSException {
        try {
            SSOTokenManager.getInstance().validateToken(ssoToken);
            tokenId = ssoToken.getTokenID().toString();
            tokenType = STSConstants.SSO_TOKEN_TYPE;
        } catch (SSOException se) {
            throw new FAMSTSException(se.getMessage());
        }
    }
    
    public STSClientUserToken(Element element) throws FAMSTSException {
        if(element == null) {
           throw new FAMSTSException();
        }
        
        String localName = element.getLocalName();
        if(!"FAMToken".equals(localName)) {
           throw new FAMSTSException();
        }
        
        NodeList nl = element.getChildNodes();
        int length = nl.getLength();
        if(length == 0) {
           throw new FAMSTSException();
        }
        
        for (int i=0; i < length; i++ ) {
            Node child = (Node)nl.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            
            String childName = child.getLocalName();
            if(childName.equals("TokenValue")) {
               tokenId = XMLUtils.getElementValue((Element)child);
            } else if (childName.equals("TokenType")) {                
               tokenType = XMLUtils.getElementValue((Element)child);
            }
        }
    }
    
    public Element getTokenValue() {        
        Document document = XMLUtils.toDOMDocument(toString(), STSUtils.debug);        
        return document.getDocumentElement();
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public String getTokenId() {
        return tokenId;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<fam:FAMToken xmlns:fam=\"")
          .append(STSConstants.FAM_TOKEN_NS).append("\"").append(">")
          .append("\n").append("<fam:TokenValue>").append(tokenId)
          .append("</fam:TokenValue>").append("\n").append("<fam:TokenType>")
          .append(tokenType).append("</fam:TokenType>").append("\n")
          .append("</fam:FAMToken>");
        return sb.toString();
    }
    
    public String getType() {
        return tokenType;
    }
    
}
