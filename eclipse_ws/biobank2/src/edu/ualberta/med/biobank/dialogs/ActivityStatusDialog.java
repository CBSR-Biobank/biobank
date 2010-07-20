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
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class ActivityStatusDialog extends BiobankDialog {

    private static final String TITLE = "Activity Status Method ";

    private static final String MSG_NO_ST_NAME = "Activity status  method must have a name.";

    private ActivityStatusWrapper origActivityStatus;

    // this is the object that is modified via the bound widgets
    private ActivityStatusWrapper ActivityStatus;

    private String message;

    private ActivityStatusWrapper oldActivityStatus;

    public ActivityStatusDialog(Shell parent,
        ActivityStatusWrapper ActivityStatus, String message) {
        super(parent);
        Assert.isNotNull(ActivityStatus);
        origActivityStatus = ActivityStatus;
        this.ActivityStatus = new ActivityStatusWrapper(null);
        ActivityStatus.setName(ActivityStatus.getName());
        this.message = message;
        oldActivityStatus = new ActivityStatusWrapper(
            SessionManager.getAppService());
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(((origActivityStatus.getName() == null) ? "Add "
            : "Edit ") + TITLE);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        if (origActivityStatus.getName() == null) {
            setTitle("Add Activity Status Method");
        } else {
            setTitle("Edit Activity Status Method");
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
            "Name", null, PojoObservables.observeValue(ActivityStatus, "name"),
            new NonEmptyStringValidator(MSG_NO_ST_NAME));

    }

    @Override
    protected void okPressed() {
        oldActivityStatus.setName(origActivityStatus.getName());
        origActivityStatus.setName(ActivityStatus.getName());
        super.okPressed();
    }

    public ActivityStatusWrapper getOrigActivityStatus() {
        return oldActivityStatus;
    }

}
