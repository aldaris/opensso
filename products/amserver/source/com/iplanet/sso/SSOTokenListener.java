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
 * $Id: SSOTokenListener.java,v 1.1 2005-11-01 00:30:30 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.sso;

/**
 * The <code>SSOTokenListener</code> interface needs to be implemented by the
 * applications to receive SSO token events. The method
 * <code>ssoTokenChanged()</code> is invoked when an SSO token event arrives.
 * 
 * @see com.iplanet.sso.SSOTokenEvent
 */

public interface SSOTokenListener {

    /**
     * This method will be called when there is a change in the single sign on
     * token status.
     * 
     * @param evt
     *            single sign on token status changed event.
     */
    public void ssoTokenChanged(com.iplanet.sso.SSOTokenEvent evt);
}
