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
 * $Id: GetBackendDataStore.java,v 1.1 2007-02-20 22:42:12 goodearth Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

import com.iplanet.sso.SSOToken;
import com.sun.identity.shared.debug.Debug;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that implements to get backend datastore based on the vendor
 * information in the schema of the respective ldapv3 based database.
 * Code is to be added in future for other directory server support
 * like openldap.
 */
public class GetBackendDataStore {

    public static Debug debug = Debug.getInstance("amSMS");

    public GetBackendDataStore() {
    }

   public static String getDataStore(SSOToken token) {
       String dataStore = "flatfile";
       String srchBaseDN = "cn=7-bit check,cn=plugins,cn=config";
       String filter = "nsslapd-pluginVendor=Sun Microsystems, Inc.";
       Set results = new HashSet();
       try {
           results = SMSEntry.search(token, srchBaseDN, filter);
           if (results != null) {
               dataStore = "dirServer";
           }
       } catch (SMSException smse) {
           // Use filter and search in Active Directory.
           srchBaseDN =
               "CN=nTDSService-Display,CN=409,CN=DisplaySpecifiers,CN=Configuration," + SMSEntry.baseDN;
           filter = "classDisplayName=Active Directory Service";
           try {
               results = SMSEntry.search(token, srchBaseDN, filter);
               if (results != null) {
                   dataStore = "activeDir";
               }
           } catch (SMSException se) {
               // Default is flatfile.
           }
       }
       return dataStore;
    }


}
