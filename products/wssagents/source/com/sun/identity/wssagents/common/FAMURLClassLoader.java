/*
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
 * $Id: FAMURLClassLoader.java,v 1.1 2007-06-08 06:39:01 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wssagents.common;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * Federated Access Mananger class loader to overcome the XML signature
 * and verification problem. This class explicitly loads the
 * classes in the package "com.sun.org.apache.xml.internal".
 * This class can be used in other agents to override default
 * class loading.
 */
public class FAMURLClassLoader extends URLClassLoader {

    private static Map loadedClasses = new HashMap();
    
    /** Creates a new instance of FAMURLClassLoader */
    public FAMURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
    
    protected Class loadClass(String name, boolean resolve)
    throws ClassNotFoundException {
        if (name.startsWith("com.sun.org.apache.xml.internal.")) {
            try {
                Class answer = (Class) loadedClasses.get(name);
                if (answer == null) {
                    answer = findClass(name);
                    loadedClasses.put(name, answer);
                }
                return (answer);
            } catch (ClassNotFoundException ce) {
                // Class not found. Try the parent
            }
        }
        return (super.loadClass(name, resolve));
    }
    
    protected Class findClass(String name) throws ClassNotFoundException {
        return (super.findClass(name));
    }
}
