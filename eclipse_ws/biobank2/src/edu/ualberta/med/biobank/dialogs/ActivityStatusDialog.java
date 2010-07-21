package edu.ualberta.med.biobank.dialogs;

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
    BiobankText activityStatusBBText;
    String activityStatusStr;
    private String message, defaultText;
    private boolean addMode;

    public ActivityStatusDialog(Shell parent, boolean addMode, String message,
        String defaultText) {
        super(parent);
        this.addMode = addMode;
        this.defaultText = defaultText;
        this.message = message;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText((this.addMode ? "Add " : "Edit ") + TITLE);
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        if (addMode)
            setTitle("Add Activity Status Method");
        else
            setTitle("Edit Activity Status Method");

        setMessage(message);
        return contents;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        activityStatusBBText = (BiobankText) createBoundWidgetWithLabel(content,
            BiobankText.class, SWT.BORDER, "Name", null, null,
            new NonEmptyStringValidator(MSG_NO_ST_NAME));
        if (defaultText != null)
            activityStatusBBText.setText(defaultText);
    }

    @Override
    protected void okPressed() {
        this.activityStatusStr = activityStatusBBText.getText();
        super.okPressed();
    }

    public ActivityStatusWrapper getNewActivityStatus() {
        if (this.activityStatusStr != null) {
            ActivityStatusWrapper asw = new ActivityStatusWrapper(
                SessionManager.getAppService());
            asw.setName(this.activityStatusStr);
            return asw;
        } else
            return null;

    }
}
