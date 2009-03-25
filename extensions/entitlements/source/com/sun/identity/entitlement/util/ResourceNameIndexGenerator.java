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
 * $Id: ResourceNameIndexGenerator.java,v 1.7 2009-03-25 06:42:53 veiming Exp $
 */

package com.sun.identity.entitlement.util;

import com.sun.identity.entitlement.ResourceSaveIndexes;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class provides methods to generate index for URL resource name.
 */
public class ResourceNameIndexGenerator {
    private ResourceNameIndexGenerator() {
    }

    /**
     * Returns resource index of a given resource name.
     * 
     * @param resName Resource name.
     * @return resource index of a given resource name.
     */
    public static ResourceSaveIndexes getResourceIndex(String resName) {
        try {
            URL url = new URL(resName);
            Set<String> hostIndexes = new HashSet<String>();
            hostIndexes.add(getHostIndex(url));

            Set<String> pathIndexes = new HashSet<String>();
            String pathIndex = getPathIndex(url);
            pathIndexes.add(pathIndex);

            Set<String> pathParentIndexes = getPathParentIndexes(pathIndex);

            return new ResourceSaveIndexes(hostIndexes, pathIndexes,
                pathParentIndexes);
        } catch (MalformedURLException e) {
            Set<String> hostIndexes = new HashSet<String>();
            hostIndexes.add(resName);
            return new ResourceSaveIndexes(hostIndexes, Collections.EMPTY_SET,
                Collections.EMPTY_SET);
        }
    
    }
    
    private static String getHostIndex(URL url) {
        String host = url.getHost().toLowerCase();
        int idx = host.lastIndexOf("*");
        if (idx != -1) {
            int dotIdx = host.indexOf('.', idx);
            host = (dotIdx != -1) ? host.substring(dotIdx) : "";
        }
        return url.getProtocol().toLowerCase() + "://" + host;
    }
    
    private static String getPathIndex(URL url) {
        String path = url.getPath().toLowerCase();
        String query = url.getQuery();
        if (query == null) {
            query = "";
        } else {
            query = query.toLowerCase();
        }

        int idx = path.indexOf("*");
        if (idx != -1) {
            int slashIdx = path.lastIndexOf('/', idx);
            path = path.substring(0, slashIdx + 1);
            query = "";
        }

        if (query.length() > 0) {
            Map<String, Set<String>> map =
                new HashMap<String, Set<String>>();
            StringTokenizer st = new StringTokenizer(query, "&");
            while (st.hasMoreTokens()) {
                String tok = st.nextToken();
                int i = tok.indexOf("=");
                String key = (i != -1) ? tok.substring(0, i) : tok;
                String val = (i != -1) ? tok.substring(i + 1) : "";

                Set<String> set = map.get(key);
                if (set == null) {
                    set = new HashSet<String>();
                    map.put(key, set);
                }
                set.add(val);
            }

            boolean queryHasWildCard = false;
            for (Iterator i = map.keySet().iterator();
                i.hasNext() && !queryHasWildCard;) {
                String s = (String) i.next();
                queryHasWildCard = (s.indexOf('*') != -1);
            }

            if (queryHasWildCard) {
                query = "";
            } else {
                List<String> list = new ArrayList<String>();
                for (String key : map.keySet()) {
                    Set<String> val = map.get(key);

                    boolean wildcard = false;
                    for (Iterator i = val.iterator(); i.hasNext() && !wildcard;)
                    {
                        String s = (String) i.next();
                        wildcard = (s.indexOf('*') != -1);
                    }

                    if (wildcard) {
                        list.add(key + "=");
                    } else {
                        for (String s : val) {
                            list.add(key + "=" + s);
                        }
                    }
                }
                query = ResourceNameSplitter.queryToString(list);
            }
        }

        return (query.length() > 0) ? path + "?" + query : path;
    }
    
    private static Set<String> getPathParentIndexes(String pathIndex) {
        Set<String> parents = new HashSet<String>();
        int idx = pathIndex.indexOf("?");
        
        /*
         * add a dummy so that the last path will be included. e.g.
         * /a/b/c?q=1, we want /a/b/c to be include too.
         * but if there are no query parameter, e.g. /a/b/c. we only want
         * /a/b
         */
        String str = (idx != -1) ? pathIndex.substring(0, idx) + "/dummy" 
            : pathIndex;
        
        StringTokenizer st = new StringTokenizer(str, "/");
        StringBuffer tracker = new StringBuffer();
        tracker.append("/");
        while (st.hasMoreElements()) {
            String s = st.nextToken();
            // drop the last token
            if (st.hasMoreElements()) {
                tracker.append(s);
                parents.add(tracker.toString());
                tracker.append("/");
            }
        }
        
        if (parents.isEmpty()) {
            parents.add("/");
        }
        return parents;
    }
}
