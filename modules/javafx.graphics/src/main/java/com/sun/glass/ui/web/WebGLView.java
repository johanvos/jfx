package com.sun.glass.ui.web;

import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.View;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Map;

public class WebGLView extends View {

    private boolean imEnabled = false;
    private boolean isInPreeditMode = false;
    private final StringBuilder preedit = new StringBuilder();
    private ByteBuffer attributes;
    private int lastCaret;

    private native void enableInputMethodEventsImpl(long ptr, boolean enable);
    private static WebGLView instance = null;

    public WebGLView() {
        instance= this;
    }

    public WebGLView getInstance() {
        return instance;
    }

    public static void doResize(int w, int h ) {
        instance.notifyResize(w, h);
    }

    @Override
    protected void _enableInputMethodEvents(long ptr, boolean enable) {
        enableInputMethodEventsImpl(ptr, enable);
        if (imEnabled) {
            preedit.setLength(0);
        }
        imEnabled = enable;
    }

    @Override
    protected int _getNativeFrameBuffer(long ptr) {
        return 0;
    }

    @Override
    protected long _create(Map caps) {
return 1;
    }

    @Override
    protected native long _getNativeView(long ptr);

    @Override
    protected native int _getX(long ptr);

    @Override
    protected native int _getY(long ptr);

    @Override
    protected native void _setParent(long ptr, long parentPtr);

    @Override
    protected native boolean _close(long ptr);

    @Override
    protected native void _scheduleRepaint(long ptr);

    @Override
    protected void _begin(long ptr) {}

    @Override
    protected void _end(long ptr) {}

    @Override
    protected void _uploadPixels(long ptr, Pixels pixels) {
System.err.println("[WEBGLVIEW] uploadPixels asked, ptr = " + ptr+", pixels = " + pixels);
        Buffer data = pixels.getPixels();
System.err.println("[WEBGLVIEW] data = " + data);
        if (data.isDirect() == true) {
System.err.println("[WEBGLVIEW] data direct! ");
            _uploadPixelsDirect(ptr, data, pixels.getWidth(), pixels.getHeight());
        } else if (data.hasArray() == true) {
System.err.println("[WEBGLVIEW] data has array! ");
            if (pixels.getBytesPerComponent() == 1) {
System.err.println("[WEBGLVIEW] data has BYTES! ");
                ByteBuffer bytes = (ByteBuffer)data;
                _uploadPixelsByteArray(ptr, bytes.array(), bytes.arrayOffset(), pixels.getWidth(), pixels.getHeight());
            } else {
                IntBuffer ints = (IntBuffer)data;
System.err.println("[WEBGLVIEW] data has ints! ");
int[] raw = ints.array();
int nz = 0;
for (int i = 0; i < raw.length; i++) {
if (raw[i] != 0) nz++;
}
System.out.println("[WEBGLVIEW] has " + nz+" non-zero values in total " + raw.length);
                try {
                    WebApplication.uploadPixelMethod.invoke(null, ptr, ints.array(), ints.arrayOffset(), pixels.getWidth(), pixels.getHeight());
                } catch (Throwable t) {
System.err.println("Error uploading pixels native web! " + t);
t.printStackTrace();
                }
                // _uploadPixelsIntArray(ptr, ints.array(), ints.arrayOffset(), pixels.getWidth(), pixels.getHeight());
System.err.println("[WEBGLVIEW] data has uploaded to native layer native ! ");
            }
        } else {
            // gznote: what are the circumstances under which this can happen?
            _uploadPixelsDirect(ptr, pixels.asByteBuffer(), pixels.getWidth(), pixels.getHeight());
        }
    }
    private native void _uploadPixelsDirect(long viewPtr, Buffer pixels, int width, int height);
    private native void _uploadPixelsByteArray(long viewPtr, byte[] pixels, int offset, int width, int height);
    private native void _uploadPixelsIntArray(long viewPtr, int[] pixels, int offset, int width, int height);

    @Override
    protected native boolean _enterFullscreen(long ptr, boolean animate, boolean keepRatio, boolean hideCursor);

    @Override
    protected native void _exitFullscreen(long ptr, boolean animate);

    @Override
    protected void _finishInputMethodComposition(long ptr) {
        if (imEnabled && isInPreeditMode) {
            // Discard any pre-edited text
            preedit.setLength(0);
            notifyInputMethod(preedit.toString(), null, null, null, 0, 0, 0);
        }
    }

    private void notifyPreeditMode(boolean enabled){
        isInPreeditMode = enabled;
    }


    protected void notifyInputMethodDraw(String text, int first, int length, int caret, byte[] attr) {
        int[] boundary = null;
        byte[] values = null;

        if (attributes == null ) {
            attributes = ByteBuffer.allocate(32);
        }

        if (length > 0) {
            preedit.replace(first, first + length, "");
        }

        if (text != null) {
            preedit.insert(first, text);
        } else {
            if (attr == null) {
                preedit.setLength(0);
            }
        }

        if (attributes.capacity() < preedit.length()) {
            ByteBuffer tmp  = ByteBuffer.allocate((int) (preedit.length() * 1.5));
            tmp.put(attributes);
            attributes = tmp;
        }

        attributes.limit(preedit.length());

        if (attr != null && attributes.limit() >= (first + attr.length)) {
            attributes.position(first);
            attributes.put(attr);
        }

        if (attributes.limit() > 0) {
            ArrayList<Integer> boundaryList = new ArrayList<>();
            ArrayList<Byte> valuesList = new ArrayList<>();
            attributes.rewind();
            byte lastAttribute = attributes.get();

            boundaryList.add(0);
            valuesList.add(lastAttribute);

            int i = 1;
            while (attributes.hasRemaining()) {
                byte a = attributes.get();
                if (lastAttribute != a) {
                    boundaryList.add(i);
                    valuesList.add(a);
                }
                lastAttribute = a;
                i++;
            }

            boundaryList.add(attributes.limit());

            boundary = new int[boundaryList.size()];
            i = 0;
            for (Integer e : boundaryList) {
                boundary[i++] = e;
            }

            values = new byte[valuesList.size()];
            i = 0;
            for (Byte e: valuesList) {
                values[i++] = e;
            }
        }

        notifyInputMethod(preedit.toString(), boundary, boundary, values, 0, caret, 0);
        lastCaret = caret;
    }

    protected void notifyInputMethodCaret(int pos, int direction, int style) {
        switch (direction) {
            case 0: //XIMForwardChar
                lastCaret += pos;
                break;
            case 1: //XIMBackwardChar
                lastCaret -= pos;
                break;
            case 10: //XIMAbsolute
                lastCaret = pos;
                break;
            default:
                //TODO: as we don't know the text structure, we cannot compute the position
                // for other directions (like forward words, lines, etc...).
                // Luckily, vast majority of IM uses XIMAbsolute (10)
        }
        notifyInputMethod(preedit.toString(), null, null, null, 0, lastCaret, 0);
    }
}
