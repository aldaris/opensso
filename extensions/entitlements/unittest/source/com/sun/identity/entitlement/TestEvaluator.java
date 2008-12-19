/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: TestEvaluator.java,v 1.1 2008-12-19 09:37:01 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.unittest.UnittestLog;
import java.util.HashSet;
import java.util.Set;
import javax.security.auth.Subject;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

public class TestEvaluator {
    @DataProvider(name = "provideURLs")
    public Object[][] provideURLs() {
        return new Object[][] {
            {"htp://www.sun.com:8080/test", ""},
            {"http://:8080/", ""},
            {"http://:8080/test", ""},
            {"http://www.sun.com/", ""},
            {"http://www.sun.com/test", ""},
            {"http://www.sun.com/test.html", ""},
            {"http://www.sun.com:8080/test/", ""},
            {"http://www.sun.com:8080/test/banner.html", ""},
            {"http://www.sun.com:8080/test/banner.htm", ""},
            {"http://www.sun.com:8080/?q1=1", ""},
            {"http://www.sun.com:8080/test?q1=1", ""},
            {"http://www.sun.com:8080/test.jsp?q1=1", ""},
            {"http://www.sun.com:8080/test?q1=1+2", ""},
            {"http://www.sun.com:8080/test?q1=1*2", ""},
            {"http://www.sun.com:8080/test?q1=1%202", ""},
            {"http://www.sun.com:8080/test?q1=1&q2=2", ""},
            {"http://www.sun.com:8080/test?q1=&q2=2&q3=sun", ""},
            {"http://helium.r*.iplanet.com:80/*.html", ""},
            {"http://helium.red.iplanet.com:8*/*.html", ""},
            {"http*://helium.red.iplanet.com:8*/*.html", ""},
            {"http://dummygoogle.red.iplanet.com:80/a/b/-*-/f/index.html", ""},
            {"http://dummygoogle.red.iplanet.com:80/a/b/bc/f/index.html", ""},
            {"http://dummygoogle.red.iplanet.com:80/a/b/c/d/f/index.html", ""},
            {"http://dummyyahoo.red.iplanet.com:80/a/b/-*-/f/-*-/g/index.html",
                 ""}
        };
    } 

    @Test(dataProvider = "provideURLs")
    public boolean evaluate(String url, String dummy) 
        throws EntitlementException {
        UnittestLog.logMessage("TestEvaluator.evaluate: " + url);
        Set actions = new HashSet();
        actions.add("GET");
        Evaluator evaluator = new Evaluator(new Subject());
        return evaluator.hasEntitlement(new Subject(), 
            new Entitlement(url, actions));
    }
    
}
