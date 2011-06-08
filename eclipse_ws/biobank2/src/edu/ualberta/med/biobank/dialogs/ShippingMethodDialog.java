package edu.ualberta.med.biobank.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;

public class ShippingMethodDialog extends BgcBaseDialog {

    private static final String TITLE = "Shipping Method";
    private static final String MSG_NO_ST_NAME = "Shipping method must have a name.";
    private String message;
    private ShippingMethodWrapper shippingMethod;
    private String currentTitle;

    public ShippingMethodDialog(Shell parent,
        ShippingMethodWrapper shippingMethod, String message) {
        super(parent);
        this.shippingMethod = shippingMethod;
        this.message = message;
        currentTitle = (shippingMethod.getName() == null ? "Add " : "Edit ")
            + TITLE;
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
            "Name", null, shippingMethod, "name", new NonEmptyStringValidator(
                MSG_NO_ST_NAME));
    }
}
