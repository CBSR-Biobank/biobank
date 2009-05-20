package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.PatientViewForm;
import edu.ualberta.med.biobank.forms.PatientVisitEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PatientAdapter extends Node {
    
    private Patient patient;

    public PatientAdapter(Node parent, Patient patient) {
        super(parent);
        this.patient = patient;
        setHasChildren(true);
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
    	this.patient = patient;
    }

    @Override
    public int getId() {
        Assert.isNotNull(patient, "patient is null");
        Object o = patient.getId();
        if (o == null) return 0;
        return patient.getId();
    }

    @Override
    public String getName() {
        Assert.isNotNull(patient, "storage type is null");
        Object o = patient.getNumber();
        if (o == null) return null;
        return patient.getNumber();
    }
    
    @Override
	public void performDoubleClick() {
        openForm(new FormInput(this), PatientViewForm.ID);
    }

    @Override
	public void performExpand() {   
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {                
                // read from database again                 
                WritableApplicationService appService = getAppService();
                try {
                    Patient searchPatient = new Patient();
                    searchPatient.setId(patient.getId());
                    List<Patient> result = appService.search(Patient.class, searchPatient);
                    Assert.isTrue(result.size() == 1);
                    searchPatient = result.get(0);

                    Collection<PatientVisit> visits = searchPatient.getPatientVisitCollection();

                    for (PatientVisit visit : visits) {
                        PatientVisitAdapter node = (PatientVisitAdapter) 
                            getChild(visit.getId());
                            
                        if (node == null) {
                            node = new PatientVisitAdapter(PatientAdapter.this, visit);
                            addChild(node);
                        }
                        
                        SessionManager.getInstance().getTreeViewer().update(node, null);
                    }
                    SessionManager.getInstance().getTreeViewer().expandToLevel(
                        PatientAdapter.this, 1);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    @Override
	public void popupMenu(TreeViewer tv, Tree tree,  Menu menu) {
        MenuItem mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Edit Patient");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(PatientAdapter.this), PatientEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });

        mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("View Patient");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(new FormInput(PatientAdapter.this), PatientViewForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        }); 

        mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Add Patient Visit");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                PatientVisitAdapter adapter = new PatientVisitAdapter(
                    PatientAdapter.this, new PatientVisit());
                openForm(new FormInput(adapter), PatientVisitEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        }); 
    }
}
