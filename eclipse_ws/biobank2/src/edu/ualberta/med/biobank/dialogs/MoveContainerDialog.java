package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.validators.StringLengthValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;

/**
 * Allows the user to move a container and its contents to a new location
 */
public class MoveContainerDialog extends BiobankDialog {

    private ContainerWrapper container;

    private IObservableValue newLabel = new WritableValue("", String.class);

    public MoveContainerDialog(Shell parent, ContainerWrapper container) {
        super(parent);
        Assert.isNotNull(container);
        this.container = container;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        String title = "Move Container";
        shell.setText(title);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Move Container " + container.getLabel());
        setMessage("Select the destination for this container.");
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.FILL,
            "Destination Address", null, newLabel, new StringLengthValidator(2,
                "Destination label must be another container "
                    + "(4 characters minimum)."));
    }

    public String getNewLabel() {
        return newLabel.getValue().toString().toUpperCase();
    }

}
