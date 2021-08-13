package com.sun.prism.es2;

import com.sun.prism.es2.GLPixelFormat.Attributes;
import java.util.HashMap;

public class WebGLFactory extends GLFactory {
    @Override 
boolean isGLGPUQualify() {
return true;
}
    static native boolean nIsGLExtensionSupported(long nativeContextObject, String glExtStr);


    static String nGetGLVendor(long nativeCtxInfo) {
return "GluonWebGL";
    }
    static String nGetGLRenderer(long nativeCtxInfo) {
return "GluonWebRenderer";
    }
    static String nGetGLVersion(long nativeCtxInfo) {
return "GluonWebVersion";
    }

    private static long nInitialize(int[] attrArr) {
        return 1;
    }
    private static int nGetAdapterOrdinal(long nativeScreen) {
System.err.println("[WEBGLFACTORY] getAdapterOrdinal");
return 0;
    }

    static native int nGetAdapterCount() ;
/*
System.err.println("[WEBGLFACTORY] getAdapterCount");
return 1;
    }
*/

    private native static boolean nGetIsGL2(long nativeCtxInfo);
/*
System.err.println("[WEBGLFACTORY] getisGL2 will return true for now");
return true;
    }
*/

    // Entries must be in lowercase and null string is a wild card
    private GLGPUInfo preQualificationFilter[] = null;

    // These are older GPUs that users have reported problem in using the es2 pipe.
    // We don't have these units in-house to verify or maintain.
    private GLGPUInfo rejectList[] = {
        new GLGPUInfo("ati", "radeon x1600 opengl engine"),
        new GLGPUInfo("ati", "radeon x1900 opengl engine"),
        new GLGPUInfo("intel", "gma x3100 opengl engine")
    };

    @Override
    GLGPUInfo[] getPreQualificationFilter() {
        return preQualificationFilter;
    }

    @Override
    GLGPUInfo[] getRejectList() {
        return rejectList;
    }

    @Override
    GLContext createGLContext(long nativeCtxInfo) {
        return new WebGLContext(nativeCtxInfo);
    }

    @Override
    GLContext createGLContext(GLDrawable drawable, GLPixelFormat pixelFormat,
        GLContext shareCtx, boolean vSyncRequest) {
        GLContext glassCtx = new WebGLContext(drawable, pixelFormat, shareCtx, vSyncRequest);
        GLContext prismCtx = new WebGLContext(drawable, pixelFormat, shareCtx, vSyncRequest);

        // NOTE: glassCtx isn't the prism rendering context. This glassCtx is created
        // and passed to Glass; prism never needs to switch or access it.
        HashMap devDetails = (HashMap) ES2Pipeline.getInstance().getDeviceDetails();
        devDetails.put("contextPtr", glassCtx.getNativeHandle());

        return prismCtx;
    }

    @Override
    GLDrawable createDummyGLDrawable(GLPixelFormat pixelFormat) {
        return new WebGLDrawable(pixelFormat);
    }

    @Override
    GLDrawable createGLDrawable(long nativeWindow, GLPixelFormat pixelFormat) {
        return new WebGLDrawable(nativeWindow, pixelFormat);
    }

    @Override
    GLPixelFormat createGLPixelFormat(long nativeScreen, Attributes attributes) {
        return new WebGLPixelFormat(nativeScreen, attributes);
    }

    @Override
    boolean initialize(Class psClass, Attributes attrs) {

        // holds the list of attributes to be translated for native call
        int attrArr[] = new int[GLPixelFormat.Attributes.NUM_ITEMS];

        attrArr[GLPixelFormat.Attributes.RED_SIZE] = attrs.getRedSize();
        attrArr[GLPixelFormat.Attributes.GREEN_SIZE] = attrs.getGreenSize();
        attrArr[GLPixelFormat.Attributes.BLUE_SIZE] = attrs.getBlueSize();
        attrArr[GLPixelFormat.Attributes.ALPHA_SIZE] = attrs.getAlphaSize();
        attrArr[GLPixelFormat.Attributes.DEPTH_SIZE] = attrs.getDepthSize();
        attrArr[GLPixelFormat.Attributes.DOUBLEBUFFER] = attrs.isDoubleBuffer() ? 1 : 0;
        attrArr[GLPixelFormat.Attributes.ONSCREEN] = attrs.isOnScreen() ? 1 : 0;

        // return the context info object create on the default screen
        nativeCtxInfo = nInitialize(attrArr);

        if (nativeCtxInfo == 0) {
            // current pipe doesn't support this pixelFormat request
            return false;
        } else {
            gl2 = nGetIsGL2(nativeCtxInfo);
            return true;
        }
    }

    @Override
    int getAdapterCount() {
        return nGetAdapterCount();
    }

    @Override
    int getAdapterOrdinal(long nativeScreen) {
        return nGetAdapterOrdinal(nativeScreen);
    }

    @Override
    void updateDeviceDetails(HashMap deviceDetails) {
           deviceDetails.put("shareContextPtr", getShareContext().getNativeHandle());
    }
}
