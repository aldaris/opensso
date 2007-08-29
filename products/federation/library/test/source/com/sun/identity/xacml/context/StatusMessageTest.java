package com.sun.identity.xacml.context;

import com.sun.identity.shared.test.UnitTestBase;

import com.sun.identity.xacml.common.XACMLException;
import com.sun.identity.xacml.context.ContextFactory;
import com.sun.identity.xacml.context.StatusMessage;

import java.util.logging.Level;

import org.testng.annotations.Test;

public class StatusMessageTest extends UnitTestBase {

    public StatusMessageTest() {
        super("OpenFed-xacml-StatusMessageTest");
    }

    //@Test(groups={"xacml"}, expectedExceptions={XACMLException.class})
    @Test(groups={"xacml"})
    public void getStatusMessage() throws XACMLException {

        log(Level.INFO,"getStatusMessage()","\n");
        entering("getStatusMessage()", null);
        log(Level.INFO,"getStatusMessage()","message-test-1-b");
        StatusMessage message = ContextFactory.getInstance().createStatusMessage();

        log(Level.INFO,"getStatusMessage()","set value to Permit");
        message.setValue("Permit");
        String xml = message.toXMLString();
        log(Level.INFO,"getStatusMessage()","message xml:" + xml);
        log(Level.INFO,"getStatusMessage()","message value:" + message.getValue());
        log(Level.INFO,"getStatusMessage()","\n");

        log(Level.INFO,"getStatusMessage()","set value to Deny");
        message.setValue("Deny");
        xml = message.toXMLString();
        log(Level.INFO,"getStatusMessage()","message xml:" + xml);
        log(Level.INFO,"getStatusMessage()","message value:" + message.getValue());
        log(Level.INFO,"getStatusMessage()","mutable value:" + message.isMutable());
        log(Level.INFO,"getStatusMessage()","\n");

        log(Level.INFO,"getStatusMessage()","make immutable");
        message.makeImmutable();
        xml = message.toXMLString();
        log(Level.INFO,"getStatusMessage()","message xml:" + xml);
        log(Level.INFO,"getStatusMessage()","message value:" + message.getValue());
        log(Level.INFO,"getStatusMessage()","mutable value:" + message.isMutable());
        log(Level.INFO,"getStatusMessage()","\n");


        log(Level.INFO,"getStatusMessage()","make immutable");
        message.makeImmutable();
        xml = message.toXMLString(true, true);
        log(Level.INFO,"getStatusMessage()","message xml, include prefix, ns:" + xml);
        log(Level.INFO,"getStatusMessage()","message value:" + message.getValue());
        log(Level.INFO,"getStatusMessage()","mutable value:" + message.isMutable());
        log(Level.INFO,"getStatusMessage()","\n");

        log(Level.INFO,"getStatusMessage()","creating message from xml");
        StatusMessage message1 = ContextFactory.getInstance().createStatusMessage(xml);
        log(Level.INFO,"getStatusMessage()","message value:" + message1.getValue());
        xml = message1.toXMLString();
        log(Level.INFO,"getStatusMessage()","message xml:" + xml);
        log(Level.INFO,"getStatusMessage()","\n");

        log(Level.INFO,"getStatusMessage()","message-test-1-e");
        log(Level.INFO,"getStatusMessage()","\n");
        exiting("getStatusMessage()");

    }

}
