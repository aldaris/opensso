/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: Entitlement.java,v 1.1 2008-12-11 17:13:41 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.util.Map;
import java.util.Set;

/**
 * This class encapsulates entitlement of a subject.
 */ 
public class Entitlement {
    private String actionName;
    private Set values;
    private long timeToLive = Long.MAX_VALUE;
    private Map advices;

    public Entitlement() {
    }

    public String getActionName() {
        return actionName;
    }

    public Set getValues() {
        return values;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public Map getAdvices() {
        return advices;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void setValues(Set values) {
        this.values = values;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public void getAdvices(Map advices) {
        this.advices = advices;
    }
}
