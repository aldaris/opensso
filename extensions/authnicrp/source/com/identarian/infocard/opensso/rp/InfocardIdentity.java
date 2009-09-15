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
 * $Id: InfocardIdentity.java,v 1.2 2009-09-15 13:27:13 ppetitsm Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2008 Patrick Petit Consulting
 */
package com.identarian.infocard.opensso.rp;

import com.identarian.infocard.opensso.rp.exception.InfocardIdentityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.xmldap.rp.Token;
import org.xmldap.exceptions.InfoCardProcessingException;

/**
 * 
 * @author Patrick
 */
public class InfocardIdentity {

    private Token token = null;
    private X509Certificate certificate = null;
    private Map<String, Set<String>> claims = null;

    public InfocardIdentity(String samlToken, PrivateKey key)
            throws InfocardIdentityException {

        String errorMsg = null;

        try {
            token = new Token(samlToken, key);

            if (!token.isSignatureValid()) {
                errorMsg = Infocard.bundle.getString("invalidSignature");
                throw new InfocardIdentityException(errorMsg);
            }
            if (!token.isConditionsValid()) {
                errorMsg = Infocard.bundle.getString("invalidCondition");
                throw new InfocardIdentityException(errorMsg);
            }

            certificate = token.getCertificateOrNull();
            if (certificate != null) {
                if (!token.isCertificateValid()) {
                    errorMsg = Infocard.bundle.getString("invalidCertificate");
                    throw new InfocardIdentityException(errorMsg);
                }
            }
            claims = new HashMap<String, Set<String>>();
            Map tokenClaims = token.getClaims();
            Set keys = tokenClaims.keySet();
            Iterator keyIter = keys.iterator();
            while (keyIter.hasNext()) {
                String name = (String) keyIter.next();
                String value = ((String) tokenClaims.get(name)).trim();
                Set<String> valueSet;
                if (InfocardClaims.ISIP_CLAIMS.contains(name)) {
                    name = InfocardClaims.ISIP_CLAIM_SUFFIX + name;
                }
                if (claims.containsKey(name)) {
                    valueSet = claims.get(name);
                } else {
                    valueSet = new HashSet<String>();
                }
                valueSet.add(value);
                claims.put(name, valueSet);
            }
        } catch (InfoCardProcessingException e) {
            errorMsg = Infocard.bundle.getString("invalidToken");
            throw new InfocardIdentityException(errorMsg, e);
        }
    }

    public boolean areClaimsSupplied(Set claimUris) {

        return claims.keySet().containsAll(claimUris);
    }

    public boolean isClaimSupplied(String claimUri) {

        return claims.keySet().contains(claimUri);
    }

    public Set<String> getClaimValues(String claimUri) {

        return claims.get(claimUri);
    }

    public Map<String, Set<String>> getClaims() {

        return claims;
    }

    public String getClaimValue(String claimUri) {

        Iterator valueItr = claims.get(claimUri).iterator();
        if (valueItr.hasNext()) {
            return ((String) valueItr.next()).trim();
        } else {
            return null;
        }
    }

    public String getAudience() {

        return token.getAudience();
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public String getConfirmationMethod() throws InfocardIdentityException {
        try {
            return token.getConfirmationMethod();
        } catch (InfoCardProcessingException e) {
            String errorMsg = Infocard.bundle.getString("invalidToken");
            throw new InfocardIdentityException(errorMsg, e);
        }
    }

    public String getIssuer() throws InfocardIdentityException {

        try {
            return token.getIssuer();
        } catch (InfoCardProcessingException e) {
            String errorMsg = Infocard.bundle.getString("invalidToken");
            throw new InfocardIdentityException(errorMsg, e);
        }
    }
}
