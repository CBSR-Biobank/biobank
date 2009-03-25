package edu.ualberta.med.biobank.forms;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Section;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.BiobankCollectionTable;

public class SiteViewForm extends AddressViewForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteViewForm";
	
	private SiteAdapter siteAdapter;
	
	private Site site;

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
    
    protected void createFormContent() {
		address = site.getAddress();  

		if (site.getName() != null) {
			form.setText("BioBank Site: " + site.getName());
		}
		
		form.getBody().setLayout(new GridLayout(1, false));
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createAddressSection();
		createStudySection();
		FormUtils.createClinicSection(toolkit, form.getBody(), 
		        site.getClinicCollection());
		createButtons();
        
        bindValues();
	}
	
	private void createAddressSection() {        
        Section section = toolkit.createSection(form.getBody(), 
            Section.TWISTIE | Section.TITLE_BAR); // | Section.EXPANDED);
        section.setText("Address");
        section.setLayout(new GridLayout(1, false));
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        Composite client;
        client = toolkit.createComposite(section);
        section.setClient(client);
        
        client.setLayout(new GridLayout(2, false));
        toolkit.paintBordersFor(client);     
        createAddressArea(client);
	}
	
    private void createStudySection() {        
        Section section = toolkit.createSection(form.getBody(), 
            Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Studies");
        section.setLayout(new GridLayout(1, false));
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));  
        
        // hack required here because site.getStudyCollection().toArray(new Study[0])
        // returns Object[].        
        int count = 0;
        Collection<Study> studies = site.getStudyCollection();
        Study [] arr = new Study [studies.size()];
        Iterator<Study> it = studies.iterator();
        while (it.hasNext()) {
            arr[count] = it.next();
            ++count;
        }

        String [] headings = new String[] {"Name", "Short Name", "Num. Patients"};      
        BiobankCollectionTable comp = 
            new BiobankCollectionTable(section, SWT.NONE, headings, arr);
        section.setClient(comp);
        comp.adaptToToolkit(toolkit);   
        toolkit.paintBordersFor(comp);
        
        comp.getTableViewer().addDoubleClickListener(
                FormUtils.getBiobankCollectionDoubleClickListener());
    }
	
	private void createButtons() {      
        Composite client;
        
		client = toolkit.createComposite(form.getBody());
		client.setLayout(new GridLayout(4, false));
		toolkit.paintBordersFor(client);

		final Button edit = toolkit.createButton(client, "Edit Site Info", SWT.PUSH);
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

		final Button study = toolkit.createButton(client, "Add Study", SWT.PUSH);
		study.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				try {
					Study study = new Study();
					study.setSite(site);
                    study.setNameShort("New Study");
					Node studiesNode = siteAdapter.getStudiesGroupNode();
					StudyAdapter studyAdapter = new StudyAdapter(studiesNode, study);
					studiesNode.addChild(studyAdapter);
                    study.setNameShort("");
					getSite().getPage().openEditor(new NodeInput(studyAdapter), 
							StudyEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
					exp.printStackTrace();				
				}
			}
		});

		final Button clinic = toolkit.createButton(client, "Add Clinic", SWT.PUSH);
		clinic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					Clinic clinic = new Clinic();
					clinic.setName("New Clinic");
					clinic.setAddress(new Address());
					Node clinicsNode = siteAdapter.getClinicGroupNode();
					ClinicAdapter clinicAdapter = new ClinicAdapter(clinicsNode, clinic);
					clinicsNode.addChild(clinicAdapter);
                    clinic.setName("");
					getSite().getPage().openEditor(new NodeInput(clinicAdapter), 
							ClinicEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
					exp.printStackTrace();				
				}
			}
		});

		final Button storageType = toolkit.createButton(client, "Add Storage Type", SWT.PUSH);
		storageType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});
	}
    
    private void bindValues() {
    	DataBindingContext dbc = new DataBindingContext();    	
    	super.bindValues(dbc);
    }
}

