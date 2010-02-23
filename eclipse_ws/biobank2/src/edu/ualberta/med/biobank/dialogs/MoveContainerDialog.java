package edu.ualberta.med.biobank.dialogs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.validators.AbstractValidator;

/**
 * Allows the user to move a container and its contents to a new location
 */
public class MoveContainerDialog extends BiobankDialog {

    private ContainerWrapper container;
    private Text text;
    private IObservableValue newLabel = new WritableValue("", String.class);

    private static final Pattern PATTERN = Pattern
        .compile("^([\\w&&[^_]]{2}){2,}$");

    public MoveContainerDialog(Shell parent, ContainerWrapper container) {
        super(parent);
        Assert.isNotNull(container);
        this.container = container;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        String title = "Moving Container: ";
        int id = container.getId();
        title += id;
        shell.setText(title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite parentComposite = (Composite) super.createDialogArea(parent);
        Composite contents = new Composite(parentComposite, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Control c = createBoundWidgetWithLabel(contents, Text.class,
            SWT.BORDER, "Source Address: ", new String[0], BeansObservables
                .observeValue(container, "label"), null);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;
        c.setLayoutData(gd);
        c.setEnabled(false);

        AbstractValidator validator = new AbstractValidator(null) {
            @Override
            public IStatus validate(Object value) {
                if (!(value instanceof String)) {
                    throw new RuntimeException(
                        "Not supposed to be called for non-strings.");
                }
                String v = (String) value;
                Matcher m = PATTERN.matcher(v);
                if (m.matches()) {
                    hideDecoration();
                    return Status.OK_STATUS;
                }
                showDecoration();
                return ValidationStatus
                    .error("Destination label must be another container (4 characters minimum).");
            }

        };
        text = (Text) createBoundWidgetWithLabel(contents, Text.class,
            SWT.FILL, "Destination Address", null, newLabel, validator);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;
        text.setLayoutData(gd);

        return contents;
    }

    public String getNewLabel() {
        return (String) newLabel.getValue();
    }

}
