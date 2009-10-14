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
 * $Id: FileRecord.java,v 1.2 2009-10-14 16:58:02 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Implements a record, storing data in a file.
 *
 * @author Paul C. Bryan
 */
public class FileRecord implements Record
{
    /** Supports reading and writing data within the file. */
    private RandomAccessFile raf;

    /** The length limit of the record. */
    private int limit;

    /**
     * Creates a new file record.
     *
     * @param file the file to store data in.
     * @param limit the length limit of the record, after which an {@link IOException} should be thrown.
     * @throws IOException if an I/O exception occurs.
     */
    public FileRecord(File file, int limit) throws IOException {
        raf = new RandomAccessFile(file, "rw");
        this.limit = limit;
    }

    /**
     * Used to ensure that the record is not closed when operations are
     * performed on it.
     *
     * @throws IOExcepton if record has been closed.
     */
    private void closedException() throws IOException {
        if (raf == null) {
            throw new IOException("record has been closed");
        }
    }

    @Override
    public void close() throws IOException {
        if (raf != null) {
            raf.close();
            raf = null;
        }
    }

    @Override
    public int length() throws IOException {
        closedException();
        return (int)raf.length();
    }

    @Override
    public int limit() throws IOException {
        closedException();
        return limit;
    }

    @Override
    public int position() throws IOException {
        closedException();
        return (int)raf.getFilePointer();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        closedException();
        return raf.read(b, off, len);
    }

    @Override
    public void seek(int position) throws IOException {
        closedException();
        if (raf.getFilePointer() > raf.length()) {
            throw new IOException("requested position greater than record length");
        }
        raf.seek(position);
    }

    public int skip(int n) throws IOException {
        closedException();
        return raf.skipBytes(n);
    }

    @Override
    public void truncate(int length) throws IOException {
        closedException();
        if (length < 0) {
            throw new IllegalArgumentException();
        }
        if (length > raf.length()) {
            throw new IOException("cannot increase length of record via truncate method");
        }
        long position = raf.getFilePointer();
        raf.setLength(length);
        if (position > length) {
            position = length;
        }
        raf.seek(position);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        closedException();
        if (raf.getFilePointer() + len > limit) {
            throw new OverflowException();
        }
        raf.write(b, off, len);
    }
}

