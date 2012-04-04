package edu.ualberta.med.biobank.dialogs.dispatch;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;

public class PalletBarcodeDialog extends BgcBaseDialog {

    public PalletBarcodeDialog(Shell parentShell) {
        super(parentShell);
    }

    IObservableValue barcode = new WritableValue("", String.class); 

    @Override
    protected String getTitleAreaMessage() {
        return "Enter pallet barcode";
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Product Barcode Required";
    }

    @Override
    protected String getDialogShellTitle() {
        return "Product Barcode";
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        widgetCreator.createBoundWidgetWithLabel(parent, BgcBaseText.class,
            SWT.NONE, "Barcode", new String[] {}, barcode,
            new NonEmptyStringValidator("Barcode cannot be empty"));
    }

    public String getBarcode() {
        return (String) this.barcode.getValue();
    }

};
