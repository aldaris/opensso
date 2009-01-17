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
 * $Id: SerializedResponseProvider.java,v 1.2 2009-01-17 02:08:46 veiming Exp $
 * 
 */

package com.sun.identity.policy;

import com.sun.identity.policy.interfaces.ResponseProvider;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class serializes and deserializes Policy's Response Provider object.
 */
public class SerializedResponseProvider implements Serializable {
    private String type;
    private Map<String, Set<String>> map;
    
    private static final long serialVersionUID = -403250971215465050L;
    
    public static SerializedResponseProvider serialize(
        ResponseProvider responseProvider
    ) {
        SerializedResponseProvider serResProvider = 
            new SerializedResponseProvider();
        serResProvider.type = 
            ResponseProviderTypeManager.responseProviderTypeName(
            responseProvider);
        Map properties = responseProvider.getProperties();
        if (properties != null) {
            serResProvider.map = new HashMap<String, Set<String>>();
            serResProvider.map.putAll(properties);
        }
        return serResProvider;
    }
    
    public static ResponseProvider deserialize(
        PolicyManager pm,
        SerializedResponseProvider serResponseProvider) {
        try {
            ResponseProviderTypeManager mgr = 
                pm.getResponseProviderTypeManager();
            ResponseProvider rp  = mgr.getResponseProvider(
                serResponseProvider.type);
            rp.setProperties(serResponseProvider.map);
            return rp;
        } catch (PolicyException ex) {
            return null;
        }
    }
}
