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
 * $Id: SsoServerSAML2IDPEntryImpl.java,v 1.1 2009-06-19 02:23:18 bigfatrat Exp $
 *
 */

package com.sun.identity.monitoring;

import com.sun.identity.shared.debug.Debug;
import com.sun.management.snmp.agent.SnmpMib;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * This class extends the "SsoServerSAML2IDPEntry" class.
 */
public class SsoServerSAML2IDPEntryImpl extends SsoServerSAML2IDPEntry {
    private static Debug debug = null;
    private static String myMibName;

    /**
     * Constructor
     */
    public SsoServerSAML2IDPEntryImpl (SnmpMib myMib) {
        super(myMib);
        myMibName = myMib.getMibName();
        init();
    }

    private void init() {
        if (debug == null) {
            debug = Debug.getInstance("amMonitoring");
        }
    }

    public ObjectName
        createSsoServerSAML2IDPEntryObjectName (MBeanServer server)
    {
        String classModule = "SsoServerSAML2IDPEntryImpl." +
            "createSsoServerSAML2IDPEntryObjectName: ";
        String prfx = "ssoServerSAML2IDPEntry.";
        String realm = Agent.getRealmNameFromIndex(SsoServerRealmIndex);

        if (debug.messageEnabled()) {
            debug.message(classModule +
                "\n    SsoServerRealmIndex = " +
                SsoServerRealmIndex +
                "\n    SsoServerSAML2IDPIndex = " +
                SsoServerSAML2IDPIndex +
                "\n    SsoServerSAML2IDPName = " +
                SsoServerSAML2IDPName);
        }
        
        String objname = myMibName +
            "/ssoServerSAML2IDPTable:" +
            prfx + "ssoServerRealmName=" + realm + "," +
            prfx + "ssoServerSAML2IDPName=" + SsoServerSAML2IDPName;

        try {
            if (server == null) {
                return null;
            } else {
                // is the object name sufficiently unique?
                return
                    new ObjectName(objname);
            }
        } catch (Exception ex) {
            debug.error(classModule + objname, ex);
            return null;
        }
    }

    public void incSAML2IDPArtifsIssued() {
        String classModule =
            "SsoServerSAML2IDPEntryImpl.incSAML2IDPArtifsIssued:";
        
        if (!Agent.isRunning()) {
            return;
        }
        long li = SsoServerSAML2IDPArtifactsIssued.longValue();
        li++;
        SsoServerSAML2IDPArtifactsIssued = Long.valueOf(li);
    }

    public void incSAML2IDPAssertsIssued() {
        String classModule =
            "SsoServerSAML2IDPEntryImpl.incSAML2IDPAssertsIssued:";
        
        if (!Agent.isRunning()) {
            return;
        }
        long li = SsoServerSAML2IDPAssertionsIssued.longValue();
        li++;
        SsoServerSAML2IDPAssertionsIssued = Long.valueOf(li);
    }

    public void incSAML2IDPInvalRqtsRcvd() {
        String classModule =
            "SsoServerSAML2IDPEntryImpl.incSAML2IDPInvalRqtsRcvd:";
        
        if (!Agent.isRunning()) {
            return;
        }
        long li = SsoServerSAML2IDPInvalRqtsRcvd.longValue();
        li++;
        SsoServerSAML2IDPInvalRqtsRcvd = Long.valueOf(li);
    }

    public void incSAML2IDPRqtsRcvd() {
        String classModule =
            "SsoServerSAML2IDPEntryImpl.incSAML2IDPRqtsRcvd:";
        
        if (!Agent.isRunning()) {
            return;
        }
        long li = SsoServerSAML2IDPRqtsRcvd.longValue();
        li++;
        SsoServerSAML2IDPRqtsRcvd = Long.valueOf(li);
    }

    public void incSAML2IDPArtifsInCache() {
        String classModule =
            "SsoServerSAML2IDPEntryImpl.incSAML2IDPArtifsInCache:";
        
        if (!Agent.isRunning()) {
            return;
        }
        long li = SsoServerSAML2IDPArtifactsInCache.longValue();
        li++;
        SsoServerSAML2IDPArtifactsInCache = Long.valueOf(li);
    }

    public void decSAML2IDPArtifsInCache() {
        String classModule =
            "SsoServerSAML2IDPEntryImpl.decSAML2IDPArtifsInCache:";
        
        if (!Agent.isRunning()) {
            return;
        }
        long li = SsoServerSAML2IDPArtifactsInCache.longValue();
        li--;
        SsoServerSAML2IDPArtifactsInCache = Long.valueOf(li);
    }

    public void incSAML2IDPAssertsInCache() {
        String classModule =
            "SsoServerSAML2IDPEntryImpl.incSAML2IDPAssertsInCache:";
        
        if (!Agent.isRunning()) {
            return;
        }
        long li = SsoServerSAML2IDPAssertionsInCache.longValue();
        li++;
        SsoServerSAML2IDPAssertionsInCache = Long.valueOf(li);
    }

    public void decSAML2IDPAssertsInCache() {
        String classModule =
            "SsoServerSAML2IDPEntryImpl.decSAML2IDPAssertsInCache:";
        
        if (!Agent.isRunning()) {
            return;
        }
        long li = SsoServerSAML2IDPAssertionsInCache.longValue();
        li--;
        SsoServerSAML2IDPAssertionsInCache = Long.valueOf(li);
    }
}