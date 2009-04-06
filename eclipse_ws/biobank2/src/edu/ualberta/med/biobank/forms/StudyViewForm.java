package edu.ualberta.med.biobank.forms;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.databinding.beans.PojoObservables;
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
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.BiobankCollectionTable;

public class StudyViewForm extends BiobankViewForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.StudyViewForm";
    
    private StudyAdapter studyAdapter;
    private Study study;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input) 
    throws PartInitException {        
        super.init(editorSite, input);
        
        Node node = ((FormInput) input).getNode();
        Assert.notNull(node, "Null editor input");

        if (node instanceof StudyAdapter) {
            studyAdapter = (StudyAdapter) node;
            study = studyAdapter.getStudy();
            setPartName("Study " + study.getName());
        }
        else {
            Assert.isTrue(false, "Invalid editor input: object of type "
                + node.getClass().getName());
        }
    }
    
    protected void createFormContent() {

        if (study.getName() != null) {
            form.setText("Study: " + study.getName());
        }
        
        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        toolkit.paintBordersFor(client); 
        
        createBoundWidget(client, Label.class, SWT.NONE, "Short Name",
            PojoObservables.observeValue(study, "nameShort"));
        
        createBoundWidget(client, Label.class, SWT.NONE, "Activity Status",
            PojoObservables.observeValue(study, "activityStatus"));
        
        createBoundWidget(client, Label.class, 
            SWT.NONE, "Comments", PojoObservables.observeValue(study, "comment"));
        
        Node clinicGroupNode = 
            ((SiteAdapter) studyAdapter.getParent().getParent()).getClinicGroupNode();

        FormUtils.createClinicSection(toolkit, form.getBody(), clinicGroupNode,
                study.getClinicCollection());
        
        createPatientsSection();
        createStorageContainerSection();
        createDataCollectedSection();
    }
    
    private void createDataCollectedSection() {           
        Section section = createSection("Study Data Collected");

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

        String [] headings = new String[] {"Name", "Valid Values (optional)"};      
        BiobankCollectionTable comp = 
            new BiobankCollectionTable(section, SWT.NONE, headings, arr);
        section.setClient(comp);
        comp.adaptToToolkit(toolkit); 
        toolkit.paintBordersFor(comp);

        comp.getTableViewer().addDoubleClickListener(
                FormUtils.getBiobankCollectionDoubleClickListener());
    }
    
    private void createPatientsSection() {        
        Section section = createSection("Patients");  
        
        // hack required here because site.getStudyCollection().toArray(new Study[0])
        // returns Object[].        
        int count = 0;
        Collection<Patient> patients = study.getPatientCollection();
        Patient [] arr = new Patient [patients.size()];
        Iterator<Patient> it = patients.iterator();
        while (it.hasNext()) {
            arr[count] = it.next();
            ++count;
        }

        String [] headings = new String[] {"Patient Number"};      
        BiobankCollectionTable comp = 
            new BiobankCollectionTable(section, SWT.NONE, headings, arr);
        section.setClient(comp);
        comp.adaptToToolkit(toolkit);   
        toolkit.paintBordersFor(comp);
        
        comp.getTableViewer().addDoubleClickListener(
                FormUtils.getBiobankCollectionDoubleClickListener());
    }
    
    private void createStorageContainerSection() {        
        Section section = createSection("Storage Containers");  
        
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

        String [] headings = new String[] {"Name", "Status", "Bar Code", "Full", "Temperature"};      
        BiobankCollectionTable comp = 
            new BiobankCollectionTable(section, SWT.NONE, headings, arr);
        section.setClient(comp);
        comp.adaptToToolkit(toolkit);   
        toolkit.paintBordersFor(comp);
        
        comp.getTableViewer().addDoubleClickListener(
                FormUtils.getBiobankCollectionDoubleClickListener());
    }
}
