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
 * $Id: Streamer.java,v 1.2 2009-10-09 07:38:38 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class Streamer
{
	/**
	 * TODO: Description.
	 *
	 * @param in TODO.
	 * @param out TODO.
	 * @throws IOException if an I/O exception occurs.
	 */
	public static void stream(InputStream in, OutputStream out) throws IOException {

        byte[] buf = new byte[4096];

		int n;

        while ((n = in.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
    }
}

