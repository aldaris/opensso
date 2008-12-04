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
 * $Id: UnittestBase.java,v 1.1 2008-12-04 21:12:20 veiming Exp $
 *
 */

package com.sun.identity.unittest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Base class for developing unittests. The method <class>run()</class> is
 * the only abstract method that must be implemented. Other methods are
 * optional.
 */
public abstract class UnittestBase {
    private Properties data = new Properties();
    
    public void init() {
        String className = getClass().getName();
        try {
            ResourceBundle rb = ResourceBundle.getBundle(
                className);
            for (Enumeration e = rb.getKeys(); e.hasMoreElements(); ) {
                String s = (String)e.nextElement();
                data.setProperty(s, rb.getString(s));
            }
        } catch (MissingResourceException e) {
            //ignore
        }
    }
    
    /**
     * To setup the environment needed to the run the test cases.
     * Calls to this method are not included in the performance numbers
     * that is computed for the <class>run()</method>.
     */
    public void setup() {
        // no-op
    }
    
    /**
     * Executes the test cases. Execution time of the method would be
     * measured and can be used to compare with other runs.
     * 
     * @return <class>true</class> if the all tests are successful;
     * <class>false</class> otherwise.
     * @throws java.lang.Throwable
     */
    public abstract boolean run() throws Throwable;
    
    /**
     * To cleanup the environment after the execution of the test cases.
     * This method will be called in both the event of the failure and
     * success of the test cases.
     */
    public void cleanup() {
        // no-op
    }
    
    /**
     * Returns the name for the test case. The default implementation
     * returns the class name
     * 
     * @return name for the test case
     */
    public String getName() {
        return getClass().getName();
    }
    
    /**
     * Returns the set of issues addressed by this test case.
     * 
     * @return set of issues addressed
     */
    public Set getIssues() {
        return Collections.EMPTY_SET;
    }
    
    /**
     * Returns the set of interfaces that is tested by this unittest.
     * 
     * @return set of interfaces tested.
     */
    public Set getInterfacesTested() {
        return Collections.EMPTY_SET;
    }
    
    protected void logMessage(String msg) {
        UnittestLog.logMessage(this, msg);
    }

    protected void logError(String msg, Exception e) {
        UnittestLog.logError(this, msg, e);
    }

    protected Properties getData() {
        return data;
    }
}
