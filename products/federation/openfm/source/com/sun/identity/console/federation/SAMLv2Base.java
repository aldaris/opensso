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
 * $Id: SAMLv2Base.java,v 1.3 2007-10-09 01:10:03 asyhuang Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.console.federation.model.EntityModel;
import com.sun.identity.console.federation.model.EntityModelImpl;
import com.sun.identity.console.federation.model.SAMLv2ModelImpl;

import javax.servlet.http.HttpServletRequest;

public abstract class SAMLv2Base extends EntityPropertiesBase {
    
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
}
