package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.widgets.trees.infos.SpecimenTypeEntryInfoTree;

public class SpecimenTypesViewForm extends BiobankFormBase {

    public static final String ID = "edu.ualberta.med.biobank.forms.SpecimenTypesViewForm";

    public static final String OK_MESSAGE = "Add or edit a specimen type";

    private SpecimenTypeEntryInfoTree specimenWidget;

    private List<SpecimenTypeWrapper> globalSampleTypes;

    @Override
    public void init() throws Exception {
        setPartName("Specimen Types");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Specimen Types");
        page.setLayout(new GridLayout(1, false));

        globalSampleTypes = SpecimenTypeWrapper.getAllSpecimenTypes(appService,
            true);
        if (globalSampleTypes == null) {
            globalSampleTypes = new ArrayList<SpecimenTypeWrapper>();
        }
        createGlobalSampleTypeSection();
    }

    private void createGlobalSampleTypeSection() {
        Section section = createSection("Specimen Types");
        specimenWidget = new SpecimenTypeEntryInfoTree(section,
            globalSampleTypes, "Add a new global specimen type",
            "Edit the global specimen type");
        specimenWidget.adaptToToolkit(toolkit, true);
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
