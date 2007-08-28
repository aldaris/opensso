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
 * $Id: DefaultAuthenticator.java,v 1.5 2007-08-28 00:20:06 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.security.handler;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ResourceBundle;
import javax.security.auth.Subject;
import java.security.PrivilegedAction;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.security.AccessController;

import com.sun.identity.shared.debug.Debug;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdRepoException;

import com.sun.identity.wss.provider.ProviderConfig;
import com.sun.identity.wss.security.PasswordCredential;
import com.sun.identity.wss.security.SecurityMechanism;
import com.sun.identity.wss.security.SecurityToken;
import com.sun.identity.wss.security.SecurityException;
import com.sun.identity.wss.security.SecurityPrincipal;
import com.sun.identity.wss.security.UserNameToken;
import com.sun.identity.wss.security.AssertionToken;
import com.sun.identity.wss.security.WSSConstants;
import com.sun.identity.wss.security.WSSUtils;
import com.sun.identity.saml.assertion.Assertion;
import com.sun.identity.saml.assertion.NameIdentifier;
import com.sun.identity.saml.assertion.AttributeStatement;
import com.sun.identity.saml.assertion.AuthenticationStatement;
import com.sun.identity.saml.assertion.Statement;
import com.sun.identity.liberty.ws.soapbinding.Message;
import com.sun.identity.liberty.ws.security.SecurityAssertion;
import com.sun.identity.wss.security.SAML2Token;
import com.sun.identity.wss.security.SAML2TokenUtils;

import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.authentication.spi.X509CertificateCallback;
import com.sun.identity.saml.xmlsig.XMLSignatureManager;

/**
 * This class provides a default implementation for authenticating the
 * webservices clients using various security mechanisms.
 */ 
public class DefaultAuthenticator implements MessageAuthenticator {

    private ProviderConfig config = null;
    //private Subject subject = null;
    private static ResourceBundle bundle = WSSUtils.bundle;
    private static Debug debug = WSSUtils.debug;

