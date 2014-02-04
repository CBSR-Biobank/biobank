package edu.ualberta.med.biobank.gui.common;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.graphics.Rectangle;

public class Swt2DUtil {

    public static Rectangle2D.Double rectangleToDouble(Rectangle r) {
        Rectangle2D.Double result = new Rectangle2D.Double(r.x, r.y, r.width, r.height);
        return result;
    }

    /**
     * Given an arbitrary rectangle, get the rectangle with the given transform. The result
     * rectangle is positive width and positive height.
     * 
     * @param af AffineTransform
     * @param src source rectangle
     * @return rectangle after transform with positive width and height
     */
    public static Rectangle2D.Double transformRect(AffineTransform af, Rectangle2D.Double src) {
        Rectangle2D.Double dest = new Rectangle2D.Double(0, 0, 0, 0);
        src = absRect(src);
        Point2D.Double p1 = new Point2D.Double(src.x, src.y);
        p1 = transformPoint(af, p1);
        dest.x = p1.x;
        dest.y = p1.y;
        dest.width = src.width * af.getScaleX();
        dest.height = src.height * af.getScaleY();
        return dest;
    }

    /**
     * Given an arbitrary rectangle, get the rectangle with the inverse given transform. The result
     * rectangle is positive width and positive height.
     * 
     * @param af AffineTransform
     * @param src source rectangle
     * @return rectangle after transform with positive width and height
     */
    public static Rectangle2D.Double inverseTransformRect(AffineTransform af, Rectangle2D.Double src) {
        Rectangle2D.Double dest = new Rectangle2D.Double(0, 0, 0, 0);
        src = absRect(src);
        Point2D.Double p1 = new Point2D.Double(src.x, src.y);
        p1 = inverseTransformPoint(af, p1);
        dest.x = p1.x;
        dest.y = p1.y;
        dest.width = src.width / af.getScaleX();
        dest.height = src.height / af.getScaleY();
        return dest;
    }

    /**
     * Given an arbitrary point, get the point with the given transform.
     * 
     * @param af affine transform
     * @param pt point to be transformed
     * @return point after tranform
     */
    public static Point2D.Double transformPoint(AffineTransform af, Point2D.Double pt) {
        Point2D.Double src = new Point2D.Double(pt.x, pt.y);
        Point2D dest = af.transform(src, null);
        Point2D.Double point = new Point2D.Double(dest.getX(), dest.getY());
        return point;
    }

    /**
     * Given an arbitrary point, get the point with the inverse given transform.
     * 
     * @param af AffineTransform
     * @param pt source point
     * @return point after transform
     */
    public static Point2D.Double inverseTransformPoint(AffineTransform af, Point2D.Double pt) {
        Point2D.Double src = new Point2D.Double(pt.x, pt.y);
        try {
            Point2D dest = af.inverseTransform(src, null);
            return new Point2D.Double(dest.getX(), dest.getY());
        } catch (NoninvertibleTransformException e) {
            return new Point2D.Double(0, 0);
        }
    }

    /**
     * Given arbitrary rectangle, return a rectangle with upper-left start and positive width and
     * height.
     * 
     * @param src source rectangle
     * @return result rectangle with positive width and height
     */
    public static Rectangle2D.Double absRect(Rectangle2D.Double src) {
        Rectangle2D.Double dest = new Rectangle2D.Double(0, 0, 0, 0);
        if (src.width < 0) {
            dest.x = src.x + src.width + 1;
            dest.width = -src.width;
        }
        else {
            dest.x = src.x;
            dest.width = src.width;
        }
        if (src.height < 0) {
            dest.y = src.y + src.height + 1;
            dest.height = -src.height;
        }
        else {
            dest.y = src.y;
            dest.height = src.height;
        }
        return dest;
    }

}
