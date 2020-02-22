/*
 * Copyright (c) 2013, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package javafx.scene.control.skin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import javafx.scene.control.skin.TextFieldSkin;

public class TextFieldSkinIos extends TextFieldSkin {

    private final TextField control;

    public TextFieldSkinIos(final TextField textField) {
        super(textField);
        this.control = textField;
System.err.println("TEXTFIELDskinandroid created");

        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable,
                    Boolean wasFocused, Boolean isFocused) {
System.err.println("TEXTFIELDskinandroid changed");
                if (textField.isEditable()) {
                    if (isFocused) {
System.err.println("TEXTFIELDskinandroid focused");
                        showSoftwareKeyboard();
adjustSize(460);
                    } else {
System.err.println("TEXTFIELDskinandroid not focused");
                        hideSoftwareKeyboard();
        if(control.getScene() != null) {
            control.getScene().getRoot().setTranslateY(0);
        }

                    }
                }
            }
        });
    }

    private void adjustSize(double kh) {
        double tTot = control.getScene().getHeight();
        double ty = control.getLocalToSceneTransform().getTy()+ control.getHeight();
        if (ty > (tTot - kh) ) {
            control.getScene().getRoot().setTranslateY(tTot - ty - kh);
        } else if (kh < 1) {
            control.getScene().getRoot().setTranslateY(0);
        }
    }

    native void showSoftwareKeyboard();
    native void hideSoftwareKeyboard();
}
