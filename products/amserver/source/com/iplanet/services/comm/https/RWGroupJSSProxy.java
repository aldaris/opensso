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
 * $Id: RWGroupJSSProxy.java,v 1.1 2007-12-14 21:33:38 beomsuk Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.iplanet.services.comm.https;

import java.net.Socket;
import com.sun.identity.shared.debug.Debug;

public class RWGroupJSSProxy extends ReaderWriterLock {
    private Socket fromClient;
    private Socket toServer;

    private ReaderWriterClear src_to_dst;
    private ReaderWriterClear dst_to_src;
    private boolean src_to_dst_clean = false;
    private boolean dst_to_src_clean = false;
    private static Debug debug = Debug.getInstance("amJSS");

    public RWGroupJSSProxy(Socket fromSocket, Socket toSocket)
    {
        fromClient = fromSocket;
        toServer = toSocket;

        src_to_dst = new ReaderWriterClear(this, fromClient, toServer);
        dst_to_src = new ReaderWriterClear(this, toServer, fromClient);
        try {
            JSSThreadPool.run(src_to_dst); 
            JSSThreadPool.run(dst_to_src);
        } 
        catch (InterruptedException e) {
            debug.message("Could not start ReaderWriterClear tasks",e);
        }
    }

    public synchronized void notifyFinished(ReaderWriter obj) {
        if (obj == src_to_dst) {
            if (dst_to_src.isAlive()) {
                dst_to_src.stop();
            }
        } else if (obj == dst_to_src) {
            if (src_to_dst.isAlive()) {
                src_to_dst.stop();
            }
        }
        cleanup();
        if (obj == src_to_dst) {
            src_to_dst_clean = true;
        } else if (obj == dst_to_src) {
            dst_to_src_clean = true;
        }
    }

    public void cleanup() {
        if (fromClient != null) {
            try {
                fromClient.close();
            } catch (Exception e) {
            } finally {
                fromClient = null;
            }
        }
        if (toServer != null) {
            try {
                toServer.close();
            } catch (Exception e) {
            } finally {
                toServer = null;
            }
        }
    }

    public boolean isDone() {
        if (dst_to_src_clean && src_to_dst_clean) {
            dst_to_src = null;
            src_to_dst = null;
        }
        return(dst_to_src_clean && src_to_dst_clean);
    }
}

