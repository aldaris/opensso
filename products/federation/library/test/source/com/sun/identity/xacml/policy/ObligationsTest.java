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

public class ObligationsTest extends UnitTestBase {

    public ObligationsTest() {
        super("FedLibrary-XACML-ObligationTest");
    }

    //@Test(groups={"xacml"}, expectedExceptions={XACMLException.class})
    @Test(groups={"xacml"})
    public void getObligations() throws XACMLException, Exception {

        entering("getObligations()", null);
        log(Level.INFO, "getObligations()","obligations-test-1-b");

        log(Level.INFO, "getObligations()","create obligation1");
        Obligation obligation1 = PolicyFactory.getInstance().createObligation();
        log(Level.INFO, "getObligation()","set obligationId");
        obligation1.setObligationId(new URI("obligation-10"));
        log(Level.INFO, "getObligation()","set fulfillOn");
        obligation1.setFulfillOn("Permit");

        log(Level.INFO, "getObligations()","create obligation2");
        Obligation obligation2 = PolicyFactory.getInstance().createObligation();
        obligation2.setObligationId(new URI("obligation-20"));
        obligation2.setFulfillOn("Permit");
        List list = new ArrayList();
        Document doc = XMLUtils.newDocument();
        Element elem = doc.createElementNS(XACMLConstants.XACML_NS_URI, 
                "xacml:AttributeAssignment");
        elem.setAttribute("AttributeId", "a-120");
        elem.setAttribute("DataType", "f-120");
        list.add(elem);
        log(Level.INFO, "getObligation()","setting attributeAssignments");
        obligation2.setAttributeAssignments(list);
        log(Level.INFO, "getObligation()","obligation xml:" 
                + obligation2.toXMLString(true, true));

        log(Level.INFO, "getObligations()","create obligations");
        Obligations obligations = PolicyFactory.getInstance().createObligations();
        obligations.addObligation(obligation1);
        obligations.addObligation(obligation2);
        log(Level.INFO, "getObligations()","obligations xml:" 
                + obligation2.toXMLString(true, true));

        log(Level.INFO, "getObligation()","create obligations from xml");
        //obligations = PolicyFactory.getInstance().createObligations(
         //       obligations.toXMLString(true, true));
        log(Level.INFO, "getObligations()","obligations xml:" 
                + obligations.toXMLString(true, true));
        exiting("getObligations()");

    }

}
