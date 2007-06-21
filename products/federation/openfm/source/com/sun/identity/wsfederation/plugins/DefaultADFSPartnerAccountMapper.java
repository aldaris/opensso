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
 * $Id: DefaultADFSPartnerAccountMapper.java,v 1.1 2007-06-21 23:01:42 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wsfederation.plugins;

import com.sun.identity.shared.debug.Debug;

import com.sun.identity.saml.assertion.Assertion;
import com.sun.identity.saml.assertion.Subject;
import com.sun.identity.saml.assertion.NameIdentifier;
import com.sun.identity.saml.assertion.Statement;
import com.sun.identity.saml.assertion.SubjectStatement;
import com.sun.identity.saml.common.SAMLUtils;
import com.sun.identity.wsfederation.plugins.PartnerAccountMapper;
import com.sun.identity.saml.protocol.SubjectQuery;
import com.sun.identity.sm.SMSEntry;
import com.sun.identity.wsfederation.common.WSFederationUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ap102904
 */
public class DefaultADFSPartnerAccountMapper implements PartnerAccountMapper {
    private static Debug debug = WSFederationUtils.debug;
    
    /** Creates a new instance of DefaultADFSPartnerAccountMapper */
    public DefaultADFSPartnerAccountMapper() {
    }

    /**
     * Returns user account in Sun Java System Access Manager to which the
     * subject in the assertion is mapped. This method will be called in POST
     * profile, ARTIFACT profile, AttributeQuery and AuthorizationDecisionQuery.
     *
     * @param assertions a list of authentication assertions returned from
     *                   partner side, this will contains user's identity in
     *                   the partner side. The object in the list will be
     *                   <code>com.sun.identity.saml.assertion.Assertion</code>
     * @param sourceID source ID for the site from which the subject
     *                 originated.
     * @param targetURL value for TARGET query parameter when the user
     *                  accessing the SAML aware servlet or post profile
     *                  servlet
     * @return Map which contains NAME, ORG and ATTRIBUTE keys, value of the
     *             NAME key is the user DN, value of the ORG is the user
     *             organization  DN, value of the ATTRIBUTE is a Map
     *             containing key/value pairs which will be set as properties
     *             on the Access manager SSO token, the key is the SSO
     *             property name, the value is a String value of the property.
     *             Returns empty map if the mapped user could not be obtained
     *             from the subject.
     */
    public Map getUser(List assertions, String sourceID, String targetURL) {
        String classMethod = "DefaultADFSPartnerAccountMapper:getUser(List) ";
        if ( debug.messageEnabled() )
        {
            debug.message(classMethod + "targetURL = " + targetURL);
        }

        Map map = new HashMap();
        Subject subject = null;
        Assertion assertion = (Assertion)assertions.get(0);
        Iterator iter = assertion.getStatement().iterator();
        while (iter.hasNext()) {
            Statement statement = (Statement)iter.next();
            if (statement.getStatementType() !=
                Statement.AUTHENTICATION_STATEMENT) {
                continue;
            }

            subject = ((SubjectStatement)statement).getSubject();
	}

        if (subject != null) {
            getUser(subject, sourceID, map);
            Map attrMap = new HashMap();
            SAMLUtils.addEnvParamsFromAssertion(attrMap, assertion, subject);
            if (!attrMap.isEmpty()) {
                map.put(ATTRIBUTE, attrMap);
            }
	}

        return map;
    }

    /**
     * Returns user account in Sun Java System Access Manager to which the
     * subject in the query is mapped. This method will be called in
     * AttributeQuery.The returned Map is subject to changes per SAML
     * specification.
     *
     * @param subjectQuery subject query returned from partner side,
     *                  this will contains user's identity in the partner side.
     * @param sourceID source ID for the site from which the subject
     *                 originated.
     * @return Map which contains NAME and ORG keys, value of the
     *             NAME key is the user DN, value of the ORG is the user
     *             organization  DN. Returns empty map if the mapped user
     *             could not be obtained from the subject.
     */
    public Map getUser(SubjectQuery subjectQuery,
                       String sourceID)
    {
        String classMethod = 
            "DefaultADFSPartnerAccountMapper:getUser(SubjectQuery) ";

        if ( debug.messageEnabled() )
        {
            debug.message(classMethod + "entered");
        }

        Map map = new HashMap();
        getUser(subjectQuery.getSubject(), sourceID, map);
        return map;
    }

    private void getUser(Subject subject, String sourceID, Map map) {
        String classMethod = 
            "DefaultADFSPartnerAccountMapper:getUser(Subject) ";
        
        // No need to check SSO in SubjectConfirmation here
        // since AssertionManager will handle it without calling account mapper
        NameIdentifier nameIdentifier = subject.getNameIdentifier();
        if (nameIdentifier != null) {
            // name comes as a upn of form login@domain, where login is windows 
            // login name - e.g. alansh, and domain is windows domain - 
            // e.g. adatum.com
            //
            // TODO - Need to search for an entry with configured attributes 
            // equal to login and domain rather than just create a DN!
            String upn = nameIdentifier.getName();
            // String rootSuffix = SystemPropertiesManager.get(
            // SAMLConstants.DEFAULT_ORG);
            String rootSuffix = SMSEntry.getRootSuffix();    
            if (upn != null && upn.length() > 0 ) {
                int atSign = upn.indexOf('@');
                if ( atSign == -1 )
                {
                    debug.error(classMethod + "No @ in name");
                }
                else
                {
                    String name = upn.substring(0,atSign);
                    String domain = upn.substring(atSign+1);
                    
                    // TODO - What to do with domain???
                    
                    if ( debug.messageEnabled() )
                    {
                        debug.message(classMethod + "name is "+name);
                    }
                    map.put(NAME, name); 
                }
            } else {
                debug.error(classMethod + "name is null");
            }
            if ( debug.messageEnabled() )
            {
                debug.message(classMethod + "org is "+rootSuffix);
            }
            map.put(ORG, rootSuffix); 
        }
    } 
}
