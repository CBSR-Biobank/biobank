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
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.entry.SampleTypeEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SampleTypesEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleTypesEntryForm";

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SampleTypesEntryForm.class.getName());

    public static final String OK_MESSAGE = "Add or edit a sample type";

    private SampleTypeEntryInfoTable sampleWidget;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() throws Exception {
        setPartName("Sample Types");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Sample Types");
        page.setLayout(new GridLayout(1, false));
        createGlobalSampleTypeSection();
        setFirstControl(sampleWidget);
    }

    private void createGlobalSampleTypeSection() throws Exception {
        Section section = createSection("Sample Types");
        List<SpecimenTypeWrapper> globalSampleTypes = SpecimenTypeWrapper
            .getAllSampleTypes(appService, true);
        if (globalSampleTypes == null) {
            globalSampleTypes = new ArrayList<SpecimenTypeWrapper>();
        }
        sampleWidget = new SampleTypeEntryInfoTable(section, globalSampleTypes,
            "Add a new global sample type", "Edit the global sample type", null);
        sampleWidget.adaptToToolkit(toolkit, true);
        sampleWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(sampleWidget);

        addSectionToolbar(section, "Add a sample type", new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                sampleWidget.addSampleType();
            }
        }, SpecimenTypeWrapper.class);
        section.setClient(sampleWidget);
    }

    @Override
    public void saveForm() throws BiobankCheckException, Exception {
        SpecimenTypeWrapper.persistSampleTypes(
            sampleWidget.getAddedOrModifiedSampleTypes(),
            sampleWidget.getDeletedSampleTypes());
    }

    @Override
    public String getNextOpenedFormID() {
        return null;
    }

    @Override
    protected String getOkMessage() {
        return OK_MESSAGE;
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        List<SpecimenTypeWrapper> globalSampleTypes = null;
        try {
            globalSampleTypes = SpecimenTypeWrapper.getAllSampleTypes(appService,
                true);
        } catch (ApplicationException e) {
            logger.error("Can't reset global sample types", e);
        }
        if (globalSampleTypes != null) {
            sampleWidget.setLists(globalSampleTypes);
        }
    }

    @Override
    protected void checkEditAccess() {
        if (!SessionManager.canUpdate(SpecimenTypeWrapper.class, null)
            && !SessionManager.canCreate(SpecimenTypeWrapper.class, null)
            && !SessionManager.canDelete(SpecimenTypeWrapper.class, null)) {
            BioBankPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                "Cannot access Sample Type editor. Access Denied.");
        }
    }
}
