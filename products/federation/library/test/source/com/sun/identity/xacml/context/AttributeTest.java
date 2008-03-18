package com.sun.identity.xacml.context;

import com.sun.identity.shared.test.UnitTestBase;
import com.sun.identity.shared.xml.XMLUtils;

import com.sun.identity.xacml.common.XACMLConstants;
import com.sun.identity.xacml.common.XACMLException;
import com.sun.identity.xacml.common.XACMLSDKUtils;
import com.sun.identity.xacml.context.ContextFactory;
import com.sun.identity.xacml.context.Attribute;

import java.net.URI;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;

import org.testng.annotations.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AttributeTest extends UnitTestBase {

    public AttributeTest() {
        super("FedLibrary-XACML-AttributeTest");
    }

    //@Test(groups={"xacml"}, expectedExceptions={XACMLException.class})
    @Test(groups={"xacml"})
    public void getAttribute() throws XACMLException, Exception {

        entering("getAttribute()", null);
        log(Level.INFO, "getAttribute()","\n");
        log(Level.INFO, "getAttribute()","attribute-test-1-b");

        log(Level.INFO, "getAttribute()","create empty attribute");
        Attribute attribute = ContextFactory.getInstance().createAttribute();
        log(Level.INFO, "getAttribute()","set AttributeId");
        attribute.setAttributeId(new URI("attribute-10"));
        log(Level.INFO, "getAttribute()","set Issuer");
        attribute.setIssuer("issuer-10");
        log(Level.INFO, "getAttribute()","set DataType");
        attribute.setDataType(new URI("DataType-10"));
        List stringValues = new ArrayList();
        stringValues.add("val1");
        stringValues.add("val2");
        attribute.setAttributeStringValues(stringValues);
        log(Level.INFO, "getAttribute()","attribute xml:" 
                + attribute.toXMLString(true, true));
        log(Level.INFO, "getAttribute()","create attribute from xml string");
        attribute = ContextFactory.getInstance().createAttribute(
                attribute.toXMLString(true, true));
        log(Level.INFO, "getAttribute()","attribute xml:" 
                + attribute.toXMLString(true, true));

        List list = new ArrayList(); //value list
        Document doc = XMLUtils.newDocument();
        Element elem = doc.createElementNS(XACMLConstants.XACML_NS_URI, 
                "xacml:AttributeValue");
        elem.setAttribute("attr1", "a-120");
        elem.setAttribute("attr2", "f-120");
        Element child = doc.createElement("SampleElem");
        elem.appendChild(child);
        list.add(elem);
        log(Level.INFO, "getAttribute()","setting attributeValues");
        attribute.setAttributeValues(list);
        log(Level.INFO, "getAttribute()","attribute xml:" 
                + attribute.toXMLString(true, true));

        log(Level.INFO, "getAttribute()","create attribute from xml");
        attribute = ContextFactory.getInstance().createAttribute(
                attribute.toXMLString(true, true));
        log(Level.INFO, "getAttribute()","attribute xml:" 
                + attribute.toXMLString(true, true));
        exiting("getAttribute()");

    }

}
