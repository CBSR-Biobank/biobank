package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.widgets.infotables.SampleTypeEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SampleTypesEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SampleTypesEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleTypesEntryForm";
    public static final String OK_MESSAGE = "View and edit sample types.";

    private SiteWrapper siteWrapper;
    private List<SampleTypeWrapper> globalSampleTypes;
    private List<SampleTypeWrapper> siteSampleTypes;
    private SampleTypeEntryInfoTable siteSampleWidget;
    private SampleTypeEntryInfoTable globalSampleWidget;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() throws Exception {
        SiteAdapter siteAdapter = (SiteAdapter) adapter;
        siteWrapper = siteAdapter.getWrapper();

        globalSampleTypes = SampleTypeWrapper.getGlobalSampleTypes(appService,
            true);
        siteSampleTypes = siteWrapper.getSampleTypeCollection(true);
        setPartName("Aliquot Types Entry");
    }

    @Override
    protected void createFormContent() {
        form.setText("Aliquot Type Information");
        form.getBody().setLayout(new GridLayout(1, false));
        if (!siteWrapper.getName().equals("All Sites"))
            createSiteSampleTypeSection();
        createGlobalSampleTypeSection();
        if (!siteWrapper.getName().equals("All Sites"))
            firstControl = siteSampleWidget;
        else
            firstControl = globalSampleWidget;
    }

    private void createSiteSampleTypeSection() {
        Section section = createSection("Site Aliquot Types");
        siteSampleWidget = new SampleTypeEntryInfoTable(section,
            siteSampleTypes, globalSampleTypes,
            "Add a new sample type to the repository site",
            "Edit the repository site's sample type");
        siteSampleWidget.adaptToToolkit(toolkit, true);
        siteSampleWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(siteSampleWidget);

        addSectionToolbar(section, "Add Site Aliquot Type",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    siteSampleWidget.addSampleType();
                }
            });
        section.setClient(siteSampleWidget);
    }

    private void createGlobalSampleTypeSection() {
        Section section = createSection("Global Aliquot Types");
        globalSampleWidget = new SampleTypeEntryInfoTable(section,
            globalSampleTypes, siteSampleTypes, "Add a new global sample type",
            "Edit the global sample type");
        globalSampleWidget.adaptToToolkit(toolkit, true);
        globalSampleWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(globalSampleWidget);

        addSectionToolbar(section, "Add Global Aliquot Type",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    globalSampleWidget.addSampleType();
                }
            });
        section.setClient(globalSampleWidget);
    }

    @Override
    public void saveForm() throws BiobankCheckException, Exception {
        if (siteWrapper != null) {
            siteWrapper.reload();
            siteWrapper.addSampleTypes(siteSampleWidget
                .getAddedOrModifiedSampleTypes());
            siteWrapper.removeSampleTypes(siteSampleWidget
                .getDeletedSampleTypes());
            siteWrapper.persist();
        }
        SampleTypeWrapper.persistGlobalSampleTypes(globalSampleWidget
            .getAddedOrModifiedSampleTypes(), globalSampleWidget
            .getDeletedSampleTypes());
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
        try {
            globalSampleTypes = SampleTypeWrapper.getGlobalSampleTypes(
                appService, true);
        } catch (ApplicationException e) {
            logger.error("Can't reset global sample types", e);
        }
        siteSampleTypes = siteWrapper.getSampleTypeCollection(true);
        globalSampleWidget.setLists(globalSampleTypes, siteSampleTypes);
        siteSampleWidget.setLists(siteSampleTypes, globalSampleTypes);
    }
}
