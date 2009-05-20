package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Capacity;
//import edu.ualberta.med.biobank.model.SampleDerivativeType;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StorageTypeAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StorageTypeViewForm extends BiobankViewForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StorageTypeViewForm";
    
    private StorageTypeAdapter storageTypeAdapter;
    
    private StorageType storageType;
    
    private Capacity capacity;

	private Label nameLabel;

	private Label defaultTempLabel;

	private Label activityStatusLabel;

	private Label commentLabel;

	private Label dimOneLabelLabel;

	private Label dimOneCapacityLabel;

	private Label dimTwoLabelLabel;

	private Label dimTwoCapacityLabel;
    
	private org.eclipse.swt.widgets.List sampleDerivTypesList;
	
	private org.eclipse.swt.widgets.List childStorageTypesList;
	
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
            retrieveStorageType();            
            setPartName("Storage Type " + storageType.getName());
        }
        else {
            Assert.isTrue(false, "Invalid editor input: object of type "
                + node.getClass().getName());
        }
    }

    private void retrieveStorageType() {
    	List<StorageType> result;
    	StorageType searchStorageType = new StorageType();
    	searchStorageType.setId(storageTypeAdapter.getStorageType().getId());
		try {
			result = appService.search(StorageType.class, searchStorageType);
			Assert.isTrue(result.size() == 1);
			storageType = result.get(0);
			storageTypeAdapter.setStorageType(storageType);
			capacity = storageType.getCapacity();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	@Override
    protected void createFormContent() {
        form.setText("Storage Type: " + storageType.getName());
        
        addRefreshToolbarAction();
        
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
        
        nameLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Name");
        defaultTempLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Default Temperature\n(Celcius)");
        activityStatusLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Activity Status");
        commentLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Comments"); 
        
        setStorageTypeValues();
    }

	private void setStorageTypeValues() {
		FormUtils.setTextValue(nameLabel, storageType.getName());
        FormUtils.setTextValue(defaultTempLabel, storageType.getDefaultTemperature().toString());
        FormUtils.setTextValue(activityStatusLabel, storageType.getActivityStatus());
        FormUtils.setTextValue(commentLabel, storageType.getComment());
	}

    private void createDimensionsSection() {
        Composite client = createSectionWithClient("Default Capacity");        
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        dimOneLabelLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Dimension One Label");
        dimOneCapacityLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Dimension One Capacity"); 
        dimTwoLabelLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Dimension Two Label"); 
        dimTwoCapacityLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Dimension Two Capacity"); 
        
        setDimensionsValues();
    }

	private void setDimensionsValues() {
		FormUtils.setTextValue(dimOneLabelLabel, storageType.getDimensionOneLabel());
        FormUtils.setTextValue(dimOneCapacityLabel, capacity.getDimensionOneCapacity().toString());
        FormUtils.setTextValue(dimTwoLabelLabel, storageType.getDimensionTwoLabel());
        FormUtils.setTextValue(dimTwoCapacityLabel, capacity.getDimensionTwoCapacity().toString());
	}

    private void createSampleDerivTypesSection() {
        Composite client = createSectionWithClient("Contains Sample Derivatives");       
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client); 
        
        Label label = toolkit.createLabel(client, "Sample derivative types:");      
        label.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
        
//        sampleDerivTypesList = new org.eclipse.swt.widgets.List(client, SWT.BORDER | SWT.V_SCROLL);
//        GridData gd = new GridData(GridData.FILL_BOTH);
//        gd.heightHint = 100;
//        sampleDerivTypesList.setLayoutData(gd);
//        setSampleDerivTypesValues();
    }

//	private void setSampleDerivTypesValues() {
//		sampleDerivTypesList.removeAll();
//		for (SampleDerivativeType type : storageType.getSampleDerivativeTypeCollection()) {
//            sampleDerivTypesList.add(type.getNameShort());
//        }
//	}

    private void createChildStorageTypesSection() {
        Composite client = createSectionWithClient("Contains Storage Types");       
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 10;
        toolkit.paintBordersFor(client); 
        
        Label label = toolkit.createLabel(client, "Storage types:");      
        label.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
        
        childStorageTypesList = new org.eclipse.swt.widgets.List(client, SWT.BORDER | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        childStorageTypesList.setLayoutData(gd);
        setChildStorageTypesValues();
    }

	private void setChildStorageTypesValues() {
		childStorageTypesList.removeAll();
		for (StorageType type : storageType.getChildStorageTypeCollection()) {
            childStorageTypesList.add(type.getName());
        }
	}

    private void createButtons() {        
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(4, false));
        toolkit.paintBordersFor(client);

        final Button edit = toolkit.createButton(
            client, "Edit this information", SWT.PUSH);
        edit.addSelectionListener(new SelectionAdapter() {
            @Override
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

	@Override
	protected void reload() {
		retrieveStorageType();
		setPartName("Storage Type " + storageType.getName());
		form.setText("Storage Type: " + storageType.getName());
		setStorageTypeValues();
		setDimensionsValues();
//		setSampleDerivTypesValues();
		setChildStorageTypesValues();
	}
}
