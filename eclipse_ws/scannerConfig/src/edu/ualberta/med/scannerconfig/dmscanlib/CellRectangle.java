package edu.ualberta.med.scannerconfig.dmscanlib;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.ualberta.med.biobank.gui.common.Swt2DUtil;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.SbsLabeling;
import edu.ualberta.med.scannerconfig.BarcodePosition;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import edu.ualberta.med.scannerconfig.PalletOrientation;

/**
 * Defines rectangular coordinates, in inches, for a region of image that contains a single 2D
 * barcode. The region also contains a label used to refer to it. This region of the image will then
 * be examined and if it contains a valid 2D barcode it will be decoded.
 * 
 * @author Nelson Loyola
 * 
 */
public final class CellRectangle implements Comparable<CellRectangle> {

    // private static Logger log = LoggerFactory.getLogger(WellRectangle.class);

    private final String label;

    private final Path2D.Double polygon;

    private final Map<Integer, Point2D.Double> points;

    public CellRectangle(String label, Rectangle2D.Double rectangle) {
        this.label = label;
        this.polygon = rectToPoly(rectangle);
        this.points = rectToPoints(rectangle);
    }

    /*
     * Corner one is where X and Y are minimum then the following corners go in a counter clockwise
     * direction.
     */
    private Path2D.Double rectToPoly(Rectangle2D.Double rectangle) {
        Double maxX = rectangle.x + rectangle.width;
        Double maxY = rectangle.y + rectangle.height;

        Path2D.Double polygon = new Path2D.Double();
        polygon.moveTo(rectangle.x, rectangle.y);
        polygon.lineTo(rectangle.x, maxY);
        polygon.lineTo(maxX, maxY);
        polygon.lineTo(maxX, rectangle.y);
        return polygon;
    }

    /*
     * Corner one is where X and Y are minimum then the following corners go in a counter clockwise
     * direction.
     */
    private Map<Integer, Point2D.Double> rectToPoints(Rectangle2D.Double rectangle) {
        Map<Integer, Point2D.Double> result = new HashMap<Integer, Point2D.Double>(4);

        Double maxX = rectangle.x + rectangle.width;
        Double maxY = rectangle.y + rectangle.height;

        result.put(0, new Point2D.Double(rectangle.x, rectangle.y));
        result.put(1, new Point2D.Double(rectangle.x, maxY));
        result.put(2, new Point2D.Double(maxX, maxY));
        result.put(3, new Point2D.Double(maxX, rectangle.y));
        return result;
    }

    public String getLabel() {
        return label;
    }

    public Path2D.Double getPolygon() {
        return polygon;
    }

    public boolean containsPoint(double x, double y) {
        return polygon.contains(x, y);
    }

    public Rectangle2D.Double getBoundsRectangle() {
        Rectangle2D r = polygon.getBounds2D();
        return new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    @SuppressWarnings("nls")
    private Point2D.Double getPoint(int pointId) {
        Point2D.Double point = points.get(pointId);
        if (point == null) {
            throw new IllegalArgumentException("invalid value for corner: " + pointId);
        }
        return point;
    }

    /**
     * 
     * @param cornerId Corner one is where X and Y are minimum then the following corners go in a
     *            counter clockwise direction.
     * @return
     */
    public double getCornerX(int cornerId) {
        return getPoint(cornerId).x;
    }

    /**
     * 
     * @param cornerId Corner one is where X and Y are minimum then the following corners go in a
     *            counter clockwise direction.
     * @return
     */
    public double getCornerY(int cornerId) {
        return getPoint(cornerId).y;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(label).append(" ");
        for (Entry<Integer, Point2D.Double> entry : points.entrySet()) {
            sb.append(entry.getKey()).append(": ");
            sb.append("(").append(entry.getValue().x).append(", ");
            sb.append(entry.getValue().y).append("), ");
        }
        return sb.toString();
    }

    /**
     * Generates each cell of the grid based on the parameters passed in.
     * 
     * @param bbox The dimensions of the image the cells overlap onto.
     * @param orientation The orientation of the pallet: either landscape or portrait.
     * @param dimensions The dimensions of the pallet in terms of number of tubes it holds.
     * @param barcodePosition Where the barcodes are placed on the tubes: either the top or bottom.
     * @return The grid cells.
     * @note The units are in inches.
     */
    @SuppressWarnings("nls")
    public static Set<CellRectangle> getCellsForBoundingBox(
        final Rectangle2D.Double bbox,
        final PalletOrientation orientation,
        final PalletDimensions dimensions,
        final BarcodePosition barcodePosition) {

        int rows, cols;

        switch (orientation) {
        case LANDSCAPE:
            rows = dimensions.getRows();
            cols = dimensions.getCols();
            break;
        case PORTRAIT:
            rows = dimensions.getCols();
            cols = dimensions.getRows();
            break;
        default:
            throw new IllegalArgumentException("invalid orientation value: " + orientation);
        }

        // make cells slightly smaller so that they all fit within the image
        double cellWidth = 0.9999 * Math.floor(bbox.getWidth()) / cols;
        double cellHeight = 0.9999 * Math.floor(bbox.getHeight()) / rows;

        Rectangle2D.Double cellRect = new Rectangle2D.Double(
            bbox.x,
            bbox.y,
            cellWidth, cellHeight);

        Set<CellRectangle> cells = new HashSet<CellRectangle>();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                String label = getLabelForPosition(row, col, dimensions, orientation, barcodePosition);
                AffineTransform t = AffineTransform.getTranslateInstance(
                    col * cellWidth, row * cellHeight);
                CellRectangle cell = new CellRectangle(label, Swt2DUtil.transformRect(t, cellRect));
                cells.add(cell);
            }
        }

        return cells;
    }

    @SuppressWarnings("nls")
    private static String getLabelForPosition(
        int row,
        int col,
        PalletDimensions dimensions,
        PalletOrientation orientation,
        BarcodePosition barcodePosition) {
        int maxCols;

        switch (barcodePosition) {
        case TOP:
            switch (orientation) {
            case LANDSCAPE:
                return SbsLabeling.fromRowCol(row, col);
            case PORTRAIT:
                maxCols = dimensions.getRows();
                return SbsLabeling.fromRowCol(maxCols - 1 - col, row);

            default:
                throw new IllegalStateException("invalid value for orientation: " + orientation);
            }

        case BOTTOM:
            switch (orientation) {
            case LANDSCAPE:
                maxCols = dimensions.getCols();
                return SbsLabeling.fromRowCol(row, maxCols - 1 - col);
            case PORTRAIT:
                return SbsLabeling.fromRowCol(col, row);

            default:
                throw new IllegalStateException("invalid value for orientation: " + orientation);
            }

        default:
            throw new IllegalStateException("invalid value for barcode position: "
                + barcodePosition);
        }
    }

    @Override
    public int compareTo(CellRectangle that) {
        RowColPos thisPos = SbsLabeling.toRowCol(this.label);
        RowColPos thatPos = SbsLabeling.toRowCol(that.label);
        return thisPos.compareTo(thatPos);
    }
}
