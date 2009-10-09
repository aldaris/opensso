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
 * $Id: CachedStream.java,v 1.2 2009-10-09 07:38:37 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.io;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class CachedStream extends InputStream
{
    /** TODO: Description. */
    private static final int MIN_SIZE = 4 * 1024; // 4 KiB

	/** Indicates whether this object is still caching content. */
	private boolean caching = true;

    /** TODO: Description. */
    private Cache cache;

	/** Directory where the cache file should be created. */
	private File directory;

    /** TODO: Description. */
    private InputStream in;

	/** TODO: Description. */
	private int maxBuffer;

	/** TODO: Description. */
	private int maxSize;

	/**
     * TODO: Description
     *
     * @param in TODO.
	 * @param directory TODO.
     * @param maxSize TODO.
     * @param maxBuffer TODO.
     */
	public CachedStream(InputStream in, File directory, int maxSize, int maxBuffer)
	{
	    this.in = in;
	    this.directory = directory;
	    this.maxSize = Math.max(maxSize, MIN_SIZE);
		this.maxBuffer = Math.max(Math.min(maxBuffer, this.maxSize), MIN_SIZE);

        // start caching in memory buffer
		cache = new BufferCache(MIN_SIZE, maxBuffer);
	}

	/**
     * Returns the number of bytes that can be read (or skipped over) from
     * this input stream without blocking by the next caller of a method for
     * this input stream.
	 *
	 * @return the number of bytes that can be read from this input stream without blocking.
     * @throws IOException if an I/O exception occurs.
     */
	public int available() throws IOException
	{
    	int n;

    	// see if there is availability from the cache first
        n = cache.size() - cache.position();

        // if not, use what's available from underlying stream
        if (n == 0) {
            n = in.available();
        }

        return n;
    }

	/**
     * Closes the input stream and releases any system resources associated
     * with the CachedStream wrapper.
     *
     * @throws IOException if an I/O exception occurs.
     */
	public void close() throws IOException
	{
        if (cache != null) {
            cache.close();
            cache = null;
        }

		if (in != null) {
		    in.close();
		    in = null;
		}
	}

	/**
     * Called by the garbage collector when garbage collection determines that
     * there are no more references to the object. This implementation cleans
     * up any open file created to hold cached input stream contents.
     */
	public void finalize() {
		try {
			close();
		}
		catch (IOException ioe) {
		}
	}

	/**
     * This class does not support mark and reset; so this method does
     * nothing.
     *
     * @param readlimit irrelevant.
     */
	public void mark(int readlimit) {
	}

	/**
     * Indicates if this input stream supports the mark and reset methods.
     * <p>
     * This class does not support mark and reset, so this method
     * unconditionally returns <code>false</code>.
     *
     * @return <code>false</code> unconditionally.
     */
	public boolean markSupported() {
		return false;
	}

	/**
	 * Reads the next byte of data from the input stream. The value byte is
     * returned as an int in the range 0 to 255. If no byte is available
     * because the end of the stream has been reached, the value -1 is
     * returned. This method blocks until input data is available, the end of
     * the stream is detected, or an exception is thrown.
     *
     * @return the next byte of data, or -1 if the end of the stream is reached.
     * @throws IOException if an I/O exception occurs.
     * @throws OverflowException if cache maximum size is exceeded.
     */
	public int read() throws IOException {
	    byte[] b = new byte[1];
	    int len = read(b, 0, 1);
	    return (len > 0 ? len : -1);
	}

	/**
     * Reads some number of bytes from the input stream and stores them into
     * the buffer array b. The number of bytes actually read is returned as an
     * integer. This method blocks until input data is available, end of file
     * is detected, or an exception is thrown.
	 *
     * @return the total number of bytes read into the buffer, or -1 if no more data because of end of stream.
     * @throws IOException if an I/O exception occurs.
     * @throws OverflowException if cache maximum size is exceeded.
     * @throws NullPointerException if b is null.
	 */
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	/**
     * Reads up to len bytes of data from the input stream into an array of
     * bytes. An attempt is made to read as many as len bytes, but a smaller
     * number may be read. The number of bytes actually read is returned as
     * an integer.
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset in array b at which the data is written.
     * @param len the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or -1 if no more data because of end of stream.
     * @throws IndexOutOfBoundsException if off and/or len puts read of b array out of bounds.
     * @throws IOException if an I/O exception occurs.
     * @throws NullPointerException if b is null.
     * @throws OverflowException if cache maximum size is exceeded.
     */
	public int read(byte[] b, int off, int len) throws IOException
	{
		// sanity checks on array bounds (bonus NullPointerException if b is null)
        if (off < 0 || len < 0 || (b != null && len > b.length - off)) {
            throw new IndexOutOfBoundsException();
        }

        // current position is inside of cache
        if (cache.position() < cache.size()) {
            return cache.read(b, off, len);
        }

        // current position is outside of cache
        int n = in.read(b, off, len);
        cacheBytes(b, off, n);
        return n;
    }

    /**
     * TODO: Description.
     *
     * @throws IOException TODO.
     * @throws OverflowException TODO.
     */
    private void upgrade() throws IOException
    {
        // upgrade from buffer to file
        if (cache instanceof BufferCache) {
            BufferCache bufferCache = (BufferCache)cache;
            cache = new FileCache(directory, maxSize);
            byte[] bytes = bufferCache.bytes();
            bufferCache.close();
            cache.write(bytes, 0, bytes.length);
        }

        // no further upgrade path for cache
        else {
            throw new OverflowException();
        }
    }

    /**
     * TODO: Description.
     *
     * @param b TODO.
     * @param off TODO.
     * @param len TODO.
     * @throws OverflowException TODO.
     * @throws IOException TODO.
     */
    private void cacheBytes(byte[] b, int off, int len) throws IOException
    {
        if (!caching || len <= 0) {
            return;
        }

        try {
            cache.write(b, off, len);
        }

        catch (OverflowException oe) {
            upgrade(); // this throws overflow exception if there is no upgrade path
            cache.write(b, off, len); // this can throw overflow exception, which is also fine
        }
    }

	/**
     * This class does not support mark and reset; this method unconditionally
     * throws IOException.
     *
     * @throws IOException unconditionally.
     */
	public void reset() throws IOException {
		throw new IOException("mark/reset not supported");
	}

	/**
     * Skips over and discards n bytes of data from this input stream. The
     * skip method may, for a variety of reasons, end up skipping over some
     * smaller number of bytes, possibly 0. This may result from any of a
     * number of conditions; reaching end of file before n bytes have been
     * skipped is only one possibility. The actual number of bytes skipped is
     * returned. If n is negative, no bytes are skipped.
	 *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @throws IOException if an I/O exception occurs.
	 */
	public long skip(long n) throws IOException
	{
		// per the interface contract
		if (n <= 0) {
			return 0;
		}

		// truncate to integer maximum value (uniform interface)
		int i = (int)Math.min(n, Integer.MAX_VALUE);

		// position within cache; instruct it to skip
		if (cache.position() < cache.size()) {
		    return cache.skip(i);
		}

        // not caching; instruct underlying stream to skip
        if (!caching) {
            return in.skip(n); // can go larger than int value in this case
        }

        // need to cache whatever is skipped
        byte[] b = new byte[i];
        int len = read(b, 0, i);
        return len;
    }

	/**
	 * Sets the read position to the beginning.
	 */
	public void rewind() throws IOException {

	    if (!caching) {
	        throw new IOException("can only rewind if caching");
	    }

	    cache.rewind();
	}
	
	/**
	 * Signal that further reads of the underlying stream should not be cached.
	 */
	public void stop() {
	    caching = false;
	}
}

