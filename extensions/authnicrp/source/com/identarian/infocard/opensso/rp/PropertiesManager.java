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
 * $Id: PropertiesManager.java,v 1.2 2008-02-28 23:31:19 superpat7 Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2008 Patrick Petit Consulting
 */

package com.identarian.infocard.opensso.rp;

import com.sun.identity.authentication.service.AuthD;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletContext;


public class PropertiesManager {

    private Properties properties = new Properties();

    public PropertiesManager(String file) throws IOException {

        // There sould be a better way of doing it... ?
        ServletContext context = AuthD.getAuth().getServletContext();
        InputStream is = context.getResourceAsStream(file);
        properties.load(is);

    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }
}