    /**
     * Authenticates the web services client.
     * @param subject the JAAS subject that may be used during authentication.
     * @param securityMechanism the security mechanism that will be used to
     *        authenticate the web services client.
     * @param securityToken the security token that is used.
     * @param config the provider configuration.
     * @param secureMessage the secure SOAPMessage.
     *      If the message security is provided by the WS-I profies, the
     *      secureMessage object is of type 
     *     <code>com.sun.identity.wss.security.handler.SecureSOAPMessage</code>.
     *     If the message security is provided by the Liberty ID-WSF
     *     profiles, the secure message is of type 
     *     <code>com.sun.identity.liberty.ws.soapbinding.Message</code>.
     * @param isLiberty boolean variable to indicate that the message
     *        security is provided by the liberty security profiles.
     * @exception SecurityException if there is a failure in authentication.
     */
    public Object authenticate(
             Subject subject,
             SecurityMechanism securityMechanism,
             SecurityToken securityToken,
             ProviderConfig config,
             Object secureMessage,
             boolean isLiberty) throws SecurityException {

        debug.message("DefaultAuthenticator.authenticate: start");
        this.config = config;        
        
        String authChain = config.getAuthenticationChain();

        if(isLiberty) {
           return authenticateLibertyMessage(secureMessage, subject);
        }
        if(securityMechanism == null || securityToken == null) {
           throw new SecurityException(
                 bundle.getString("nullInputParameter"));
        }

        String uri = securityMechanism.getURI();

        if((SecurityMechanism.WSS_NULL_USERNAME_TOKEN_URI.equals(uri)) ||
            (SecurityMechanism.WSS_TLS_USERNAME_TOKEN_URI.equals(uri)) ||
            (SecurityMechanism.WSS_CLIENT_TLS_USERNAME_TOKEN_URI.equals(uri)) ||
            (SecurityMechanism.WSS_NULL_USERNAME_TOKEN_PLAIN_URI.equals(uri)) ||
            (SecurityMechanism.WSS_TLS_USERNAME_TOKEN_PLAIN_URI.equals(uri)) ||
            (SecurityMechanism.WSS_CLIENT_TLS_USERNAME_TOKEN_PLAIN_URI.
             equals(uri))){

            if(debug.messageEnabled()) {
               debug.message("DefaultAuthenticator.authenticate:: username" +
               " token authentication");
               debug.message("authenticate: authChain : " + authChain);
            }

            UserNameToken usertoken = (UserNameToken)securityToken;
            
            if ((authChain == null) || (authChain.length() == 0) ||
                (authChain.equals("none"))) {
                if(!validateUser(usertoken, subject)) {
                    throw new SecurityException(
                        bundle.getString("authenticationFailed"));
                }
            } else {
                if(!authenticateUser(usertoken, subject, authChain)) {
                    throw new SecurityException(
                        bundle.getString("authenticationFailed"));
                }
            }
        } else if(
            (SecurityMechanism.WSS_NULL_X509_TOKEN_URI.equals(uri)) ||
            (SecurityMechanism.WSS_TLS_X509_TOKEN_URI.equals(uri)) ||
            (SecurityMechanism.WSS_CLIENT_TLS_X509_TOKEN_URI.equals(uri))) {

            if(debug.messageEnabled()) {
               debug.message("DefaultAuthenticator.authenticate:: x509" +
               " token authentication");
               debug.message("authenticate: authChain : " + authChain);
            }

            SecureSOAPMessage securedMessage = 
                              (SecureSOAPMessage)secureMessage;
            X509Certificate cert = securedMessage.getMessageCertificate();
            
            if(cert == null) {
               debug.error("DefaultAuthenticator.authenticate:: X509 auth " +
               "could not find the message certificate.");
               throw new SecurityException(
                     bundle.getString("authenticationFailed"));
            }

            XMLSignatureManager sigManager = WSSUtils.getXMLSignatureManager();
            String certAlias = sigManager.getKeyProvider().
                getCertificateAlias(cert);

            if(debug.messageEnabled()) {
                debug.message("DefaultAuthenticator.authenticate: cert : " 
                              + cert);
                debug.message("DefaultAuthenticator.authenticate: certAlias : " 
                              + certAlias);
            }
            
            if ((authChain != null) && (authChain.length() != 0) &&
                (!authChain.equals("none"))) {
                if(!authenticateCert(certAlias,authChain,cert, subject)) {
                    throw new SecurityException(
                        bundle.getString("authenticationFailed"));
                }
            }
            
            String subjectDN = cert.getSubjectDN().getName();
            subject = addPrincipal(subjectDN, subject);
            subject.getPublicCredentials().add(cert);
            WSSUtils.setRoles(subject, subjectDN);
        } else if(
            (SecurityMechanism.WSS_NULL_SAML_HK_URI.equals(uri)) ||
            (SecurityMechanism.WSS_TLS_SAML_HK_URI.equals(uri)) ||
            (SecurityMechanism.WSS_CLIENT_TLS_SAML_HK_URI.equals(uri)) ||
            (SecurityMechanism.WSS_NULL_SAML_SV_URI.equals(uri)) ||
            (SecurityMechanism.WSS_TLS_SAML_SV_URI.equals(uri)) ||
            (SecurityMechanism.WSS_CLIENT_TLS_SAML_SV_URI.equals(uri))) {

            if(debug.messageEnabled()) {
               debug.message("DefaultAuthenticator.authenticate:: saml" +
               " token authentication");
            }
            AssertionToken assertionToken = (AssertionToken)securityToken;
            if(!validateAssertion(assertionToken.getAssertion(), subject)) {
               throw new SecurityException(
                     bundle.getString("authenticationFailed"));
            }
        }  else if(
            (SecurityMechanism.WSS_NULL_SAML2_HK_URI.equals(uri)) ||
            (SecurityMechanism.WSS_TLS_SAML2_HK_URI.equals(uri)) ||
            (SecurityMechanism.WSS_CLIENT_TLS_SAML2_HK_URI.equals(uri)) ||
            (SecurityMechanism.WSS_NULL_SAML2_SV_URI.equals(uri)) ||
            (SecurityMechanism.WSS_TLS_SAML2_SV_URI.equals(uri)) ||
            (SecurityMechanism.WSS_CLIENT_TLS_SAML2_SV_URI.equals(uri))) {
            
            if(debug.messageEnabled()) {
               debug.message("DefaultAuthenticator.authenticate:: saml2" +
               " token authentication");
            }
            SAML2Token saml2Token = (SAML2Token)securityToken;
            if(!SAML2TokenUtils.validateAssertion(saml2Token.getAssertion(),
                    subject)) {
               throw new SecurityException(
                     bundle.getString("authenticationFailed"));
            }
        } else {
            debug.error("DefaultAuthenticator.authenticate:: Invalid " +
            "security mechanism");
            throw new SecurityException(
                     bundle.getString("authenticationFailed"));
        }
        return subject;
    }

