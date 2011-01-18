package edu.ualberta.med.biobank.dialogs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.security.Group;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class GroupEditDialog extends BiobankDialog {
    public static final int CLOSE_PARENT_RETURN_CODE = 3;
    private static final String TITLE = "Group";
    private final String currentTitle;
    private final String titleAreaMessage;
    private static final String MSG_NAME_REQUIRED = "A valid name is required.";
    private static final String GROUP_PERSIST_ERROR_TITLE = "Unable to Save Group";

    private Group originalGroup, modifiedGroup;

    public GroupEditDialog(Shell parent, Group originalGroup, boolean isNewGroup) {
        super(parent);
        Assert.isNotNull(originalGroup);
        this.originalGroup = originalGroup;
        this.modifiedGroup = new Group();
        this.modifiedGroup.copy(originalGroup);
        if (isNewGroup) {
            currentTitle = "Add " + TITLE;
            titleAreaMessage = "Add a new group";
        } else {
            currentTitle = "Edit " + TITLE;
            titleAreaMessage = "Modify an existing group's information";
        }
    }

    @Override
    protected String getDialogShellTitle() {
        return currentTitle;
    }

    @Override
    protected String getTitleAreaMessage() {
        return titleAreaMessage;
    }

    @Override
    protected String getTitleAreaTitle() {
        return currentTitle;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Control c = createBoundWidgetWithLabel(contents, BiobankText.class,
            SWT.BORDER, "Name", null, modifiedGroup, "name",
            new NonEmptyStringValidator(MSG_NAME_REQUIRED));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 250;
        c.setLayoutData(gd);
    }

    @Override
    protected void okPressed() {
        // try saving or updating the group inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            SessionManager.getAppService().persistGroup(modifiedGroup);
            originalGroup.copy(modifiedGroup);
            close();
        } catch (ApplicationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                BioBankPlugin.openAsyncError(GROUP_PERSIST_ERROR_TITLE,
                    "This name is already used.");
            } else {
                BioBankPlugin.openAsyncError(GROUP_PERSIST_ERROR_TITLE, e);
            }
        }
    }
}