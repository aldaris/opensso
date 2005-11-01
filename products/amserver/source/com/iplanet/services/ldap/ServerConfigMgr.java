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
 * $Id: ServerConfigMgr.java,v 1.1 2005-11-01 00:30:18 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.services.ldap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.AccessController;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.security.auth.login.LoginException;

import netscape.ldap.util.DN;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.iplanet.am.util.Debug;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.am.util.XMLUtils;
import com.iplanet.services.util.XMLException;
import com.iplanet.ums.Guid;
import com.iplanet.ums.IUMSConstants;
import com.iplanet.ums.PersistentObject;
import com.iplanet.ums.UMSObject;
import com.sun.identity.authentication.internal.AuthContext;
import com.sun.identity.authentication.internal.AuthPrincipal;
import com.sun.identity.security.DecodeAction;
import com.sun.identity.security.EncodeAction;
import com.sun.identity.sm.SMSSchema;

/**
 * The class <code>ServiceConfigMgr</code> provides interfaces to set the
 * directory server information such as hostname, port number, admin DN and
 * password, and proxy user DN and password.
 */
public class ServerConfigMgr {

    // Private static varibales
    private static final String HELP = "--help";

    private static final String S_HELP = "-h";

    private static final String Q_HELP = "?";

    private static final String SQ_HELP = "-?";

    private static final String ADMIN = "--admin";

    private static final String S_ADMIN = "-a";

    private static final String PROXY = "--proxy";

    private static final String S_PROXY = "-p";

    private static final String OLD = "--old";

    private static final String S_OLD = "-o";

    private static final String NEW = "--new";

    private static final String S_NEW = "-n";

    private static final String ENCRYPT = "--encrypt";

    private static final String S_ENCRYPT = "-e";

    private static final int MIN_PASSWORD_LEN = 8;

    // Run time property key to obtain serverconfig.xml path
    private static final String RUN_TIME_CONFIG_PATH = 
        "com.iplanet.coreservices.configpath";

    private String configFile = null;

    private Document document = null;

    private Node root = null;

    private Node defaultServerGroup = null;

    private static ResourceBundle i18n = ResourceBundle
            .getBundle(IUMSConstants.UMS_PKG);

    private static Debug debug = Debug.getInstance(IUMSConstants.UMS_DEBUG);

    /**
     * Constructor that get the serverconfig.xml file and gets the XML document.
     */
    public ServerConfigMgr() throws Exception {
        // Get the config file name
        String path = SystemProperties.get(SystemProperties.CONFIG_PATH);
        if (path == null) { // For Backward compatibility obtain from runtime
                            // flag
            path = System.getProperty(RUN_TIME_CONFIG_PATH);
        }
        configFile = path + System.getProperty("file.separator")
                + SystemProperties.CONFIG_FILE_NAME;
        // Debug messages
        if (debug.messageEnabled()) {
            debug.message("Server config file: " + configFile);
        }

        // Check if the user has read/write privileges on the file
        File file = new File(configFile);
        if (!file.exists() || !file.canRead() || !file.canWrite()) {
            if (debug.warningEnabled()) {
                debug.warning("User does not have read/write privileges "
                        + "for file: " + configFile);
            }
            String objs[] = { configFile };
            throw (new Exception(MessageFormat.format(i18n
                    .getString("dscfg-no-file-permission"), objs)));
        }

        // Read the file and get the XML document, root node
        // and default server group
        Exception exception = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            if ((document = XMLUtils.getXMLDocument(fis)) == null) {
                debug.error("Unable to read server config file: " + configFile
                        + " error in getting the document");
                throw (new XMLException(i18n
                        .getString("dscfg-error-reading-config-file")
                        + "\n" 
                        + i18n.getString("dscfg-corrupted-serverconfig")));
            }

            if ((root = XMLUtils.getRootNode(document, DSConfigMgr.ROOT)) 
                    == null) {
                debug.error("Unable to get root node: " + configFile
                        + " error in parsing the document");
                throw (new XMLException(i18n
                        .getString("dscfg-unable-to-find-root-node")
                        + "\n" + i18n.getString(
                                "dscfg-corrupted-serverconfig")));
            }

            if ((defaultServerGroup = XMLUtils.getNamedChildNode(root,
                    DSConfigMgr.SERVERGROUP, DSConfigMgr.NAME,
                    DSConfigMgr.DEFAULT)) == null) {
                debug.error("Misconfigured server config file: " + configFile
                        + " unable to get default server group");
                throw (new XMLException(i18n
                        .getString("dscfg-unable-to-find-default-servergroup")
                        + "\n" + i18n.getString(
                                "dscfg-corrupted-serverconfig")));
            }
        } catch (Exception e) {
            exception = e;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        // Check if an exception has occured and throw it
        if (exception != null) {
            throw (exception);
        }
    }

