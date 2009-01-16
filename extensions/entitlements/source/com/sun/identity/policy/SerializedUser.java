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
 * $Id: SerializedUser.java,v 1.1 2009-01-16 21:02:20 veiming Exp $
 * 
 */

package com.sun.identity.policy;

import com.sun.identity.policy.interfaces.Subject;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class serializes and deserializes Policy's User object.
 */

public class SerializedUser implements Serializable {
    private boolean isExclusive;
    private boolean isRealmSubject;
    private String subjectType;
    private Set<String> values;
    
    private static final long serialVersionUID = -403250971215465050L;
    
    public static SerializedUser serialize(
        boolean isExclusive,
        boolean isRealmSubject,
        Subject subject
    ) {
        SerializedUser serUser = new SerializedUser();
        serUser.isExclusive = isExclusive;
        serUser.isRealmSubject = isRealmSubject;
        
        if (!isRealmSubject) {
            serUser.subjectType= SubjectTypeManager.subjectTypeName(subject);
            serUser.values = new HashSet<String>();
            serUser.values.addAll(subject.getValues());
        }
        
        return serUser;
    }
}
