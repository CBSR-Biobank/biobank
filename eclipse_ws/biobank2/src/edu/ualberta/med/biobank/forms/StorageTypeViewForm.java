package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.SampleDerivativeType;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StorageTypeAdapter;

public class StorageTypeViewForm extends BiobankViewForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StorageTypeViewForm";
    
    private StorageTypeAdapter storageTypeAdapter;
    
    private StorageType storageType;
    
    private Capacity capacity;
    
    public StorageTypeViewForm() {
        super();
    }

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
            throws PartInitException {        
        super.init(editorSite, input);
        
        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");
        
        if (node instanceof StorageTypeAdapter) {        
            storageTypeAdapter = (StorageTypeAdapter) node;
            appService = storageTypeAdapter.getAppService();
            storageType = storageTypeAdapter.getStorageType();
            capacity = storageType.getCapacity();       
            setPartName("Storage Type " + storageType.getName());
        }
        else {
            Assert.isTrue(false, "Invalid editor input: object of type "
                + node.getClass().getName());
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Storage Type: " + storageType.getName());
        
        form.getBody().setLayout(new GridLayout(1, false));
        createStorageTypeSection();     
        createDimensionsSection();
        createSampleDerivTypesSection();
        createChildStorageTypesSection();
        createButtons();
    }

    private void createStorageTypeSection() {   
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));    
        toolkit.paintBordersFor(client); 
        
        createBoundWidget(client, Label.class, SWT.NONE, "Name",
            PojoObservables.observeValue(storageType, "name"));
        
        createBoundWidget(client, Label.class, SWT.NONE, "Activity Status",
            PojoObservables.observeValue(storageType, "activityStatus"));
        
        createBoundWidget(client, Label.class, SWT.NONE, "Comments", 
            PojoObservables.observeValue(storageType, "comment"));
    }

    private void createDimensionsSection() {
        Composite client = createSectionWithClient("Default Capacity");        
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        createBoundWidget(client, Text.class, SWT.NONE, "Dimension One Label", 
            PojoObservables.observeValue(storageType, "dimensionOneLabel")); 
        
        createBoundWidget(client, Text.class, SWT.NONE, "Dimension One Capacity", 
            PojoObservables.observeValue(capacity, "dimensionOneCapacity"));
        
        createBoundWidget(client, Text.class, SWT.NONE, "Dimension Two Label", 
            PojoObservables.observeValue(storageType, "dimensionTwoLabel")); 
        
        createBoundWidget(client, Text.class, SWT.NONE, "Dimension Two Capacity", 
            PojoObservables.observeValue(capacity, "dimensionTwoCapacity"));
    }

    private void createSampleDerivTypesSection() {
        Composite client = createSectionWithClient("Contains Sample Derivatives");       
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client); 
        
        Label label = toolkit.createLabel(client, "Sample derivative types:");      
        label.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
        
        
        List list = new List(client, SWT.BORDER | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        list.setLayoutData(gd);
        for (SampleDerivativeType type : storageType.getSampleDerivativeTypeCollection()) {
            list.add(type.getNameShort());
        }
    }

    private void createChildStorageTypesSection() {
        Composite client = createSectionWithClient("Contains Storage Types");       
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client); 
        
        Label label = toolkit.createLabel(client, "Storage types:");      
        label.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
        
        
        List list = new List(client, SWT.BORDER | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        list.setLayoutData(gd);
        for (StorageType type : storageType.getChildStorageTypeCollection()) {
            list.add(type.getName());
        }
    }

    private void createButtons() {        
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(4, false));
        toolkit.paintBordersFor(client);

        final Button edit = toolkit.createButton(
            client, "Edit this information", SWT.PUSH);
        edit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                getSite().getPage().closeEditor(StorageTypeViewForm.this, false);
                try {
                    getSite().getPage().openEditor(
                        new FormInput(storageTypeAdapter), 
                        StorageTypeEntryForm.ID, true);
                }
                catch (PartInitException exp) {
                    exp.printStackTrace();              
                }
            }
        });   
    }
}
