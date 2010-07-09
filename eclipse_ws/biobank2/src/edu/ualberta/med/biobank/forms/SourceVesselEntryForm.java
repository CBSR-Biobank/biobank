package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.entry.SourceVesselEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SourceVesselEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SourceVesselEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.SourceVesselEntryForm";
    public static final String OK_MESSAGE = "View and edit source vessels.";

    private SourceVesselEntryInfoTable globalSourceWidget;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() throws Exception {
        setPartName("Source Vessels Entry");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Source Vessel Information");
        page.setLayout(new GridLayout(1, false));

        createGlobalSourceVesselSection();
        setFirstControl(globalSourceWidget);

    }

    private void createGlobalSourceVesselSection() throws Exception {
        Section section = createSection("Global source vessels");
        List<SourceVesselWrapper> globalSourceVessels = SourceVesselWrapper
            .getAllSourceVessels(appService);
        if (globalSourceVessels == null) {
            globalSourceVessels = new ArrayList<SourceVesselWrapper>();
        }
        globalSourceWidget = new SourceVesselEntryInfoTable(section,
            globalSourceVessels, "Add a new global source vessel",
            "Edit the global source vessel", null);
        globalSourceWidget.adaptToToolkit(toolkit, true);
        globalSourceWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(globalSourceWidget);

        addSectionToolbar(section, "Add Global Source Vessel",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    globalSourceWidget.addSourceVessel();
                }
            });
        section.setClient(globalSourceWidget);
    }

    @Override
    public void saveForm() throws BiobankCheckException, Exception {
        SourceVesselWrapper.persistSourceVessels(
            globalSourceWidget.getAddedOrModifiedSampleTypes(),
            globalSourceWidget.getDeletedSampleTypes());
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
        List<SourceVesselWrapper> globalSourceVessels = null;
        try {
            globalSourceVessels = SourceVesselWrapper
                .getAllSourceVessels(appService);
        } catch (ApplicationException e) {
            logger.error("Can't reset global source vessels", e);
        }
        if (globalSourceVessels != null) {
            globalSourceWidget.setLists(globalSourceVessels);
        }
    }

    @Override
    protected void checkEditAccess() {
        if (!SessionManager.canUpdate(SourceVesselWrapper.class)
            && !SessionManager.canCreate(SourceVesselWrapper.class)
            && !SessionManager.canDelete(SourceVesselWrapper.class)) {
            BioBankPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                "Cannot access Source Vessel editor. Access Denied.");
        }
    }

}
