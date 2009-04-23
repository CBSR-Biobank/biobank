package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StorageContainerAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;

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
        
        form.setText("BioBank Site Information");
        form.getBody().setLayout(new GridLayout(1, false));
        
        createContainerSection();

    }
    
    private void createContainerSection() {
        
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);  

        createBoundWidget(client, Text.class, SWT.NONE, "Name", null,
            PojoObservables.observeValue(storageContainer, "name"),
            NonEmptyString.class, NO_CONTAINER_NAME_MESSAGE);   

        createBoundWidget(client, Text.class, SWT.NONE, "Barcode", null,
            PojoObservables.observeValue(storageContainer, "barcode"),
            null, null);    
        
        createBoundWidget(client, Text.class, SWT.NONE, 
            "Default Temperature (Celcius)", 
            null, PojoObservables.observeValue(storageContainer, "temperature"), 
            DoubleNumber.class, "Default temperature is not a valid number"); 

        createBoundWidget(client, Combo.class, SWT.NONE, "Activity Status", 
            FormConstants.ACTIVITY_STATUS,
            PojoObservables.observeValue(storageContainer, "activityStatus"),
            null, null);  

        Text comment = (Text) createBoundWidget(client, Text.class, SWT.MULTI, 
            "Comments", null, 
            PojoObservables.observeValue(storageContainer, "comment"), 
            null, null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);        
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void saveForm() {
        // TODO Auto-generated method stub

    }

}
