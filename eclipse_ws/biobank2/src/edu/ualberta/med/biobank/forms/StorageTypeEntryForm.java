package edu.ualberta.med.biobank.forms;
import java.util.HashMap;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StorageTypeAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;

@SuppressWarnings("serial")
public class StorageTypeEntryForm extends BiobankEditForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StorageTypeEntryForm";
    
    private static final String NEW_STORAGE_TYPE_OK_MESSAGE = 
        "Creating a new storage type.";
    
    private static final String STORAGE_TYPE_OK_MESSAGE = 
        "Editing an existing study.";

    public static final String[] ORDERED_FIELDS = new String[] {
        "name",
        "defaultTemperature",
        "dimensionOneLabel",
        "dimensionTwoLabel",
        "activityStatus",
        "comment",
    };
    
    public static final HashMap<String, FieldInfo> FIELDS = 
        new HashMap<String, FieldInfo>() {{
            put("name", new FieldInfo("Name", Text.class,  
                    NonEmptyString.class, "Study name cannot be blank"));
            put("defaultTemperature", new FieldInfo("Default Temperature", Text.class, 
                    null,  null));
            put("dimensionOneLabel", new FieldInfo("Dimension One Label", Text.class,  
                    NonEmptyString.class, "Dimension one label cannot be blank"));
            put("dimensionTwoLabel", new FieldInfo("Dimension Two Label", Text.class,  
                    NonEmptyString.class, "Dimension two label cannot be blank"));
            put("activityStatus", new FieldInfo("Activity Status", Combo.class, 
                    null,  null));
            put("comment", new FieldInfo("Comment", Text.class,  
                    null, null));
        }
    };

    public static final String[] CAPACITY_ORDERED_FIELDS = new String[] {
        "dimensionOneCapacity",
        "dimensionTwoCapacity",
    };
    
    public static final HashMap<String, FieldInfo> CAPACITY_FIELDS = 
        new HashMap<String, FieldInfo>() {{
            put("dimensionOneCapacity", new FieldInfo("Dimension One Capacity", Text.class,  
                    null, null));
            put("dimensionTwoCapacity", new FieldInfo("Dimension Two Capacity", Text.class,  
                    null, null));
        }
    };
    
    private StorageTypeAdapter storageTypeAdapter;
    
    private StorageType storageType;
    
    private Capacity capacity;
    
    private Site site;
    
    private Button submit;
    
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
        bindValues();
    }
    
    protected void createStorageTypeSection() {        
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        // TODO: set items to valid activity status'es
        createWidgetsFromHashMap(FIELDS, ORDERED_FIELDS, storageType, client);
        createWidgetsFromHashMap(CAPACITY_FIELDS, CAPACITY_ORDERED_FIELDS, 
                capacity, client);
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
    
    private void bindValues() {
        DataBindingContext dbc = new DataBindingContext();
        bindValuesFromHashMap(dbc, FIELDS, storageType);
        bindValuesFromHashMap(dbc, CAPACITY_FIELDS, capacity);
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

    @Override
    protected void saveForm() {
        // TODO Auto-generated method stub
    }

}
