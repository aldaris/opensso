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
 * $Id: Step4.java,v 1.3 2008-01-15 19:59:00 jefberpe Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;

/**
 * @author Les Hazlewood
 */
public class Step4 extends LDAPStoreWizardPage {

    public static final String LDAP_STORE_SESSION_KEY = "wizardCustomUserStore";

    public Step4() {

        setType("user");
        setTypeTitle( "User" );
        setPageNum(4);
        setStoreSessionName( LDAP_STORE_SESSION_KEY );
    }
    
    public void onInit() {
        super.onInit();
        setPath("/config/wizard/step3.htm"); //uses the same template.  The rendered page is changed based on the above 3 props.
    }
}
