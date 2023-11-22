package com.sun.glass.ui.headless;

import com.sun.glass.ui.Application;
import com.sun.glass.ui.CommonDialogs.ExtensionFilter;
import com.sun.glass.ui.CommonDialogs.FileChooserResult;
import com.sun.glass.ui.Cursor;
import com.sun.glass.ui.GlassRobot;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.Screen;
import com.sun.glass.ui.Size;
import com.sun.glass.ui.Timer;
import com.sun.glass.ui.View;
import com.sun.glass.ui.Window;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class HeadlessApplication extends Application {

    public HeadlessApplication() {
    }

    @Override
    protected void runLoop(Runnable launchable) {
        Thread eventThread = new Thread() {
            @Override public void run() {
                runForever();
            }
        };
        setEventThread(eventThread);
        eventThread.start();
    }

    @Override
    protected void _invokeAndWait(Runnable runnable) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void _invokeLater(Runnable runnable) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object _enterNestedEventLoop() {
        throw new UnsupportedOperationException();
    }   

    @Override
    protected void _leaveNestedEventLoop(Object retValue) {
        throw new UnsupportedOperationException();
    }   

    @Override
    public Window createWindow(Window owner, Screen screen, int styleMask) {
        throw new UnsupportedOperationException();
    }   

    @Override
    public View createView() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cursor createCursor(int type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cursor createCursor(int x, int y, Pixels pixels) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void staticCursor_setVisible(boolean visible) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Size staticCursor_getBestSize(int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pixels createPixels(int width, int height, ByteBuffer data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pixels createPixels(int width, int height, ByteBuffer data, float scalex, float scaley) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pixels createPixels(int width, int height, IntBuffer data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pixels createPixels(int width, int height, IntBuffer data, float scalex, float scaley) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int staticPixels_getNativeFormat() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GlassRobot createRobot() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected double staticScreen_getVideoRefreshPeriod() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Screen[] staticScreen_getScreens() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Timer createTimer(Runnable runnable) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int staticTimer_getMinPeriod() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int staticTimer_getMaxPeriod() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasWindowManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected FileChooserResult staticCommonDialogs_showFileChooser(
            Window owner, String folder, String filename, String title,
            int type, boolean multipleMode,
            ExtensionFilter[] extensionFilters,
            int defaultFilterIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected File staticCommonDialogs_showFolderChooser(Window owner,
                                                         String folder,
                                                         String title) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected long staticView_getMultiClickTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int staticView_getMultiClickMaxX() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int staticView_getMultiClickMaxY() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean _supportsTransparentWindows() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean _supportsUnifiedWindows() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasTwoLevelFocus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasVirtualKeyboard() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasTouch() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasMultiTouch() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasPointer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyRenderingFinished() {
    }

    @Override
    protected void finishTerminating() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int _getKeyCodeForChar(char c) {
        throw new UnsupportedOperationException();
    }


    private void runForever() {
        int i = 0;
        while (i < 1000000) {
            try {
                Thread.sleep(10000);
                System.err.println(i);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
