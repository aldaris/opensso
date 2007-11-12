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
 * $Id: Wizard.java,v 1.2 2007-11-12 14:51:13 lhazlewood Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;

import com.sun.identity.config.pojos.LDAPStore;
import com.sun.identity.config.util.AjaxPage;
import net.sf.click.control.ActionLink;

/**
 * @author Les Hazlewood
 */
public class Wizard extends AjaxPage {

    public int startingTab = 1;

    public ActionLink createConfigLink = new ActionLink("createConfig", this, "createConfig" );
    public ActionLink testUrlLink = new ActionLink("testNewInstanceUrl", this, "testNewInstanceUrl" );
    public ActionLink pushConfigLink = new ActionLink("pushConfig", this, "pushConfig" );

    public void onInit() {
        startingTab = Step1.isSkipped( getContext() ) ? 2 : 1;
    }

    /**
     * This is the 'execute' operation for the entire wizard.  This method aggregates all data submitted across the 
     * wizard pages here in one lump and hands it off to the back-end for processing.
     */
    public boolean createConfig() {

        String newInstanceUrl = (String)getContext().getSessionAttribute( Step2.NEW_INSTANCE_URL_SESSION_KEY );
        LDAPStore configStore = (LDAPStore)getContext().getSessionAttribute( Step3.LDAP_STORE_SESSION_KEY );
        LDAPStore userStore = (LDAPStore)getContext().getSessionAttribute( Step4.LDAP_STORE_SESSION_KEY );
        String loadBalancerHost = (String)getContext().getSessionAttribute( Step5.LOAD_BALANCER_HOST_SESSION_KEY );
        Integer loadBalancerPort = (Integer)getContext().getSessionAttribute( Step5.LOAD_BALANCER_PORT_SESSION_KEY );

        int port = ( loadBalancerPort != null ? loadBalancerPort.intValue() : 0 );

        getConfigurator().writeConfiguration( newInstanceUrl, configStore, userStore, loadBalancerHost, port );

        writeToResponse("true");

        setPath(null);
        return false;
    }

    /**
     * Supports FAM 8.0 Final wireframes, page 52.
     */
    public boolean testNewInstanceUrl() {
        String newInstanceUrl = (String)getContext().getSessionAttribute( Step2.NEW_INSTANCE_URL_SESSION_KEY );
        if ( newInstanceUrl == null ) {
            throw new IllegalStateException( "This method should only be called by html/javascript after a user " +
                "has specified a 'New Instance URL'" );
        }

        try {
            getConfigurator().testNewInstanceUrl( newInstanceUrl );
            writeToResponse("true");
        } catch ( Exception e ) {
            writeToResponse(e.getMessage());
        }

        setPath(null);
        return false;
    }

    /**
     * In a multi-instance configuration, this method will push the the new instance configuration to that instance.
     */
    public boolean pushConfig() {

        String newInstanceUrl = (String)getContext().getSessionAttribute( Step2.NEW_INSTANCE_URL_SESSION_KEY );
        if ( newInstanceUrl == null ) {
            throw new IllegalStateException( "This method should only be called by html/javascript after a user " +
                "has specified a 'New Instance URL' in a multi-instance configuration." );
        }

        try {
            getConfigurator().pushConfiguration( newInstanceUrl );
            writeToResponse("true");
        } catch ( Exception e ) {
            writeToResponse(e.getMessage());
        }

        setPath(null);
        return false;
    }
}
