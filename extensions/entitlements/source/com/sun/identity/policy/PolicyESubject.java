/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.identity.policy;

import com.sun.identity.entitlement.ESubject;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.SubjectDecision;
import com.sun.identity.entitlement.SubjectResult;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * <code>com.sun.identity.ESubject</code> warapper for
 * <code>com.sun.identity.policy.Subject</code>
 */
public class PolicyESubject implements ESubject {

    com.sun.identity.policy.interfaces.Subject pSubject;

    /**
     * Sets state of the object
     * @param state State of the object encoded as string
     */
    public void setState(String state) {
    }

    /**
     * Returns state of the object
     * @return state of the object encoded as string
     */
    public String getState() {
        return null;
    }

    /**
     * Returns nested <code>com.sun.identity.policy.Subject</code>
     * @return nested <code>com.sun.identity.policy.Subject</code>
     */
    public com.sun.identity.policy.interfaces.Subject getPSubject() {
        return pSubject;
    }

    /**
     * Sets nested <code>com.sun.identity.policy.Subject</code>
     */
    public void setPSubject(
            com.sun.identity.policy.interfaces.Subject pSubject) {
        this.pSubject = pSubject;
    }

    /**
     * Returns <code>SubjectDecision</code> of
     * <code>ESubject</code> evaluation
     * @param subject ESubject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return <code>SubjectDecision</code> of
     * <code>ESubject</code> evaluation
     * @throws com.sun.identity.entitlement,  EntitlementException in case
     * of any error
     */
    public SubjectDecision evaluate(
            Subject subject,
            String resourceName,
            Map<String, Set<String>> environment)
            throws EntitlementException {
        return null;
    }
}

