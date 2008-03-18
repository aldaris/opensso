package com.sun.identity.xacml.common;

import com.sun.identity.shared.test.UnitTestBase;

import com.sun.identity.xacml.common.XACMLException;
import com.sun.identity.xacml.context.ContextFactory;
import com.sun.identity.xacml.context.Decision;

import java.util.logging.Level;

import org.testng.annotations.Test;

public class XACMLSDKUtilsTest extends UnitTestBase {

    public XACMLSDKUtilsTest() {
        super("FedLibrary-XACML-XACMLSDKUtilsTest");
    }

    //@Test(groups={"xacml"}, expectedExceptions={XACMLException.class})
    @Test(groups={"xacml"})
    public void isValidDecision() throws XACMLException {
        entering("isValidDecision()", null);
        log(Level.INFO,"isValidDecision()","\n");
        log(Level.INFO,"isValidDecision()","xacmlsdkutils-test-1b");
        log(Level.INFO,"isValidDecision()","decision value Permit is valid:" 
                + XACMLSDKUtils.isValidDecision("Permit"));
        log(Level.INFO,"isValidDecision()","decision value Deny is valid:" 
                + XACMLSDKUtils.isValidDecision("Deny"));
        log(Level.INFO,"isValidDecision()","decision value Indeterminate is valid:" 
                + XACMLSDKUtils.isValidDecision("NotApplicable"));
        log(Level.INFO,"isValidDecision()","decision value Indeterminate is valid:" 
                + XACMLSDKUtils.isValidDecision("NotApplicable"));
        log(Level.INFO,"isValidDecision()","decision value allow is valid:" 
                + XACMLSDKUtils.isValidDecision("allow"));
        log(Level.INFO,"isValidDecision()","decision value deny is valid:" 
                + XACMLSDKUtils.isValidDecision("deny"));
        log(Level.INFO,"isValidDecision()","xacmlsdkutils-test-1e");
        log(Level.INFO,"isValidDecision()","\n");
        exiting("isValidDecision()");
    }

}
