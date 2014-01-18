package edu.ualberta.med.biobank.gui.common.widgets;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.gui.common.Swt2DUtil;

/**
 * A scrollable image canvas that extends org.eclipse.swt.graphics.Canvas.
 * <p/>
 * This implementation using the pure SWT, no UI AWT package is used. For convenience, I put
 * everything into one class. However, the best way to implement this is to use inheritance to
 * create multiple hierarchies.
 * 
 * This source code originally taken from
 * 
 * http://www.eclipse.org/articles/Article-Image-Viewer/Image_viewer.html
 */
public class ImageCanvas extends Canvas {

    public static class MouseDragInfo {

        public final boolean mouseDrag;

        public final Point2D.Double startPoint;

        public final Point2D.Double startTranslation;

        public MouseDragInfo(Point2D.Double startPoint, Point2D.Double startTranslation) {
            this.mouseDrag = true;
            this.startPoint = startPoint;
            this.startTranslation = startTranslation;
        }

        public MouseDragInfo() {
            this.mouseDrag = false;
            this.startPoint = new Point2D.Double();
            this.startTranslation = new Point2D.Double();
        }
    }

    private static Logger log = LoggerFactory.getLogger(ImageCanvas.class.getName());

    // margin between canvas and screen image
    private static final int CANVAS_MARGIN = 2;

    // zooming rates in x and y direction are equal.
    private final float ZOOM_IN_RATE = 1.1f;

    private final float ZOOM_OUT_RATE = 0.9f;

    private Image sourceImage;

    private Image screenImage;

    protected MouseDragInfo mouseDragInfo = new MouseDragInfo();

    protected AffineTransform sourceImageToCanvasTransform = new AffineTransform();

    public ImageCanvas(final Composite parent) {
        this(parent, SWT.NONE);
    }

    /**
     * Constructor for ScrollableCanvas.
     * 
     * @param parent the parent of this control.
     * @param style the style of this control.
     */
    public ImageCanvas(final Composite parent, int style) {
        super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
        // super(parent, style | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_BACKGROUND);
        addListeners();
        initScrollBars();
        autoHideScrollBars();
    }

