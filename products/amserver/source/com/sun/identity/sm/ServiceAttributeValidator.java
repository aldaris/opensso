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
 * $Id: ServiceAttributeValidator.java,v 1.1 2005-11-01 00:31:31 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

import java.util.Set;

/* iPlanet-PUBLIC-CLASS */

/**
 * The interface <code>ServiceAttributeValidator</code> should be implemented
 * by the services/applications if validator plugins are required.
 */
public interface ServiceAttributeValidator {

    /**
     * Validates the given set of string values.
     * <p>
     * 
     * <pre>
     *  Example:
     *       Set values = new HashSet();
     *       values.add(&quot;o=iplanet.com&quot;);
     *       values.add(&quot;uid=amadmin,ou=people,o=isp&quot;);
     *       if ( DNValidator.validate(values) ) {
     *           System.out.println(&quot;valid attribute values&quot;);
     *       } else {
     *           System.out.println(&quot;invalid attribute values&quot;);
     *       }
     * </pre>
     * 
     * @param values
     *            the <code>Set</code> of attribute values to validate
     * @return true if validates successfully; false otherwise
     */
    public boolean validate(Set values);

}
