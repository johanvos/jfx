package com.sun.glass.ui.headless;

import com.sun.glass.events.MouseEvent;
import com.sun.glass.events.WindowEvent;
import com.sun.glass.ui.Cursor;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.Screen;
import com.sun.glass.ui.View;
import com.sun.glass.ui.Window;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.scene.paint.Color;

public class HeadlessWindow extends Window {

    private static final AtomicInteger ptrCount = new AtomicInteger(0);
    private long ptr;
    private final HeadlessWindowManager windowManager;
    
    private int minWidth;
    private int minHeight;
    private int maxWidth = -1;
    private int maxHeight = -1;
    private int originalX, originalY, originalWidth, originalHeight;
    
    private boolean resizable;
    private boolean visible;
    private boolean isFocusable;
    private boolean enabled;
    private boolean closed;
    private float bg_r, bg_g, bg_b;
    private float alpha;
    private Pixels icon;
    private Cursor cursor;
    private IntBuffer screenBuffer;
    private final ByteBuffer frameBuffer;
    private HeadlessView currentView;
    private HeadlessRobot robot;

    public HeadlessWindow(HeadlessWindowManager wm, Window owner, Screen screen, ByteBuffer frameBuffer, int styleMask) {
        super(owner, screen, styleMask);
        this.frameBuffer = frameBuffer;
        this.windowManager = wm;
        Thread.dumpStack();
//        notifyResizeAndMove(1,1,100,100);
//        System.err.println("[HW] x = "+getX()+", screen = " + screen+" with screenw = "+screen.getWidth()+", this = "+this);
        screenBuffer = IntBuffer.allocate(screen.getWidth() * screen.getHeight());
    }

    @Override
    protected long _createWindow(long ownerPtr, long screenPtr, int mask) {
        this.ptr = ptrCount.incrementAndGet();
        return ptr;
    }

    @Override
    protected boolean _close(long ptr) {
        System.err.println("[HW] HWCLOSE "+this+", robot = "+this.robot);
        Thread.dumpStack();
        this.closed = true;
        this.notifyDestroy();
        if (this.robot != null) {
            this.robot.windowRemoved(this);
        }
        System.err.println("[HW] HWCLOSEDONE ");
        return true;
    }

    @Override
    protected boolean _setView(long ptr, View view) {
        System.err.println("[HW] SETVIEW called for "+this+" with view = "+view);
        if (currentView != null) {
            currentView.notifyMouse(MouseEvent.EXIT, MouseEvent.BUTTON_NONE, 0,0,0,0,0, false, false);
        }
        this.currentView = (HeadlessView)view;
        if (currentView != null) {
            currentView.notifyMouse(MouseEvent.ENTER, MouseEvent.BUTTON_NONE, 0,0,0,0,0, false, false);
        }
        return true;
    }

    @Override
    protected void _updateViewSize(long ptr) {
    }

