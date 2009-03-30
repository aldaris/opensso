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
 * $Id: ApplicationManager.java,v 1.3 2009-03-30 13:00:10 veiming Exp $
 */
package com.sun.identity.entitlement;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dennis
 */
public final class ApplicationManager {
    //private static ApplicationManager instance = new ApplicationManager();
    private static Map<String, Application> applications =
        new HashMap<String, Application>();

    static {
        Application appl = URLApplication.getInstance();
        applications.put(appl.getName(), appl);
        appl = DelegationApplication.getInstance();
        applications.put(appl.getName(), appl);
    }

    private ApplicationManager() {
    }

    /**
     * TODO
     * create application
     * delete application
     * list applications
     */

    public static Application getApplication(String name) {
        //sms: TODO new Entitlement service
        //name=classname
        if ((name == null) || (name.length() == 0)) {
            return URLApplication.getInstance();
        }
        return applications.get(name);
    }

    public static void addApplication(Application application) {
        applications.put(application.getName(), application);
    }

    public static void deleteApplication(String name) {
        applications.remove(name);
    }

}
