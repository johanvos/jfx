package com.sun.glass.ui.headless;

import com.sun.glass.ui.Application;
import com.sun.glass.ui.Menu;
import com.sun.glass.ui.MenuBar;
import com.sun.glass.ui.MenuItem;
import com.sun.glass.ui.PlatformFactory;
import com.sun.glass.ui.delegate.ClipboardDelegate;
import com.sun.glass.ui.delegate.MenuBarDelegate;
import com.sun.glass.ui.delegate.MenuDelegate;
import com.sun.glass.ui.delegate.MenuItemDelegate;

public final class HeadlessPlatformFactory extends PlatformFactory {

    public Application createApplication() {
        return new HeadlessApplication();
    }

    public MenuBarDelegate createMenuBarDelegate(MenuBar menubar) {
        throw new UnsupportedOperationException();
    }

    public MenuDelegate createMenuDelegate(Menu menu) {
        throw new UnsupportedOperationException();
    }

    public MenuItemDelegate createMenuItemDelegate(MenuItem menuItem) {
        throw new UnsupportedOperationException();
    }

    public ClipboardDelegate createClipboardDelegate() {
        throw new UnsupportedOperationException();
    }


}
