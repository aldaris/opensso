package com.sun.identity.xacml2.client;

import com.iplanet.sso.SSOToken;

import com.sun.identity.shared.test.UnitTestBase;

import com.sun.identity.saml2.common.SAML2Exception;

import com.sun.identity.xacml2.client.XACML2RequestProcessor;
import com.sun.identity.xacml2.common.XACML2Constants;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.context.ContextFactory;
import com.sun.identity.xacml2.context.Action;
import com.sun.identity.xacml2.context.Attribute;
import com.sun.identity.xacml2.context.Environment;
import com.sun.identity.xacml2.context.Request;
import com.sun.identity.xacml2.context.Resource;
import com.sun.identity.xacml2.context.Response;
import com.sun.identity.xacml2.context.Subject;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.testng.annotations.Test;
import org.testng.annotations.Parameters;

public class XACML2RequestProcessorTest extends UnitTestBase {

    public XACML2RequestProcessorTest() {
        super("OpenFed-xacml2-XACMLRequestProcessorTest");
    }

    //@Test(groups={"xacml2"}, expectedExceptions={XACML2Exception.class})
    //@Test(groups={"xacml2"})
    public void testGetInstance() throws XACML2Exception {
        entering("testGetInstance()", null);
        log(Level.INFO,"testGetInstance()","\n");
        XACML2RequestProcessor.getInstance();
        log(Level.INFO,"testGetInstance()","\n");
        exiting("testGetInstance()");
    }

    @Test(groups={"xacml2"})
    @Parameters({"login.id", "login.password",
            "subject.id", "subject.id.datatype", "subject.category", 
            "resource.id", "resource.id.datatype",
            "resource.servicename", 
            "action.id", "action.id.datatype"})
    public void testProcessRequest(String loginId, String loginPassword,
            String subjectId, String subjectIdType,
            String subjectCategory,
            String resourceId, String resourceIdType,
            String serviceName,
            String actionId, String actionIdType) 
            throws XACML2Exception, SAML2Exception, 
            URISyntaxException, Exception {

        SSOToken ssoToken 
                = TokenUtils.getSessionToken("/", loginId, loginPassword);
        String tokenId = ssoToken.getTokenID().toString();

        Request xacmlRequest = createSampleXacmlRequest(
            tokenId, XACML2Constants.OPENSSO_SESSION_ID,
            XACML2Constants.ACCESS_SUBJECT,
            resourceId, resourceIdType,
            serviceName,
            actionId, actionIdType); 

        log(Level.INFO,"testProcessRequest():xacmlRequest:\n",
                xacmlRequest.toXMLString(true, true));

        Response xacmlResponse = XACML2RequestProcessor.getInstance()
                .processRequest(xacmlRequest, null, null);

        log(Level.INFO,"testProcessRequest():xacmlResponse:\n",
                xacmlResponse.toXMLString(true, true));
    }

