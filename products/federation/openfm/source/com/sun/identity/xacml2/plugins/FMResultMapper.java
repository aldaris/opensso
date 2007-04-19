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
 * $Id: FMResultMapper.java,v 1.1 2007-04-19 19:18:46 dillidorai Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.xacml2.plugins;
import com.sun.identity.xacml2.context.Result;
import com.sun.identity.xacml2.common.XACML2Exception;
import com.sun.identity.xacml2.spi.ActionMapper;
import com.sun.identity.xacml2.spi.ResourceMapper;
import com.sun.identity.xacml2.spi.ResultMapper;
import java.util.Map;

/**
 * This class implements ResultMapper to map between 
 * and native resource result and XACML context  result
 */
public class FMResultMapper implements ResultMapper {

    /**
     * Initializes the mapper implementation. This would be called immediately 
     * after constructing an instance of the implementation.
     *
     * @param pdpEntityId EntityID of PDP
     * @param pepEntityId EntityID of PEP
     * @param properties configuration properties
     * @exception XACML2Exception if can not initialize
     */
    public void initialize(String pdpEntityId, String pepEntityId, 
            Map properties) throws XACML2Exception {
    }

    /**
     * Maps <code>com.sun.identity.policy.ResourceResult</code> to
     * XACML context Result
     *
     * @param nativeResult native resource result
     * @param nativeServiceName native service name
     * @param resourceMapper resource mapper to map to XACML Resource
     * @param actionMapper  action mapper to map to XACML Action
     * @return XACML  context Result
     * @exception XACML2Exception if can not map to XACML  context Result
     */
    public Result mapToXACMLResult(Object nativeResult, 
            String nativeServiceName,
            ResourceMapper resourceMapper, ActionMapper actionMapper) 
            throws XACML2Exception {
        return null;
    }

}

