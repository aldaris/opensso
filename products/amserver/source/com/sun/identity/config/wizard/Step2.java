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
 * $Id: Step2.java,v 1.3 2008-01-15 19:59:00 jefberpe Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;

import com.sun.identity.config.util.AjaxPage;
import net.sf.click.control.ActionLink;

/**
 * @author Les Hazlewood
 */
public class Step2 extends AjaxPage {

    public static final String NEW_INSTANCE_URL_SESSION_KEY = "wizardNewInstanceUrl";

    public ActionLink clearLink = new ActionLink( "clear", this, "clear" );
    public ActionLink checkNewInstanceUrlLink = new ActionLink( "checkNewInstanceUrl", this, "checkNewInstanceUrl" );

    public Step2() {

    }

    public void onInit() {
        super.onInit();
        String newInstanceUrl = (String)getContext().getSessionAttribute( NEW_INSTANCE_URL_SESSION_KEY );
        if ( newInstanceUrl != null ) {
            addModel( "newInstanceUrl", newInstanceUrl );
        }
    }

    public boolean clear() {
        getContext().removeSessionAttribute( NEW_INSTANCE_URL_SESSION_KEY );
        setPath( null );
        return false;
    }

    public boolean checkNewInstanceUrl() {
        String newInstanceUrl = toString( "newInstanceUrl" );
        if ( newInstanceUrl == null ) {
            writeToResponse( super.getLocalizedString("configuration.wizard.step2.tooltip") );
        } else {
            try {
                getConfigurator().testNewInstanceUrl( newInstanceUrl );
                //it is valid, save to http session for access at end of wizard
                getContext().setSessionAttribute( NEW_INSTANCE_URL_SESSION_KEY, newInstanceUrl );
                writeToResponse( "true" );
            } catch ( Exception e ) {
                writeToResponse( e.getMessage() );
            }
        }

        setPath( null );
        return false;
    }
}
