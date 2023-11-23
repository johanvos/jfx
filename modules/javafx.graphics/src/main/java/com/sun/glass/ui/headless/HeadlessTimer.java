package com.sun.glass.ui.headless;

import com.sun.glass.ui.Timer;

final class HeadlessTimer extends Timer {

    HeadlessTimer(final Runnable runnable) {
        super(runnable);
    }

    @Override protected long _start(final Runnable runnable, int period) {
        throw new UnsupportedOperationException();
    }

    @Override protected long _start(Runnable runnable) {
        throw new RuntimeException("vsync timer not supported");
    }   

    @Override protected void _stop(long timer) {
        throw new UnsupportedOperationException();
    }   

    @Override protected void _pause(long timer) {}
    @Override protected void _resume(long timer) {}
}
