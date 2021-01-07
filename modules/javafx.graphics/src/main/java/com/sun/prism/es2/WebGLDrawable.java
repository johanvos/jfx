package com.sun.prism.es2;


class WebGLDrawable extends GLDrawable {

    private static long nCreateDrawable(long nativeWindow, long nativeCtxInfo) {
System.out.println("[WEBGLDRAWABLE] createDrawable");
return 1;
    }

    private static long nGetDummyDrawable(long nativeCtxInfo) {
System.out.println("[WEBGLDRAWABLE] getDmmyDrawable");
return 1;
    }

    private static boolean nSwapBuffers(long nativeCtxInfo, long nativeDInfo) {
System.out.println("[WEBGLDRAWABLE] swapBuffers");
return true;
    }

    WebGLDrawable(GLPixelFormat pixelFormat) {

        super(0L, pixelFormat);
        long nDInfo = nGetDummyDrawable(pixelFormat.getNativePFInfo());
        setNativeDrawableInfo(nDInfo);
    }

    WebGLDrawable(long nativeWindow, GLPixelFormat pixelFormat) {
        super(nativeWindow, pixelFormat);
        long nDInfo = nCreateDrawable(nativeWindow, pixelFormat.getNativePFInfo());
        setNativeDrawableInfo(nDInfo);
    }

    @Override
    boolean swapBuffers(GLContext glCtx) {
        return nSwapBuffers(glCtx.getNativeCtxInfo(), getNativeDrawableInfo());
    }
}
