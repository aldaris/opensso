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
 * $Id: FileCache.java,v 1.1 2009-10-06 01:05:19 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class FileCache implements Cache {

    /** TODO: Description. */
    private File file;

    /** TODO: Description. */
    private RandomAccessFile raf;

    /** TODO: Description. */
    private int limit;

    /**
     * TODO: Description.
     *
     * @param directory TODO.
     * @param limit TODO.
     * @throws IOException TODO.
     */
    public FileCache(File directory, int limit) throws IOException {
        file = File.createTempFile("FileCache", null, directory);
		raf = new RandomAccessFile(file, "rw");
        this.limit = limit;
    }

    /**
     * TODO: Description.
     *
     * @throws IOException TODO.
     */
    public void close() throws IOException {
        raf.close();
        file.delete();
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     * @throws IOException TODO.
     */
    public int position() throws IOException {
        return (int)raf.getFilePointer();
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
    public int read(byte[] b, int off, int len) throws IOException {
        return raf.read(b, off, len);
    }

    /**
     * TODO: Description.
     *
     * @throws IOException TODO.
     */
    public void rewind() throws IOException {
        raf.seek(0L);
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     * @throws IOException TODO.
     */
    public int size() throws IOException {
        return (int)raf.length();
    }

    /**
     * TODO: Description.
     *
     * @param n TODO.
     * @return TODO.
     * @throws IOException TODO.
     */
    public int skip(int n) throws IOException {
        return raf.skipBytes(n);
    }

    /**
     * TODO: Description.
     *
     * @param b TODO.
     * @param off TODO.
     * @param len TODO.
     * @throws IOException TODO.
     */
    public void write(byte[] b, int off, int len) throws IOException
    {
		// enforce limit on cache size
        if (raf.getFilePointer() + len > limit) {
			throw new OverflowException();
		}

        raf.write(b, off, len);
    }
}

