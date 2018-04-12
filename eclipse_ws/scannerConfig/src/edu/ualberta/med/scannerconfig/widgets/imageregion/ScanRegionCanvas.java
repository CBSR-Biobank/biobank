package edu.ualberta.med.scannerconfig.widgets.imageregion;

import java.awt.geom.Rectangle2D;

import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.scannerconfig.BarcodeImage;
import edu.ualberta.med.scannerconfig.FlatbedImageScan;

/**
 * A widget that allows the user to manipulate a rectangle, representing a scanning region,
 * projected on to of an image of the entire flatbed scanning region.
 * 
 * The image should have a DPI of {@link FlatbedImageScan.PLATE_IMAGE_DPI}
 * 
 * @author loyola
 */
public class ScanRegionCanvas extends ImageWithRegionCanvas {

    private static Logger log = LoggerFactory.getLogger(ScanRegionCanvas.class.getName());

    private final IScanRegionWidget parentWidget;

    protected boolean regionEnabled = false;

    @SuppressWarnings("nls")
    public ScanRegionCanvas(Composite parent, IScanRegionWidget parentWidget) {
        super(parent);
        if (parentWidget == null) {
            throw new NullPointerException("parent widget is null");
        }
        this.parentWidget = parentWidget;
        setEnabled(regionEnabled);
    }

    @Override
    protected void mouseDrag(MouseEvent e) {
        super.mouseDrag(e);
        notifyListener();
    }

    @Override
    protected void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        notifyListener();
    }

    /*
     * Used to inform the parent widget that the user has used the mouse or the keyboard to change
     * the dimensions of the scan region.
     */
    private void notifyListener() {
        final Rectangle2D.Double plateInInches = getUserRegionInPixels();

        SafeRunnable.run(new SafeRunnable() {
            @Override
            public void run() {
                parentWidget.scanRegionChanged(plateInInches);
            }
        });
    }

    /**
     * Called by parent widget when the dimensions of the scan region have been updated by the user.
     * 
     * Converts the scan region from inches to units used by the canvas that displays the flatbed
     * image.
     * 
     * @param scanRegionInInches
     */
    public void scanRegionDimensionsUpdated(Rectangle2D.Double scanRegionInInches) {
        setUserRegionInPixels(scanRegionInInches);
        redraw();
        update();
    }

    /**
     * Called by parent widget when to enable the scan region.
     * 
     * @param setting When {@link true} then the scan region is enabled. It is disabled otherwise.
     */
    @SuppressWarnings("nls")
    public void enableRegion() {
        log.trace("enableRegion");
        regionEnabled = true;
        redraw();
        update();
    }

    /**
     * Called by parent widget when to disable the scan region.
     * 
     */
    @SuppressWarnings("nls")
    public void disableRegion() {
        log.trace("disableRegion");
        regionEnabled = false;
        super.removeImage();
        redraw();
        update();
    }

    /**
     * Called by parent widget when a new flatbed image is available.
     */
    public void updateImage(BarcodeImage image, Rectangle2D.Double scanRegion) {
        regionEnabled = setUserRegionInPixels(image, scanRegion);
        setEnabled(regionEnabled);
        super.updateImage(image);
    }
}
