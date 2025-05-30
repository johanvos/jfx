package com.sun.glass.ui.headless;

import com.sun.glass.events.KeyEvent;
import com.sun.glass.events.MouseEvent;
import com.sun.glass.ui.Application;
import com.sun.glass.ui.GlassRobot;
import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.Window;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

public class HeadlessRobot extends GlassRobot {

    final int multiplierX = 40;
    final int multiplierY = 40;
    private final HeadlessApplication application;
    private Window activeWindow = null;

    private double mouseX, mouseY;

    private final SpecialKeys specialKeys = new SpecialKeys();
    private final char[] NO_CHAR = { };

    public HeadlessRobot(HeadlessApplication application) {
        this.application = application;
        System.err.println("Created HR, windows = "+ Window.getWindows()+", current = "+activeWindow);
    }

    void windowAdded(HeadlessWindow window) {
        if (this.activeWindow == null) activeWindow = window;
    }

    @Override
    public void create() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void keyPress(KeyCode keyCode) {
        checkWindowFocused();
        if (activeWindow == null) return;
        HeadlessView view = (HeadlessView)activeWindow.getView();
        int code = keyCode.getCode();
        processSpecialKeys(code, true);
        char[] keyval = getKeyChars(code);
        int mods = getKeyModifiers();
        if (view != null) {
            view.notifyKey(KeyEvent.PRESS, code, keyval, mods);
            if (keyval.length > 0) { 
                view.notifyKey(KeyEvent.TYPED, 0, keyval, mods);
            }

        }
    }

    @Override
    public void keyRelease(KeyCode keyCode) {
        checkWindowFocused();
        if (activeWindow == null) return; 
        HeadlessView view = (HeadlessView)activeWindow.getView();
        int code = keyCode.getCode();
        processSpecialKeys(code, false); 
        int mods = getKeyModifiers();
        char[] keyval = new char[1];
        keyval[0] = (char) code;
        if (view != null) { 
            view.notifyKey(KeyEvent.RELEASE, code, keyval, mods);
        }
    }

    @Override
    public double getMouseX() {
        return this.mouseX;
    }

    @Override
    public double getMouseY() {
        return this.mouseY;
    }

    @Override
    public void mouseMove(double x, double y) {
//        Thread.dumpStack();
        this.mouseX = x;
        this.mouseY = y;
        checkWindowEnterExit();
        if (activeWindow == null) return; 
        HeadlessView view = (HeadlessView)activeWindow.getView();
        if (view == null) return; 
        int wx = activeWindow.getX();
        int wy = activeWindow.getY();
        System.err.println("MOUSEMOVE to "+x+", "+y+" and wx = "+wx+" and wy = "+wy);
        int modifiers = 0;
        view.notifyMouse(MouseEvent.MOVE, GlassRobot.convertToRobotMouseButton(new MouseButton[0]), (int)mouseX-wx, (int)mouseY-wy, (int)mouseX, (int)mouseY, modifiers, false, false);
    }

    @Override
    public void mousePress(MouseButton... buttons) {
        Thread.dumpStack();
        System.err.println("PRESS "+Arrays.asList(buttons));
        Application.checkEventThread();
        checkWindowEnterExit();
        HeadlessView view = (HeadlessView)activeWindow.getView();
        if (view == null) { 
            view = (HeadlessView)activeWindow.getView();
            if (view == null) { 
                System.err.println("no view for this window, return");
            }
        }
        int wx = activeWindow.getX();
        int wy = activeWindow.getY();
        int modifiers = getModifiers(buttons);
        System.err.println("MODS = "+modifiers);
        view.notifyMouse(MouseEvent.DOWN, getGlassEventButton(buttons), (int)mouseX-wx, (int)mouseY-wy, (int)mouseX, (int)mouseY, modifiers, true, true);
    }

    @Override
    public void mouseRelease(MouseButton... buttons) {
        Thread.dumpStack();
        Application.checkEventThread();
        checkWindowEnterExit();
        if (this.activeWindow == null) {
            System.err.println("NO active window, don't process");
            return;
        }
        HeadlessView view = (HeadlessView) activeWindow.getView();
        int wx = activeWindow.getX();
        int wy = activeWindow.getY();
        int modifiers = getModifiers(buttons);
        System.err.println("MODS2 = "+modifiers);
        view.notifyMouse(MouseEvent.UP, getGlassEventButton(buttons), (int) mouseX - wx, (int) mouseY - wy, (int) mouseX, (int) mouseY, modifiers, true, true);
    }

