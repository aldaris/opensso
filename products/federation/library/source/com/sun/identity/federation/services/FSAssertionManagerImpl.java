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
 * $Id: FSAssertionManagerImpl.java,v 1.1 2006-10-30 23:14:20 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.federation.services;

import com.sun.identity.federation.common.FSRemoteException;
import com.sun.identity.federation.common.FSUtils;
import com.sun.identity.federation.message.FSAssertion;
import com.sun.identity.federation.message.FSAssertionArtifact;
import com.sun.identity.saml.common.SAMLUtils;
import com.sun.identity.shared.encode.Base64;
import java.util.List;

/**
 * This class implements interface <code>FSAssertionManagerIF</code>.
 */
public class FSAssertionManagerImpl implements FSAssertionManagerIF {

    // Flag used to check if service is available locally
    protected static boolean isLocal;

    /**
     * Turns on the local flag.
     */
    public void checkForLocal() {
        isLocal = true;
    }

    /**
     * Returns assertion associated with the artifact.
     * @param hostedEntityId hosted provider's entity ID
     * @param artifact assertion artifact.
     * @param destID The destination site requesting the assertion using
     *  the artifact.
     * @return The Assertion referenced to by artifact.
     * @exception FSRemoteException, RemoteException If an error occurred during     *  the process
     */
    public String getAssertion(
        String hostedEntityId, String artifact, String destID)
        throws FSRemoteException 
    {
        try {
            FSAssertionManager assertionManager = 
                FSAssertionManager.getInstance(hostedEntityId);
            FSAssertion a = (FSAssertion)assertionManager.getAssertion(
                new FSAssertionArtifact(artifact),
                SAMLUtils.byteArrayToString(Base64.decode(destID)));
            if (a == null) {
                if (FSUtils.debug.messageEnabled()) {
                    FSUtils.debug.message("FSAssertionManagerImpl: Unable to " +
                        "get assertion from Artifact: " + artifact);
                }
                return (null);
            }
            if (FSUtils.debug.messageEnabled()) {
                FSUtils.debug.message(
                    "FSAssertionManagerImpl: Assertion got from " +
                    "assertionManager.getAssertion: " + 
                    a.toXMLString(true, true));
            }
            return (a.toXMLString(true, true));
        } catch(Exception e) {
            throw new FSRemoteException(e.getMessage());
        }        
    }
    
    /**
     * Returns the destination id the artifact is created for.
     * @param hostedEntityId hosted provider's entity ID
     * @param artifact assertion artifact string
     * @return destination id
     * @exception FSRemoteException if error occurred.
     */
    public String getDestIdForArtifact(String hostedEntityId, String artifact)
        throws FSRemoteException 
    {
       try {
            FSAssertionManager assertionManager = 
                FSAssertionManager.getInstance(hostedEntityId);
            String destID = assertionManager.getDestIdForArtifact(
                new FSAssertionArtifact(artifact));
            if (destID == null) {
                if (FSUtils.debug.messageEnabled()) {
                    FSUtils.debug.message("FSAssertionManagerImpl: Unable to " +
                        "get destination ID from remote : " );
                }
            }
            return destID;
        } catch(Exception e) {
            throw new FSRemoteException(e.getMessage());
        }        
    }

    /**
     * Checks if the user exists.
     * @param userDN user ID
     * @param hostedEntityId hosted provider's entity ID
     * @return <code>true</code> if the user exists; <code>false</code>
     *  otherwise.
     * @exception FSRemoteException,RemoteException if error occurred.
     */
    public boolean isUserExists(String userDN, String hostedEntityId)
        throws FSRemoteException
    {
        try {
            FSSessionManager sessionMgr = FSSessionManager.getInstance(
                hostedEntityId);
            synchronized(sessionMgr) {
                FSUtils.debug.message("About to call getSessionList");
                List sessionList = sessionMgr.getSessionList(userDN);
                if (sessionList == null) {
                    FSUtils.debug.message("AMC:isUserExists:List is empty");
                    return false;
                } else {
                    if (FSUtils.debug.messageEnabled()) {
                        FSUtils.debug.message(
                            "AMC:isUserExists: List is not empty User found: "
                            + userDN);
                    }
                    return true;
                }
            }
        } catch(Exception e) {
            if (FSUtils.debug.messageEnabled()) {
                FSUtils.debug.message("AMC.isUserExists:", e);
            }
            throw new FSRemoteException(e.getMessage());
        }
    }

}
