package edu.ualberta.med.scannerconfig.dmscanlib;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.scannerconfig.BarcodePosition;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import edu.ualberta.med.scannerconfig.PalletOrientation;

@SuppressWarnings("nls")
public class TestWellRectangle {

    private static Logger log = LoggerFactory.getLogger(TestWellRectangle.class);

    private void logWellRectangle(List<CellRectangle> rectsSorted) {
        for (CellRectangle r : rectsSorted) {
            log.debug("{}", r);
        }
    }

    /**
     * If the bounds are offset from the origin, so should the first well
     */
    @Test
    public void boundsOffsetFromOrigin() {
        Rectangle2D.Double bounds = new Rectangle2D.Double(5, 5, 10, 10);

        Set<CellRectangle> rectangleSet = CellRectangle.getCellsForBoundingBox(
            bounds,
            PalletOrientation.LANDSCAPE,
            PalletDimensions.DIM_ROWS_8_COLS_12,
            BarcodePosition.TOP);

        Assert.assertNotNull(rectangleSet);
        Assert.assertFalse(rectangleSet.isEmpty());

        List<CellRectangle> rectsSorted = new ArrayList<CellRectangle>(rectangleSet);
        Collections.sort(rectsSorted);

        // logWellRectangle(rectsSorted);

        Double firstRect = rectsSorted.get(0).getBoundsRectangle();
        Assert.assertEquals(bounds.x, firstRect.x, 0.001);
    }

    @Test
    public void landscape8x12Top() {
        Rectangle2D.Double bounds = new Rectangle2D.Double(0, 0, 10, 10);

        Set<CellRectangle> rectangleSet = CellRectangle.getCellsForBoundingBox(
            bounds,
            PalletOrientation.LANDSCAPE,
            PalletDimensions.DIM_ROWS_8_COLS_12,
            BarcodePosition.TOP);

        Assert.assertNotNull(rectangleSet);
        Assert.assertFalse(rectangleSet.isEmpty());

        List<CellRectangle> rectsSorted = new ArrayList<CellRectangle>(rectangleSet);
        Collections.sort(rectsSorted);

        // logWellRectangle(rectsSorted);

        Assert.assertEquals("A1", rectsSorted.get(0).getLabel());
        Assert.assertEquals("H12", rectsSorted.get(rectsSorted.size() - 1).getLabel());

        double yRectOffset = rectsSorted.get(0).getBoundsRectangle().width;

        // check that rectangles do not not intersect
        for (int i = 0, n = rectsSorted.size(); i < n - 1; ++i) {
            CellRectangle wrect1 = rectsSorted.get(i);
            Rectangle2D.Double rect1 = wrect1.getBoundsRectangle();

            Assert.assertTrue(bounds.contains(rect1));

            for (int j = i + 1; j < n; ++j) {
                CellRectangle wrect2 = rectsSorted.get(j);
                Rectangle2D.Double rect2 = wrect2.getBoundsRectangle();

                // this compare does not work
                //
                // Assert.assertFalse(
                // String.format("r1: %s, r2: %s", wrect1, wrect2),
                // !rect1.intersects(rect2));

                // similar to transposing every rectangle into a single row
                double v1 = bounds.width * (rect1.y / yRectOffset) + rect1.x;
                double v2 = bounds.width * (rect2.y / yRectOffset) + rect2.x;

                Assert.assertTrue(
                    String.format("r1: %s, r2: %s", wrect1, wrect2),
                    v1 <= v2);
            }
        }
    }

    @Test
    public void portrait8x12Top() {
        Rectangle2D.Double bounds = new Rectangle2D.Double(0, 0, 10, 10);

        Set<CellRectangle> rectangleSet = CellRectangle.getCellsForBoundingBox(
            bounds,
            PalletOrientation.PORTRAIT,
            PalletDimensions.DIM_ROWS_8_COLS_12,
            BarcodePosition.TOP);

        Assert.assertNotNull(rectangleSet);
        Assert.assertFalse(rectangleSet.isEmpty());

        List<CellRectangle> rectsSorted = new ArrayList<CellRectangle>(rectangleSet);
        Collections.sort(rectsSorted);

        logWellRectangle(rectsSorted);

        Assert.assertEquals("A1", rectsSorted.get(0).getLabel());
        Assert.assertEquals("H12", rectsSorted.get(rectsSorted.size() - 1).getLabel());

        double xRectOffset = rectsSorted.get(0).getBoundsRectangle().width;

        // check that rectangles do not not intersect
        for (int i = 0, n = rectsSorted.size(); i < n - 1; ++i) {
            CellRectangle wrect1 = rectsSorted.get(i);
            Rectangle2D.Double rect1 = wrect1.getBoundsRectangle();

            Assert.assertTrue(bounds.contains(rect1));

            for (int j = i + 1; j < n; ++j) {
                CellRectangle wrect2 = rectsSorted.get(j);
                Rectangle2D.Double rect2 = wrect2.getBoundsRectangle();

                // this compare does not work
                //
                // Assert.assertFalse(
                // String.format("r1: %s, r2: %s", wrect1, wrect2),
                // !rect1.intersects(rect2));

                // similar to transposing every rectangle into a single column
                double v1 = bounds.height * (bounds.width - rect1.x) / xRectOffset + rect1.y;
                double v2 = bounds.height * (bounds.width - rect2.x) / xRectOffset + rect2.y;

                Assert.assertTrue(
                    String.format("r1: %s, r2: %s", wrect1, wrect2),
                    v1 < v2);
            }
        }
    }

