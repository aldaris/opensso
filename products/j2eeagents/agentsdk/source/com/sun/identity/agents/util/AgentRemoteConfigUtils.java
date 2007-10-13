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
 * $Id: AgentRemoteConfigUtils.java,v 1.1 2007-10-13 00:09:50 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.util;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.xml.XMLUtils;

import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.identity.agents.arch.AgentException;

public class AgentRemoteConfigUtils {

    static final String USER_DETAILS = "userdetails";
    static final String ATTRIBUTE = "attribute";
    static final String ATTRIBUTE_NAME = "name";
    static final String VALUE = "value";
    static final String ATTRIBUTE_SERVICE = "/xml/attributes?subjectid=";
    
    /**
     * Returns <code>Properties</code> object constructed from XML.
     *
     * @param xml the XML document for the <code>Properties</code> object.
     * @return constructed <code>Properties</code> object.
     */
    public static Properties getAgentProperties(Vector urls, String tokenId)
        throws AgentException
    {
        Properties result = new Properties();
        if (urls == null) {
            return result;
        }
        
        String xml = null;
        for (int i = 0; i < urls.size(); i++) {
            URL url = (URL)urls.get(i);
            xml = getAttributesInXMLFromRest(url, tokenId);
            if (xml != null) {
                break;
            }
        }
        if (xml != null) {
            Document doc = null;
            try {
                doc = XMLUtils.getXMLDocument(
                     new ByteArrayInputStream(xml.getBytes("UTF-8")));
            } catch (Exception xe) {
                throw new AgentException("xml parsing error", xe);
            }

            // get the root node of agent configuration response
            Element rootElement = doc.getDocumentElement();;
            if (rootElement == null) {
                throw new AgentException("Invalid root element");
            }
        
            String elemName = rootElement.getTagName();
            if (elemName == null) {
                throw new AgentException("Missing local name");
            } else if (!elemName.equals(USER_DETAILS)) {
                throw new AgentException("Invalid root element name");
            }

            // Get all the agent configuration attribute nodes
            NodeList nodes = rootElement.getChildNodes();
            int numOfNodes = nodes.getLength();
            if (numOfNodes < 1) {
                throw new AgentException("Missing agent configuration elements");
            }
            int nextElem = 0;
            while (nextElem < numOfNodes) {
                Node elem = (Node)nodes.item(nextElem);
                if (elem.getNodeType() == Node.ELEMENT_NODE) {
                    if (elem.getNodeName().equals(ATTRIBUTE)) {
                        String attrName = ((Element)elem).getAttribute(
                                ATTRIBUTE_NAME);
                        if ((attrName == null) || (attrName.trim().length() == 0)) {
                            throw new AgentException("Missing attribute name");
                        }
                        NodeList valueNodes = elem.getChildNodes();
                        int numOfValueNodes = valueNodes.getLength();
                        int nextValueNode = 0;
                        while (nextValueNode < numOfValueNodes) {
                            Node valueNode = (Node)valueNodes.item(nextValueNode);
                            if (valueNode.getNodeType() == Node.ELEMENT_NODE) {
                                if (valueNode.getNodeName().equals(VALUE)) {
                                    String value = XMLUtils.getElementValue(
                                                     (Element)valueNode);
                                    setProp(result, attrName, value);
                                }
                            }
                            nextValueNode++;
                        }    
                    }
                }
                nextElem++;
            }
        }
        return result;
    }

    private static void setProp(Properties prop, 
                                String attrName, 
                                String attrValue) {
        if ((attrName == null) || (attrName.trim().length() == 0)) {
            return;
        }
        if (attrValue == null) {
            attrValue = "";
        } else {
            attrValue = attrValue.trim();
            if (attrValue.indexOf("[") == 0) {
                int idx = attrValue.indexOf("]");
                if (idx > 0) {
                    int delimitIndex = attrValue.indexOf("=", idx);
                    if (delimitIndex > idx) {
                        String indexKey = attrValue.substring(0, idx+1);
                        attrName += indexKey; 
                        attrValue = attrValue.substring(delimitIndex+1).trim();
                    }
                }
            }
        }
        prop.setProperty(attrName, attrValue);
        return;
    }
    
    private static String getAttributesInXMLFromRest(URL url, String tokenId) 
        throws AgentException {
	HttpURLConnection conn = null;
        char[] buf = new char[1024];
        StringBuffer in_buf = new StringBuffer();
        int len;
	try {
            String attributeServiceURL = 
                    url + ATTRIBUTE_SERVICE + URLEncoder.encode(tokenId, "UTF-8"); 
            URL serviceURL = new URL(attributeServiceURL);
	    conn = (HttpURLConnection)serviceURL.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
	    while((len = in.read(buf,0,buf.length)) != -1) {
	        in_buf.append(buf,0,len);
	    }
	} catch (Exception e) {
           throw new AgentException(
                   "Fetching Agent configuration properties failed", e);
	} 
        return in_buf.toString();
    }
} 
