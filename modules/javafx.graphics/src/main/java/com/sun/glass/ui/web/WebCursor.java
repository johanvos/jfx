package com.sun.glass.ui.web;

import com.sun.glass.ui.Cursor;
import com.sun.glass.ui.Pixels;

final class WebCursor extends Cursor {
    protected WebCursor(int type) {
        super(type);
    }

    protected WebCursor(int x, int y, Pixels pixels) {
        super(x, y, pixels);
    }

    @Override
    native protected long _createCursor(int x, int y, Pixels pixels);
    native private void _set(int type);
    native private void _setCustom(long ptr);

}
