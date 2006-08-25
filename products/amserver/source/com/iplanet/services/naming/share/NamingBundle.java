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
 * $Id: NamingBundle.java,v 1.2 2006-08-25 21:19:56 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.services.naming.share;

import java.util.ResourceBundle;

public class NamingBundle {

    private static ResourceBundle namingBundle = null;

    public static String getString(String str) {
        if (namingBundle == null) {
            namingBundle = com.sun.identity.shared.locale.Locale
                .getInstallResourceBundle("amNaming");
        }
        return namingBundle.getString(str);
    }
}
