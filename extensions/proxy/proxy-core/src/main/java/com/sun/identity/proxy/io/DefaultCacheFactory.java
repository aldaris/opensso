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
 * $Id: DefaultCacheFactory.java,v 1.1 2009-10-06 01:05:19 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.io;

import java.io.File;
import java.io.InputStream;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class DefaultCacheFactory implements CacheFactory
{
    /** TODO: Description. */
    private final int maxSize = 1 * 1024 * 1024; // 1 MiB

    /** TODO: Description. */
    private final int maxBuffer = 64 * 1024; // 64 KiB

    /** TODO: Description. */
    private final File directory = new File(System.getProperty("java.io.tmpdir"));

    /**
     * TODO: Description.
     *
     * @param in TODO.
     * @return TODO.
     */
    public CachedStream cacheStream(InputStream in) {
        return new CachedStream(in, directory, maxSize, maxBuffer);
    }
}

