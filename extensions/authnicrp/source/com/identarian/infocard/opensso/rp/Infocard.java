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
 * $Id: Infocard.java,v 1.4 2008-04-18 13:52:35 ppetitsm Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2008 Patrick Petit Consulting
 */
package com.identarian.infocard.opensso.rp;

import com.identarian.infocard.opensso.db.InfocardStorage;
import com.identarian.infocard.opensso.db.InfocardStorageImpl;
import com.identarian.infocard.opensso.db.StoredCredentials;
import com.identarian.infocard.opensso.exception.BrokenTokenException;
import com.identarian.infocard.opensso.exception.InfocardException;
import com.identarian.infocard.opensso.exception.InvalidCertException;
import com.identarian.infocard.opensso.exception.InvalidTokenException;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOException;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;
import com.sun.identity.authentication.spi.AMLoginModule;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.authentication.util.ISAuthConstants;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.shared.debug.Debug;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.callback.ChoiceCallback;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.servlet.http.HttpServletRequest;
import org.xmldap.exceptions.*;
import org.xmldap.rp.Token;
import org.xmldap.util.KeystoreUtil;

/**
 * Sample Login Module.
 */
public class Infocard extends AMLoginModule {

    public static final String amAuthInfocard = "amInfocard";
    private static final String PPID_CLAIM = "privatepersonalidentifier";
    private static final int BEGIN_STATE = ISAuthConstants.LOGIN_START;
    private static final int BINDING_STATE = 2;
    private static final int REGISTRATION_STATE = 3;
    private static final int NO_USERID_ERROR = 4;
    private static final int NO_PASSWD_ERROR = 5;
    private static final int NO_CONFIRM_ERROR = 6;
    private static final int INVALID_PASSWORD_ERROR = 7;
    private static final int PASSWORD_MISMATCH_ERROR = 8;
    private static final int USER_EXISTS_ERROR = 9;
    private static final int USER_PASSWD_SAME_ERROR = 10;
    private static final int MISSING_REQ_FIELD_ERROR = 11;
    private static final int PROFILE_ERROR = 12;
    private static final int CARDSPACE_TOKEN_ERROR = 13;
    private static final int CARDSPACE_CERT_ERROR = 14;
    private static final int NOCONFIG_ERROR = 15;
    private static PrivateKey privateKey = null;
    private static Debug debug = null;
    private static String keyStorePath;
    private static String keyStorePasswd;
    private static String keyAlias;
    private static int minPasswordLength;
    private static boolean initialized = false;
    private String validatedUserID = null;
    private java.security.Principal userPrincipal = null;
    private Map sharedState;
    private Map options;
    private Token token = null;
    private String ppid = null;
    private String digest = null;
    private Map<String, String> claims = null;
    private String userID = null;
    private String userPasswd = null;
    private int previousScreen = 0;
    private String regEx = "";
    private Map<String, Set> attributes = new HashMap<String, Set>();
    private Set defaultRoles = null;

    /**
     * Creates an instance of this class.
     *
     * @throws LoginException if class cannot be instantiated.
     */
    public Infocard() throws LoginException {

    }

    /**
     * Initializes the module.
     *
     * @param subject
     * @param sharedState
     * @param options
     */
    public void init(Subject subject, Map sharedState, Map options) {


        this.options = options;
        this.sharedState = sharedState;
        debugPrintMap(">>> sharedState", sharedState);
        debugPrintMap(">>> options", options);

        if (initialized) {
            return;
        }

        if (debug == null) {
            debug = com.sun.identity.shared.debug.Debug.getInstance(amAuthInfocard);
            debug.message("Infocard Authentication Module initializing");
        }

        /*java.util.Locale locale = getLoginLocale();
        //bundle = amCache.getResBundle(amAuthInfocard, locale);
        if (debug.messageEnabled()) {
        debug.message("Infocard getting resource bundle for locale: " +
        locale);
        } */

        //Properties allProperties = SystemProperties.getAll();
        //allProperties.list(System.out);
        PropertiesManager modProperties = null;
        try {
            modProperties = new PropertiesManager(
                    "/WEB-INF/classes/Infocard.properties");
        } catch (Exception e) {
            // Shouldn't we be able to throw an exception in init?
            debug.error("Configuration error: can't get Infocard properties", e);
            return;
        }

        keyStorePath = SystemProperties.get("javax.net.ssl.keyStore");
        keyStorePasswd = modProperties.getProperty(
                "com.identarian.infocard.opensso.keyStorePassword");
        keyAlias = modProperties.getProperty(
                "com.identarian.infocard.opensso.keyAlias");
        minPasswordLength = Integer.getInteger(
                modProperties.getProperty(
                "com.identarian.infocard.opensso.userPasswordLength"), 8);
        try {
            privateKey = getPrivateKey();
        } catch (InfocardException e) {
            debug.error("Configuration error: can't get server's private key", e);
            return;
        }
        initialized = true;
    }

