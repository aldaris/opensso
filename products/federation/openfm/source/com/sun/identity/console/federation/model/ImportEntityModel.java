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
 * $Id: ImportEntityModel.java,v 1.2 2007-08-03 23:12:25 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.base.model.AMConsoleException;
import java.util.Set;
import java.util.Map;

public interface ImportEntityModel extends AMModel {
   
    public static final String REALM_NAME = "realmName";
    public static final String STANDARD_META = "standardMetaData";
    public static final String EXTENDED_META = "extendedMetaData";

    public void importEntity(Map data) throws AMConsoleException;
}

