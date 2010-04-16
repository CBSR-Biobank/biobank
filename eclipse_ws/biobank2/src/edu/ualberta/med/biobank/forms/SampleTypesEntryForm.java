package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.widgets.infotables.entry.SampleTypeEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SampleTypesEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SampleTypesEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleTypesEntryForm";
    public static final String OK_MESSAGE = "View and edit sample types.";

    private SiteWrapper mainSite;

    private SampleTypeEntryInfoTable globalSampleWidget;

    List<SampleTypeEntryInfoTable> siteWidgets = new ArrayList<SampleTypeEntryInfoTable>();

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() throws Exception {
        SiteAdapter siteAdapter = (SiteAdapter) adapter;
        mainSite = siteAdapter.getWrapper();
        if (SessionManager.getInstance().isAllSitesSelected()) {
            mainSite = null;
        }
        setPartName("Sample Types Entry");
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Sample Type Information");
        form.getBody().setLayout(new GridLayout(1, false));

        createGlobalSampleTypeSection();
        firstControl = globalSampleWidget;

        if (mainSite == null) {
            // show all sites
            try {
                for (SiteWrapper site : SiteWrapper.getSites(appService)) {
                    createSiteSampleTypeSection(site);
                }
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Problem creating sites widgets",
                    e);
            }
        } else {
            createSiteSampleTypeSection(mainSite);
        }
    }

    private void createSiteSampleTypeSection(SiteWrapper widgetSite) {
        Section section = createSection(widgetSite.getNameShort()
            + " only sample types");
        List<SampleTypeWrapper> siteSampleTypes = widgetSite
            .getSampleTypeCollection(true);
        if (siteSampleTypes == null) {
            siteSampleTypes = new ArrayList<SampleTypeWrapper>();
        }
        final SampleTypeEntryInfoTable siteSampleWidget = new SampleTypeEntryInfoTable(
            section, siteSampleTypes,
            "Add a new sample type to the repository site   ",
            "Edit the repository site's sample type", widgetSite);
        siteSampleWidget.adaptToToolkit(toolkit, true);
        siteSampleWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(siteSampleWidget);

        addSectionToolbar(section, "Add Site Sample Type",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    siteSampleWidget.addSampleType();
                }
            });
        section.setClient(siteSampleWidget);
        siteWidgets.add(siteSampleWidget);
    }

    private void createGlobalSampleTypeSection() throws Exception {
        Section section = createSection("Global sample types");
        List<SampleTypeWrapper> globalSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        if (globalSampleTypes == null) {
            globalSampleTypes = new ArrayList<SampleTypeWrapper>();
        }
        globalSampleWidget = new SampleTypeEntryInfoTable(section,
            globalSampleTypes, "Add a new global sample type",
            "Edit the global sample type", null);
        globalSampleWidget.adaptToToolkit(toolkit, true);
        globalSampleWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(globalSampleWidget);

        addSectionToolbar(section, "Add Global Sample Type",
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
        for (SampleTypeEntryInfoTable siteWidget : siteWidgets) {
            SiteWrapper currentSite = siteWidget.getCurrentSite();
            currentSite.reload();
            currentSite.addSampleTypes(siteWidget
                .getAddedOrModifiedSampleTypes());
            currentSite.removeSampleTypes(siteWidget.getDeletedSampleTypes());
            currentSite.persist();
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
        List<SampleTypeWrapper> globalSampleTypes = null;
        try {
            globalSampleTypes = SampleTypeWrapper.getGlobalSampleTypes(
                appService, true);
        } catch (ApplicationException e) {
            logger.error("Can't reset global sample types", e);
        }
        if (globalSampleTypes != null) {
            globalSampleWidget.setLists(globalSampleTypes);
        }

        for (SampleTypeEntryInfoTable siteWidget : siteWidgets) {
            siteWidget.reload();
        }
    }
}
