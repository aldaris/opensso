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

    public ConditionDecision evaluate(Subject subject, String resourceName,
        Map<String, Set<String>> environment) throws EntitlementException {
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
