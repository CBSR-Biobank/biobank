package edu.ualberta.med.biobank.dialogs;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.dialogs.BiobankDialog;

public class AliquotStatusDialog extends BiobankDialog {

    AliquotWrapper aliquot;
    ActivityStatusWrapper activityStatus;

    ComboViewer combo;

    protected AliquotStatusDialog(Shell parentShell, AliquotWrapper aliquot) {
        super(parentShell);
        this.aliquot = aliquot;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        setTitle("Edit Aliquot Activity Status");
        setMessage("Choose a selection below:");
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        area.setLayout(layout);
        area.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        combo = getWidgetCreator().createComboViewerWithNoSelectionValidator(
            area,
            "Activity Status",
            ActivityStatusWrapper.getAllActivityStatuses(aliquot
                .getAppService()), aliquot.getActivityStatus(),
            "A sample type should be selected");
        combo.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                activityStatus = (ActivityStatusWrapper) ((StructuredSelection) combo
                    .getSelection()).getFirstElement();
            }
        });
    }

    public ActivityStatusWrapper getActivityStatus() {
        return activityStatus;
    }
}
