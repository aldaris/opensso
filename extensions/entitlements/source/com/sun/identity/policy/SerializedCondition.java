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
 * $Id: SerializedCondition.java,v 1.2 2009-01-17 02:08:46 veiming Exp $
 * 
 */

package com.sun.identity.policy;

import com.sun.identity.policy.interfaces.Condition;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class serializes and deserializes Policy's Condition object.
 */
public class SerializedCondition implements Serializable {
    private String type;
    private Map<String, Set<String>> map;
    
    private static final long serialVersionUID = -403250971215465050L;
    
    public static SerializedCondition serialize(Condition condition) {
        SerializedCondition serCondition = new SerializedCondition();
        serCondition.type = ConditionTypeManager.conditionTypeName(
            condition);
        Map properties = condition.getProperties();
        if (properties != null) {
            serCondition.map = new HashMap<String, Set<String>>();
            serCondition.map.putAll(properties);
        }
        return serCondition;
    }
 
    public static Condition deserialize(
        PolicyManager pm,
        SerializedCondition serCondition) {
        try {
            ConditionTypeManager mgr = pm.getConditionTypeManager();
            Condition condition = mgr.getCondition(serCondition.type);
            condition.setProperties(serCondition.map);
            return condition;
        } catch (PolicyException ex) {
            return null;
        }
    }
}
