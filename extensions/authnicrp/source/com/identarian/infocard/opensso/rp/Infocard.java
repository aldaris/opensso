/*
 * Copyright (c) 2008, Patrick Petit Consulting, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names Patrick Petit, Patrick Petit Consulting,
 *       PatrickPetit.com, identarian.com nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.identarian.infocard.opensso.rp;

import com.identarian.infocard.opensso.db.InfocardStorage;
import com.identarian.infocard.opensso.db.InfocardStorageImpl;
import com.identarian.infocard.opensso.db.StoredCredentials;
import com.identarian.infocard.opensso.exception.InfocardException;
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
    private static final String EMAIL_ADDR_CLAIM = "emailaddress";
    private static final String GIVEN_NAME_CLAIM = "givenname";
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
    private static PrivateKey privateKey = null;
    private static Debug debug = null;
    private static String keyStorePath;
    private static String keyStorePasswd;
    private static String keyAlias;
    private static int minPasswordLength;
    private String validatedUserID = null;
    private java.security.Principal userPrincipal = null;
    private Map sharedState;
    private Map options;
    private Token token = null;
    private String ppid = null;
    private Map<String, String> claims = null;
    private String userID = null;
    private String userPasswd = null;
    private int previousScreen = 0;
    private String regEx = "";
    private Map<String, Set> attributes = new HashMap();
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

        if (debug == null) {
            debug = com.sun.identity.shared.debug.Debug.getInstance(amAuthInfocard);
            debug.message("Infocard Authentication Module initialization");
        }

        /*java.util.Locale locale = getLoginLocale();
        //bundle = amCache.getResBundle(amAuthInfocard, locale);
        if (debug.messageEnabled()) {
        debug.message("Infocard getting resource bundle for locale: " +
        locale);
        } */

        this.options = options;
        this.sharedState = sharedState;
        debugPrintMap(">>> sharedState", sharedState);
        debugPrintMap(">>> options", options);

        //Properties allProperties = SystemProperties.getAll();
        //allProperties.list(System.out);
        PropertiesManager modProperties = null;
        try {
            modProperties = new PropertiesManager(
                    "/WEB-INF/classes/Infocard.properties");
        } catch (Exception e) {
            // Should be able to throw an exception here!
            if (debug.errorEnabled()) {
                debug.error("Can't open Infocard properties", e);
            }
        }

        keyStorePath = SystemProperties.get("javax.net.ssl.keyStore");
        keyStorePasswd = modProperties.getProperty("keyStorePassword");
        keyAlias = modProperties.getProperty("keyAlias");
        minPasswordLength = Integer.getInteger(
                modProperties.getProperty("userPasswordLength"), 8);
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
                    // Will attempt to associate the user with the PPID
                    retval = bindCredentials(callbacks);

                } else if (action == 1) { // New User
                    retval = REGISTRATION_STATE;
                } else { // Cancel
                    retval = ISAuthConstants.LOGIN_IGNORE;
                }
                addInfocardClaimsToSession();
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
                    retval = ISAuthConstants.LOGIN_IGNORE;
                }
                addInfocardClaimsToSession();
                break;

            default: // Error states
                return previousScreen;
        }

        return retval;
    }

    // ISAuthConstants.LOGIN_START
    //if (callbacks != null && callbacks.length != 0) {
    //    action =
    //        ((ConfirmationCallback) callbacks[0]).getSelectedIndex();
    //    System.out.println(">>> LOGIN page button index: " + action);
    //}
    private int processInfocard(Callback[] callbacks)
            throws AuthLoginException {

        //System.out.println(">>> InfocardLoginModule: Entering processInfoCard");
        int retval = ISAuthConstants.LOGIN_SUCCEED;


        String encXmlToken = getXmlToken();

        if (encXmlToken != null) {

            //dumpXmlToken(encXmlToken);

            ppid = getPPID(encXmlToken);
            if (ppid == null) {
                throw new AuthLoginException("Manadatory claim 'PPID' undefined");
            }

            StoredCredentials cred = null;

            try {
                cred = getStoredCred(ppid);
            } catch (InfocardException ife) {
                throw new AuthLoginException("failed to search user's credentials", ife);
            }

            if (cred != null) {
                try {
                    storeUsernamePasswd(userID, userPasswd);
                    Callback[] idCallbacks = new Callback[2];
                    NameCallback nameCallback = new NameCallback("dummy");
                    nameCallback.setName(cred.getUserID());
                    idCallbacks[0] = nameCallback;
                    PasswordCallback passwordCallback = new PasswordCallback("dummy", false);
                    passwordCallback.setPassword(cred.getUserPasswd().toCharArray());
                    idCallbacks[1] = passwordCallback;

                    AMIdentityRepository idrepo = getAMIdentityRepository(
                            getRequestOrg());

                    boolean success = idrepo.authenticate(idCallbacks);

                    if (success) {
                        retval = ISAuthConstants.LOGIN_SUCCEED;
                        validatedUserID = cred.getUserID();
                    } else {
                        setFailureID(cred.getUserID());
                        throw new AuthLoginException("Authentication Failed");
                    }
                } catch (AuthLoginException e1) {
                    setFailureID(cred.getUserID());
                    throw new AuthLoginException("Internal authentication error" +
                            e1.getMessage());
                } catch (IdRepoException e2) {
                    setFailureID(cred.getUserID());
                    throw new AuthLoginException("Internal iDRepo error" + e2.getMessage());
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

                    persistCredentials(ppid, userID, userPasswd);

                } catch (InfocardException ife) {
                    throw new AuthLoginException("failed to store user's credentials", ife);
                }
            } else {
                throw new AuthLoginException("Authentication failed");
            }
        } else {
            throw new AuthLoginException("Null callbacks in binding state");
        }
        return ISAuthConstants.LOGIN_SUCCEED;
    }

    private int registerNewUser() throws AuthLoginException {

        if (debug.messageEnabled()) {
            debug.message("trying to register(create) a new user: " + userID);
        }

        try {
            if (userExists(userID)) {
                if (debug.messageEnabled()) {
                    debug.message("unable to register, user " +
                            userID + " already exists");
                }
                return USER_EXISTS_ERROR;
            }

            // set user status
            Set<String> vals = new HashSet();
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
            
            persistCredentials(ppid, userID, userPasswd);
        
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
            throws AuthLoginException {

        return getClaim(encXmlToken, PPID_CLAIM);
    }

    private String getGivenName(String encXmlToken)
            throws AuthLoginException {

        return getClaim(encXmlToken, GIVEN_NAME_CLAIM);
    }

    private String getEmailAddress(String encXmlToken)
            throws AuthLoginException {

        return getClaim(encXmlToken, EMAIL_ADDR_CLAIM);
    }

    private String getClaim(String encXmlToken, String cname)
            throws AuthLoginException {

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
            throws AuthLoginException {

        if (this.claims == null) {
            try {
                // encXmlToken should never be null here!
                assert (encXmlToken != null);
                Token var = getToken(encXmlToken);

                if (var.isSignatureValid() /* &&
                        var.isConditionsValid() &&
                        var.isCertificateValid()*/) {
                    this.claims = token.getClaims();
                } else {
                    throw new AuthLoginException("Received invalid token");
                }
            } catch (InfoCardProcessingException e) {
                throw new AuthLoginException("Error processing token", e);
            }
        }
        return this.claims;
    }

    /**
     * Get the private. Must correspond to the server's SSL cert private key
     */
    private static synchronized PrivateKey getPrivateKey()
            throws AuthLoginException {

        try {

            if (privateKey == null) {
                KeystoreUtil keystore = new KeystoreUtil(keyStorePath,
                        keyStorePasswd);
                privateKey = keystore.getPrivateKey(keyAlias, keyStorePasswd);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthLoginException("Error getting Private Key", e);
        }
        return privateKey;
    }

    private Token getToken(String encXmlToken)
            throws AuthLoginException {

        try {
            /*  if (token == null) { */
            PrivateKey key = getPrivateKey();
            token = new Token(encXmlToken, key);
        /*}*/
        } catch (InfoCardProcessingException e) {
            throw new AuthLoginException("Error getting token", e);
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

    private StoredCredentials getStoredCred(String ppid) throws InfocardException {

        StoredCredentials creds = null;
        InfocardStorage storage = new InfocardStorageImpl();

        creds = storage.findCredentials(ppid);

        return creds;
    }

    private void persistCredentials(String ppid, String uid, String passwd)
            throws InfocardException {

        InfocardStorage storage = new InfocardStorageImpl();
        StoredCredentials creds = new StoredCredentials(ppid, uid, passwd);

        storage.addCredentials(creds);
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

        } catch (AuthLoginException e1) {
            System.out.println(">>> error reading token" + e1.getMessage());
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
}
