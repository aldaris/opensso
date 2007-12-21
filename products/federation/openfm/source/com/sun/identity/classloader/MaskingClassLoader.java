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
 * $Id: MaskingClassLoader.java,v 1.1 2007-12-21 20:47:40 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.classloader;

import java.util.Collection;

/**
 * {@link ClassLoader} that masks a specified set of classes
 * from its parent class loader.
 *
 * <p>
 * This code is used to create an isolated environment.
 *
 */
public class MaskingClassLoader extends ClassLoader {

    private final String[] masks;

    public MaskingClassLoader(String[] masks) {
        this.masks = masks;
    }

    public MaskingClassLoader(Collection<String> masks) {
        this(masks.toArray(new String[masks.size()]));
    }

    public MaskingClassLoader(ClassLoader parent, String[] masks) {
        super(parent);
        this.masks = masks;
    }

    public MaskingClassLoader(ClassLoader parent, 
                              Collection<String> masks) {
        this(parent, masks.toArray(new String[masks.size()]));
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) 
    throws ClassNotFoundException {
        for (String mask : masks) {
            if(name.startsWith(mask))
                throw new ClassNotFoundException();
        }

        return super.loadClass(name, resolve);
    }
}
