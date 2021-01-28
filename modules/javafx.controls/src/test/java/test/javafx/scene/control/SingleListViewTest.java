/*
 * Copyright (c) 2011, 2020, Oracle and/or its affiliates. All rights reserved.
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

package test.javafx.scene.control;

import com.sun.javafx.scene.control.VirtualScrollBar;
import com.sun.javafx.scene.control.behavior.FocusTraversalInputMap;
import com.sun.javafx.scene.control.behavior.ListCellBehavior;
import com.sun.javafx.scene.control.behavior.ListViewBehavior;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.InputMap.KeyMapping;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import com.sun.javafx.tk.Toolkit;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.FocusModel;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListCellShim;
import javafx.scene.control.ListView;
import javafx.scene.control.ListViewShim;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils.assertStyleClassContains;
import static javafx.collections.FXCollections.*;
import test.com.sun.javafx.scene.control.infrastructure.ControlSkinFactory;
import test.com.sun.javafx.scene.control.infrastructure.KeyEventFirer;
import test.com.sun.javafx.scene.control.infrastructure.KeyModifier;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import test.com.sun.javafx.scene.control.test.Person;
import test.com.sun.javafx.scene.control.test.RT_22463_Person;

public class SingleListViewTest {
    private ListView<String> listView;
    private MultipleSelectionModel<String> sm;
    private FocusModel<String> fm;

    @Before public void setup() {
        listView = new ListView<>();
        sm = listView.getSelectionModel();
        fm = listView.getFocusModel();
    }

@Test
    public void test_JDK8088400() {
        ListView<Node> listView = new ListView<>();
        listView.setPrefWidth(700);
        listView.getItems().addAll(new Circle(10), new Circle(20), new Circle(100),
                new Circle(30), new Circle(50),new Circle(150), new Circle(60));


        StageLoader sl = new StageLoader(listView);

        Platform.runLater(() -> {
            Toolkit.getToolkit().firePulse();
            VirtualFlow<?> vf = VirtualFlowTestUtils.getVirtualFlow(listView);

            double oldPos = 0, firstDif = 0;
            int inc = 0;
            while (vf.getPosition() < 1.0) {
                vf.scrollPixels(1);
                inc += 1;
                double pos = vf.getPosition();
                double dif = pos - oldPos;
                System.err.println("inc = "+inc+", pos = "+pos);
                if ((oldPos > 0) && (pos < 1.)){
                    assertEquals(firstDif, dif, 0.00001);
                } else {
                    firstDif = dif;
                }
                vf.layout();
                oldPos = pos;
            }
            sl.dispose();
        });
    }
    
   // @Test
    public void test_JDK8089589() {
        ListView<Rectangle> listView = new ListView<>();
        for (int i = 0; i < 4; i++) {
            listView.getItems().add(new Rectangle(100, 200, Color.RED));
            listView.getItems().add(new Rectangle(100, 10, Color.BLUE));
        }

        StageLoader sl = new StageLoader(listView);

        Platform.runLater(() -> {
            Toolkit.getToolkit().firePulse();
            VirtualFlow<?> vf = VirtualFlowTestUtils.getVirtualFlow(listView);

            double oldPos = 0, firstDif = 0;
            int inc = 0;
            while (vf.getPosition() < 1.0) {
                vf.scrollPixels(1);
                inc += 1;
                double pos = vf.getPosition();
                double dif = pos - oldPos;
                System.err.println("inc = "+inc+", pos = "+pos);
                if (oldPos > 0) {
                    assertEquals(firstDif, dif, 0.00001);
                } else {
                    firstDif = dif;
                }
                vf.layout();
                oldPos = pos;
            }
            sl.dispose();
        });
    }
}
