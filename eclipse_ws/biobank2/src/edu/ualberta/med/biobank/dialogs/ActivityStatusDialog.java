package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;

public class ActivityStatusDialog extends BiobankDialog {

    private static final String TITLE = "Activity Status";
    private static final String MSG_NO_ST_NAME = "Activity status must have a name.";
    private String message;
    private ActivityStatusWrapper activityStatus;

    public ActivityStatusDialog(Shell parent,
        ActivityStatusWrapper activityStatus, String message) {
        super(parent);
        this.activityStatus = new ActivityStatusWrapper(null);
        this.activityStatus.setName(activityStatus.getName());
        this.message = message;
    }

    @Override
    protected String getDialogShellTitle() {
        return (activityStatus.getName() == null ? "Add " : "Edit ") + TITLE;
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        if (activityStatus.getName() == null)
            setTitle("Add Activity Status");
        else
            setTitle("Edit Activity Status");

        setMessage(message);
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(content, BiobankText.class, SWT.BORDER,
            "Name", null, PojoObservables.observeValue(activityStatus, "name"),
            new NonEmptyStringValidator(MSG_NO_ST_NAME));
    }

    public ActivityStatusWrapper getActivityStatusCopy() {
        return activityStatus;
    }

}
