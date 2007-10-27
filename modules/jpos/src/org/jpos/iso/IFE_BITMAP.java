/*
 * Copyright (c) 2007 jPOS.org 
 *
 * See terms of license at http://jpos.org/license.html
 *
 */
package org.jpos.iso;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

/**
 * EBCDIC [unpacked] Bitmap
 *
 * @author apr
 * @see ISOComponent
 * @see ISOBitMapPackager
 */
public class IFE_BITMAP extends ISOBitMapPackager {
    public IFE_BITMAP() {
        super();
    }
    /**
     * @param len - field len
     * @param description symbolic descrption
     */
    public IFE_BITMAP(int len, String description) {
        super(len, description);
    }
    /**
     * @param c - a component
     * @return packed component
     * @exception ISOException
     */
    public byte[] pack (ISOComponent c) throws ISOException {
        byte[] b = ISOUtil.bitSet2byte ((BitSet) c.getValue());
        return ISOUtil.asciiToEbcdic(ISOUtil.hexString(b));
    }
    public int getMaxPackedLength() {
        return getLength() >> 2;
    }
    /**
     * @param c - the Component to unpack
     * @param b - binary image
     * @param offset - starting offset within the binary image
     * @return consumed bytes
     * @exception ISOException
     */
    public int unpack (ISOComponent c, byte[] b, int offset)
        throws ISOException
    {
        int len;
        b = ISOUtil.ebcdicToAsciiBytes (b, offset, getLength() << 2);
        BitSet bmap = ISOUtil.hex2BitSet (b, 0, getLength() << 3);
        c.setValue(bmap);
        len = (bmap.get(1) == true) ? 128 : 64; /* changed by Hani */
        if (getLength() > 16 && bmap.get(65))
            len = 192;
        return (len >> 2);
    }
    public void unpack (ISOComponent c, InputStream in) 
        throws IOException, ISOException
    {
        byte[] b = ISOUtil.ebcdicToAsciiBytes (readBytes (in, 16));
        BitSet bmap = ISOUtil.hex2BitSet (new BitSet (64), b, 0);
        if (getLength() > 8 && bmap.get (1)) {
            b = ISOUtil.ebcdicToAsciiBytes (readBytes (in, 16));
            ISOUtil.hex2BitSet (bmap, b, 64);
        }
        c.setValue(bmap);
    }
}
