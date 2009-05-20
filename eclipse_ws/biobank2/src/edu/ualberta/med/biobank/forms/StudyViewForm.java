package edu.ualberta.med.biobank.forms;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Section;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Sdata;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.BiobankCollectionTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StudyViewForm extends BiobankViewForm {

	public static final String ID =
		"edu.ualberta.med.biobank.forms.StudyViewForm";

	private StudyAdapter studyAdapter;
	private Study study;

	private Label nameShortLabel;
	private Label activityStatusLabel;
	private Label commentLabel;

	private BiobankCollectionTable clinicsTable;
	private BiobankCollectionTable patientsTable;
	private BiobankCollectionTable sContainersTable;
	private BiobankCollectionTable sDatasTable;

	@Override
	public void init(IEditorSite editorSite, IEditorInput input) 
	throws PartInitException {        
		super.init(editorSite, input);

		Node node = ((FormInput) input).getNode();
		Assert.notNull(node, "Null editor input");

		if (node instanceof StudyAdapter) {
			studyAdapter = (StudyAdapter) node;

			// retrieve info from database because could have been modified after first opening
			retrieveStudy();
			setPartName("Study " + study.getName());
		}
		else {
			Assert.isTrue(false, "Invalid editor input: object of type "
					+ node.getClass().getName());
		}    
	}

	@Override
	protected void createFormContent() {
		if (study.getName() != null) {
			form.setText("Study: " + study.getName());
		}
		
		addRefreshToolbarAction();
		
		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite client = toolkit.createComposite(form.getBody());
		client.setLayout(new GridLayout(2, false));
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
		toolkit.paintBordersFor(client); 

		nameShortLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Short Name");
		activityStatusLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Activity Status");
		commentLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Comments");

		setStudySectionValues();
				
		Node clinicGroupNode = 
			((SiteAdapter) studyAdapter.getParent().getParent()).getClinicGroupNode();
		clinicsTable = FormUtils.createClinicSection(toolkit, form.getBody(), clinicGroupNode,
				study.getClinicCollection());

		createPatientsSection();
		createStorageContainerSection();
		createDataCollectedSection();        
	}

	private void setStudySectionValues() {
		FormUtils.setTextValue(nameShortLabel, study.getNameShort());
		FormUtils.setTextValue(activityStatusLabel, study.getActivityStatus());
		FormUtils.setTextValue(commentLabel, study.getComment());
	}

	private void createPatientsSection() {        
		Section section = createSection("Patients");  

		String [] headings = new String[] {"Patient Number"};      
		patientsTable = new BiobankCollectionTable(section, SWT.NONE, headings, getPatientsAdapters());
		section.setClient(patientsTable);
		patientsTable.adaptToToolkit(toolkit);   
		toolkit.paintBordersFor(patientsTable);

		patientsTable.getTableViewer().addDoubleClickListener(
				FormUtils.getBiobankCollectionDoubleClickListener());
	}

	private PatientAdapter[] getPatientsAdapters() {
		// hack required here because site.getStudyCollection().toArray(new Study[0])
		// returns Object[].        
		int count = 0;
		Collection<Patient> patients = study.getPatientCollection();
		PatientAdapter [] arr = new PatientAdapter [patients.size()];
		for (Patient patient : patients) {
			arr[count] = new PatientAdapter(studyAdapter, patient);
			++count;
		}
		return arr;
	}

	private void createStorageContainerSection() {        
		Section section = createSection("Storage Containers");  

		String [] headings = new String[] {"Name", "Status", "Bar Code", "Full", "Temperature"};      
		sContainersTable = new BiobankCollectionTable(section, SWT.NONE, headings, getStorageContainers());
		section.setClient(sContainersTable);
		sContainersTable.adaptToToolkit(toolkit);   
		toolkit.paintBordersFor(sContainersTable);

		sContainersTable.getTableViewer().addDoubleClickListener(
				FormUtils.getBiobankCollectionDoubleClickListener());
	}

	private StorageContainer[] getStorageContainers() {
		// hack required here because site.getStudyCollection().toArray(new Study[0])
		// returns Object[].        
		int count = 0;
		Collection<StorageContainer> storageContainers = study.getStorageContainerCollection();
		StorageContainer [] arr = new StorageContainer [storageContainers.size()];
		Iterator<StorageContainer> it = storageContainers.iterator();
		while (it.hasNext()) {
			arr[count] = it.next();
			++count;
		}
		return arr;
	}
	
	private void createDataCollectedSection() {           
		Section section = createSection("Study Data Collected");

		String [] headings = new String[] {"Name", "Valid Values (optional)"};      
		sDatasTable = new BiobankCollectionTable(section, SWT.NONE, headings, getSDatas());
		section.setClient(sDatasTable);
		sDatasTable.adaptToToolkit(toolkit); 
		toolkit.paintBordersFor(sDatasTable);

		sDatasTable.getTableViewer().addDoubleClickListener(
				FormUtils.getBiobankCollectionDoubleClickListener());
	}

	private Sdata[] getSDatas() {
		// hack required here because site.getStudyCollection().toArray(new Study[0])
		// returns Object[].        
		int count = 0;
		Collection<Sdata> sdatas = study.getSdataCollection();
		Sdata [] arr = new Sdata [sdatas.size()];
		Iterator<Sdata> it = sdatas.iterator();
		while (it.hasNext()) {
			arr[count] = it.next();
			++count;
		}
		return arr;
	}

	@Override
	protected void reload() {    	
		retrieveStudy();
		setPartName("Study " + study.getName());
		form.setText("Study: " + study.getName());
		setStudySectionValues();
		Node clinicGroupNode = 
			((SiteAdapter) studyAdapter.getParent().getParent()).getClinicGroupNode();
		clinicsTable.getTableViewer().setInput(FormUtils.getClinicsAdapters(clinicGroupNode, study.getClinicCollection()));
		patientsTable.getTableViewer().setInput(getPatientsAdapters());	
		sContainersTable.getTableViewer().setInput(getStorageContainers());
		sDatasTable.getTableViewer().setInput(getSDatas());
	}

	private void retrieveStudy() {
		List<Study> result;
		Study searchStudy = new Study();
		searchStudy.setId(studyAdapter.getStudy().getId());
		try {
			result = studyAdapter.getAppService().search(Study.class, searchStudy);
			Assert.isTrue(result.size() == 1);
			study = result.get(0);
			studyAdapter.setStudy(study);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

}