    @Test
    public void landscape8x12Bottom() {
        Rectangle2D.Double bounds = new Rectangle2D.Double(0, 0, 10, 10);

        Set<CellRectangle> rectangleSet = CellRectangle.getCellsForBoundingBox(
            bounds,
            PalletOrientation.LANDSCAPE,
            PalletDimensions.DIM_ROWS_8_COLS_12,
            BarcodePosition.BOTTOM);

        Assert.assertNotNull(rectangleSet);
        Assert.assertFalse(rectangleSet.isEmpty());

        List<CellRectangle> rectsSorted = new ArrayList<CellRectangle>(rectangleSet);
        Collections.sort(rectsSorted);

        // logWellRectangle(rectsSorted);

        Assert.assertEquals("A1", rectsSorted.get(0).getLabel());
        Assert.assertEquals("H12", rectsSorted.get(rectsSorted.size() - 1).getLabel());

        double yRectOffset = rectsSorted.get(0).getBoundsRectangle().height;

        // check that rectangles do not not intersect
        for (int i = 0, n = rectsSorted.size(); i < n - 1; ++i) {
            CellRectangle wrect1 = rectsSorted.get(i);
            Rectangle2D.Double rect1 = wrect1.getBoundsRectangle();

            Assert.assertTrue(bounds.contains(rect1));

            for (int j = i + 1; j < n; ++j) {
                CellRectangle wrect2 = rectsSorted.get(j);
                Rectangle2D.Double rect2 = wrect2.getBoundsRectangle();

                // this compare does not work
                //
                // Assert.assertFalse(
                // String.format("r1: %s, r2: %s", wrect1, wrect2),
                // !rect1.intersects(rect2));

                // similar to transposing every rectangle into a single row
                double v1 = bounds.width * rect1.y / yRectOffset - rect1.x;
                double v2 = bounds.width * rect2.y / yRectOffset - rect2.x;

                Assert.assertTrue(
                    String.format("r1: %s, r2: %s", wrect1, wrect2),
                    v1 <= v2);
            }
        }
    }

    @Test
    public void portrait8x12Bottom() {
        Rectangle2D.Double bounds = new Rectangle2D.Double(0, 0, 10, 10);

        Set<CellRectangle> rectangleSet = CellRectangle.getCellsForBoundingBox(
            bounds,
            PalletOrientation.PORTRAIT,
            PalletDimensions.DIM_ROWS_8_COLS_12,
            BarcodePosition.BOTTOM);

        Assert.assertNotNull(rectangleSet);
        Assert.assertFalse(rectangleSet.isEmpty());

        List<CellRectangle> rectsSorted = new ArrayList<CellRectangle>(rectangleSet);
        Collections.sort(rectsSorted);

        logWellRectangle(rectsSorted);

        Assert.assertEquals("A1", rectsSorted.get(0).getLabel());
        Assert.assertEquals("H12", rectsSorted.get(rectsSorted.size() - 1).getLabel());

        double xRectOffset = rectsSorted.get(0).getBoundsRectangle().height;

        // check that rectangles do not not intersect
        for (int i = 0, n = rectsSorted.size(); i < n - 1; ++i) {
            CellRectangle wrect1 = rectsSorted.get(i);
            Rectangle2D.Double rect1 = wrect1.getBoundsRectangle();

            Assert.assertTrue(bounds.contains(rect1));

            for (int j = i + 1; j < n; ++j) {
                CellRectangle wrect2 = rectsSorted.get(j);
                Rectangle2D.Double rect2 = wrect2.getBoundsRectangle();

                // this compare does not work
                //
                // Assert.assertFalse(
                // String.format("r1: %s, r2: %s", wrect1, wrect2),
                // !rect1.intersects(rect2));

                // similar to transposing every rectangle into a single column
                double v1 = bounds.height * rect1.x / xRectOffset + rect1.y;
                double v2 = bounds.height * rect2.x / xRectOffset + rect2.y;

                Assert.assertTrue(
                    String.format("r1: %s, r2: %s", wrect1, wrect2),
                    v1 < v2);
            }
        }
    }
}
