package edu.ualberta.med.biobank.dialogs.select;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.dialogs.BiobankDialog;

public class SelectAliquotStatusDialog extends BiobankDialog {

    private AliquotWrapper aliquot;
    private ActivityStatusWrapper activityStatus;

    private Map<ActivityStatusWrapper, Button> buttons;

    public SelectAliquotStatusDialog(Shell parentShell, AliquotWrapper aliquot) {
        super(parentShell);
        this.aliquot = aliquot;
        activityStatus = aliquot.getActivityStatus();
        buttons = new HashMap<ActivityStatusWrapper, Button>();
    }

    @Override
    protected String getDialogShellTitle() {
        return "Activity Status";
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Select an activity status:";
    }

    @Override
    protected String getTitleAreaTitle() {
        return "Aliquot Activity Status";
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 10;
        area.setLayout(layout);
        area.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        for (ActivityStatusWrapper status : ActivityStatusWrapper
            .getAllActivityStatuses(aliquot.getAppService())) {
            Button b = new Button(area, SWT.RADIO);
            b.setText(status.getName());
            if (activityStatus.equals(status)) {
                b.setSelection(true);
            }
            buttons.put(status, b);

        }
    }

    @Override
    protected void okPressed() {
        for (ActivityStatusWrapper status : buttons.keySet()) {
            if (buttons.get(status).getSelection()) {
                activityStatus = status;
            }
        }
        super.okPressed();
    }

    public ActivityStatusWrapper getActivityStatus() {
        return activityStatus;
    }
}
