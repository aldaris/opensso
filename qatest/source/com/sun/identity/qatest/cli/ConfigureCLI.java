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
 * $Id: ConfigureCLI.java,v 1.2 2007-08-16 19:39:19 cmwesley Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.cli;

import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import com.sun.identity.qatest.common.cli.CLIUtility;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.Map;
import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;

/**
 * This class verifies that the CLI can be executed on this host.
 */
public class ConfigureCLI extends CLIUtility {
    
    ResourceBundle rb_amconfig = ResourceBundle.getBundle(
                    TestConstants.TEST_PROPERTY_AMCONFIG); 
    
    /** Creates a new instance of ConfigureCLI */
    public ConfigureCLI() {
        super(cliPath + System.getProperty("file.separator") + "setup");
    }
       
    /**
     * Checks if the local host name on which tests are executing
     * is same as the one mentioned in AMConfig.properties file. If
     * same, continue the execution else abort. 
     */
    @BeforeSuite(groups={"ds_ds", "ff_ds", "ds_ds_sec", "ff_ds_sec"})
    public void configureCLI()
    throws Exception {
        entering("configureCLI", null);
        try {
            String amconfigHost = 
                    rb_amconfig.getString(TestConstants.KEY_AMC_HOST);
            log(Level.FINEST, "configureCLI", "Value of " + 
                    TestConstants.KEY_AMC_HOST + ": " + amconfigHost);
            String[] fqdnElements = amconfigHost.split("\\.");
            String fqdnHostname = 
                    InetAddress.getLocalHost().getHostName();
            if (fqdnElements.length > 0) {
                log(Level.FINEST, "configureCLI", "AMConfig hostname: " + 
                        fqdnElements[0]);
                log(Level.FINEST, "configureCLI", "Hostname from getHostName: " + 
                        fqdnHostname);
                if (!fqdnElements[0].equals(fqdnHostname)) {
                    log(Level.SEVERE, "configureCLI", 
                            "ERROR: The CLI tests must be run on the same host " +
                            "on which Federated Access Manager is deployed.");
                    Reporter.log("ERROR: The CLI tests must be run on the same host " +
                            "on which Federated Access Manager is deployed.");
                    assert false;
                }
                
                ResourceBundle rb_cli = ResourceBundle.getBundle("cliTest");
                File cliDir = new File(rb_cli.getString("cli-path"));
                String cliAbsPath = cliDir.getAbsolutePath();
                File binDir = new File(new StringBuffer(cliAbsPath).
                        append(fileseparator).append(uri).append(fileseparator).
                        append("bin").toString());
                if (!binDir.exists()) {
                    if (cliDir.exists() && cliDir.isDirectory()) {
                        configureTools();
                        log(Level.FINE, "configureCLI", 
                                "Sleeping for 30 seconds to create utilities");
                        Thread.sleep(30000);
                        if (!binDir.exists()) {
                            log(Level.SEVERE, "configureCLI", 
                                    "The setup script failed to create " + 
                                    binDir.getAbsolutePath());
                            assert false;
                        }
                    } else {
                        log(Level.SEVERE, "configureCLI", "The directory " + 
                                cliAbsPath + " is not a directory.");
                        assert false;
                    }
                }
            } else {
                log(Level.SEVERE, "configureCLI", "ERROR: Unable to get host " +
                        "name from " + amconfigHost + ".");
                Reporter.log("ERROR: Unable to get host " +
                        "name from " + amconfigHost + ".");
                assert false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "configureCLI", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("configureCLI");
    } 
    
    /**
     * Execute the setup script to configure the administration utilities.
     */
    private void configureTools() 
    throws Exception {
        clearArguments(2);
        setArgument(1, "-p");
        setArgument(2, rb_amconfig.getString(TestConstants.KEY_ATT_CONFIG_DIR));
        executeCommand(60);
        logCommand("configureTools");
    }
}
