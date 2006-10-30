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
 * $Id: AssertionManagerIF.java,v 1.1 2006-10-30 23:15:32 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import com.sun.identity.saml.common.SAMLException;

/**
 * JAX-RPC interface for AssertionManager to make it remoteable
 */
public interface AssertionManagerIF extends Remote {
    public void checkForLocal() throws RemoteException;

    public String createAssertion(String ssoToken)
        throws SAMLException, RemoteException;

    public String createAssertion(String ssoToken, List attributes)
        throws SAMLException, RemoteException;

    public String createAssertionArtifact(String assertion, String target)
        throws SAMLException, RemoteException;

    public String getAssertionByIdRef(String idRef, Set destID)
        throws SAMLException, RemoteException;
   
    public String getAssertionByIdRef(String idRef, String destID)
        throws SAMLException, RemoteException;

    public String getAssertionByIdRefToken(String idRef, String ssoToken)
        throws SAMLException, RemoteException;

    public String getAssertion(String artifact, Set destID)
        throws SAMLException, RemoteException;
    
    public String getAssertion(String artifact, String destID)
        throws SAMLException, RemoteException;

    public Set getAssertionArtifacts(String ssoToken)
        throws SAMLException, RemoteException;

    public Set getAssertions(String ssoToken)
        throws SAMLException, RemoteException;
}
