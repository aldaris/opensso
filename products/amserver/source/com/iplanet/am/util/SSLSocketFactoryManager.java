/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2005 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: SSLSocketFactoryManager.java,v 1.2 2008-06-25 05:41:28 qcheng Exp $
 *
 */

package com.iplanet.am.util;

import netscape.ldap.LDAPSocketFactory;

public class SSLSocketFactoryManager {

    private static final String LDAP_SOCKET_FACTORY_IMPL_KEY = 
        "com.iplanet.security.SSLSocketFactoryImpl";

    private static final String LDAP_SOCKET_FACTORY_DEFAULT_IMPL = 
        "com.iplanet.services.ldap.JSSSocketFactory";

    private static LDAPSocketFactory socketFactory = null;

    public static LDAPSocketFactory getSSLSocketFactory() throws Exception {
        if (socketFactory == null) {
            String socketFactoryImplClass = SystemProperties.get(
                    LDAP_SOCKET_FACTORY_IMPL_KEY,
                    LDAP_SOCKET_FACTORY_DEFAULT_IMPL);

            if (socketFactoryImplClass
                    .equals("netscape.ldap.factory.JSSESocketFactory")) {
                socketFactory = new netscape.ldap.factory.JSSESocketFactory(
                        null);
            } else {
                socketFactory = (LDAPSocketFactory) Class.forName(
                        socketFactoryImplClass).newInstance();
            }
        }

        return socketFactory;
    }

}
