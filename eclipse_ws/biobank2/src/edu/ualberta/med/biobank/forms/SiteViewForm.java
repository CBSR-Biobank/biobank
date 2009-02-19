package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class SiteViewForm extends AddressViewForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteViewForm";
	
	private SiteAdapter siteAdapter;
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
		if ( !(input instanceof NodeInput)) 
			throw new PartInitException("Invalid editor input"); 
		
		Node node = ((NodeInput) input).getNode();
		Assert.notNull(node, "Null editor input");

		if (node instanceof SiteAdapter) {
			siteAdapter = (SiteAdapter) node;
			site = siteAdapter.getSite();
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
		
		Composite sbody = toolkit.createComposite(form.getBody());
		sbody.setLayout(new GridLayout(2, false));
		sbody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		toolkit.paintBordersFor(sbody);	
		
		createAddressArea(sbody);

		sbody = toolkit.createComposite(form.getBody());
		sbody.setLayout(new GridLayout(4, false));
		toolkit.paintBordersFor(sbody);

		final Button edit = toolkit.createButton(sbody, "Edit Site Info", SWT.PUSH);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSite().getPage().closeEditor(SiteViewForm.this, false);
				try {
					getSite().getPage().openEditor(new NodeInput(siteAdapter), 
							SiteEntryForm.ID, true);
				}
				catch (PartInitException exp) {
					exp.printStackTrace();				
				}
			}
		});

		final Button study = toolkit.createButton(sbody, "Add Study", SWT.PUSH);
		study.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				try {
					Study study = new Study();
					Node studiesNode = siteAdapter.getStudiesGroupNode();
					StudyAdapter studyAdapter = new StudyAdapter(studiesNode, study);
					studiesNode.addChild(studyAdapter);
					getSite().getPage().openEditor(new NodeInput(studyAdapter), 
							StudyEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
					exp.printStackTrace();				
				}
			}
		});

		final Button clinic = toolkit.createButton(sbody, "Add Clinic", SWT.PUSH);
		clinic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					Clinic clinic = new Clinic();
					clinic.setAddress(new Address());
					Node clinicsNode = siteAdapter.getClinicGroupNode();
					ClinicAdapter clinicAdapter = new ClinicAdapter(clinicsNode, clinic);
					clinicsNode.addChild(clinicAdapter);
					getSite().getPage().openEditor(new NodeInput(clinicAdapter), 
							ClinicEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
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
    	super.bindValues(dbc);
    }

	@Override
	public void setFocus() {
		form.setFocus();
	}

}
