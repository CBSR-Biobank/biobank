package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
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
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StorageContainerAdapter;
import edu.ualberta.med.biobank.treeview.StorageTypeAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.IntegerNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.BiobankContentProvider;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class StorageContainerEntryForm extends BiobankEntryForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StorageContainerEntryForm";

    public static final String NEW_STORAGE_CONTAINER_OK_MESSAGE =
        "Creating a new storage container.";

    public static final String STORAGE_CONTAINER_OK_MESSAGE =
        "Editing an existing storage container.";
    
    public static final String NO_CONTAINER_NAME_MESSAGE =
        "Storage container must have a name";
    
    private StorageContainerAdapter storageContainerAdapter;
    
    private StorageContainer storageContainer;

    private Study study;
    
    private Site site;
    
    private Text tempWidget;
    
    private Label dimensionOneLabel;
    
    private Label dimensionTwoLabel;
    
    private Button submit;

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
            setPartName("Storage Container" + storageContainer.getId());
        }
    }

    @Override
    protected void createFormContent() {
        study = (Study) (
            (StudyAdapter) storageContainerAdapter.getParent().getParent()).getStudy(); 
        
        site = study.getSite();
        
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
            NonEmptyString.class, NO_CONTAINER_NAME_MESSAGE);   

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
        StorageTypeAdapter [] adapters = 
            new StorageTypeAdapter [storageTypes.size()];
        
        int count = 0;
        for (StorageType storageType : storageTypes) {
            adapters[count] = new StorageTypeAdapter(null, storageType);
            ++count;
        }
        
        toolkit.createLabel(client, "Container Type:", SWT.LEFT);
        
        ComboViewer viewer = new ComboViewer(client, SWT.READ_ONLY);
        viewer.setContentProvider(new BiobankContentProvider());
        viewer.setLabelProvider(new BiobankLabelProvider());
        viewer.setInput(adapters);
        
        Combo combo = viewer.getCombo();
        combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));  
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object selection = event.getSelection();
                StorageTypeAdapter adapter = 
                    (StorageTypeAdapter) ((StructuredSelection)selection).getFirstElement();
                final StorageType storageType = adapter.getStorageType();

                setDirty(true);
                
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                    public void run() {
                        final Object temp = storageType.getDefaultTemperature();                        
                        
                        final Object dim1Label = storageType.getDimensionOneLabel();
                        final Object dim2Label = storageType.getDimensionTwoLabel();

                        final Object dim1Max = storageType.getCapacity().getDimensionOneCapacity();
                        final Object dim2Max = storageType.getCapacity().getDimensionTwoCapacity();

                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {                
                                updateForm(temp, dim1Label, dim2Label, dim1Max, dim2Max);
                            }
                        });
                    }
                });
            }
        });  
        
        tempWidget = (Text) createBoundWidgetWithLabel(client, Text.class, 
            SWT.NONE, "Temperature (Celcius)", 
            null, PojoObservables.observeValue(storageContainer, "temperature"), 
            DoubleNumber.class, "Default temperature is not a valid number");
        
        createLocationSection(client);   
    }
    
    private void createLocationSection(Composite client) {  
        ContainerPosition position = storageContainer.getLocatedAtPosition();
        if (position == null) {
            position = new ContainerPosition();
        }
        
        dimensionOneLabel = toolkit.createLabel(client, "Position Dimension 1:", 
            SWT.LEFT);
        
        createBoundWidget(client, Text.class, SWT.NONE,  dimensionOneLabel,
            null, PojoObservables.observeValue(position, "positionDimensionOne"), 
            IntegerNumber.class, "Position is not a valid number");
        
        dimensionTwoLabel = toolkit.createLabel(client, "Position Dimension 2:", 
            SWT.LEFT);  
        
        createBoundWidget(client, Text.class, SWT.NONE,  dimensionTwoLabel,
            null, PojoObservables.observeValue(position, "positionDimensionTwo"), 
            DoubleNumber.class, "Position is not a valid number");
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
    
    private String getOkMessage() {
        if (storageContainer.getId() == null) {
            return NEW_STORAGE_CONTAINER_OK_MESSAGE;
        }
        return STORAGE_CONTAINER_OK_MESSAGE;
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
        
    private void updateForm(Object temp, Object dim1Label, Object dim2Label, 
        Object dim1Max, Object dim2Max) {
        String str = "";
        if (temp != null) {
            str = "" + (Double) temp;
        }
        tempWidget.setText(str);
        
        // handle dimension 1 
        str = "Position ";
        
        if (dim1Label != null) {
            str += "- " + (String) dim1Label; 
        }
        else {
            str += "Dimension 1 ";
        }
        
        if (dim1Max != null) {
            str += "\n(1 - " + (Integer) dim1Max + ")";
        }
        
        dimensionOneLabel.setText(str + ":");

        // handle dimension 2
        str = "Position ";
        
        if (dim1Label != null) {
            str += "- " + (String) dim2Label; 
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
    protected void saveForm() {
        // TODO Auto-generated method stub

    }

}
