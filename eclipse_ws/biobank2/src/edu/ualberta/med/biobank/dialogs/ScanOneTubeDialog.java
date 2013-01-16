package edu.ualberta.med.biobank.dialogs;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Specimen;


/**
 * This dialog prompts the user to enter a string that represents the value decoded from a single
 * aliquot tube. It is assumed that the user will use a hand scanner to decode the 2D barcode at
 * the bottom of the tube.
 * 
 * Once the value is entered by the user, it is checked against already decode values to
 * ensure that the same value is not recorded for two or more tubes.
 *
 */
public class ScanOneTubeDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory.getI18n(ScanOneTubeDialog.class);

    private final String label;
    private String scannedValue;
    private BgcBaseText valueText;
    private final Map<String, String> decodedBarcodes;

    /**
     * 
     * @param parentShell
     *      the parent SWT shell
     * @param decodedBarcodes
     *      maps decoded barcodes to corresponding position label.
     * @param label
     *      the label that corresponds to the tube.
     */
    public ScanOneTubeDialog(Shell parentShell, Map<String, String> decodedBarcodes, String label) {
        super(parentShell);
        this.label = label;
        this.decodedBarcodes = decodedBarcodes;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        area.setLayout(layout);
        area.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        widgetCreator.createLabel(area,
            Specimen.PropertyName.INVENTORY_ID.toString());
        valueText = widgetCreator.createText(area, SWT.NONE, null, null);
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        // TR: dialog title area message
        return i18n.tr("Scan the tube at position {0}", label);
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaTitle() {
        // TR: dialog title area title
        return i18n.tr("Pallet tube scan");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getDialogShellTitle() {
        // TR: dialog shell title
        return i18n.tr("Pallet tube scan");
    }

    @SuppressWarnings("nls")
    @Override
    protected void okPressed() {
        this.scannedValue = valueText.getText();

        // check if this value entered by the user belongs to another tube
        String label = decodedBarcodes.get(scannedValue);
        if (label != null) {
            BgcPlugin.openAsyncError(
                // TR: dialog title
                i18n.tr("Tube Scan Error"),
                // TR: dialog message
                i18n.tr("The value entered already exists in position {0}", label));
            valueText.setFocus();
            valueText.setSelection(0, scannedValue.length());
            return;
        }
        super.okPressed();
    }

    public String getScannedValue() {
        return scannedValue;
    }
}
