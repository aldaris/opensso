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
 * $Id: ChainTestFactory.java,v 1.1 2007-05-04 20:47:43 sridharev Exp $
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
 * <code>ChainTestFactory</code> is a factory implementation for chaining tests
 * that drives the chaining tests, Each Chain can have the number of
 * module instances.This <code>Factory Implementation reads the each 
 * chain/service under execution and calls <code>AuthChainTest</code> for
 * setup and execution, basically this factory class drives the execution.
 * The following testcases are automated with this scenario.
 * AuthModule(LDAP)_1 , AuthModule(LDAP)_4, AuthModule(LDAP)_5,
 * AuthModule(LDAP)_11, AuthModule(LDAP)_12, AuthModule(LDAP)_13
 * AuthModule(Membership)_1,AuthModule(Membership)_2, AuthModule(Membership)_3,
 * AuthModule(Membership)_17, AuthModules(NT)_1,AuthModules(NT)_2,
 * AuthModules(NT)_5,AuthModules(NT)_6,AuthModule(Active Directory)_1
 * AuthModule(Active Directory )_12,AuthModule(JDBC )__1,AuthModule(JDBC )__6
 */
public class ChainTestFactory extends TestCommon{
    
    private ResourceBundle chain;
    
    /**
     * Default Constructor
     **/
    public ChainTestFactory() {
        super("ChainTestFactory");
    }
    
    /**
     * Factory implementation for reading and the chain/services to be
     * executed
     */
    @Factory
    public Object[] processTests() throws Exception {
        List result = new ArrayList();
        chain =  ResourceBundle.getBundle("chainTest");
        String testChains = chain.getString("am-auth-test-chains");
        StringTokenizer chainTokens = new StringTokenizer(testChains,",");
        List<String> chainList = new ArrayList<String>();
        while (chainTokens.hasMoreTokens()) {
            chainList.add(chainTokens.nextToken());
         }
        for(String chainName: chainList){
            result.add(new ChainTest(chain,chainName));
        }
        return result.toArray();
    }
}
