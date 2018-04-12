package edu.ualberta.med.scannerconfig.widgets;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.widgets.Event;
import edu.ualberta.med.biobank.gui.common.widgets.GroupedRadioSelectionWidget;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.scannerconfig.BarcodePosition;
import edu.ualberta.med.scannerconfig.ImageSource;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import edu.ualberta.med.scannerconfig.PalletOrientation;
import edu.ualberta.med.scannerconfig.ScanPlate;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dialogs.ImageSourceDialogSettings;
import edu.ualberta.med.scannerconfig.dialogs.ImageSourceSettings;
import edu.ualberta.med.scannerconfig.preferences.PreferenceConstants;
import edu.ualberta.med.scannerconfig.preferences.scanner.ScannerDpi;

/**
 * A widget that allows the user to select the source for where to scan or load an image containinig
 * 2D barcodes. The user of the widget also registers a listener callback to be notified when the
 * user has made the selection and is ready to aquire the image.
 * 
 * @author nelson
 * 
 */
public class ImageSourceWidget extends Composite implements SelectionListener {

    private static final I18n i18n = I18nFactory.getI18n(ImageSourceWidget.class);

    private static Logger log = LoggerFactory.getLogger(ImageSourceWidget.class.getName());

    @SuppressWarnings("nls")
    private static final String SCANNED_FILE_FILE_NAME_FORMAT = "plate_scan__%d.bmp";

    @SuppressWarnings("nls")
    private static final String FAKE_SCANNED_IMAGE_FILE_NAME = "fakeScannedImage.bmp";

    private final Map<ImageSource, String> imageSourceSelections;

    private final BgcWidgetCreator widgetCreator;

    private final GroupedRadioSelectionWidget<ImageSource> imageSourceSelectionWidget;

    private final ImageFileWidget imageFileWidget;

    private final PalletOrientationWidget plateOrientationWidget;

    private final BarcodePositionWidget barcodePositionWidget;

    private final ScannerDpiWidget scannerDpiWidget;

    private final Composite scanningButtonsWidget;

    private Button scanButton;

    private Button scanAndDecodeButton;

    private Button rescanButton;

    private final Set<PalletDimensions> validPlateDimensions;

    private PalletDimensions selectedPlateDimensions;

    private final ImageSourceDialogSettings dialogSettings;

    private final ComboViewer plateDimensionsWidget;

    private edu.ualberta.med.biobank.gui.common.events.SelectionListener selectionListener;

    /**
     * A widget that allows the user to select where source for retrieving an image containinig 2D
     * barcodes. The possible choices are either from one of the plates flatbed scanner or an image
     * from the file system. The flatbed scanning plate regions must first be enabled in the
     * preferences for the corresponding plates to be displayed by this widget.
     * 
     * @param parent The parent composite.
     * @param minWidth The minimum with allowed for the grid the widget is displayed in.
     * @param widgetCreator
     * @param imageSourceInitVal The initial value for the image source.
     * @param scanPlateInitVal The initial value for the plate to be scanned. This value can be
     *            {@link null}.
     * @param cameraPositionInitVal The initial value for the camera position. See
     *            {@link BarcodePosition} for possible values.
     */
    @SuppressWarnings("nls")
    public ImageSourceWidget(
        Composite parent,
        int minWidth,
        BgcWidgetCreator widgetCreator,
        IDialogSettings parentDialogSettings,
        Set<PalletDimensions> validPlateDimensions) {
        super(parent, SWT.NONE);
        this.widgetCreator = widgetCreator;
        this.validPlateDimensions = validPlateDimensions;
        this.dialogSettings = new ImageSourceDialogSettings(parentDialogSettings);
        imageSourceSelections = Collections.unmodifiableMap(getImageSources());

        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginRight = 5;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        setLayout(layout);

        GridData gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true);
        gridData.widthHint = minWidth;
        setLayoutData(gridData);

        ImageSource imageSource = dialogSettings.getImageSource();

        imageSourceSelectionWidget = new GroupedRadioSelectionWidget<ImageSource>(
            this, i18n.tr("Image Source"), imageSourceSelections, imageSource);
        imageSourceSelectionWidget.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ImageSource selection = imageSourceSelectionWidget.getSelection();
                updateVisibleWidgets(selection);

                // be careful here, re-laying out is tricky
                getParent().getParent().layout(true, true);

