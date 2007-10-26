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
 * "Portions Copyrighted [year] [name of copyright owner]
 *
 * $Id: SAMLv2Base.java,v 1.5 2007-10-26 21:38:29 babysunil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.federation.model.EntityModel;
import com.sun.identity.console.federation.model.SAMLv2ModelImpl;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public abstract class SAMLv2Base extends EntityPropertiesBase {
    protected static final String PROPERTIES = "propertyAttributes";
    protected static final String KEYNAMES = "128, 192, 256";
    protected static final String ALGORITHM = "AES, DESede";
    
    public SAMLv2Base(String name) {
        super(name);
    }    
    
    public void beginDisplay(DisplayEvent event)
        throws ModelControlException 
    {
        super.beginDisplay(event);

    }
        
    protected String getProfileName() {
        return EntityModel.SAMLV2;
    }
    
    protected AMModel getModelInternal() {
        HttpServletRequest req = getRequestContext().getRequest();
        return new SAMLv2ModelImpl(req, getPageSessionAttributes());
    }
    
    protected abstract void createPropertyModel();
    /**
     * Converts the List to Set.
     *
     * @param list the list to be converted.
     * @return the corresponding Set.
     */
    protected Set convertListToSet(List list) {
        Set s = new HashSet();
        s.addAll(list);
        return s;
    }
    
    /**
     * Return empty set if value is null
     *
     * @param set the set to be checked for null.
     * @return the EMPTY_SET if value is null.
     */
    protected Set returnEmptySetIfValueIsNull(Set set) {
        return (set != null) ? set : Collections.EMPTY_SET;
    }
}
