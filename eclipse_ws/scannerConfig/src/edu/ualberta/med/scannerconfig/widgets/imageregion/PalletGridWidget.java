package edu.ualberta.med.scannerconfig.widgets.imageregion;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.scannerconfig.BarcodeImage;
import edu.ualberta.med.scannerconfig.BarcodePosition;
import edu.ualberta.med.scannerconfig.ImageSource;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import edu.ualberta.med.scannerconfig.PalletOrientation;
import edu.ualberta.med.scannerconfig.dialogs.DecodeImageDialog.ScanMode;
import edu.ualberta.med.scannerconfig.dmscanlib.CellRectangle;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;

/**
 * A widget that allows the user to manipulate a grid that is projected on an image of a scanned
 * pallet. Each cell in the grid represents an area of the image that will later be examined and, if
 * it contains a 2D DataMatrix barcode, the barcode will be decoded.
 * 
 * @author loyola
 */
public class PalletGridWidget extends Composite {

    private static final I18n i18n = I18nFactory.getI18n(PalletGridWidget.class);

    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(PalletGridWidget.class.getName());

    private BarcodeImage barcodeImage;

    // text to display below the image
    private final Label infoTextLabel;

    private final PalletGridCanvas canvas;

    private final Map<String, DecodedWell> decodedWells;

    public PalletGridWidget(Composite parent) {
        super(parent, SWT.NONE);

        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        setLayout(layout);

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        setLayoutData(gd);

        canvas = new PalletGridCanvas(this);
        infoTextLabel = createInfoLabel();

        decodedWells = new HashMap<String, DecodedWell>();
    }

    @Override
    public void dispose() {
        barcodeImage.dispose();
    }

    private Label createInfoLabel() {
        final Composite composite = new Composite(this, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);

        GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        composite.setLayoutData(gd);

        Label infoLabel = new Label(composite, SWT.NONE);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        infoLabel.setLayoutData(gd);
        return infoLabel;
    }

    @SuppressWarnings("nls")
    private String getImageInfoText(BarcodeImage image) {
        StringBuffer buf = new StringBuffer();
        String basename = image.getBasename();
        if (image.getImageSource() == ImageSource.FILE) {
            buf.append(i18n.tr("File: "));
            buf.append(basename);
            buf.append(i18n.tr(", created: "));

            infoTextLabel.setToolTipText(image.getFilename());
        } else {
            buf.append(i18n.tr("Image scan date: "));

            infoTextLabel.setToolTipText(null);
        }
        buf.append(DateFormatter.formatAsDateTime(image.getDateLastModified()));
        int size = decodedWells.size();
        if (size > 0) {
            buf.append(", tubes decoded: ").append(size);
        }

        return buf.toString();
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
        this.barcodeImage = image;
        canvas.updateImage(image, gridRectangle, orientation, dimensions, barcodePosition);
        infoTextLabel.setText(getImageInfoText(image));
        refresh();
    }

    public void refresh() {
        canvas.redraw();
    }

    public void removeImage() {
        canvas.removeImage();
        canvas.removeDecodeInfo();
        refresh();
    }

    public void removeDecodeInfo() {
        decodedWells.clear();
        canvas.removeDecodeInfo();
    }

    /**
     * Called when the user changes the orientation of the grid. The grid is updated to show the new
     * orientation.
     * 
     * @param orientation Either landscape or portrait.
     */
    public void setPlateOrientation(PalletOrientation orientation) {
        canvas.setOrientation(orientation);
        removeDecodeInfo();
        refresh();
    }

    /**
     * Called when the user changes the dimensios of the grid. The dimensions state how many tubes
     * are conained in the image. The grid is updated to show the new dimensions.
     * 
     * @param dimensions The number or rows and columns of tubes.
     */
    public void setPlateDimensions(PalletDimensions dimensions) {
        canvas.setDimensions(dimensions);
        removeDecodeInfo();
        refresh();
    }

    /**
     * Called when the user changes the location of where the barcodes are located on a tube. They
     * may be either on the tops or bottoms of the tubes.
     * 
     * @param barcodePosition if the barcodes are on the tops or bottoms of the tubes.
     */
    public void setBarcodePosition(BarcodePosition barcodePosition) {
        canvas.setBarcodePosition(barcodePosition);
        removeDecodeInfo();
        refresh();
    }

    /**
     * Used to display decoding information with the image.
     * 
     * @param scanMode
     * 
     * @param decodedWells
     */
    @SuppressWarnings("nls")
    public boolean setDecodedWells(Set<DecodedWell> wells, ScanMode scanMode) {
        switch (scanMode) {
        case SCAN:
            for (DecodedWell decodedWell : wells) {
                decodedWells.put(decodedWell.getLabel(), decodedWell);
            }
            break;

        case RESCAN:
            // make sure decoded tubes in the last decode match the ones in the new ones
            Map<String, DecodedWell> newDecodedWells = new HashMap<String, DecodedWell>();

            for (DecodedWell decodedWell : wells) {
                DecodedWell prevDecode = decodedWells.get(decodedWell.getLabel());
                if ((prevDecode != null)
                    && !prevDecode.getMessage().equals(decodedWell.getMessage())) {
                    return false;
                }
                newDecodedWells.put(decodedWell.getLabel(), decodedWell);
            }
            decodedWells.putAll(newDecodedWells);
            break;

        default:
            throw new IllegalArgumentException("invalid value for scanMode " + scanMode);
        }

        canvas.setDecodeInfo(decodedWells);
        infoTextLabel.setText(getImageInfoText(barcodeImage));
        refresh();
        return true;
    }

    public Set<CellRectangle> getCellsInPixels() {
        return canvas.getCellsInPixels();
    }

    public Double getUserRegionInPixels() {
        return canvas.getUserRegionInPixels();
    }

    public void clearInfoText() {
        infoTextLabel.setText(StringUtil.EMPTY_STRING);
    }
}
