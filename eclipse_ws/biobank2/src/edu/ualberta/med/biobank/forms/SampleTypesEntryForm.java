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
        if (globalSampleTypes == null) {
            globalSampleTypes = new ArrayList<SampleTypeWrapper>();
        }
        siteSampleTypes = siteWrapper.getSampleTypeCollection(true);
        if (siteSampleTypes == null) {
            siteSampleTypes = new ArrayList<SampleTypeWrapper>();
        }
        setPartName("Sample Types Entry");
    }

    @Override
    protected void createFormContent() {
        form.setText("Sample Type Information");
        form.getBody().setLayout(new GridLayout(1, false));
        boolean allSiteSelected = SessionManager.getInstance()
            .isAllSitesSelected();
        if (!allSiteSelected && siteWrapper.canEdit()) {
            createSiteSampleTypeSection();
            firstControl = siteSampleWidget;
        }
        createGlobalSampleTypeSection();
        if (firstControl == null) {
            firstControl = globalSampleWidget;
        }
    }

    private void createSiteSampleTypeSection() {
        Section section = createSection(siteWrapper.getNameShort()
            + " only sample types");
        siteSampleWidget = new SampleTypeEntryInfoTable(section,
            siteSampleTypes, globalSampleTypes,
            "Add a new sample type to the repository site   ",
            "Edit the repository site's sample type");
        siteSampleWidget.adaptToToolkit(toolkit, true);
        siteSampleWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(siteSampleWidget);

        addSectionToolbar(section, "Add Site Sample Type",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    siteSampleWidget.addSampleType();
                }
            }, SampleTypeWrapper.class);
        section.setClient(siteSampleWidget);
    }

    private void createGlobalSampleTypeSection() {
        Section section = createSection("Global sample types");
        globalSampleWidget = new SampleTypeEntryInfoTable(section,
            globalSampleTypes, siteSampleTypes, "Add a new global sample type",
            "Edit the global sample type");
        globalSampleWidget.adaptToToolkit(toolkit, true);
        globalSampleWidget.addSelectionChangedListener(listener);
        toolkit.paintBordersFor(globalSampleWidget);

        addSectionToolbar(section, "Add Global Sample Type",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    globalSampleWidget.addSampleType();
                }
            }, SampleTypeWrapper.class);
        section.setClient(globalSampleWidget);
    }

    @Override
    public void saveForm() throws BiobankCheckException, Exception {
        if (!SessionManager.getInstance().isAllSitesSelected()) {
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

    @Override
    protected void checkEditAccess() {
        if (!SessionManager.canUpdate(SampleTypeWrapper.class)
            && !SessionManager.canCreate(SampleTypeWrapper.class)
            && !SessionManager.canDelete(SampleTypeWrapper.class)) {
            BioBankPlugin.openAccessDeniedErrorMessage();
            throw new RuntimeException(
                "Cannot access Sample Type editor. Access Denied.");
        }
    }
}

// package edu.ualberta.med.biobank.forms;
//
// import java.util.ArrayList;
// import java.util.List;
//
// import org.apache.log4j.Logger;
// import org.eclipse.swt.SWT;
// import org.eclipse.swt.layout.GridLayout;
// import org.eclipse.swt.widgets.Composite;
//
// import edu.ualberta.med.biobank.BioBankPlugin;
// import edu.ualberta.med.biobank.SessionManager;
// import edu.ualberta.med.biobank.common.BiobankCheckException;
// import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
// import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
// import edu.ualberta.med.biobank.treeview.SiteAdapter;
// import edu.ualberta.med.biobank.widgets.SampleTypeEntryWidget;
// import
// edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
// import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
// import gov.nih.nci.system.applicationservice.ApplicationException;
//
// public class SampleTypesEntryForm extends BiobankEntryForm {
//
// private static Logger LOGGER = Logger.getLogger(SampleTypesEntryForm.class
// .getName());
//
// public static final String ID =
// "edu.ualberta.med.biobank.forms.SampleTypesEntryForm";
// public static final String OK_MESSAGE = "View and edit sample types.";
//
// private SiteWrapper siteWrapper;
// private List<SampleTypeWrapper> globalSampleTypes;
// private List<SampleTypeWrapper> siteSampleTypes;
// private SampleTypeEntryWidget siteSampleWidget;
// private SampleTypeEntryWidget globalSampleWidget;
//
// private BiobankEntryFormWidgetListener listener = new
// BiobankEntryFormWidgetListener() {
// @Override
// public void selectionChanged(MultiSelectEvent event) {
// setDirty(true);
// }
// };
//
// @Override
// public void init() throws Exception {
// if (!SessionManager.getInstance().isAllSitesSelected()) {
// siteWrapper = ((SiteAdapter) adapter).getWrapper();
// siteSampleTypes = siteWrapper.getSampleTypeCollection(true);
// } else {
// siteSampleTypes = new ArrayList<SampleTypeWrapper>();
// }
// globalSampleTypes = SampleTypeWrapper.getGlobalSampleTypes(appService,
// true);
// setPartName("Sample Types Entry");
// }
//
// @Override
// protected void createFormContent() {
// form.setText("Sample Type Information");
// form.getBody().setLayout(new GridLayout(1, false));
// if (siteWrapper != null && siteWrapper.canEdit()) {
// createSiteSampleTypeSection();
// firstControl = siteSampleWidget;
// }
// createGlobalSampleTypeSection();
// if (firstControl == null) {
// firstControl = globalSampleWidget;
// }
// }
//
// private void createSiteSampleTypeSection() {
// Composite client = createSectionWithClient("Site Sample Types");
// GridLayout layout = new GridLayout(1, true);
// client.setLayout(layout);
//
// siteSampleWidget = new SampleTypeEntryWidget(client, SWT.NONE,
// siteSampleTypes, globalSampleTypes, "Add Site Sample Type", toolkit);
// siteSampleWidget.adaptToToolkit(toolkit, true);
// siteSampleWidget.addSelectionChangedListener(listener);
// toolkit.paintBordersFor(siteSampleWidget);
// }
//
// private void createGlobalSampleTypeSection() {
// Composite client = createSectionWithClient("Global Sample Types");
// GridLayout layout = new GridLayout(1, true);
// client.setLayout(layout);
//
// globalSampleWidget = new SampleTypeEntryWidget(client, SWT.NONE,
// globalSampleTypes, siteSampleTypes, "Add Global Sample Type",
// toolkit);
// globalSampleWidget.adaptToToolkit(toolkit, true);
// globalSampleWidget.addSelectionChangedListener(listener);
// toolkit.paintBordersFor(globalSampleWidget);
// }
//
// @Override
// public void saveForm() throws BiobankCheckException, Exception {
// if (siteWrapper != null) {
// siteWrapper.reload();
// List<SampleTypeWrapper> ssCollection = siteSampleWidget
// .getTableSampleTypes();
// siteWrapper.setSampleTypeCollection(ssCollection);
// siteWrapper.persist();
// }
// SampleTypeWrapper.persistGlobalSampleTypes(appService,
// globalSampleWidget.getTableSampleTypes());
// }
//
// @Override
// public String getNextOpenedFormID() {
// return null;
// }
//
// @Override
// protected String getOkMessage() {
// return null;
// }
//
// @Override
// public void reset() throws Exception {
// super.reset();
// try {
// globalSampleTypes = SampleTypeWrapper.getGlobalSampleTypes(
// appService, true);
// globalSampleWidget.setLists(globalSampleTypes, siteSampleTypes);
// } catch (ApplicationException e) {
// LOGGER.error("Can't reset global sample types", e);
// }
// if (siteWrapper != null) {
// siteSampleTypes = siteWrapper.getSampleTypeCollection(true);
// siteSampleWidget.setLists(siteSampleTypes, globalSampleTypes);
// }
// }
//
// @Override
// protected void checkEditAccess() {
// if (!SessionManager.canUpdate(SampleTypeWrapper.class)
// && !SessionManager.canCreate(SampleTypeWrapper.class)
// && !SessionManager.canDelete(SampleTypeWrapper.class)) {
// BioBankPlugin.openAccessDeniedErrorMessage();
// throw new RuntimeException(
// "Cannot access Sample Type editor. Access Denied.");
// }
// }
// }
