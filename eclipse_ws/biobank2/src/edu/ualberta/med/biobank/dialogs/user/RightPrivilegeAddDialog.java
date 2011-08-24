package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RightPrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class RightPrivilegeAddDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;
    private RoleWrapper role;
    private MultiSelectWidget<PrivilegeWrapper> privilegesWidget;
    private MultiSelectWidget<BbRightWrapper> rightsWidget;
    List<RightPrivilegeWrapper> rpList = new ArrayList<RightPrivilegeWrapper>();
    private RightPrivilegeWrapper editRp;

    public RightPrivilegeAddDialog(Shell parent, RoleWrapper role) {
        super(parent);
        Assert.isNotNull(role);
        this.role = role;
        currentTitle = "Add right/privileges associations";
        titleAreaMessage = "Add new right/privileges associations";
    }

    public int edit(RightPrivilegeWrapper rp) {
        this.editRp = rp;
        return open();
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
        contents.setLayout(new GridLayout(1, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        if (editRp == null) {
            rightsWidget = new MultiSelectWidget<BbRightWrapper>(contents,
                SWT.NONE, "Available rights", "Selected rights", 110) {
                @Override
                protected String getTextForObject(BbRightWrapper nodeObject) {
                    return nodeObject.getName();
                }
            };
            List<BbRightWrapper> allRights = BbRightWrapper
                .getAllRights(SessionManager.getAppService());
            allRights.removeAll(role.getRightsinUse());
            rightsWidget.setSelections(allRights,
                new ArrayList<BbRightWrapper>());
        } else {
            Label label = new Label(contents, SWT.NONE);
            label.setText(NLS.bind(
                "Editing privileges associated with right ''{0}'''", editRp
                    .getRight().getName()));
        }

        privilegesWidget = new MultiSelectWidget<PrivilegeWrapper>(contents,
            SWT.NONE, "Available privileges", "Selected privileges", 110) {
            @Override
            protected String getTextForObject(PrivilegeWrapper nodeObject) {
                return nodeObject.getName();
            }
        };
        List<PrivilegeWrapper> selection = new ArrayList<PrivilegeWrapper>();
        if (editRp != null)
            selection = editRp.getPrivilegeCollection(true);
        privilegesWidget.setSelections(
            PrivilegeWrapper.getAllPrivileges(SessionManager.getAppService()),
            selection);
    }

    @Override
    protected void okPressed() {
        if (editRp == null) {
            for (BbRightWrapper right : rightsWidget.getAddedToSelection()) {
                RightPrivilegeWrapper rp = new RightPrivilegeWrapper(
                    SessionManager.getAppService());
                rp.setRight(right);
                rp.addToPrivilegeCollection(privilegesWidget
                    .getAddedToSelection());
                rpList.add(rp);
            }
            role.addToRightPrivilegeCollection(rpList);
        } else {
            editRp.addToPrivilegeCollection(privilegesWidget
                .getAddedToSelection());
            editRp.removeFromPrivilegeCollection(privilegesWidget
                .getRemovedFromSelection());
        }
        super.okPressed();
    }

    public List<RightPrivilegeWrapper> getRightPrivilegeList() {
        return rpList;
    }

}