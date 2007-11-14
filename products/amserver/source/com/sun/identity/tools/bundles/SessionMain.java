/**
 * $Id: SessionMain.java,v 1.1 2007-11-14 00:26:07 manish_rustagi Exp $
 * Copyright © 2005 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to
 * technology embodied in the product that is described in this document.
 * In particular, and without limitation, these intellectual property rights
 * may include one or more of the U.S. patents listed at
 * http://www.sun.com/patents and one or more additional patents or pending
 * patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and applicable
 * provisions of the FAR and its supplements.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.Sun,
 * Sun Microsystems and  the Sun logo are trademarks or registered trademarks
 * of Sun Microsystems, Inc. in the U.S. and other countries.  
 *
 * Copyright © 2005 Sun Microsystems, Inc. Tous droits réservés.
 * Sun Microsystems, Inc. détient les droits de propriété intellectuels relatifs
 * à la technologie incorporée dans le produit qui est décrit dans ce document.
 * En particulier, et ce sans limitation, ces droits de propriété
 * intellectuelle peuvent inclure un ou plus des brevets américains listés
 * à l'adresse http://www.sun.com/patents et un ou les brevets supplémentaires
 * ou les applications de brevet en attente aux Etats - Unis et dans les
 * autres pays.
 *
 * L'utilisation est soumise aux termes du contrat de licence.
 *
 * Cette distribution peut comprendre des composants développés par des
 * tierces parties.
 *
 * Sun,  Sun Microsystems et  le logo Sun sont des marques de fabrique ou des
 * marques déposées de Sun Microsystems, Inc. aux Etats-Unis et dans
 * d'autres pays.
 */

package com.sun.identity.tools.bundles;

import java.io.File;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

public class SessionMain implements SetupConstants{
    
    public static void main(String[] args) {
        String destPath = null;
        String currentOS = null;
        Properties configProp=new Properties();
        
        ResourceBundle bundle = ResourceBundle.getBundle(System.getProperty(
            SETUP_PROPERTIES_FILE, DEFAULT_SESSION_PROPERTIES_FILE));
        if (System.getProperty(PRINT_HELP) != null) {
            if (System.getProperty(PRINT_HELP).equals(YES)) {
                SetupUtils.printUsage(bundle);
                System.exit(0);
            }
        }
        currentOS = SetupUtils.determineOS();
        destPath = System.getProperty(PATH_DEST);
        try {
            if ((destPath == null) || (destPath.length() == 0)) {
                destPath = SetupUtils.getUserInput(bundle.getString(currentOS
                    + QUESTION));
            }
        } catch (IOException ex) {
            System.out.println(bundle.getString("message.error.input"));
            System.exit(1);
        }
        configProp.setProperty(USER_INPUT, destPath);
        configProp.setProperty(CURRENT_PLATFORM, currentOS);
        SetupUtils.evaluateBundleValues(bundle, configProp);
        try {
            SetupUtils.copyAndFilterScripts(bundle, configProp);
        } catch (IOException ex) {
            System.out.println(bundle.getString("message.error.copy"));
            System.exit(1);
            //ex.printStackTrace();
        }
        String extDir = null;
        try{
            extDir = bundle.getString(EXT_DIR);
        } catch (MissingResourceException ex) {
            extDir = DEFAULT_EXT_DIR;
        }
        String jmqDir = null;
        try{
            jmqDir = bundle.getString(JMQ_DIR); 
        } catch (MissingResourceException ex) {
            jmqDir = DEFAULT_JMQ_DIR;
        }
        try {
            String jmqFileName = bundle.getString(currentOS + JMQ);
            if (currentOS.equals(WINDOWS)) {
                SetupUtils.unzip(extDir + FILE_SEPARATOR + jmqFileName, jmqDir,
                    true);
                System.out.println(bundle.getString("message.info.jmq.success")
                    + " " + jmqDir + ".");
            } else {
                Process proc = Runtime.getRuntime().exec("unzip -o -q " + extDir
                    + FILE_SEPARATOR + jmqFileName + " -d " + jmqDir);
                try {
                    if (proc.waitFor() != 0) {
                        System.out.println(bundle.getString(
                            "message.info.jmq.fail") + " " + jmqDir + ".");
                    } else {
                        System.out.println(bundle.getString(
                            "message.info.jmq.success") + " " + jmqDir + ".");
                    }
                } catch (InterruptedException ex) {
                    System.out.println(bundle.getString(
                        "message.info.jmq.fail") + " " + jmqDir + ".");
                }
            }
        } catch (IOException ex) {
            System.out.println(bundle.getString("message.error.jmq"));
            System.out.println(bundle.getString("message.info.jmq.fail") + " " +
                jmqDir + ".");
            //ex.printStackTrace();
        }
        
        /*
        String bdbDir = null;
        try{
            bdbDir = bundle.getString(BDB_DIR);
        } catch (MissingResourceException ex) {
            bdbDir = DEFAULT_BDB_DIR;
        }
        try {
        	
            String bdbFileName = bundle.getString(currentOS + BDB);
            if (currentOS.equals(WINDOWS)) {
                SetupUtils.unzip(extDir + FILE_SEPARATOR + bundle.getString(
                    currentOS + BDB), bdbDir, true);
                System.out.println(bundle.getString("message.info.bdb.success")
                    + " " + bdbDir + ".");
            } else{
                SetupUtils.ungzip(extDir + FILE_SEPARATOR + bdbFileName,
                    bdbDir);
                File tarFile = new File(bdbDir + FILE_SEPARATOR +
                    bdbFileName.substring(0, bdbFileName
                    .lastIndexOf(GZIP_EXT)));
                Process proc = Runtime.getRuntime().exec("tar -xf " +
                    tarFile.getName(), null, new File(bdbDir));
                try {
                    if (proc.waitFor() != 0) {
                        System.out.println(bundle.getString(
                            "message.info.bdb.fail") + " " + bdbDir + ".");
                    } else {
                        System.out.println(bundle.getString(
                            "message.info.bdb.success") + " " + bdbDir + ".");
                    }
                } catch (InterruptedException ex) {
                    System.out.println(bundle.getString("message.info.bdb.fail")
                        + " " + bdbDir + ".");
                }
                tarFile.delete();
            }
        } catch (IOException ex) {
            System.out.println(bundle.getString("message.error.bdb"));
            System.out.println(bundle.getString("message.info.bdb.fail") + " " +
                bdbDir + ".");
            //ex.printStackTrace();
        }*/
    }

}
