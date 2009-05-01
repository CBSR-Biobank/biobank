package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StorageContainerAdapter;
import edu.ualberta.med.biobank.treeview.StorageContainerGroup;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.IntegerNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.BiobankContentProvider;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

public class StorageContainerEntryForm extends BiobankEntryForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StorageContainerEntryForm";

    public static final String MSG_STORAGE_CONTAINER_NEW_OK =
        "Creating a new storage container.";

    public static final String MSG_STORAGE_CONTAINER_OK =
        "Editing an existing storage container.";
    
    public static final String MSG_CONTAINER_NAME_EMPTY =
        "Storage container must have a name";
    
    public static final String MSG_STORAGE_TYPE_EMPTY =
        "Storage container must have a container type";

    public static final String MSG_INVALID_POSITION =
        "Position is empty or not a valid number";
    
    private StorageContainerAdapter storageContainerAdapter;
    
    private StorageContainer storageContainer;
    
    private ContainerPosition position;

    private Study study;
    
    private Site site;
    
    private Text tempWidget;
    
    private Label dimensionOneLabel;
    
    private Label dimensionTwoLabel;
    
    private Button submit;
    
    private StorageType currentStorageType;
    
    private ComboViewer storageTypeComboViewer;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {
        super.init(editorSite, input);

        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");

        storageContainerAdapter = (StorageContainerAdapter) node;
        appService = storageContainerAdapter.getAppService();
        storageContainer = storageContainerAdapter.getStorageContainer();

        if (storageContainer.getId() == null) {
            setPartName("Storage Container");
        }
        else {
            setPartName("Storage Container " + storageContainer.getName());
        }
    }

    @Override
    protected void createFormContent() {
        study = (Study) (
            (StudyAdapter) storageContainerAdapter.getParent().getParent()).getStudy();
        site = study.getSite();
        currentStorageType = storageContainer.getStorageType();
        
        form.setText("Storage Container");
        form.getBody().setLayout(new GridLayout(1, false));
        
        createContainerSection();
        createButtonsSection();
    }
    
    private void createContainerSection() {        
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);  

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Name", null,
            PojoObservables.observeValue(storageContainer, "name"),
            NonEmptyString.class, MSG_CONTAINER_NAME_EMPTY);   

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Barcode", null,
            PojoObservables.observeValue(storageContainer, "barcode"),
            null, null);  

        createBoundWidgetWithLabel(client, Combo.class, SWT.NONE, "Activity Status", 
            FormConstants.ACTIVITY_STATUS,
            PojoObservables.observeValue(storageContainer, "activityStatus"),
            null, null);  

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class, SWT.MULTI, 
            "Comments", null, 
            PojoObservables.observeValue(storageContainer, "comment"), 
            null, null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);     
        
        createStorageTypesSection(client);
    }
    
    private void createStorageTypesSection(Composite client) {
        Collection<StorageType> storageTypes = 
            site.getStorageTypeCollection();
        StorageType[] arr = new StorageType[storageTypes.size()];
        int count = 0;
        for (StorageType st : storageTypes) {
            arr[count] = st;
            if ((currentStorageType != null) 
                && currentStorageType.getId().equals(st.getId())) {
                currentStorageType = st;
            }
            count++;
        }
        
        Label storageTypeLabel = 
            toolkit.createLabel(client, "Container Type:", SWT.LEFT);        
        
        storageTypeComboViewer = new ComboViewer(client, SWT.READ_ONLY);
        storageTypeComboViewer.setContentProvider(new BiobankContentProvider());
        storageTypeComboViewer.setLabelProvider(new BiobankLabelProvider());
        storageTypeComboViewer.setInput(arr);
        if (currentStorageType != null) {
            storageTypeComboViewer.setSelection(new StructuredSelection(currentStorageType));
        }        
        
        Combo combo = storageTypeComboViewer.getCombo();
        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));  
        storageTypeComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                setDirty(true);
                
                Object selection = event.getSelection();
                final StorageType storageType = 
                    (StorageType) ((StructuredSelection)selection).getFirstElement();
                
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                    public void run() {
                        final Double temp = storageType.getDefaultTemperature();                        
                        
                        final String dim1Label = storageType.getDimensionOneLabel();
                        final String dim2Label = storageType.getDimensionTwoLabel();

                        final Integer dim1Max = storageType.getCapacity().getDimensionOneCapacity();
                        final Integer dim2Max = storageType.getCapacity().getDimensionTwoCapacity();

                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {                
                                updateForm(temp, dim1Label, dim2Label, dim1Max, dim2Max);
                            }
                        });
                    }
                });
            }
        });  
        bindStorageTypeCombo(storageTypeLabel, combo);
        
        tempWidget = (Text) createBoundWidgetWithLabel(client, Text.class, 
            SWT.NONE, "Temperature (Celcius)", 
            null, PojoObservables.observeValue(storageContainer, "temperature"), 
            DoubleNumber.class, "Default temperature is not a valid number");
        
        createLocationSection();   
    }
    
    private void createLocationSection() {
        String dim1Label = null, dim2Label = null;
        Integer dim1Max = null, dim2Max = null;
        
        Composite client = createSectionWithClient("Location");
        position = storageContainer.getLocatedAtPosition();
        if (position == null) {
            position = new ContainerPosition();
        }
        
        if (currentStorageType != null) {
            dim1Label = currentStorageType.getDimensionOneLabel();
            dim2Label = currentStorageType.getDimensionTwoLabel();
        }
        
        // could be that the dimension labels are not assigned in the 
        // database objects
        if (dim1Label == null) {
            dim1Label = "Dimension 1";
            dim2Label = "Dimension 2";
        }

        if (currentStorageType != null) { 
            Capacity capacity = currentStorageType.getCapacity(); 
            if (capacity != null) {
                dim1Max = capacity.getDimensionOneCapacity();
                dim2Max = capacity.getDimensionTwoCapacity();
                if (dim1Max != null) {
                    dim1Label += "\n(1 - " + dim1Max + ")";
                }
                if (dim2Max != null) {
                    dim2Label += "\n(1 - " + dim2Max + ")";
                }
            }
        }
        
        dimensionOneLabel = toolkit.createLabel(client, dim1Label + ":", SWT.LEFT);        
        
        IntegerNumber validator = new IntegerNumber(MSG_INVALID_POSITION,
            FormUtils.createDecorator(dimensionOneLabel, MSG_INVALID_POSITION),
            false);
        
        createBoundWidget(client, Text.class, SWT.NONE,  dimensionOneLabel,
            null, PojoObservables.observeValue(position, "positionDimensionOne"), 
            validator);
        
        dimensionTwoLabel = toolkit.createLabel(client, dim2Label + ":", SWT.LEFT);  
        
        validator = new IntegerNumber(MSG_INVALID_POSITION,
            FormUtils.createDecorator(dimensionTwoLabel, MSG_INVALID_POSITION),
            false);
        
        createBoundWidget(client, Text.class, SWT.NONE,  dimensionTwoLabel,
            null, PojoObservables.observeValue(position, "positionDimensionTwo"), 
            validator);
    }
    
    private void createButtonsSection() {
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
                    .getActivePage().saveEditor(StorageContainerEntryForm.this, false);
            }
        });
    }
    
    private void bindStorageTypeCombo(Label label, Combo combo) {
        IValidator validator = createValidator(NonEmptyString.class, 
            FormUtils.createDecorator(label, MSG_STORAGE_TYPE_EMPTY), 
            MSG_STORAGE_TYPE_EMPTY);
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(validator);
            
        dbc.bindValue(SWTObservables.observeSelection(combo),
            PojoObservables.observeValue(storageContainer, "barcode"),
            uvs, null);
    }
    
    private String getOkMessage() {
        if (storageContainer.getId() == null) {
            return MSG_STORAGE_CONTAINER_NEW_OK;
        }
        return MSG_STORAGE_CONTAINER_OK;
    }

    @Override
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
        
    /*
     * Parameters need to be passed as objects since they could be set to NULL 
     * in the database.
     * 
     */
    private void updateForm(Double temp, String dim1Label, String dim2Label, 
        Integer dim1Max, Integer dim2Max) {
        String str = "";
        if (temp != null) {
            str = "" + (Double) temp;
        }
        tempWidget.setText(str);
        
        // handle dimension 1 
        str = "";
        
        if (dim1Label != null) {
            str += dim1Label; 
        }
        else {
            str += "Dimension 1 ";
        }
        
        if (dim1Max != null) {
            str += "\n(1 - " + (Integer) dim1Max + ")";
        }
        
        dimensionOneLabel.setText(str + ":");

        // handle dimension 2
        str = "";
        
        if (dim1Label != null) {
            str += dim2Label; 
        }
        else {
            str += "Dimension 2 ";
        }
        
        if (dim1Max != null) {
            str += "\n(1 - " + (Integer) dim2Max + ")";
        }
        
        dimensionTwoLabel.setText(str + ":");
        form.reflow(true);
    }

    @Override
    protected void saveForm() throws Exception {
        SDKQuery query;
        SDKQueryResult result;
        
        StorageType storageType = 
            (StorageType) (
                (StructuredSelection)
                storageTypeComboViewer.getSelection()).getFirstElement();
        storageContainer.setStorageType(storageType);
        storageContainer.setLocatedAtPosition(position);
        storageContainer.setCapacity(storageType.getCapacity());
        storageContainer.setStudy(study);

        if (storageContainer.getId() == null) {
            query = new InsertExampleQuery(storageContainer);
        }
        else { 
            query = new UpdateExampleQuery(storageContainer);
        }

        result = appService.executeQuery(query);
        storageContainer = (StorageContainer) result.getObjectResult();   

        ((StorageContainerGroup) storageContainerAdapter.getParent()).performExpand();       
        getSite().getPage().closeEditor(this, false);  

    }

}
