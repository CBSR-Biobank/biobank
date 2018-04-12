package edu.ualberta.med.scannerconfig.preferences.scanner.plateposition;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.scannerconfig.BarcodeImage;
import edu.ualberta.med.scannerconfig.FlatbedImageScan;
import edu.ualberta.med.scannerconfig.IScanImageListener;
import edu.ualberta.med.scannerconfig.ScanPlate;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.preferences.DoubleFieldEditor;
import edu.ualberta.med.scannerconfig.preferences.PreferenceConstants;
import edu.ualberta.med.scannerconfig.widgets.imageregion.IScanRegionWidget;
import edu.ualberta.med.scannerconfig.widgets.imageregion.ScanRegionCanvas;

/**
 * A preference page that manages the size of the scanning region for an individual plate.
 * 
 * @author loyola
 * 
 */
public class PlateSettings extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage, IScanImageListener, IScanRegionWidget {

    private static Logger log = LoggerFactory.getLogger(PlateSettings.class.getName());

    private static final I18n i18n = I18nFactory.getI18n(PlateSettings.class);

    @SuppressWarnings("nls")
    private enum Settings {
        NAME(i18n.tr("Name")),
        LEFT(i18n.tr("Left")),
        TOP(i18n.tr("Top")),
        RIGHT(i18n.tr("Right")),
        BOTTOM(i18n.tr("Bottom"));

        private final String label;

        private Settings(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }

    };

    @SuppressWarnings("nls")
    private static final String NOT_ENABLED_STATUS_MSG = i18n.tr("Plate is not enabled");

    @SuppressWarnings("nls")
    private static final String ALIGN_STATUS_MSG = i18n.tr("Align rectangle with the edge tubes");

    @SuppressWarnings("nls")
    private static final String SCAN_REQ_STATUS_MSG = i18n.tr("A scan is required");

    @SuppressWarnings("nls")
    private static final String SETTINGS_APPLIED = i18n.tr("The new settings have been saved");

    protected ListenerList changeListeners = new ListenerList();

    protected ScanPlate plateId;
    private boolean isEnabled;

    private Map<Settings, StringFieldEditor> plateFieldEditors;
    private Map<Settings, Text> plateTextControls;
    private ScanRegionCanvas canvas;

    private BooleanFieldEditor enabledFieldEditor;
    private Button scanBtn;

    private Label statusLabel;

    private final FlatbedImageScan flatbedImageScan;

    private boolean internalUpdate = false;

    private BarcodeImage flatbedImage;

    public PlateSettings(ScanPlate plateId) {
        super(GRID);
        this.plateId = plateId;

        IPreferenceStore prefs = ScannerConfigPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(prefs);

        flatbedImageScan = new FlatbedImageScan();
        flatbedImageScan.addScannedImageChangeListener(this);
    }

    @Override
    public void dispose() {
        flatbedImageScan.removeScannedImageChangeListener(this);
    }

    @Override
    public void setValid(boolean enable) {
        super.setValid(enable);

        getContainer().updateButtons();
        Button applyButton = getApplyButton();
        if (applyButton != null)
            applyButton.setEnabled(true);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(ScannerConfigPlugin.getDefault().getPreferenceStore());
    }

    @SuppressWarnings("nls")
    @Override
    protected Control createContents(final Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout(2, false));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        top.setLayoutData(gd);

