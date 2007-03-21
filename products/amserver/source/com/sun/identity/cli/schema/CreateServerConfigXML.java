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
 * $Id: CreateServerConfigXML.java,v 1.1 2007-03-21 22:33:42 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli.schema;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOToken;
import com.sun.identity.cli.AuthenticatedCommand;
import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.IArgument;
import com.sun.identity.cli.LogWriter;
import com.sun.identity.cli.RequestContext;
import com.sun.identity.log.Level;
import com.sun.identity.security.EncodeAction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.AccessController;

public class CreateServerConfigXML extends AuthenticatedCommand {
    static final String DS_HOST = "dshost";
    static final String DS_PORT = "dsport";
    static final String DS_ADMIN = "dsadmin";
    static final String DS_PWD = "dspassword";
    
    private String dsHost;
    private String dsPort;
    private String dsAdmin;
    private String dsPassword;
    
    /**
     * Handles request.
     *
     * @param rc Request Context.
     * @throws CLIException if request cannot be processed.
     */
    public void handleRequest(RequestContext rc)
        throws CLIException {
        super.handleRequest(rc);
        ldapLogin();
        SSOToken adminSSOToken = getAdminSSOToken();
        String outputFile = getStringOptionValue(IArgument.OUTPUT_FILE);
        FileOutputStream fout = null;
        String[] param = {"tty"};
        String[] paramException = {"tty", ""};
        
        dsHost = this.getStringOptionValue(DS_HOST);
        dsPort = this.getStringOptionValue(DS_PORT);
        dsAdmin = this.getStringOptionValue(DS_ADMIN);
        dsPassword = this.getStringOptionValue(DS_PWD);
        
        if ((dsHost == null) || (dsHost.length() == 0)) {
            dsHost = "ds.opensso.java.net";
        }
        if ((dsPort == null) || (dsPort.length() == 0)) {
            dsPort = "389";
        }
        if ((dsAdmin == null) || (dsAdmin.length() == 0)) {
            dsAdmin = "cn=Directory Manager";
        }
        if ((dsPassword == null) || (dsPassword.length() == 0)) {
            dsPassword = "11111111";
        }
        dsPassword = (String)AccessController.doPrivileged(
            new EncodeAction(dsPassword));
        
        try {
            if ((outputFile != null) && (outputFile.length() > 0)) {
                fout = new FileOutputStream(outputFile);
                param[0] = outputFile;
                paramException[0] = outputFile;
            }       
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "ATTEMPT_CREATE_SERVERCONFIG_XML", param);
            
            String template = getServerConfigXMLTemplate(paramException);
            String modified = modifyXML(template);
            
            if (fout != null) {
                fout.write(modified.getBytes());
            } else {
                getOutputWriter().printlnMessage(modified);
            }
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "SUCCEEDED_CREATE_SERVERCONFIG_XML", param);
        } catch (IOException e) {
            paramException[1] = e.getMessage();
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "FAILED_CREATE_SERVERCONFIG_XML", paramException);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException ioe) {
                    //ignored
                }
            }
        }
    }
    
    private String getServerConfigXMLTemplate(String[] paramException) 
        throws CLIException  {
        BufferedReader reader = null;
        StringBuffer buff = new StringBuffer();
        
        String path = SystemProperties.get(SystemProperties.CONFIG_PATH);
        String configFile = path + File.separator + 
            SystemProperties.CONFIG_FILE_NAME;

        try {
            reader = new BufferedReader(new FileReader(configFile));
            String line = reader.readLine();
            while (line != null) {
                buff.append(line).append("\n");
                line = reader.readLine();
            }
        } catch (IOException e) {
            paramException[1] = e.getMessage();
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "FAILED_CREATE_SERVERCONFIG_XML", paramException);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    //ignored
                }
            }
        }
        
        return buff.toString();
    }
    
    private String modifyXML(String xml) {
        int start = xml.indexOf("<ServerGroup name=\"sms");
        int end = xml.indexOf("<BaseDN>", start);
        
        return xml.substring(0, start) +
            "<ServerGroup name=\"sms\" minConnPool=\"1\" maxConnPool=\"10\">\n"+
            "    <Server name=\"Server1\" host=\"" + dsHost + "\" port=\"" +
            dsPort + "\"\n        type=\"SIMPLE\" />\n" +
            "        <User name=\"User2\" type=\"admin\">\n" +
            "             <DirDN>" + dsAdmin + "</DirDN>\n" +
            "             <DirPassword>" + dsPassword + "</DirPassword>\n" + 
            "        </User>\n        " + xml.substring(end);
    }
}
