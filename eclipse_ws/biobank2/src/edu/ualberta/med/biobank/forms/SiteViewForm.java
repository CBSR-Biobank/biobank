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

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ClinicNode;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SiteNode;
import edu.ualberta.med.biobank.model.WsObject;

public class SiteViewForm extends AddressViewForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteViewForm";
	
	private WsObject node;
	private Site site;
	
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

		if (node instanceof SiteNode) {
			SiteNode siteNode = (SiteNode) node;
			site = siteNode.getSite();
			address = site.getAddress();
			setPartName("Site " + site.getName());
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

		if (site.getName() != null) {
			form.setText("BioBank Site: " + site.getName());
		}
		
		toolkit.decorateFormHeading(form);
		//form.setMessage(OK_MESSAGE);
		
		GridLayout layout = new GridLayout(1, false);
		//layout.marginHeight = 10;
		//layout.marginWidth = 6;
		//layout.horizontalSpacing = 20;
		form.getBody().setLayout(layout);
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Section section = toolkit.createSection(form.getBody(),  
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED);
		section.setText("Site");
		section.setLayout(new GridLayout(1, false));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		Composite sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		sbody.setLayout(new GridLayout(2, false));
		sbody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		toolkit.paintBordersFor(sbody);	
		
		name = createLabelledField(sbody, "Name :", 100, null);

		section = toolkit.createSection(form.getBody(),  
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setText("Address");
		sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sbody.setLayout(new GridLayout(4, false));
		sbody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		toolkit.paintBordersFor(sbody);
		
		createAddressArea(sbody);

		section = toolkit.createSection(form.getBody(),  
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED);
		section.setText("Studies");
		sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		//layout.horizontalSpacing = 10;
		layout.numColumns = 4;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);
		
		// studies go here

		section = toolkit.createSection(form.getBody(),  
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED);
		section.setText("Clinics");
		sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.numColumns = 4;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);
		
		// Clinics go here

		section = toolkit.createSection(form.getBody(),  
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED);
		section.setText("Storage Types");
		sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.numColumns = 4;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);
		
		// Storage types go here

		section = toolkit.createSection(form.getBody(), SWT.NONE);
		sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.numColumns = 4;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);

		final Button edit = toolkit.createButton(sbody, "Edit Site Info", SWT.PUSH);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSite().getPage().closeEditor(SiteViewForm.this, false);
				
				WsObjectInput input = new WsObjectInput(node);
				
				try {
					getSite().getPage().openEditor(input, SiteEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
					// handle error
					exp.printStackTrace();				
				}
			}
		});

		final Button study = toolkit.createButton(sbody, "Add Study", SWT.PUSH);
		study.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});

		final Button clinic = toolkit.createButton(sbody, "Add Clinic", SWT.PUSH);
		clinic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					SiteNode siteNode = (SiteNode) node;
					Clinic clinic = new Clinic();
					clinic.setAddress(new Address());
					ClinicNode clinicNode = new ClinicNode(siteNode.getClinicGroupNode(), clinic);
					siteNode.getClinicGroupNode().addChild(clinicNode);
					getSite().getPage().openEditor(new WsObjectInput(clinicNode), ClinicEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
					// handle error
					exp.printStackTrace();				
				}
			}
		});

		final Button storageType = toolkit.createButton(sbody, "Add Storage Type", SWT.PUSH);
		storageType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});
		
		bindValues();
	}
    
    private void bindValues() {
    	DataBindingContext dbc = new DataBindingContext();
    	
		dbc.bindValue(SWTObservables.observeText(name),
				PojoObservables.observeValue(site, "name"), null, null);
    	
    	super.bindValues(dbc);
    }

	@Override
	public void setFocus() {
		form.setFocus();
	}

}
