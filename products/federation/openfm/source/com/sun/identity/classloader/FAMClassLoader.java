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
 * $Id: FAMClassLoader.java,v 1.2 2008-01-31 20:01:40 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileFilter;
import javax.servlet.ServletContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Federated Access Mananger class loader to overcome the class loading
 * issues of jars that are not compatible for Federation Access Manager.
 */
public class FAMClassLoader {
    
   public static ClassLoader cl;
    
    /** Creates a new instance of FAMClassLoader */
    public FAMClassLoader() {
    }
    
    public static ClassLoader getFAMClassLoader(ServletContext context) {
        if (cl == null) {
            try {
                URL[] urls = jarFinder(context);

                ClassLoader localcc = FAMClassLoader.class.getClassLoader();

                List<String> mask = 
                    new ArrayList<String>(Arrays.asList(maskedPackages));

                // first create a protected area so that we load WS 2.1 API
                // and everything that depends on them, inside FAM classloader.
                localcc = new MaskingClassLoader(localcc,mask);

                // then this classloader loads the API and tools.jar
                cl = new URLClassLoader(urls, localcc);

                Thread.currentThread().setContextClassLoader(cl);
            } catch (Exception ex) {                
                ex.printStackTrace();
            }
        }
        return (cl);        
    }
    
    private static URL[] jarFinder(ServletContext context) {
        URL[] urls = new URL[jars.length];
        
        try {
            for (int i=0; i < jars.length; i++) {
                urls[i] = context.getResource("/WEB-INF/lib/" + jars[i]);
                System.out.println("FAM urls[" + i + "] : " + 
                                   (urls[i]).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    /**
     * The list of jar files to be loaded by FAMClassLoader.
     */
    public static String[] jars = new String[]{
        "webservices-api.jar",
        "webservices-rt.jar",
        "webservices-tools.jar",
        "webservices-extra-api.jar",
        "webservices-extra.jar",
        "fam.jar"
    };

    /**
     * The list of package prefixes we want the
     * {@link MaskingClassLoader} to prevent the parent
     * classLoader from loading.
     */
    public static String[] maskedPackages = new String[]{
        "com.sun.istack.tools.",
        "com.sun.tools.jxc.",
        "com.sun.tools.xjc.",
        "com.sun.tools.ws.",
        "com.sun.codemodel.",
        "com.sun.relaxng.",
        "com.sun.xml.xsom.",
        "com.sun.xml.bind.",
        "com.sun.xml.messaging",
        "com.sun.xml.ws.",
        "com.sun.xml.wss.",
        "com.sun.xml.xwss.",
        "javax.xml.bind.",
        "javax.xml.ws.",
        "javax.jws.",
        "javax.jws.soap.",
        "javax.xml.soap.",
        "com.sun.istack.",
        "com.sun.identity.wss."
    };
    
    
}
