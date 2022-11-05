/*
 * Copyright (c) 2010, 2022, Oracle and/or its affiliates. All rights reserved.
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

package test.javafx.scene.control.skin;

import java.util.AbstractList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedList;

import javafx.beans.InvalidationListener;
import javafx.event.Event;
import javafx.scene.control.IndexedCell;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;

import test.javafx.scene.control.SkinStub;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.IndexedCellShim;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.VirtualFlowShim;
import javafx.scene.control.skin.VirtualFlowShim.ArrayLinkedListShim;

/**
 * Tests for the VirtualFlow class. VirtualFlow is the guts of the ListView,
 * TreeView, and TableView implementations.
 */
public class SecondVirtualFlowTest {
    // The following 4 vars are used when testing the
    private ArrayLinkedListShim<SecondCellStub> list;
    private SecondCellStub a;
    private SecondCellStub b;
    private SecondCellStub c;

    // The VirtualFlow we are going to test. By default, there are 100 cells
    // and each cell is 100 wide and 25 tall, except for the 30th cell, which
    // is 200 wide and 100 tall.
    private VirtualFlowShim<IndexedCell> flow;


    private int rt36556_instanceCount;
@Ignore
    @Test public void test_rt36556() {
        rt36556_instanceCount = 0;
        flow = new VirtualFlowShim();
        flow.setVertical(true);
        flow.setCellFactory(p -> {
            rt36556_instanceCount++;
Thread.dumpStack();
            return new CellStub(flow);
        });
System.err.println("ready to set coung");
        flow.setCellCount(100);
System.err.println("ready to resize");
        flow.resize(300, 300);
System.err.println("ready to pulse");
        pulse();
        final int cellCountAtStart = rt36556_instanceCount;
System.err.println("ready to scroll, cc = " + cellCountAtStart);
        flow.scrollPixels(10000);
System.err.println("ready to pulse again");
        pulse();
        assertEquals(cellCountAtStart, rt36556_instanceCount);
        assertNull(flow.getVisibleCell(0));
        assertMinimalNumberOfCellsAreUsed(flow);
    }


    // @Before 
public void dontsetUp() {
        list = new ArrayLinkedListShim<SecondCellStub>();
        a = new SecondCellStub(flow, "A");
        b = new SecondCellStub(flow, "B");
        c = new SecondCellStub(flow, "C");

        flow = new VirtualFlowShim();
//        flow.setManaged(false);
        flow.setVertical(true);
        flow.setCellFactory(p -> new SecondCellStub(flow) {
            @Override
            protected double computeMinWidth(double height) {
                return computePrefWidth(height);
            }

            @Override
            protected double computeMaxWidth(double height) {
                return computePrefWidth(height);
            }

            @Override
            protected double computePrefWidth(double height) {
                return flow.isVertical() ? (c.getIndex() == 29 ? 200 : 100) : (c.getIndex() == 29 ? 100 : 25);
            }

            @Override
            protected double computeMinHeight(double width) {
                return computePrefHeight(width);
            }

            @Override
            protected double computeMaxHeight(double width) {
                return computePrefHeight(width);
            }

            @Override
            protected double computePrefHeight(double width) {
                return flow.isVertical() ? (c.getIndex() == 29 ? 100 : 25) : (c.getIndex() == 29 ? 200 : 100);
            }
        });
        flow.setCellCount(100);
        flow.resize(300, 300);
        pulse();
        // Need a second pulse() call is because this parent can be made
        // "layout" dirty again by its children
        pulse();
    }

    private void pulse() {
        flow.layout();
    }

    /**
     * Asserts that the items in the control LinkedList and the ones in the
     * list are exactly the same.
     */
    private void assertMatch(List<IndexedCell> control, AbstractList<IndexedCell> list) {
        assertEquals("The control and list did not have the same sizes. " +
                     "Expected " + control.size() + " but was " + list.size(),
                     control.size(), list.size());
        int index = 0;
        Iterator<IndexedCell> itr = control.iterator();
        while (itr.hasNext()) {
            IndexedCell cell = itr.next();
            IndexedCell cell2 = (IndexedCell)list.get(index);
            assertSame("The control and list did not have the same item at " +
                       "index " + index + ". Expected " + cell + " but was " + cell2,
                       cell, cell2);
            index++;
        }
    }

