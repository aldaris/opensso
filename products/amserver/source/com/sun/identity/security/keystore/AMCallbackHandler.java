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
 * $Id: AMCallbackHandler.java,v 1.1 2006-01-28 09:28:37 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.security.keystore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.security.AccessController;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Locale;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.iplanet.am.util.AMResourceBundleCache;
import com.sun.identity.security.SecurityDebug;
import com.sun.identity.security.DecodeAction;


public class AMCallbackHandler implements CallbackHandler {
    static final String bundleName = "amSecurity";
    static ResourceBundle bundle = null;
    static AMResourceBundleCache amCache = AMResourceBundleCache.getInstance(); 
    static String passwdPrompt = null;
    static String passWDFile = System.getProperty(
        "com.sun.identity.security.keyStorePasswordFile", null);
    static transient String keystorePW = System.getProperty(
        "javax.net.ssl.keyStorePassword", null);
    
    static {
        if (SecurityDebug.debug.messageEnabled()) {
            SecurityDebug.debug.message(
                "AMCallbackHandler() : Keystore Password File ---> " +
                    passWDFile); 
        }

        bundle = amCache.getResBundle(bundleName, Locale.getDefault());
        passwdPrompt = bundle.getString("KeyStorePrompt");

        if (passWDFile != null) {
            try {
                    FileInputStream fis = new FileInputStream(passWDFile);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    keystorePW = (String) AccessController.doPrivileged(
                    new DecodeAction(br.readLine())); 
                fis.close(); 
            } catch (Exception ex) {
                ex.printStackTrace();
                    SecurityDebug.debug.error("AMCallbackHandler: Unable to " +
                        "read keystore password file " + passWDFile);
            }
        }
    }
    
    public AMCallbackHandler() {
            this(passwdPrompt);
    }
    
    public AMCallbackHandler(String prompt) {
            super();
            
            if (prompt != null) {
                passwdPrompt = prompt;
            }
    }
    
    public void handle(Callback[] callbacks)
        throws UnsupportedCallbackException {
        int i = 0;
        try {
            for (i = 0; i < callbacks.length; i++) {
                if (callbacks[i] instanceof PasswordCallback) {
                // prompt the user for sensitive information
                    if (SecurityDebug.debug.messageEnabled()) {
                        SecurityDebug.debug.message(
                            "AMCallbackHandler() :  PasswordCallback()");
                    }
                    PasswordCallback pc = (PasswordCallback) callbacks[i];
                    
                    if (keystorePW == null) {
                        if (SecurityDebug.debug.messageEnabled()) {
                            SecurityDebug.debug.message(
                                "AMCallbackHandler() :  Prompt Password ");
                        }

                        if (passwdPrompt != null) {
                            System.out.print(passwdPrompt);
                        } else {
                            System.out.print(pc.getPrompt());
                        }

                        System.out.flush();
                        pc.setPassword(readPassword(System.in));
                    } else {
                        pc.setPassword(keystorePW.toCharArray());
                    }
                } else {
                           SecurityDebug.debug.error("Got UnknownCallback");
                    break;
                }
            }
        } catch (Exception e) {
            SecurityDebug.debug.error("Exception in Callback : "+e);
            e.printStackTrace();
            throw new UnsupportedCallbackException(
                callbacks[i], "Callback exception: " + e);
        }
    }

    // Reads user password from given input stream.
    private static char[] readPassword(InputStream in) throws IOException {
        char[] lineBuffer;
        char[] buf;
        int i;

        buf = lineBuffer = new char[128];

        int room = buf.length;
        int offset = 0;
        int c;

        loop:
            while (true) {
                switch (c = in.read()) {
                    case -1:
                    case '\n':
                        break loop;

                    case '\r':
                        int c2 = in.read();
                        if ((c2 != '\n') && (c2 != -1)) {
                            if (!(in instanceof PushbackInputStream)) {
                                in = new PushbackInputStream(in);
                            }
                            ((PushbackInputStream)in).unread(c2);
                        } else
                            break loop;

                    default:
                        if (--room < 0) {
                            buf = new char[offset + 128];
                            room = buf.length - offset - 1;
                            System.arraycopy(lineBuffer, 0, buf, 0, offset);
                            Arrays.fill(lineBuffer, ' ');
                            lineBuffer = buf;
                        }
                        buf[offset++] = (char) c;
                        break;
                }
            }

            if (offset == 0) {
                return null;
            }

            char[] ret = new char[offset];
            System.arraycopy(buf, 0, ret, 0, offset);
            Arrays.fill(buf, ' ');

            return ret;
    }

    /**
     * Set password for key store 
     * @param passwd Value of string to be set 
     */
    static public void setPassword(String passwd) {
            keystorePW = passwd;
    }
}
        
