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
 * $Id: SsoServerLoggingHdlrEntryImpl.java,v 1.1 2009-06-19 02:23:16 bigfatrat Exp $
 *
 */

package com.sun.identity.monitoring;

import com.sun.identity.shared.debug.Debug;
import com.sun.management.snmp.agent.SnmpMib;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * This class extends the "SsoServerLoggingHdlrEntry" class.
 */
public class SsoServerLoggingHdlrEntryImpl extends SsoServerLoggingHdlrEntry {
    private static Debug debug = null;
    private static String myMibName;

    /**
     * Constructor
     */
    public SsoServerLoggingHdlrEntryImpl (SnmpMib myMib) {
        super(myMib);
        myMibName = myMib.getMibName();
        init();
    }

    private void init() {
        if (debug == null) {
            debug = Debug.getInstance("amMonitoring");
        }
        SsoServerLoggingHdlrConnFailed = new Long(0);
        SsoServerLoggingHdlrConnMade = new Long(0);
        SsoServerLoggingHdlrConnRqts = new Long(0);
        SsoServerLoggingHdlrDroppedCt = new Long(0);
        SsoServerLoggingHdlrFailureCt = new Long(0);
        SsoServerLoggingHdlrSuccessCt = new Long(0);
        SsoServerLoggingHdlrRqtCt = new Long(0);
    }

    public ObjectName
        createSsoServerLoggingHdlrEntryObjectName (MBeanServer server)
    {
        String classModule = "SsoServerLoggingHdlrEntryImpl." +
            "createSsoServerLoggingHdlrEntryObjectName: ";
        String prfx = "ssoServerLoggingHdlrEntry.";

        if (debug.messageEnabled()) {
            debug.message(classModule +
                "\n    SsoServerLoggingHdlrIndex = " +
                SsoServerLoggingHdlrIndex +
                "\n    SsoServerLoggingHdlrName = " +
                SsoServerLoggingHdlrName);
        }

        String objname = myMibName +
            "/ssoServerLoggingHdlrTable:" +
            prfx + "ssoServerLoggingHdlrName=" + SsoServerLoggingHdlrName;

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

    /*
     * Increment the handler's logging request count
     */
    public void incHandlerRequestCount(int n) {
        long li = SsoServerLoggingHdlrRqtCt.longValue();
        li = li + n;
        SsoServerLoggingHdlrRqtCt = Long.valueOf(li);
    }

    /*
     * Increment the handler's count of logging requests that had to be dropped.
     * This may happen in cases like - There are lot of requests for DB logging.
     * There are more requests than the buffer can accommodate and hence the
     * extra requests need to be dropped.
     */
    public void incHandlerDroppedCount(int n) {
        long li = SsoServerLoggingHdlrDroppedCt.longValue();
        li = li + n;
        SsoServerLoggingHdlrDroppedCt = Long.valueOf(li);
    }

    /*
     * Increment the handler's logging failure count
     */
    public void incHandlerFailureCount(int n) {
        long li = SsoServerLoggingHdlrFailureCt.longValue();
        li = li + n;
        SsoServerLoggingHdlrFailureCt = Long.valueOf(li);
    }

    /*
     * Increment the handler's logging success count
     */
    public void incHandlerSuccessCount(int n) {
        long li = SsoServerLoggingHdlrSuccessCt.longValue();
        li = li + n;
        SsoServerLoggingHdlrSuccessCt = Long.valueOf(li);
    }

    /*
     * Increment the DB handler's DB connection requests count
     */
    public void incHandlerConnectionRequests(int n) {
        long li = SsoServerLoggingHdlrConnRqts.longValue();
        li = li + n;
        SsoServerLoggingHdlrConnRqts = Long.valueOf(li);
    }

    /*
     * Increment the DB handler's DB connections failed count
     */
    public void incHandlerConnectionsFailed(int n) {
        long li = SsoServerLoggingHdlrFailureCt.longValue();
        li = li + n;
        SsoServerLoggingHdlrFailureCt = Long.valueOf(li);
    }

    /*
     * Increment the DB/File handler's DB/File connections made count.
     * This is NOT showing the number of DB/File connections currently open,
     * BUT rather the number of times DB/File has been connected to from the
     * particular log handler (DB, File, Secure File, Remote etc.)
     * since the OpenSSO server came up.
     */
    public void incHandlerConnectionsMade(int n) {
        long li = SsoServerLoggingHdlrConnMade.longValue();
        li = li + n;
        SsoServerLoggingHdlrConnMade = Long.valueOf(li);
    }

}
