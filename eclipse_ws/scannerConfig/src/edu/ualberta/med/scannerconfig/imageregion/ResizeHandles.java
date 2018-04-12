package edu.ualberta.med.scannerconfig.imageregion;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.ualberta.med.biobank.gui.common.Swt2DUtil;

/*
 * Creates resize handles, implemented as cubes, along the perimeter of the retion.
 */
class ResizeHandles {

    private final double HANDLE_HALF_SIZE = ImageRegion.HANDLE_SIZE_PIXELS / 2;

    final Rectangle2D.Double handleRect;

    final Map<ResizeHandle, AffineTransform> handleTransforms;

    ResizeHandles(Rectangle2D.Double region) {
        handleRect = new Rectangle2D.Double(0, 0, ImageRegion.HANDLE_SIZE_PIXELS, ImageRegion.HANDLE_SIZE_PIXELS);
        handleTransforms = new HashMap<ResizeHandle, AffineTransform>(ResizeHandle.size);
        updateRegion(region);
    }

    void updateRegion(Rectangle2D.Double region) {
        double halfWidth = region.width / 2;
        double halfHeight = region.height / 2;

        AffineTransform northWestTransform =
            AffineTransform.getTranslateInstance(-HANDLE_HALF_SIZE, -HANDLE_HALF_SIZE);
        handleTransforms.put(ResizeHandle.NORTH_WEST, northWestTransform);

        AffineTransform t = new AffineTransform(northWestTransform);
        t.translate(halfWidth, 0);
        handleTransforms.put(ResizeHandle.NORTH, t);

        t = new AffineTransform(northWestTransform);
        t.translate(region.width, 0);
        handleTransforms.put(ResizeHandle.NORTH_EAST, t);

        t = new AffineTransform(northWestTransform);
        t.translate(region.width, halfHeight);
        handleTransforms.put(ResizeHandle.EAST, t);

        t = new AffineTransform(northWestTransform);
        t.translate(region.width, region.height);
        handleTransforms.put(ResizeHandle.SOUTH_EAST, t);

        t = new AffineTransform(northWestTransform);
        t.translate(halfWidth, region.height);
        handleTransforms.put(ResizeHandle.SOUTH, t);

        t = new AffineTransform(northWestTransform);
        t.translate(0, region.height);
        handleTransforms.put(ResizeHandle.SOUTH_WEST, t);

        t = new AffineTransform(northWestTransform);
        t.translate(0, halfHeight);
        handleTransforms.put(ResizeHandle.WEST, t);
    }

    /**
     * Returns the resize handle if the point (x, y) is inside one of them. Point is in coordinates
     * of the rgion (in inches).
     * 
     * @param pt the point to test.
     * @return The handle the point is in. If in none, then PointRegion.OUTSIDE_REGION.
     */
    PointToRegion getHandleFromPoint(Point2D.Double pt) {
        for (Entry<ResizeHandle, AffineTransform> entry : handleTransforms.entrySet()) {
            AffineTransform t = entry.getValue();
            Point2D.Double handlePoint = Swt2DUtil.inverseTransformPoint(t, pt);
            if (handleRect.contains(handlePoint)) {
                return entry.getKey().getPointRegion();
            }
        }
        return PointToRegion.OUTSIDE_REGION;
    }

    @SuppressWarnings("nls")
    Rectangle2D.Double getResizeHandleRect(ResizeHandle handle) {
        AffineTransform transform = handleTransforms.get(handle);
        if (transform == null) {
            throw new IllegalStateException("no transform for handle: " + handle);
        }
        return new Rectangle2D.Double(
            transform.getTranslateX(),
            transform.getTranslateY(),
            ImageRegion.HANDLE_SIZE_PIXELS,
            ImageRegion.HANDLE_SIZE_PIXELS);

    }

    Map<PointToRegion, Rectangle2D.Double> getResizeHandleRects() {
        Map<PointToRegion, Rectangle2D.Double> map =
            new HashMap<PointToRegion, Rectangle2D.Double>(ResizeHandle.size);
        for (ResizeHandle handle : ResizeHandle.values()) {
            map.put(handle.getPointRegion(), getResizeHandleRect(handle));
        }
        return map;
    }
}