package edu.ualberta.med.biobank.dialogs.startup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class WorkingCenterSelectDialog extends BgcBaseDialog {

    private User user;
    private CenterWrapper<?> currentCenter;
    private List<CenterWrapper<?>> availableCenters;

    public WorkingCenterSelectDialog(Shell parentShell, User user,
        List<CenterWrapper<?>> availableCenters) {
        super(parentShell);
        this.user = user;
        this.availableCenters = availableCenters;
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.WorkingCenterSelectDialog_description;
    }

    @Override
    protected String getTitleAreaTitle() {
        return Messages.WorkingCenterSelectDialog_title;
    }

    @Override
    protected String getDialogShellTitle() {
        return Messages.WorkingCenterSelectDialog_title;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        List<Object> objectList = new ArrayList<Object>(availableCenters);
        String noCenterString = "-- " //$NON-NLS-1$ 
            + Messages.WorkingCenterSelectDialog_no_center_selection_text
            + " --"; //$NON-NLS-1$ 
        if (user.isInSuperAdminMode())
            objectList.add(noCenterString);
        widgetCreator.createComboViewer(contents,
            Messages.WorkingCenterSelectDialog_available_centers_label,
            objectList, noCenterString, null, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    if (selectedObject instanceof CenterWrapper<?>)
                        currentCenter = (CenterWrapper<?>) selectedObject;
                    else
                        currentCenter = null;
                }
            }, new BiobankLabelProvider());
    }

    @Override
    protected void okPressed() {
        user.setCurrentWorkingCenter(currentCenter);
        super.okPressed();
    }
}