    /**
     * Processes the callback requests.
     *
     * @param callbacks Array of callback object.
     * @param state
     * @return the status of the request.
     * @throws AuthLoginException if there are errors in processing the request.
     */
    public int process(Callback[] callbacks, int state)
            throws AuthLoginException {

        if (!initialized) {
            return NOCONFIG_ERROR;
        }

        int retval = ISAuthConstants.LOGIN_SUCCEED;
        int action;

        //System.out.println(">>> InfocardLoginModule: Entering process");

        switch (state) {

            case BEGIN_STATE:
                previousScreen = BEGIN_STATE;
                retval = processInfocard(callbacks);
                break;

            case BINDING_STATE:
                previousScreen = BINDING_STATE;
                if (callbacks != null && callbacks.length != 0) {
                    // Callbacke[2] is the confirmation callback
                    action = ((ConfirmationCallback) callbacks[2]).getSelectedIndex();
                } else {
                    throw new AuthLoginException("null callbacks in confirmation state");
                }

                if (action == 0) { // Confirm
                    // Will attempt to associate the account with the PPID
                    retval = bindCredentials(callbacks);
                } else if (action == 1) { // New User
                    retval = REGISTRATION_STATE;
                } else { // Cancel
                    clearCallbacks(callbacks);
                    retval = ISAuthConstants.LOGIN_IGNORE;
                }
                break;

            case REGISTRATION_STATE:
                // Registration will attempt to create a new user account.
                previousScreen = REGISTRATION_STATE;
                if (callbacks != null && callbacks.length != 0) {
                    // callbacks[3] is confirmation callback
                    action = ((ConfirmationCallback) callbacks[3]).getSelectedIndex();
                } else {
                    throw new AuthLoginException("null callbacks in registration state");
                }
                if (action == 0) { // Register button
                    retval = getAndCheckRegistrationFields(callbacks);
                    if (retval == 0) {
                        retval = registerNewUser();
                    }
                } else if (action == 1) { // Cancel
                    clearCallbacks(callbacks);
                    retval = ISAuthConstants.LOGIN_IGNORE;
                } else if (action == 2) { // Reset Form
                    retval = previousScreen;
                }
                break;

            default: // Error states
                return previousScreen;
        }

        if (retval == ISAuthConstants.LOGIN_SUCCEED) {
            addInfocardClaimsToSession();
        }
        return retval;
    }

