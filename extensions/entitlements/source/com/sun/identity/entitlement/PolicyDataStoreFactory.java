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
 * $Id: PolicyDataStoreFactory.java,v 1.3 2009-04-14 00:24:18 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IPolicyDataStore;
import com.sun.identity.entitlement.util.DebugFactory;
import com.sun.identity.shared.debug.IDebug;

/**
 * This factory returns the implementation class that implements
 * <code>com.sun.identity.entitlement.IPolicyDataStore</code>.
 */
public final class PolicyDataStoreFactory {
    private static PolicyDataStoreFactory instance =
        new PolicyDataStoreFactory();
    private static IPolicyDataStore defaultImpl;
    public static IDebug debug = DebugFactory.getDebug("entitlementDatastore");
    
    static {
        try {
            Class clazz = Class.forName(
                "com.sun.identity.entitlement.opensso.PolicyDataStore");
            defaultImpl = (IPolicyDataStore)clazz.newInstance();
        } catch (ClassNotFoundException e) {
            debug.error("PolicyDataStoreFactory.static<init>", e);
        } catch (InstantiationException e) {
            debug.error("PolicyDataStoreFactory.static<init>", e);
        } catch (IllegalAccessException e) {
            debug.error("PolicyDataStoreFactory.static<init>", e);
        }
    }
    
    private PolicyDataStoreFactory() {
    }

    /**
     * Returns an instance of the factory.
     *
     * @return an instance of the factory.
     */
    public static PolicyDataStoreFactory getInstance() {
        return instance;
    }

    /**
     * Returns the data store object.
     * 
     * @return the data store object.
     */
    public IPolicyDataStore getDataStore() {
        return defaultImpl; //TODO allow overwritting default impl;
    }
}
