package edu.ualberta.med.biobank.forms;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.widgets.infotables.entry.ActivityStatusEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public class ActivityStatusEntryForm extends BiobankEntryForm {

    private ActivityStatusEntryInfoTable activityStatusTable;

    public static final String ID = "edu.ualberta.med.biobank.forms.ActivityStatusMethodEntryForm";
    public static final String OK_MESSAGE = "View and edit activity statuses.";

    private BiobankEntryFormWidgetListener tableChangeListener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() throws Exception {
        setPartName("Activity Status Entry");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Activity Status Information");
        page.setLayout(new GridLayout(1, false));

        createActivityStatusSection();
        setFirstControl(activityStatusTable);

    }

    private void createActivityStatusSection() throws Exception {
        Section section = createSection("Activity Statuses");

        activityStatusTable = new ActivityStatusEntryInfoTable(section,
            "Add a new global activity status",
            "Edit the global activity status", null);

        activityStatusTable.adaptToToolkit(toolkit, true);
        activityStatusTable.addSelectionChangedListener(tableChangeListener);
        toolkit.paintBordersFor(activityStatusTable);

        addSectionToolbar(section, "Add Global Activity Status",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    activityStatusTable.addActivityStatusExternal();
                }
            });

        section.setClient(activityStatusTable);
    }

    // the table figures out what to add/delete
    // there is a maximum of one hql call to the database if no changes have
    // been made.
    @Override
    public void saveForm() throws BiobankCheckException, Exception {
        activityStatusTable.save();
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        activityStatusTable.reset();
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
    protected void checkEditAccess() {
        if (!SessionManager.canUpdate(ActivityStatusWrapper.class, null)
            && !SessionManager.canCreate(ActivityStatusWrapper.class, null)
            && !SessionManager.canDelete(ActivityStatusWrapper.class, null)) {
            BiobankPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                "Cannot access Activity Status editor. Access Denied.");
        }
    }

}
