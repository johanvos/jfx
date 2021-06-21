package com.sun.javafx.iio.web;

import com.sun.javafx.iio.ImageFormatDescription;
import com.sun.javafx.iio.ImageLoader;
import com.sun.javafx.iio.ImageLoaderFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * A factory which creates a loader for images on Web platform.
 * NOT NEEDED ANYMORE
 */
@Deprecated
public class WebImageLoaderFactory implements ImageLoaderFactory {

    private static WebImageLoaderFactory theInstance;

    private WebImageLoaderFactory() {};

    /**
     * Returns an instance of WebImageLoaderFactory
     *
     * @return an instance of WebImageLoaderFactory
     */
    public static final synchronized WebImageLoaderFactory getInstance() {
        if (theInstance == null) {
            theInstance = new WebImageLoaderFactory();
        }
        return theInstance;
    }

    /**
     * @inheritDoc
     */
    public ImageFormatDescription getFormatDescription() {
        return WebDescriptor.getInstance();
    }

    /**
     * @inheritDoc
     */
    public ImageLoader createImageLoader(final InputStream input) throws IOException {
System.out.println("[WILF] createImageLoader, input  = " + input);
        return new WebImageLoader(input, WebDescriptor.getInstance());
    }

    /**
     * Creates a loader for the specified input URL.
     *
     * @param input a URL containing an image in the supported format.
     * @return a loader capable of loading and decoding an image from the supplied URL.
     * @throws <IOException> if there is an error creating the loader.
     */
    public ImageLoader createImageLoader(final String input) throws IOException {
        return new WebImageLoader(input, WebDescriptor.getInstance());
    }
}
