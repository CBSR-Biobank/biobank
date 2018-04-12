package edu.ualberta.med.scannerconfig.widgets.imageregion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.Swt2DUtil;
import edu.ualberta.med.scannerconfig.BarcodeImage;
import edu.ualberta.med.scannerconfig.BarcodePosition;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import edu.ualberta.med.scannerconfig.PalletOrientation;
import edu.ualberta.med.scannerconfig.dmscanlib.CellRectangle;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;

/**
 * A canvas where a rectangular region is superimpsed on top of an image. The rectangular region is
 * composed of cells which correspond to regions containing 2D barcodes.
 * 
 * @author nelson
 * 
 */
public class PalletGridCanvas extends ImageWithRegionCanvas {

    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(PalletGridCanvas.class
        .getName());

    private PalletDimensions dimensions;

    private PalletOrientation orientation;

    private BarcodePosition barcodePosition;

    // these rectangles have pixels as units
    private final Map<String, CellRectangle> cellRectangles;

    private final Map<String, DecodedWell> decodedWells;

    private final Image decodedIconImage;

    private final Rectangle decodedIconImageBounds;

    private final Color foregroundColor;

    private final Color a1BackgroundColor;

    private final Color decodedBackgroundColor;

    public PalletGridCanvas(Composite parent) {
        super(parent);
        cellRectangles = new HashMap<String, CellRectangle>();
        decodedWells = new HashMap<String, DecodedWell>();
        decodedIconImage = BgcPlugin.getDefault().getImage(BgcPlugin.Image.ACCEPT);
        decodedIconImageBounds = decodedIconImage.getBounds();

        Display display = getDisplay();
        foregroundColor = new Color(display, 0, 255, 0);
        a1BackgroundColor = new Color(display, 0, 255, 255);
        decodedBackgroundColor = new Color(display, 0, 127, 0);

        addMouseTrackListener(new MouseTrackListener() {

            @Override
            public void mouseHover(MouseEvent e) {
                PalletGridCanvas.this.mouseHover(e);
            }

            @Override
            public void mouseExit(MouseEvent e) {
                // do nothing
            }

            @Override
            public void mouseEnter(MouseEvent e) {
                // do nothing
            }
        });
    }

    @Override
    public void dispose() {
        foregroundColor.dispose();
        a1BackgroundColor.dispose();
    }

    @SuppressWarnings("nls")
    @Override
    protected void paint(GC gc) {
        if (barcodeImage == null) {
            super.paint(gc);
            return;
        }

        Image clippedImage = clippedSourceImage();
        Rectangle clientRect = getClientArea();
        GC newGC = new GC(clippedImage);

        newGC.setClipping(clientRect);
        newGC.setForeground(foregroundColor);

        for (CellRectangle cell : cellRectangles.values()) {
            Rectangle2D.Double rect = cell.getBoundsRectangle();
            Rectangle2D.Double rectOnCanvas = Swt2DUtil.transformRect(
                sourceImageToCanvasTransform, rect);

            newGC.drawRectangle((int) rectOnCanvas.x, (int) rectOnCanvas.y,
                (int) rectOnCanvas.width, (int) rectOnCanvas.height);

            if (cell.getLabel().equals("A1")) {
                newGC.setAlpha(125);
                newGC.setBackground(a1BackgroundColor);
                newGC.fillRectangle((int) rectOnCanvas.x, (int) rectOnCanvas.y,
                    (int) rectOnCanvas.width, (int) rectOnCanvas.height);
                newGC.setAlpha(255);
            }

            DecodedWell decodedWell = decodedWells.get(cell.getLabel());
            if (decodedWell != null) {
                newGC.setAlpha(64);
                newGC.setBackground(decodedBackgroundColor);
                newGC.fillRectangle((int) rectOnCanvas.x, (int) rectOnCanvas.y,
                    (int) rectOnCanvas.width, (int) rectOnCanvas.height);
                newGC.setAlpha(255);

                newGC.drawImage(decodedIconImage, 0, 0,
                    decodedIconImageBounds.width,
                    decodedIconImageBounds.height, (int) rectOnCanvas.x,
                    (int) rectOnCanvas.y, decodedIconImageBounds.width,
                    decodedIconImageBounds.height);
            }
        }

        drawResizeHandles(newGC, a1BackgroundColor);

        newGC.dispose();

        gc.drawImage(clippedImage, 0, 0);
        clippedImage.dispose();
    }