    // ----------------------------------------------------------------------
    // Main method
    // ----------------------------------------------------------------------
    public static void main(String args[]) {

        // Check the initial arguments
        if ((args.length == 0) || args[0].equals(HELP)
                || (args[0].equals(S_HELP)) || (args[0].equals(Q_HELP))
                || (args[0].equals(SQ_HELP))) {
            System.err.println(i18n.getString("dscfg-usage"));
            System.exit(1);
        } else if (!args[0].equals(ADMIN) && !args[0].equals(S_ADMIN)
                && !args[0].equals(PROXY) && !args[0].equals(S_PROXY)
                && !args[0].equals(ENCRYPT) && !args[0].equals(S_ENCRYPT)) {
            // Invalid subcommand
            String[] objs = { args[0] };
            System.err.println(MessageFormat.format(i18n
                    .getString("dscfg-invalid-option"), objs));
            System.err.println(i18n.getString("dscfg-usage"));
            System.exit(1);
        } else if ((args.length != 1) && (args.length != 2)
                && (args.length != 5)) {
            // Illegal number of arguments
            System.err.println(i18n.getString("dscfg-illegal-args"));
            System.err.println(i18n.getString("dscfg-usage"));
            System.exit(1);
        }

        // Encrypt the password and print it out
        if (args[0].equals(S_ENCRYPT) || args[0].equals(ENCRYPT)) {
            String password = null;
            if (args.length > 1) {
                password = args[1];
            } else {
                // prompt for the password
                System.out
                        .print(i18n.getString("dscfg-enter-encrypt-password"));
                password = readPassword();
                if ((password == null) || (password.length() == 0)) {
                    System.err.println(i18n.getString("dscfg-null-password"));
                    System.err.println(i18n.getString("dscfg-usage"));
                    System.exit(1);
                }
            }

            // output the encrypted password
            System.out.println((String) AccessController
                    .doPrivileged(new EncodeAction(password)));
            System.exit(0);
        }

        // Get the user type for which the password is being changed
        boolean adminPassword = false, proxyPassword = false;
        if (args[0].equals(S_ADMIN) || args[0].equals(ADMIN)) {
            adminPassword = true;
        } else {
            proxyPassword = true;
        }

        // Check if the passwords are present on the command line
        String oldPassword = null;
        String newPassword = null;
        // Parse the input arguments, if any
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals(OLD) || args[i].equals(S_OLD)) {
                oldPassword = args[++i];
            } else if (args[i].equals(NEW) || args[i].equals(S_NEW)) {
                newPassword = args[++i];
            } else {
                String[] objs = { args[i] };
                System.err.println(MessageFormat.format(i18n
                        .getString("dscfg-invalid-option"), objs));
                System.err.println(i18n.getString("dscfg-usage"));
                System.exit(1);
            }
        }

        // if passwords are null, prompt for them
        if ((oldPassword == null) || (newPassword == null)) {
            System.out.print(i18n.getString("dscfg-enter-old-password"));
            oldPassword = readPassword();

            // Get the password twice and check it
            String objs[] = { Integer.toString(MIN_PASSWORD_LEN) };
            System.out.print(MessageFormat.format(i18n
                    .getString("dscfg-enter-new-password"), objs));
            String newPassword1 = readPassword();
            System.out.print(i18n.getString("dscfg-enter-new-password-again"));
            String newPassword2 = readPassword();

            // Check the entered new passwords
            checkPassword(oldPassword, newPassword1);
            checkPassword(oldPassword, newPassword2);

            // Check if the entered new password are the same
            if (newPassword1.equals(newPassword2)) {
                newPassword = newPassword1;
            } else {
                System.err.println(i18n
                        .getString("dscfg-new-passwords-donot-match"));
                System.exit(1);
            }
        } else {
            checkPassword(oldPassword, newPassword);
        }

        // Execute the command
        try {
            ServerConfigMgr scm = new ServerConfigMgr();

            // Check if admin DN and proxy DN are the same
            DN adminDN = new DN(scm.getUserDN(DSConfigMgr.VAL_AUTH_ADMIN));
            DN proxyDN = new DN(scm.getUserDN(DSConfigMgr.VAL_AUTH_PROXY));
            if (adminDN.equals(proxyDN)) {
                // must change both of them
                adminPassword = true;
                proxyPassword = true;
            }

            // Change admin Password
            if (adminPassword) {
                if (debug.messageEnabled()) {
                    debug.message("Setting the admin password");
                }
                // Change the password
                scm.setAdminUserPassword(oldPassword, newPassword);
            }

            // Change admin Password
            if (proxyPassword) {
                if (debug.messageEnabled()) {
                    debug.message("Setting the proxy password");
                }
                // Change the password
                scm.setProxyUserPassword(oldPassword, newPassword);
            }

            // Commit the changes to file
            if (debug.messageEnabled()) {
                debug.message("Updating serverconfig.xml");
            }
            scm.save();
            System.out.println(i18n.getString("dscfg-passwd-success"));
        } catch (Exception e) {
            debug.error("Exception while changing password", e);
            System.err.println(e.getMessage());
        }
    }

    // ----------------------------------------------------------------------
    // Currently supported methods
    // ----------------------------------------------------------------------

    /**
     * Sets the admin user's password.
     */
    public void setAdminUserPassword(String oldPassword, String newPassword)
            throws Exception {
        changePassword(DSConfigMgr.VAL_AUTH_ADMIN, oldPassword, newPassword);
    }

    /**
     * Sets the proxy user's password.
     */
    protected void setProxyUserPassword(String oldPassword, String newPassword)
            throws Exception {
        changePassword(DSConfigMgr.VAL_AUTH_PROXY, oldPassword, newPassword);
    }

    /**
     * Stores the directory server configuration information to the file system.
     */
    public void save() throws Exception {
        // Read the server config until we get the root node
        String line = null;
        StringBuffer prefix = new StringBuffer(100);
        BufferedReader in = new BufferedReader(new FileReader(configFile));
        while ((line = in.readLine()) != null) {
            int index;
            if ((index = line.indexOf(DSConfigMgr.ROOT)) == -1) {
                // Root node not yet found
                prefix.append(line);
                prefix.append("\n");
            } else {
                // Found the root node
                if (--index > 0) {
                    prefix.append(line.substring(0, index));
                    prefix.append("\n");
                }
                break;
            }
        }
        in.close();

        // Debug messages
        if (debug.messageEnabled()) {
            debug.message("Prefix read from old serverconfig.xml: " + prefix);
        }

        // Write the server config file
        PrintWriter out = new PrintWriter(new FileOutputStream(configFile));
        // Debug messages
        if (debug.messageEnabled()) {
            debug.message("Prefix being added to serverconfig.xml: " + prefix);
        }
        out.print(prefix.toString());
        // Debug messages
        if (debug.messageEnabled()) {
            debug.message("Config info being added to serverconfig.xml: "
                    + SMSSchema.nodeToString(root));
        }
        out.println(SMSSchema.nodeToString(root));
        out.close();
    }

    // ----------------------------------------------------------------------
    // Methods for future implementation
    // ----------------------------------------------------------------------

    /**
     * Sets the admin user. The Admin user (DN) will be used by DSAME to perform
     * administrative operations, like searching for users, services, etc.
     */
    protected void setAdminUser(String adminDN) {
        // %%%
    }

    /**
     * Sets the proxy user. The proxy user (DN) must have proxy privileges for
     * the directory server. The proxy DN will be used by DSAME to perform
     * directory operations ob behalf of the users.
     */
    protected void setProxyDN(String adminDN) {
        // %%%
    }

    /**
     * Adds a directory server to the list of servers.
     */
    protected void setServer(String name, String hostname, int port, 
            String type) 
    {

    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Returns the user node given the user type
     */
    private Node getUserNode(String userType) throws Exception {
        Node userNode = XMLUtils.getNamedChildNode(defaultServerGroup,
                DSConfigMgr.USER, DSConfigMgr.AUTH_TYPE, userType);
        if (userNode == null) {
            debug.error("Unable to get user type: " + userType
                    + " node from file: " + configFile);
            throw (new XMLException(i18n
                    .getString("dscfg-corrupted-serverconfig")));
        }
        return (userNode);
    }

    /**
     * Returns the user DN for the given user type
     */
    private String getUserDN(String userType) throws Exception {
        Node dnNode = XMLUtils.getChildNode(getUserNode(userType),
                DSConfigMgr.AUTH_ID);
        if (dnNode == null) {
            debug.error("Unable to get user DN for type: " + userType
                    + " from file: " + configFile);
            throw (new XMLException(i18n
                    .getString("dscfg-corrupted-serverconfig")));
        }
        return (XMLUtils.getValueOfValueNode(dnNode));
    }

    /**
     * Checks and sets the password
     */
    private void changePassword(String userType, String oldPassword,
            String newPassword) throws Exception {
        // Get the User, Password & DN node
        Node passwdNode = null;
        if ((passwdNode = XMLUtils.getChildNode(getUserNode(userType),
                DSConfigMgr.AUTH_PASSWD)) == null) {
            debug.error("Unable to get Password for type: " + userType
                    + " from file: " + configFile);
            throw (new XMLException(i18n
                    .getString("dscfg-corrupted-serverconfig")));
        }

        // Get the information from the serverconfig.xml
        String fileEncPassword = XMLUtils.getValueOfValueNode(passwdNode);
        String userDN = getUserDN(userType);
        if ((fileEncPassword == null) || (fileEncPassword.length() == 0)
                || (userDN == null) || (userDN.length() == 0)) {
            debug.error("Null password or user DN for user type: " + userType
                    + " from file: " + configFile);
            throw (new XMLException(i18n
                    .getString("dscfg-corrupted-serverconfig")));
        }

        // Verify old password
        if (!oldPassword.equals(AccessController.doPrivileged(new DecodeAction(
                fileEncPassword)))) {
            throw (new Exception(i18n.getString(
                    "dscfg-old-passwd-donot-match")));
        }

        // Check with iDS and change if necessary
        try {
            // Try new password
            new AuthContext(new AuthPrincipal(userDN), newPassword
                    .toCharArray());
            // Password has already been changed
            if (debug.messageEnabled()) {
                debug.message("DN: " + userDN + " new password is already "
                        + "updated in the directory");
            }
        } catch (LoginException lee) {
            try {
                // Try with the old password
                AuthContext ac = new AuthContext(new AuthPrincipal(userDN),
                        oldPassword.toCharArray());
                if (debug.messageEnabled()) {
                    debug.message("For DN: " + userDN
                            + " old password matchs with directory");
                }
                // Get the user object
                PersistentObject user = UMSObject.getObject(ac.getSSOToken(),
                        new Guid(userDN));
                // set the password
                if (debug.messageEnabled()) {
                    debug.message("For DN: " + userDN
                            + " changing password in directory");
                }
                user.setAttribute(new Attr("userPassword", newPassword));
                user.save();
            } catch (LoginException le) {
                if (debug.warningEnabled()) {
                    debug
                            .warning("For DN: "
                                    + userDN
                                    + " new and old passwords donot match " +
                                            "with directory");
                }
                throw (new Exception(i18n.getString("dscfg-invalid-password")
                        + "\n" + le.getMessage()));
            }
        }

        // Encrypt the new password and store
        String encPassword = (String) AccessController
                .doPrivileged(new EncodeAction(newPassword));

        // Add it to the XML, the text nodes must exist
        // else obtaining the oldEncPassword would have failed
        if (debug.messageEnabled()) {
            debug.message("Updating the XML document with new password");
        }
        NodeList textNodes = passwdNode.getChildNodes();
        Node textNode = textNodes.item(0);
        textNode.setNodeValue(encPassword);
        // Delete the remaining text nodes
        for (int i = 1; i < textNodes.getLength(); i++) {
            passwdNode.removeChild(textNodes.item(i));
        }
    }

    /**
     * Checks the old and new passwords provided
     */
    private static void checkPassword(String oldPassword, String newPassword) {
        // Check if old password is null or empty, it is not allowed
        if ((oldPassword == null) || (oldPassword.length() == 0)) {
            System.err.println(i18n.getString("dscfg-null-old-password"));
            System.err.println(i18n.getString("dscfg-usage"));
            System.exit(1);
        }

        // Check if new password is null or empty, it is not allowed
        if ((newPassword == null) || (newPassword.length() == 0)) {
            System.err.println(i18n.getString("dscfg-null-new-password"));
            System.err.println(i18n.getString("dscfg-usage"));
            System.exit(1);
        }

        // Both passwords are present, check them
        if (newPassword.length() < MIN_PASSWORD_LEN) {
            String objs[] = { Integer.toString(MIN_PASSWORD_LEN) };
            System.err.println(MessageFormat.format(i18n
                    .getString("dscfg-password-lenght-not-met"), objs));
            System.exit(1);
        } else if (newPassword.equals(oldPassword)) {
            System.err.println(i18n.getString("dscfg-passwords-are-same"));
            System.exit(1);
        }
    }

    /**
     * Reads the user entered password
     */
    private static String readPassword() {
        // Try using the native method
        if (!libraryLoaded) {
            synchronized (loadLibrary) {
                if (!libraryLoaded) {
                    try {
                        System.loadLibrary(loadLibrary);
                        libraryLoaded = true;
                    } catch (UnsatisfiedLinkError e) {
                        debug.error("Error in loading library", e);
                    }
                }
            }
        }
        // If library was successfully loaded
        // read the password
        if (libraryLoaded) {
            try {
                String password = jniReadPassword();
                if (password != null) {
                    return (password);
                }
            } catch (UnsatisfiedLinkError e) {
                debug.error("Error in loading library", e);
            }
        }

        // If JNI did not work, use the Java
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        System.in));
                return (br.readLine());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Try using JNI to read password and not echoing it. Returns null if it
     * failed.
     */
    public static native String jniReadPassword();

    private static String loadLibrary = "amutils";

    private static boolean libraryLoaded = false;
}
