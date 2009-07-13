/* The contents of this file are subject to the terms
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
 * $Id:
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.qaweb.beans;

import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

/**
 * This class acts as a valuechangelistener for authentication related
 * properties
 */
public class authenticationValueChangeListener implements ValueChangeListener {

    /**
     * This method checks if  LDAP details for authentication are same as
     * UMConfig details.
     */
    public void processValueChange(ValueChangeEvent event)
            throws AbortProcessingException {
        Map map = FacesContext.getCurrentInstance().getExternalContext().
                getSessionMap();
            ModuleBean mbean = (ModuleBean) map.get("ModuleBean");       
        if (!event.getOldValue().equals(event.getNewValue())) {
            mbean.setIsAuthSameAsUM(false);
        }else if (event.getOldValue().equals(event.getNewValue())) {
            mbean.setIsAuthSameAsUM(true);
        }
    }
}
