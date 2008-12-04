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
 * $Id: TestHarness.java,v 1.1 2008-12-04 21:12:20 veiming Exp $
 */

package com.sun.identity.unittest;

import com.iplanet.am.util.SystemProperties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

/**
 * Test Harness instantiates the test classes and executes them.
 */
public final class TestHarness {
   
    public TestHarness() {
    }
   
    public void execute(HttpServletResponse res, String tests) {
        List<String> classes = getTestClasses(tests);
        for (String strClass : classes) {
            if (strClass.endsWith(".jsp")) {
                executeJSP(strClass);
            } else {
                executeClass(strClass);
            }
        }
        UnittestLog.logMessage("TestHarness:DONE");
    }
    
    private void executeJSP(String strJSP) {
        String jsp = strJSP.substring(0, strJSP.lastIndexOf(".jsp"));
        jsp = SystemProperties.getServerInstanceName() + "/unittest/" +
            jsp.replace('.',  '/') + ".jsp";
        UnittestLog.logMessage("Executing JSP, " + jsp);
        
        try {
            URL url = new URL(jsp);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                conn.getOutputStream());
            wr.write("hello=1");
            wr.flush();
            BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                //
            }
            wr.close();
            rd.close();
        } catch (MalformedURLException e) {
            UnittestLog.logError(
                "TestHarness.executeJSP: execute JSP failed", e);
        } catch (IOException e) {
            UnittestLog.logError(
                "TestHarness.executeJSP: execute JSP failed", e);
        }
        UnittestLog.logMessage("Executed JSP, " + jsp);
    }
    
    private void executeClass(String strClass) {
        try {
            Class clazz = Class.forName(strClass);
            UnittestBase instance = (UnittestBase) clazz.newInstance();

            UnittestLog.logMessage(instance, "Started initializing...");
            instance.init();
            UnittestLog.logMessage(instance, "Done with initialize");

            UnittestLog.logMessage(instance, "Started setup");
            instance.setup();
            UnittestLog.logMessage(instance, "Done with setup");

            long startTime = System.currentTimeMillis();
            UnittestLog.logMessage(instance, "Started run");
            instance.run();
            long timeTaken = System.currentTimeMillis() - startTime;
            UnittestLog.logMessage(instance,
                "Done with run (time taken = " + timeTaken + " msec)");

            UnittestLog.logMessage(instance, "Started cleanup");
            instance.cleanup();
            UnittestLog.logMessage(instance, "Done with cleanup");
        } catch (ClassNotFoundException e) {
            UnittestLog.logError(
                "TestHarness.executeClass: execute method failed", e);
        } catch (InstantiationException e) {
            UnittestLog.logError(
                "TestHarness.executeClass: execute method failed", e);
        } catch (IllegalAccessException e) {
            UnittestLog.logError(
                "TestHarness.executeClass: execute method failed", e);
        } catch (Throwable e) {
            UnittestLog.logError(
                "TestHarness.executeClass: execute method failed", e);
        }
    }
    
    private static List<String> getTestClasses(String tests) {
        List classes = new ArrayList();
        StringTokenizer st = new StringTokenizer(tests, ",");
        while (st.hasMoreTokens()) {
            classes.add(st.nextToken());
        }
        return classes;
    }
    
    public static Map getTests(ServletContext servletContext) {
        Map map = new HashMap();
        getTestClasses(servletContext, map);
        getTestJSPs(servletContext, map, "/unittest", true);
        return map;
    }
    
    public static void getTestClasses(ServletContext servletContext, Map map) {
        JarInputStream in = null;
        try {
            in = new JarInputStream(servletContext.getResourceAsStream(
                "WEB-INF/lib/unittest.jar"));
            JarEntry jarEntry = in.getNextJarEntry();

            while (jarEntry != null) {
                String name = jarEntry.getName();
                if (name.endsWith(".class")) {
                    name = name.replaceAll("/", ".");
                    int idx = name.lastIndexOf('.');
                    name = name.substring(0, idx);

                    Class clazz = Class.forName(name);
                    UnittestBase instance = isSubClassOfUnittestBase(clazz);

                    if (instance != null) {
                        idx = name.lastIndexOf('.');
                        String pkgName = name.substring(0, idx);
                        Set set = (Set)map.get(pkgName);
                        if (set == null) {
                            set = new TreeSet();
                            map.put(pkgName, set);
                        }

                        String s = name.substring(idx+1) + " - " +
                            instance.getName();
                        Set issues = instance.getIssues();
                        if ((issues != null) && !issues.isEmpty()) {
                            s += " (Issue: " + issues.toString() + ")";
                        }
                        Set iTested = instance.getInterfacesTested();
                        if ((iTested != null) && !iTested.isEmpty()) {
                            s += " (Interfaces: " + iTested.toString() + ")";
                        }
                        set.add(s);
                    }
                }
                jarEntry = in.getNextJarEntry();
            }
        } catch (IOException e) {
            UnittestLog.logError("TestHarness.getTestClasses: failed", e);
        } catch (ClassNotFoundException e) {
            UnittestLog.logError("TestHarness.getTestClasses: failed", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }

    public static void getTestJSPs(
        ServletContext servletContext, 
        Map map,
        String root, 
        boolean top
    ) {
        Set paths = servletContext.getResourcePaths(root);
        for (Iterator i = paths.iterator(); i.hasNext(); ) {
            String path = (String)i.next();
            if (path.endsWith("/")) { //directory
                getTestJSPs(servletContext, map, path, false);
            } else if (!top) {
                if (path.endsWith(".jsp")) {
                    int idx = path.lastIndexOf('/');
                    String name = path.substring(idx + 1) + " - JSP";
                    String pkgName = path.substring(0, idx);
                    pkgName = pkgName.replace('/', '.');
                    pkgName = pkgName.substring(10); // strip .unittest.

                    Set set = (Set) map.get(pkgName);
                    if (set == null) {
                        set = new TreeSet();
                        map.put(pkgName, set);
                    }
                    set.add(name);
                }
                
                
            }
        }
    }
    
    private static UnittestBase isSubClassOfUnittestBase(Class clazz) {
        Class superClass = clazz.getSuperclass();
        while (superClass != null) {
            if (superClass.equals(UnittestBase.class)) {
                try {
                    return (UnittestBase)clazz.newInstance();
                } catch (IllegalAccessException e) {
                    return null;
                } catch (InstantiationException e) {
                    return null;
                }
            }
            superClass = superClass.getSuperclass();
        }
        return null;
    }
}

