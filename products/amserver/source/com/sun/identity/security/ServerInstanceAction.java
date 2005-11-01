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
 * $Id: ServerInstanceAction.java,v 1.1 2005-11-01 00:31:18 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.security;

import java.security.PrivilegedAction;

import com.iplanet.am.util.Debug;
import com.iplanet.services.ldap.ServerInstance;

/**
 * 
 * The class is used to perform privileged operations using
 * <code>AccessController.doPrivileged()
 * </code> when using
 * <code> com.iplanet.services.ldap.ServerInstance</code> to obtain Admin
 * passwords. Ths class implements the interface <code>
 * PrivilegedAction </code>
 * with a non-default constructor. This class should be used in order to perform
 * the privileged operation of
 * <code> com.iplanet.services.ldap.ServerInstance.getPasswd()</code>.
 * 
 * <PRE>
 * 
 * This line of code: String encStr =
 * com.iplanet.services.ldap.ServerInstance.getPasswd(); should be replaced
 * with: String encStr = (String) AccessController.doPrivileged( new
 * ServerInstanceAction(svrInstance)); If this is not done and Java security
 * permissions check is enabled, then the operation will fail and return a null
 * everytime.
 * 
 * Note: Java security permissions check for Access Manager can be enabled by
 * setting the property <code>com.sun.identity.security.checkcaller</code> to
 * true in AMConfig properties file.
 * 
 * </PRE>
 */
public class ServerInstanceAction implements PrivilegedAction {

    protected Debug debug = Debug.getInstance("amSDK");

    protected ServerInstance sInstance = null;

    /**
     * Non default constructor to be used when a doPrivileged() is performed for
     * the decryption operations.
     * 
     * @param si
     *            ServerInstance object
     * 
     */
    public ServerInstanceAction(ServerInstance si) {
        super();
        sInstance = si;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.security.PrivilegedAction#run()
     */
    public Object run() {
        String ret = sInstance.getPasswd();
        return ret;
    }
}
