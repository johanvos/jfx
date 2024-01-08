
package com.sun.javafx.tk.quantum;

import com.sun.javafx.menu.MenuBase;
import java.util.List;

/**
 *
 */
public class GlassSystemMenuShim {

    private GlassSystemMenu gsm;
    public GlassSystemMenuShim() {
        gsm = new GlassSystemMenu();
    }
    
    public void setMenus(List<MenuBase> menus) {
        gsm.setMenus(menus);
    }
}
