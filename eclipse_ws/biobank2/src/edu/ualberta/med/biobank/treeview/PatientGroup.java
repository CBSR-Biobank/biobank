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
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PatientGroup extends Node {

    public PatientGroup(StudyAdapter parent, int id) {
        super(parent, id, "Patients", true);
    }

    public void performDoubleClick() {
        performExpand();
    }

    public void performExpand() {   
        final Study parentStudy = ((StudyAdapter) getParent()).getStudy();
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {                
                // read from database again                 
                WritableApplicationService appService = getAppService();
                try {
                    Study searchStudy = new Study();
                    searchStudy.setId(parentStudy.getId());
                    List<Study> result = appService.search(Study.class, searchStudy);
                    Assert.isTrue(result.size() == 1);
                    searchStudy = result.get(0);

                    Collection<Patient> patients = searchStudy.getPatientCollection();

                    for (Patient patient: patients) {
                        PatientAdapter node = 
                            new PatientAdapter(PatientGroup.this, patient);
                        addChild(node);
                        SessionManager.getInstance().getTreeViewer().update(node, null);
                    }
                    SessionManager.getInstance().getTreeViewer().expandToLevel(
                        PatientGroup.this, 1);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void popupMenu(TreeViewer tv, Tree tree,  Menu menu) {
        MenuItem mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Add Patient");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                PatientAdapter adapter = new PatientAdapter(
                    PatientGroup.this, new Patient());
                openForm(new FormInput(adapter), PatientEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        }); 
    }
}
