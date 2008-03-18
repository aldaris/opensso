package com.sun.identity.xacml.policy;

import com.sun.identity.shared.test.UnitTestBase;
import com.sun.identity.shared.xml.XMLUtils;

import com.sun.identity.xacml.common.XACMLConstants;
import com.sun.identity.xacml.common.XACMLException;
import com.sun.identity.xacml.common.XACMLSDKUtils;
import com.sun.identity.xacml.policy.PolicyFactory;
import com.sun.identity.xacml.policy.Obligation;

import java.net.URI;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;

import org.testng.annotations.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ObligationTest extends UnitTestBase {

    public ObligationTest() {
        super("FedLibrary-XACML-ObligationTest");
    }

    //@Test(groups={"xacml"}, expectedExceptions={XACMLException.class})
    @Test(groups={"xacml"})
    public void getObligation() throws XACMLException, Exception {

        entering("getObligation()", null);
        log(Level.INFO, "getObligation()","\n");
        log(Level.INFO, "getObligation()","obligation-test-1-b");

        log(Level.INFO, "getObligation()","create empty obligation");
        Obligation obligation = PolicyFactory.getInstance().createObligation();
        log(Level.INFO, "getObligation()","set obligationId");
        obligation.setObligationId(new URI("obligation-10"));
        log(Level.INFO, "getObligation()","set fulfillOn");
        obligation.setFulfillOn("Permit");
        String obligationXml = obligation.toXMLString(true, true);
        log(Level.INFO, "getObligation()","obligation xml:" + obligationXml);
        log(Level.INFO, "getObligation()","create obligation from xml string");
        obligation = PolicyFactory.getInstance().createObligation(
                obligation.toXMLString(true, true));
        obligationXml = obligation.toXMLString(true, true);
        log(Level.INFO, "getObligation()","obligation xml:" + obligationXml);

        List list = new ArrayList();
        Document doc = XMLUtils.newDocument();
        Element elem = doc.createElementNS(XACMLConstants.XACML_NS_URI, 
                "xacml:AttributeAssignment");
        elem.setAttribute("AttributeId", "a-120");
        elem.setAttribute("DataType", "f-120");
        list.add(elem);
        log(Level.INFO, "getObligation()","setting attributeAssignments");
        obligation.setAttributeAssignments(list);
        log(Level.INFO, "getObligation()","obligation xml:" 
                + obligation.toXMLString(true, true));

        log(Level.INFO, "getObligation()","create obligation from xml");
        obligation = PolicyFactory.getInstance().createObligation(
                obligation.toXMLString(true, true));
        log(Level.INFO, "getObligation()","obligation xml:" 
                + obligation.toXMLString(true, true));
        exiting("getObligation()");

    }

}
