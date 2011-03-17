package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.infotables.entry.SpecimenTypeEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenTypesEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenTypesEntryForm";

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SpecimenTypesEntryForm.class.getName());

    public static final String OK_MESSAGE = "Add or edit a specimen type";

    private SpecimenTypeEntryInfoTable specimenWidget;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() throws Exception {
        setPartName("Specimen Types");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Specimen Types");
        page.setLayout(new GridLayout(1, false));
        createGlobalSampleTypeSection();
        setFirstControl(specimenWidget);
    }

    private void createGlobalSampleTypeSection() throws Exception {
        Section section = createSection("Specimen Types");
        List<SpecimenTypeWrapper> globalSampleTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        if (globalSampleTypes == null) {
            globalSampleTypes = new ArrayList<SpecimenTypeWrapper>();
        }
        specimenWidget = new SpecimenTypeEntryInfoTable(section,
            globalSampleTypes, "Add a new global specimen type",
            "Edit the global specimen type");
        specimenWidget.adaptToToolkit(toolkit, true);
        specimenWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(specimenWidget);

        addSectionToolbar(section, "Add a specimen type",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    specimenWidget.addSpecimenType();
                }
            }, SpecimenTypeWrapper.class);
        section.setClient(specimenWidget);
    }

    @Override
    public void saveForm() throws BiobankCheckException, Exception {
        SpecimenTypeWrapper.persistSpecimenTypes(
            specimenWidget.getAddedOrModifiedSpecimenTypes(),
            specimenWidget.getDeletedSpecimenTypes());
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
            globalSampleTypes = SpecimenTypeWrapper.getAllSpecimenTypes(
                appService, true);
        } catch (ApplicationException e) {
            logger.error("Can't reset global specimen types", e);
        }
        if (globalSampleTypes != null) {
            specimenWidget.setLists(globalSampleTypes);
        }
    }

    @Override
    protected void checkEditAccess() {
        if (!SessionManager.canUpdate(SpecimenTypeWrapper.class)
            && !SessionManager.canCreate(SpecimenTypeWrapper.class)
            && !SessionManager.canDelete(SpecimenTypeWrapper.class)) {
            BiobankPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                "Cannot access Specimen Type editor. Access Denied.");
        }
    }
}
