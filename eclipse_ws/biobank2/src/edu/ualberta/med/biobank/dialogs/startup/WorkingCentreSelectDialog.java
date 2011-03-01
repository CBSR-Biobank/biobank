package edu.ualberta.med.biobank.dialogs.startup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.BiobankDialog;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class WorkingCentreSelectDialog extends BiobankDialog {

    private User user;
    private BiobankApplicationService appService;
    private SiteWrapper currentCentre;

    public WorkingCentreSelectDialog(Shell parentShell,
        BiobankApplicationService appService, User user) {
        super(parentShell);
        this.appService = appService;
        this.user = user;
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.getString("WorkingCenterSelectDialog.description");
    }

    @Override
    protected String getTitleAreaTitle() {
        return Messages.getString("WorkingCenterSelectDialog.title");
    }

    @Override
    protected String getDialogShellTitle() {
        return Messages.getString("WorkingCenterSelectDialog.title");
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        widgetCreator.createComboViewer(contents, "Available centers",
            user.getWorkingCenters(appService), null, null,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    currentCentre = (SiteWrapper) selectedObject;
                }
            });
    }

    @Override
    protected void okPressed() {
        user.setCurrentWorkingSite(currentCentre);
        super.okPressed();
    }
}
