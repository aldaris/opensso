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
 * $Id: ApplicationCreateWizardHandler.java,v 1.2 2009-08-04 18:50:45 farble1670 Exp $
 */

package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.ApplicationWizardStep;
import com.sun.identity.admin.model.NameApplicationCreateWizardStepValidator;
import com.sun.identity.admin.model.ResourcesApplicationWizardStepValidator;

public class ApplicationCreateWizardHandler extends ApplicationWizardHandler {
    @Override
    public void initWizardStepValidators() {
        getWizardStepValidators()[ApplicationWizardStep.NAME.toInt()] = new NameApplicationCreateWizardStepValidator(getWizardBean());
        getWizardStepValidators()[ApplicationWizardStep.RESOURCES.toInt()] = new ResourcesApplicationWizardStepValidator(getWizardBean());
    }
}