                dialogSettings.setImageSource(selection);
                ImageSourceSettings imageSourceSettings = getImageSourceSettings();
                plateOrientationWidget.setSelection(imageSourceSettings.getOrientation());
                plateDimensionsWidget.setSelection(
                    new StructuredSelection(imageSourceSettings.getDimensions()));
                barcodePositionWidget.setSelection(imageSourceSettings.getBarcodePosition());
                scannerDpiWidget.setSelection(imageSourceSettings.getScannerDpi());

                Event newEvent = new Event();
                newEvent.widget = ImageSourceWidget.this;
                newEvent.type = SWT.Selection;
                newEvent.data = selection;
                newEvent.detail = ImageSourceAction.IMAGE_SOURCE_CHANGED.getId();
                ImageSourceWidget.this.selectionListener.widgetSelected(newEvent);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

        });

        ImageSourceSettings imageSourceSettings = dialogSettings.getImageSourceSettings(imageSource);

        PalletDimensions palletDimensions = imageSourceSettings.getDimensions();
        if (!validPlateDimensions.contains(palletDimensions)) {
            // last used pallet dimensions not in valid set, use the default value
            palletDimensions = validPlateDimensions.iterator().next();
        }
        plateDimensionsWidget = createPlateDimensionsWidget(this, palletDimensions);

        plateOrientationWidget = createPlateOrientationWidget(this, imageSourceSettings.getOrientation());
        barcodePositionWidget = createBarcodePositionWidget(this, imageSourceSettings.getBarcodePosition());

        scannerDpiWidget = createScannerDpiWidget(this, imageSourceSettings.getScannerDpi());
        imageFileWidget = createImageFileWidget(this);
        scanningButtonsWidget = createScanningButtons();

        updateVisibleWidgets(imageSource);
    }

    private PalletOrientationWidget createPlateOrientationWidget(
        Composite parent,
        PalletOrientation orientation) {
        final PalletOrientationWidget widget = new PalletOrientationWidget(parent, orientation);
        widget.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Event newEvent = new Event();
                newEvent.widget = ImageSourceWidget.this;
                newEvent.type = SWT.Selection;
                newEvent.data = widget.getSelection();
                newEvent.detail = ImageSourceAction.PLATE_ORIENTATION.getId();
                ImageSourceWidget.this.selectionListener.widgetSelected(newEvent);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        return widget;
    }

    @SuppressWarnings("nls")
    private ComboViewer createPlateDimensionsWidget(
        Composite parent,
        PalletDimensions defaultSelection) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        composite.setLayoutData(gd);

        if (!validPlateDimensions.contains(defaultSelection)) {
            throw new IllegalArgumentException("default selection not contained in valid set");
        }

        selectedPlateDimensions = defaultSelection;

        return widgetCreator.createComboViewer(
            composite,
            i18n.tr("Plate dimensions"),
            validPlateDimensions,
            selectedPlateDimensions,
            i18n.tr("select dimension for the plate"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    if (selectedObject instanceof PalletDimensions) {
                        selectedPlateDimensions = (PalletDimensions) selectedObject;

                        if (ImageSourceWidget.this.selectionListener != null) {
                            Event newEvent = new Event();
                            newEvent.widget = ImageSourceWidget.this;
                            newEvent.type = SWT.Selection;
                            newEvent.data = selectedPlateDimensions;
                            newEvent.detail = ImageSourceAction.PLATE_DIMENSIONS.getId();
                            ImageSourceWidget.this.selectionListener.widgetSelected(newEvent);
                        }
                    } else {
                        throw new IllegalStateException("invalid selection");
                    }
                }
            },
            new LabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof PalletDimensions) {
                        return ((PalletDimensions) element).getDisplayLabel();
                    }
                    return super.getText(element);
                }
            });
    }

    private BarcodePositionWidget createBarcodePositionWidget(
        Composite parent,
        BarcodePosition defaultValue) {
        BarcodePositionWidget widget = new BarcodePositionWidget(parent, defaultValue);
        widget.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Event newEvent = new Event();
                newEvent.widget = ImageSourceWidget.this;
                newEvent.type = SWT.Selection;
                newEvent.detail = ImageSourceAction.BARCODE_POSITION.getId();
                newEvent.data = barcodePositionWidget.getSelection();
                ImageSourceWidget.this.selectionListener.widgetSelected(newEvent);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        return widget;
    }

    private ScannerDpiWidget createScannerDpiWidget(Composite parent, ScannerDpi dpi) {
        final ScannerDpiWidget widget = new ScannerDpiWidget(parent, dpi);
        widget.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Event newEvent = new Event();
                newEvent.widget = ImageSourceWidget.this;
                newEvent.type = SWT.Selection;
                newEvent.data = widget.getSelection();
                newEvent.detail = ImageSourceAction.DPI_CHANGED.getId();
                ImageSourceWidget.this.selectionListener.widgetSelected(newEvent);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        return widget;
    }

    private ImageFileWidget createImageFileWidget(Composite parent) {
        ImageFileWidget widget = new ImageFileWidget(parent);

        widget.addSelectionListener(
            new edu.ualberta.med.biobank.gui.common.events.SelectionListener() {

                @Override
                public void widgetSelected(Event e) {
                    ImageSourceWidget.this.selectionListener.widgetSelected(e);

                }
            });
        return widget;
    }

    @SuppressWarnings("nls")
    private Composite createScanningButtons() {
        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        composite.setLayoutData(gd);

        scanButton = new Button(composite, SWT.PUSH);
        scanButton.setText(i18n.tr("Scan"));
        scanButton.addSelectionListener(this);

        rescanButton = new Button(composite, SWT.PUSH);
        rescanButton.setText(i18n.tr("Rescan"));
        rescanButton.addSelectionListener(this);

        scanAndDecodeButton = new Button(composite, SWT.PUSH);
        scanAndDecodeButton.setText(i18n.tr("Scan && Decode"));
        scanAndDecodeButton.addSelectionListener(this);

        return composite;
    }

    private void updateVisibleWidgets(ImageSource imageSource) {
        scanningButtonsWidget.setVisible(imageSource != ImageSource.FILE);
        scannerDpiWidget.setVisible(imageSource != ImageSource.FILE);
        imageFileWidget.setVisible(imageSource == ImageSource.FILE);
    }

    public void addSelectionListener(
        edu.ualberta.med.biobank.gui.common.events.SelectionListener listener) {
        this.selectionListener = listener;
    }

    @SuppressWarnings("nls")
    public Rectangle2D.Double getGridRectangle() {
        ImageSource imageSource = imageSourceSelectionWidget.getSelection();
        ImageSourceSettings pastImageSourceSettings = dialogSettings.getImageSourceSettings(imageSource);
        Rectangle2D.Double gridRectangle = pastImageSourceSettings.getGridRectangle();

        boolean girdNotInitialized = (gridRectangle.x < 0) || (gridRectangle.y < 0)
            || (gridRectangle.width < 0) || (gridRectangle.height < 0);

        if ((imageSource == ImageSource.FILE) || girdNotInitialized) {
            return gridRectangle;
        }

        // convert from inches to pixels
        int dpi = getDpi().getValue();
        Double gridPixels = ScannerConfigPlugin.rectangleToPixels(dpi, gridRectangle);
        log.debug("getGridRectangle: rect: {}", gridPixels);
        return gridPixels;
    }

    @SuppressWarnings("nls")
    public void setGridRectangle(
        ImageSource imageSource,
        ScannerDpi scannerDpi,
        Rectangle2D.Double gridPixels) {

        if ((imageSource != ImageSource.FILE) && (scannerDpi == ScannerDpi.DPI_UNKNOWN)) {
            throw new IllegalArgumentException("invalid image source and dpi");
        }

        if (scannerDpi != ScannerDpi.DPI_UNKNOWN) {
            int dpi = scannerDpi.getValue();
            Rectangle2D.Double gridInches = ScannerConfigPlugin.rectangleToInches(dpi, gridPixels);
            getImageSourceSettings().setGridRectangle(gridInches);
            log.debug("setGridRectangle: dpi: {}, rect: {}", dpi, gridInches);
        } else {
            getImageSourceSettings().setGridRectangle(gridPixels);
            log.debug("setGridRectangle: rect: {}", gridPixels);
        }

    }

    public PalletOrientation getPlateOrientation() {
        return plateOrientationWidget.getSelection();
    }

    public PalletDimensions getPlateDimensions() {
        return selectedPlateDimensions;
    }

    public BarcodePosition getBarcodePosition() {
        return barcodePositionWidget.getSelection();
    }

    @SuppressWarnings("nls")
    private Map<ImageSource, String> getImageSources() {
        Map<ImageSource, String> map = new LinkedHashMap<ImageSource, String>();

        IPreferenceStore prefs = ScannerConfigPlugin.getDefault().getPreferenceStore();

        for (ImageSource source : ImageSource.values()) {
            if (source != ImageSource.FILE) {
                ScanPlate scanPlate = source.getScanPlate();
                if (ScannerConfigPlugin.getDefault().getPlateEnabled(scanPlate)) {

                    String[] prefsArr =
                        PreferenceConstants.SCANNER_PALLET_CONFIG[scanPlate.getId() - 1];
                    String plateName = prefs.getString(prefsArr[0]);

                    StringBuffer buf = new StringBuffer();
                    buf.append(source.getDisplayLabel());
                    if (!plateName.isEmpty()) {
                        buf.append(": ");
                        buf.append(plateName);
                    }
                    map.put(source, buf.toString());
                }
            }
        }

        // the source to load an image from a image is always available
        map.put(ImageSource.FILE, ImageSource.FILE.getDisplayLabel());
        return map;
    }

    @SuppressWarnings("nls")
    @Override
    public void widgetSelected(SelectionEvent e) {
        // tell parent
        if (selectionListener != null) {
            Event newEvent = new Event();
            newEvent.widget = this;
            newEvent.type = SWT.Selection;

            if (e.getSource().equals(scanButton)) {
                newEvent.detail = ImageSourceAction.SCAN.getId();
                newEvent.data = imageSourceSelectionWidget.getSelection();
            } else if (e.getSource().equals(scanAndDecodeButton)) {
                newEvent.detail = ImageSourceAction.SCAN_AND_DECODE.getId();
                newEvent.data = imageSourceSelectionWidget.getSelection();
            } else if (e.getSource().equals(rescanButton)) {
                newEvent.detail = ImageSourceAction.RESCAN.getId();
                newEvent.data = imageSourceSelectionWidget.getSelection();
            } else {
                throw new IllegalStateException("invalid source for event: " + e.getSource());
            }

            selectionListener.widgetSelected(newEvent);
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
    }

    private ImageSourceSettings getImageSourceSettings() {
        ImageSource source = dialogSettings.getImageSource();
        return dialogSettings.getImageSourceSettings(source);
    }

    public ImageSource getImageSource() {
        return imageSourceSelectionWidget.getSelection();
    }

    public String getFileName() {
        ImageSource source = getImageSource();
        if (source == ImageSource.FILE) {
            return imageFileWidget.getFilename();
        }

        if (haveFakeScannedImage()) {
            return FAKE_SCANNED_IMAGE_FILE_NAME;
        }

        ScanPlate scanPlate = source.getScanPlate();
        return String.format(SCANNED_FILE_FILE_NAME_FORMAT, scanPlate.getId());
    }

    public ScannerDpi getDpi() {
        ImageSource source = dialogSettings.getImageSource();
        if (source == ImageSource.FILE) {
            return ScannerDpi.DPI_UNKNOWN;
        }
        return scannerDpiWidget.getSelection();
    }

    public void setPlateOrientation(PalletOrientation orientation) {
        getImageSourceSettings().setOrientation(orientation);
    }

    public void setPlateDimensions(PalletDimensions dimensions) {
        getImageSourceSettings().setDimensions(dimensions);
    }

    public void setBarcodePosition(BarcodePosition barcodePosition) {
        getImageSourceSettings().setBarcodePosition(barcodePosition);
    }

    public void setScannerDpi(ScannerDpi dpi) {
        getImageSourceSettings().setScannerDpi(dpi);
    }

    /**
     * Saves the selections the user made in the dialog box.
     */
    public void saveSettings() {
        setPlateOrientation(plateOrientationWidget.getSelection());
        setPlateDimensions(selectedPlateDimensions);
        setBarcodePosition(barcodePositionWidget.getSelection());

        ImageSource selection = imageSourceSelectionWidget.getSelection();
        if (selection != ImageSource.FILE) {
            setScannerDpi(scannerDpiWidget.getSelection());
        }
        dialogSettings.save();
    }

    // used for debugging in Linux
    public boolean haveFakeScannedImage() {
        boolean result = false;

        // When debug is on, use a fake image if the file exits
        if (ScannerConfigPlugin.getDefault().isDebugging()) {
            File fakeScannedFile = new File(FAKE_SCANNED_IMAGE_FILE_NAME);
            result = fakeScannedFile.exists();
        }

        return result;
    }
}