    // ISAuthConstants.LOGIN_START
    //if (callbacks != null && callbacks.length != 0) {
    //    action =
    //        ((ConfirmationCallback) callbacks[0]).getSelectedIndex();
    //    System.out.println(">>> LOGIN page button key: " + action);
    //}
    private int processInfocard(Callback[] callbacks)
            throws AuthLoginException {

        //System.out.println(">>> InfocardLoginModule: Entering processInfoCard");
        int retval = ISAuthConstants.LOGIN_SUCCEED;

        String encXmlToken = getXmlToken();

        if (encXmlToken != null) {
            //dumpXmlToken(encXmlToken);
            StoredCredentials creds = null;
            try {
                ppid = getPPID(encXmlToken);
                if (ppid == null) {
                    throw new InvalidTokenException("Manadatory claim 'PPID' is missing");
                }

                digest = getDigest(encXmlToken);
                if (digest == null) {
                    throw new BrokenTokenException("Manadatory token signature is missing");
                }

                System.out.println("PPID = " + ppid + " digest = " + digest);
                
                creds = null;
                creds = getStoredCred(ppid);

            } catch (BrokenTokenException e1) {
                debug.error("Received broken token", e1);
                return CARDSPACE_TOKEN_ERROR;
            } catch (InvalidCertException e2) {
                debug.error("Received invalid certificate in token", e2);
                return CARDSPACE_CERT_ERROR;
            } catch (InvalidTokenException e3) {
                debug.error("Received invalid token", e3);
                return CARDSPACE_TOKEN_ERROR;
            } catch (InfocardException e4) {
                e4.printStackTrace();
                throw new AuthLoginException("Processing infocard Internal error", e4);
            }

            if (creds != null) {
                try {
                    userID = creds.getUserID();
                    userPasswd = creds.getUserPasswd();
                    storeUsernamePasswd(userID, userPasswd);
                    Callback[] idCallbacks = new Callback[2];
                    NameCallback nameCallback = new NameCallback("dummy");
                    nameCallback.setName(userID);
                    idCallbacks[0] = nameCallback;
                    PasswordCallback passwordCallback = new PasswordCallback("dummy", false);
                    passwordCallback.setPassword(userPasswd.toCharArray());
                    idCallbacks[1] = passwordCallback;

                    AMIdentityRepository idrepo = getAMIdentityRepository(
                            getRequestOrg());

                    boolean success = idrepo.authenticate(idCallbacks);

                    if (success) {
                        retval = ISAuthConstants.LOGIN_SUCCEED;
                        validatedUserID = userID;
                    } else {
                        setFailureID(userID);
                        // credential now longer associated with user account
                        // remove that entry
                        debug.message("Delete orphan stored credentials for userID" + userID);
                        deleteCredentials(creds);
                        retval = BINDING_STATE;
                    }
                } catch (AuthLoginException e1) {
                    setFailureID(userID);
                    throw new AuthLoginException("Internal Authentication Error" +
                            e1.getMessage());
                } catch (IdRepoException e2) {
                    setFailureID(userID);
                    throw new AuthLoginException("Internal iDRepo Authentication Error" + e2.getMessage());
                } catch (InfocardException e3) {
                    e3.printStackTrace();
                    throw new AuthLoginException("Processing infocard Internal error", e3);
                }
            } else {
                // Goto BINDING_STATE to associate PPID with user account
                // Substitute the prompt with user name first
                /*
                Callback[] callbacks2 = getCallback(BINDING_STATE);
                String msg = ((NameCallback) callbacks2[0]).getPrompt();
                int i = msg.indexOf("#REPLACE#");
                String newMsg = msg.substring(0, i) + userName + msg.substring(i + 9);
                replaceCallback(2, 0, new NameCallback(newMsg));
                 */
                retval = BINDING_STATE;
            }
        } else {
            throw new AuthLoginException("Invalid null xmlToken");
        }

        return retval;
    }

    private int bindCredentials(Callback[] callbacks)
            throws AuthLoginException {

        boolean success;

        if (callbacks != null && callbacks.length != 0) {
            userID = ((NameCallback) callbacks[0]).getName();
            userPasswd = String.valueOf(((PasswordCallback) callbacks[1]).getPassword());

            if (userID == null || userID.length() == 0) {
                return NO_USERID_ERROR;
            }

            if (userPasswd == null || userPasswd.length() == 0) {
                return NO_PASSWD_ERROR;
            }

            storeUsernamePasswd(userID, userPasswd);

            try {
                //Now get the userid and password data from idRepo
                AMIdentityRepository idrepo = getAMIdentityRepository(getRequestOrg());
                success = idrepo.authenticate(callbacks);
            } catch (IdRepoException e2) {
                setFailureID(userID);
                throw new AuthLoginException("Internal iDRepo error" + e2.getMessage());
            }
            if (success) {
                validatedUserID = userID;
                // store ppid along with user's credentials
                try {
                    persistCredentials(ppid, digest, userID, userPasswd);
                } catch (InfocardException ife) {
                    throw new AuthLoginException("Failed to store user's credentials", ife);
                }
            } else {
                setFailureID(userID);
                throw new AuthLoginException("Authentication Failed");
            }
        } else {
            throw new AuthLoginException("Null callbacks in binding state");
        }
        return ISAuthConstants.LOGIN_SUCCEED;
    }

