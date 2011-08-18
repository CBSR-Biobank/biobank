package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.infotables.entry.ActivityStatusEntryInfoTable;

public class ActivityStatusViewForm extends BiobankFormBase {

    public static final String ID = "edu.ualberta.med.biobank.forms.ActivityStatusViewForm"; //$NON-NLS-1$

    public static final String OK_MESSAGE = Messages.ActivityStatusViewForm_ok_msg;

    private ActivityStatusEntryInfoTable statusWidget;

    @Override
    public void init() throws Exception {
        setPartName(Messages.ActivityStatusViewForm_title);
        checkEditAccess();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.ActivityStatusViewForm_title);
        page.setLayout(new GridLayout(1, false));
        createGlobalSampleTypeSection();
    }

    private void createGlobalSampleTypeSection() throws Exception {
        Section section = createSection(Messages.ActivityStatusViewForm_title);
        List<ActivityStatusWrapper> globalActivityStatus = ActivityStatusWrapper
            .getAllActivityStatuses(appService);
        if (globalActivityStatus == null) {
            globalActivityStatus = new ArrayList<ActivityStatusWrapper>();
        }
        statusWidget = new ActivityStatusEntryInfoTable(section,
            globalActivityStatus,
            Messages.ActivityStatusViewForm_table_add_msg,
            Messages.ActivityStatusViewForm_table_edit_msg);
        statusWidget.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(statusWidget);

        addSectionToolbar(section,
            Messages.ActivityStatusViewForm_add_button_label,
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
            BgcPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                Messages.ActivityStatusViewForm_access_denied_error_msg);
        }
    }

}
