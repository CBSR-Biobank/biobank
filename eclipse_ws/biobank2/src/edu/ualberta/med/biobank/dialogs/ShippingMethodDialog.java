package edu.ualberta.med.biobank.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.peer.ShippingMethodPeer;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;

public class ShippingMethodDialog extends BgcBaseDialog {

    private String message;
    private ShippingMethodWrapper origShippingMethod;
    private ShippingMethodWrapper tmpShippingMethod;
    private String currentTitle;

    public ShippingMethodDialog(Shell parent,
        ShippingMethodWrapper shippingMethod, String message) {
        super(parent);
        this.origShippingMethod = shippingMethod;
        this.tmpShippingMethod = new ShippingMethodWrapper(null);
        copyTo(origShippingMethod, tmpShippingMethod);
        this.message = message;
        currentTitle = (shippingMethod.getName() == null ? "Add Shipping Method"
            : "Edit Shipping Method");
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

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(content, BgcBaseText.class, SWT.BORDER,
            "Name", null, tmpShippingMethod,
            ShippingMethodPeer.NAME.getName(), new NonEmptyStringValidator(
                "Shipping method must have a name."));
    }

    @Override
    protected void okPressed() {
        copyTo(tmpShippingMethod, origShippingMethod);
        super.okPressed();
    }
}