    private int registerNewUser() throws AuthLoginException {

        try {
            if (userExists(userID)) {
                return USER_EXISTS_ERROR;
            }

            // set user status
            Set<String> vals = new HashSet<String>();
            vals.add("Active");
            attributes.put("inetuserstatus", vals);

            // Todo:
            // Should add all idRepo mappable claims
            // Define defaultRoles. For now it's null
            createIdentity(userID, attributes, defaultRoles);

        } catch (SSOException pe) {
            debug.error("profile exception occured: ", pe);
            return PROFILE_ERROR;

        } catch (IdRepoException pe) {
            debug.error("profile exception occured: ", pe);
            return PROFILE_ERROR;
        }

        // store ppid along with user's credentials 
        try {
            persistCredentials(ppid, digest, userID, userPasswd);
        } catch (InfocardException ife) {
            throw new AuthLoginException("failed to store user's credentials", ife);
        }

        validatedUserID = userID;

        if (debug.messageEnabled()) {
            debug.message("registration is completed, created user: " +
                    validatedUserID);
        }

        return ISAuthConstants.LOGIN_SUCCEED;
    }

    public Principal getPrincipal() {

        if (userPrincipal != null) {
            return userPrincipal;
        } else if (validatedUserID != null) {
            userPrincipal = new InfocardPrincipal(validatedUserID);
            return userPrincipal;
        } else {
            return null;
        }
    }

    private String getXmlToken()
            throws AuthLoginException {

        String encXmlToken = null;

        HttpServletRequest request = getHttpServletRequest();

        if (request == null) {
            throw new AuthLoginException("HttpServeltRequest is null");
        }

        encXmlToken =
                request.getParameter("xmlToken");


        if (encXmlToken == null ||
                (encXmlToken != null && encXmlToken.equals(""))) {
            throw new AuthLoginException("Encrypter XML Token is null or undefined");
        }

//System.out.println(">>> encXmlToken = " + encXmlToken);
        return encXmlToken;
    }

    private String getPPID(String encXmlToken)
            throws BrokenTokenException, InvalidCertException,
            InvalidTokenException, InfocardException {

        return getClaim(encXmlToken, PPID_CLAIM);
    }

