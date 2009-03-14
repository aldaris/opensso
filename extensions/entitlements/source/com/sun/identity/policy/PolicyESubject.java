/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.identity.policy;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.SubjectDecision;
import com.sun.identity.entitlement.SubjectResult;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * <code>com.sun.identity.EntitlementSubject</code> warapper for
 * <code>com.sun.identity.policy.Subject</code>
 */
public class PolicyESubject implements EntitlementSubject {

    Set nqSubjects;

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
    public Set getPSubjects() {
        return nqSubjects;
    }

    /**
     * Sets nested <code>com.sun.identity.policy.Subject</code>
     */
    public void setPSubjects(Set nqSubjects) {
        this.nqSubjects = nqSubjects;
    }

    /**
     * Returns <code>SubjectDecision</code> of
     * <code>EntitlementSubject</code> evaluation
     * @param subject EntitlementSubject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return <code>SubjectDecision</code> of
     * <code>EntitlementSubject</code> evaluation
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

