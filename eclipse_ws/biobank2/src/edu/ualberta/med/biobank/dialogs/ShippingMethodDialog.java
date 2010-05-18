package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ShippingMethodWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class ShippingMethodDialog extends BiobankDialog {

    private static final String TITLE = "Shipping Method ";

    private static final String MSG_NO_ST_NAME = "Shipping method must have a name.";

    private ShippingMethodWrapper origShippingMethod;

    // this is the object that is modified via the bound widgets
    private ShippingMethodWrapper shippingMethod;

    private String message;

    private ShippingMethodWrapper oldShippingMethod;

    public ShippingMethodDialog(Shell parent,
        ShippingMethodWrapper shippingMethod, String message) {
        super(parent);
        Assert.isNotNull(shippingMethod);
        origShippingMethod = shippingMethod;
        this.shippingMethod = new ShippingMethodWrapper(null);
        this.shippingMethod.setName(shippingMethod.getName());
        this.message = message;
        oldShippingMethod = new ShippingMethodWrapper(SessionManager
            .getAppService());
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(((origShippingMethod.getName() == null) ? "Add "
            : "Edit ")
            + TITLE);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        if (origShippingMethod.getName() == null) {
            setTitle("Add Shipping Method");
        } else {
            setTitle("Edit Shipping Method");
        }
        setMessage(message);
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(content, BiobankText.class, SWT.BORDER,
            "Name", null, PojoObservables.observeValue(shippingMethod, "name"),
            new NonEmptyStringValidator(MSG_NO_ST_NAME));

    }

    @Override
    protected void okPressed() {
        oldShippingMethod.setName(origShippingMethod.getName());
        origShippingMethod.setName(shippingMethod.getName());
        super.okPressed();
    }

    public ShippingMethodWrapper getOrigShippingMethod() {
        return oldShippingMethod;
    }

}
