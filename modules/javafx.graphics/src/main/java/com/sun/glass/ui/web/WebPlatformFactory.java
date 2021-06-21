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

import com.sun.glass.ui.*;
import com.sun.glass.ui.delegate.*;

public final class WebPlatformFactory extends PlatformFactory {

    @Override
    public Application createApplication(){
        return new WebApplication();
    }

    @Override
    public MenuBarDelegate createMenuBarDelegate(final MenuBar menubar) {
        throw new RuntimeException ("Not supported");
    }

    @Override
    public MenuDelegate createMenuDelegate(final Menu menu) { 
        throw new RuntimeException ("Not supported");
    }

    @Override
    public MenuItemDelegate createMenuItemDelegate(final MenuItem item) { 
        throw new RuntimeException ("Not supported");
    }

    @Override
    public ClipboardDelegate createClipboardDelegate() {
        throw new RuntimeException ("Not supported");
    }

}
