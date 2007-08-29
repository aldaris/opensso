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
 * $Id: RequestTest.java,v 1.1 2007-08-29 23:41:59 dillidorai Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.xacml.context;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import com.sun.identity.shared.test.UnitTestBase;
import com.sun.identity.xacml.context.Attribute;
import com.sun.identity.xacml.context.Subject;
import com.sun.identity.xacml.context.Resource;
import com.sun.identity.xacml.context.ContextFactory;
import com.sun.identity.xacml.common.XACMLException;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.identity.shared.xml.XMLUtils;
import org.xml.sax.SAXException;
import java.net.URI;

import java.util.Iterator;
import java.util.List;

/**
 * Unit Test Cases to test
 * <code>com.sun.identity.xacml.context.Request</code> class.
 */

public class RequestTest extends UnitTestBase {
    
    public RequestTest() {
        super("FedLibrary-XACML");
    }
    
    /**
     * Validates the <code>Request</code> object.
     *
     * @param xmlFile the file containing the Request XML.
     * @throws XACMLException if there is creating the <code>Request</code>
     *         object or the XML String does not conform to the XML Schema
     * @throws ParserConfigurationException if there is an error parsing the
     *         Request XML string.
     * @throws IOException if there is an error reading the file.
     * @throws SAXException if there is an error during XML parsing.
     */
    @Parameters({"request-filename"})
    @Test(groups = {"xacml"})
    public void validateRequest(String xmlFile) throws XACMLException,
            ParserConfigurationException, IOException,
            SAXException {
        entering("validateRequest",null);
        try {
            log(Level.INFO, "validateRequest",xmlFile);
            FileInputStream fis = new FileInputStream(new File(xmlFile));
            Document doc = XMLUtils.toDOMDocument(fis, null);
            Element elt = doc.getDocumentElement();
            Request request =
                    ContextFactory.getInstance().createRequest(elt);
            
            // object to xml string
            String xmlString = request.toXMLString(true,true);
            System.out.println("xmlString:"+ xmlString);
            assert (xmlString != null) :
                "Error creating XML String from Request object";
            log(Level.INFO, "validateRequest",xmlString);
            
            validateAction(request.getAction());
            validateEnvironment(request.getEnvironment());
            // recreate Request from xml
            request = ContextFactory.getInstance().createRequest(xmlString);
            validateSubjects(request.getSubjects());
            validateResources(request.getResources());
            validateAction(request.getAction());
            validateEnvironment(request.getEnvironment());

            
            log(Level.INFO, "createRequest",xmlString);
        } finally {
            exiting("createRequest");
        }
    }
    
    /**
     * Validates the <code>Action</code> element in the Request.
     *
     * @param actions the <code>Action</code> in the request.
     */
    private void validateAction(Action action) throws XACMLException {
        List attributes = action.getAttributes();
        System.out.println("Action XML :"+action.toXMLString(true, true));
        for (int i = 0; i < attributes.size(); i++) {
	    Attribute attribute = (Attribute)attributes.get(i);
            System.out.println("attribute string:"+attribute.toXMLString(true, true));
            System.out.println("issuer:"+attribute.getIssuer());
            System.out.println("attributId:"+attribute.getAttributeID());
            System.out.println("datatype:"+attribute.getDataType());
            for (int j=0; j < attribute.getAttributeValues().size(); j++) {
                Element value = (Element)attribute.getAttributeValues().get(j);
                System.out.println("value:"
                    +XMLUtils.print(value));
            }
        }
    }
             
    /**
     * Validates the <code>Resource</code> element in the Request.
     *
     * @param actions the <code>Resource</code> in the request.
     */
    private void validateResources(List resources) throws XACMLException {
        for (int i = 0 ; i < resources.size(); i++) {
	    Resource resource = (Resource)resources.get(i);
            List resAttrs = resource.getAttributes();
            for (int j = 0; j< resAttrs.size(); j++) {
                Attribute attr = (Attribute)resAttrs.get(j);
                System.out.println("issuer:"+attr.getIssuer());
                System.out.println("attributId:"+attr.getAttributeID());
                System.out.println("datatype:"+attr.getDataType());
            }
        }
    }
             
    /**
     * Validates the <code>Subject</code> element in the Request.
     *
     * @param actions the <code>Subject</code> in the request.
     */
    private void validateSubjects(List subjects) throws XACMLException {
        for (int i = 0; i < subjects.size(); i++) {
	    Subject subject = (Subject)subjects.get(i);
            System.out.println("subjectCategory:"+subject.getSubjectCategory());
            List subAttrs = subject.getAttributes();
            for (int j = 0; j< subAttrs.size(); j++) {
                Attribute attr = (Attribute)subAttrs.get(j);
                System.out.println("issuer:"+attr.getIssuer());
                System.out.println("attributId:"+attr.getAttributeID());
                System.out.println("datatype:"+attr.getDataType());
            }
        }
    }
             
    /**
     * Validates the <code>Environment</code> element in the Request.
     *
     * @param env the <code>Environment</code> in the request.
     */
    private void validateEnvironment(Environment environment) throws XACMLException {
        List attributes = environment.getAttributes();
        System.out.println("Environment XML :"+environment.toXMLString(true, true));
        for (int i = 0; i < attributes.size(); i++) {
	    Attribute attribute = (Attribute)attributes.get(i);
            System.out.println("issuer:"+attribute.getIssuer());
            System.out.println("attributId:"+attribute.getAttributeID());
            System.out.println("datatype:"+attribute.getDataType());
        }
    }
}
