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
 * $Id: PermissionMapReader.java,v 1.1 2009-11-10 19:29:05 farble1670 Exp $
 */
package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.AccessLevel;
import com.sun.identity.admin.model.Permission;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PermissionMapReader {

    private static final Pattern LINE_PATTERN = Pattern.compile("^(.*)\\.(.*)=(.*)$");

    public Map<String, Map<AccessLevel, Set<Permission>>> read() {
        Map<String, Map<AccessLevel, Set<Permission>>> permissionMap = new HashMap<String, Map<AccessLevel, Set<Permission>>>();

        InputStream is = getClass().getResourceAsStream("/admin-permission-map.properties");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.length() == 0 || line.startsWith("#")) {
                    continue;
                }
                Matcher matcher = LINE_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    throw new AssertionError("invalid permission map line: " + line);
                }
                String app = matcher.group(1);
                AccessLevel ac = AccessLevel.valueOf(matcher.group(2));
                Permission p = Permission.valueOf(matcher.group(3));

                Map<AccessLevel, Set<Permission>> accessMap = permissionMap.get(app);
                if (accessMap == null) {
                    accessMap = new HashMap<AccessLevel, Set<Permission>>();
                    permissionMap.put(app, accessMap);
                }
                Set<Permission> permissionSet = accessMap.get(ac);
                if (permissionSet == null) {
                    permissionSet = new HashSet<Permission>();
                    accessMap.put(ac, permissionSet);
                }
                permissionSet.add(p);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        return permissionMap;
    }
}
