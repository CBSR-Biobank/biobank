package edu.ualberta.med.biobank.dialogs.startup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class WorkingCenterSelectDialog extends BgcBaseDialog {

    private UserWrapper user;
    private CenterWrapper<?> currentCenter;
    private List<CenterWrapper<?>> availableCenters;

    public WorkingCenterSelectDialog(Shell parentShell, UserWrapper user,
        List<CenterWrapper<?>> availableCenters) {
        super(parentShell);
        this.user = user;
        this.availableCenters = availableCenters;
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Choose the center you will work with.";
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Working center selection";
    }

    @Override
    protected String getDialogShellTitle() {
        return "Working center selection";
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        List<Object> objectList = new ArrayList<Object>(availableCenters);
        String noCenterString = "-- "
            + "no center selection"
            + " --";
        if (user.isSuperAdmin())
            objectList.add(noCenterString);
        widgetCreator.createComboViewer(contents,
            "Available centers",
            objectList, noCenterString, null, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    if (selectedObject instanceof CenterWrapper<?>)
                        currentCenter = (CenterWrapper<?>) selectedObject;
                    else
                        currentCenter = null;
                }
            }, new BiobankLabelProvider() {
                @Override
                public String getText(Object element) {
                    if (element instanceof CenterWrapper) {
                        return ((CenterWrapper<?>) element).getNameShort();
                    }
                    return super.getText(element);
                }
            });
    }

    @Override
    protected void okPressed() {
        user.setCurrentWorkingCenter(currentCenter);
        super.okPressed();
    }
}
