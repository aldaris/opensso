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
 * $Id: WSFederationUtils.java,v 1.1 2007-06-21 23:01:34 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wsfederation.common;

import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.wsfederation.meta.WSFederationMetaException;
import java.util.logging.Level;

import com.sun.identity.shared.debug.Debug;

import com.sun.identity.saml.assertion.Assertion;
import com.sun.identity.saml.xmlsig.XMLSignatureManager; 
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.xmlsig.SigManager;
import com.sun.identity.wsfederation.jaxb.wsfederation.FederationElement;
import com.sun.identity.wsfederation.key.KeyUtil;
import com.sun.identity.wsfederation.logging.LogUtil;
import com.sun.identity.wsfederation.meta.WSFederationMetaManager;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 */
public class WSFederationUtils {
    public static String AM_WSFEDERATION = "amWSFederation";
    
    public static Debug debug = null;
    
    private static HashMap wctxMap = null;
    
    static {
        debug = Debug.getInstance(AM_WSFEDERATION);

        wctxMap = new HashMap();
    }
    
    private WSFederationUtils() {
    }
    
    public static String accountRealmFromUserAgent( String uaHeader, 
        String accountRealmCookieName )
    {
        String classMethod = "WSFederationUtils.accountRealmFromUserAgent";
        
        // UA String is of form "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 
        // 5.1; SV1; .NET CLR 1.1.4322; InfoPath.1; 
        // amWSFederationAccountRealm:Adatum Corp)"
        int leftBracket = uaHeader.indexOf('(');
        if ( leftBracket == -1 )
        {
            if (debug.warningEnabled()) {
                debug.warning(classMethod + "Can't find left bracket");
            }
            return null;
        }
        
        int rightBracket = uaHeader.lastIndexOf(')');
        if ( rightBracket == -1 || rightBracket < leftBracket )
        {
            if (debug.warningEnabled()) {
                debug.warning(classMethod + "Can't find right bracket");
            }
            return null;
        }
        
        String insideBrackets = uaHeader.substring(leftBracket+1,rightBracket);
        if ( insideBrackets.length() == 0 )
        {
            if (debug.warningEnabled()) {
                debug.warning(classMethod + "zero length between brackets");
            }
            return null;
        }
        
        // insideBrackets is of form "compatible; MSIE 6.0; Windows NT 5.1; SV1; 
        // .NET CLR 1.1.4322; InfoPath.1; 
        // amWSFederationAccountRealm:Adatum Corp"
        
        // Split string on matches of any amount of whitespace surrounding a 
        // semicolon
        String uaFields[] = insideBrackets.split("[\\s]*;[\\s]*");
        if ( uaFields == null )
        {
            if (debug.warningEnabled()) {
                debug.warning(classMethod + "zero length between brackets");
            }
            return null;
        }
        
        // uaFields[] is of form {"compatible", "MSIE 6.0", "Windows NT 5.1", 
        // "SV1", ".NET CLR 1.1.4322", "InfoPath.1", 
        // "amWSFederationAccountRealm:Adatum Corp"}
        
        for ( int i = 0; i < uaFields.length; i++ )
        {
            if ( uaFields[i].indexOf(accountRealmCookieName) != -1 )
            {
                // Split this field on matches of any amount of whitespace 
                // surrounding a colon
                String keyValue[] = uaFields[i].split("[\\s]*:[\\s]*");
                if ( keyValue.length < 2 )
                {
                    if (debug.warningEnabled()) {
                        debug.warning(classMethod + 
                            "can't see accountRealm in " + uaFields[i]);
                    }
                    return null;
                }
                
                if ( ! keyValue[0].equals(accountRealmCookieName))
                {
                    if (debug.warningEnabled()) {
                        debug.warning(classMethod + "can't understand " + 
                            uaFields[i]);
                    }
                    return null;
                }
                
                return keyValue[1];
            }
        }
        
        return null;
    }

