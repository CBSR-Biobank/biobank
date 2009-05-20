package edu.ualberta.med.biobank.forms;

import java.util.Collection;
import java.util.List;

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
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StorageTypeAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.BiobankCollectionTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SiteViewForm extends AddressViewFormCommon {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteViewForm";
	
	private SiteAdapter siteAdapter;
	
	private Site site;
	
	private BiobankCollectionTable studiesTable;
	private BiobankCollectionTable clinicsTable;
	private BiobankCollectionTable storageTypesTable;

	private Label activityStatusLabel;

	private Label commentLabel;
	
	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);
		
		Node node = ((FormInput) input).getNode();
		Assert.notNull(node, "Null editor input");

		if (node instanceof SiteAdapter) {
			siteAdapter = (SiteAdapter) node;
			retrieveSite();	
			setPartName("Repository Site " + site.getName());
		}
		else {
			Assert.isTrue(false, "Invalid editor input: object of type "
				+ node.getClass().getName());
		}
	}
    
    @Override
	protected void createFormContent() {
		address = site.getAddress();  

		if (site.getName() != null) {
			form.setText("Repository Site: " + site.getName());
		}
		
		addRefreshToolbarAction();			
		
		form.getBody().setLayout(new GridLayout(1, false));
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createSiteSection();
		createAddressSection();
		createStudySection();
		clinicsTable = FormUtils.createClinicSection(toolkit, form.getBody(), 
		        siteAdapter.getClinicGroupNode(), site.getClinicCollection());
        createStorageTypesSection();
		createButtons();
	}

    private void createSiteSection() {    
		Composite client = toolkit.createComposite(form.getBody());
		client.setLayout(new GridLayout(2, false));
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(client);
		
    	activityStatusLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Activity Status");
    	commentLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Comments");
    	setSiteSectionValues();
    }
	
	private void setSiteSectionValues() {
		FormUtils.setTextValue(activityStatusLabel, site.getActivityStatus());
		FormUtils.setTextValue(commentLabel, site.getComment());	
	}
	
    private void createStudySection() {        
		Composite client = createSectionWithClient("Studies");
		
        String [] headings = new String[] {"Name", "Short Name", "Num. Patients"};      
        studiesTable = new BiobankCollectionTable(client, SWT.NONE, headings, getStudiesAdapters());
        studiesTable.adaptToToolkit(toolkit);   
        toolkit.paintBordersFor(studiesTable);
        
        studiesTable.getTableViewer().addDoubleClickListener(
                FormUtils.getBiobankCollectionDoubleClickListener());
    }

	private StudyAdapter[] getStudiesAdapters() {
		Collection<Study> studies = site.getStudyCollection();
        StudyAdapter [] studyAdapters = new StudyAdapter [studies.size()];
        int count = 0;
        for (Study study : studies) {
            studyAdapters[count] = new StudyAdapter(
                    siteAdapter.getStudiesGroupNode(), study);
            count++;
        }
		return studyAdapters;
	}
    
    private void createStorageTypesSection() {     
        Composite client = createSectionWithClient("Storage Types");
        
        String [] headings = new String[] {"Name", "Status", "Default Temperature"};      
        storageTypesTable = new BiobankCollectionTable(client, SWT.NONE, headings, getStorageTypesAdapters());
        storageTypesTable.adaptToToolkit(toolkit);   
        toolkit.paintBordersFor(storageTypesTable);
        
        storageTypesTable.getTableViewer().addDoubleClickListener(
                FormUtils.getBiobankCollectionDoubleClickListener());
        
    }

	private StorageTypeAdapter[] getStorageTypesAdapters() {
		Collection<StorageType> stCollection = site.getStorageTypeCollection();
        StorageTypeAdapter [] adapters = new StorageTypeAdapter [stCollection.size()];
        int count = 0;
        for (StorageType storage : stCollection) {
            adapters[count] = new StorageTypeAdapter(
                    siteAdapter.getStudiesGroupNode(), storage);
            count++;
        }
		return adapters;
	}
	
	private void createButtons() {      
		Composite client = toolkit.createComposite(form.getBody());
		client.setLayout(new GridLayout(4, false));
		toolkit.paintBordersFor(client);

		final Button edit = toolkit.createButton(client, "Edit Site Info", SWT.PUSH);
		edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getSite().getPage().closeEditor(SiteViewForm.this, false);
				try {
					getSite().getPage().openEditor(new FormInput(siteAdapter), 
							SiteEntryForm.ID, true);
				}
				catch (PartInitException exp) {
					exp.printStackTrace();				
				}
			}
		});

		final Button study = toolkit.createButton(client, "Add Study", SWT.PUSH);
		study.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				try {
					Study study = new Study();
					Node studiesNode = siteAdapter.getStudiesGroupNode();
					StudyAdapter studyAdapter = new StudyAdapter(studiesNode, study);
					getSite().getPage().openEditor(new FormInput(studyAdapter), 
							StudyEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
					exp.printStackTrace();				
				}
			}
		});

		final Button clinic = toolkit.createButton(client, "Add Clinic", SWT.PUSH);
		clinic.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ClinicAdapter clinicAdapter = new ClinicAdapter(
					        siteAdapter.getClinicGroupNode(), new Clinic());
					getSite().getPage().openEditor(
							new FormInput(clinicAdapter), ClinicEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
					exp.printStackTrace();				
				}
			}
		});

		final Button storageType = toolkit.createButton(client, "Add Storage Type", SWT.PUSH);
		storageType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	protected void reload() {
		retrieveSite();
		setPartName("Repository Site " + site.getName());
		form.setText("Repository Site: " + site.getName());
		setSiteSectionValues();
		setAdressValues();
		studiesTable.getTableViewer().setInput(getStudiesAdapters());
		clinicsTable.getTableViewer().setInput(FormUtils.getClinicsAdapters(siteAdapter.getClinicGroupNode(), site.getClinicCollection()));
		storageTypesTable.getTableViewer().setInput(getStorageTypesAdapters());
	}
	
	private void retrieveSite() {
		List<Site> result;
		Site searchSite = new Site();
		searchSite.setId(siteAdapter.getSite().getId());
		try {
			result = siteAdapter.getAppService().search(Site.class, searchSite);
			Assert.isTrue(result.size() == 1);
			site = result.get(0);
			siteAdapter.setSite(site);
			address = site.getAddress();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}
}