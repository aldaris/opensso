package com.sun.identity.xacml.context;

import com.sun.identity.shared.test.UnitTestBase;
import com.sun.identity.shared.xml.XMLUtils;

import com.sun.identity.xacml.common.XACMLException;
import com.sun.identity.xacml.common.XACMLSDKUtils;
import com.sun.identity.xacml.context.ContextFactory;
import com.sun.identity.xacml.context.Decision;
import com.sun.identity.xacml.context.Result;
import com.sun.identity.xacml.context.Status;
import com.sun.identity.xacml.context.StatusCode;
import com.sun.identity.xacml.context.StatusMessage;
import com.sun.identity.xacml.context.StatusDetail;

import java.util.logging.Level;

import org.testng.annotations.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResultTest extends UnitTestBase {

    public ResultTest() {
        super("OpenFed-xacml-ResultTest");
    }

    //@Test(groups={"xacml"}, expectedExceptions={XACMLException.class})
    @Test(groups={"xacml"})
    public void getResult() throws XACMLException {

        entering("getResult()", null);
        log(Level.INFO, "getResult()","\n");
        log(Level.INFO, "getResult()","result-test-1-b");
        log(Level.INFO, "getResult()","\n");

        log(Level.INFO, "getResult()","create empty status code");
        StatusCode code = ContextFactory.getInstance().createStatusCode();
        code.setValue("10");
        code.setMinorCodeValue("5");
        String statusCodeXml = code.toXMLString(true, true);
        log(Level.INFO, "getResult()","status code xml:" + statusCodeXml);
        log(Level.INFO, "getResult()","\n");

        log(Level.INFO, "getResult()","create empty status message");
        StatusMessage message = ContextFactory.getInstance().createStatusMessage();
        message.setValue("success");
        String statusMessageXml = message.toXMLString(true, true);
        log(Level.INFO, "getResult()","status message xml:" + statusMessageXml);
        log(Level.INFO, "getResult()","\n");

        log(Level.INFO, "getResult()","create empty statusDetail");
        StatusDetail detail = ContextFactory.getInstance().createStatusDetail();
        log(Level.INFO, "getResult()","detail-xml:" + detail.toXMLString());
        log(Level.INFO, "getResult()","add a child");
        detail.getElement().insertBefore(detail.getElement().cloneNode(true), null);
        log(Level.INFO, "getResult()","detail-xml:" + detail.toXMLString());
        log(Level.INFO, "getResult()","create statusDetail from xml string");
        StatusDetail detail1 = ContextFactory.getInstance().createStatusDetail(
                detail.toXMLString(true, true));
        log(Level.INFO, "getResult()","detail-xml:" + detail1.toXMLString());
        log(Level.INFO, "getResult()","\n");

        log(Level.INFO, "getResult()","create empty status");
        Status status = ContextFactory.getInstance().createStatus();
        log(Level.INFO, "getResult()","status-xml:" + status.toXMLString());
        log(Level.INFO, "getResult()","set status code");
        status.setStatusCode(code);
        log(Level.INFO, "getResult()","status-xml:" + status.toXMLString());
        log(Level.INFO, "getResult()","set status message");
        status.setStatusMessage(message);
        log(Level.INFO, "getResult()","status-xml:" + status.toXMLString());
        log(Level.INFO, "getResult()","set status detail");
        status.setStatusDetail(detail1);
        log(Level.INFO, "getResult()","status-xml:" + status.toXMLString());
        log(Level.INFO, "getResult()","\n");
        log(Level.INFO, "getResult()","status-xml, with ns declared:" + status.toXMLString(true,
                true));
        log(Level.INFO, "getResult()","create status from xml");
        Status status1 = ContextFactory.getInstance().createStatus(
                status.toXMLString(true, true));
        log(Level.INFO, "getResult()","status-xml, with ns declared:" + status1.toXMLString(true,
                true));
        log(Level.INFO, "getResult()","\n");

        log(Level.INFO, "getResult()","create empty decision");
        Decision decision = ContextFactory.getInstance().createDecision();
        log(Level.INFO, "getResult()","decision-xml:" + decision.toXMLString());
        log(Level.INFO, "getResult()","set value to Permit");
        decision.setValue("Permit");
        log(Level.INFO, "getResult()","detail-xml:" + decision.toXMLString());
        log(Level.INFO, "getResult()","create decision from xml string");
        Decision decision1 = ContextFactory.getInstance().createDecision(
                decision.toXMLString(true, true));
        log(Level.INFO, "getResult()","decision-xml:" + decision1.toXMLString());
        log(Level.INFO, "getResult()","\n");

        log(Level.INFO, "getResult()","create empty result");
        Result result = ContextFactory.getInstance().createResult();
        log(Level.INFO, "getResult()","result-xml:" + result.toXMLString());
        log(Level.INFO, "getResult()","resource id:" + result.getResourceId());
        log(Level.INFO, "getResult()","set resource id");
        log(Level.INFO, "getResult()","http://insat.red.iplanet.com/banner.html");
        result.setResourceId("http://insat.red.iplanet.com/banner.html");
        log(Level.INFO, "getResult()","get resource id:" + result.getResourceId());
        log(Level.INFO, "getResult()","result-xml:" + result.toXMLString());
        log(Level.INFO, "getResult()","set decision");
        result.setDecision(decision1);
        log(Level.INFO, "getResult()","result-xml:" + result.toXMLString());
        log(Level.INFO, "getResult()","set status");
        result.setStatus(status1);
        log(Level.INFO, "getResult()","result-xml:" + result.toXMLString());
        log(Level.INFO, "getResult()","result-xml, with nsDeclaration:" 
                + result.toXMLString(true, true));
        log(Level.INFO, "getResult()","create result from xml string");
        Result result1 = ContextFactory.getInstance().createResult(
                result.toXMLString(true, true));
        log(Level.INFO, "getResult()","result-xml:" + result1.toXMLString());
        log(Level.INFO, "getResult()","\n");

        log(Level.INFO, "getResult()","result-test-1-e");
        log(Level.INFO, "getResult()","\n");
        exiting("getResult()");

    }

}