    @Override
    public void mouseWheel(int wheelAmt) {
        Thread.dumpStack();
        checkWindowFocused();
//        checkWindowEnterExit();

        final int dff = wheelAmt > 0 ? -1 : 1;
        HeadlessView view = (HeadlessView) activeWindow.getView();

        int wx = activeWindow.getX();
        int wy = activeWindow.getY();
        int repeat = Math.abs(wheelAmt);
        for (int i = 0; i < repeat; i++) {
            System.err.println("PART "+i+" FROM "+repeat);
//            this.mouseX = this.mouseX + dff;
//            this.mouseY = this.mouseY + dff;     
//            view.notifyMouse(MouseEvent.MOVE, MouseEvent.BUTTON_NONE, (int) mouseX - wx, (int) mouseY - wy, (int) mouseX, (int) mouseY, 0, true, true);
System.err.println("STARTNOT at "+Thread.currentThread());    
int mods = 0;
view.notifyScroll((int) mouseX, (int) mouseY, wx, wy, 0, dff, mods, 0, 0, 0, 0, multiplierX, multiplierY);
            System.err.println("DONENOT");
        }
    }

    @Override
    public Color getPixelColor(double x, double y) {
        checkWindowFocused();
        return ((HeadlessWindow)activeWindow).getColor((int)x, (int)y);
//        
//        HeadlessView view = (HeadlessView) activeWindow.getView();
//        Pixels pixels = view.getPixels();
//        System.err.println("[HR] getPixelColor for x = "+x+", y = "+y+" and pixels w = "+pixels.getWidth()+" and h = "+pixels.getHeight());
//        IntBuffer buffer = (IntBuffer) pixels.getBuffer();
//
//        int x0 = (int) x;
//        int y0 = (int) y;
//        int idx = y0 * pixels.getWidth() + x0;
//        int rgba = buffer.get(idx);
//        int a = (rgba >> 24) & 0xFF;
//        int r = (rgba >> 16) & 0xFF;
//        int g = (rgba >> 8) & 0xFF;
//        int b = rgba & 0xFF;
//
//        Color color = Color.color(
//                r / 255.0,
//                g / 255.0,
//                b / 255.0,
//                a / 255.0
//        );
//        return color;
    }

