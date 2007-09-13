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
 * $Id: FAMSTSAttributeProvider.java,v 1.2 2007-09-13 16:19:29 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.sts.spi;

import javax.security.auth.Subject;
import com.sun.xml.ws.api.security.trust.*;
import java.security.Principal;
import java.util.*;
import javax.xml.namespace.*;
import com.iplanet.security.x509.CertUtils;
import java.security.cert.X509Certificate;

public class FAMSTSAttributeProvider implements STSAttributeProvider {
    
    public Map<QName, List<String>> getClaimedAttributes(Subject subject, String appliesTo, String tokenType, Claims claims)
    {
        String name = null; 

        Iterator iter = subject.getPublicCredentials().iterator();
        Object  object = iter.next();
        if(object instanceof X509Certificate) {
           X509Certificate cert = (X509Certificate)object;
           name = CertUtils.getSubjectName(cert); 
        }
        
        Set<Principal> principals = subject.getPrincipals();
        if (principals != null){
            final Iterator iterator = principals.iterator();
            while (iterator.hasNext()){
                String cnName = principals.iterator().next().getName();
                int pos = cnName.indexOf("=");
                name = cnName.substring(pos+1);
                break;
            }       
        }
        
	Map<QName, List<String>> attrs = new HashMap<QName, List<String>>();

	QName nameIdQName = 
            new QName("http://sun.com",STSAttributeProvider.NAME_IDENTIFIER);
	List<String> nameIdAttrs = new ArrayList<String>();
	nameIdAttrs.add(getUserPseduoName(name));
	attrs.put(nameIdQName,nameIdAttrs);

	QName testQName = new QName("http://sun.com","Role");
	List<String> testAttrs = new ArrayList<String>();
	testAttrs.add(getUserRole(name));
	attrs.put(testQName,testAttrs);

	return attrs;
    }  
    
    private String getUserPseduoName(String userName){
        
        if ("jsmith".equals(userName)){
            return "jsmith";
        }
        
        if ("jondoe".equals(userName)){
            return "jondoe";
        }
        
        return userName;
    }
    
    private String getUserRole(String userName){
        if ("jsmith".equals(userName)){
            return "gold ";
        }
        
        if ("jondoe".equals(userName)){
            return "silver";
        }
        
        return "wsc";
    }
}
