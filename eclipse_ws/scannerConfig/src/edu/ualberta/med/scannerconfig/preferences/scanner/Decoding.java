package edu.ualberta.med.scannerconfig.preferences.scanner;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.preferences.DoubleFieldEditor;
import edu.ualberta.med.scannerconfig.preferences.PreferenceConstants;

public class Decoding extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

    private final Map<String, DoubleFieldEditor> dblFieldMap =
        new HashMap<String, DoubleFieldEditor>();

    private final Map<String, IntegerFieldEditor> intFieldMap =
        new HashMap<String, IntegerFieldEditor>();

    private static final I18n i18n = I18nFactory.getI18n(Decoding.class);

    IntegerFieldEditor debugLevelInputField;

    DoubleFieldEditor scanGapFactorDblInput;

    DoubleFieldEditor edgeMinDblInput;

    DoubleFieldEditor edgeMaxDblInput;

    IntegerFieldEditor thresholdInputField;

    IntegerFieldEditor squaredevInputField;

    IntegerFieldEditor correctionsInputField;

    DoubleFieldEditor celldistDblInput;

    public Decoding() {
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
    protected void createFieldEditors() {

        debugLevelInputField = new IntegerFieldEditor(
            PreferenceConstants.DLL_DEBUG_LEVEL,
            i18n.tr("Library Debug Level:"),
            getFieldEditorParent());
        debugLevelInputField.setValidRange(0, 9);
        addField(debugLevelInputField);
        intFieldMap.put(debugLevelInputField.getPreferenceName(), debugLevelInputField);

        edgeMinDblInput = new DoubleFieldEditor(
            PreferenceConstants.LIBDMTX_MIN_EDGE_FACTOR,
            i18n.tr("Edge Minimum Factor:"),
            getFieldEditorParent());
        edgeMinDblInput.setValidRange(0.0, 1.0);
        addField(edgeMinDblInput);
        dblFieldMap.put(PreferenceConstants.LIBDMTX_MIN_EDGE_FACTOR, edgeMinDblInput);

        edgeMaxDblInput = new DoubleFieldEditor(
            PreferenceConstants.LIBDMTX_MAX_EDGE_FACTOR,
            i18n.tr("Edge Maximum Factor:"),
            getFieldEditorParent());
        edgeMaxDblInput.setValidRange(0.0, 1.0);
        addField(edgeMaxDblInput);
        dblFieldMap.put(PreferenceConstants.LIBDMTX_MAX_EDGE_FACTOR, edgeMaxDblInput);

        scanGapFactorDblInput = new DoubleFieldEditor(
            PreferenceConstants.LIBDMTX_SCAN_GAP_FACTOR,
            i18n.tr("Scan Gap Factor:"),
            getFieldEditorParent());
        scanGapFactorDblInput.setValidRange(0.0, 1.0);
        addField(scanGapFactorDblInput);
        dblFieldMap.put(PreferenceConstants.LIBDMTX_SCAN_GAP_FACTOR, scanGapFactorDblInput);

        thresholdInputField = new IntegerFieldEditor(
            PreferenceConstants.LIBDMTX_EDGE_THRESH,
            i18n.tr("Edge Threshold:"),
            getFieldEditorParent());
        thresholdInputField.setValidRange(0, 100);
        addField(thresholdInputField);
        intFieldMap.put(thresholdInputField.getPreferenceName(), thresholdInputField);

        squaredevInputField = new IntegerFieldEditor(
            PreferenceConstants.LIBDMTX_SQUARE_DEV,
            i18n.tr("Square Deviation:"),
            getFieldEditorParent());
        squaredevInputField.setValidRange(0, 90);
        addField(squaredevInputField);
        intFieldMap.put(squaredevInputField.getPreferenceName(), squaredevInputField);

        correctionsInputField = new IntegerFieldEditor(
            PreferenceConstants.LIBDMTX_CORRECTIONS,
            i18n.tr("Corrections:"),
            getFieldEditorParent());
        correctionsInputField.setValidRange(0, 100);
        addField(correctionsInputField);
        intFieldMap.put(correctionsInputField.getPreferenceName(), correctionsInputField);
    }

}
