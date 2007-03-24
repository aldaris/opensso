package com.sun.identity.xacml2.common;

import com.sun.identity.shared.test.UnitTestBase;

import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.context.ContextFactory;
import com.sun.identity.xacml2.context.Decision;

import java.util.logging.Level;

import org.testng.annotations.Test;

public class XACML2SDKUtilsTest extends UnitTestBase {

    public XACML2SDKUtilsTest() {
        super("OpenFed-xacml2-XACML2SDKUtilsTest");
    }

    //@Test(groups={"xacml2"}, expectedExceptions={XACML2Exception.class})
    @Test(groups={"xacml2"})
    public void isValidDecision() throws XACML2Exception {
        entering("isValidDecision()", null);
        log(Level.INFO,"isValidDecision()","\n");
        log(Level.INFO,"isValidDecision()","xacml2sdkutils-test-1b");
        log(Level.INFO,"isValidDecision()","decision value Permit is valid:" 
                + XACML2SDKUtils.isValidDecision("Permit"));
        log(Level.INFO,"isValidDecision()","decision value Deny is valid:" 
                + XACML2SDKUtils.isValidDecision("Deny"));
        log(Level.INFO,"isValidDecision()","decision value Indeterminate is valid:" 
                + XACML2SDKUtils.isValidDecision("NotApplicable"));
        log(Level.INFO,"isValidDecision()","decision value Indeterminate is valid:" 
                + XACML2SDKUtils.isValidDecision("NotApplicable"));
        log(Level.INFO,"isValidDecision()","decision value allow is valid:" 
                + XACML2SDKUtils.isValidDecision("allow"));
        log(Level.INFO,"isValidDecision()","decision value deny is valid:" 
                + XACML2SDKUtils.isValidDecision("deny"));
        log(Level.INFO,"isValidDecision()","xacml2sdkutils-test-1e");
        log(Level.INFO,"isValidDecision()","\n");
        exiting("isValidDecision()");
    }

}
