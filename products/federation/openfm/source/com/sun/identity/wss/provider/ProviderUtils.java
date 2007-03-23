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
 * $Id: ProviderUtils.java,v 1.1 2007-03-23 00:01:51 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.provider;

import java.util.ResourceBundle;

import com.iplanet.am.util.Locale;
import com.sun.identity.shared.debug.Debug;

public class ProviderUtils {

     static ResourceBundle bundle = null;
     public static Debug debug = Debug.getInstance("fmWSSProvider");
     
     static {
            bundle = Locale.getInstallResourceBundle("fmWSSProvider");
     }

}

