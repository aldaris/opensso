package com.sun.identity.xacml.client;

import com.sun.identity.shared.test.UnitTestBase;

import com.sun.identity.saml2.common.SAML2Exception;

import com.sun.identity.xacml.client.XACMLRequestProcessor;
import com.sun.identity.xacml.common.XACMLException;
import com.sun.identity.xacml.context.ContextFactory;
import com.sun.identity.xacml.context.Action;
import com.sun.identity.xacml.context.Attribute;
import com.sun.identity.xacml.context.Environment;
import com.sun.identity.xacml.context.Request;
import com.sun.identity.xacml.context.Resource;
import com.sun.identity.xacml.context.Response;
import com.sun.identity.xacml.context.Subject;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.testng.annotations.Test;

public class XACMLRequestProcessorTest extends UnitTestBase {

    public XACMLRequestProcessorTest() {
        super("OpenFed-xacml-XACMLReuestProcessorTest");
    }

    //@Test(groups={"xacml"}, expectedExceptions={XACMLException.class})
    @Test(groups={"xacml"})
    public void testGetInstance() throws XACMLException {
        entering("testGetInstance()", null);
        log(Level.INFO,"testGetInstance()","\n");
        XACMLRequestProcessor.getInstance();
        log(Level.INFO,"testGetInstance()","\n");
        exiting("testGetInstance()");
    }

    @Test(groups={"xacml"})
    public void processRequest() 
            throws XACMLException, SAML2Exception, URISyntaxException {
        Request xacmlRequest = createSampleXacmlRequest();
        log(Level.INFO,"processRequest():xacmlRequest:\n",
                xacmlRequest.toXMLString(true, true));
        Response xacmlResponse = XACMLRequestProcessor.getInstance()
                .processRequest(xacmlRequest, null, null);
        log(Level.INFO,"processRequest():xacmlResponse:\n",
                xacmlResponse.toXMLString(true, true));
    }

    //temporay for testing
    private Request createSampleXacmlRequest()
            throws XACMLException, URISyntaxException {
        Request request = ContextFactory.getInstance().createRequest();

        Subject subject1 = ContextFactory.getInstance().createSubject();

        //supported category for id
        //urn:oasis:names:tc:xacml:1.0:subject-category:access-subject
        subject1.setSubjectCategory(
            new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"));

        Attribute attribute = ContextFactory.getInstance().createAttribute();
        attribute.setIssuer("sampleIssuer1");

        //key attribute id
        //urn:oasis:names:tc:xacml:1.0:subject:subject-id
        attribute.setAttributeID(
            new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"));

        //supported data type for id
        //urn:oasis:names:tc:xacml:1.0:data-type:x500Name
        //urn:sun:names:xacml:2.0:data-type:opensso-session-id
        //urn:sun:names:xacml:2.0:data-type:openfm-sp-nameid
        attribute.setDataType(
            new URI("urn:opensso:names:xacml:2.0:data-type:opensso-session-id"));

        List valueList = new ArrayList();
        valueList.add("sessionId1");
        valueList.add("sessionId2");
        attribute.setAttributeStringValues(valueList);
        List attributeList = new ArrayList();
        attributeList.add(attribute);
        subject1.setAttributes(attributeList);

        Subject subject2 = ContextFactory.getInstance().createSubject();
        subject2.setSubjectCategory(
            new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"));
        attribute = ContextFactory.getInstance().createAttribute();
        attribute.setIssuer("sampleIssuer2");
        attribute.setAttributeID(
            new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"));
        attribute.setDataType(
            new URI("urn:sun:names:xacml:2.0:data-type:openfm-sp-nameid"));
        valueList = new ArrayList();
        valueList.add("openfm-sp-nameid1");
        attribute.setAttributeStringValues(valueList);
        attributeList = new ArrayList();
        attributeList.add(attribute);
        subject2.setAttributes(attributeList);

        List subjects = new ArrayList();
        subjects.add(subject1);
        subjects.add(subject2);
        request.setSubjects(subjects);

        Resource resource = ContextFactory.getInstance().createResource();
        attribute = ContextFactory.getInstance().createAttribute();
        attribute.setIssuer("sampleIssuer3");

        //key attribute id
        //urn:oasis:names:tc:xacml:1.0:resource:resource-id
        //additional attribute id
        //urn:opensso:names:xacml:2.0:resource:target-service
        attribute.setAttributeID(
            new URI("urn:oasis:names:tc:xacml:1.0:resource:resource-id"));

        //supported data type
        //http://www.w3.org/2001/XMLSchema#string
        attribute.setDataType(
            new URI("http://www.w3.org/2001/XMLSchema#string"));
        valueList = new ArrayList();
        valueList.add("http://insat.red.iplanet.com/banner.html");
        attribute.setAttributeStringValues(valueList);
        attributeList = new ArrayList();
        attributeList.add(attribute);

        attribute = ContextFactory.getInstance().createAttribute();
        attribute.setIssuer("sampleIssuer4");
        attribute.setAttributeID(
            new URI("urn:oasis:names:tc:xacml:1.0:resource:resource-id"));
        attribute.setDataType(
            new URI("http://www.w3.org/2001/XMLSchema#string"));
        valueList = new ArrayList();
        valueList.add("http://insat.red.iplanet.com/banner.html");
        attribute.setAttributeStringValues(valueList);
        attributeList.add(attribute);

        resource.setAttributes(attributeList);
        List resourceList = new ArrayList();
        resourceList.add(resource);
        request.setResources(resourceList);

        Action action = ContextFactory.getInstance().createAction();
        attribute = ContextFactory.getInstance().createAttribute();
        attribute.setIssuer("sampleIssuer5");

        //key attribute id
        //urn:oasis:names:tc:xacml:1.0:action:action-id
        attribute.setAttributeID(
            new URI("urn:oasis:names:tc:xacml:1.0:action:action-id"));

        //supported data type
        //http://www.w3.org/2001/XMLSchema#string
        attribute.setDataType(
            new URI("http://www.w3.org/2001/XMLSchema#string"));
        valueList = new ArrayList();
        valueList.add("GET");
        attribute.setAttributeStringValues(valueList);
        attributeList = new ArrayList();
        attributeList.add(attribute);

        action.setAttributes(attributeList);

        request.setAction(action);

        Environment environment = ContextFactory.getInstance().createEnvironment();
        request.setEnvironment(environment);
        return request;
    }

}
