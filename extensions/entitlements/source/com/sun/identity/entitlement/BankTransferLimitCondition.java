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
 * $Id: BankTransferLimitCondition.java,v 1.3 2009-06-04 11:49:19 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.security.auth.Subject;

public class BankTransferLimitCondition implements EntitlementCondition {
    
    public static final String MAX_TRANSFER_LIMIT =
        "banking.funds.maxTransferLimit";
    public static final String MIN_TRANSFER_LIMIT =
        "banking.funds.minTransferLimit";

    private int transferLimit;
    private String limitType;

    public ConditionDecision evaluate(
        String realm, 
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment
    ) throws EntitlementException {
        ConditionDecision result = new ConditionDecision(false, null);
        if (environment != null) {
            Set<String> values = environment.get(MAX_TRANSFER_LIMIT);
            if (values != null && !values.isEmpty()) {
                String valueString = values.iterator().next();
                try {
                    int value = Integer.parseInt(valueString);
                    if (value <= getTransferLimit()) {
                        result = new ConditionDecision(true, null);
                    }
                } catch (NumberFormatException nfe) {
                    // Ignore, decision will be false
                }
            }
            values = environment.get(MIN_TRANSFER_LIMIT);
            if (values != null && !values.isEmpty()) {
                String valueString = values.iterator().next();
                try {
                    int value = Integer.parseInt(valueString);
                    if (value <= getTransferLimit()) {
                        result = new ConditionDecision(true, null);
                    }
                } catch (NumberFormatException nfe) {
                    // Ignore, decision will be false
                }
            }
        }
        return result;
    }

    public String getState() {
        return (getLimitType() + "=" + getTransferLimit());
    }

    public void setState(String state) {
        StringTokenizer st = new StringTokenizer(state, "=");
        if (st.hasMoreTokens()) {
            // Get the key
            setLimitType(st.nextToken());
        }
        // Get the transfer amount
        if (st.hasMoreTokens()) {
            String valueString = st.nextToken();
            try {
                setTransferLimit(Integer.parseInt(valueString));
            } catch (NumberFormatException nfe) {
                //Ignore, value will be zero
            }
        }
    }

    public void setTransferLimit(int transferLimit) {
        this.transferLimit = transferLimit;
    }

    public int getTransferLimit() {
        return transferLimit;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }
}
