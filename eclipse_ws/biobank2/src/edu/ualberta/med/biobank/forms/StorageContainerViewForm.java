package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StorageContainerAdapter;

public class StorageContainerViewForm extends BiobankViewForm {
    
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StorageContainerViewForm";
    
    private StorageContainerAdapter storageContainerAdapter;
    
    private StorageContainer storageContainer;
    
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
        form.setText("Storage Container " + storageContainer.getName());
        form.getBody().setLayout(new GridLayout(1, false));
        
        createContainerSection();
    }
    
    private void createContainerSection() {        
        StorageType storageType = storageContainer.getStorageType();        
        ContainerPosition position = storageContainer.getLocatedAtPosition();
        
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);  
        
        createBoundWidget(client, Label.class, SWT.NONE, "Name",
            PojoObservables.observeValue(storageContainer, "name"));
            
        createBoundWidget(client, Label.class, SWT.NONE, "Bar Code",
            PojoObservables.observeValue(storageContainer, "barcode"));
        
        createBoundWidget(client, Label.class, SWT.NONE, "Activity Status",
            PojoObservables.observeValue(storageContainer, "activityStatus"));
        
        createBoundWidget(client, Label.class, SWT.NONE, "Comments", 
            PojoObservables.observeValue(storageContainer, "comment"));
        
        createBoundWidget(client, Label.class, SWT.NONE, "Storage Type", 
            PojoObservables.observeValue(storageType, "name"));
        
        createBoundWidget(client, Label.class, SWT.NONE, "Temperature", 
            PojoObservables.observeValue(storageContainer, "temperature"));
        
        String str = storageType.getDimensionOneLabel();
        if (str == null) {
            str = "Position Dimension 1";
        }
        
        createBoundWidget(client, Label.class, SWT.NONE, 
            str,  PojoObservables.observeValue(position, "positionDimensionOne"));
        
        str = storageType.getDimensionTwoLabel();
        if (str == null) {
            str = "Position Dimension 2";
        }
        
        createBoundWidget(client, Label.class, SWT.NONE, 
            str, PojoObservables.observeValue(position, "positionDimensionTwo"));
        
    }

	@Override
	protected void reload() {
		// TODO Auto-generated method stub
		
	}

}
