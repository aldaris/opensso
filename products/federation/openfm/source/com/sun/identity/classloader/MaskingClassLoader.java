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
 * $Id: MaskingClassLoader.java,v 1.3 2008-03-05 00:00:15 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.classloader;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import java.io.IOException;
import java.net.URL;
import sun.misc.CompoundEnumeration;

/**
 * {@link ClassLoader} that masks a specified set of classes
 * from its parent class loader.
 *
 * <p>
 * This code is used to create an isolated environment.
 *
 */
public class MaskingClassLoader extends ClassLoader {

    private final ClassLoader parent;
    private final String[] masks;
    private final String[] maskResources;
    private final URL[] urls;
    private final String resource = 
        "META-INF/services/com.sun.xml.ws.api.pipe.TransportPipeFactory";

    /*public MaskingClassLoader(String[] masks) {
        this.masks = masks;
    }

    public MaskingClassLoader(Collection<String> masks) {
        this(masks.toArray(new String[masks.size()]));
    }*/

    public MaskingClassLoader(ClassLoader parent, String[] masks, 
        String[] maskResources,URL[] urls) {
        super(parent);
        this.parent = parent;
        this.masks = masks;
        this.maskResources = maskResources;
        this.urls = urls;
    }

    public MaskingClassLoader(ClassLoader parent, 
                              Collection<String> masks,
                              Collection<String> maskResources,
                              URL[] urls) {
        this(parent, masks.toArray(new String[masks.size()]) , 
            maskResources.toArray(new String[maskResources.size()]), urls);
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) 
    throws ClassNotFoundException {
        for (String mask : masks) {
            if(name.startsWith(mask)) {
                throw new ClassNotFoundException();
            }
        }
        return super.loadClass(name, resolve);
    }
    
    @Override
    public synchronized URL getResource(String name) {
        for (String mask : maskResources) {
            if(name.startsWith(mask)) {
                return null;
            }
        }
        return super.getResource(name);
    }
    
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration[] tmp = new Enumeration[1];
	if(name.startsWith(resource)) {
            Vector vec = new Vector(1);
            URL jarURL = 
                new URL("jar:" + (urls[5]).toString() + "!/" + resource);
            vec.add(jarURL);
            tmp[0] = vec.elements();
            return new CompoundEnumeration(tmp);
        }
        return super.getResources(name);
    }
    
    @Override
    public synchronized String toString() {
        return "com.sun.identity.classloader.MaskingClassLoader : Super is : " 
            + super.toString();
    }
}
