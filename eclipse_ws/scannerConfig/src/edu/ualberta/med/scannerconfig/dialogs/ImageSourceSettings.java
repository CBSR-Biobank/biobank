package edu.ualberta.med.scannerconfig.dialogs;

import java.awt.geom.Rectangle2D;

import org.eclipse.jface.dialogs.IDialogSettings;

import edu.ualberta.med.scannerconfig.BarcodePosition;
import edu.ualberta.med.scannerconfig.ImageSource;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import edu.ualberta.med.scannerconfig.PalletOrientation;
import edu.ualberta.med.scannerconfig.preferences.scanner.ScannerDpi;

/**
 * Stores the settings associated with an image source. These include:
 * <ul>
 * <li>The image source (see {@link ImageSrouce})</li>
 * <li>The orientation of the pallet.</li>
 * <li>The dimensions of the pallet.</li>
 * <li>Where the 2D barcode is located on each tube in the pallet (see {@link BarcodePosition}).</li>
 * <li>If the image source is a flatbed scanner, then DPI used to scan the image.</li>
 * <li>The rectangle that contains the cells for each tube.</li>
 * </ul>
 * 
 * Image source settings can be persisted using Eclipse's {@link IDialogSettings}.
 * 
 * @author loyola
 * 
 */
public class ImageSourceSettings {

    @SuppressWarnings("nls")
    private static final String PALLET_ORIENTATION_KEY = "palletOrientation";

    @SuppressWarnings("nls")
    private static final String PALLET_DIMENSIONS_KEY = "palletDimensions";

    @SuppressWarnings("nls")
    private static final String BARCODE_POSITION_KEY = "barcodePosition";

    @SuppressWarnings("nls")
    private static final String SCANNER_DPI_KEY = "scannerDpi";

    @SuppressWarnings("nls")
    private static final String GRID_SECTION_KEY = "grid";

    @SuppressWarnings("nls")
    private static final String GRID_X_KEY = "gridX";

    @SuppressWarnings("nls")
    private static final String GRID_Y_KEY = "gridY";

    @SuppressWarnings("nls")
    private static final String GRID_WIDTH_KEY = "gridWidth";

    @SuppressWarnings("nls")
    private static final String GRID_HEIGHT_KEY = "gridHeight";

    private ImageSource imageSource;
    private PalletOrientation orientation;
    private PalletDimensions dimensions;
    private BarcodePosition barcodePosition;
    private ScannerDpi scannerDpi;
    Rectangle2D.Double gridRectangle;

    /**
     * The settings associated with an image source.
     * 
     * @param imageSource Can be one of the flatbed scanning regions or a file.
     * @param orientation The orientation of the pallet.
     * @param dimensions The dimensions of the pallet in terms of number of tubes.
     * @param barcodePosition If the tubes have the 2D barcode on the top or bottom of the tube.
     * @param gridRectangle The rectangle that defines the decode region in an image.
     */
    private ImageSourceSettings(
        ImageSource imageSource,
        PalletOrientation orientation,
        PalletDimensions dimensions,
        BarcodePosition barcodePosition,
        ScannerDpi scannerDpi,
        Rectangle2D.Double gridRectangle) {
        this.setImageSource(imageSource);
        this.setOrientation(orientation);
        this.dimensions = dimensions;
        this.barcodePosition = barcodePosition;
        this.scannerDpi = scannerDpi;
        this.gridRectangle = gridRectangle;
    }

    public ImageSource getImageSource() {
        return imageSource;
    }

    public void setImageSource(ImageSource imageSource) {
        this.imageSource = imageSource;
    }

    public PalletOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(PalletOrientation orientation) {
        this.orientation = orientation;
    }

    public PalletDimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(PalletDimensions dimensions) {
        this.dimensions = dimensions;
    }

    public BarcodePosition getBarcodePosition() {
        return barcodePosition;
    }

    public void setBarcodePosition(BarcodePosition barcodePosition) {
        this.barcodePosition = barcodePosition;
    }

    public ScannerDpi getScannerDpi() {
        return scannerDpi;
    }

    public void setScannerDpi(ScannerDpi scannerDpi) {
        this.scannerDpi = scannerDpi;
    }

    /**
     * For flatbed scanner image sources, the grid rectangle is stored in inches. For other types of
     * image sources, the grid rectangle is stored in pixels.
     * 
     * @return The rectangle that contains the cells for each tube.
     */
    public Rectangle2D.Double getGridRectangle() {
        return gridRectangle;
    }

    public void setGridRectangle(Rectangle2D.Double gridRectangle) {
        this.gridRectangle = gridRectangle;
    }

