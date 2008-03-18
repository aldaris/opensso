package com.sun.identity.xacml.context;

import com.sun.identity.shared.test.UnitTestBase;

import com.sun.identity.xacml.common.XACMLException;
import com.sun.identity.xacml.context.ContextFactory;
import com.sun.identity.xacml.context.Decision;

import java.util.logging.Level;

import org.testng.annotations.Test;

public class DecisionTest extends UnitTestBase {

    public DecisionTest() {
        super("FedLibrary-XACML-DecisionTest");
    }

    //@Test(groups={"xacml"}, expectedExceptions={XACMLException.class})
    @Test(groups={"xacml"})
    public void getDecision() throws XACMLException {

        entering("getDecision()", null);
        log(Level.INFO,"getDecision()","\n");
        log(Level.INFO,"getDecision()","decision-test-1-b");
        Decision decision = ContextFactory.getInstance().createDecision();

        log(Level.INFO,"getDecision()","set value to Permit");
        decision.setValue("Permit");
        String xml = decision.toXMLString();
        log(Level.INFO,"getDecision()","decision xml:" + xml);
        log(Level.INFO,"getDecision()","decision value:" + decision.getValue());
        log(Level.INFO,"getDecision()","\n");

        log(Level.INFO,"getDecision()","set value to Deny");
        decision.setValue("Deny");
        xml = decision.toXMLString();
        log(Level.INFO,"getDecision()","decision xml:" + xml);
        log(Level.INFO,"getDecision()","decision value:" + decision.getValue());
        log(Level.INFO,"getDecision()","\n");

        log(Level.INFO,"getDecision()","set value to Indeterminate");
        decision.setValue("Indeterminate");
        xml = decision.toXMLString();
        log(Level.INFO,"getDecision()","decision xml:" + xml);
        log(Level.INFO,"getDecision()","decision value:" + decision.getValue());
        log(Level.INFO,"getDecision()","\n");

        log(Level.INFO,"getDecision()","set value to NotApplicable");
        decision.setValue("NotApplicable");
        xml = decision.toXMLString();
        log(Level.INFO,"getDecision()","decision xml:" + xml);
        log(Level.INFO,"getDecision()","decision value:" + decision.getValue());
        log(Level.INFO,"getDecision()","mutable value:" + decision.isMutable());
        log(Level.INFO,"getDecision()","\n");

        log(Level.INFO,"getDecision()","make immutable");
        decision.makeImmutable();
        xml = decision.toXMLString();
        log(Level.INFO,"getDecision()","decision xml:" + xml);
        log(Level.INFO,"getDecision()","decision value:" + decision.getValue());
        log(Level.INFO,"getDecision()","mutable value:" + decision.isMutable());
        //Decision decision1 = ContextFactory.getInstance().createDecision(xml);
        //log(Level.INFO,"getDecision()","decision value:" + decision1.getValue());
        //xml = decision1.toXMLString();
        //log(Level.INFO,"getDecision()","decision xml:" + xml);
        log(Level.INFO,"getDecision()","\n");


        log(Level.INFO,"getDecision()","make immutable");
        decision.makeImmutable();
        xml = decision.toXMLString(true, true);
        log(Level.INFO,"getDecision()","decision xml, include prefix, ns:" + xml);
        log(Level.INFO,"getDecision()","decision value:" + decision.getValue());
        log(Level.INFO,"getDecision()","mutable value:" + decision.isMutable());
        log(Level.INFO,"getDecision()","\n");

        log(Level.INFO,"getDecision()","creating decision from xml");
        Decision decision1 = ContextFactory.getInstance().createDecision(xml);
        log(Level.INFO,"getDecision()","decision value:" + decision1.getValue());
        xml = decision1.toXMLString();
        log(Level.INFO,"getDecision()","decision xml:" + xml);
        log(Level.INFO,"getDecision()","\n");

        log(Level.INFO,"getDecision()","decision-test-1-e");
        log(Level.INFO,"getDecision()","\n");
        exiting("getDecision()");

    }

}
