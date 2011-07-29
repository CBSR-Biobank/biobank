package edu.ualberta.med.biobank.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.peer.ActivityStatusPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;

public class ActivityStatusDialog extends BgcBaseDialog {

    private static final String MSG_NO_ST_NAME = Messages.ActivityStatusDialog_name_validator_msg;
    private String message;
    private ActivityStatusWrapper origActivityStatus;
    private ActivityStatusWrapper tmpActivityStatus;
    private String currentTitle;

    public ActivityStatusDialog(Shell parent,
        ActivityStatusWrapper activityStatus, String message) {
        super(parent);
        this.origActivityStatus = activityStatus;
        this.tmpActivityStatus = new ActivityStatusWrapper(null);
        // a working copy is necessary to be sure the real object won't be
        // modified (in case of cancel or failure)
        copyTo(origActivityStatus, tmpActivityStatus);
        this.message = message;
        currentTitle = (activityStatus.getName() == null ? Messages.ActivityStatusDialog_title_add
            : Messages.ActivityStatusDialog_title_edit);
    }

    private void copyTo(ActivityStatusWrapper src, ActivityStatusWrapper dest) {
        dest.setName(src.getName());
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

        createBoundWidgetWithLabel(content, BgcBaseText.class, SWT.BORDER,
            Messages.ActivityStatusDialog_name_label, null, tmpActivityStatus,
            ActivityStatusPeer.NAME.getName(), new NonEmptyStringValidator(
                MSG_NO_ST_NAME));
    }

    @Override
    protected void okPressed() {
        copyTo(tmpActivityStatus, origActivityStatus);
        super.okPressed();
    }

}
