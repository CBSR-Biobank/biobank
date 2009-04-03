package edu.ualberta.med.biobank.forms;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
import org.eclipse.ui.forms.widgets.Section;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.SampleDerivativeType;
import edu.ualberta.med.biobank.model.SdataType;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StorageTypeAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.IntegerNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.MultiSelect;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

public class StorageTypeEntryForm extends BiobankEditForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StorageTypeEntryForm";
    
    private static final String NEW_STORAGE_TYPE_OK_MESSAGE = 
        "Creating a new storage type.";
    
    private static final String STORAGE_TYPE_OK_MESSAGE = 
        "Editing an existing storage type.";
    
    private static final String NO_STORAGE_TYPE_NAME_MESSAGE = 
        "Storage type must have a name";
    
    private StorageTypeAdapter storageTypeAdapter;
    
    private StorageType storageType;
    
    private Capacity capacity;
    
    //private Site site;
    
    private Button submit;
    
    private MultiSelect samplesMultiSelect;
    
    private Collection<SampleDerivativeType> allSampleDerivTypes;
    
    public StorageTypeEntryForm() {
        super();
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {
        if ( !(input instanceof FormInput)) 
            throw new PartInitException("Invalid editor input");
        
        super.init(editorSite, input);
        
        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");
        
        storageTypeAdapter = (StorageTypeAdapter) node;
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
        Section section = toolkit.createSection(form.getBody(), 
            Section.TITLE_BAR | Section.EXPANDED);
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        section.setText("Default Capacity");
        
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));     
        Composite client = toolkit.createComposite(section);
        section.setClient(client);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
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
        storageType.getHoldsStorageTypeCollection();
        Collection<SampleDerivativeType> stSamplesTypes = 
            storageType.getSampleDerivativeTypeCollection();
        
        allSampleDerivTypes = getAllSampleDerivativeTypes();
        
        Section section = toolkit.createSection(form.getBody(), 
                Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Contains Sample Derivative Types");
        
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));     
        Composite client = toolkit.createComposite(section);
        section.setClient(client);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);  
        
        samplesMultiSelect = new MultiSelect(client, SWT.NONE, 
                "Selected Sample Derivatives", "Available Sample Derivatives", 100);
        samplesMultiSelect.adaptToToolkit(toolkit);

        HashMap<Integer, String> availSampleDerivTypes = 
            new HashMap<Integer, String>();
        HashMap<Integer, String> selSampleDerivTypes = 
            new HashMap<Integer, String>();

        if (stSamplesTypes != null) {
            for (SampleDerivativeType sampleType : stSamplesTypes) {
                selSampleDerivTypes.put(sampleType.getId(), 
                    sampleType.getNameShort());
            }
            samplesMultiSelect.addSelected(selSampleDerivTypes);
        }
        
        for (SampleDerivativeType sampleType : allSampleDerivTypes) {
            if (selSampleDerivTypes.get(sampleType.getId()) == null) {
                availSampleDerivTypes.put(sampleType.getId(), 
                    sampleType.getNameShort());
            }
        }
        samplesMultiSelect.addAvailable(availSampleDerivTypes);
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

            if ((storageType.getId() == null) || (storageType.getId() == 0)) {
                Assert.isTrue(capacity.getId() == null, 
                    "insert invoked on capacity already in database");
                
                query = new InsertExampleQuery(capacity);                  
                result = storageTypeAdapter.getAppService().executeQuery(query);
                storageType.setCapacity((Capacity) result.getObjectResult());
                query = new InsertExampleQuery(storageType);   
            }
            else { 
                Assert.isNotNull(capacity.getId(), 
                    "update invoked on address not in database");

                query = new UpdateExampleQuery(capacity);                  
                result = storageTypeAdapter.getAppService().executeQuery(query);
                storageType.setCapacity((Capacity) result.getObjectResult());
                query = new UpdateExampleQuery(storageType);   
            }
            
            result = storageTypeAdapter.getAppService().executeQuery(query);
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
        
        SessionManager.getInstance().updateStorageTypes(storageTypeAdapter);      
        getSite().getPage().closeEditor(this, false);    
    }
    
    private List<SampleDerivativeType> getAllSampleDerivativeTypes() {        
        SampleDerivativeType sample = new SampleDerivativeType();

        try {
            return storageTypeAdapter.getAppService().search(
                SampleDerivativeType.class, sample);
        }
        catch (final RemoteConnectFailureException exp) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(),
                        "Connection Attempt Failed",
                        "Could not connect to server. Make sure server is running.");
                }
            });
        }
        catch (Exception exp) {
            exp.printStackTrace();
        }
        return null;
    }
}