    /**
     * Saves the settings used for this image source. These settings are stored in dialog settings
     * (see {@link IDialogSettings}).
     * 
     * @param section The section key name in the dialog settings the data is stored in.
     */
    public void putSettingsInSection(IDialogSettings section) {
        section.put(PALLET_ORIENTATION_KEY, orientation.getId());

        section.put(PALLET_DIMENSIONS_KEY, dimensions.getId());
        section.put(BARCODE_POSITION_KEY, barcodePosition.getId());

        if (imageSource != ImageSource.FILE) {
            section.put(SCANNER_DPI_KEY, scannerDpi.getValue());
        }

        IDialogSettings gridSection = section.getSection(GRID_SECTION_KEY);
        if (gridSection == null) {
            gridSection = section.addNewSection(GRID_SECTION_KEY);
        }

        gridSection.put(GRID_X_KEY, gridRectangle.x);
        gridSection.put(GRID_Y_KEY, gridRectangle.y);
        gridSection.put(GRID_WIDTH_KEY, gridRectangle.width);
        gridSection.put(GRID_HEIGHT_KEY, gridRectangle.height);

    }

    /**
     * Returns the last settings used for this image source. These settings are retrieved from the
     * dialog settings (see {@link IDialogSettings}).
     * 
     * @param source the image source to get the settings for (see {@link ImageSource}).
     * @param section The dialog-settings section key name these settings are stored in.
     * @return The settings used by the user the last time (see {@link ImageSourceSettings}).
     */
    public static ImageSourceSettings getSettingsFromSection(
        ImageSource source,
        IDialogSettings section) {

        IDialogSettings imageSourceSection = section.getSection(source.getId());
        if (imageSourceSection == null) {
            return defaultSettings(source);
        }

        PalletOrientation orientation;
        PalletDimensions dimensions;
        BarcodePosition barcodePosition;
        ScannerDpi scannerDpi = ScannerDpi.DPI_300;

        String orientationStr = getSetting(
            imageSourceSection,
            PALLET_ORIENTATION_KEY,
            PalletOrientation.LANDSCAPE.getId());
        orientation = PalletOrientation.getFromIdString(orientationStr);

        String dimensionsStr = getSetting(
            imageSourceSection,
            PALLET_DIMENSIONS_KEY,
            PalletDimensions.DIM_ROWS_8_COLS_12.getId());
        dimensions = PalletDimensions.getFromIdString(dimensionsStr);

        String barcodePositionStr = getSetting(
            imageSourceSection,
            BARCODE_POSITION_KEY,
            BarcodePosition.BOTTOM.getId());
        barcodePosition = BarcodePosition.getFromIdString(barcodePositionStr);

        if (source != ImageSource.FILE) {
            int dpi = getSetting(imageSourceSection, SCANNER_DPI_KEY, ScannerDpi.DPI_300.getValue());
            scannerDpi = ScannerDpi.getFromId(dpi);
        } else {
            scannerDpi = ScannerDpi.DPI_UNKNOWN;
        }

        Rectangle2D.Double gridRectangle = getGridSettingsFromSection(imageSourceSection);

        return new ImageSourceSettings(
            source, orientation, dimensions, barcodePosition, scannerDpi, gridRectangle);
    }

    private static Rectangle2D.Double getGridSettingsFromSection(IDialogSettings section) {
        double x = -1;
        double y = -1;
        double width = -1;
        double height = -1;

        IDialogSettings regionSection = section.getSection(GRID_SECTION_KEY);

        if (regionSection != null) {
            x = getSetting(regionSection, GRID_X_KEY, -1.0);
            y = getSetting(regionSection, GRID_Y_KEY, -1.0);
            width = getSetting(regionSection, GRID_WIDTH_KEY, -1.0);
            height = getSetting(regionSection, GRID_HEIGHT_KEY, -1.0);
        }

        return new Rectangle2D.Double(x, y, width, height);
    }

    private static String getSetting(IDialogSettings section, String key, String defaultValue) {
        String value = section.get(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    private static int getSetting(IDialogSettings section, String key, int defaultValue) {
        int result = defaultValue;
        try {
            result = section.getInt(key);
        } catch (NumberFormatException e) {
            // do nothing
        }
        return result;
    }

    private static double getSetting(IDialogSettings section, String key, double defaultValue) {
        double result = defaultValue;
        try {
            result = section.getDouble(key);
        } catch (NumberFormatException e) {
            // do nothing
        }
        return result;
    }

    /**
     * Returns the default values used for an image source.
     * 
     * @param source The image source the settings are for.
     * 
     * @return the default values used for an image source.
     */
    public static ImageSourceSettings defaultSettings(ImageSource source) {
        return new ImageSourceSettings(
            source,
            PalletOrientation.LANDSCAPE,
            PalletDimensions.DIM_ROWS_8_COLS_12,
            BarcodePosition.BOTTOM,
            (source == ImageSource.FILE) ? ScannerDpi.DPI_UNKNOWN : ScannerDpi.DPI_600,
            new Rectangle2D.Double(-1, -1, -1, -1));
    }
}
