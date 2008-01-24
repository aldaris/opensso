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
 * $Id: Step5.java,v 1.5 2008-01-24 20:26:40 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.wizard;

import com.sun.identity.config.util.AjaxPage;
import com.sun.identity.setup.AMSetupServlet;
import net.sf.click.control.ActionLink;

public class Step5 extends AjaxPage {

    public static final String LOAD_BALANCER_HOST_SESSION_KEY = 
        "wizardLoadBalancerHostName";
    public static final String LOAD_BALANCER_PORT_SESSION_KEY = 
        "wizardLoadBalancerPort";

    public ActionLink clearLink = new ActionLink("clear", this, "clear");
    public ActionLink validateLink = new ActionLink("validate",this,"validate");

    public Step5() {}

    public void onInit() {
        String host = (String)getContext().getSessionAttribute(
            LOAD_BALANCER_HOST_SESSION_KEY);
        Integer port = (Integer)getContext().getSessionAttribute( 
            LOAD_BALANCER_PORT_SESSION_KEY);
        
        if ( host != null ) {
            addModel("host", host);
        }
        if ( port != null ) {
            addModel("port", port);
        }
        super.onInit();
    }

    public boolean clear() {
        getContext().removeSessionAttribute(LOAD_BALANCER_HOST_SESSION_KEY);
        getContext().removeSessionAttribute(LOAD_BALANCER_PORT_SESSION_KEY);
        setPath(null);
        return false;
    }

    public boolean validate() {
        String host = toString("host");
        if (host == null) {
            writeInvalid(getLocalizedString("missing.host.name"));
        } else {
            int port = toInt("port");
            if (port > 65535) {
                writeInvalid(getLocalizedString("invalid.port.number"));
            } else {
                // test host for access( host, port );
                if (AMSetupServlet.canUseAsPort(host, port)) {
                    writeValid("OK");
                    getContext().setSessionAttribute( LOAD_BALANCER_HOST_SESSION_KEY, host );
                    getContext().setSessionAttribute( LOAD_BALANCER_PORT_SESSION_KEY, Integer.valueOf( port ) );
                } else {
                    writeInvalid(getLocalizedString("contact.host.failed"));
                }
            }
        }

        setPath(null);
        return false;
    }
}
