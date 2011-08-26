package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.peer.BbGroupPeer;
import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class GroupEditDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;

    private BbGroupWrapper originalGroup, modifiedGroup;

    public GroupEditDialog(Shell parent, BbGroupWrapper originalGroup) {
        super(parent);
        Assert.isNotNull(originalGroup);
        this.originalGroup = originalGroup;
        this.modifiedGroup = new BbGroupWrapper(null);
        copyGroup(originalGroup, modifiedGroup);
        if (originalGroup.isNew()) {
            currentTitle = Messages.GroupEditDialog_title_add;
            titleAreaMessage = Messages.GroupEditDialog_titlearea_add;
        } else {
            currentTitle = Messages.GroupEditDialog_title_edit;
            titleAreaMessage = Messages.GroupEditDialog_titlearea_modify;
        }
    }

    private void copyGroup(BbGroupWrapper src, BbGroupWrapper dest) {
        dest.setName(src.getName());
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
    protected void createDialogAreaInternal(Composite parent)
        throws ApplicationException {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.GroupEditDialog_property_title_name, null, modifiedGroup,
            BbGroupPeer.NAME.getName(), new NonEmptyStringValidator(
                Messages.GroupEditDialog_msg_name_required));
    }

    @Override
    protected void okPressed() {
        // try saving or updating the group inside this dialog so that if there
        // is an error the entered information is not lost
        try {
            copyGroup(modifiedGroup, originalGroup);
            originalGroup.persist();
            close();
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry")) { //$NON-NLS-1$
                BgcPlugin.openAsyncError(
                    Messages.GroupEditDialog_msg_persit_error,
                    Messages.GroupEditDialog_msg_error_name_used);
            } else {
                BgcPlugin.openAsyncError(
                    Messages.GroupEditDialog_msg_persit_error, e);
            }
        }
    }
}