    public static String putReplyURL(String wreply) {
        String wctx = SAML2Utils.generateID();
        synchronized (wctxMap)
        {
            wctxMap.put(wctx,wreply);
        }
        return wctx;
    }

    public static String removeReplyURL(String wctx) {
        String wreply = null;
        synchronized (wctxMap)
        {
            wreply = (String) wctxMap.remove(wctx);
        }
        return wreply;
    }

    /**
     * Return whether the signature on the object is valid or not.
     * @param assertion SAML 1.1 Assertion
     * @param realm Realm for the issuer
     * @param issuer Assertion issuer
     * @return true if the signature on the object is valid; false otherwise.
     */
    public static boolean isSignatureValid(Assertion assertion, String realm, 
        String issuer)
    {
        boolean valid = false;

        String signedXMLString = assertion.toString(true,true);
        String id = assertion.getAssertionID();
        
        try {
            FederationElement idp = 
                WSFederationMetaManager.getEntityDescriptor(realm, issuer);
            X509Certificate cert = KeyUtil.getVerificationCert(idp, issuer, 
                true);
            XMLSignatureManager manager = XMLSignatureManager.getInstance();
            valid = SigManager.getSigInstance().verify(
                signedXMLString, id, cert);
        } catch (WSFederationMetaException ex) {
            valid = false;
        } catch (SAML2Exception ex) {
            valid = false;
        }
        
        if ( ! valid )
        {
            String[] data = {LogUtil.isErrorLoggable(Level.FINER) ? 
                signedXMLString : id,
                realm, issuer
            };
            LogUtil.error(Level.INFO,
                    LogUtil.INVALID_SIGNATURE_ASSERTION,
                    data,
                    null);
        }
        return valid;
    }
    
    /**
     * @param assertion SAML 1.1 Assertion
     * @param timeskew in seconds
     * @return true if the current time is after the Assertion's notBefore time
     * - timeskew AND the current time is before the Assertion's notOnOrAfter 
     * time + timeskew
     */
    public static boolean isTimeValid(Assertion assertion, int timeskew)
    {
        String classMethod = "WSFederationUtils.isTimeValid: ";
        
        long timeNow = System.currentTimeMillis();
        Date notOnOrAfter = assertion.getConditions().getNotOnorAfter();
        String assertionID = assertion.getAssertionID();
        if (notOnOrAfter == null ) {
            String[] data = {LogUtil.isErrorLoggable(Level.FINER) ? 
                assertion.toString(true,true) : assertionID};
            LogUtil.error(Level.INFO,
                    LogUtil.MISSING_CONDITIONS_NOT_ON_OR_AFTER,
                    data,
                    null);
            return false;
        } else if ((notOnOrAfter.getTime() + timeskew * 1000) < timeNow ) {
            String[] data = {LogUtil.isErrorLoggable(Level.FINER) ? 
                assertion.toString(true,true) : assertionID,
                notOnOrAfter.toString(), 
                (new Integer(timeskew)).toString(),
                (new Date(timeNow)).toString()};
            LogUtil.error(Level.INFO,
                    LogUtil.ASSERTION_EXPIRED,
                    data,
                    null);
            return false;
        }
        Date notBefore = assertion.getConditions().getNotBefore();
        if ( notBefore == null ) {
            String[] data = {LogUtil.isErrorLoggable(Level.FINER) ? 
                assertion.toString(true,true) : assertionID};
            LogUtil.error(Level.INFO,
                    LogUtil.MISSING_CONDITIONS_NOT_BEFORE,
                    data,
                    null);
            return false;
        } else if ((notBefore.getTime() - timeskew * 1000) > timeNow ) {
            String[] data = {LogUtil.isErrorLoggable(Level.FINER) ? 
                assertion.toString(true,true) : assertionID,
                notBefore.toString(), 
                (new Integer(timeskew)).toString(),
                (new Date(timeNow)).toString()};
            LogUtil.error(Level.INFO,
                    LogUtil.ASSERTION_NOT_YET_VALID,
                    data,
                    null);
            return false;
        }
        return true;
    }
}
