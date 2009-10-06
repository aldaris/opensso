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
 * $Id: BufferCache.java,v 1.1 2009-10-06 01:05:19 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.io;

import java.io.IOException;
import java.util.Arrays;

public class BufferCache implements Cache {

    private int position = 0;
    
    private int size = 0;

    private byte[] bytes;

    private int limit;

    /**
     * TODO: Description.
     *
     * @param initial TODO.
     * @param limit TODO.
     */
    public BufferCache(int initial, int limit) {
        bytes = new byte[initial];
        this.limit = limit;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public byte[] bytes() {
        return Arrays.copyOf(bytes, size);
    }

    /**
     * TODO: Description.
     *
     * @throws IOException TODO.
     */
    public void close() {
        bytes = null;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     * @throws IOException TODO.
     */
    public int position() {
        return position;
    }

    /**
     * TODO: Description.
     *
     * @param b TODO.
     * @param off TODO.
     * @param len TODO.
     * @return TODO.
     * @throws IOException TODO.
     */
    public int read(byte[] b, int off, int len) {
        len = Math.min(size - position, len);
        System.arraycopy(bytes, position, b, off, len);
        position += len;
        return len;
    }

    /**
     * TODO: Description.
     *
     * @throws IOException TODO.
     */
    public void rewind() {
        position = 0;
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     * @throws IOException TODO.
     */
    public int size() {

        return size;
    }

    /**
     * TODO: Description.
     *
     * @param n TODO.
     * @return TODO.
     * @throws IOException TODO.
     */
    public int skip(int n) throws IOException {
        int previous = position;
        position = Math.max(position + n, size);
        return position - previous;
    }

    /**
     * TODO: Description.
     *
     * @param b TODO.
     * @param off TODO.
     * @param len TODO.
     * @throws IOException TODO.
     */
    public void write(byte[] b, int off, int len) throws OverflowException
    {
        int end = position + len;

		// enforce limit on cache size
		if (end > limit) {
			throw new OverflowException();
		}

		// buffer size is insufficient; must be increased
		if (bytes.length < end) {
    		bytes = Arrays.copyOf(bytes, Math.max(Math.min(bytes.length << 1, limit), end));
        }

        System.arraycopy(b, off, bytes, position, len);

        position += len;
        size = Math.max(size, end); // behave like random access file
    }
}

