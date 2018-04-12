package edu.ualberta.med.scannerconfig.dialogs;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.Swt2DUtil;
import edu.ualberta.med.biobank.gui.common.dialogs.PersistedDialog;
import edu.ualberta.med.biobank.gui.common.events.SelectionListener;
import edu.ualberta.med.biobank.gui.common.widgets.Event;
import edu.ualberta.med.scannerconfig.BarcodeImage;
import edu.ualberta.med.scannerconfig.BarcodePosition;
import edu.ualberta.med.scannerconfig.ImageSource;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import edu.ualberta.med.scannerconfig.PalletOrientation;
import edu.ualberta.med.scannerconfig.ScanPlate;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.CellRectangle;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodeOptions;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodeResult;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLib;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLibResult;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLibResult.Result;
import edu.ualberta.med.scannerconfig.preferences.PreferenceConstants;
import edu.ualberta.med.scannerconfig.preferences.scanner.ScannerDpi;
import edu.ualberta.med.scannerconfig.widgets.ImageSourceAction;
import edu.ualberta.med.scannerconfig.widgets.ImageSourceWidget;
import edu.ualberta.med.scannerconfig.widgets.imageregion.PalletGridWidget;

/**
 * A dialog box that is used to project a grid on top of a scanned plate image. The grid is then
 * used to provide regions where 2D barcodes will be searched for and if found decoded.
 * 
 * Allows the user to aquire an image from a flatbed scanner or to import an image from a file.
 * 
 * @author nelson
 * 
 */
public class DecodeImageDialog extends PersistedDialog implements SelectionListener {

    private static final I18n i18n = I18nFactory.getI18n(DecodeImageDialog.class);

    private static Logger log = LoggerFactory.getLogger(DecodeImageDialog.class);

    public enum ScanMode {
        SCAN,
        RESCAN;
    }

    @SuppressWarnings("nls")
    private static final String SCANNING_DIALOG_SETTINGS =
        DecodeImageDialog.class.getSimpleName() + "_SETTINGS";

    private static final int CONTROLS_MIN_WIDTH = 160;

    @SuppressWarnings("nls")
    private static final String TITLE = i18n.tr("Decode image");

    @SuppressWarnings("nls")
    private static final String TITLE_AREA_MESSAGE_SELECT_PLATE =
        i18n.tr("Select the options to match the image you are decoding");

    @SuppressWarnings("nls")
    private static final String TITLE_AREA_MESSAGE_ADJUST_GRID =
        i18n.tr("Adjust the grid to the barcodes contained in the image.");

    @SuppressWarnings("nls")
    private static final String TITLE_AREA_MESSAGE_DECODING_COMPLETED =
        i18n.tr("Decoding completed.");

    @SuppressWarnings("nls")
    private static final String PROGRESS_MESSAGE_SCANNING =
        i18n.tr("Retrieving image from the flatbed scanner...");

    @SuppressWarnings("nls")
    private static final String PROGRESS_MESSAGE_DECODING =
        i18n.tr("Decoding barcodes in image...");

    @SuppressWarnings("nls")
    private static final String SCAN_PLATE_ERROR =
        i18n.tr("Could not scan the plate region");
    @SuppressWarnings("nls")
    private static final String SCAN_PLATE_INVALID_DPI =
        i18n.tr("Invalid DPI for this scanner. Please select another.");

    private ImageSourceWidget imageSourceWidget;

    private PalletGridWidget plateGridWidget;

    private BarcodeImage imageToDecode;

    private final Set<PalletDimensions> validPlateDimensions;

    private ScanMode scanMode;

    private Button decodeButton;

    private DecodeResult decodeResult;

    private ImageSource selectedImageSource;

    private ScannerDpi selectedDpi;

    /**
     * Use this constructor to limit the valid plate dimensions the user can choose from.
     * 
     * @param parentShell
     * @param validPlateDimensions
     */
    public DecodeImageDialog(Shell parentShell, Set<PalletDimensions> validPlateDimensions) {
        super(parentShell);
        this.validPlateDimensions = validPlateDimensions;
    }