    /**
     * Validates the user present in the username token.
     */
    private boolean validateUser(UserNameToken usernameToken, Subject subject) 
        throws SecurityException {

        String user = usernameToken.getUserName();
        String password = usernameToken.getPassword();
        if( (user == null) || (password == null) ) {  
            return false;
        }
      
        List users = config.getUsers();
        if(users == null || users.isEmpty()) {
           debug.error("DefaultAuthenticator.validateUser:: users are not " +
           " configured in the providers.");
           return false;
        }

        Iterator iter = users.iterator();
        String configuredUser = null, configuredPassword = null;
        while(iter.hasNext()) {
            PasswordCredential cred = (PasswordCredential)iter.next();
            configuredUser = cred.getUserName();
            if(configuredUser.equals(user)) {
               configuredPassword = cred.getPassword();
               break;
            }
        } 

        if(configuredUser == null || configuredPassword == null) {
           debug.error("DefaultAuthenticator.validateUser:: configured user " +
           " does not have the password.");
            return false;
        }

        String passwordType = usernameToken.getPasswordType(); 
        if((passwordType != null) && 
           (passwordType.equals(WSSConstants.PASSWORD_DIGEST_TYPE)) ) {
           String nonce = usernameToken.getNonce();
           String created = usernameToken.getCreated();
           String digest = UserNameToken.getPasswordDigest(
                 configuredPassword, nonce, created);
           if(!(digest.equals(password)) || !(configuredUser.equals(user))) {
               debug.error("DefaultAuthenticator.validateUser:: Password " +
               "does not match");
               return false;
           }
        } else if(!(configuredPassword.equals(password)) || 
                   !(configuredUser.equals(user))) { 
           
          debug.error("DefaultAuthenticator.validateUser:: Password " +
          "does not match");
           return false;
        }
        
        subject = addPrincipal(user, subject);
        WSSUtils.setRoles(subject, user);
        return true;
    }

