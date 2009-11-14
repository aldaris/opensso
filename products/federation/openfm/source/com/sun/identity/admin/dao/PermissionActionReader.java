/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: PermissionActionReader.java,v 1.1 2009-11-14 00:36:39 farble1670 Exp $
 */
package com.sun.identity.admin.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PermissionActionReader {

    public List<PermissionAction> read() {
        List<PermissionAction> pas = new ArrayList<PermissionAction>();

        InputStream is = getClass().getResourceAsStream("/admin-permission-actions.properties");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.length() == 0 || line.startsWith("#")) {
                    continue;
                }
                PermissionAction pa = new PermissionAction(line);
                pas.add(pa);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        return pas;
    }
}
