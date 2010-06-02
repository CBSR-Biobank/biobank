package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class NewServerDialog extends BiobankDialog {

    protected BiobankText textBox;
    protected IObservableValue text = new WritableValue("", String.class);

    public NewServerDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        setTitle("New Server");
        setMessage("Enter the domain-name or IP address of the server:");
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 10;
        area.setLayout(layout);
        area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        NonEmptyStringValidator validator = new NonEmptyStringValidator(
            "Server field cannot be empty");
        textBox = (BiobankText) createBoundWidgetWithLabel(area,
            BiobankText.class, SWT.NONE, "Address", new String[0], text,
            validator);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        textBox.setLayoutData(gd);
    }

    @Override
    protected void okPressed() {
        this.close();
    }

    public String getText() {
        return (String) text.getValue();
    }

}
