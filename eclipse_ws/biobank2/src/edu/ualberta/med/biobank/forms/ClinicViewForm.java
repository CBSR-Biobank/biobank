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
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.BiobankCollectionTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicViewForm  extends AddressViewFormCommon {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.ClinicViewForm";

	private ClinicAdapter clinicAdapter;
	private Clinic clinic;
	
	private BiobankCollectionTable studiesTable;

	private Label activityStatusLabel;

	private Label commentLabel;
	
	@Override
	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);
        
		Node node = ((FormInput) input).getNode();
        if (node instanceof ClinicAdapter) {
        	clinicAdapter = (ClinicAdapter) node;
        	retrieveClinic();
        	address = clinic.getAddress();
        	setPartName("Clinic: " + clinic.getName());
        } else {
        	Assert.isTrue(false, "Invalid editor input: object of type "
					+ node.getClass().getName());
        }
	}

	private void retrieveClinic() {
		List<Clinic> result;
		Clinic searchClinic = new Clinic();
		searchClinic.setId(clinicAdapter.getClinic().getId());
		try {
			result = clinicAdapter.getAppService().search(Clinic.class, searchClinic);
			Assert.isTrue(result.size() == 1);
			clinic = result.get(0);
			clinicAdapter.setClinic(clinic);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void createFormContent() {
		if (clinic.getName() != null) {
			form.setText("Clinic: " + clinic.getName());
		}
		
		addRefreshToolbarAction();
		
		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createClinicSection();
		createAddressSection();
        createStudiesSection();
        createButtonsSection();
	}
    
    private void createClinicSection() {    
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        
        activityStatusLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Activity Status");
        commentLabel = (Label)createWidget(client, Label.class, SWT.NONE, "Comments");
        
        setClinicValues();
	}

	private void setClinicValues() {
		FormUtils.setTextValue(activityStatusLabel, clinic.getActivityStatus());
		FormUtils.setTextValue(commentLabel, clinic.getComment());
	}
    
	protected void createStudiesSection() {
        Composite client = createSectionWithClient("Studies");
        
        String [] headings = new String[] {"Name", "Short Name", "Num. Patients"};      
        studiesTable = new BiobankCollectionTable(client, SWT.NONE, headings, getStudiesAdapters());
        studiesTable.adaptToToolkit(toolkit);   
        toolkit.paintBordersFor(studiesTable);
        
        studiesTable.getTableViewer().addDoubleClickListener(
                FormUtils.getBiobankCollectionDoubleClickListener());
	}

	private StudyAdapter[] getStudiesAdapters() {
		Collection<Study> studies = clinic.getStudyCollection();
		
        StudyAdapter [] studyAdapters = new StudyAdapter [studies.size()];
        int count = 0;
        for (Study study : studies) {
            studyAdapters[count] = new StudyAdapter(
                clinicAdapter.getParent(), study);
            count++;
        }
		return studyAdapters;
	}
	
	protected void createButtonsSection() {
		Composite client = toolkit.createComposite(form.getBody());
		client.setLayout(new GridLayout(4, false));
		toolkit.paintBordersFor(client);

		final Button edit = toolkit.createButton(client, "Edit Clinic Info", SWT.PUSH);
		edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                clinicAdapter.openForm(new FormInput(clinicAdapter), ClinicEntryForm.ID);
			}
		});
	}

	@Override
	protected void reload() {
		retrieveClinic();
		setPartName("Clinic: " + clinic.getName());
		form.setText("Clinic: " + clinic.getName());
		setClinicValues();
		setAdressValues();
		studiesTable.getTableViewer().setInput(getStudiesAdapters());	
	}
}

