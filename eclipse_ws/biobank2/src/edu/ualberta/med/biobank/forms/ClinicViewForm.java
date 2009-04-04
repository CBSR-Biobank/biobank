package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.beans.PojoObservables;
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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;

public class ClinicViewForm  extends AddressViewFormCommon {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.ClinicViewForm";

	private ClinicAdapter clinicAdapter;
	private Clinic clinic;
	
	Label name;

	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);
		if ( !(input instanceof FormInput)) 
			throw new PartInitException("Invalid editor input"); 
        
        FormInput clinicInput = (FormInput) input;
        
        clinicAdapter = (ClinicAdapter) clinicInput.getNode();
        clinic = clinicAdapter.getClinic();
        address = clinic.getAddress();
	}

	protected void createFormContent() {
		if (clinic.getName() != null) {
			form.setText("Clinic: " + clinic.getName());
		}
		
		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createClinicSection();
        createStudiesSection();
        createButtonsSection();
	}
    
    private void createClinicSection() {    
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        
        createBoundWidget(client, Label.class, SWT.NONE, "Activity Status",
            PojoObservables.observeValue(clinic, "activityStatus"));
        
        Label comments = (Label) createBoundWidget(client, Label.class, 
            SWT.NONE, "Comments", PojoObservables.observeValue(clinic, "comment"));
        GridData gd = new GridData();
        gd.heightHint = 100;
        comments.setLayoutData(gd);		
		createAddressArea(client);
	}
	
	protected void createStudiesSection() {
		Section section = toolkit.createSection(form.getBody(),  
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED);
		section.setText("Associated Studies");
		//section.setFont(FormUtils.getSectionFont());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(4, false));
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		section.setClient(client);
		toolkit.paintBordersFor(client);	
		
		// TODO studies go here
	}
	
	protected void createButtonsSection() {
		Composite client = toolkit.createComposite(form.getBody());
		client.setLayout(new GridLayout(4, false));
		toolkit.paintBordersFor(client);

		final Button edit = toolkit.createButton(client, "Edit Clinic Info", SWT.PUSH);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
				    FormInput input = new FormInput(clinicAdapter); 
		            SessionManager.getInstance().closeEditor(input);
					getSite().getPage().openEditor(
					        input, ClinicEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
					// handle error
					exp.printStackTrace();				
				}
			}
		});
	}
}

