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
 * $Id: Infocard.java,v 1.7 2009-07-08 08:59:28 ppetitsm Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2008 Patrick Petit Consulting
 */
package com.identarian.infocard.opensso.rp;

import com.identarian.infocard.opensso.exception.InfocardException;
import com.identarian.infocard.opensso.exception.InvalidCertException;
import com.identarian.infocard.opensso.exception.InvalidTokenConditionException;
import com.identarian.infocard.opensso.exception.InvalidTokenSignatureException;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOException;
import com.sun.identity.authentication.service.AuthD;
import com.sun.identity.authentication.service.AuthException;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;
import com.sun.identity.authentication.spi.AMLoginModule;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.authentication.spi.InvalidPasswordException;
import com.sun.identity.authentication.util.ISAuthConstants;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.shared.datastruct.CollectionHelper;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceConfig;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
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

    public static final String amAuthInfocard = "amAuthInfocard";
    public static final String PPID_CLAIM = "privatepersonalidentifier";
    public static final String USERNAME_CLAIM = "username";
    public static final String PASSWORD_CLAIM = "password";
    public static final String GIVENNAME_CLAIM = "givenname";
    public static final String SURNAME_CLAIM = "surname";
    public static final String REQUIRED_CLAIMS = "";
    public static final String DEFAULT_REQUIRED_CLAIMS =
            "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier";
    public static final String DEFAULT_OPTIONAL_CLAIMS = "";
    private static final String DEFAULT_TOKEN_TYPE = "urn:oasis:names:tc:SAML:1.0:assertion";
    // Process states
    private static final int DEFAULT_AUTH_LEVEL = 0;
    private static final int BEGIN_STATE = ISAuthConstants.LOGIN_START;
    private static final int BINDING_STATE = 2;
    private static final int REGISTRATION_STATE = 3;
    private static final int CHOOSE_USERNAME = 4;
    private static final int NO_USERID_ERROR = 5;
    private static final int NO_PASSWD_ERROR = 6;
    private static final int NO_CONFIRM_ERROR = 7;
    private static final int INVALID_PASSWORD_ERROR = 8;
    private static final int PASSWORD_MISMATCH_ERROR = 9;
    private static final int USER_EXISTS_ERROR = 10;
    private static final int USER_PASSWD_SAME_ERROR = 11;
    private static final int MISSING_REQ_FIELD_ERROR = 12;
    private static final int PROFILE_ERROR = 13;
    private static final int USER_PASSWORD_SAME_ERROR = 14;
    private static final int INFOCARD_INVALID_TOKEN_ERROR = 15;
    private static final int INFOCARD_INVALID_CERT_ERROR = 16;
    private static final int INFOCARD_INVALID_SIGNATURE_ERROR = 17;
    private static final int INFOCARD_TOKEN_PROCESSING_ERROR = 18;
    private static final int INFOCARD_NOCONFIG_ERROR = 19;

    // Global variables
    private static Debug debug = null;
    protected ResourceBundle bundle = null;
    protected static String authType = null;
    private static PrivateKey privateKey;
    private static String keyStorePath;
    private static String keyStorePasswd;
    private static String keyAlias;
    private static AuthD authd = AuthD.getAuth();
    private static final int minPasswordLength = 8;
    private String validatedUserID = null;
    private java.security.Principal userPrincipal = null;
    private Map sharedState = null;
    private Map options = null;
    private ServiceConfig serviceConfig = null;
    private int requiredPasswordLength = 0;
    private Token xmlToken = null;
    private String ppid = null;
    private String digest = null;
    private String serviceStatus = null;
    private String userID = null;
    private String myOwnID = null;
    private String userPasswd = null;
    private String defaultAnonUser = null;
    private String confirmationMethod = null;
    private String verifiedClaims = null;
    private Date beginValidityPeriod = null;
    private Date endValidityPeriod = null;
    private String audienceRestriction = null;
    private String audience = null;
    private String regEx = null;
    private Map<String, String> userClaimValues = null;
    private Map<String, Set> userAttributeValues = null;
    private Map<String, String> claimToAttributeMap = null;
    private Set defaultRoles = null;
    private String errorMsg = null;
    private boolean ignoreUserProfile = false;
    private boolean isConditionValid = false;
    private boolean checkAudienceRestriction = false;
    private boolean checkAge18OrOver = false;
    private boolean checkCoppaCertified = false;
    private boolean checkAge21OrOver = false;
    private boolean checkConfirmationMethod = false;
    private boolean checkVerifiedClaims = false;
    private boolean checkValidityPeriod = false;

    /**
     * Creates an instance of this class.
     *
     * @throws LoginException if class cannot be instantiated.
     */
    public Infocard() throws LoginException {
        debug = Debug.getInstance(amAuthInfocard);
    }

    /**
     * Initializes the module.
     *
     * @param subject
     * @param sharedState
     * @param options
     */
    @Override
    public void init(Subject subject, Map sharedState, Map options) {

        if (options == null || options.isEmpty()) {
            debug.error("options is null or empty");
            return;
        } else {
            this.options = options;
        }

        if (privateKey == null) {
            keyStorePath = SystemProperties.get("javax.net.ssl.keyStore");
            keyStorePasswd = CollectionHelper.getMapAttr(options, "iplanet-am-auth-infocard-keyStorePassword");
            keyAlias = CollectionHelper.getMapAttr(options, "iplanet-am-auth-infocard-keyStoreAlias");
            try {
                privateKey = getPrivateKey();
            } catch (InfocardException e) {
                debug.error("Configuration error: check the module's Keystore parameters", e);
                return;
            }
        }

        // serviceConfig = (ServiceConfig) options.get("ServiceConfig");
        this.sharedState = sharedState;

        java.util.Locale locale = getLoginLocale();
        bundle = amCache.getResBundle(amAuthInfocard, locale);
        if (debug.messageEnabled()) {
            debug.message("DataStore resbundle locale=" + locale);
        }

        authType = bundle.getString("iplanet-am-auth-infocard-service-description");

        if (debug.messageEnabled()) {
            String ModuleName = (String) options.get(ISAuthConstants.MODULE_INSTANCE_NAME);
            if (options != null) {
                debugPrintMap(">>> " + ModuleName + " options", options);
            }
            if (serviceConfig != null) {
                debugPrintMap(">>> " + ModuleName + " service config", serviceConfig.getAttributes());
            }
            if (sharedState != null) {
                debugPrintMap(">>> " + ModuleName + " Shared State", sharedState);
            }
        }

        int authLevel = CollectionHelper.getIntMapAttr(options,
                "iplanet-am-auth-infocard-auth-level", DEFAULT_AUTH_LEVEL, debug);
        try {
            setAuthLevel(authLevel);
        } catch (Exception e) {
            debug.error("Unable to set auth level " + authLevel, e);
        }

        /*
         * Define the information card parameters as request parameters.
         */
        HttpServletRequest request = getHttpServletRequest();
        if (request != null && options != null) {

            String requiredClaims = getMapAttrValues(options,
                    "iplanet-am-auth-infocard-requiredClaims", DEFAULT_REQUIRED_CLAIMS);
            request.setAttribute("requiredClaims", requiredClaims);

            String optionalClaims = getMapAttrValues(options,
                    "iplanet-am-auth-infocard-optionalClaims", DEFAULT_OPTIONAL_CLAIMS);
            request.setAttribute("optionalClaims", optionalClaims);

            String tokenType = CollectionHelper.getMapAttr(options,
                    "iplanet-am-auth-infocard-tokenType", DEFAULT_TOKEN_TYPE);
            request.setAttribute("tokenType", tokenType);

            String privacyUrl = CollectionHelper.getMapAttr(options,
                    "iplanet-am-auth-infocard-privacyUrl");
            if (privacyUrl != null && privacyUrl.length() > 0) {
                request.setAttribute("privacyUrl", privacyUrl);
            }

            String privacyVersion = CollectionHelper.getMapAttr(options,
                    "iplanet-am-auth-infocard-privacyVersion");
            if (privacyVersion != null && privacyVersion.length() > 0) {
                request.setAttribute("privacyVersion", privacyVersion);
            }

            String issuer = CollectionHelper.getMapAttr(options,
                    "iplanet-am-auth-infocard-issuer");
            if (issuer != null && issuer.length() > 0) {
                request.setAttribute("issuer", issuer);
            }

            String issuerPolicy = CollectionHelper.getMapAttr(options,
                    "iplanet-am-auth-infocard-issuerPolicy");
            if (issuerPolicy != null && issuerPolicy.length() > 0) {
                request.setAttribute("issuerPolicy", issuer);
            }
        }
    }

    private void initAuthInfocard() throws InfocardException {

        serviceStatus = CollectionHelper.getMapAttr(
                options, "iplanet-am-auth-infocard-default-user-status", "Active");

        defaultRoles = (Set) options.get("iplanet-am-auth-infocard-default-roles");
        if (debug.messageEnabled()) {
            debug.message("defaultRoles is : " + defaultRoles);
        }

        regEx = CollectionHelper.getMapAttr(options, "iplanet-am-auth-infocard-invalid-chars");

        String var = CollectionHelper.getMapAttr(options,
                "iplanet-am-auth-infocard-min-password-length", "8");

        if (var != null && var.length() != 0) {
            requiredPasswordLength = Integer.parseInt(var);
        }

        defaultAnonUser = CollectionHelper.getMapAttr(
                options, "iplanet-am-auth-infocard-default-user-name", "Anonymous");

        // Get Information Card validation parameters
        audienceRestriction = CollectionHelper.getMapAttr(
                options, "iplanet-am-auth-infocard-audience-url", null);
        if (audienceRestriction != null && audienceRestriction.length() > 0) {
            checkAudienceRestriction = true;
        }
        checkAge18OrOver = Boolean.valueOf(CollectionHelper.getMapAttr(
                options, "iplanet-am-auth-infocard-age-18-or-over", "false")).booleanValue();
        checkCoppaCertified = Boolean.valueOf(CollectionHelper.getMapAttr(
                options, "iplanet-am-auth-infocard-coppa-certified", "false")).booleanValue();
        checkAge21OrOver = Boolean.valueOf(CollectionHelper.getMapAttr(
                options, "iplanet-am-auth-infocard-age-21-or-over", "false")).booleanValue();
        confirmationMethod = CollectionHelper.getMapAttr(
                options, "iplanet-am-auth-infocard-confirmation-method", null);
        if (confirmationMethod != null && confirmationMethod.length() > 0) {
            checkConfirmationMethod = true;
        }
        verifiedClaims = CollectionHelper.getMapAttr(
                options, "iplanet-am-auth-infocard-verified-claims", null);
        if (verifiedClaims != null && verifiedClaims.length() > 0) {
            checkVerifiedClaims = true;
        }
        checkValidityPeriod = Boolean.valueOf(CollectionHelper.getMapAttr(
                options, "iplanet-am-auth-infocard-validity-period", "true")).booleanValue();

        // Get auth service to determine authentication profile
        OrganizationConfigManager orgConfigMgr = authd.getOrgConfigManager(getRequestOrg());
        ServiceConfig svcConfig;
        try {
            svcConfig = orgConfigMgr.getServiceConfig(ISAuthConstants.AUTH_SERVICE_NAME);
            Map params = svcConfig.getAttributes();
            var = CollectionHelper.getMapAttr(params, ISAuthConstants.DYNAMIC_PROFILE);
            if (var.equalsIgnoreCase("ignore")) {
                ignoreUserProfile = true;
            }
        } catch (SMSException ex) {
            throw new InfocardException("Failed to get Auth Service config", ex);
        }

        /*
         * Compute user's attributes creation map
         */
        claimToAttributeMap = new HashMap<String, String>();
        userAttributeValues = new HashMap<String, Set>();
        setClaimToAttributeMap(options);
    }

    /**
     * Processes the callback requests.
     *
     * @param callbacks Array of callback object.
     * @param state
     * @return the status of the request.
     * @throws AuthLoginException if there are errors in processing the request.
     */
    @Override
    public int process(Callback[] callbacks, int state)
            throws AuthLoginException {

        int action;
        int retval = ISAuthConstants.LOGIN_IGNORE;

        if (options == null || privateKey == null) {
            debug.error("Internal configuration error");
            return INFOCARD_NOCONFIG_ERROR;
        }

        switch (state) {

            case BEGIN_STATE:

                try {
                    xmlToken = getToken();
                    if (xmlToken != null) {
                        // An information card is presented
                        initAuthInfocard();
                        retval = LoginWithInfocard();
                    } else {
                        // No infocard is presented: Regular login
                        retval = LoginWithUserNamePasswd(callbacks);
                    }
                } catch (InfoCardProcessingException ex) {
                    debug.error("Information Card processing error:", ex);
                    retval = INFOCARD_TOKEN_PROCESSING_ERROR;
                } catch (InvalidCertException ex) {
                    debug.error("Information Card certificate error:", ex);
                    retval = INFOCARD_INVALID_CERT_ERROR;
                } catch (InvalidTokenConditionException ex) {
                    debug.error("Information Card validity error:", ex);
                    retval = INFOCARD_INVALID_TOKEN_ERROR;
                } catch (InvalidTokenSignatureException ex) {
                    debug.error("Information Card signature error:", ex);
                    retval = INFOCARD_INVALID_SIGNATURE_ERROR;
                } catch (InfocardException e) {
                    debug.error("Internal configuration error:", e);
                    retval = INFOCARD_NOCONFIG_ERROR;
                }
                break;

            case BINDING_STATE:

                if (callbacks != null && callbacks.length != 0) {
                    // Callbacks[2] is the confirmation/cancelation callback
                    action = ((ConfirmationCallback) callbacks[2]).getSelectedIndex();
                } else {
                    throw new AuthLoginException(amAuthInfocard, "authFailed", null);
                }
                if (action == 0) {
                    // Confirm Information Card binding
                    retval = registerInfocardWithExistingUserEntry(callbacks);
                } else if (action == 1) {
                    retval = REGISTRATION_STATE;
                } else {
                    // Cancel
                    retval = ISAuthConstants.LOGIN_IGNORE;
                }
                break;

            case REGISTRATION_STATE:
                // Registration will attempt to create a new user account.

                if (callbacks != null && callbacks.length != 0) {
                    // callbacks[3] is confirmation callback
                    action = ((ConfirmationCallback) callbacks[3]).getSelectedIndex();
                } else {
                    throw new AuthLoginException(amAuthInfocard, "authFailed", null);
                }
                if (action == 0) { // Register button
                    retval = registerInfocardWithNewUserEntry(callbacks);
                    if (retval == ISAuthConstants.LOGIN_SUCCEED) {
                        retval = registerNewUser();
                    }
                } else if (action == 1) { // Cancel
                    clearCallbacks(callbacks);
                    retval = ISAuthConstants.LOGIN_IGNORE;
                } else if (action == 2) { // Reset Form
                    retval = REGISTRATION_STATE;
                }
                break;

            case CHOOSE_USERNAME:
                // user name entered already exists, generate
                // a set of user names for user to choose

                // callbacks[0] is the choice of the user ID
                String userChoiceID = getCallbackFieldValue(callbacks[0]);

                if (userChoiceID.equals(myOwnID)) {
                    retval = REGISTRATION_STATE;

                } else {
                    userID = userChoiceID;
                    Set<String> values = new HashSet<String>();
                    values.add(userID);
                    userAttributeValues.put("uid", values);
                    retval = registerNewUser();
                }

                break;
        }
        if (xmlToken != null && retval == ISAuthConstants.LOGIN_SUCCEED) {
            addInfocardClaimsToSession();
        }
        return retval;
    }

    @Override
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

    @Override
    public void destroyModuleState() {
        validatedUserID = null;
        userPrincipal = null;
    }

    @Override
    public void nullifyUsedVars() {
        bundle = null;
        sharedState = null;
        errorMsg = null;
        userID = null;
        myOwnID = null;
        serviceStatus = null;
        userPasswd = null;
        defaultAnonUser = null;
        serviceConfig = null;
        xmlToken = null;
        ppid = null;
        digest = null;
        userClaimValues = null;
        confirmationMethod = null;
        verifiedClaims = null;
        beginValidityPeriod = null;
        endValidityPeriod = null;
        audienceRestriction = null;
        audience = null;
        regEx = null;
        requiredPasswordLength = 0;
        options = null;
        userAttributeValues = null;
        claimToAttributeMap = null;
        defaultRoles = null;
        errorMsg = null;
        ignoreUserProfile = false;
        isConditionValid = false;
        checkAudienceRestriction = false;
        checkAge18OrOver = false;
        checkCoppaCertified = false;
        checkAge21OrOver = false;
        checkConfirmationMethod = false;
        checkVerifiedClaims = false;
        checkValidityPeriod = false;
    }

    private int LoginWithUserNamePasswd(Callback[] callbacks) throws AuthLoginException {

        String password = null;
        Callback[] idCallbacks;
        boolean getCredentialsFromSharedState = false;

        // That's 'regular' compatibility mode login
        if (callbacks != null && callbacks.length == 0) {
            idCallbacks = new Callback[2];
            userID = (String) sharedState.get(getUserKey());
            password = (String) sharedState.get(getPwdKey());
            if (userID == null || password == null) {
                return ISAuthConstants.LOGIN_START;
            }
            getCredentialsFromSharedState = true;
            NameCallback nameCallback = new NameCallback("dummy");
            nameCallback.setName(userID);
            idCallbacks[0] = nameCallback;
            PasswordCallback passwordCallback = new PasswordCallback("dummy", false);
            passwordCallback.setPassword(password.toCharArray());
            idCallbacks[1] = passwordCallback;
        } else {
            //callbacks is not null
            idCallbacks = callbacks;
            userID = ((NameCallback) callbacks[0]).getName();
            password = String.valueOf(((PasswordCallback) callbacks[1]).getPassword());
        }

        if (password == null || password.length() == 0) {
            if (debug.errorEnabled()) {
                debug.error("User password is null or empty");
            }
            throw new InvalidPasswordException("amAuth", "invalidPasswd", null);
        }

        //store user ID and password both in success and failure case
        storeUsernamePasswd(userID, password);

        //initAuthConfig();
        try {
            AMIdentityRepository idrepo = getAMIdentityRepository(getRequestOrg());
            boolean success = idrepo.authenticate(idCallbacks);
            if (success) {
                validatedUserID = userID;
                return ISAuthConstants.LOGIN_SUCCEED;
            } else {
                throw new AuthLoginException(amAuthInfocard, "authFailed", null);
            }
        } catch (IdRepoException ex) {
            if (getCredentialsFromSharedState && !isUseFirstPassEnabled()) {
                return ISAuthConstants.LOGIN_START;
            }
            setFailureID(userID);
            throw new AuthLoginException(amAuthInfocard, "authFailed", null, ex);
        }
    }

    private int LoginWithInfocard() throws AuthLoginException {

        Callback[] callbacks = new Callback[2];

        try {
            ppid = getPpid();
            if (ppid == null) {
                if (debug.errorEnabled()) {
                    debug.error("Manadatory 'PPID' claim is missing");
                }
                errorMsg = bundle.getString("missingPPID");
                throw new AuthLoginException(amAuthInfocard, "invalidInfocard",
                        new Object[]{errorMsg});
            }
            digest = getDigest();
            if (digest == null) {
                errorMsg = bundle.getString("missingDigest");
                throw new AuthLoginException(amAuthInfocard, "invalidInfocard",
                        new Object[]{errorMsg});
            }

            if (debug.messageEnabled()) {
                HttpServletRequest request = getHttpServletRequest();
                if (request != null) {
                    String cardSelectorName = request.getHeader("X-ID-Selector");
                    debug.message("Received Information Card authentication request for ppid = " + ppid + "sent from Identity Selector type:'" + cardSelectorName + "'");
                }
            }
            // TODO: I think we have a problem here because searchUserIdentity returns
            // too many entries. Must improve search criteria filter.
            AMIdentityRepository idrepo = getAMIdentityRepository(getRequestOrg());
            AMIdentity userIdentity = InfocardUtils.searchUserIdentity(idrepo, ppid, "*");

            if (userIdentity != null) { // Information Card already linked with this entry
                userID = userIdentity.getName();
                InfocardData infocardData = InfocardUtils.readInfocardData(userIdentity, ppid);
                if (infocardData != null) {
                    userPasswd = infocardData.getPassword();
                    if (!digest.equals(infocardData.get(ppid))) {
                        // Forgery ?
                        debug.error(
                                "Information Card token digest doesn't match stored digest for PPID =" + ppid);
                        throw new AuthLoginException(amAuthInfocard, "authFailed", null);
                    }
                }
                if (userPasswd == null || userPasswd.length() == 0) {
                    debug.error("User password is null or empty");
                    throw new InvalidPasswordException("amAuth", "invalidPasswd", null);
                }

                NameCallback nameCallback = new NameCallback("dummy");
                nameCallback.setName(userID);
                callbacks[0] = nameCallback;
                PasswordCallback passwordCallback = new PasswordCallback("dummy", false);
                passwordCallback.setPassword(userPasswd.toCharArray());
                callbacks[1] = passwordCallback;

                //store user ID and password both in success and failure case
                storeUsernamePasswd(userID, userPasswd);

                try {
                    boolean success = idrepo.authenticate(callbacks);
                    if (success) {
                        if (isInformationCardValid()) {
                            validatedUserID = userID;
                            return ISAuthConstants.LOGIN_SUCCEED;
                        } else {
                            setFailureID(userID);
                            throw new AuthLoginException(amAuthInfocard, "invalidInfocard",
                                    new Object[]{errorMsg});
                        }
                    } else {
                        // Current password and stored password for IC are different
                        // TODO: that's not very clean... To be improved
                        InfocardUtils.removeInfocards(userIdentity);
                        setFailureID(userID);
                        debug.error("User password has changed since Information Card registration. " +
                                "Deleting all Information Cards for that user: " + userID);
                        return PROFILE_ERROR;
                    }
                } catch (IdRepoException e) {
                    setFailureID(userID);
                    debug.error("profile exception occured: ", e);
                    return PROFILE_ERROR;
                }
            } else if (ignoreUserProfile) {
                // Since user profile is ignored, user is allowed to login provided
                // Information Card claims meet validation conditions
                userID = defaultAnonUser;
                storeUsernamePasswd(userID, null);
                if (isInformationCardValid()) {
                    validatedUserID = userID;
                    return ISAuthConstants.LOGIN_SUCCEED;
                } else {
                    setFailureID(userID);
                    throw new AuthLoginException(amAuthInfocard, "invalidInfocard",
                            new Object[]{errorMsg}, null);
                }
            } else {
                // Bind this Information Card with a new or existing user account
                return BINDING_STATE;
            }
        } catch (InfocardException e) {
            // Ooops... something unexpected happened!
            e.printStackTrace();
            throw new AuthLoginException(amAuthInfocard, "internalError", null, e);
        }
    }

    private int registerInfocardWithExistingUserEntry(Callback[] callbacks)
            throws AuthLoginException {

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
                AMIdentityRepository idrepo = getAMIdentityRepository(getRequestOrg());
                boolean success = idrepo.authenticate(callbacks);
                if (success) {
                    if (isInformationCardValid()) {
                        AMIdentity userIdentity = getUserIdentity(userID, getRequestOrg());
                        InfocardUtils.addInfocard(userIdentity, ppid, digest, userPasswd);
                        validatedUserID = userID;
                        return ISAuthConstants.LOGIN_SUCCEED;
                    } else {
                        setFailureID(userID);
                        throw new AuthLoginException(amAuthInfocard, "invalidInfocard",
                                new Object[]{errorMsg}, null);
                    }
                } else {
                    setFailureID(userID);
                    throw new AuthLoginException(amAuthInfocard, "authFailed", null);
                }
            } catch (IdRepoException e) {
                // This user doesn't exist
                setFailureID(userID);
                throw new AuthLoginException(amAuthInfocard, "authFailed", null, e);
            } catch (InfocardException e) {
                // Ooops... something bad happened!
                e.printStackTrace();
                throw new AuthLoginException(amAuthInfocard, "internalError", null, e);
            }
        } else {
            throw new AuthLoginException(amAuthInfocard, "authFailed", null);
        }
    }

    private int registerInfocardWithNewUserEntry(Callback[] callbacks)
            throws AuthLoginException {

        // callback[0] is for user name
        // callback[1] is for new password
        // callback[2] is for confirm password
        if (callbacks != null && callbacks.length != 0) {
            userID = ((NameCallback) callbacks[0]).getName();
            // check user name
            if ((userID == null) || userID.length() == 0) {
                // no user name was entered, this is required to
                // create the user's profile
                return NO_USERID_ERROR;
            }

            //validate username using plugin if any
            validateUserName(userID, regEx);

            // get the passwords from the input form
            userPasswd = String.valueOf(((PasswordCallback) callbacks[1]).getPassword());
            String confirmPassword = String.valueOf(((PasswordCallback) callbacks[2]).getPassword());

            // check passwords
            int status = checkPassword(userID, userPasswd, confirmPassword);

            // Return if any error
            if (status != ISAuthConstants.LOGIN_SUCCEED) {
                debug.error("Check password failed with status= " + status);
                return status;
            }

            // validate password using validation plugin if any
            validatePassword(userPasswd);

            if (userPasswd.equals(userID)) {
                // the user name and password are the same. these fields
                // must be different
                return USER_PASSWORD_SAME_ERROR;
            }

            Set<String> values = new HashSet<String>();
            values.add(userID);
            userAttributeValues.put("uid", values);

            values = new HashSet<String>();
            values.add(userPasswd);
            userAttributeValues.put("userPassword", values);

            // check user ID uniqueness
            try {
                if (isUserRegistered(userID)) {
                    if (debug.messageEnabled()) {
                        debug.message("user ID " + userID + " already exists");
                    }

                    values = new HashSet<String>();
                    values.add(getFirstName());
                    userAttributeValues.put("givenname", values);

                    values = new HashSet<String>();
                    values.add(getLastName());
                    userAttributeValues.put("sn", values);

                    // get a list of user IDs from the generator
                    Set generatedUserIDs = getNewUserIDs(userAttributeValues, 6);
                    if (generatedUserIDs == null) {
                        // user name generator is disable
                        return USER_EXISTS_ERROR;
                    }

                    // get a list of user IDs that are not yet being used
                    ArrayList nonExistingUserIDs = getNonExistingUserIDs(generatedUserIDs);

                    resetCallback(CHOOSE_USERNAME, 0);
                    Callback[] origCallbacks = getCallback(CHOOSE_USERNAME);
                    ChoiceCallback origCallback = (ChoiceCallback) origCallbacks[0];
                    String prompt = origCallback.getPrompt();
                    myOwnID = origCallback.getChoices()[0];

                    nonExistingUserIDs.add(myOwnID);

                    String[] choices = ((String[]) nonExistingUserIDs.toArray(new String[0]));

                    ChoiceCallback callback = new ChoiceCallback(prompt, choices, 0, false);
                    callback.setSelectedIndex(0);
                    replaceCallback(CHOOSE_USERNAME, 0, callback);

                    return CHOOSE_USERNAME;
                }
            } catch (SSOException e) {
                debug.error("profile exception occured: ", e);
                return PROFILE_ERROR;
            } catch (IdRepoException e) {
                debug.error("profile exception occured: ", e);
                return PROFILE_ERROR;
            }
        } else {
            throw new AuthLoginException(amAuthInfocard, "authFailed", null);
        }

        return ISAuthConstants.LOGIN_SUCCEED;
    }

    private int registerNewUser() throws AuthLoginException {

        try {
            // Check again ?
            if (isUserRegistered(userID)) {
                return USER_EXISTS_ERROR;
            }

            Set<String> values = new HashSet<String>();
            values.add(serviceStatus);
            userAttributeValues.put("inetuserstatus", values);

            if (isInformationCardValid()) {
                if (isDynamicProfileCreationEnabled()) {
                    addClaimsToAttributeValues();
                }
                createIdentity(userID, userAttributeValues, defaultRoles);
                // Should optimize this ...
                AMIdentity userIdentity = getUserIdentity(userID, getRequestOrg());
                InfocardUtils.addInfocard(userIdentity, ppid, digest, userPasswd);
                validatedUserID = userID;
                return ISAuthConstants.LOGIN_SUCCEED;
            } else {
                setFailureID(userID);
                throw new AuthLoginException(amAuthInfocard, "invalidInfocard",
                        new Object[]{errorMsg}, null);
            }
        } catch (SSOException e) {
            setFailureID(userID);
            debug.error("profile exception occured: ", e);
            return PROFILE_ERROR;

        } catch (IdRepoException e) {
            setFailureID(userID);
            debug.error("profile exception occured: ", e);
            return PROFILE_ERROR;
        } catch (InfocardException e) {
            // Ooops... something unexpected happened!
            e.printStackTrace();
            throw new AuthLoginException(amAuthInfocard, "internalError", null, e);
        }
    }

    protected static String getAuthType() {
        return authType;
    }

    private AMIdentity getUserIdentity(String userName, String org) {

        AMIdentity userIdentity = null;

        if (org != null && org.length() != 0) {
            try {
                userIdentity = AuthD.getAuth().getIdentity(IdType.USER, userName, org);
                if (userIdentity.isExists() && userIdentity.isActive()) {
                    if (debug.messageEnabled()) {
                        debug.message(
                                "getUserIdentity: Found identity for '" +
                                userName + "' in realm = " + userIdentity.getRealm() +
                                " id = " + userIdentity.getUniversalId());
                    }
                }
            } catch (AuthException e1) {
                debug.error("getUserIdentity: caught exception", e1);
            } catch (IdRepoException e2) {
                debug.error("getUserIdentity: caught exception", e2);
            } catch (SSOException e3) {
                debug.error("getUserIdentity: caught exception", e3);
            }
        }
        return userIdentity;
    }

    private boolean isInformationCardValid() {


        if (checkValidityPeriod && !isConditionValid) {
            errorMsg = "Token validity period not before [" + beginValidityPeriod.toString() +
                    "] and not after [" + endValidityPeriod.toString() + "]";
            return false;
        }
        if (checkAudienceRestriction) {
        }

        if (checkAge18OrOver) {
        }

        if (checkCoppaCertified) {
        }

        if (checkAge21OrOver) {
        }

        if (checkConfirmationMethod) {
        }

        if (checkVerifiedClaims) {
        }

        return true;
    }

    private ArrayList getNonExistingUserIDs(Set userIDs)
            throws IdRepoException, SSOException {

        ArrayList<String> validUserIDs = new ArrayList<String>();
        Iterator it = userIDs.iterator();

        while (it.hasNext()) {
            String uid = (String) it.next();
            // check if user already exists with the same user ID
            if (!isUserRegistered(uid)) {
                validUserIDs.add(uid);
            }
        }
        return validUserIDs;
    }

    /**
     * When user click cancel button, these input field should be reset
     * to blank.
     */
    private void clearCallbacks(Callback[] callbacks) {
        for (int i = 0; i <
                callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                NameCallback nc = (NameCallback) callbacks[i];
                nc.setName("");
            }
        }
    }

    /**
     * Returns the first input value for the given Callback.
     * Returns null if there is no value for the Callback.
     */
    private static String getCallbackFieldValue(Callback callback) {

        Set values = getCallbackFieldValues(callback);
        Iterator it = values.iterator();
        if (it.hasNext()) {
            return ((String) it.next());
        }
        return null;
    }

    private static Set getCallbackFieldValues(Callback callback) {
        Set<String> values = new HashSet<String>();

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

    private static String getPassword(PasswordCallback callback) {
        char[] tmpPassword = callback.getPassword();
        if (tmpPassword == null) {
            // treat a NULL password as an empty password
            tmpPassword = new char[0];
        }

        char[] pwd = new char[tmpPassword.length];
        System.arraycopy(tmpPassword, 0, pwd, 0, tmpPassword.length);

        return (new String(pwd));
    }

    private static int checkPassword(String uid, String password, String confirmPassword) {

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
        return ISAuthConstants.LOGIN_SUCCEED;
    }

    private boolean isUserRegistered(String userID)
            throws IdRepoException, SSOException {

        AMIdentityRepository amIdRepo = getAMIdentityRepository(getRequestOrg());

        IdSearchControl idsc = new IdSearchControl();
        idsc.setRecursive(true);
        idsc.setTimeOut(0);
        idsc.setAllReturnAttributes(true);
        // search for the identity
        Set results = Collections.EMPTY_SET;
        try {
            idsc.setMaxResults(0);
            IdSearchResults searchResults = amIdRepo.searchIdentities(IdType.USER, userID, idsc);
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

    private void setClaimToAttributeMap(Map currentConfig) {

        Set parameter = (Set) currentConfig.get("iplanet-am-auth-infocard-creation-attr-list");
        if (debug.messageEnabled()) {
            debug.message("User attributes creation list is : " + parameter);
        }
        if ((parameter != null) && (!parameter.isEmpty())) {
            Iterator attrIterator = parameter.iterator();
            while (attrIterator.hasNext()) {
                String attr = (String) attrIterator.next();
                int i = attr.indexOf("|");
                if (i != -1) {
                    String claimName = attr.substring(0, i);
                    String attrName = attr.substring(i + 1, attr.length());
                    if ((attrName == null) || (attrName.length() == 0)) {
                        claimToAttributeMap.put(claimName, claimName);
                    } else {
                        claimToAttributeMap.put(claimName, attrName);
                    }
                } else {
                    claimToAttributeMap.put(attr, attr);
                }
            }
        }
        return;
    }

    private void addClaimsToAttributeValues() {

        if (debug.messageEnabled()) {
            debug.message("claims map = " + userClaimValues);
        }

        Iterator itr = claimToAttributeMap.keySet().iterator();
        while (itr.hasNext()) {
            String claimName = (String) itr.next();
            String attrName = (String) claimToAttributeMap.get(claimName);
            String value = userClaimValues.get(claimName);

            if (value != null && value.length() != 0) {
                Set<String> newSet = new HashSet<String>();
                newSet.add(value);
                userAttributeValues.put(attrName, newSet);
                if (debug.messageEnabled()) {
                    debug.message("Added claim '" + claimName + "' value = " + value + "entry attributes list");
                }
            }
        }
        if (debug.messageEnabled()) {
            debug.message("Attributes map = " + userAttributeValues);
        }
    }

    private void addInfocardClaimsToSession() {

        if (userClaimValues != null) {
            Set<String> keys = userClaimValues.keySet();
            try {
                Iterator itr = keys.iterator();
                while (itr.hasNext()) {
                    String claim = (String) itr.next();
                    setUserSessionProperty(claim, (String) userClaimValues.get(claim));
                }
            } catch (AuthLoginException e) {
                debug.message("Error setting session's attributes", e);
            }
        }
    }

    private Token getToken()
            throws InfoCardProcessingException, InvalidCertException,
            InvalidTokenConditionException, InvalidTokenSignatureException {

        Token token = null;

        HttpServletRequest request = getHttpServletRequest();
        if (request != null) {
            String encXmlToken = request.getParameter("xmlToken");
            if (encXmlToken != null && encXmlToken.length() != 0) {
                token = new Token(encXmlToken, privateKey);
                if (token.isSignatureValid()) {
                    X509Certificate cert = token.getCertificateOrNull();
                    if (cert == null || (cert != null && token.isCertificateValid())) {
                        try {
                            userClaimValues = (Map<String, String>)token.getClaims();
                            confirmationMethod = token.getConfirmationMethod();
                            audience = token.getAudience();
                            digest = token.getClientDigest();
                            endValidityPeriod = token.getEndValidityPeriod().getTime();
                            beginValidityPeriod = token.getStartValidityPeriod().getTime();
                            isConditionValid = token.isConditionsValid();
                        } catch (CryptoException e) {
                            throw new InfoCardProcessingException("Information Card Token processing error", e);
                        }
                    } else {
                        throw new InvalidCertException("Information Card has an invalid certificate");
                    }
                } else {
                    throw new InvalidTokenSignatureException("Information token has an invalid signature");
                }
            }
        }

        return token;
    }

    private String getPpid() {

        return getClaim(PPID_CLAIM);
    }

    private String getFirstName() {

        return getClaim(GIVENNAME_CLAIM);
    }

    private String getLastName() {

        return getClaim(SURNAME_CLAIM);
    }

    private String getClaim(String cname) {

        Map var = getClaims();
        Set keys = var.keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            if (cname.equals(name)) {
                return (String) userClaimValues.get(name);
            }

        }
        return null;
    }

    private Map getClaims() {

        return userClaimValues;
    }

    private String getDigest() {

        return digest;
    }

    private String getAudience() {

        return audience;
    }

    private String getConfirmationMethod() {

        return confirmationMethod;
    }

    private Date getBeginValidityPeriod() {

        return beginValidityPeriod;
    }

    private Date getEndValidityPeriod() {

        return endValidityPeriod;
    }

    /**
     * Get the private. Must correspond to the server's SSL cert private key
     */
    private static synchronized PrivateKey getPrivateKey()
            throws InfocardException {

        PrivateKey key = null;
        try {
            KeystoreUtil keystore = new KeystoreUtil(keyStorePath, keyStorePasswd);
            key = keystore.getPrivateKey(keyAlias, keyStorePasswd);
        } catch (Exception e) {
            throw new InfocardException("Caught keystore exception", e);
        }

        return key;
    }

    private static void debugPrintMap(String name, Map map) {
        Set keys = map.keySet();
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            System.out.println(">>> " + name + "(" + key + ")=" + map.get(key));
        }

    }

    private static String getMapAttrValues(Map map, String attrName, String defValue) {

        String value = defValue;
        Set keys = map.keySet();

        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if (key.equals(attrName)) {
                String var = map.get(key).toString();
                var = var.substring(1, var.length() - 1);
                value = var;
                break;
            }
        }
        return value;
    }

    private void dumpXmlToken() {

        try {
            Map var = getClaims();

            System.out.println(">>> You provided the following claims:\n");
            Set keys = var.keySet();
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                String name = (String) iter.next();
                String value = (String) var.get(name);
                System.out.println(">>> \t" + name + ":" + value + "\n");
            }

        } catch (Exception e1) {
            // System.out.println("Error getting token" + e1.getMessage());
        }
    }
}
            