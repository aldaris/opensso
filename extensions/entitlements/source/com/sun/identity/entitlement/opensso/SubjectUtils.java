/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: SubjectUtils.java,v 1.1 2009-04-09 13:15:03 veiming Exp $
 */

package com.sun.identity.entitlement.opensso;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.internal.server.AuthSPrincipal;
import com.sun.identity.shared.Constants;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import javax.security.auth.Subject;

/**
 *
 * @author dennis
 */
public class SubjectUtils {
    private SubjectUtils() {
    }

    public static Subject createSubject(SSOToken token) {
        try {
            Principal userP = new AuthSPrincipal(token.getTokenID().toString());
            Set<Principal> userPrincipals = new HashSet<Principal>(2);
            userPrincipals.add(userP);
            String uuid = token.getProperty(Constants.UNIVERSAL_IDENTIFIER);
            userPrincipals.add(new AuthSPrincipal(uuid));

            Set<SSOToken> privateCred = new HashSet<SSOToken>();
            privateCred.add(token);
            return new Subject(false, userPrincipals, new HashSet(),
                privateCred);
        } catch (SSOException ex) {
            return null; //TOFIX
        }
    }

    public static SSOToken getSSOToken(Subject subject) {
        Set privCreds = subject.getPrivateCredentials();
        return ((privCreds != null) && !privCreds.isEmpty()) ?
            (SSOToken)privCreds.iterator().next() : null;
    }
}
