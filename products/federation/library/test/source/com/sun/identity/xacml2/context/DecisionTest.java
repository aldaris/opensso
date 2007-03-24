package com.sun.identity.xacml2.context;

import com.sun.identity.shared.test.UnitTestBase;

import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.context.ContextFactory;
import com.sun.identity.xacml2.context.Decision;

import java.util.logging.Level;

import org.testng.annotations.Test;

public class DecisionTest extends UnitTestBase {

    public DecisionTest() {
        super("OpenFed-xacml2-DecisionTest");
    }

    //@Test(groups={"xacml2"}, expectedExceptions={XACML2Exception.class})
    @Test(groups={"xacml2"})
    public void getDecision() throws XACML2Exception {

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