        final Control s = super.createContents(top);
        gd = (GridData) s.getLayoutData();
        if (gd == null) {
            gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true);
        }
        gd.grabExcessHorizontalSpace = false;
        gd.widthHint = 160;
        s.setLayoutData(gd);

        Composite right = new Composite(top, SWT.NONE);
        right.setLayout(new GridLayout(1, false));
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.grabExcessHorizontalSpace = true;
        gd.widthHint = 200;
        right.setLayoutData(gd);

        canvas = new ScanRegionCanvas(right, this);

        statusLabel = new Label(right, SWT.BORDER);
        statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Composite buttonComposite = new Composite(right, SWT.NONE);
        buttonComposite.setLayout(new GridLayout(2, false));

        scanBtn = new Button(buttonComposite, SWT.NONE);
        scanBtn.setText(i18n.tr("Scan"));
        scanBtn.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                flatbedImageScan.scan();
            }
        });

        setEnabled(ScannerConfigPlugin.getDefault().getPlateEnabled(plateId));
        return top;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFieldEditors() {
        StringFieldEditor fe;

        enabledFieldEditor = new BooleanFieldEditor(
            PreferenceConstants.SCANNER_PALLET_ENABLED[plateId.getId() - 1],
            i18n.tr("Enable"),
            getFieldEditorParent());
        addField(enabledFieldEditor);

        String[] prefsArr = PreferenceConstants.SCANNER_PALLET_CONFIG[plateId.getId() - 1];

        plateFieldEditors = new HashMap<Settings, StringFieldEditor>();
        plateTextControls = new HashMap<Settings, Text>();
        Composite parent = getFieldEditorParent();

        int count = 0;
        for (Settings setting : Settings.values()) {
            if (setting == Settings.NAME) {
                fe = new StringFieldEditor(prefsArr[count], setting + ":", parent);
            } else {
                fe = new DoubleFieldEditor(prefsArr[count], setting + ":", parent);
                ((DoubleFieldEditor) fe).setValidRange(0, 20);
            }
            addField(fe);
            Text text = fe.getTextControl(parent);
            plateTextControls.put(setting, text);
            plateFieldEditors.put(setting, fe);

            if (setting != Settings.NAME) {
                text.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e) {
                        if (flatbedImage != null) {
                            if (!internalUpdate) {
                                scanRegionDimensionsUpdated();
                            }
                        }
                    }
                });
            }
            ++count;
        }
    }

    private Double parseDouble(String numberStr) {
        try {
            return Double.parseDouble(numberStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private Rectangle2D.Double getPlateRegionInches() {
        double left = parseDouble(plateFieldEditors.get(Settings.LEFT).getStringValue());
        double top = parseDouble(plateFieldEditors.get(Settings.TOP).getStringValue());
        double right = parseDouble(plateFieldEditors.get(Settings.RIGHT).getStringValue());
        double bottom = parseDouble(plateFieldEditors.get(Settings.BOTTOM).getStringValue());
        return new Rectangle2D.Double(left, top, right - left, bottom - top);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);
        Object source = event.getSource();
        if (source == enabledFieldEditor) {
            boolean enabled = enabledFieldEditor.getBooleanValue();
            if (enabled) {
                // set default size
                internalUpdate(new Rectangle2D.Double(0, 0, 4, 3));
            }
            setEnabled(enabled);
        }
    }

    @SuppressWarnings("nls")
    private void internalUpdate(Rectangle2D.Double region) {
        log.debug("internalUpdate: plate: {}", region);

        internalUpdate = true;
        plateTextControls.get(Settings.LEFT).setText(String.valueOf(region.x));
        plateTextControls.get(Settings.TOP).setText(String.valueOf(region.y));
        plateTextControls.get(Settings.RIGHT).setText(String.valueOf(region.x + region.width));
        plateTextControls.get(Settings.BOTTOM).setText(String.valueOf(region.y + region.height));
        internalUpdate = false;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    private void setEnabled(boolean enabled) {
        isEnabled = enabled;

        for (Text text : plateTextControls.values()) {
            if (text != null)
                text.setEnabled(enabled);
        }

        if (!enabled) {
            statusLabel.setText(NOT_ENABLED_STATUS_MSG);
        } else {
            statusLabel.setText(SCAN_REQ_STATUS_MSG);
        }
        scanBtn.setEnabled(enabled);
        scanWidgetSetEnabled(enabled);
    }

    private void saveSettings() {
        setEnabled(enabledFieldEditor.getBooleanValue());
        statusLabel.setText(SETTINGS_APPLIED);
    }

    @Override
    public boolean performOk() {
        saveSettings();
        return super.performOk();
    }

    @SuppressWarnings("nls")
    @Override
    public void imageAvailable(BarcodeImage image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }

        this.flatbedImage = image;
        Rectangle2D.Double imageRectangle = image.getRectangle();
        Rectangle2D.Double imageRectangleInches =
            ScannerConfigPlugin.rectangleToInches(FlatbedImageScan.PLATE_IMAGE_DPI, imageRectangle);

        statusLabel.setText(ALIGN_STATUS_MSG);

        ((DoubleFieldEditor) plateFieldEditors.get(Settings.LEFT)).setValidRange(0, imageRectangleInches.width);
        ((DoubleFieldEditor) plateFieldEditors.get(Settings.TOP)).setValidRange(0, imageRectangleInches.height);
        ((DoubleFieldEditor) plateFieldEditors.get(Settings.RIGHT)).setValidRange(0, imageRectangleInches.width);
        ((DoubleFieldEditor) plateFieldEditors.get(Settings.BOTTOM)).setValidRange(0, imageRectangleInches.height);

        Rectangle2D.Double plateRegionPixels =
            ScannerConfigPlugin.rectangleToPixels(FlatbedImageScan.PLATE_IMAGE_DPI, getPlateRegionInches());

        canvas.updateImage(image, plateRegionPixels);

        if (!imageRectangle.contains(plateRegionPixels)) {
            this.flatbedImage = null;
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Position error"),
                // TR: dialog message
                i18n.tr("Position settings are oustide the scanned image."
                    + "Please adjust the settings."));
            return;
        }
    }

    @Override
    public void imageDeleted() {
        canvas.removeImage();
        statusLabel.setText(SCAN_REQ_STATUS_MSG);
    }

    @SuppressWarnings("nls")
    @Override
    public void scanRegionChanged(Rectangle2D.Double region) {
        statusLabel.setText(ALIGN_STATUS_MSG);
        Rectangle2D.Double regionInInches =
            ScannerConfigPlugin.rectangleToInches(FlatbedImageScan.PLATE_IMAGE_DPI, region);
        log.debug("scanRegionChanged: region in pixels: {}", region);
        log.debug("scanRegionChanged: region in inches: {}", regionInInches);
        internalUpdate(regionInInches);
    }

    private void scanRegionDimensionsUpdated() {
        if (flatbedImage != null) {
            canvas.scanRegionDimensionsUpdated(getPlateRegionInches());
        }
    }

    private void scanWidgetSetEnabled(boolean setting) {
        if (flatbedImage != null) {
            if (setting) {
                canvas.enableRegion();
            } else {
                canvas.disableRegion();
            }
        }
    }
}
