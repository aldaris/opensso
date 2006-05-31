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
 * $Id: CLIUtil.java,v 1.1 2006-05-31 21:49:42 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli;


import com.iplanet.am.util.Debug;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is an utility class. 
 */
public class CLIUtil {
    private CLIUtil() {
    }

    /**
     * Returns content of a file.
     *
     * @param fileName Name of file.
     * @return content of a file.
     * @throws CLIException if file content cannot be returned.
     */
    public static String getFileContent(String fileName)
        throws CLIException
    {
        return getFileContent(fileName, false);
    }

    /**
     * Returns content of a file.
     *
     * @param fileName Name of file.
     * @param singleLine <code>true</code> to only read one line from the file.
     * @return content of a file.
     * @throws CLIException if file content cannot be returned.
     */
    public static String getFileContent(String fileName, boolean singleLine)
        throws CLIException
    {
        StringBuffer buff = new StringBuffer();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fileName));
            if (in.ready()) {
                String line = in.readLine();
                while (line != null) {
                    buff.append(line);
                    if (singleLine) {
                        break;
                    } else {
                        buff.append("\n");
                        line = in.readLine();
                    }
                }
            }
        } catch(IOException e){
            throw new CLIException(e.getMessage(), ExitCodes.CANNOT_READ_FILE);
        } finally {
            if (in != null ) {
                try {
                    in.close();
                } catch (Exception e) {
                    Debug debugger = CommandManager.getDebugger();
                    if (debugger.warningEnabled()) {
                        debugger.warning("cannot close file, " + fileName, e);
                    }
                }
            }
        }
        return buff.toString();
    }
}
