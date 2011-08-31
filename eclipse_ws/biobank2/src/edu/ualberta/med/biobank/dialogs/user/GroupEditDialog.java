package edu.ualberta.med.biobank.dialogs.user;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.peer.BbGroupPeer;
import edu.ualberta.med.biobank.common.wrappers.BbGroupWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.widgets.infotables.MembershipInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class GroupEditDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;

    private BbGroupWrapper originalGroup;
    private MembershipInfoTable membershipInfoTable;

    public GroupEditDialog(Shell parent, BbGroupWrapper originalGroup) {
        super(parent);
        Assert.isNotNull(originalGroup);
        this.originalGroup = originalGroup;
        if (originalGroup.isNew()) {
            currentTitle = Messages.GroupEditDialog_title_add;
            titleAreaMessage = Messages.GroupEditDialog_titlearea_add;
        } else {
            currentTitle = Messages.GroupEditDialog_title_edit;
            titleAreaMessage = Messages.GroupEditDialog_titlearea_modify;
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
    protected void createDialogAreaInternal(Composite parent)
        throws ApplicationException {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBoundWidgetWithLabel(contents, BgcBaseText.class, SWT.BORDER,
            Messages.GroupEditDialog_property_title_name, null, originalGroup,
            BbGroupPeer.NAME.getName(), new NonEmptyStringValidator(
                Messages.GroupEditDialog_msg_name_required));

        createSection(contents,
            Messages.GroupEditDialog_membership_section_label,
            Messages.GroupEditDialog_membership_add_label,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addMembership();
                }
            });
        membershipInfoTable = new MembershipInfoTable(contents, originalGroup);
        GridData gd = (GridData) membershipInfoTable.getLayoutData();
        gd.horizontalSpan = 2;
    }

    protected void addMembership() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                MembershipAddDialog dlg = new MembershipAddDialog(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    originalGroup);
                int res = dlg.open();
                if (res == Status.OK) {
                    membershipInfoTable.getCollection()
                        .add(dlg.getMembership());
                    membershipInfoTable.reloadCollection(
                        originalGroup.getMembershipCollection(true), null);
                }
            }
        });
    }

    @Override
    protected void okPressed() {
        // try saving or updating the group inside this dialog so that if there
        // is an error the entered information is not lost
        try {
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

    @Override
    protected void cancelPressed() {
        try {
            originalGroup.reset();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.cancelPressed();
    }
}