package edu.ualberta.med.scannerconfig;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.PlatformUI;

/**
 * This class is used for an image that may contain one or more DataMatrix 2D barcodes.
 * 
 * @author nelson
 * 
 */
public class BarcodeImage {

    private final String filename;

    private final String basename;

    private final Rectangle2D.Double rectangle;

    private final Date dateLastModified;

    private final Image image;

    private final ImageSource imageSource;

    /**
     * Creates an object that contains an image that may contain one or more DataMatrix 2D barcodes.
     * 
     * @param filename The file name on the file system of the file that contains the image.
     * @param imageSource Where the image was aquired from. If the image is of the entire flatbed
     *            scanning region then this value can be null.
     * @throws FileNotFoundException
     */
    public BarcodeImage(String filename, ImageSource imageSource) {
        this.filename = filename;
        image = new Image(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay(),
            filename);

        File file = new File(filename);
        this.basename = file.getName();
        this.dateLastModified = new Date(file.lastModified());
        this.imageSource = imageSource;

        Rectangle bounds = image.getBounds();
        rectangle = new Rectangle2D.Double(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public String getFilename() {
        return filename;
    }

    public String getBasename() {
        return basename;
    }

    public Image getImage() {
        return image;
    }

    public Rectangle getBounds() {
        return image.getBounds();
    }

    public void dispose() {
        image.dispose();
    }

    /**
     * The date the file this image comes from was last modified.
     * 
     * @return
     */
    public Date getDateLastModified() {
        return dateLastModified;
    }

    /**
     * Returns true if the rectangle passed as a parameter is contained by the images rectangle. All
     * dimensions are in inches.
     * 
     * @param rectangle rectangle to test.
     * 
     * @return True if contained. False otherwise.
     */
    public boolean contains(Rectangle2D rectangle) {
        return this.getRectangle().contains(rectangle);
    }

    /**
     * Returns the dimensions of the rectangle in inches.
     * 
     * @return the dimensions of the rectangle.
     */
    public Rectangle2D.Double getRectangle() {
        return rectangle;
    }

    public ImageSource getImageSource() {
        return imageSource;
    }
}
