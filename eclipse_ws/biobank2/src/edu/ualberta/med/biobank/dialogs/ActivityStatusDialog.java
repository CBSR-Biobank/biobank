package edu.ualberta.med.biobank.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BiobankDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BiobankText;

public class ActivityStatusDialog extends BiobankDialog {

    private static final String TITLE = "Activity Status";
    private static final String MSG_NO_ST_NAME = "Activity status must have a name.";
    private String message;
    private ActivityStatusWrapper activityStatus;
    private String currentTitle;

    public ActivityStatusDialog(Shell parent,
        ActivityStatusWrapper activityStatus, String message) {
        super(parent);
        this.activityStatus = activityStatus;
        this.message = message;
        currentTitle = (activityStatus.getName() == null ? "Add " : "Edit ")
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

        createBoundWidgetWithLabel(content, BiobankText.class, SWT.BORDER,
            "Name", null, activityStatus, "name", new NonEmptyStringValidator(
                MSG_NO_ST_NAME));
    }
}