    /**
     * Authenticates the user, present in the username token
     * against configured LDAP server.
     */
    private boolean authenticateUser(UserNameToken usernameToken, 
            Subject subject, String authChain) throws SecurityException {

        String user = usernameToken.getUserName();
        String password = usernameToken.getPassword();
        if( (user == null) || (password == null) ) {  
            return false;
        }
      
        // Autheticate to LDAP server using Authentication client API
        AuthContext ac = null;
        AuthContext.IndexType indexType = AuthContext.IndexType.SERVICE;
        String indexName = authChain;
	try {
            ac = new AuthContext("/");
	    debug.message("authenticateUser: Obtained AuthContext");
            ac.login(indexType, indexName);
        } catch (AuthLoginException le) {
            debug.error("authenticateUser: Login error : " + le.getMessage());
            return false;
        }

	Callback[] callbacks = null;
        while (ac.hasMoreRequirements()) {
	    callbacks = ac.getRequirements();

	    if (callbacks != null) {
		try {
		    addLoginCallbackMessage(callbacks,user,password,null);
		    ac.submitRequirements(callbacks);
		} catch (Exception e) {
		    debug.error("authenticateUser: Submit error : " 
                                + e.getMessage());
                    return false;
		}
	    }
	}

        SSOToken ssotoken = null;
	if (ac.getStatus() == AuthContext.Status.SUCCESS) {
	    debug.message("authenticateUser: Login success!!");
            try {
                ssotoken = ac.getSSOToken();
                debug.message("authenticateUser: got SSOToken successfully");
            } catch(Exception ex){
                if(debug.messageEnabled()) {
                    debug.message("authenticateUser: SSOToken error : " 
                              + ex.getMessage());
                }
            }

        } else if (ac.getStatus() == AuthContext.Status.FAILED) {
	    debug.error("authenticateUser: Login Failed.");
            return false;
	} else {
            debug.error("authenticateUser: Unknown status : " 
                        + ac.getStatus());
            return false;
	}
        
        subject = addPrincipal(user, subject);
        WSSUtils.setRoles(subject, user);
        addSSOToken(ssotoken, subject);
        return true;
    }

    /**
     * Authenticates the client certificate, present in the X509 token
     * against configured Certificate authentication chain on AM server.
     */
    private boolean authenticateCert(String certAlias, String authChain,
           X509Certificate cert, Subject subject) throws SecurityException {

        if( (certAlias == null) || (certAlias.length() == 0) ) {  
            return false;
        }
      
        if (debug.messageEnabled()) {
            debug.message("authenticateCert: certAlias : " + certAlias);
        }

        // Autheticate to LDAP server using Authentication client API
        AuthContext ac = null;
        AuthContext.IndexType indexType = AuthContext.IndexType.SERVICE;
        String indexName = authChain;
        try {
            ac = new AuthContext("/", certAlias);
	    debug.message("authenticateCert: Obtained AuthContext");
            ac.login(indexType, indexName);
        } catch (AuthLoginException le) {
            debug.error("authenticateCert: Login error : " + le.getMessage());
            return false;
        }

	Callback[] callbacks = null;
        while (ac.hasMoreRequirements()) {
	    callbacks = ac.getRequirements();

	    if (callbacks != null) {
		try {
		    addLoginCallbackMessage(callbacks,null,null,cert);
		    ac.submitRequirements(callbacks);
		} catch (Exception e) {
		    debug.error("authenticateCert: Submit error : " 
                                + e.getMessage());
                    return false;
		}
	    }
	}

        SSOToken ssotoken = null;
	if (ac.getStatus() == AuthContext.Status.SUCCESS) {
	    debug.message("authenticateCert: Login success!!");
            try {
                ssotoken = ac.getSSOToken();
                debug.message("authenticateCert: got SSOToken successfully");
            } catch(Exception ex){
                if(debug.messageEnabled()) {
                    debug.message("authenticateCert: SSOToken error : " 
                              + ex.getMessage());
                }
            }

        } else if (ac.getStatus() == AuthContext.Status.FAILED) {
	    debug.error("authenticateCert: Login Failed.");
            return false;
	} else {
            debug.error("authenticateCert: Unknown status : " 
                        + ac.getStatus());
            return false;
	}

        addSSOToken(ssotoken, subject);
        return true;
    }

