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
 * $Id: AuthTestFactory.java,v 1.2 2007-05-04 20:47:42 sridharev Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.authentication;

import com.sun.identity.qatest.common.TestCommon;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.testng.annotations.Factory;

/**
 * This class is factory class for AuthTest.java. It reads test data
 * from properties file and recursively calls test methods in 
 * AuthTest.java. This way we can easily call same method with 
 * different data sets.
 */
public class AuthTestFactory extends TestCommon {

    private Map<String, String> map;

/**
 * Constructor for AuthTestFactory class. Its an empty constructor.
 */
    public AuthTestFactory() {
        super("AuthTestFactory");
    }

/**
 * Factory method for getting test data from properties file and instantiate
 * AuthTest.java for execution.
 */
    @Factory
    public Object[] testLogin()
        throws Exception {
        entering("testLogin", null);
        Map map;
        Map localMapConfig;
        Map localMapExecute;
        Object[] result = null;
        try {
            ResourceBundle client = 
                    ResourceBundle.getBundle("authenticationTest");
            map = new HashMap<String, String>();
            for (Enumeration e = client.getKeys(); e.hasMoreElements(); ) {
                String key = (String)e.nextElement();
                String value = (String)client.getString(key);
                map.put(key, value);
            }
            log(logLevel, "testLogin", "GlobalMap:" + map);
            Integer iAll = new Integer((String)map.get("test.allobjects"));
            log(logLevel, "testLogin", "iAll:" + iAll);
            Integer iSub = new Integer((String)map.get("test.subobjects"));
            log(logLevel, "testLogin", "iSub:" + iSub);
            for (int i=0;i<iAll.intValue();i++) {
                result = new Object[iSub.intValue()];
                localMapConfig = new HashMap();
                localMapConfig.put("module_subconfigid", map.get("test" + i +
                        ".module_subconfigid"));
                localMapConfig.put("module_servicename", map.get("test" + i +
                        ".module_servicename"));
                localMapConfig.put("module_subconfigname", map.get("test" + i +
                        ".module_subconfigname"));
                localMapConfig.put("module_datafile", map.get("test" + i +
                        ".module_datafile"));
                localMapConfig.put("service_subconfigid", map.get("test" + i +
                        ".service_subconfigid"));
                localMapConfig.put("service_servicename", map.get("test" + i +
                        ".service_servicename"));
                localMapConfig.put("service_subconfigname", map.get("test" + i +
                        ".service_subconfigname"));
                localMapConfig.put("rolename", map.get("test" + i +
                        ".rolename"));
                log(logLevel, "testLogin", "localMapConfig:" + localMapConfig);
                for (int j=0;j<iSub.intValue();j++) {
                    localMapExecute = new HashMap();
                    localMapExecute.put("user", map.get("test" + i + ".user"));
                    localMapExecute.put("password", map.get("test" + i +
                            ".password"));
                    localMapExecute.put("failmsg", map.get("test" + i +
                            ".failmsg"));
                    localMapExecute.put("passmsg", map.get("test" + i +
                            ".passmsg"));
                    localMapExecute.put("mode", map.get("test" + i +
                            ".mode." + j));
                    localMapExecute.put("modevalue", map.get("test" + i +
                            ".modevalue." + j));
                    log(logLevel, "testLogin", "localMapExecute:" +
                            localMapExecute);
                    result[j] = 
                            new AuthTest(localMapConfig, localMapExecute, j);
                } 
            } 
        } catch (Exception e) {
            log(Level.SEVERE, "testLogin", e.getMessage(),
                    null);
            e.printStackTrace();
            throw e;
        }
        exiting("testLogin");
        return result;
    }
}
