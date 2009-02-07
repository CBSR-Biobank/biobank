package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ClinicNode;
import edu.ualberta.med.biobank.model.WsObject;

public class ClinicViewForm  extends AddressViewForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.ClinicViewForm";
	
	private WsObject node;
	private Clinic clinic;
	
	Label name;

	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {		
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);
		if ( !(input instanceof WsObjectInput)) 
			throw new PartInitException("Invalid editor input"); 
		
		node = ((WsObjectInput) input).getWsObject();
		Assert.notNull(node, "Null editor input");

		if (node instanceof ClinicNode) {
			ClinicNode clinicNode = (ClinicNode) node;
			clinic = clinicNode.getClinic();
			address = clinic.getAddress();
			setPartName("Clinic " + clinic.getName());
		}
		else {
			Assert.isTrue(false, "Invalid editor input: object of type "
				+ node.getClass().getName());
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);	

		if (clinic.getName() != null) {
			form.setText("Clinic: " + clinic.getName());
		}
		
		toolkit.decorateFormHeading(form);
		//form.setMessage(OK_MESSAGE);
		
		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Section section = toolkit.createSection(form.getBody(),  
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setText("Address");
		section.setLayout(new GridLayout(1, false));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		Composite sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		sbody.setLayout(new GridLayout(4, false));
		sbody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		toolkit.paintBordersFor(sbody);	
		
		createAddressArea(sbody);

		section = toolkit.createSection(form.getBody(),  
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED);
		section.setText("Associated Studies");
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sbody = toolkit.createComposite(section);
		sbody.setLayout(new GridLayout(4, false));
		sbody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		section.setClient(sbody);
		toolkit.paintBordersFor(sbody);
		
		// studies go here

		sbody = toolkit.createComposite(form.getBody());
		sbody.setLayout(new GridLayout(4, false));
		toolkit.paintBordersFor(sbody);

		final Button edit = toolkit.createButton(sbody, "Edit Clinic Info", SWT.PUSH);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSite().getPage().closeEditor(ClinicViewForm.this, false);
				
				WsObjectInput input = new WsObjectInput(node);
				
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

	@Override
	public void setFocus() {
		form.setFocus();
	}

}