    @Override
    protected boolean _setMenubar(long ptr, long menubarPtr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected boolean _minimize(long ptr, boolean minimize) {
        int cx = this.x;
        int cy = this.y;
        int cw = this.width;
        int ch = this.height;

        notifyResize(minimize ? WindowEvent.MINIMIZE : WindowEvent.RESTORE, width, height);
        windowManager.repaintAll();
     //   ((HeadlessView) this.getView()).notifyRepaint(cx, cy, cw, ch);
//        if (this.robot != null) {
//            this.robot.windowRemoved(this);
//        }
        return true;
    }

    @Override
    protected boolean _maximize(long ptr, boolean maximize, boolean wasMaximized) {
        int newX = 0;
        int newY = 0;
        int newWidth = 0;
        int newHeight = 0;
        if (maximize && !wasMaximized) {
            this.originalHeight = this.height;
            this.originalWidth = this.width;
            this.originalX = this.x; 
            this.originalY = this.y; 
            newX = 0;
            newY = 0;
            newWidth = screen.getWidth();
            newHeight = screen.getHeight();
            setState(State.MAXIMIZED);
        } else if (!maximize && wasMaximized) {
            newHeight = this.originalHeight;
            newWidth = this.originalWidth;
            newX = this.originalX;
            newY = this.originalY;
            setState(State.NORMAL);
        }
        notifyResizeAndMove(newX, newY, newWidth, newHeight);
        if (maximize) {
            notifyResize(WindowEvent.MAXIMIZE, newWidth, newHeight);
        }

        return maximize;
    }

    @Override
    protected void _setBounds(long ptr, int x, int y, boolean xSet, boolean ySet, int w, int h, int cw, int ch, float xGravity, float yGravity) {
//        Thread.dumpStack();

//        System.err.println("[HW] setBounds, x = "+x);
        int newWidth = w > 0 ? w : cw > 0 ? cw : getWidth();
        int newHeight = h > 0 ? h : ch > 0 ? ch : getHeight();
        if (!xSet) {
            x = getX();
        }
        if (!ySet) {
            y = getY();
        }
        if (maxWidth >= 0) {
            newWidth = Math.min(newWidth, maxWidth);
        }
        if (maxHeight >= 0) {
            newHeight = Math.min(newHeight, maxHeight);
        }
        newWidth = Math.max(newWidth, minWidth);
        newHeight = Math.max(newHeight, minHeight);
        notifyResizeAndMove(x, y, newWidth, newHeight);
        screenBuffer = IntBuffer.allocate(getWidth() * getHeight());
    }

    @Override
    protected boolean _setVisible(long ptr, boolean v) {
        this.visible = v;
        return this.visible;
    }

    @Override
    protected boolean _setResizable(long ptr, boolean resizable) {
        this.resizable = resizable;
        return true;
    }

    @Override
    protected boolean _requestFocus(long ptr, int event) {
        this.notifyFocus(event);
        return this.isFocused();
    }

    @Override
    protected void _setFocusable(long ptr, boolean isFocusable) {
        this.isFocusable = isFocusable;
    }

    @Override
    protected boolean _grabFocus(long ptr) {
        return true;
    }

    @Override
    protected void _ungrabFocus(long ptr) {
    }

    @Override
    protected boolean _setTitle(long ptr, String title) {
        return true;
    }

    @Override
    protected void _setLevel(long ptr, int level) {
    }

    @Override
    protected void _setAlpha(long ptr, float alpha) {
        this.alpha = alpha;
    }

    @Override
    protected boolean _setBackground(long ptr, float r, float g, float b) {
        this.bg_r = r;
        this.bg_g = g;
        this.bg_b = b;
        return true;
    }

    @Override
    protected void _setEnabled(long ptr, boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected boolean _setMinimumSize(long ptr, int width, int height) {
        this.minWidth = width;
        this.minHeight = height;
        return true;
    }

    @Override
    protected boolean _setMaximumSize(long ptr, int width, int height) {
        this.maxWidth = width;
        this.maxHeight = height;
        return true;
    }

    @Override
    protected void _setIcon(long ptr, Pixels pixels) {
        this.icon = pixels;
    }

    @Override
    protected void _setCursor(long ptr, Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    protected void _toFront(long ptr) {
    }

    @Override
    protected void _toBack(long ptr) {
    }

    @Override
    protected void _enterModal(long ptr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected void _enterModalWithWindow(long dialog, long window) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected void _exitModal(long ptr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected void _requestInput(long ptr, String text, int type, double width, double height, double Mxx, double Mxy, double Mxz, double Mxt, double Myx, double Myy, double Myz, double Myt, double Mzx, double Mzy, double Mzz, double Mzt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected void _releaseInput(long ptr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    boolean setFullscreen(boolean full) { 
        int newX = 0;
        int newY = 0;
        int newWidth = 0;
        int newHeight = 0;
        if (full) {
            this.originalHeight = this.height;
            this.originalWidth = this.width;
            this.originalX = this.x; 
            this.originalY = this.y; 
            newX = 0;
            newY = 0;
            newWidth = screen.getWidth();
            newHeight = screen.getHeight();
        } else  { 
            newHeight = this.originalHeight;
            newWidth = this.originalWidth;
            newX = this.originalX;
            newY = this.originalY;
        }
        notifyResizeAndMove(newX, newY, newWidth, newHeight);
        return full;
    }
    
    private void notifyResizeAndMove(int x, int y, int width, int height) {
        System.err.println("[HW] resizeandmove, x = "+x+", width = "+width);
        Thread.dumpStack();
        HeadlessView view = (HeadlessView) getView();
        System.err.println("[HW] resizeandmovem getWidth = "+getWidth()+", width = "+width+", view = "+view);
     //   if (getWidth() != width || getHeight() != height) {
            System.err.println("[HW] getWidth = "+getWidth()+", not1");
            notifyResize(WindowEvent.RESIZE, width, height);
            System.err.println("[HW] getWidth = "+getWidth()+", not1 done, view = "+view);
            if (view != null) {
                view.notifyResize(width, height); 
                System.err.println("[HW] getWidth = "+getWidth()+", not2 done, view = "+view);
            }
     //  }
        if (getX() != x || getY() != y) {
            notifyMove(x, y);
        }
    }

    public Color getColor(int lx, int ly) {
        int mx = lx;// + getX();
        int my = ly;// + getY();
        int idx = 1000*my+mx;
        System.err.println("GET val on " + this.ptr+" for "+idx+"(local: "+lx+", " + ly+"), (global "+mx+", "+my+")");
        int rgba = frameBuffer.asIntBuffer().get(idx);
        int a = (rgba >> 24) & 0xFF;
        int r = (rgba >> 16) & 0xFF;
        int g = (rgba >> 8) & 0xFF;
        int b = rgba & 0xFF;

        Color color = Color.color(
                r / 255.0,
                g / 255.0,
                b / 255.0,
                a / 255.0
        );
                System.err.println("GOT "+rgba+" = "+color);

        return color;
    }

    public void getScreenCapture(int x, int y, int width, int height, int[] data, boolean scaleToFit) {
        System.err.println("[HW] gcc");
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int idx = i * width +j;
                int fidx = (y+i)*1000 + x+j;
                int val = frameBuffer.asIntBuffer().get(fidx);
                data[idx] = val;
            }
        }
//        Pixels pixels = view.getPixels();
//        System.err.println("[ROBOT] GCC, x = "+x+", y = " +y+", w = "+width+", h = "+height+", dsize = "+data.length+", winx = "+activeWindow.getX());
//        System.err.println("[ROBOT] pixels with size "+pixels.getWidth()+" h = "+pixels.getHeight()); // +" and buff = "+pixels.asByteBuffer().remaining());
//        int num = width * height/4;
//        Buffer buffer = pixels.getBuffer();
//        System.err.println("GOT buffer: "+buffer);
//        IntBuffer buffer2 = (IntBuffer) buffer.duplicate(); // preserve original
//buffer2.position(0);
//buffer2.get(data);
//        ByteBuffer buffer = pixels.asByteBuffer();
//        for (int i = 0; i < num; i++) {
//    data[i] = buffer.getInt();
//}
    }
    void blit(Pixels pixels) {
        System.err.println("BLIT");
        int pW = pixels.getWidth();
        int pH = pixels.getHeight();
        int offsetX = this.getX();
        int offsetY = this.getY();
        int stride = 1000;
        
        IntBuffer intBuffer = (IntBuffer) pixels.getBuffer();
        System.err.println("offsetX = "+offsetX+", offSetY = "+offsetY+", pw = "+pW+", pH = "+pH);
        
       // IntBuffer intBuffer = pixels.asByteBuffer().asIntBuffer();
        
        for (int i = 0; i < pixels.getHeight(); i ++) {
            int rowIdx = offsetY + i;
            for (int j = 0; j < pixels.getWidth(); j++) {
                int idx = rowIdx * stride + offsetX + j;
                int val = intBuffer.get(i * pixels.getWidth() + j);
                if (val != 0) {
             //       System.err.println("add "+val+" to "+idx+" which is ("+i+", "+j+")");
                }
                frameBuffer.asIntBuffer().put(idx, val);
            }
        }
        Color color = this.getColor(100, 100);
        System.err.println("After BLIT, 100100 -> "+frameBuffer.asIntBuffer().get(100100) + " or clr = "+color);

    }

    void setRobot(HeadlessRobot activeRobot) {
        this.robot = activeRobot;
    }
}