    private Request createSampleXacmlRequest(
            String subjectId, String subjectIdType,
            String subjectCategory,
            String resourceId, String resourceIdType,
            String serviceName, 
            String actionId, String actionIdType) 
            throws XACML2Exception, URISyntaxException {
        Request request = ContextFactory.getInstance().createRequest();

        //Subject1, access-subject
        Subject subject1 = ContextFactory.getInstance().createSubject();

        //supported category for id
        //urn:oasis:names:tc:xacml:1.0:subject-category:access-subject
        subject1.setSubjectCategory(
            new URI(XACML2Constants.ACCESS_SUBJECT));

        Attribute attribute = ContextFactory.getInstance().createAttribute();

        //key attribute id
        //urn:oasis:names:tc:xacml:1.0:subject:subject-id
        attribute.setAttributeID(
            new URI(XACML2Constants.SUBJECT_ID));

        //supported data type for id
        //urn:oasis:names:tc:xacml:1.0:data-type:x500Name
        //urn:sun:names:xacml:2.0:data-type:opensso-session-id
        //urn:sun:names:xacml:2.0:data-type:openfm-sp-nameid
        attribute.setDataType(new URI(subjectIdType));

        attribute.setIssuer("sampleIssuer1");

        //set values
        List valueList = new ArrayList();
        valueList.add(subjectId);
        attribute.setAttributeStringValues(valueList);
        List attributeList = new ArrayList();
        attributeList.add(attribute);
        subject1.setAttributes(attributeList);

        //Subject2, intermediary-subject
        Subject subject2 = ContextFactory.getInstance().createSubject();

        subject2.setSubjectCategory(
            new URI(XACML2Constants.INTERMEDIARY_SUBJECT));

        attribute = ContextFactory.getInstance().createAttribute();

        attribute.setAttributeID(
            new URI(XACML2Constants.SUBJECT_ID));

        //supported data type for id
        //urn:oasis:names:tc:xacml:1.0:data-type:x500Name
        //urn:sun:names:xacml:2.0:data-type:opensso-session-id
        //urn:sun:names:xacml:2.0:data-type:openfm-sp-nameid
        attribute.setDataType(new URI(subjectIdType)); 

        attribute.setIssuer("sampleIssuer2");

        //set values
        valueList = new ArrayList();
        valueList.add(subjectId);
        attribute.setAttributeStringValues(valueList);
        attributeList = new ArrayList();
        attributeList.add(attribute);
        subject2.setAttributes(attributeList);

        //set subjects in request
        List subjectList = new ArrayList();
        subjectList.add(subject1);
        subjectList.add(subject2);
        request.setSubjects(subjectList);

        //Resource
        Resource resource = ContextFactory.getInstance().createResource();

        //resoruce-id attribute
        attribute = ContextFactory.getInstance().createAttribute();

        //key attribute id
        //urn:oasis:names:tc:xacml:1.0:resource:resource-id
        attribute.setAttributeID(
            new URI(XACML2Constants.RESOURCE_ID));

        //supported data type
        //http://www.w3.org/2001/XMLSchema#string
        attribute.setDataType(
            new URI(XACML2Constants.XS_STRING));

        attribute.setIssuer("sampleIssuer3");


        //set values
        valueList = new ArrayList();
        valueList.add(resourceId);
        attribute.setAttributeStringValues(valueList);

        attributeList = new ArrayList();
        attributeList.add(attribute);

        //serviceName attribute
        attribute = ContextFactory.getInstance().createAttribute();

        //additional attribute id
        //urn:sun:names:xacml:2.0:resource:target-service
        attribute.setAttributeID(
            new URI(XACML2Constants.TARGET_SERVICE));

        //supported data type
        //http://www.w3.org/2001/XMLSchema#string
        attribute.setDataType(
            new URI(XACML2Constants.XS_STRING));

        attribute.setIssuer("sampleIssuer3");


        //set values
        valueList = new ArrayList();
        valueList.add(serviceName);
        attribute.setAttributeStringValues(valueList);

        attributeList.add(attribute);

        resource.setAttributes(attributeList);

        List resourceList = new ArrayList();
        resourceList.add(resource);
        request.setResources(resourceList);

        //Action
        Action action = ContextFactory.getInstance().createAction();

        attribute = ContextFactory.getInstance().createAttribute();

        //key attribute id
        //urn:oasis:names:tc:xacml:1.0:action:action-id
        attribute.setAttributeID(
            new URI(XACML2Constants.ACTION_ID));

        //supported data type
        //http://www.w3.org/2001/XMLSchema#string
        attribute.setDataType(
            new URI(XACML2Constants.XS_STRING));

        attribute.setIssuer("sampleIssuer5");

        valueList = new ArrayList();
        valueList.add(actionId);
        attribute.setAttributeStringValues(valueList);
        attributeList = new ArrayList();
        attributeList.add(attribute);

        action.setAttributes(attributeList);

        request.setAction(action);

        //Enviornment
        Environment environment = ContextFactory.getInstance().createEnvironment();
        request.setEnvironment(environment);
        return request;
    }

}
