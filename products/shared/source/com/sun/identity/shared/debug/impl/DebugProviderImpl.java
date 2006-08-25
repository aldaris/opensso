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
 * $Id: DebugProviderImpl.java,v 1.1 2006-08-25 21:21:47 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.shared.debug.impl;

import java.util.HashMap;
import java.util.Map;

import com.sun.identity.shared.debug.IDebug;
import com.sun.identity.shared.debug.IDebugProvider;

/**
 * Default debug provider implementation.
 */
public class DebugProviderImpl implements IDebugProvider {
    private Map<String, IDebug> debugMap = new HashMap<String, IDebug>();
    
    public synchronized IDebug getInstance(String debugName) {
        IDebug debug = (IDebug)getDebugMap().get(debugName);
        if (debug == null) {
            debug = new DebugImpl(debugName);
            getDebugMap().put(debugName, debug);
        }
        return debug;
    }

    private Map<String, IDebug> getDebugMap() {
        return debugMap;
    }
}
