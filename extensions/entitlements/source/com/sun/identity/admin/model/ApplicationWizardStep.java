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
 * $Id: ApplicationWizardStep.java,v 1.1 2009-07-22 16:40:09 farble1670 Exp $
 */

package com.sun.identity.admin.model;

import java.util.HashMap;
import java.util.Map;

public enum ApplicationWizardStep {

    NAME(0),
    ACTIONS(1),
    CONDITIONS(2),
    OVERRIDE(3),
    SUMMARY(4);

    private final int stepNumber;
    private static final Map<Integer, ApplicationWizardStep> intValues = new HashMap<Integer, ApplicationWizardStep>() {
        {
            put(NAME.toInt(), NAME);
            put(ACTIONS.toInt(), ACTIONS);
            put(CONDITIONS.toInt(), CONDITIONS);
            put(OVERRIDE.toInt(), OVERRIDE);
            put(SUMMARY.toInt(), SUMMARY);
        }
    };

    ApplicationWizardStep(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public int toInt() {
        return stepNumber;
    }

    public static ApplicationWizardStep valueOf(int i) {
        return intValues.get(Integer.valueOf(i));
    }
}