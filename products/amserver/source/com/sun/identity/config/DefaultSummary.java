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
 * $Id: DefaultSummary.java,v 1.2 2007-11-12 14:51:14 lhazlewood Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config;

import com.sun.identity.config.util.TemplatedPage;
import net.sf.click.control.ActionLink;

/**
 * @author Les Hazlewood
 */
public class DefaultSummary extends TemplatedPage {

    public ActionLink createDefaultConfigLink = new ActionLink("createDefaultConfig", this, "createDefaultConfig" );

    protected String getTitle() {
        return "defaultSummary.title";
    }

    public boolean createDefaultConfig() {
        try {
            getConfigurator().writeConfiguration();
            writeToResponse("true");
        } catch( Exception e ) {
            writeToResponse(e.getMessage());
        }

        setPath(null); //ajax call - response rendered directly - prevent Velocity template from being rendered.
        return false; //prevent the request from continuing to other click components.
    }

}
