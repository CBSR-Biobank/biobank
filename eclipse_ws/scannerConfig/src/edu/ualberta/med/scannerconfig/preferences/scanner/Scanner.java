package edu.ualberta.med.scannerconfig.preferences.scanner;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLib;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLibResult;
import edu.ualberta.med.scannerconfig.preferences.PreferenceConstants;
import edu.ualberta.med.scannerconfig.widgets.AdvancedRadioGroupFieldEditor;

public class Scanner extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage, SelectionListener {

    private final Map<String, IntegerFieldEditor> intFieldMap = new HashMap<String, IntegerFieldEditor>();

    private static final I18n i18n = I18nFactory.getI18n(Scanner.class);

    Button selectScannerBtn;
    AdvancedRadioGroupFieldEditor driverTypeRadio;

    IntegerFieldEditor brightnessInputField, contrastInputField;

    public Scanner() {
        super(GRID);
        setPreferenceStore(ScannerConfigPlugin.getDefault()
            .getPreferenceStore());
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(ScannerConfigPlugin.getDefault()
            .getPreferenceStore());
    }

    @SuppressWarnings("nls")
    @Override
    public void createFieldEditors() {
        selectScannerBtn = new Button(getFieldEditorParent(), SWT.NONE);
        selectScannerBtn.setText(i18n.tr("Select Scanner"));
        selectScannerBtn.setImage(ScannerConfigPlugin.getDefault()
            .getImageRegistry().get(ScannerConfigPlugin.IMG_SCANNER));
        selectScannerBtn.addSelectionListener(this);

        driverTypeRadio = new AdvancedRadioGroupFieldEditor(
            PreferenceConstants.SCANNER_DRV_TYPE,
            i18n.tr("Driver Type"),
            2,
            new String[][] {
                { "WIA", PreferenceConstants.SCANNER_DRV_TYPE_WIA },
                { "TWAIN", PreferenceConstants.SCANNER_DRV_TYPE_TWAIN } },
            getFieldEditorParent(), true);
        addField(driverTypeRadio);

        brightnessInputField = new IntegerFieldEditor(
            PreferenceConstants.SCANNER_BRIGHTNESS, i18n.tr("Brightness:"),
            getFieldEditorParent());
        brightnessInputField.setValidRange(-1000, 1000);
        addField(brightnessInputField);
        intFieldMap.put(brightnessInputField.getPreferenceName(),
            brightnessInputField);

        contrastInputField = new IntegerFieldEditor(
            PreferenceConstants.SCANNER_CONTRAST, i18n.tr("Contrast:"),
            getFieldEditorParent());
        contrastInputField.setValidRange(-1000, 1000);
        addField(contrastInputField);
        intFieldMap.put(contrastInputField.getPreferenceName(),
            contrastInputField);
    }

    private void setEnableAllWidgets(boolean enableSettings) {
        selectScannerBtn.setEnabled(true);
        driverTypeRadio.setEnabled(enableSettings, getFieldEditorParent());
        brightnessInputField.setEnabled(enableSettings, getFieldEditorParent());
        contrastInputField.setEnabled(enableSettings, getFieldEditorParent());
    }

    @SuppressWarnings("nls")
    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() != selectScannerBtn) return;

        ScanLibResult scanlibResult = ScanLib.getInstance().selectSourceAsDefault();

        if (scanlibResult.getResultCode() != ScanLibResult.Result.SUCCESS) {
            return;
        }

        ScanLibResult scannerCapResult = ScanLib.getInstance().getScannerCapability();
        int scannerCap = scannerCapResult.getValue();

        if (scanlibResult.getResultCode() != ScanLibResult.Result.SUCCESS) {
            // just stay with the last selected source
            if ((scannerCap & ScanLib.CAP_IS_SCANNER) != 0) {
                return;
            }
            setEnableAllWidgets(false);
            BgcPlugin.openError(i18n.tr("Scanning Source Error"), scanlibResult.getMessage());
            return;
        }

        IPreferenceStore prefs = ScannerConfigPlugin.getDefault().getPreferenceStore();

        String drvSetting = null;
        boolean[] drvRadioSettings = new boolean[] { false, false };

        if ((scannerCap & ScanLib.CAP_IS_WIA) != 0) {
            drvSetting = PreferenceConstants.SCANNER_DRV_TYPE_WIA;
            drvRadioSettings[1] = true;
        } else {
            drvSetting = PreferenceConstants.SCANNER_DRV_TYPE_TWAIN;
            drvRadioSettings[0] = true;
        }

        prefs.setValue(PreferenceConstants.SCANNER_DRV_TYPE, drvSetting);
        driverTypeRadio.setSelectionArray(drvRadioSettings);
        driverTypeRadio.doLoad();
        setEnableAllWidgets(true);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // do nothing
    }

}
