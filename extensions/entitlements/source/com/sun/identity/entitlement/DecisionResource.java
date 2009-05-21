/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the License). You may not use
 * this file except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each
 * file and include the License file at opensso/legal/CDDLv1.0.txt. If
 * applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: DecisionResource.java,v 1.1 2009-05-21 21:15:37 pbryan Exp $
 */

package com.sun.identity.entitlement;

import java.security.AccessController;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.security.auth.Subject;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.internal.server.AuthSPrincipal;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import com.sun.identity.security.AdminTokenAction;

/**
 * Exposes the entitlement decision REST resource.
 * 
 * @author Paul C. Bryan <pbryan@sun.com>
 */
@Path("/1/entitlement/decision")
public class DecisionResource {

	private static final Subject ADMIN_SUBJECT =
	 SubjectUtils.createSubject((SSOToken)AccessController.doPrivileged(AdminTokenAction.getInstance()));

	@GET
	@Produces("text/plain")
	public String decision(
	 @QueryParam("realm") String realm,
	 @QueryParam("subject") String subject,
	 @QueryParam("action") String action,
	 @QueryParam("resource") String resource) {

	 	try {
			return Boolean.toString(new Evaluator(ADMIN_SUBJECT).hasEntitlement(realm,
			 toSubject(subject), toEntitlement(resource, action), Collections.EMPTY_MAP));
		}

		// fail safe
		catch (EntitlementException ee) {
			return "false";
		}		
	}

	private Entitlement toEntitlement(String resource, String action) {
		HashSet set = new HashSet<String>();
		set.add(action);
		return new Entitlement(resource, set);
	}

	private Subject toSubject(String subject) {
		Set<Principal> principals = new HashSet<Principal>();
		principals.add(new AuthSPrincipal(subject));
		return new Subject(false, principals, new HashSet(), new HashSet());
	}
}

