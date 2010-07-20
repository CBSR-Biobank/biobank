package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.entry.ActivityStatusEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ActivityStatusMethodEntryForm extends BiobankEntryForm {

    private ActivityStatusEntryInfoTable activityStatusTable;

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ActivityStatusMethodEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.ActivityStatusMethodEntryForm";
    public static final String OK_MESSAGE = "View and edit activity status methods.";

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() throws Exception {
        setPartName("Activity Status Methods Entry");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Activity Status Method Information");
        page.setLayout(new GridLayout(1, false));

        createGlobalActivityStatusMethodSection();
        setFirstControl(activityStatusTable);

    }

    private void createGlobalActivityStatusMethodSection() throws Exception {
        Section section = createSection("Global Activity Status methods");
        List<ActivityStatusWrapper> globalActivityStatuses;
        Collection<ActivityStatusWrapper> globalActivityStatusColl = ActivityStatusWrapper
            .getAllActivityStatuses(appService);
        if (globalActivityStatusColl != null)
            globalActivityStatuses = new ArrayList<ActivityStatusWrapper>(
                globalActivityStatusColl);
        else
            globalActivityStatuses = new ArrayList<ActivityStatusWrapper>();

        activityStatusTable = new ActivityStatusEntryInfoTable(section,
            globalActivityStatuses, "Add a new global activity status method",
            "Edit the global activity status methods", null);

        activityStatusTable.adaptToToolkit(toolkit, true);
        activityStatusTable.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(activityStatusTable);

        addSectionToolbar(section, "Add Global Activity Status Method",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    activityStatusTable.addActivityStatus();
                }
            });

        section.setClient(activityStatusTable);
    }

    @Override
    public void saveForm() throws BiobankCheckException, Exception {
        ActivityStatusWrapper.persistActivityStatuses(
            activityStatusTable.getAddedOrModifiedActivityStatuss(),
            activityStatusTable.getDeletedActivityStatuss());

        activityStatusTable.getDeletedActivityStatuss().clear();
        activityStatusTable.getAddedOrModifiedActivityStatuss().clear();
    }

    @Override
    public String getNextOpenedFormID() {
        return null;
    }

    @Override
    protected String getOkMessage() {
        return null;
    }

    @Override
    public void reset() throws Exception {
        super.reset();

        List<ActivityStatusWrapper> globalActivityStatusMethods = null;
        try {
            globalActivityStatusMethods = new ArrayList<ActivityStatusWrapper>(
                ActivityStatusWrapper.getAllActivityStatuses(appService));
        } catch (ApplicationException e) {
            logger.error("Can't reset global activity status methods", e);
        }
        if (globalActivityStatusMethods != null) {
            activityStatusTable.setCollection(globalActivityStatusMethods);
        }
    }

    @Override
    protected void checkEditAccess() {
        if (!SessionManager.canUpdate(ActivityStatusWrapper.class)
            && !SessionManager.canCreate(ActivityStatusWrapper.class)
            && !SessionManager.canDelete(ActivityStatusWrapper.class)) {
            BioBankPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                "Cannot access Activity Status Method editor. Access Denied.");
        }
    }

}