    /**
     * Asserts that only the minimal number of cells are used.
     */
    public <T extends IndexedCell> void assertMinimalNumberOfCellsAreUsed(VirtualFlowShim<T> flow) {
        pulse();
        IndexedCell firstCell = VirtualFlowShim.<T>cells_getFirst(flow.cells);
        IndexedCell lastCell = VirtualFlowShim.<T>cells_getLast(flow.cells);
        if (flow.isVertical()) {
            // First make sure that enough cells were created
            assertTrue("There is a gap between the top of the viewport and the first cell",
                       firstCell.getLayoutY() <= 0);
            assertTrue("There is a gap between the bottom of the last cell and the bottom of the viewport",
                       lastCell.getLayoutY() + lastCell.getHeight() >= flow.getViewportLength());

            // Now make sure that no extra cells were created.
            if (VirtualFlowShim.cells_size(flow.cells) > 3) {
                IndexedCell secondLastCell = VirtualFlowShim.<T>cells_get(flow.cells, VirtualFlowShim.cells_size(flow.cells) - 2);
                IndexedCell secondCell = VirtualFlowShim.<T>cells_get(flow.cells, 1);
                assertFalse("There are more cells created before the start of " +
                            "the flow than necessary",
                            secondCell.getLayoutY() <= 0);
                assertFalse("There are more cells created after the end of the " +
                            "flow than necessary",
                            secondLastCell.getLayoutY() + secondLastCell.getHeight() >= flow.getViewportLength());
            }
        } else {
            // First make sure that enough cells were created
            assertTrue("There is a gap between the left of the viewport and the first cell",
                       firstCell.getLayoutX() <= 0);
            assertTrue("There is a gap between the right of the last cell and the right of the viewport",
                       lastCell.getLayoutX() + lastCell.getWidth() >= flow.getViewportLength());

            // Now make sure that no extra cells were created.
            if (VirtualFlowShim.cells_size(flow.cells) > 3) {
                IndexedCell secondLastCell = VirtualFlowShim.<T>cells_get(flow.cells, VirtualFlowShim.cells_size(flow.cells) - 2);
                IndexedCell secondCell = VirtualFlowShim.<T>cells_get(flow.cells, 1);
                assertFalse("There are more cells created before the start of " +
                            "the flow than necessary",
                            secondCell.getLayoutX() <= 0);
                assertFalse("There are more cells created after the end of the " +
                            "flow than necessary",
                            secondLastCell.getLayoutX() + secondLastCell.getWidth() >= flow.getViewportLength());
            }
        }
    }

@Ignore
    @Test
    public void test_jv1() {
        int[] heights = {100, 100, 100, 100, 100, 100, 100, 100, 100};
        VirtualFlowShim<IndexedCell> flow = new VirtualFlowShim();
        flow.setVertical(true);
        flow.setCellFactory(p -> new SecondCellStub(flow) {
            @Override public void updateIndex(int i) {
// System.err.println("[VFS] FIRST updateIndex for cell " + this+" to " + i);
                super.updateIndex(i);
// System.err.println("[VFS] updateIndex for cell " + this+" to " + i);
if (i > -1) {
                this.setPrefHeight(heights[i]);
// System.err.println("[VFS] setPrefHeight for cell " + this+" to " + heights[i]);
}
            }
           @Override public void updateItem(Object ic, boolean empty) {
               super.updateItem(ic, empty);
Integer idx = (Integer)ic;
// System.err.println("[VFS] updateItem for a cell to " + ic+", vis = "+empty);
if (idx > -1) {
                 this.setMinHeight(heights[idx]);
                 this.setPrefHeight(heights[idx]);
}
            }
        });

        flow.setCellCount(heights.length);
        flow.setViewportLength(400);
        flow.resize(400, 400);
        flow.layout();
IndexedCell firstCell = VirtualFlowShim.cells_getFirst(flow.cells);
        // Before scrolling, top-cell must have index 0
assertEquals(0, firstCell.getIndex());
System.err.println("[TEST] scroll to 1 now");
        // We now scroll to item with index 3
        flow.scrollToTop(3);
System.err.println("[TEST] And do layout");
        flow.layout();
        firstCell = VirtualFlowShim.cells_getFirst(flow.cells);
        // After scrolling, top-cell must have index 3
        // index(pixel);
        // 3 (0); 4 (100); 5 (200); 6 (300)
        assertEquals(3, firstCell.getIndex());
        IndexedCell thirdCell = VirtualFlowShim.cells_get(flow.cells, 3);
        double l3y = thirdCell.getLayoutY();
        System.err.println("l3y = " + l3y);
        // the third visible cell must be at 3 x 100 = 300
        assertEquals(l3y, 300, 0.1);
        assertEquals(6, thirdCell.getIndex());
        assertEquals(300, thirdCell.getLayoutY(), 1.);

        flow.scrollPixels(10);
        flow.layout();

        firstCell = VirtualFlowShim.cells_getFirst(flow.cells);
        // After scrolling 10 pixels, top-cell must still have index 3
        // index(pixel);
        // 3 (-10); 4 (90); 5 (190); 6 (290)
        assertEquals(3, firstCell.getIndex());
        assertEquals(-10, firstCell.getLayoutY(),.1);

        IndexedCell thirdCellBis = VirtualFlowShim.cells_get(flow.cells, 3);
        double l3yBis = thirdCellBis.getLayoutY();
        System.err.println("l3yBis = " + l3yBis);
        assertEquals(6, thirdCellBis.getIndex());
        assertEquals(l3yBis, l3y-10, 0.1);

        heights[0] = 220;
        heights[1] = 220;
        heights[2] = 220;
        flow.setCellCount(heights.length);
        flow.scrollPixels(10);
        flow.layout();
        firstCell = VirtualFlowShim.cells_get(flow.cells, 0);
        // After resizing, top-cell must still have index 3
        assertEquals(3, firstCell.getIndex());
        assertEquals(0, firstCell.getLayoutY(),1);
// double l3yTris = thirdCellTris.getLayoutY();
// assertEquals(l3yTris, l3yBis-10, 0.1);
    }

@Ignore
    @Test
    public void test_jv2() {
        int[] heights = {100, 100, 100, 100, 100, 100, 100, 100, 100};
        VirtualFlowShim<IndexedCell> flow = new VirtualFlowShim();
        flow.setVertical(true);
        flow.setCellFactory(p -> new SecondCellStub(flow) {
            @Override public void updateIndex(int i) {
// System.err.println("[VFS] FIRST updateIndex for cell " + this+" to " + i);
                super.updateIndex(i);
// System.err.println("[VFS] updateIndex for cell " + this+" to " + i);
if (i > -1) {
                this.setPrefHeight(heights[i]);
 System.err.println("[VFS] setPrefHeight for cell " + this+" to " + heights[i]);
}
            }
           @Override public void updateItem(Object ic, boolean empty) {
               super.updateItem(ic, empty);
Integer idx = (Integer)ic;
if (idx > -1) {
 System.err.println("[VFS] updateItem for a cell to " + ic+", vis = "+empty+", h = " + heights[idx]);
// if (heights[idx] == 220) Thread.dumpStack();
                 this.setMinHeight(heights[idx]);
                 this.setPrefHeight(heights[idx]);
}
            }
        });

        flow.setCellCount(heights.length);
        flow.setViewportLength(400);
        flow.resize(400, 400);
        flow.layout();
IndexedCell firstCell = VirtualFlowShim.cells_getFirst(flow.cells);
        // Before scrolling, top-cell must have index 0
assertEquals(0, firstCell.getIndex());
System.err.println("[TEST] scroll to 1 now");
        // We now scroll to item with index 3
        flow.scrollToTop(3);
System.err.println("[TEST] And do layout");
        flow.layout();
        firstCell = VirtualFlowShim.cells_getFirst(flow.cells);
        // After scrolling, top-cell must have index 3
        // index(pixel);
        // 3 (0); 4 (100); 5 (200); 6 (300)
        assertEquals(3, firstCell.getIndex());
        IndexedCell thirdCell = VirtualFlowShim.cells_get(flow.cells, 3);
        double l3y = thirdCell.getLayoutY();
        System.err.println("l3y = " + l3y);
        // the third visible cell must be at 3 x 100 = 300
        assertEquals(l3y, 300, 0.1);
        assertEquals(6, thirdCell.getIndex());
        assertEquals(300, thirdCell.getLayoutY(), 1.);

        for (int i =0 ; i < heights.length; i++) {
        heights[i] = 220;
            flow.setCellDirty(i);
        }
        heights[0] = 220;
        heights[1] = 220;
        heights[2] = 220;
System.err.println("HEIGHTS CHANGED!");
        flow.setCellCount(heights.length);
        // flow.scrollPixels(10);
        flow.layout();
        firstCell = VirtualFlowShim.cells_get(flow.cells, 0);
        // After resizing, top-cell must still have index 3
        assertEquals(3, firstCell.getIndex());
        assertEquals(0, firstCell.getLayoutY(),1);
        IndexedCell secondCell = VirtualFlowShim.cells_get(flow.cells, 1);
        assertEquals(4, secondCell.getIndex());
        assertEquals(220, secondCell.getLayoutY(),1);
        // And now scroll down 10 pixels
System.err.println("SCROLL10PIX");
        flow.scrollPixels(10);
System.err.println("DOLAYOUT");
        flow.layout();
System.err.println("DOCHECK");
        firstCell = VirtualFlowShim.cells_get(flow.cells, 0);
        // After resizing, top-cell must still have index 3
        assertEquals(3, firstCell.getIndex());
        assertEquals(-10, firstCell.getLayoutY(),1);
// double l3yTris = thirdCellTris.getLayoutY();
// assertEquals(l3yTris, l3yBis-10, 0.1);
    }

