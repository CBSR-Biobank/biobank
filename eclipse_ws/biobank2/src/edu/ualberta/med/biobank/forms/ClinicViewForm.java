package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.DataBindingContext;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;

public class ClinicViewForm  extends AddressViewForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.ClinicViewForm";
	
	private Node node;
	private Clinic clinic;
	
	Label name;

	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);
		if ( !(input instanceof NodeInput)) 
			throw new PartInitException("Invalid editor input"); 
		
		node = ((NodeInput) input).getNode();
		Assert.notNull(node, "Null editor input");

		if (node instanceof ClinicAdapter) {
			ClinicAdapter clinicNode = (ClinicAdapter) node;
			clinic = clinicNode.getClinic();
			address = clinic.getAddress();
			setPartName("Clinic " + clinic.getName());
		}
		else {
			Assert.isTrue(false, "Invalid editor input: object of type "
				+ node.getClass().getName());
		}
	}

	protected void createFormContent() {
		if (clinic.getName() != null) {
			form.setText("Clinic: " + clinic.getName());
		}
		
		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite client = toolkit.createComposite(form.getBody());
		client.setLayout(new GridLayout(4, false));
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		toolkit.paintBordersFor(client);	
		
		createAddressArea(client);

		Section section = toolkit.createSection(form.getBody(),  
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED);
		section.setText("Associated Studies");
		//section.setFont(FormUtils.getSectionFont());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(4, false));
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		section.setClient(client);
		toolkit.paintBordersFor(client);
		
		// studies go here

		client = toolkit.createComposite(form.getBody());
		client.setLayout(new GridLayout(4, false));
		toolkit.paintBordersFor(client);

		final Button edit = toolkit.createButton(client, "Edit Clinic Info", SWT.PUSH);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSite().getPage().closeEditor(ClinicViewForm.this, false);
				
				NodeInput input = new NodeInput(node);
				
				try {
					getSite().getPage().openEditor(input, ClinicEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
					// handle error
					exp.printStackTrace();				
				}
			}
		});
		
		bindValues();
	}
    
    private void bindValues() {
    	DataBindingContext dbc = new DataBindingContext();    	
    	super.bindValues(dbc);
    }
}

