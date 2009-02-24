/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: DebugFactory.java,v 1.1 2009-02-24 20:20:42 veiming Exp $
 */

package com.sun.identity.entitlement.util;

import com.sun.identity.entitlement.interfaces.IDebugProvider;

/**
 * Providers the debug provider handler.
 */
public final class DebugFactory {
    private static DebugFactory instance = new DebugFactory();
    private static final String DEF_IMPL =
        "com.sun.identity.shared.debug.impl.DebugProviderImpl";

    private IDebugProvider impl;

    private DebugFactory() {
        //TOFIX: load different debug provider.

        try {
            Class clazz = Class.forName(DEF_IMPL);
            impl = (IDebugProvider)clazz.newInstance();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the debug factory.
     *
     * @return the debug factory.
     */
    public static DebugFactory getInstance() {
        return instance;
    }

    public IDebugProvider getProvider() {
        return impl;
    }
}
