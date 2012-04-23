package edu.ualberta.med.biobank.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.peer.ShippingMethodPeer;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.ShippingMethod;

public class ShippingMethodDialog extends BgcBaseDialog {
    private static final I18n i18n = I18nFactory
        .getI18n(ShippingMethodDialog.class);

    private final String message;
    private final ShippingMethodWrapper origShippingMethod;
    private final ShippingMethodWrapper tmpShippingMethod;
    private final String currentTitle;

    @SuppressWarnings("nls")
    public ShippingMethodDialog(Shell parent,
        ShippingMethodWrapper shippingMethod, String message) {
        super(parent);
        this.origShippingMethod = shippingMethod;
        this.tmpShippingMethod = new ShippingMethodWrapper(null);
        copyTo(origShippingMethod, tmpShippingMethod);
        this.message = message;
        currentTitle = (shippingMethod.getName() == null
            // TR: dialog title
            ? i18n.tr("Add Shipping Method")
            // TR: dialog title
            : i18n.tr("Edit Shipping Method"));
    }

    private void copyTo(ShippingMethodWrapper src, ShippingMethodWrapper dest) {
        dest.setName(src.getName());
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return message;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(content, BgcBaseText.class, SWT.BORDER,
            ShippingMethod.Property.NAME.toString(),
            null, tmpShippingMethod,
            ShippingMethodPeer.NAME.getName(), new NonEmptyStringValidator(
                // TR: validation error message
                i18n.tr("Shipping method must have a name.")));
    }

    @Override
    protected void okPressed() {
        copyTo(tmpShippingMethod, origShippingMethod);
        super.okPressed();
    }
}