    /**
     * Called by parent widget when a new image is available.
     * 
     * @param image the image to display in the widget
     * @param gridRectangle the starting dimensions for the grid to overlay on top of the image
     * @param orientation the orientation of the grid
     * @param dimensions the dimensions of the grid
     * @param barcodePosition the location of the barcode on a tube
     * @param imageSource
     */
    public void updateImage(
        BarcodeImage image,
        Rectangle2D.Double gridRectangle,
        PalletOrientation orientation,
        PalletDimensions dimensions,
        BarcodePosition barcodePosition) {

        setOrientation(orientation);
        setDimensions(dimensions);
        setBarcodePosition(barcodePosition);
        setUserRegionInPixels(image, gridRectangle);
        updateImage(image);
        udpateCellRectangles();
        redraw();
    }

    private void udpateCellRectangles() {
        if (getSourceImage() == null)
            return;

        cellRectangles.clear();

        Rectangle2D.Double boundingBoxInInches = getUserRegionInPixels();
        Set<CellRectangle> cellsInPixels = CellRectangle.getCellsForBoundingBox(
            boundingBoxInInches,
            orientation,
            dimensions,
            barcodePosition);

        for (CellRectangle cell : cellsInPixels) {
            String label = cell.getLabel();
            cellRectangles.put(label, cell);
        }
    }

    @Override
    public void controlResized() {
        super.controlResized();
    }

    @Override
    protected void mouseDrag(MouseEvent e) {
        super.mouseDrag(e);
        udpateCellRectangles();
    }

    @Override
    protected void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        udpateCellRectangles();
    }

    @SuppressWarnings("nls")
    private void mouseHover(MouseEvent e) {
        Point2D.Double mousePointInInches = canvasPointToRegion(e.x, e.y);
        CellRectangle cell = getObjectAtCoordinates(mousePointInInches.x,
            mousePointInInches.y);
        if (cell != null) {
            StringBuffer buf = new StringBuffer();
            buf.append(cell.getLabel());

            DecodedWell decodedWell = decodedWells.get(cell.getLabel());
            if (decodedWell != null) {
                buf.append(": ").append(decodedWell.getMessage());
            }

            setToolTipText(buf.toString());
        } else {
            setToolTipText(null);
        }
    }

    private CellRectangle getObjectAtCoordinates(double x, double y) {
        for (CellRectangle cell : cellRectangles.values()) {
            if (cell.containsPoint(x, y)) {
                return cell;
            }
        }
        return null;
    }

    public void setDimensions(PalletDimensions dimensions) {
        this.dimensions = dimensions;
        udpateCellRectangles();
    }

    public void setOrientation(PalletOrientation orientation) {
        this.orientation = orientation;
        udpateCellRectangles();
    }

    public void setBarcodePosition(BarcodePosition barcodePosition) {
        this.barcodePosition = barcodePosition;
        udpateCellRectangles();
    }

    public void setDecodeInfo(Map<String, DecodedWell> decodedWells) {
        removeDecodeInfo();
        this.decodedWells.putAll(decodedWells);
    }

    public void removeDecodeInfo() {
        decodedWells.clear();
    }

    /**
     * Returs the grid region in units of inches.
     * 
     * @return
     */
    public Set<CellRectangle> getCellsInPixels() {
        Rectangle2D.Double boundingBox = getUserRegionInPixels();
        Set<CellRectangle> cellsInInches = CellRectangle.getCellsForBoundingBox(
            boundingBox,
            orientation,
            dimensions,
            barcodePosition);
        return cellsInInches;
    }
}
