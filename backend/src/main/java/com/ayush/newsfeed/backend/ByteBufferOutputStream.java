package com.ayush.newsfeed.backend;

/**
 * Created by dexter on 10/07/2016.
 */

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;


/**
 * ByteArrayOutputStream with a no-copy method to convert to a ByteBuffer
 */
public class ByteBufferOutputStream extends ByteArrayOutputStream {

    /**
     * Creates a newly allocated byte array. Its size is the current
     * size of this output stream and the valid contents of the buffer
     * have been copied into it.
     *
     * @return the current contents of this output stream, as a byte array.
     * @see java.io.ByteArrayOutputStream#size()
     */
    public synchronized ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(buf, 0, count);
    }
}