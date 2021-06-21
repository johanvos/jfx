package com.sun.javafx.iio.web;

import com.sun.glass.utils.NativeLibLoader;
import com.sun.javafx.iio.common.*;
import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageMetadata;
import com.sun.javafx.iio.ImageStorage.ImageType;
import com.sun.javafx.iio.common.ImageLoaderImpl;
import com.sun.javafx.iio.common.ImageTools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

import java.util.HashMap;
import java.util.Map;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * A loader for images on Web platform.
 */
public class WebImageLoader extends ImageLoaderImpl {

    /** These constants must match with those in native */
    public static final int GRAY = 0;
    public static final int GRAY_ALPHA = 1;
    public static final int GRAY_ALPHA_PRE = 2;
    public static final int PALETTE = 3;
    public static final int PALETTE_ALPHA = 4;
    public static final int PALETTE_ALPHA_PRE = 5;
    public static final int PALETTE_TRANS = 6;
    public static final int RGB = 7;
    public static final int RGBA = 8;
    public static final int RGBA_PRE = 9;

    private static final Map<Integer, ImageType> COLOR_SPACE_MAPPING;

    /** Pointer to the native loader */
    private long structPointer;

    /** Set by native code */
    private int inWidth;
    private int inHeight;
    private int nImages;

    private boolean isDisposed = false;

    private int delayTime; // applicable to animated images only
    private int loopCount; // applicable to animated images only

    /***************************** Native Loader methods ******************************************/

    /** Set up static method IDs for calls back to Java. */
    private static native void initNativeLoading();

    /** Create a loader and buffer data from the InputStream. Report progress if requested. */
    private native long loadImage(final InputStream stream, boolean reportProgress) throws IOException;

    /** Create a loader for the given URL. Report progress if requested. */
    private native long loadImageFromURL(final String url, boolean reportProgress) throws IOException;

    /** Set native image size */
    private native void resizeImage(long structPointer, int width, int height);

    /** Return a buffer with decompressed image data */
    private native byte[] getImageBuffer(long structPointer, int imageIndex);

    /** Return the number of color components */
    private native int getNumberOfComponents(long structPointer);

    /** Return image color space model code */
    private native int getColorSpaceCode(long structPointer);

    /** Return image duration for animated images */
    private native int getDelayTime(long structPointer);

    /** Destroy a loader. */
    private static native void disposeLoader(long structPointer);

    /*************************** End of Native Loader methods ***************************************/


    static {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            NativeLibLoader.loadLibrary("nativeiio");
            return null;
        });

        COLOR_SPACE_MAPPING = new HashMap<>();
            COLOR_SPACE_MAPPING.put(GRAY,              ImageType.GRAY);
            COLOR_SPACE_MAPPING.put(GRAY_ALPHA,        ImageType.GRAY_ALPHA);
            COLOR_SPACE_MAPPING.put(GRAY_ALPHA_PRE,    ImageType.GRAY_ALPHA_PRE);
            COLOR_SPACE_MAPPING.put(PALETTE,           ImageType.PALETTE);
            COLOR_SPACE_MAPPING.put(PALETTE_ALPHA,     ImageType.PALETTE_ALPHA);
            COLOR_SPACE_MAPPING.put(PALETTE_ALPHA_PRE, ImageType.PALETTE_ALPHA_PRE);
            COLOR_SPACE_MAPPING.put(PALETTE_TRANS,     ImageType.PALETTE_TRANS);
            COLOR_SPACE_MAPPING.put(RGB,               ImageType.RGB);
            COLOR_SPACE_MAPPING.put(RGBA,              ImageType.RGBA);
            COLOR_SPACE_MAPPING.put(RGBA_PRE,          ImageType.RGBA_PRE);

        initNativeLoading();
    }

    /** Called by the native code when input parameters are known. */
    private void setInputParameters(
            int width,
            int height,
            int imageCount,
            int loopCount) {

        inWidth = width;
        inHeight = height;
        nImages = imageCount;
        this.loopCount = loopCount;
    }

    private void updateProgress(float progressPercentage) {
        updateImageProgress(progressPercentage);
    }

    private boolean shouldReportProgress() {
        return listeners != null && !listeners.isEmpty();
    }

    private void checkNativePointer() throws IOException {
        if (structPointer == 0L) {
            throw new IOException("Unable to initialize image native loader!");
        }
    }

    private void retrieveDelayTime() {
        if (nImages > 1) {
            delayTime = getDelayTime(structPointer);
        }
    }

    public WebImageLoader(final String urlString, final ImageDescriptor desc) throws IOException {
        super(desc);

        // see if the given URL is valid
        try {
            final URL url = new URL(urlString);
        }
        catch (MalformedURLException mue) {
            throw new IllegalArgumentException("Image loader: Malformed URL!");
        }

        try {
            structPointer = loadImageFromURL(urlString, shouldReportProgress());
        } catch (IOException e) {
            dispose();
            throw e;
        }

        checkNativePointer();
        retrieveDelayTime();
    }

    public WebImageLoader(final InputStream inputStream, final ImageDescriptor desc) throws IOException {
        super(desc);
        if (inputStream == null) {
            throw new IllegalArgumentException("Image loader: input stream == null");
        }

        try {
            structPointer = loadImage(inputStream, shouldReportProgress());
        } catch (IOException e) {
            dispose();
            throw e;
        }

        checkNativePointer();
        retrieveDelayTime();
    }

    /**
     * @inheritDoc
     */
    public synchronized void dispose() {
        if (!isDisposed && structPointer != 0L) {
            isDisposed = true;
            WebImageLoader.disposeLoader(structPointer);
            structPointer = 0L;
        }
    }

   /**
    * @inheritDoc
    */
    public ImageFrame load(int imageIndex, int width, int height, boolean preserveAspectRatio, boolean smooth)
            throws IOException {

        if (imageIndex >= nImages) {
            dispose();
            return null;
        }

        // Determine output image dimensions.
        int[] widthHeight = ImageTools.computeDimensions(inWidth, inHeight, width, height, preserveAspectRatio);
        width = widthHeight[0];
        height = widthHeight[1];

        final ImageMetadata md = new ImageMetadata(
                null, // gamma
                true, // whether smaller values represent darker shades
                null, // a palette index to use as background
                null, // background color
                null, // a palette index to be used as transparency
                delayTime == 0 ? null : delayTime, // the amount of time to pause at the current image (milliseconds).
                nImages > 1 ? loopCount : null, // number of loops
                width, // image width
                height, // image height
                null, // image left offset
                null, // image top offset
                null); // disposal method

        updateImageMetadata(md);

        resizeImage(structPointer, width, height);

        // the color model and the number of components can change when resizing
        final int nComponents = getNumberOfComponents(structPointer);
        final int colorSpaceCode = getColorSpaceCode(structPointer);
        final ImageType imageType = COLOR_SPACE_MAPPING.get(colorSpaceCode);

        final byte[] pixels = getImageBuffer(structPointer, imageIndex);

        return new ImageFrame(imageType,
                ByteBuffer.wrap(pixels),
                width,
                height,
                width * nComponents,
                null,
                md);
    }
}