    @Test
    public void test_jv3() {
        int[] heights = {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
        VirtualFlowShim<IndexedCell> flow = new VirtualFlowShim();
        flow.setVertical(true);
        flow.setCellFactory(p -> new SecondCellStub(flow) {
            @Override public void updateIndex(int i) {
// System.err.println("[VFS] FIRST updateIndex for cell " + this+" to " + i);
                super.updateIndex(i);
// System.err.println("[VFS] updateIndex for cell " + this+" to " + i);
if (i > -1) {
                this.setPrefHeight(heights[i]);
 System.err.println("[VFS] setPrefHeight for cell " + this+" to " + heights[i]);
}
            }
           @Override public void updateItem(Object ic, boolean empty) {
               super.updateItem(ic, empty);
Integer idx = (Integer)ic;
if (idx > -1) {
 System.err.println("[VFS] updateItem for a cell to " + ic+", vis = "+empty+", h = " + heights[idx]);
// if (heights[idx] == 220) Thread.dumpStack();
                 this.setMinHeight(heights[idx]);
                 this.setPrefHeight(heights[idx]);
}
            }
        });

        flow.setCellCount(heights.length);
        flow.setViewportLength(398);
        flow.resize(400, 398);
        flow.layout();
IndexedCell firstCell = VirtualFlowShim.cells_getFirst(flow.cells);
        // Before scrolling, top-cell must have index 0
assertEquals(0, firstCell.getIndex());
System.err.println("[TEST] scroll to 5 now");
        // We now scroll to item with index 3
        flow.scrollToTop(12);
System.err.println("[TEST] And do layout");
        flow.layout();
        firstCell = VirtualFlowShim.cells_getFirst(flow.cells);
        // After scrolling, top-cell must have index 3
        // index(pixel);
        // 9 (-2); 10 (98); 11 (198); 12 (298)
        assertEquals(9, firstCell.getIndex());
        IndexedCell thirdCell = VirtualFlowShim.cells_get(flow.cells, 3);
        double l3y = thirdCell.getLayoutY();
        System.err.println("l3y = " + l3y);
        // the third visible cell must be at 3 x 100 = 300
        assertEquals(l3y, 298, 0.1);
        assertEquals(12, thirdCell.getIndex());
        assertEquals(298, thirdCell.getLayoutY(), 1.);

        for (int i =0 ; i < heights.length; i++) {
            heights[i] = 220;
            flow.setCellDirty(i);
        }
System.err.println("HEIGHTS CHANGED!");
        flow.setCellCount(heights.length);
        // flow.scrollPixels(10);
        flow.layout();
        firstCell = VirtualFlowShim.cells_get(flow.cells, 0);
        // After resizing, top-cell must still have index 9
        assertEquals(9, firstCell.getIndex());
        assertEquals(-2, firstCell.getLayoutY(),1);
        IndexedCell secondCell = VirtualFlowShim.cells_get(flow.cells, 1);
        assertEquals(10, secondCell.getIndex());
        assertEquals(218, secondCell.getLayoutY(),1);
        // And now scroll down 10 pixels
System.err.println("SCROLL10PIX");
        flow.scrollPixels(10);
System.err.println("DOLAYOUT");
        flow.layout();
System.err.println("DOCHECK");
        firstCell = VirtualFlowShim.cells_get(flow.cells, 0);
        // After resizing, top-cell must still have index 5
        assertEquals(9, firstCell.getIndex());
        assertEquals(-12, firstCell.getLayoutY(),1);
// double l3yTris = thirdCellTris.getLayoutY();
// assertEquals(l3yTris, l3yBis-10, 0.1);
    }
}

class SecondCellStub extends IndexedCellShim {
    String s;
VirtualFlowShim flow;

    public SecondCellStub(VirtualFlowShim flow) { init(flow); }
    public SecondCellStub(VirtualFlowShim flow, String s) { init(flow); this.s = s; }

    private void init(VirtualFlowShim flow) {
        this.flow = flow;
        setSkin(new SkinStub<SecondCellStub>(this));
        // updateItem(this, false);
    }

    @Override
    public void updateIndex(int i) {
        super.updateIndex(i);

        s = "Item " + getIndex();
        updateItem(getIndex(), getIndex() >= flow.getCellCount());
    }

    @Override
    public String toString() {
        return super.toString() + "with idex = " + s;
    }
}
