/*
 * Copyright (c) 2020, Gluon and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package com.sun.glass.ui.web;

// import com.gluonhq.webscheduler.Util;
import com.sun.glass.ui.*;
import com.sun.glass.ui.CommonDialogs.ExtensionFilter;
import com.sun.glass.ui.CommonDialogs.FileChooserResult;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class WebApplication extends Application {

    static Method scheduleMethod;
    static Method intervalMethod;
    static Method uploadPixelMethod;

    static {
        try {
            Class c = Class.forName("com.gluonhq.webscheduler.Util");
            scheduleMethod = c.getMethod("schedule", Runnable.class);
            intervalMethod = c.getMethod("interval", Runnable.class);
            uploadPixelMethod = c.getMethod("uploadPixels", long.class, int[].class, int.class, int.class, int.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private native void _uploadPixelsIntArray(long viewPtr, int[] pixels, int offset, int width, int height);

    @Override
    protected void runLoop(final Runnable launchable) {
        // launchable.run();
// System.out.println ("[JVDBG] WEB runloop, schedule launchable now...");
        invokeLater(launchable);
// System.out.println ("[JVDBG] WEB runloop, scheduled launchable ...");
        // ClassLoader ccl = WebApplication.class.getClassLoader();
        // _runLoop(launchable, ccl);
    }

    private native void _runLoop(Runnable launchable, ClassLoader contextClassLoader);

    @Override
    public Window createWindow(Window owner, Screen screen, int styleMask) {
        return new WebWindow(owner, screen, styleMask);
    }

    @Override
    public Window createWindow(long parent) {
        throw new RuntimeException ("Not implemented");
    }

    @Override
    public View createView() {
        View answer = new WebGLView();
        return answer;
    }

    @Override
    public Cursor createCursor(int type) {
        return new WebCursor(type);
    }

    @Override
    public Timer createTimer(Runnable runnable) {
        return new WebTimer(runnable);
    }

    @Override
    protected Object _enterNestedEventLoop() {
        throw new RuntimeException ("Not implemented");
    }

    @Override
    protected void _leaveNestedEventLoop(Object retValue) {
        throw new RuntimeException ("Not implemented");
    }

    @Override
    public boolean hasTouch() {
        return true;
    }

    @Override
    public boolean hasMultiTouch() {
        return true;
    }

    protected native int _getKeyCodeForChar(char c);

    @Override
    native protected void _invokeAndWait(Runnable runnable);

    @Override
    protected void _invokeLater(Runnable runnable) {
// System.out.println("[WEB] invokelater asked, invoke " + scheduleMethod);
        try {
            scheduleMethod.invoke(null, runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
// System.out.println("[WEB] invokelater asked, invoked " + scheduleMethod);
// System.out.println("[WEB] runnable is scheduled: " + runnable);
    }

    public static void invokeOtherJob(Runnable runnable) {
// System.out.println("[WEB] invokeOtherJob will schedule " +runnable);
        try {
            scheduleMethod.invoke(null, runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
// System.out.println("[WEB] invokeOtherJob did schedule " + runnable);
    }

    public static void invokeOtherIntervalJob(Runnable runnable) {
// System.out.println("[WEB] invokeOtherJob will intervalschedule " +runnable);
        try {
            intervalMethod.invoke(null, runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
// System.out.println("[WEB] invokeOtherJob did sintervalchedule " + runnable);
    }

    @Override
    protected boolean _supportsTransparentWindows() {
        return true;
    }

    @Override protected boolean _supportsUnifiedWindows() {
        return false;
    }

    @Override
    protected long staticView_getMultiClickTime() {
        throw new RuntimeException ("Not implemented");
    }

    @Override
    protected int staticView_getMultiClickMaxX() {
        throw new RuntimeException ("Not implemented");
    }

    @Override
    protected int staticView_getMultiClickMaxY() {
        throw new RuntimeException ("Not implemented");
    }

    @Override
    protected int staticTimer_getMinPeriod() {
        return WebTimer.getMinPeriod_impl();
    }

    @Override
    protected int staticTimer_getMaxPeriod() {
        return WebTimer.getMaxPeriod_impl();
    }

    @Override
    protected FileChooserResult staticCommonDialogs_showFileChooser(Window owner, String folder, String filename, String title, int type, boolean multipleMode, ExtensionFilter[] extensionFilters, int defaultFilterIndex) {
        throw new RuntimeException ("Not implemented");
    }

    @Override
    protected File staticCommonDialogs_showFolderChooser(Window owner, String folder, String title) {
        throw new RuntimeException ("Not implemented");
    }
    @Override
    public Cursor createCursor(int x, int y, Pixels pixels) {
        throw new RuntimeException ("Not implemented");
    }

    @Override
    protected void staticCursor_setVisible(boolean visible) { }

    @Override
    protected Size staticCursor_getBestSize(int width, int height) {
        throw new RuntimeException ("Not implemented");
    }

    @Override
    public Pixels createPixels(int width, int height, ByteBuffer data) { 
        return new WebPixels(width, height, data);
    }

    @Override
    public Pixels createPixels(int width, int height, IntBuffer data) { 
        Pixels answer = new WebPixels(width, height, data);
// System.out.println("[WEBAPP] createPixels1 called, return " + answer);
        return answer;
    }

    @Override
    public Pixels createPixels(int width, int height, IntBuffer data, float scalex, float scaley) {
        Pixels answer = new WebPixels(width, height, data);
// System.out.println("[WEBAPP] createPixels called, return " + answer);
        return answer;
    }

    @Override
    protected int staticPixels_getNativeFormat() {
        return Pixels.Format.BYTE_BGRA_PRE;
    }

    @Override
    public GlassRobot createRobot() {
        throw new RuntimeException ("Not implemented");
    }

    @Override
    protected double staticScreen_getVideoRefreshPeriod() {
// System.out.println("[JVDBG] getVideoRefreshPeriod asked, return 0.0");
        return 0.0;
    }

    @Override
    protected Screen[] staticScreen_getScreens() {
        Screen s = new Screen (0,0,0,0,640,480,
                               0,0,640,480,
                               0,0,640,480,
                               10,10,
                               1f,1f,1f,1f);
        Screen[] answer = new Screen[1];
        answer[0] = s;
// System.out.println("WebApp.staticScreen_getScreens asked, return array with " + s);
// System.out.println("WebApp.staticScreen_getScreens asked, return array with " + s);
        return answer;
    }


}