    /**
     * Use this constructor to allow any plate dimension defined in {@link PalletDimensions}.
     * 
     * @param parentShell
     */
    public DecodeImageDialog(Shell parentShell) {
        this(parentShell, new HashSet<PalletDimensions>(Arrays.asList(PalletDimensions.values())));

    }

    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = super.getDialogSettings();
        IDialogSettings section = settings.getSection(SCANNING_DIALOG_SETTINGS);
        if (section == null) {
            section = settings.addNewSection(SCANNING_DIALOG_SETTINGS);
        }
        return section;
    }

    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return getDialogSettings();
    }

    @Override
    protected String getTitleAreaMessage() {
        return TITLE_AREA_MESSAGE_SELECT_PLATE;
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        final Composite contents = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        contents.setLayout(layout);

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        contents.setLayoutData(gd);
        createControls(contents);
        createImageControl(contents);

        parent.pack(true);
        Point size = getShell().computeSize(SWT.DEFAULT, 400);
        getShell().setMinimumSize(size);
    }

    private void createControls(Composite parent) {
        final Composite contents = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        contents.setLayout(layout);

        GridData gd = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
        contents.setLayoutData(gd);

        imageSourceWidget = new ImageSourceWidget(contents, CONTROLS_MIN_WIDTH,
            widgetCreator, getDialogSettings(), validPlateDimensions);
        imageSourceWidget.addSelectionListener(this);

        selectedImageSource = imageSourceWidget.getImageSource();
        selectedDpi = imageSourceWidget.getDpi();

        decodeButton = createDecodeButton(contents);
        decodeButton.setEnabled(false);
    }

    @SuppressWarnings("nls")
    private Button createDecodeButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(i18n.tr("Decode"));
        button.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                decode();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        return button;
    }

    private void createImageControl(Composite parent) {
        plateGridWidget = new PalletGridWidget(parent);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        createButton(parent, IDialogConstants.OK_ID, i18n.tr("Done"), false);
    }

    /*
     * Called when something is selected on one of the sub widgets.
     * 
     * (non-Javadoc)
     * 
     * @see
     * edu.ualberta.med.biobank.gui.common.events.SelectionListener#widgetSelected(edu.ualberta.
     * med.biobank.gui.common.widgets.Event)
     */
    @SuppressWarnings("nls")
    @Override
    public void widgetSelected(Event e) {
        ImageSourceAction imageSourceAction = ImageSourceAction.getFromId(e.detail);

        switch (imageSourceAction) {
        case IMAGE_SOURCE_CHANGED:
            setMessage(TITLE_AREA_MESSAGE_SELECT_PLATE);
            removeImage();
            selectedImageSource = imageSourceWidget.getImageSource();
            selectedDpi = imageSourceWidget.getDpi();
            plateGridWidget.clearInfoText();
            break;

        case PLATE_ORIENTATION:
            PalletOrientation orientation = (PalletOrientation) e.data;
            plateGridWidget.setPlateOrientation(orientation);
            break;

        case PLATE_DIMENSIONS:
            PalletDimensions dimensions = (PalletDimensions) e.data;
            plateGridWidget.setPlateDimensions(dimensions);
            break;

        case BARCODE_POSITION:
            BarcodePosition barcodePosition = (BarcodePosition) e.data;
            plateGridWidget.setBarcodePosition(barcodePosition);
            break;

        case SCAN:
            saveGridRectangle();
            removeDecodeInfo();
            scanMode = ScanMode.SCAN;
            plateGridWidget.clearInfoText();
            scanPlate(e);
            break;

        case SCAN_AND_DECODE:
            saveGridRectangle();
            removeDecodeInfo();
            scanMode = ScanMode.SCAN;
            plateGridWidget.clearInfoText();
            scanPlate(e);
            decode();
            break;

        case RESCAN:
            saveGridRectangle();
            scanMode = ScanMode.RESCAN;
            plateGridWidget.clearInfoText();
            scanPlate(e);
            break;

        case FILENAME:
            removeImage();
            String filename = (String) e.data;
            scanMode = ScanMode.SCAN;
            plateGridWidget.clearInfoText();
            loadFile(filename, ImageSource.FILE);
            break;

        case DPI_CHANGED:
            saveGridRectangle();
            selectedDpi = (ScannerDpi) e.data;
            plateGridWidget.clearInfoText();
            removeImage();
            break;

        default:
            throw new IllegalArgumentException("invalid image source action: " + imageSourceAction);
        }
    }

    private void scanPlate(Event e) {
        final ImageSource imageSource = (ImageSource) e.data;
        final ScanPlate plateToScan = imageSource.getScanPlate();
        final String filename = imageSourceWidget.getFileName();
        final ScannerDpi dpi = imageSourceWidget.getDpi();
        final Display display = Display.getDefault();

        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(PROGRESS_MESSAGE_SCANNING, IProgressMonitor.UNKNOWN);
                ScanLibResult.Result result = ScannerConfigPlugin.scanPlate(
                    plateToScan, filename, dpi);

                if ((result == ScanLibResult.Result.SUCCESS)
                    || ((result == ScanLibResult.Result.FAIL)
                        && ScannerConfigPlugin.getDefault().isDebugging()
                        && imageSourceWidget.haveFakeScannedImage())) {
                    display.asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            loadFile(filename, imageSource);
                        }
                    });
                } else {
                    final String message;
                    if (result == ScanLibResult.Result.INVALID_DPI) {
                        message = SCAN_PLATE_INVALID_DPI;
                    } else {
                        message = SCAN_PLATE_ERROR;
                    }
                    display.asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            setMessage(
                                message,
                                IMessageProvider.ERROR);
                        }
                    });
                }
                monitor.done();
            }
        };

        try {
            new ProgressMonitorDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell()).run(true, false, op);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("nls")
    private void loadFile(String filename, ImageSource imageSource) {
        setMessage(TITLE_AREA_MESSAGE_ADJUST_GRID, IMessageProvider.NONE);
        imageToDecode = new BarcodeImage(filename, imageSource);

        Rectangle2D.Double gridRectangle = (Rectangle2D.Double)
            imageSourceWidget.getGridRectangle().clone();

        // gridRectangle must fit inside the image rectangle
        if ((gridRectangle.x < 0) || !imageToDecode.contains(gridRectangle)) {
            // needs initialization
            AffineTransform t = AffineTransform.getScaleInstance(0.98, 0.98);
            Rectangle2D.Double rectangle = imageToDecode.getRectangle();
            gridRectangle = Swt2DUtil.transformRect(t, rectangle);
            log.trace("loadFile: initialization for gridRect");
        }

        decodeButton.setEnabled(true);
        plateGridWidget.updateImage(
            imageToDecode,
            gridRectangle,
            imageSourceWidget.getPlateOrientation(),
            imageSourceWidget.getPlateDimensions(),
            imageSourceWidget.getBarcodePosition());
    }

    private void removeImage() {
        imageToDecode = null;
        plateGridWidget.removeImage();
        decodeButton.setEnabled(false);
        removeDecodeInfo();
    }

    private void removeDecodeInfo() {
        plateGridWidget.removeDecodeInfo();
    }

    @SuppressWarnings("nls")
    private void decode() {
        IPreferenceStore prefs = ScannerConfigPlugin.getDefault().getPreferenceStore();
        final double minEdgeFactor = prefs.getDouble(PreferenceConstants.LIBDMTX_MIN_EDGE_FACTOR);
        final double maxEdgeFactor = prefs.getDouble(PreferenceConstants.LIBDMTX_MAX_EDGE_FACTOR);
        final double scanGapFactor = prefs.getDouble(PreferenceConstants.LIBDMTX_SCAN_GAP_FACTOR);
        final int debugLevel = prefs.getInt(PreferenceConstants.DLL_DEBUG_LEVEL);
        final int edgeThresh = prefs.getInt(PreferenceConstants.LIBDMTX_EDGE_THRESH);
        final int squareDev = prefs.getInt(PreferenceConstants.LIBDMTX_SQUARE_DEV);
        final int corrections = prefs.getInt(PreferenceConstants.LIBDMTX_CORRECTIONS);
        final String filename = imageSourceWidget.getFileName();

        saveGridRectangle();
        plateGridWidget.removeDecodeInfo();

        final Display display = Display.getDefault();

        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(PROGRESS_MESSAGE_DECODING, IProgressMonitor.UNKNOWN);
                Set<CellRectangle> wells = plateGridWidget.getCellsInPixels();

                decodeResult = ScanLib.getInstance().decodeImage(
                    debugLevel,
                    filename,
                    new DecodeOptions(
                        minEdgeFactor,
                        maxEdgeFactor,
                        scanGapFactor,
                        squareDev,
                        edgeThresh,
                        corrections,
                        DecodeOptions.DEFAULT_SHRINK),
                    wells.toArray(new CellRectangle[] {}));

                Result resultCode = decodeResult.getResultCode();

                log.debug("decode: result: {}, tubes decoded: {}",
                    resultCode, decodeResult.getDecodedWells().size());

                if (resultCode == Result.SUCCESS) {
                    display.asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            setMessage(TITLE_AREA_MESSAGE_DECODING_COMPLETED, IMessageProvider.NONE);
                            plateGridWidget.setDecodedWells(decodeResult.getDecodedWells(), scanMode);
                        }
                    });
                } else {
                    BgcPlugin.openAsyncError(
                        // TR: error dialog title
                        i18n.tr("Decoding Error"),
                        // TR: error dialog message
                        decodeResult.getMessage());
                }
                monitor.done();
            }
        };

        try {
            new ProgressMonitorDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell()).run(true, false, op);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("nls")
    private void saveGridRectangle() {
        if (imageToDecode != null) {
            Rectangle2D.Double gridRegion = plateGridWidget.getUserRegionInPixels();
            if (gridRegion == null) {
                throw new IllegalStateException("grid region is null");
            }
            imageSourceWidget.setGridRectangle(selectedImageSource, selectedDpi, gridRegion);
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected void okPressed() {
        if (imageToDecode != null) {
            saveGridRectangle();
        }
        imageSourceWidget.saveSettings();

        boolean okToProceed = true;

        if (decodeResult == null) {
            okToProceed = BgcPlugin.openConfirm(
                // dialog title.
                i18n.tr("Decode warning"),
                // dialog message.
                i18n.tr("No tubes were decoded. Continue?"));
        }

        if (okToProceed) super.okPressed();
    }

    public Set<DecodedWell> getDecodeResult() {
        if (decodeResult != null) {
            return decodeResult.getDecodedWells();
        }
        return new HashSet<DecodedWell>(0);
    }

    public PalletDimensions getPlateDimensions() {
        return imageSourceWidget.getPlateDimensions();
    }
}
