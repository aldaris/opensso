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
 * $Id: SsoServerIDFFSvcImpl.java,v 1.2 2009-08-03 18:15:01 bigfatrat Exp $
 *
 */

package com.sun.identity.monitoring;

import com.sun.identity.shared.debug.Debug;
import com.sun.management.snmp.agent.SnmpMib;
import javax.management.MBeanServer;

/**
 * This class extends the "SsoServerIDFFSvc" class.
 */
public class SsoServerIDFFSvcImpl extends SsoServerIDFFSvc {
    private static Debug debug = null;
    private static String myMibName;

    /**
     * Constructors
     */
    public SsoServerIDFFSvcImpl(SnmpMib myMib) {
        super(myMib);
        myMibName = myMib.getMibName();
        init(myMib, null);
    }

    public SsoServerIDFFSvcImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
        myMibName = myMib.getMibName();
        init(myMib, server);
    }

    private void init(SnmpMib myMib, MBeanServer server) {
        SsoServerIDFFIdLocalSessToken = new Long(0);
        SsoServerIDFFIdAuthnRqt = new Long(0);
        SsoServerIDFFUserIDSessionList = new Long(0);
        SsoServerIDFFArtifacts = new Long(0);
        SsoServerIDFFAssertions = new Long(0);
        SsoServerIDFFStatus = new String("dormant");
        SsoServerIDFFRelayState = new Long(0);
        SsoServerIDFFIdDestn = new Long(0);
    }

    public void incIdLocalSessToken() {
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        long li = SsoServerIDFFIdLocalSessToken.longValue();
        li++;
        SsoServerIDFFIdLocalSessToken = Long.valueOf(li);
    }

    public void decIdLocalSessToken() {
        long li = SsoServerIDFFIdLocalSessToken.longValue();
        li--;
        SsoServerIDFFIdLocalSessToken = Long.valueOf(li);
    }

    public void setIdLocalSessToken(long count) {
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        SsoServerIDFFIdLocalSessToken = Long.valueOf(count);
    }

    public void incIdAuthnRqt() {
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        long li = SsoServerIDFFIdAuthnRqt.longValue();
        li++;
        SsoServerIDFFIdAuthnRqt = Long.valueOf(li);
    }

    public void incUserIDSessionList() {
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        long li = SsoServerIDFFUserIDSessionList.longValue();
        li++;
        SsoServerIDFFUserIDSessionList = Long.valueOf(li);
    }

    public void decUserIDSessionList() {
        long li = SsoServerIDFFUserIDSessionList.longValue();
        li--;
        SsoServerIDFFUserIDSessionList = Long.valueOf(li);
    }

    public void setUserIDSessionList(long count) {
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        SsoServerIDFFUserIDSessionList = Long.valueOf(count);
    }

    public void incArtifacts() {
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        long li = SsoServerIDFFArtifacts.longValue();
        li++;
        SsoServerIDFFArtifacts = Long.valueOf(li);
    }

    public void decArtifacts() {
        long li = SsoServerIDFFArtifacts.longValue();
        li--;
        SsoServerIDFFArtifacts = Long.valueOf(li);
    }

    public void setArtifacts(long count) {
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        SsoServerIDFFArtifacts = Long.valueOf(count);
    }

    public void incAssertions() {
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        long li = SsoServerIDFFAssertions.longValue();
        li++;
        SsoServerIDFFAssertions = Long.valueOf(li);
    }

    public void decAssertions() {
        long li = SsoServerIDFFAssertions.longValue();
        li--;
        SsoServerIDFFAssertions = Long.valueOf(li);
    }

    public void setAssertions(long count) {
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        SsoServerIDFFAssertions = Long.valueOf(count);
    }

    public void setRelayState(long state) {
        // might need to change this attribute's type
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        SsoServerIDFFRelayState = Long.valueOf(state);
    }

    public void incIdDestn() {
        // is this a counter?
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        long li = SsoServerIDFFIdDestn.longValue();
        li++;
        SsoServerIDFFIdDestn = Long.valueOf(li);
    }

    public void decIdDestn() {
        long li = SsoServerIDFFIdDestn.longValue();
        li--;
        SsoServerIDFFIdDestn = Long.valueOf(li);
    }

    public void setIdDestn(long count) {
        if (SsoServerIDFFStatus.equals("dormant")) {
            SsoServerIDFFStatus = "operational";
        }
        SsoServerIDFFIdDestn = Long.valueOf(count);
    }
}
