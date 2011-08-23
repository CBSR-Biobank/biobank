package edu.ualberta.med.biobank.dialogs.user;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RightPrivilegeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class RightPrivilegeAddDialog extends BgcBaseDialog {
    private final String currentTitle;
    private final String titleAreaMessage;
    private RoleWrapper role;
    private RightPrivilegeWrapper rp;
    private MultiSelectWidget<PrivilegeWrapper> privilegesWidget;

    public RightPrivilegeAddDialog(Shell parent, RoleWrapper role) {
        super(parent);
        Assert.isNotNull(role);
        this.role = role;
        this.rp = new RightPrivilegeWrapper(SessionManager.getAppService());
        currentTitle = "Add right/privileges association";
        titleAreaMessage = "Add a new right/privileges association";
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

        createComboViewer(contents, "Right",
            BbRightWrapper.getAllRights(SessionManager.getAppService()), null,
            "Please select a right", new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    rp.setRight((BbRightWrapper) selectedObject);
                }
            }, new BiobankLabelProvider() {
                @Override
                public String getText(Object element) {
                    return ((BbRightWrapper) element).getName();
                }
            });

        privilegesWidget = new MultiSelectWidget<PrivilegeWrapper>(contents,
            SWT.NONE, "Available privileges", "Selected privileges", 110) {
            @Override
            protected String getTextForObject(PrivilegeWrapper nodeObject) {
                return nodeObject.getName();
            }
        };
        GridData gd = (GridData) privilegesWidget.getLayoutData();
        gd.horizontalSpan = 2;

        privilegesWidget.setSelections(
            PrivilegeWrapper.getAllPrivileges(SessionManager.getAppService()),
            new ArrayList<PrivilegeWrapper>());
    }

    @Override
    protected void okPressed() {
        rp.addToPrivilegeCollection(privilegesWidget.getAddedToSelection());
        rp.removeFromPrivilegeCollection(privilegesWidget
            .getRemovedToSelection());
        role.addToRightPrivilegeCollection(Arrays.asList(rp));
        super.okPressed();
    }

    public RightPrivilegeWrapper getRightPrivilege() {
        return rp;
    }

}