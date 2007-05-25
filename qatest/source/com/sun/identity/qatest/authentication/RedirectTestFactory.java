/* The contents of this file are subject to the terms
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
 * $Id: RedirectTestFactory.java,v 1.1 2007-05-25 21:58:30 sridharev Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.authentication;

import com.sun.identity.qatest.common.TestCommon;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.testng.annotations.Factory;

/**
 * <code>RedirectTestFactory</code> is a factory implementation for redirection
 * tests.This <code>Factory</code> implementation reads the each
 * module and org under test execution and calls <code>RedirectTest</code> for
 * setup and execution, basically this factory class drives the execution.
 */
public class RedirectTestFactory extends TestCommon {
    
    private ResourceBundle redirect;
    
    /**
     * Default Constructor
     **/
    public RedirectTestFactory() {
        super("RedirectTestFactory");
    }
    
    /**
     * Factory implementation for reading and the chain/services to be
     * executed
     */
    @Factory
    public Object[] processTests()
    throws Exception {
        List result = new ArrayList();
        redirect =  ResourceBundle.getBundle("RedirectTest");
        String testOrgs = redirect.getString("am-auth-test-realm");
        StringTokenizer realmTokens = new StringTokenizer(testOrgs, ",");
        List<String> orgList = getListFromTokens(realmTokens);
        for (String orgName: orgList) {
            String testModules = redirect.getString("am-auth-test-modules");
            StringTokenizer moduleTokens = new StringTokenizer(testModules, ",");
            List<String> moduleList = getListFromTokens(moduleTokens);
            for (String modName: moduleList) {
                result.add(new RedirectTest(redirect, modName, orgName));
            }
        }
        return result.toArray();
    }
}
