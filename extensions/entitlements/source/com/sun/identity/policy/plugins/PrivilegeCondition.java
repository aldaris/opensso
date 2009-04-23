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
 * $Id: PrivilegeCondition.java,v 1.1 2009-04-23 23:29:19 veiming Exp $
 */

package com.sun.identity.policy.plugins;


import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.delegation.ResBundleUtils;
import com.sun.identity.policy.ConditionDecision;
import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.Syntax;
import com.sun.identity.policy.interfaces.Condition;
import com.sun.identity.shared.locale.AMResourceBundleCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;


/**
 * TOFIX
 */
public class PrivilegeCondition implements Condition {
    private static List propertyNames = new ArrayList(1);
    public static final String STATE = "privilegeConditionState";
    private String state;

    static {
        propertyNames.add(STATE);
    }

    public List getPropertyNames() {
        return (new ArrayList(propertyNames));
    }

    public Syntax getPropertySyntax(String property) {
        return Syntax.ANY;
    }

    public String getDisplayName(String property, Locale locale)
        throws PolicyException {
        ResourceBundle rb = AMResourceBundleCache.getInstance().getResBundle(
            ResBundleUtils.rbName, locale);
        return com.sun.identity.shared.locale.Locale.getString(rb, property);
    }

    public Set getValidValues(String property) throws PolicyException {
        return Collections.EMPTY_SET;
    }

    public void setProperties(Map properties) throws PolicyException {
        if ((properties != null) && !properties.isEmpty()) {
            String k = (String)properties.keySet().iterator().next();
            Set set = (Set)properties.get(k);
            String v = (String)set.iterator().next();
            state = k + "=" + v;
        }
    }

    public Map getProperties() {
        Map map = new HashMap(2);
        if (state != null) {
            int idx = state.indexOf("=");
            Set set = new HashSet(2);
            set.add(state.substring(idx+1));
            map.put(state.substring(0, idx), set);
        }
        return map;
    }

    public ConditionDecision getConditionDecision(SSOToken token, Map env)
        throws PolicyException, SSOException {
        return new ConditionDecision(false);
    }

    @Override
    public Object clone() {
        PrivilegeCondition theClone = null;
        try {
            theClone = (PrivilegeCondition)super.clone();
        } catch (CloneNotSupportedException e) {
            // this should never happen
            throw new InternalError();
        }
        theClone.state = state;
        return theClone;
    }
}
