package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.helpers.GetHelper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.SampleDerivativeType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StorageTypeAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.IntegerNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.MultiSelect;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StorageTypeEntryForm extends BiobankEditForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StorageTypeEntryForm";
    
    private static final String NEW_STORAGE_TYPE_OK_MESSAGE = 
        "Creating a new storage type.";
    
    private static final String STORAGE_TYPE_OK_MESSAGE = 
        "Editing an existing storage type.";
    
    private static final String NO_STORAGE_TYPE_NAME_MESSAGE = 
        "Storage type must have a name";
    
    static Logger log4j = Logger.getLogger(SessionManager.class.getName());
    
    private StorageTypeAdapter storageTypeAdapter;
    
    private StorageType storageType;
    
    private Capacity capacity;
    
    private Button submit;
    
    private MultiSelect samplesMultiSelect;
    
    private MultiSelect childStorageTypesMultiSelect;
    
    private List<SampleDerivativeType> allSampleDerivTypes;
    
    private Collection<StorageType> allStorageTypes;
    
    public StorageTypeEntryForm() {
        super();
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {        
        super.init(editorSite, input);
        
        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");
        
        storageTypeAdapter = (StorageTypeAdapter) node;
        appService = storageTypeAdapter.getAppService();
        storageType = storageTypeAdapter.getStorageType();       
        
        if (storageType.getId() == null) {
            setPartName("New Storage Type");
            capacity = new Capacity();
        }
        else {
            setPartName("Storage Type " + storageType.getName());
            capacity = storageType.getCapacity();
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Storage Type Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);        
        form.getBody().setLayout(new GridLayout(1, false));
        
        createStorageTypeSection();     
        createDimensionsSection();
        createSampleDerivTypesSection();
        createChildStorageTypesSection();
        createButtons();
    }
    
    protected void createStorageTypeSection() {        
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);  

        createBoundWidget(client, Text.class, SWT.NONE, "Name", null,
            PojoObservables.observeValue(storageType, "name"),
            NonEmptyString.class, NO_STORAGE_TYPE_NAME_MESSAGE);
        
        createBoundWidget(client, Text.class, SWT.NONE, 
            "Default Temperature (Celcius)", 
            null, PojoObservables.observeValue(storageType, "defaultTemperature"), 
            DoubleNumber.class, "Default temperature is not a valid number");

        createBoundWidget(client, Combo.class, SWT.NONE, "Activity Status", 
            FormConstants.ACTIVITY_STATUS,
            PojoObservables.observeValue(storageType, "activityStatus"),
            null, null);  

        Text comment = (Text) createBoundWidget(client, Text.class, SWT.MULTI,
            "Comments", null, 
            PojoObservables.observeValue(storageType, "comment"), null, null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);
    }
    
    private void createDimensionsSection() {
        Composite client = createSectionWithClient("Default Capacity");
        
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);  
        
        createBoundWidget(client, Text.class, SWT.NONE, "Dimension One Label", 
            null, PojoObservables.observeValue(storageType, "dimensionOneLabel"), 
            null, null); 
        
        createBoundWidget(client, Text.class, SWT.NONE, "Dimension One Capacity", 
            null, PojoObservables.observeValue(capacity, "dimensionOneCapacity"),
            IntegerNumber.class, "Dimension one capacity is not a valid number");
        
        createBoundWidget(client, Text.class, SWT.NONE, "Dimension Two Label", 
            null, PojoObservables.observeValue(storageType, "dimensionTwoLabel"), 
            null, null); 
        
        createBoundWidget(client, Text.class, SWT.NONE, "Dimension Two Capacity", 
            null, PojoObservables.observeValue(capacity, "dimensionTwoCapacity"), 
            IntegerNumber.class, "Dimension two capacity is not a valid nubmer");
    }
    
    private void createSampleDerivTypesSection() {
        Composite client = createSectionWithClient("Contains Sample Derivative Types");
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        
        Collection<SampleDerivativeType> stSamplesTypes = 
            storageType.getSampleDerivativeTypeCollection();
        
        GetHelper<SampleDerivativeType> helper = 
            new GetHelper<SampleDerivativeType>();
        
        allSampleDerivTypes = helper.getModelObjects(
            appService, SampleDerivativeType.class);
        
        samplesMultiSelect = new MultiSelect(client, SWT.NONE, 
                "Selected Sample Derivatives", "Available Sample Derivatives", 100);
        samplesMultiSelect.adaptToToolkit(toolkit);

        HashMap<Integer, String> availSampleDerivTypes = 
            new HashMap<Integer, String>();
        List<Integer> selSampleDerivTypes = new ArrayList<Integer>();

        if (stSamplesTypes != null) {
            for (SampleDerivativeType sampleType : stSamplesTypes) {
                selSampleDerivTypes.add(sampleType.getId());
            }
        }
        
        for (SampleDerivativeType sampleType : allSampleDerivTypes) {
            availSampleDerivTypes.put(sampleType.getId(), 
                    sampleType.getNameShort());
        }
        samplesMultiSelect.addSelections(availSampleDerivTypes, 
            selSampleDerivTypes);
    }
    
    private void createChildStorageTypesSection() {    
        Composite client = createSectionWithClient("Contains Storage Types");
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        
        Collection<StorageType> childStorageTypes = 
            storageType.getChildStorageTypeCollection();
        
        SiteAdapter siteAdapter = 
            (SiteAdapter) storageTypeAdapter.getParent().getParent();
        Site site = (Site) siteAdapter.getSite();
        allStorageTypes = site.getStorageTypeCollection();
        
        childStorageTypesMultiSelect = new MultiSelect(client, SWT.NONE, 
                "Selected Storage Types", "Available Storage Types", 100);
        childStorageTypesMultiSelect.adaptToToolkit(toolkit);

        HashMap<Integer, String> availStorageTypes = 
            new HashMap<Integer, String>();
        List<Integer> selChildStorageTypes = new ArrayList<Integer>();

        if (childStorageTypes != null) {
            for (StorageType childStorageType : childStorageTypes) {
                selChildStorageTypes.add(childStorageType.getId());
            }
        }
        
        int myId = 0;
        if (storageType.getId() != null) {
            myId = storageType.getId(); 
        }
        
        for (StorageType storageType : allStorageTypes) {
            int id = storageType.getId(); 
            if (myId != id) {
                availStorageTypes.put(id, storageType.getName());
            }
        }
        childStorageTypesMultiSelect.addSelections(availStorageTypes, 
            selChildStorageTypes);
    }
    
    protected void createButtons() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 10;
        layout.numColumns = 2;
        client.setLayout(layout);
        toolkit.paintBordersFor(client);

        submit = toolkit.createButton(client, "Submit", SWT.PUSH);
        submit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().saveEditor(StorageTypeEntryForm.this, false);
            }
        });
    }
    
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            submit.setEnabled(true);
        }
        else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            submit.setEnabled(false);
        }       
    }
    
    private String getOkMessage() {
        if (storageType.getId() == null) {
            return NEW_STORAGE_TYPE_OK_MESSAGE;
        }
        return STORAGE_TYPE_OK_MESSAGE;
    }

    /**
     * Called by base class when form data is to be saved.
     */
    @Override
    protected void saveForm() { 
        try {
            SDKQuery query;
            SDKQueryResult result;
            
            if ((storageType.getId() == null) && !checkStorageTypeNameUnique()) {
                setDirty(true);
                return;
            }
            
            saveSampleDerivativeTypes(); 
            saveChildStorageTypes();
            
            // associate the storage type to it's site
            Site site = (Site) ((SiteAdapter) 
                storageTypeAdapter.getParent().getParent()).getSite();
            Assert.isTrue((site != null) && (site.getId() != null) && (site.getId() != 0),
                "site is not in the database");
            storageType.setSite(site);

            if ((storageType.getId() == null) || (storageType.getId() == 0)) {
                Assert.isTrue(capacity.getId() == null, 
                    "insert invoked on capacity already in database");
                
                query = new InsertExampleQuery(capacity);                  
                result = appService.executeQuery(query);
                storageType.setCapacity((Capacity) result.getObjectResult());
                query = new InsertExampleQuery(storageType);   
            }
            else { 
                Assert.isNotNull(capacity.getId(), 
                    "update invoked on address not in database");

                query = new UpdateExampleQuery(capacity);                  
                result = appService.executeQuery(query);
                storageType.setCapacity((Capacity) result.getObjectResult());
                query = new UpdateExampleQuery(storageType);   
            }
            
            result = appService.executeQuery(query);
            storageType = (StorageType) result.getObjectResult();
        }
        catch (final RemoteAccessException exp) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                            "Connection Attempt Failed", 
                            "Could not perform database operation. Make sure server is running correct version.");
                }
            });
        }
        catch (Exception exp) {
            exp.printStackTrace(); 
        }
        
        storageTypeAdapter.getParent().performExpand();      
        getSite().getPage().closeEditor(this, false);    
    }
    
    private void saveSampleDerivativeTypes() {
        List<Integer> selSampleTypeIds = samplesMultiSelect.getSelected();
        Set<SampleDerivativeType> selSampleTypes = new HashSet<SampleDerivativeType>();
        for (SampleDerivativeType sampleType : allSampleDerivTypes) {
            int id = sampleType.getId();
            if (selSampleTypeIds.indexOf(id) >= 0) {
                selSampleTypes.add(sampleType);
            }
            
        }
        Assert.isTrue(selSampleTypes.size() == selSampleTypeIds.size(), 
                "problem with sample type selections");
        storageType.setSampleDerivativeTypeCollection(selSampleTypes);        
    }
    
    private void saveChildStorageTypes() {
        List<Integer> selStorageTypeIds = childStorageTypesMultiSelect.getSelected();
        Set<StorageType> selStorageTypes = new HashSet<StorageType>();
        for (StorageType storageType : allStorageTypes) {
            int id = storageType.getId();
            if (selStorageTypeIds.indexOf(id) >= 0) {
                selStorageTypes.add(storageType);
            }
            
        }
        Assert.isTrue(selStorageTypes.size() == selStorageTypeIds.size(), 
                "problem with sample type selections");
        storageType.setChildStorageTypeCollection(selStorageTypes);        
    }
    
    private boolean checkStorageTypeNameUnique() throws ApplicationException {
        WritableApplicationService appService = storageTypeAdapter.getAppService();
        Site site = (Site) ((SiteAdapter) 
            storageTypeAdapter.getParent().getParent()).getSite();
        
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.StorageType as st "
            + "inner join fetch st.site "
            + "where st.site.id='" + site.getId() + "' "
            + "and st.name = '" + storageType.getName() + "'");

        List<Object> results = appService.query(c);
        if (results.size() == 0) return true;
        
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                MessageDialog.openError(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                    "Site Name Problem", 
                    "A storage type with name \"" + storageType.getName() 
                    + "\" already exists.");
            }
        });
        return false;
    }
}
