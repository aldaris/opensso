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
 * $Id: Step6.java,v 1.3 2008-01-15 19:59:00 jefberpe Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;

import com.sun.identity.config.pojos.LDAPStore;
import net.sf.click.Page;

/**
 * @author Les Hazlewood
 */
public class Step6 extends Page {

    public Step6(){}

    public void onInit() {
        super.onInit();
        String newInstanceUrl = (String)getContext().getSessionAttribute( Step2.NEW_INSTANCE_URL_SESSION_KEY );
        add( "newInstanceUrl", newInstanceUrl );

        LDAPStore configStore = (LDAPStore)getContext().getSessionAttribute( Step3.LDAP_STORE_SESSION_KEY );
        add( "configStore", configStore );

        LDAPStore userStore = (LDAPStore)getContext().getSessionAttribute( Step4.LDAP_STORE_SESSION_KEY );
        add( "userStore", userStore );

        String loadBalancerHost = (String)getContext().getSessionAttribute( Step5.LOAD_BALANCER_HOST_SESSION_KEY );
        add( "loadBalancerHost", loadBalancerHost );
        Integer loadBalancerPort = (Integer)getContext().getSessionAttribute( Step5.LOAD_BALANCER_PORT_SESSION_KEY );
        add( "loadBalancerPort", loadBalancerPort );
    }

    protected void add( String key, Object value ) {
        if ( value != null ) {
            addModel( key, value );
        }
    }
}
