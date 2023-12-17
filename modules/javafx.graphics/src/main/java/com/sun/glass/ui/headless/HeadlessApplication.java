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
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HeadlessApplication extends Application {

    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private Queue<Runnable> queue = new LinkedList<>();
    private boolean active = false;

    private final RunnableProcessor runnableProcessor = new RunnableProcessor();

    public HeadlessApplication() {
    }

    @Override
    protected void runLoop(Runnable launchable) {
        runnableProcessor.invokeLater(launchable);
        Thread eventThread = new Thread(runnableProcessor);
/*
        Thread eventThread = new Thread() {
            @Override public void run() {
                launchable.run();
                runForever();
            }
        };
*/
        this.active = true;
        setEventThread(eventThread);
        eventThread.start();
    }

    @Override
    protected void _invokeAndWait(Runnable runnable) {
        runnableProcessor.invokeAndWait(runnable);
/*
        System.err.println("HP invokeAndWait!");
Thread.dumpStack();
        throw new UnsupportedOperationException();
*/
    }

    @Override
    protected void _invokeLater(Runnable runnable) {
        runnableProcessor.invokeLater(runnable);
/*
        synchronized (queue) {
            queue.add(runnable);
            queue.notifyAll();
        }
*/
    }

    @Override
    protected Object _enterNestedEventLoop() {
        return runnableProcessor.enterNestedEventLoop();
    }   

    @Override
    protected void _leaveNestedEventLoop(Object retValue) {
        runnableProcessor.leaveNestedEventLoop(retValue);
    }   

    @Override
    public Window createWindow(Window owner, Screen screen, int styleMask) {
        return new HeadlessWindow(owner, screen, styleMask);
    }   

    @Override
    public View createView() {
        return new HeadlessView();
    }

    @Override
    public Cursor createCursor(int type) {
        return new HeadlessCursor(type);
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
        return new HeadlessPixels(width, height, data);
    }

    @Override
    public Pixels createPixels(int width, int height, ByteBuffer data, float scalex, float scaley) {
        return new HeadlessPixels(width, height, data, scalex, scaley);
    }

    @Override
    public Pixels createPixels(int width, int height, IntBuffer data) {
        return new HeadlessPixels(width, height, data);
    }

    @Override
    public Pixels createPixels(int width, int height, IntBuffer data, float scalex, float scaley) {
        return new HeadlessPixels(width, height, data, scalex, scaley);
    }

    @Override
    protected int staticPixels_getNativeFormat() {
        return Pixels.Format.BYTE_BGRA_PRE;
    }

    @Override
    public GlassRobot createRobot() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected double staticScreen_getVideoRefreshPeriod() {
        return 0.;
    }

    @Override
    protected Screen[] staticScreen_getScreens() {
        Screen screen = new Screen(0, 32, 0, 0, 1000,1000,
0, 0, 1000, 1000, 0, 0, 1000, 1000, 100, 100, 1f,1f, 1f,1f);
        Screen[] answer = new Screen[1];
        answer[0] = screen;
        return answer;
        // throw new UnsupportedOperationException();
    }

    @Override
    public Timer createTimer(Runnable runnable) {
        return new HeadlessTimer(runnable);
    }

    @Override
    protected int staticTimer_getMinPeriod() {
        return 0;
    }

    @Override
    protected int staticTimer_getMaxPeriod() {
        return 1000000;
    }

    @Override
    public boolean hasWindowManager() {
        return false;
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
        return false;
    }

    @Override
    protected boolean _supportsUnifiedWindows() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasTwoLevelFocus() {
        return false;
    }

    @Override
    public boolean hasVirtualKeyboard() {
        return false;
    }

    @Override
    public boolean hasTouch() {
        return false;
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
        System.err.println("Terminating HeadlessApplication");
        this.active = false;
        setEventThread(null);
        super.finishTerminating();
    }

    @Override
    protected int _getKeyCodeForChar(char c) {
        throw new UnsupportedOperationException();
    }


    private void runForever() {
        while (active) {
            Runnable r = null;
            synchronized (queue) {
                r = queue.poll();
            }
            if (r != null) {
                r.run();
            } else {
                synchronized (queue) {
                    try {
                        queue.wait(10000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }
    }

    static class HeadlessCursor extends Cursor {

        protected HeadlessCursor (int type) {
            super(type);
        }

        protected long _createCursor(int x, int y, Pixels pixels) {
            return -1;
        }
    }
}
