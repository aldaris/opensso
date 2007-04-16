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
 * $Id: ImportServiceConfiguration.java,v 1.2 2007-04-16 07:14:13 veiming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli.schema;

import com.iplanet.services.ldap.DSConfigMgr;
import com.iplanet.services.ldap.LDAPServiceException;
import com.iplanet.services.ldap.LDAPUser;
import com.iplanet.services.ldap.ServerGroup;
import com.iplanet.services.util.AMEncryption;
import com.iplanet.services.util.ConfigurableKey;
import com.iplanet.services.util.JCEEncryption;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.common.LDAPUtils;
import com.sun.identity.cli.AuthenticatedCommand;
import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.CommandManager;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.LogWriter;
import com.sun.identity.cli.IArgument;
import com.sun.identity.cli.IOutput;
import com.sun.identity.cli.RequestContext;
import com.sun.identity.sm.SMSEntry;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.logging.Level;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.util.LDIF;

/**
 * Import service configuration data.
 */
public class ImportServiceConfiguration extends AuthenticatedCommand {
    static final String DB_TYPE = "datastoretype";
    static final String TYPE_SUN_DS = "sunds";
    static final String TYPE_AD = "ad";
    static final String TYPE_FLATFILE = "file";

    static final String DS_LDIF = "am_sm_ds_schema.ldif";
    static final String AD_LDIF = "am_sm_ad_schema.ldif";


    /**
     * Services a Commandline Request.
     *
     * @param rc Request Context.
     * @throw CLIException if the request cannot serviced.
     */
    public void handleRequest(RequestContext rc) 
        throws CLIException {
        super.handleRequest(rc);
        ldapLogin();

        String xmlFile = getStringOptionValue(IArgument.XML_FILE);
        String dbType = getStringOptionValue(DB_TYPE);
        String encryptSecret = getStringOptionValue(IArgument.ENCRYPT_SECRET);

        if (!dbType.equals(TYPE_SUN_DS) && !dbType.equals(TYPE_AD) &&
            !dbType.equals(TYPE_FLATFILE)
        ) {
            throw new CLIException(
                getResourceString(
                    "import-service-configuration-directory-invalid-ds-type"),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED, null);
        }

        LDAPConnection ldConnection = null;
        try {
            if (dbType.equals(TYPE_SUN_DS) || dbType.equals(TYPE_AD)) {
                ldConnection = getLDAPConnection();
                loadLDIF(dbType, ldConnection);
                disconnectDServer(ldConnection);
                ldConnection = null;
            }
            importData(xmlFile, encryptSecret);
        } finally {
            disconnectDServer(ldConnection);
        }
    }
       
    private void importData(String xmlFile, String encryptSecret)
        throws CLIException { 
        SSOToken adminSSOToken = getAdminSSOToken();
        IOutput outputWriter = getOutputWriter();        
        CommandManager mgr = getCommandManager();
        String[] param = {xmlFile};
        writeLog(LogWriter.LOG_ACCESS, Level.INFO,
            "ATTEMPT_IMPORT_SM_CONFIG_DATA", param);

        FileInputStream fis = null;

        try {
            AMEncryption encryptObj = new JCEEncryption();
            ((ConfigurableKey)encryptObj).setPassword(encryptSecret);
            
            ServiceManager ssm = new ServiceManager(adminSSOToken);
            fis = new FileInputStream(xmlFile);
            ssm.registerServices(fis, encryptObj);

            outputWriter.printlnMessage(getResourceString(
                "import-service-configuration-succeeded"));
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "SUCCESS_IMPORT_SM_CONFIG_DATA", param);
        } catch (IOException e) {
            String[] args = {xmlFile, e.getMessage()};
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "FAILED_IMPORT_SM_CONFIG_DATA", args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (SSOException e) {
            String[] args = {xmlFile, e.getMessage()};
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "FAILED_IMPORT_SM_CONFIG_DATA", args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (SMSException e) {
            String[] args = {xmlFile, e.getMessage()};
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "FAILED_IMPORT_SM_CONFIG_DATA", args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (Exception e) {
            String[] args = {xmlFile, e.getMessage()};
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "FAILED_IMPORT_SM_CONFIG_DATA", args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ie) {
                    //ignore if file input stream cannot be closed.
                }
            }
        }
    }

    private int validateDSPort(String port)
        throws CLIException {
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new CLIException(
                getResourceString("import-service-configuration-invalid-port"),
                    ExitCodes.REQUEST_CANNOT_BE_PROCESSED, null);
        }
    }

    private LDAPConnection getLDAPConnection()
        throws CLIException {

        IOutput outputWriter = getOutputWriter();
        if (isVerbose()) {
            outputWriter.printlnMessage(
                getResourceString(
                    "import-service-configuration-connecting-to-ds"));
        }
        
        LDAPConnection ld = null;

        try {
            DSConfigMgr dsCfg = DSConfigMgr.getDSConfigMgr();
            ServerGroup sg = dsCfg.getServerGroup("sms");
            if (sg != null) {
                ld = dsCfg.getNewConnection("sms",LDAPUser.Type.AUTH_ADMIN);
            } else  {
                throw new CLIException(
                    getResourceString(
                        "import-service-configuration-not-connect-to-ds"),
                    ExitCodes.REQUEST_CANNOT_BE_PROCESSED, null);
            }
           
            if (isVerbose()) {
                outputWriter.printlnMessage(
                    getResourceString(
                        "import-service-configuration-connected-to-ds"));
            }
            return ld;
        } catch (LDAPServiceException e) {
            throw new CLIException(
                getResourceString(
                    "import-service-configuration-not-connect-to-ds"),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED, null);
        }
    }

    private void disconnectDServer(LDAPConnection ld) {
        if ((ld != null) && ld.isConnected()) {
            try {
                ld.disconnect();
            } catch (LDAPException e) {
                debugWarning("cannot discount from directory server", e);
            }
        }
    }


    private void loadLDIF(String type, LDAPConnection ld)
        throws CLIException {
        DataInputStream d = null;

        try {
            if (type.equals(TYPE_SUN_DS)) {
                d = new DataInputStream(
                    getClass().getClassLoader().getResourceAsStream(DS_LDIF));
                LDIF ldif = new LDIF(d);
                LDAPUtils.createSchemaFromLDIF(ldif, ld);
            } else if (type.equals(TYPE_AD)) {
                String rootDn = SMSEntry.getRootSuffix();
                BufferedReader buff = new BufferedReader(
                    new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream(
                            AD_LDIF)));
                StringBuffer sb = new StringBuffer();

                for (String s = buff.readLine(); (s != null);
                    s = buff.readLine()
                ) {
                    sb.append(s.replaceAll("@ROOT_SUFFIX@", rootDn))
                        .append("\n");
                }
                d = new DataInputStream(new ByteArrayInputStream(
                    sb.toString().getBytes()));
                LDIF ldif = new LDIF(d);
                LDAPUtils.createSchemaFromLDIF(ldif, ld);
            }
        } catch (LDAPException e) {
            e.printStackTrace();
            throw new CLIException(e.getMessage(),
                    ExitCodes.REQUEST_CANNOT_BE_PROCESSED, null);
        } catch (IOException e) {
            throw new CLIException(e.getMessage(),
                    ExitCodes.REQUEST_CANNOT_BE_PROCESSED, null);
        } finally {
            if (d != null) {
                try {
                    d.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
    }
}
