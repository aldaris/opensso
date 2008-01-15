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
 * $Id: Anonymous.java,v 1.3 2008-01-15 19:58:58 jefberpe Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.config.authc;

import com.sun.identity.config.pojos.AnonymousStore;
import com.sun.identity.config.util.AjaxPage;

/**
 * @author Jeffrey Bermudez
 */
public class Anonymous extends AjaxPage {

    public AnonymousStore anonymousStore = new AnonymousStore();


    public void onPost() {
        anonymousStore.getRealm().setName(toString("realmName"));
        anonymousStore.setAnonymousName(toString("anonymousName"));

        save(anonymousStore);
    }

    protected void save(AnonymousStore anonymousStore) {
        getConfigurator().addAuthenticationStore(anonymousStore);
    }

}