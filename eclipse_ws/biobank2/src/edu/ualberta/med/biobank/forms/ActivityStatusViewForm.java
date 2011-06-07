package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.widgets.infotables.entry.ActivityStatusEntryInfoTable;

public class ActivityStatusViewForm extends BiobankFormBase {

    public static final String ID = "edu.ualberta.med.biobank.forms.ActivityStatusViewForm";

    public static final String OK_MESSAGE = "Add or edit a  activity status";

    private ActivityStatusEntryInfoTable statusWidget;

    @Override
    public void init() throws Exception {
        setPartName("Activity Status");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Activity Status");
        page.setLayout(new GridLayout(1, false));
        createGlobalSampleTypeSection();
    }

    private void createGlobalSampleTypeSection() throws Exception {
        Section section = createSection("Activity Status");
        List<ActivityStatusWrapper> globalActivityStatus = ActivityStatusWrapper
            .getAllActivityStatuses(appService);
        if (globalActivityStatus == null) {
            globalActivityStatus = new ArrayList<ActivityStatusWrapper>();
        }
        statusWidget = new ActivityStatusEntryInfoTable(section,
            globalActivityStatus, "Add a new global activity status",
            "Edit the global activity status");
        statusWidget.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(statusWidget);

        addSectionToolbar(section, "Add an activity status",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    statusWidget.addActivityStatus();
                }
            }, ActivityStatusWrapper.class);
        section.setClient(statusWidget);
    }

    protected void checkEditAccess() {
        if (!SessionManager.canUpdate(ActivityStatusWrapper.class)
            && !SessionManager.canCreate(ActivityStatusWrapper.class)
            && !SessionManager.canDelete(ActivityStatusWrapper.class)) {
            BiobankGuiCommonPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                "Cannot access  Activity Status editor. Access Denied.");
        }
    }

}