    protected void addListeners() {
        // resize listener
        addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent event) {
                ImageCanvas.this.controlResized();
            }
        });

        addPaintListener(new PaintListener() {
            @Override
            public void paintControl(final PaintEvent event) {
                paint(event.gc);
            }
        });

        addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
                ImageCanvas.this.mouseUp(e);
            }

            @Override
            public void mouseDown(MouseEvent e) {
                ImageCanvas.this.mouseDown(e);
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // do nothing
            }
        });
        setFocus();
    }

    @SuppressWarnings("nls")
    @Override
    public void dispose() {
        log.debug("dispose: ");
        if ((sourceImage != null) && !sourceImage.isDisposed()) {
            sourceImage.dispose();
        }
        if ((screenImage != null) && !screenImage.isDisposed()) {
            screenImage.dispose();
        }
        super.dispose();
    }

    public void controlResized() {
        syncScrollBars();
    }

    /*
     * Draws the source image on the canvas.
     */
    @SuppressWarnings("nls")
    protected Image clippedSourceImage() {
        if (sourceImage == null) {
            throw new IllegalStateException("source image is null");
        }

        Rectangle clientRect = super.getClientArea();
        // log.debug("clippedSourceImage: clientRect: {}", clientRect);
        Rectangle2D.Double clientRectDouble = Swt2DUtil.rectangleToDouble(clientRect);

        Rectangle2D.Double canvasRectTf = Swt2DUtil.inverseTransformRect(sourceImageToCanvasTransform, clientRectDouble);
        canvasRectTf.x -= CANVAS_MARGIN;
        canvasRectTf.y -= CANVAS_MARGIN;
        canvasRectTf.width += 2 * CANVAS_MARGIN;
        canvasRectTf.height += 2 * CANVAS_MARGIN;

        Rectangle2D.Double sourceImageBounds = Swt2DUtil.rectangleToDouble(sourceImage.getBounds());
        Rectangle2D intersection = canvasRectTf.createIntersection(sourceImageBounds);
        Rectangle2D.Double imageIntersection = new Rectangle2D.Double(
            intersection.getX(),
            intersection.getY(),
            intersection.getWidth(),
            intersection.getHeight());
        Rectangle2D.Double destRect = Swt2DUtil.transformRect(sourceImageToCanvasTransform, imageIntersection);

        Image resultImage = new Image(getDisplay(), clientRect.width, clientRect.height);
        GC newGC = new GC(resultImage);
        newGC.setClipping(clientRect);
        newGC.drawImage(
            sourceImage,
            (int) imageIntersection.x,
            (int) imageIntersection.y,
            (int) imageIntersection.width,
            (int) imageIntersection.height,
            (int) destRect.x,
            (int) destRect.y,
            (int) destRect.width,
            (int) destRect.height);
        newGC.dispose();

        return resultImage;
    }

    /*
     * Paints the image on the canvas
     */
    protected void paint(GC gc) {
        if (sourceImage != null) {
            if (screenImage != null) {
                screenImage.dispose();
            }
            screenImage = clippedSourceImage();
            gc.drawImage(screenImage, 0, 0);
        } else {
            Rectangle clientRect = getClientArea();
            gc.setClipping(clientRect);
            gc.fillRectangle(clientRect);
        }
    }

    protected void mouseDrag(MouseEvent e) {
        if (sourceImage == null) return;

        if (mouseDragInfo.mouseDrag) {
            AffineTransform af = sourceImageToCanvasTransform;
            double dx = e.x - mouseDragInfo.startPoint.x;
            double dy = e.y - mouseDragInfo.startPoint.y;
            double tx = dx + mouseDragInfo.startTranslation.x - af.getTranslateX();
            double ty = dy + mouseDragInfo.startTranslation.y - af.getTranslateY();
            af.preConcatenate(AffineTransform.getTranslateInstance(tx, ty));
            sourceImageToCanvasTransform = af;
            syncScrollBars();
        }
    }

    protected void mouseDown(MouseEvent e) {
        if (getSourceImage() == null) return;

        mouseDragInfo = new MouseDragInfo(
            new Point2D.Double(e.x, e.y),
            new Point2D.Double(sourceImageToCanvasTransform.getTranslateX(), sourceImageToCanvasTransform.getTranslateY()));
    }

    @SuppressWarnings("unused")
    protected void mouseUp(MouseEvent e) {
        if (getSourceImage() == null) return;
        mouseDragInfo = new MouseDragInfo();
    }

    /* Initalize the scrollbar and register listeners. */
    private void initScrollBars() {
        ScrollBar horizontal = getHorizontalBar();
        horizontal.setEnabled(false);
        horizontal.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                scrollHorizontally((ScrollBar) event.widget);
            }
        });
        ScrollBar vertical = getVerticalBar();
        vertical.setEnabled(false);
        vertical.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                scrollVertically((ScrollBar) event.widget);
            }
        });
    }

    /* Scroll horizontally */
    private void scrollHorizontally(ScrollBar scrollBar) {
        if (sourceImage == null) return;

        AffineTransform af = sourceImageToCanvasTransform;
        double tx = af.getTranslateX();
        double select = -scrollBar.getSelection();
        af.preConcatenate(AffineTransform.getTranslateInstance(select - tx, 0));
        sourceImageToCanvasTransform = af;
        syncScrollBars();
    }

    /* Scroll vertically */
    private void scrollVertically(ScrollBar scrollBar) {
        if (sourceImage == null) return;

        AffineTransform af = sourceImageToCanvasTransform;
        double ty = af.getTranslateY();
        double select = -scrollBar.getSelection();
        af.preConcatenate(AffineTransform.getTranslateInstance(0, select - ty));
        sourceImageToCanvasTransform = af;
        syncScrollBars();
    }

    /**
     * Source image getter.
     * 
     * @return sourceImage.
     */
    public Image getSourceImage() {
        return sourceImage;
    }

    /**
     * Reset the image data and update the image
     * 
     * @param data image data to be set
     */
    public void setSourceImage(Image image) {
        if (sourceImage != null) {
            sourceImage.dispose();
        }

        sourceImage = image;
        syncScrollBars();
        autoHideScrollBars();
    }

    /**
     * Synchronize the scrollbar with the image. If the transform is out of range, it will correct
     * it. This function considers only following factors :<b> transform, image size, client
     * area</b>.
     */
    public void syncScrollBars() {
        if (sourceImage == null) {
            redraw();
            return;
        }

        AffineTransform af = sourceImageToCanvasTransform;
        double sx = af.getScaleX(), sy = af.getScaleY();
        double tx = af.getTranslateX(), ty = af.getTranslateY();
        Rectangle clientArea = getClientArea();
        int cw = clientArea.width;
        int ch = clientArea.height;

        if (tx > 0) tx = 0;
        if (ty > 0) ty = 0;

        ScrollBar horizontal = getHorizontalBar();
        horizontal.setIncrement(cw / 100);
        horizontal.setPageIncrement(cw);
        Rectangle imageBounds = sourceImage.getBounds();
        if (imageBounds.width * sx > cw) {
            /* image is wider than client area */
            horizontal.setMaximum((int) (imageBounds.width * sx));
            horizontal.setEnabled(true);
            if (((int) -tx) > horizontal.getMaximum() - cw)
                tx = -horizontal.getMaximum() + cw;
        } else {
            /* image is narrower than client area */
            horizontal.setEnabled(false);
            tx = (cw - imageBounds.width * sx) / 2; // center if too small.
        }
        horizontal.setSelection((int) (-tx));
        horizontal.setThumb(cw);

        ScrollBar vertical = getVerticalBar();
        vertical.setIncrement(ch / 100);
        vertical.setPageIncrement(ch);
        if (imageBounds.height * sy > ch) {
            /* image is higher than client area */
            vertical.setMaximum((int) (imageBounds.height * sy));
            vertical.setEnabled(true);
            if (((int) -ty) > vertical.getMaximum() - ch)
                ty = -vertical.getMaximum() + ch;
        } else {
            /* image is less higher than client area */
            vertical.setEnabled(false);
            ty = (ch - imageBounds.height * sy) / 2; // center if too small.
        }
        vertical.setSelection((int) (-ty));
        vertical.setThumb(ch);

        /* update transform. */
        af = AffineTransform.getScaleInstance(sx, sy);
        af.preConcatenate(AffineTransform.getTranslateInstance(tx, ty));
        sourceImageToCanvasTransform = af;

        redraw();
    }

    /**
     * Displays or hides the scroll bars if they are needed.
     */
    @SuppressWarnings("nls")
    public void autoHideScrollBars() {
        ScrollBar horizontal = getHorizontalBar();
        ScrollBar vertical = getVerticalBar();

        Rectangle clientArea = getClientArea();
        int cw = clientArea.width;
        int ch = clientArea.height;

        if ((sourceImage == null) || ((cw == 0) && (ch == 0))) {
            horizontal.setVisible(false);
            vertical.setVisible(false);
            return;
        }

        Rectangle imageBounds = sourceImage.getBounds();
        AffineTransform af = sourceImageToCanvasTransform;
        double sx = af.getScaleX(), sy = af.getScaleY();

        log.debug("autoHideScrollBars: imageBounds: {}", imageBounds);
        log.debug("autoHideScrollBars: clientArea: {}", clientArea);

        horizontal.setVisible(imageBounds.width * sx > cw);
        vertical.setVisible(imageBounds.height * sy > ch);
    }

    /**
     * Get the image data. (for future use only)
     * 
     * @return image data of canvas
     */
    public ImageData getImageData() {
        return sourceImage.getImageData();
    }

    /**
     * Perform a zooming operation centered on the given point (dx, dy) and using the given scale
     * factor. The given AffineTransform instance is preconcatenated.
     * 
     * @param dx center x
     * @param dy center y
     * @param scale zoom rate
     * @param af original affinetransform
     */
    protected void centerZoom(
        double dx,
        double dy,
        double scale,
        AffineTransform af) {

        double zoomFactor = sourceImageToCanvasTransform.getScaleX();
        if (((scale < 1) && (zoomFactor < 0.11))
            || ((scale > 1) && (zoomFactor > 1.1))) {
            return;
        }

        af.preConcatenate(AffineTransform.getTranslateInstance(-dx, -dy));
        af.preConcatenate(AffineTransform.getScaleInstance(scale, scale));
        af.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
        sourceImageToCanvasTransform = af;
        syncScrollBars();
        autoHideScrollBars();
    }

    /**
     * Fit the image onto the canvas
     */
    public void fitCanvas() {
        if (sourceImage == null) return;

        Rectangle imageBound = sourceImage.getBounds();
        Rectangle destRect = getClientArea();
        double sx = (double) destRect.width / (double) imageBound.width;
        double sy = (double) destRect.height / (double) imageBound.height;
        double s = Math.min(sx, sy);
        double dx = 0.5 * destRect.width;
        double dy = 0.5 * destRect.height;
        centerZoom(dx, dy, s, new AffineTransform());
    }

    /**
     * Show the image with the original size
     */
    public void showOriginal() {
        if (sourceImage == null) return;

        sourceImageToCanvasTransform = new AffineTransform();
        syncScrollBars();
        autoHideScrollBars();
    }

    protected void centerZoomIn(double dx, double dy) {
        centerZoom(dx, dy, ZOOM_IN_RATE, sourceImageToCanvasTransform);
    }

    protected void centerZoomOut(double dx, double dy) {
        centerZoom(dx, dy, ZOOM_OUT_RATE, sourceImageToCanvasTransform);
    }

    /*
     * Zoom in around the center of client Area.
     */
    protected void zoomIn() {
        if (sourceImage == null) return;
        Rectangle rect = getClientArea();
        int w = rect.width, h = rect.height;
        double dx = ((double) w) / 2;
        double dy = ((double) h) / 2;
        centerZoom(dx, dy, ZOOM_IN_RATE, sourceImageToCanvasTransform);
    }

    /*
     * Zoom out around the center of client Area.
     */
    protected void zoomOut() {
        if (sourceImage == null) return;
        Rectangle rect = getClientArea();
        int w = rect.width, h = rect.height;
        double dx = ((double) w) / 2;
        double dy = ((double) h) / 2;
        centerZoom(dx, dy, ZOOM_OUT_RATE, sourceImageToCanvasTransform);
    }

}
