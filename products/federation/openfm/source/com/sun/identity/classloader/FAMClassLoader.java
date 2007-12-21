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
 * $Id: FAMClassLoader.java,v 1.1 2007-12-21 20:47:40 mrudul_uchil Exp $
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
   public static ClassLoader oldcc;
    
    /** Creates a new instance of FAMClassLoader */
    public FAMClassLoader() {
    }
    
    public static ClassLoader getFAMClassLoader(ServletContext context) {
        if (cl == null) {
            try {
                String jarsPath = context.getRealPath("/");
                System.out.println("getRealPath: " + jarsPath);

                //String jarsResPath = context.getResource("/").getPath();                
                //jarsPath = jarsPath.substring(1, jarsPath.length());
                //System.out.println("Resource: " + jarsResPath);
                
                //String jarsResPath2 = 
                //    context.getResource("/WEB-INF/lib").getPath();
                //System.out.println("Resource WEB-INF : " + jarsResPath2);

                jarsPath = jarsPath + "WEB-INF/lib";
                System.out.println("Jars path:" + jarsPath);
                //String jarsPath = "WEB-INF/lib/";                
                URL[] urls = jarFinder(jarsPath);
                System.out.println("Jars:" + urls.length);

                oldcc = Thread.currentThread().getContextClassLoader();

                List<String> mask = 
                    new ArrayList<String>(Arrays.asList(maskedPackages));

                // first create a protected area so that we load WS 2.1 API
                // and everything that depends on them inside
                cl = new MaskingClassLoader(oldcc,mask);

                // then this classloader loads the API and tools.jar
                cl = new URLClassLoader(urls, cl);

                Thread.currentThread().setContextClassLoader(cl);
            } catch (Exception ex) {                
                ex.printStackTrace();
            } finally {
                Thread.currentThread().setContextClassLoader(oldcc);
            }
        }
        return (cl);        
    }
    
    private static URL[] jarFinder(String path) {
        List jars = new ArrayList();
        File file = new File(path);
        File[] files = file.listFiles(new FileFilter() {
               public boolean accept(File pathname) {
                    return pathname.getName().startsWith("webservices-");                           
               }
         });
         try {
             for (int i=0; i < files.length; i++) {
                  jars.add(files[i].toURL());
             }
             System.out.println("Jars: " + jars);
         } catch (MalformedURLException mre) {
             mre.printStackTrace();
         }
         return (URL[])jars.toArray(new URL[0]);
    }

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
        "com.sun.xml.ws.",
        "com.sun.xml.wss.",
        "com.sun.xml.xwss.",
        "javax.xml.bind.",
        "javax.xml.ws.",
        "javax.jws.",
        "javax.jws.soap.",
        "javax.xml.soap.",
        "com.sun.istack."
    };
    
    
}
