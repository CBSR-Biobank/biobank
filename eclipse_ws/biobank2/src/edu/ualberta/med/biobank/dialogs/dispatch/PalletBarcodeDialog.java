package edu.ualberta.med.biobank.dialogs.dispatch;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Container;

public class PalletBarcodeDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(PalletBarcodeDialog.class);

    public PalletBarcodeDialog(Shell parentShell) {
        super(parentShell);
    }

    @SuppressWarnings("nls")
    IObservableValue barcode = new WritableValue("", String.class);

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        return i18n.tr("Enter pallet barcode");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaTitle() {
        return i18n.tr("Product Barcode Required");
    }

    @Override
    protected String getDialogShellTitle() {
        return Container.PropertyName.PRODUCT_BARCODE.toString();
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        widgetCreator.createBoundWidgetWithLabel(parent, BgcBaseText.class,
            SWT.NONE, Container.PropertyName.PRODUCT_BARCODE.toString(),
            new String[] {}, barcode,
            new NonEmptyStringValidator(i18n.tr("Barcode cannot be empty")));
    }

    public String getBarcode() {
        return (String) this.barcode.getValue();
    }

};