    /**
     * Validates the security assertion token.
     */
    private boolean validateAssertion(Assertion assertion, Subject subject)
               throws SecurityException {

        if((assertion.getConditions() != null) &&
                  !(assertion.getConditions().checkDateValidity(
                    System.currentTimeMillis())) ) {
           if(debug.messageEnabled()) {
              debug.message("DefaultAuthenticator.validateAssertionToken:: " +
              " assertion time is not valid");
           }
           return false;
        }

        com.sun.identity.saml.assertion.Subject sub = null;
        Iterator iter = assertion.getStatement().iterator();
        while(iter.hasNext()) {
            Statement st = (Statement)iter.next();
            if(Statement.AUTHENTICATION_STATEMENT == st.getStatementType()) {
               AuthenticationStatement authStatement = 
                                       (AuthenticationStatement)st;
               sub = authStatement.getSubject(); 
               break;
            } else if(Statement.ATTRIBUTE_STATEMENT == st.getStatementType()) {
               AttributeStatement attribStatement = (AttributeStatement)st;
               sub = attribStatement.getSubject();
               break;
            }
        }

        if(sub == null) {
           if(debug.messageEnabled()) {
              debug.message("DefaultAuthenticator.validateAssertionToken:: " +
              "Assertion does not have subject");
           }
           return false;
        }

        NameIdentifier ni = sub.getNameIdentifier(); 
        if(ni == null) {
           return false;
        }

        subject = addPrincipal(ni.getName(), subject);
        WSSUtils.setRoles(subject, ni.getName());
        return true;
    }

    /**
     * Authenticates SOAPMessages using Liberty ID-WSF profiles.
     */
    private Object authenticateLibertyMessage(Object message, Subject subject)
         throws SecurityException  {

        if(message == null || subject == null) {
           throw new IllegalArgumentException(
                 bundle.getString("nullInput"));
        }
        Message requestMsg = (Message)message;
        SecurityAssertion assertion = requestMsg.getAssertion();
        if(assertion != null) {
           if(!validateAssertion(assertion, subject)) {
              throw new SecurityException(
                 bundle.getString("authenticationFailed"));
           } else {
              return subject;
           }
        }
        X509Certificate messageCert = requestMsg.getMessageCertificate();
        if(messageCert == null) {
           throw new SecurityException(
                 bundle.getString("authenticationFailed"));
        }
        String subjectDN = messageCert.getSubjectDN().getName();
        Principal principal = new SecurityPrincipal(subjectDN);
        subject.getPrincipals().add(principal);
        return subject;   
    }

    /**
     * Adds SSOToken Id as private credential of the Subject.
     * @param ssoToken
     *
     * @exception SecurityException
     */
    private void addSSOToken(final SSOToken ssoToken, final Subject subj)
                   throws SecurityException {
        if (ssoToken != null) {
            try {                
                AccessController.doPrivileged(new PrivilegedAction() {
                    public java.lang.Object run() {
                        subj.getPrivateCredentials().add(ssoToken);
                        return null;
                    }
                });
                debug.message("Set SSOToken in Subject successfully");
            } catch (Exception e) {
                debug.message("Can not set SSOToken in Subject");
                throw new SecurityException(e.getMessage());
            }
        }
    }

    /**
     * Adds SecurityPrincipal to the Subject.
     */
    private Subject addPrincipal(String principalName, Subject subject) {
        Principal principal = new SecurityPrincipal(principalName); 
        subject.getPrincipals().add(principal);
        return subject;
    }

    // get user's inputs and set them to callback array.
    static void addLoginCallbackMessage(Callback[] callbacks,String userName, 
                                        String password, X509Certificate cert) 
        throws UnsupportedCallbackException {
        int i = 0;
        try {
            for (i = 0; i < callbacks.length; i++) {
                if (callbacks[i] instanceof NameCallback) {
                    NameCallback nc = (NameCallback) callbacks[i];
                    nc.setName(userName.trim());
                } else if (callbacks[i] instanceof PasswordCallback) {
                    PasswordCallback pc = (PasswordCallback) callbacks[i];
                    pc.setPassword(password.toCharArray());
                } else if (callbacks[i] instanceof X509CertificateCallback) {
                    X509CertificateCallback certCB = 
                        (X509CertificateCallback)callbacks[i];
                    try {
                        certCB.setReqSignature(false);
                        certCB.setCertificate(cert);  
                    } catch (Exception e) {
                        if(debug.messageEnabled()) {
                            debug.message("createX509CertificateCallback : " 
                                         + e.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new UnsupportedCallbackException(callbacks[i], 
                                                   "Callback exception: " + e);
        }
    }
}
