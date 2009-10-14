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
 * $Id: ByteArrayRecord.java,v 1.1 2009-10-14 08:56:55 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * An implementation of the {@link Record} interface, backed by a
 * dynamically-growing byte array.
 *
 * @author Paul C. Bryan
 */
public class ByteArrayRecord implements Record
{
    /** The initial length of the byte array. */
    private static final int INITIAL_LENGTH = 8192;

    /** Current read/write position within the record. */
    private int position = 0;

    /** Current length of the record. */
    private int length = 0;

    /** Byte array storage for the record. */
    private byte[] bytes;

    /** The length limit of the record. */
    private int limit;

    /**
     * Creates a new record backed by a byte array.
     *
     * @param limit the length limit of the record, after which an {@link IOException} should be thrown.
     */
    public ByteArrayRecord(int limit) {
        bytes = new byte[Math.min(INITIAL_LENGTH, limit)];
        this.limit = limit;
    }

    /**
     * Used to ensure that the record is not closed when operations are
     * performed on it.
     *
     * @throws IOExcepton if record has been closed.
     */
    private void closedException() throws IOException {
        if (bytes == null) {
            throw new IOException("record has been closed");
        }
    }

    @Override
    public void close() throws IOException {
        bytes = null;
    }

    @Override
    public int length() throws IOException {
        closedException();
        return length;
    }

    @Override
    public int limit() throws IOException {
        closedException();
        return limit;
    }

    @Override
    public int position() throws IOException {
        closedException();
        return position;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        closedException();
        if (len == 0) {
            return 0;
        }
        len = Math.min(length - position, len);
        if (len == 0) {
            return -1;
        }
        System.arraycopy(bytes, position, b, off, len);
        position += len;
        return len;
    }

    @Override
    public void seek(int position) throws IOException {
        closedException();
        if (position > length) {
            throw new IOException("requested position greater than record length");
        }
        this.position = position;
    }

    @Override
    public int skip(int n) throws IOException {
        closedException();
        int previous = position;
        position = Math.max(Math.max(position + n, length), 0);
        return position - previous;
    }

    @Override
    public void truncate(int length) throws IOException {
        closedException();
        if (length < 0) {
            throw new IllegalArgumentException();
        }
        if (length > this.length) {
            throw new IOException("cannot increase length of record via truncate method");
        }
        this.length = length;
        if (length > position) {
            position = length;
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException, OverflowException {
        closedException();
        int end = position + len;
        if (end > limit) {
            throw new OverflowException();
        }
        if (bytes.length < end) { // must grow buffer
            bytes = Arrays.copyOf(bytes, Math.max(Math.min(bytes.length << 1, limit), end));
        }
        System.arraycopy(b, off, bytes, position, len);
        position += len;
        length = Math.max(length, end);
    }
}