    @Override
    public WritableImage getScreenCapture(WritableImage image, double x, double y, double width, double height, boolean scaleToFit) {
        return super.getScreenCapture(image, x, y, width, height, scaleToFit); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public void getScreenCapture(int x, int y, int width, int height, int[] data, boolean scaleToFit) {
        checkWindowFocused();
        ((HeadlessWindow)activeWindow).getScreenCapture(x, y, width, height, data, scaleToFit);
//        activeWindow.getS
//        checkWindowFocused();
//        HeadlessView view = (HeadlessView) activeWindow.getView();
//        Pixels pixels = view.getPixels();
//        System.err.println("[ROBOT] GCC, x = "+x+", y = " +y+", w = "+width+", h = "+height+", dsize = "+data.length+", winx = "+activeWindow.getX());
//        System.err.println("[ROBOT] pixels with size "+pixels.getWidth()+" h = "+pixels.getHeight()); // +" and buff = "+pixels.asByteBuffer().remaining());
//        int num = width * height/4;
//        Buffer buffer = pixels.getBuffer();
//        System.err.println("GOT buffer: "+buffer);
//        IntBuffer buffer2 = (IntBuffer) buffer.duplicate(); // preserve original
//buffer2.position(0);
//buffer2.get(data);
    }

    private void checkActiveWindowExists() {
        if ((this.activeWindow != null) && (!this.activeWindow.isVisible())) {
            System.err.println("[HR] activeWindow "+Objects.hashCode(this.activeWindow)+" invisible, set null.");
            this.activeWindow = null;
        }
    }
    private void checkWindowFocused() {
        checkActiveWindowExists();
        this.activeWindow = getFocusedWindow();
    }

    private void checkWindowEnterExit() {
        checkActiveWindowExists();
        Window oldWindow = activeWindow;
        this.activeWindow = getTargetWindow(this.mouseX, this.mouseY);

        if (this.activeWindow == null) {
            if (oldWindow != null) {
                HeadlessView oldView = (HeadlessView)oldWindow.getView();
                if (oldView != null) {
                    oldView.notifyMouse(MouseEvent.EXIT, MouseEvent.BUTTON_NONE, 0, 0,0,0, 0, true, true);
                }
            }
            return;
        } 
        int wx = activeWindow.getX();
        int wy = activeWindow.getY();

        if (activeWindow != oldWindow) {
            HeadlessView view = (HeadlessView)activeWindow.getView();
            int modifiers = 0;
            view.notifyMouse(MouseEvent.ENTER, MouseEvent.BUTTON_NONE, (int)mouseX-wx, (int)mouseY-wy, (int)mouseX, (int)mouseY, modifiers, true, true);
            if (oldWindow != null) { 
                HeadlessView oldView = (HeadlessView)oldWindow.getView();
                if (oldView != null) { 
                    int owx = oldWindow.getX();
                    int owy = oldWindow.getY();
                    oldView.notifyMouse(MouseEvent.EXIT, MouseEvent.BUTTON_NONE, (int)mouseX-owx, (int)mouseY-owy, (int)mouseX, (int)mouseY, modifiers, true, true);
                }
            }
        }
    }

    private HeadlessWindow getFocusedWindow() {
        List<Window> windows = Window.getWindows().stream()
                .filter(win -> win.getView()!= null)
                .filter(win -> !win.isClosed())
                .filter(win -> win.isFocused()).toList();
        if (windows.isEmpty()) return null;
        if (windows.size() == 1) return (HeadlessWindow)windows.get(0);
        return (HeadlessWindow)windows.get(windows.size() -1);
    }

    private HeadlessWindow getTargetWindow(double x, double y) {
        System.err.println("GTW for x = "+x+", y = "+y+" and windows = "+ Window.getWindows()+", current = "+activeWindow);
        List<Window> windows = Window.getWindows().stream()
                .filter(win -> win.getView()!= null)
                .filter(win -> !win.isClosed())
                .filter(win -> (x >= win.getX() && x <= win.getX() + win.getWidth()
                        && y >= win.getY() && y <= win.getY()+ win.getHeight())).toList();
        if (windows.isEmpty()) {
            return null;
        }
        if (windows.size() == 1) return (HeadlessWindow)windows.get(0);
        return (HeadlessWindow)windows.get(windows.size() -1);
    }

    int getModifiers(MouseButton... buttons) {
        int modifiers = KeyEvent.MODIFIER_NONE;
        for (int i = 0; i < buttons.length; i++) {
            switch (buttons[i]) {
                case PRIMARY:
                    modifiers |= KeyEvent.MODIFIER_BUTTON_PRIMARY;
                    break;
                case MIDDLE:
                    modifiers |= KeyEvent.MODIFIER_BUTTON_MIDDLE;
                    break;
                case SECONDARY:
                    modifiers |= KeyEvent.MODIFIER_BUTTON_SECONDARY;
                    break;
                case BACK:
                    modifiers |= KeyEvent.MODIFIER_BUTTON_BACK;
                    break;
                case FORWARD:
                    modifiers |= KeyEvent.MODIFIER_BUTTON_FORWARD;
                    break;
            }
        }
        return modifiers;
    }

    int getGlassEventButton(MouseButton[] buttons) {
        if ((buttons == null) || (buttons.length == 0)) return 0;
        return getGlassEventButton(buttons[0]);
    }

    int getGlassEventButton(MouseButton button) {
        if (button == MouseButton.SECONDARY) return MouseEvent.BUTTON_RIGHT;
        if (button == MouseButton.PRIMARY) return MouseEvent.BUTTON_LEFT;
        if (button == MouseButton.MIDDLE) return MouseEvent.BUTTON_OTHER;
        if (button == MouseButton.BACK) return MouseEvent.BUTTON_BACK;
        if (button == MouseButton.FORWARD) return MouseEvent.BUTTON_FORWARD;
        return MouseEvent.BUTTON_NONE;
    }

    private void processSpecialKeys(int c, boolean on) {
        if (c == KeyEvent.VK_CONTROL) {
            this.specialKeys.keyControl = on;
        }
        if (c == KeyEvent.VK_SHIFT) {
            this.specialKeys.keyShift = on;
        }
        if (c == KeyEvent.VK_COMMAND) {
            this.specialKeys.keyCommand = on;
        }
        if (c == KeyEvent.VK_ALT) {
            this.specialKeys.keyAlt = on;
        }
    }
    private char[] getKeyChars(int key) {
        char c = '\000';
        boolean shifted = this.specialKeys.keyShift;
        // TODO: implement configurable keyboard mappings.
        // The following is only for US keyboards
        if (key >= KeyEvent.VK_A && key <= KeyEvent.VK_Z) {
            shifted ^= this.specialKeys.capsLock;
            if (shifted) {
                c = (char) (key - KeyEvent.VK_A + 'A');
            } else {
                c = (char) (key - KeyEvent.VK_A + 'a');
            }
        } else if (key >= KeyEvent.VK_NUMPAD0 && key <= KeyEvent.VK_NUMPAD9) {
            if (this.specialKeys.numLock) {
                c = (char) (key - KeyEvent.VK_NUMPAD0 + '0');
            }
        } else if (key >= KeyEvent.VK_0 && key <= KeyEvent.VK_9) {
            if (shifted) {
                switch (key) {
                    case KeyEvent.VK_0: c = ')'; break;
                    case KeyEvent.VK_1: c = '!'; break;
                    case KeyEvent.VK_2: c = '@'; break;
                    case KeyEvent.VK_3: c = '#'; break;
                    case KeyEvent.VK_4: c = '$'; break;
                    case KeyEvent.VK_5: c = '%'; break;
                    case KeyEvent.VK_6: c = '^'; break;
                    case KeyEvent.VK_7: c = '&'; break;
                    case KeyEvent.VK_8: c = '*'; break;
                    case KeyEvent.VK_9: c = '('; break;
                }
            } else {
                c = (char) (key - KeyEvent.VK_0 + '0');
            }
        } else if (key == KeyEvent.VK_SPACE) {
            c = ' ';
        } else if (key == KeyEvent.VK_TAB) {
            c = '\t';
        } else if (key == KeyEvent.VK_ENTER) {
            c = (char)13;
        } else if (key == KeyEvent.VK_MULTIPLY) {
            c = '*';
        } else if (key == KeyEvent.VK_DIVIDE) {
            c = '/';
        } else if (shifted) {
            switch (key) {
                case KeyEvent.VK_BACK_QUOTE: c = '~'; break;
                case KeyEvent.VK_COMMA: c = '<'; break;
                case KeyEvent.VK_PERIOD: c = '>'; break;
                case KeyEvent.VK_SLASH: c = '?'; break;
                case KeyEvent.VK_SEMICOLON: c = ':'; break;
                case KeyEvent.VK_QUOTE: c = '\"'; break;
                case KeyEvent.VK_BRACELEFT: c = '{'; break;
                case KeyEvent.VK_BRACERIGHT: c = '}'; break;
                case KeyEvent.VK_BACK_SLASH: c = '|'; break;
                case KeyEvent.VK_MINUS: c = '_'; break;
                case KeyEvent.VK_EQUALS: c = '+'; break;
            }        } else {
            switch (key) {
                case KeyEvent.VK_BACK_QUOTE: c = '`'; break;
                case KeyEvent.VK_COMMA: c = ','; break;
                case KeyEvent.VK_PERIOD: c = '.'; break;
                case KeyEvent.VK_SLASH: c = '/'; break;
                case KeyEvent.VK_SEMICOLON: c = ';'; break;
                case KeyEvent.VK_QUOTE: c = '\''; break;
                case KeyEvent.VK_BRACELEFT: c = '['; break;
                case KeyEvent.VK_BRACERIGHT: c = ']'; break;
                case KeyEvent.VK_BACK_SLASH: c = '\\'; break;
                case KeyEvent.VK_MINUS: c = '-'; break;
                case KeyEvent.VK_EQUALS: c = '='; break;
            }
        }
        return c == '\000' ? NO_CHAR : new char[] { c };
    }


    private int getKeyModifiers() {
        int answer = 0;
        if (this.specialKeys.keyControl) answer = answer | KeyEvent.MODIFIER_CONTROL;
        if (this.specialKeys.keyShift) answer = answer | KeyEvent.MODIFIER_SHIFT;
        if (this.specialKeys.keyCommand) answer = answer | KeyEvent.MODIFIER_COMMAND;
        if (this.specialKeys.keyAlt) answer = answer | KeyEvent.MODIFIER_ALT;
        return answer;
    }

    class SpecialKeys {
        boolean keyControl;
        boolean keyShift;
        boolean keyCommand;
        boolean keyAlt;
        boolean capsLock;
        boolean numLock;
    }
}
