package edu.ualberta.med.scannerconfig.imageregion;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.gui.common.Swt2DUtil;

/**
 * An axis aligned, resizeable, rectangle used to specify an area within an image. The dimensions of
 * the rectangle are in pixels.
 * 
 * @author loyola
 * 
 */
public class ImageRegion {

    private static Logger log = LoggerFactory.getLogger(ImageRegion.class.getName());

    static final double HANDLE_SIZE_PIXELS = 20;

    private static final double RESIZE_MIN_WIDTH_PIXELS = 1;

    private final Rectangle2D.Double imageBounds;

    private final Rectangle2D.Double region;

    private AffineTransform positionTransform;

    private final ResizeHandles resizeHandles;

    /**
     * The flatbed dimensions must be specified.
     * 
     * @param imageBounds a rectangle containing the dimensions of the flatbed in pixels.
     * @param region a rectangle containing the dimensions of the scanning region that is contained
     *            within the flatbed image (in pixels).
     */
    @SuppressWarnings("nls")
    public ImageRegion(Rectangle2D.Double imageBounds, Rectangle2D.Double region) {
        this.imageBounds = imageBounds;
        if (!this.imageBounds.contains(region)) {
            throw new IllegalArgumentException("image region not contained by image dimensions");
        }
        this.region = new Rectangle2D.Double(0, 0, region.width, region.height);
        this.resizeHandles = new ResizeHandles(region);
        positionTransform = AffineTransform.getTranslateInstance(region.x, region.y);

        log.trace("ImageRegion: region: {}", region);
    }

    public Rectangle2D.Double getRectangle() {
        Rectangle2D.Double regionRect = Swt2DUtil.transformRect(positionTransform, region);
        return regionRect;
    }

    public double getPositionX() {
        return positionTransform.getTranslateX();
    }

    public double getPositionY() {
        return positionTransform.getTranslateY();
    }

    public double getWidth() {
        return region.width;
    }

    public double getHeight() {
        return region.height;
    }

    private double adjustTranslation(double delta, double pos, double min, double max) {
        if (delta < 0) {
            double maxDelta = min - pos;
            if (delta < maxDelta) {
                return maxDelta;
            }
        } else if (delta > 0) {
            double maxDelta = max - pos;
            if (delta > maxDelta) {
                return maxDelta;
            }
        }
        return delta;
    }

    /**
     * Applies a translation to the region.
     * 
     * @param dx translation distance in the X direction.
     * @param dy translation distance in the Y direction.
     * 
     * @note The translation is adjusted so that the region is not moved outside the bounds of the
     *       image bounds.
     */
    public void translate(double dx, double dy) {
        double tx = positionTransform.getTranslateX();
        double ty = positionTransform.getTranslateY();

        dx = adjustTranslation(dx, tx, 0, imageBounds.width - region.width);
        dy = adjustTranslation(dy, ty, 0, imageBounds.height - region.getHeight());

        AffineTransform af = positionTransform;
        af.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
        positionTransform = af;
    }

    /**
     * Resizes the region by moving the left edge of the region by a distance of {@link dx}.
     * 
     * @param dx distance in the X direction to resize.
     */
    public void resizeLeftEdge(double dx) {
        double tx = positionTransform.getTranslateX();
        double maxValue = region.width - RESIZE_MIN_WIDTH_PIXELS;
        dx = Math.max(-tx, Math.min(dx, maxValue));
        region.setRect(0, 0, region.width - dx, region.height);
        resizeHandles.updateRegion(region);
        translate(dx, 0);
    }

    /**
     * Resizes the region by moving the top edge of the region by a distance of {@link dx}.
     * 
     * @param dy distance in the Y direction to resize.
     */
    public void resizeTopEdge(double dy) {
        double ty = positionTransform.getTranslateY();
        double maxValue = region.height - RESIZE_MIN_WIDTH_PIXELS;
        dy = Math.max(-ty, Math.min(dy, maxValue));
        region.setRect(0, 0, region.width, region.height - dy);
        resizeHandles.updateRegion(region);
        translate(0, dy);
    }

    /*
     * Used to adjust the right or bottom edges of the user region. Adjusts the distance to change
     * by according to the current translation, size and maximum size of the region.
     */
    private double resizeEdge(
        double distance,
        double translation,
        double currentSize,
        double maxSize) {

        double possibleEdge = translation + currentSize + distance;

        if (possibleEdge > maxSize) {
            distance = maxSize - translation - currentSize;
        }

        if (distance < -currentSize + RESIZE_MIN_WIDTH_PIXELS) {
            distance = -currentSize + RESIZE_MIN_WIDTH_PIXELS;
        }

        return distance;
    }

    /**
     * Resizes the region by moving the right edge of the region by a distance of {@link dx}.
     * 
     * @param dx distance in the X direction to resize.
     */
    public void resizeRightEdge(double dx) {
        dx = resizeEdge(dx, positionTransform.getTranslateX(), region.width, imageBounds.width);
        region.setRect(0, 0, region.width + dx, region.height);
        resizeHandles.updateRegion(region);
    }

    /**
     * Resizes the region by moving the bottom edge of the region by a distance of {@link dx}.
     * 
     * @param dy distance in the Y direction to resize.
     */
    public void resizeBottomEdge(double dy) {
        dy = resizeEdge(dy, positionTransform.getTranslateY(), region.height, imageBounds.height);
        region.setRect(0, 0, region.width, region.height + dy);
        resizeHandles.updateRegion(region);
    }

    public PointToRegion pointToRegion(Point2D.Double pt) {
        Point2D.Double regionPoint = Swt2DUtil.inverseTransformPoint(positionTransform, pt);

        PointToRegion pointRegion = resizeHandles.getHandleFromPoint(regionPoint);
        if (pointRegion != PointToRegion.OUTSIDE_REGION) {
            return pointRegion;
        }

        if (region.contains(regionPoint)) {
            return PointToRegion.IN_REGION;
        }

        return PointToRegion.OUTSIDE_REGION;
    }

    public PointToRegion pointToRegion(double x, double y) {
        Point2D.Double pt = new Point2D.Double(x, y);
        return pointToRegion(pt);
    }

    public Map<PointToRegion, Rectangle2D.Double> getResizeHandleRects() {
        Map<PointToRegion, Rectangle2D.Double> map = resizeHandles.getResizeHandleRects();
        for (Entry<PointToRegion, Rectangle2D.Double> entry : map.entrySet()) {
            map.put(entry.getKey(), Swt2DUtil.transformRect(positionTransform, entry.getValue()));
        }
        return map;
    }

    @Override
    public String toString() {
        return region.toString();
    }
}
