package com.sun.javafx.iio.web;

import com.sun.javafx.iio.common.ImageDescriptor;

/**
 * A description of images on web platform.
 */
public class WebDescriptor extends ImageDescriptor {
    private static final String formatName = "PNGorJPEGorBMP";

    private static final String[] extensions = { "bmp", "png", "jpg", "jpeg", "gif" };

    private static final Signature[] signatures = {

        new Signature((byte) 0xff, (byte) 0xD8), // JPEG

        new Signature((byte) 137, (byte) 80, (byte) 78, (byte) 71, // PNG
        (byte) 13, (byte) 10, (byte) 26, (byte) 10),

        new Signature((byte)0x42, (byte)0x4D), // BMP

        new Signature(new byte[] {'G', 'I', 'F', '8', '7', 'a'}), // GIF87
        new Signature(new byte[] {'G', 'I', 'F', '8', '9', 'a'})  // GIF89
    };

    private static ImageDescriptor theInstance = null;

    private WebDescriptor() {
        super(formatName, extensions, signatures);
    }

    /**
     * Returns an instance of WebDescriptor
     *
     * @return an instance of WebDescriptor
     */
    public static synchronized ImageDescriptor getInstance() {
        if (theInstance == null) {
            theInstance = new WebDescriptor();
        }
        return theInstance;
    }
}
