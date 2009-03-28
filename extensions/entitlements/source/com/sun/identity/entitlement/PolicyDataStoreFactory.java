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
 * $Id: PolicyDataStoreFactory.java,v 1.1 2009-03-28 06:45:28 veiming Exp $
 */

package com.sun.identity.entitlement;

/**
 * This factory returns the implementation class that implements
 * <code>com.sun.identity.entitlement.IPolicyDataStore</code>.
 */
public final class PolicyDataStoreFactory {
    private static PolicyDataStoreFactory instance =
        new PolicyDataStoreFactory();
    private static IPolicyDataStore defaultImpl;
    
    static {
        try {
            Class clazz = Class.forName(
                "com.sun.identity.entitlement.PolicyDataStore");
            defaultImpl = (IPolicyDataStore)clazz.newInstance();
        } catch (ClassNotFoundException e) {
            //TODO
        } catch (InstantiationException e) {
            //TODO
        } catch (IllegalAccessException e) {
            //TODO
        }
    }
    
    private PolicyDataStoreFactory() {
    }
    
    public static PolicyDataStoreFactory getInstance() {
        return instance;
    }
    
    public IPolicyDataStore getDataStore() {
        return defaultImpl; //TODO allow overwritting default impl;
    }
}
