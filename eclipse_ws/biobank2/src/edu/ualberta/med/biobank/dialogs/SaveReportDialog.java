package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.widgets.BiobankText;

/**
 * Allows the user to move a container and its contents to a new location
 */
public class SaveReportDialog extends BiobankDialog {

    private IObservableValue fileName = new WritableValue("", String.class);

    public SaveReportDialog(Shell parent) {
        super(parent);
    }

    @Override
    protected String getDialogShellTitle() {
        return "Save";
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Enter Name");
        setMessage("Please enter a name for this report.");
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createBoundWidgetWithLabel(contents, BiobankText.class, SWT.FILL,
            "Name", null, fileName, null);
    }

    public String getName() {
        return fileName.getValue().toString();
    }

}
