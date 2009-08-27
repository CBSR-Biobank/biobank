package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.widgets.SampleTypeEntryWidget;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SampleTypesEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.SampleTypesEntryForm";
    public static final String OK_MESSAGE = "View and edit sample types.";

    private Collection<SampleType> globalSampleTypes;
    private Collection<SampleType> siteSampleTypes;
    private SiteAdapter siteAdapter;
    private SampleTypeEntryWidget siteSampleWidget;
    private SampleTypeEntryWidget globalSampleWidget;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() {
        siteAdapter = (SiteAdapter) adapter;
        globalSampleTypes = getGlobalSampleTypes();
        siteSampleTypes = siteAdapter.getSite().getSampleTypeCollection();
        setPartName("Sample Types");
    }

    private List<SampleType> getGlobalSampleTypes() {
        List<SampleType> results = new ArrayList<SampleType>();
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.SampleType "
                + "where site = null");
        try {
            results = appService.query(c);
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return results;
    }

    @Override
    protected void createFormContent() {
        form.setText("Sample Type Information");
        form.getBody().setLayout(new GridLayout(1, false));
        createSampleTypeSection();
    }

    @Override
    public void saveForm() throws Exception {
        saveLocalTypes();
        saveGlobalTypes();
    }

    private void saveLocalTypes() throws Exception {
        Collection<SampleType> ssCollection = siteSampleWidget
            .getTableSampleTypes();
        SDKQuery query;
        SDKQueryResult result;

        removeDeletedSampleStorage(ssCollection, siteSampleTypes);

        Collection<SampleType> savedSsCollection = new HashSet<SampleType>();
        for (SampleType ss : ssCollection) {
            ss.setSite(siteAdapter.getSite());
            if ((ss.getId() == null) || (ss.getId() == 0)) {
                query = new InsertExampleQuery(ss);
            } else {
                query = new UpdateExampleQuery(ss);
            }

            result = appService.executeQuery(query);
            savedSsCollection.add((SampleType) result.getObjectResult());
        }
        siteAdapter.getSite().setSampleTypeCollection(savedSsCollection);
    }

    private void saveGlobalTypes() throws Exception {
        Collection<SampleType> ssCollection = globalSampleWidget
            .getTableSampleTypes();
        SDKQuery query;
        SDKQueryResult result;

        removeDeletedSampleStorage(ssCollection, globalSampleTypes);

        Collection<SampleType> savedSsCollection = new HashSet<SampleType>();
        for (SampleType ss : ssCollection) {

            if ((ss.getId() == null) || (ss.getId() == 0)) {
                query = new InsertExampleQuery(ss);
            } else {
                query = new UpdateExampleQuery(ss);
            }

            result = appService.executeQuery(query);
            savedSsCollection.add((SampleType) result.getObjectResult());
        }

    }

    private void removeDeletedSampleStorage(Collection<SampleType> newTypes,
        Collection<SampleType> oldTypes) {
        // no need to remove if study is not yet in the database

        if (siteAdapter.getSite().getId() == null)
            return;

        List<Integer> selectedSampleTypeIds = new ArrayList<Integer>();
        for (SampleType ss : newTypes) {
            selectedSampleTypeIds.add(ss.getId());
        }

        SDKQuery query;

        try {
            // query from database again

            for (SampleType ss : oldTypes) {
                if (!selectedSampleTypeIds.contains(ss.getId())) {
                    query = new DeleteExampleQuery(ss);
                    appService.executeQuery(query);
                }
            }
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSampleTypeSection() {
        Composite client = createSectionWithClient("Sample Types");
        GridLayout layout = new GridLayout(1, true);
        client.setLayout(layout);

        siteSampleWidget = new SampleTypeEntryWidget(client, SWT.NONE,
            new HashSet<SampleType>(siteSampleTypes), globalSampleTypes,
            toolkit);
        siteSampleWidget.adaptToToolkit(toolkit, true);
        siteSampleWidget.addSelectionChangedListener(listener);

        toolkit.paintBordersFor(siteSampleWidget);

        globalSampleWidget = new SampleTypeEntryWidget(client, SWT.NONE,
            new HashSet<SampleType>(globalSampleTypes), siteSampleTypes,
            toolkit);
        globalSampleWidget.adaptToToolkit(toolkit, true);
        globalSampleWidget.addSelectionChangedListener(listener);

        initCancelConfirmWidget(client);
    }

    @Override
    public String getNextOpenedFormID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getOkMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void cancelForm() {
        // TODO Auto-generated method stub

    }

}
