
package com.sun.javafx.tk.quantum;

import com.sun.glass.ui.Menu;
import com.sun.javafx.menu.MenuBase;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GlassSystemMenuShim extends GlassSystemMenu {

    private GlassSystemMenu gsm;
            final ArrayList<WeakReference<Menu>> uncollectedMenus = new ArrayList<>();

    public GlassSystemMenuShim() {
        super();
    }

    public void setMenus(List<MenuBase> menus) {
        super.setMenus(menus);
    }

    public void createMenuBar() {
        super.createMenuBar();
    }

    @Override
        protected void setMenuBindings(final Menu glassMenu, final MenuBase mb) {
            super.setMenuBindings(glassMenu, mb);
            System.err.println("add menu "+glassMenu+" to base "+mb);
            uncollectedMenus.add(new WeakReference(glassMenu));
            System.err.println("weakitems = "+uncollectedMenus);
        }

        public List<WeakReference<Menu>> getWeakMenuReferences() {
            return uncollectedMenus;
        }
}
