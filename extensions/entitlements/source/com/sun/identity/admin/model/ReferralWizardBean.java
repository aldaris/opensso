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
 * $Id: ReferralWizardBean.java,v 1.2 2009-06-04 21:41:44 farble1670 Exp $
 */

package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;

public abstract class ReferralWizardBean extends WizardBean {
    private ReferralBean referralBean;
    private Effect nameInputEffect;

    public ReferralWizardBean() {
        super();
        reset();
    }

    public abstract boolean isNameEditable();

    public ReferralBean getReferralBean() {
        return referralBean;
    }

    @Override
    public void reset() {
        super.reset();
        referralBean = new ReferralBean();
    }

    public Effect getNameInputEffect() {
        return nameInputEffect;
    }

    public void setNameInputEffect(Effect nameInputEffect) {
        this.nameInputEffect = nameInputEffect;
    }
}