    private String getClaim(String encXmlToken, String cname)
            throws BrokenTokenException, InvalidCertException,
            InvalidTokenException, InfocardException {

        Map var = getClaims(encXmlToken);
        Set keys = var.keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            if (cname.equals(name)) {
                return (String) claims.get(name);
            }
        }
        return null;
    }

    private Map getClaims(String encXmlToken)
            throws BrokenTokenException, InvalidCertException,
            InvalidTokenException, InfocardException {

        if (this.claims == null) {
            try {
                // encXmlToken should never be null here!
                assert (encXmlToken != null);
                Token var = getToken(encXmlToken);
                if (var.isSignatureValid() && var.isConditionsValid()) {
                    X509Certificate cert = token.getCertificateOrNull();
                    if (cert != null && !token.isCertificateValid()) {
                        throw new InvalidCertException("Received invalid certificate");
                    }
                    this.claims = token.getClaims();
                } else {
                    throw new InvalidTokenException("Received invalid token");
                }
            } catch (InfoCardProcessingException e) {
                throw new BrokenTokenException("Received broken token", e);
            }
        }
        return this.claims;
    }

    // Get token's public key
    private String getDigest(String encXmlToken)
            throws BrokenTokenException, InvalidCertException,
            InvalidTokenException, InfocardException {

        String digest = null;
        try {
            // encXmlToken should never be null here!
            assert (encXmlToken != null);
            Token var = getToken(encXmlToken);

            if (var.isSignatureValid() && var.isConditionsValid()) {
                X509Certificate cert = token.getCertificateOrNull();
                if (cert != null && !token.isCertificateValid()) {
                    throw new InvalidCertException("Received invalid certificate in token");
                }
                digest = var.getClientDigest();
            } else {
                throw new InvalidTokenException("Received invalid token");
            }
        } catch (InfoCardProcessingException e) {
            throw new InfocardException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InfocardException(e);
        }
        return digest;
    }

    /**
     * Get the private. Must correspond to the server's SSL cert private key
     */
    private static synchronized PrivateKey getPrivateKey()
            throws InfocardException {

        PrivateKey key = null;
        try {

            if (privateKey == null) {
                KeystoreUtil keystore = new KeystoreUtil(keyStorePath,
                        keyStorePasswd);
                key = keystore.getPrivateKey(keyAlias, keyStorePasswd);
            } else {
                return privateKey;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InfocardException("Error getting Private Key", e);
        }
        return key;
    }

    private Token getToken(String encXmlToken)
            throws InfocardException {

        try {
            token = new Token(encXmlToken, this.privateKey);
        /*}*/
        } catch (InfoCardProcessingException e) {
            throw new InfocardException("Error getting token", e);
        }
        return token;
    }

    private void debugPrintMap(String name, Map map) {
        Set keys = map.keySet();
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            String key = (String) iter.next();
        //System.out.println(">>> " + name + "(" + key + ")=" + map.get(key));
        }
    }

    private StoredCredentials getStoredCred(String ppid)
            throws InfocardException, InvalidTokenException {

        StoredCredentials creds = null;
        InfocardStorage storage = new InfocardStorageImpl();

        creds = storage.findCredentials(ppid);

        if (creds != null && !digest.equals(creds.getSignature())) {
            // Forgery ?
            debug.message(
                    "Token's signature and stored signature do not match for ppid =" + ppid);
            throw new InvalidTokenException(
                    "Token's signature and stored signature do not match for ppid =" + ppid);
        }
        return creds;
    }

    private void persistCredentials(String ppid, String signature, String uid,
            String passwd)
            throws InfocardException {

        InfocardStorage storage = new InfocardStorageImpl();
        StoredCredentials creds = new StoredCredentials(ppid, signature, uid, passwd);

        storage.addCredentials(creds);
    }

    private void deleteCredentials(StoredCredentials creds)
            throws InfocardException {

        InfocardStorage storage = new InfocardStorageImpl();

        storage.delCredentials(creds);
    }

    private int getAndCheckRegistrationFields(Callback[] callbacks)
            throws AuthLoginException {

        // callback[0] is for user name
        // callback[1] is for new password
        // callback[2] is for confirm password

        attributes = new HashMap();
        // get the value of the user name from the input form
        String uid = getCallbackFieldValue(callbacks[0]);
        // check user name
        if ((uid == null) || uid.length() == 0) {
            // no user name was entered, this is required to
            // create the user's profile
            return NO_USERID_ERROR;
        }

        //validate username using plugin if any
        validateUserName(uid, regEx);

        // get the passwords from the input form
        String passwd = getPassword((PasswordCallback) callbacks[1]);
        String confirmPassword = getPassword((PasswordCallback) callbacks[2]);
        // check passwords
        int status = checkPassword(uid, passwd, confirmPassword);

        if (debug.messageEnabled()) {
            debug.message("state returned from checkPassword(): " +
                    status);
        }

        // Return if any error
        if (status != 0) {
            return status;
        }

        // validate password using validation plugin if any
        validatePassword(passwd);

        // get required registration attributes
        for (int i = 0; i < callbacks.length; i++) {

            String attrName = getAttribute(REGISTRATION_STATE, i);

            if (attrName != null &&
                    attrName.length() != 0) {

                Set values = getCallbackFieldValues(callbacks[i]);
                if (isRequired(REGISTRATION_STATE, i)) {
                    if (values.isEmpty()) {
                        if (debug.messageEnabled()) {
                            debug.message("Empty value for required field :" +
                                    attrName);
                        }
                        return MISSING_REQ_FIELD_ERROR;
                    }
                }
                attributes.put(attrName, values);
            }
        }

        userID = uid;
        userPasswd = passwd;
        return 0;
    }

    /**
     * Returns the first input value for the given Callback.
     * Returns null if there is no value for the Callback.
     */
    private String getCallbackFieldValue(
            Callback callback) {

        Set values = getCallbackFieldValues(callback);
        Iterator it = values.iterator();
        if (it.hasNext()) {
            return ((String) it.next());
        }

        return null;
    }

    private Set getCallbackFieldValues(Callback callback) {
        Set values = new HashSet();

        if (callback instanceof NameCallback) {
            String value = ((NameCallback) callback).getName();
            if (value != null && value.length() != 0) {
                values.add(value);
            }

        } else if (callback instanceof PasswordCallback) {
            String value = getPassword((PasswordCallback) callback);
            if (value != null && value.length() != 0) {
                values.add(value);
            }

        } else if (callback instanceof ChoiceCallback) {
            String[] vals = ((ChoiceCallback) callback).getChoices();
            int[] selectedIndexes =
                    ((ChoiceCallback) callback).getSelectedIndexes();
            for (int i = 0; i <
                    selectedIndexes.length; i++) {
                values.add(vals[selectedIndexes[i]]);
            }

        }
        return values;
    }

    private String getPassword(PasswordCallback callback) {
        char[] tmpPassword = callback.getPassword();
        if (tmpPassword == null) {
            // treat a NULL password as an empty password
            tmpPassword = new char[0];
        }

        char[] pwd = new char[tmpPassword.length];
        System.arraycopy(tmpPassword, 0, pwd, 0, tmpPassword.length);

        return (new String(pwd));
    }

    private int checkPassword(String uid, String password, String confirmPassword) {

        if ((password == null) || password.length() == 0) {
            // missing the password field
            debug.error("password was missing from the form");
            return NO_PASSWD_ERROR;

        } else {
            // compare the length of the user entered password with
            // the length required
            if (password.length() < minPasswordLength) {
                debug.error("password was not long enough");
                return INVALID_PASSWORD_ERROR;
            }

// length OK, now make sure the user entered a confirmation
// password
            if ((confirmPassword == null) || confirmPassword.length() == 0) {
                // no confirm password field entered
                debug.error("no confirmation password");
                return NO_CONFIRM_ERROR;
            } else {
                // does the confirmation password match the actual password
                if (!password.equals(confirmPassword)) {
                    // the password and the confirmation password don't match
                    return PASSWORD_MISMATCH_ERROR;
                }

            }

            // the user name and password are the same. these fields
            // must be different
            if (password.equals(uid)) {
                return USER_PASSWD_SAME_ERROR;
            }

        }
        return 0;
    }

    private boolean userExists(String userID)
            throws IdRepoException, SSOException {
        AMIdentityRepository amIdRepo = getAMIdentityRepository(
                getRequestOrg());

        IdSearchControl idsc = new IdSearchControl();
        idsc.setRecursive(true);
        idsc.setTimeOut(0);
        idsc.setAllReturnAttributes(true);
        // search for the identity
        Set results = Collections.EMPTY_SET;
        try {
            idsc.setMaxResults(0);
            IdSearchResults searchResults =
                    amIdRepo.searchIdentities(IdType.USER, userID, idsc);
            if (searchResults != null) {
                results = searchResults.getSearchResults();
            }

        } catch (IdRepoException e) {
            if (debug.messageEnabled()) {
                debug.message("IdRepoException : Error searching " + " Identities with username : " + e.getMessage());
            }

        }

        return !results.isEmpty();
    }

    private void dumpXmlToken(String encXmlToken) {

        try {
            Map var = getClaims(encXmlToken);

            System.out.println(">>> You provided the following claims:\n");
            Set keys = var.keySet();
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                String name = (String) iter.next();
                String value = (String) var.get(name);
                System.out.println(">>> \t" + name + ":" + value + "\n");
            }

        } catch (Exception e1) {
            System.out.println("Error getting token" + e1.getMessage());
        }
    }

    private void addInfocardClaimsToSession() {

        // claims cannot be null here!
        assert (claims != null);
        Set<String> keys = claims.keySet();

        try {
            Iterator itr = keys.iterator();
            while (itr.hasNext()) {
                String claim = (String) itr.next();
                setUserSessionProperty(claim, (String) claims.get(claim));
            }
        } catch (AuthLoginException e) {
            debug.message("Error setting session's attributes", e);
        }
    }

    /**
     * When user click cancel button, these input field should be reset
     * to blank.
     */
    private void clearCallbacks(Callback[] callbacks) {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                NameCallback nc = (NameCallback) callbacks[i];
                nc.setName("");
            }
        }
    }
}
