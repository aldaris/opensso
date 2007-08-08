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
 * $Id: ConfigureCLI.java,v 1.1 2007-08-08 18:30:06 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.cli;

import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import java.net.InetAddress;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;

/**
 * This class verifies that the CLI can be executed on this host.
 */
public class ConfigureCLI extends TestCommon {
    
    /** Creates a new instance of ConfigureCLI */
    public ConfigureCLI() {
        super("ConfigureCLI");
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
            ResourceBundle rb_amconfig = ResourceBundle.getBundle(
                    TestConstants.TEST_PROPERTY_AMCONFIG); 
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
}
