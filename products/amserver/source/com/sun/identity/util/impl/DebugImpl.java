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
 * $Id: DebugImpl.java,v 1.1 2005-11-01 00:31:38 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.util.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.iplanet.am.util.Debug;
import com.iplanet.am.util.Locale;
import com.iplanet.am.util.SystemProperties;
import com.sun.identity.util.IDebug;

public class DebugImpl implements IDebug {

    private static final String CONFIG_DEBUG_DIRECTORY = 
        "com.iplanet.services.debug.directory";

    private static final String CONFIG_DEBUG_LEVEL = 
        "com.iplanet.services.debug.level";

    private String debugName;

    private int debugLevel = Debug.ON;

    private PrintWriter debugWriter;

    private PrintWriter stdoutWriter = new PrintWriter(System.out, true);

    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "MM/dd/yyyy hh:mm:ss:SSS a zzz");

    private String debugFilePath;

    public DebugImpl(String debugName) {
        setName(debugName);
        String debugDirectory = SystemProperties.get(CONFIG_DEBUG_DIRECTORY);
        boolean directoryAvailable = false;
        if (debugDirectory != null && debugDirectory.trim().length() > 0) {
            File dir = new File(debugDirectory);
            if (!dir.exists()) {
                directoryAvailable = dir.mkdirs();
            } else {
                if (dir.isDirectory() && dir.canWrite()) {
                    directoryAvailable = true;
                }
            }
        }
        if (directoryAvailable) {
            setDebug(SystemProperties.get(CONFIG_DEBUG_LEVEL));
            this.debugWriter = null;
            this.debugFilePath = debugDirectory + File.separator + debugName;
        } else {
            ResourceBundle bundle = Locale
                    .getInstallResourceBundle("amUtilMsgs");
            System.err.println(bundle
                    .getString("com.iplanet.services.debug.nodir"));
        }
    }

    public String getName() {
        return this.debugName;
    }

    public int getState() {
        return this.debugLevel;
    }

    public void setDebug(int level) {
        switch (level) {
        case Debug.OFF:
        case Debug.ERROR:
        case Debug.WARNING:
        case Debug.MESSAGE:
        case Debug.ON:
            this.debugLevel = level;
            break;
        default:
            // ignore invalid level values
            break;
        }
    }

    public void setDebug(String strDebugLevel) {
        int debugLevel = Debug.ON;
        if (strDebugLevel != null && strDebugLevel.trim().length() > 0) {
            if (strDebugLevel.equals(Debug.STR_OFF)) {
                debugLevel = Debug.OFF;
            } else if (strDebugLevel.equals(Debug.STR_ERROR)) {
                debugLevel = Debug.ERROR;
            } else if (strDebugLevel.equals(Debug.STR_WARNING)) {
                debugLevel = Debug.WARNING;
            } else if (strDebugLevel.equals(Debug.STR_MESSAGE)) {
                debugLevel = Debug.MESSAGE;
            }
        }
        setDebug(debugLevel);
    }

    public boolean messageEnabled() {
        return (this.debugLevel > Debug.WARNING);
    }

    public boolean warningEnabled() {
        return (this.debugLevel > Debug.ERROR);
    }

    public boolean errorEnabled() {
        return (this.debugLevel > Debug.OFF);
    }

    public void message(String message, Throwable th) {
        if (messageEnabled()) {
            record(message, th);
        }
    }

    public void warning(String message, Throwable th) {
        if (warningEnabled()) {
            record("WARNING: " + message, th);
        }
    }

    public void error(String message, Throwable th) {
        if (errorEnabled()) {
            record("ERROR: " + message, th);
        }
    }

    private void record(String msg, Throwable th) {
        String prefix = this.dateFormat.format(new Date()) + ": "
                + Thread.currentThread().toString();
        writeIt(prefix, msg, th);
    }

    private synchronized void writeIt(String prefix, String msg, Throwable th) {
        if (this.debugLevel == Debug.ON) {
            writeIt(this.stdoutWriter, prefix, msg, th);
        } else {
            if (this.debugWriter == null) {
                try {
                    this.debugWriter = new PrintWriter(new FileWriter(
                            this.debugFilePath, true), true);
                    this.debugWriter.println("*************************" +
                            "*****************************");
                    writeIt(this.debugWriter, prefix, msg, th);
                } catch (IOException ioex) {
                    // turn debugging to STDOUT since debug file is not
                    // available
                    setDebug(Debug.ON);
                    ResourceBundle bundle = Locale
                            .getInstallResourceBundle("amUtilMsgs");
                    System.err.println(bundle
                            .getString("com.iplanet.services.debug.nofile"));
                    ioex.printStackTrace(System.err);
                    if (this.debugWriter != null) {
                        try {
                            this.debugWriter.close();
                        } catch (Exception ex1) {
                            // No handling required
                        }
                    }
                    writeIt(this.stdoutWriter, prefix, msg, th);
                }
            } else {
                writeIt(this.debugWriter, prefix, msg, th);
            }
        }
    }

    private synchronized void writeIt(PrintWriter writer, String prefix,
            String msg, Throwable th) {
        writer.println(prefix);
        writer.println(msg);
        if (th != null) {
            th.printStackTrace(writer);
        }
    }

    private void setName(String debugName) {
        this.debugName = debugName;
    }

    protected void finalize() throws Throwable {
        if (this.debugWriter != null) {
            try {
                this.debugWriter.flush();
                this.debugWriter.close();
            } catch (Exception ex) {
                // No handling required
            }
        }
    }
}
