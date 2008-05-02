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
 * $Id: JMQSAML2Repository.java,v 1.1 2008-05-02 21:46:27 weisun2 Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.saml2.plugins;


/**
 * This class is used in SAML2 failover mode to store/recover serialized
 * state of IDPSession/Response object.
 */
public interface JMQSAML2Repository {

   /**
    * Retrives existing SAML2 object from persistent datastore
    * @param samlKey primary key 
    * @return SAML2 object, if failed, return null. 
    */
   public Object retrieve(String samlKey);

   /**
    * Deletes the SAML2 object by given primary key from the repository
    * @param samlKey primary key 
    */
   public void delete(String samlKey);

    /**
     * Deletes expired SAML2 object from the repository
     * @exception When Unable to delete the expired SAML2 object
     */
    public void deleteExpired();

   /**
    * Saves SAML2 data into the SAML2 Repository
    * @param samlKey primary key 
    * @param samlObj saml object such as Response, IDPSession
    * @param expirationTime expiration time 
    */
    public void save(String samlKey, Object samlObj, long expirationTime);
}
