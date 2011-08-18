package edu.ualberta.med.biobank.dialogs.user;

import java.util.Arrays;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.BbRightWrapper;
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
                    getRightPrivilege().setRight(
                        (BbRightWrapper) selectedObject);
                }
            }, new BiobankLabelProvider() {
                @Override
                public String getText(Object element) {
                    return ((BbRightWrapper) element).getName();
                }
            });

        MultiSelectWidget privilegesWidget = new MultiSelectWidget(contents,
            SWT.NONE, "Available privileges", "Selected privileges", 80);
        GridData gd = (GridData) privilegesWidget.getLayoutData();
        gd.horizontalSpan = 2;

        // privilegesWidget.setSelections(available, selected)
    }

    @Override
    protected void okPressed() {
        role.addToRightPrivilegeCollection(Arrays.asList(getRightPrivilege()));
        super.okPressed();
    }

    public RightPrivilegeWrapper getRightPrivilege() {
        return rp;
    }

}