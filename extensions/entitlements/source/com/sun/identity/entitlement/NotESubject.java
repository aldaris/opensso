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
 * $Id: NotESubject.java,v 1.2 2009-02-27 06:05:15 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * ESubject wrapped on an ESubject object to provide boolean NOT wrapper.
 * Membership of NotESubject is satisfied in the user is not a member of
 * the nested ESubject
 * @author dorai
 */
public class NotESubject implements ESubject {

    private ESubject eSubject;
    private String pSubjectName;
    
    /**
     * Constructs NotESubject
     */
    public NotESubject() {
    }

    /**
     * Constructs NotESubject
     * @param eSubject nested ESubject
     */
    public NotESubject(ESubject eSubject) {
    }

    /**
     * Constructs NotESubject
     * @param eSubject eSubject nested ESubject
     * @param pSubjectName subject name as used in OpenSSO policy,
     * this is releavant only when NotrESubject was created from
     * OpenSSO policy Subject
     */
    public NotESubject(ESubject eSubject, String pSubjectName) {
    }

    /**
     * Sets state of the object
     * @param state State of the object encoded as string
     */
    public void setState(String state) {
        //TODO
    }

    /**
     * Returns state of the object
     * @return state of the object encoded as string
     */
    public String getState() {
        return toString();
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

    /**
     * Sets nested ESubject
     * @param eSubject nested ESubject
     */
    public void setESubject(ESubject eSubject) {
        this.eSubject = eSubject;
    }

     /**
     * Returns nested ESubject
     * @return nested ESubject
     */
    public ESubject getESubject() {
        return eSubject;
    }

     /**
     * Sets OpenSSO policy Subject name
     * @param pSubjectName subject name as used in OpenSSO policy,
     * this is releavant only when NotrESubject was created from
     * OpenSSO policy Subject
     */
    public void setPSubjectName(String pSubjectName) {
        this.pSubjectName = pSubjectName;
    }

     /**
     * Returns OpenSSO policy Subject name
     * @return subject name as used in OpenSSO policy,
     * this is releavant only when NotrESubject was created from
     * OpenSSO policy Subject
     */
    public String getPSubjectName() {
        return pSubjectName;
    }
}
