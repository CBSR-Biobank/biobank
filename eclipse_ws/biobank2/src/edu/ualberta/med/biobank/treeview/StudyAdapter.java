package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
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
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class StudyAdapter extends Node {
    
    private static Logger log4j = Logger.getLogger(SessionManager.class.getName());
    
	private Study study;
	
	public StudyAdapter(Node parent, Study study) {
		super(parent, study.getId(), study.getName(), true);
		this.setStudy(study);
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public Study getStudy() {
		return study;
	}

	@Override
	public int getId() {
		Assert.isNotNull(study, "study is null");
		Object o = (Object) study.getId();
		if (o == null) return 0;
		return study.getId();
	}

	@Override
	public String getName() {
		Assert.isNotNull(study, "study is null");
		Object o = (Object) study.getNameShort();
		if (o == null) return null;
		return study.getNameShort();
	}
    
    public void performDoubleClick() {
        openForm(StudyViewForm.ID);
    }
    
    public void performExpand() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {                
                // read from database again                 
                WritableApplicationService appService = getAppService();
                try {
                    Study searchStudy = new Study();
                    searchStudy.setId(study.getId());
                    List<Study> result = appService.search(Study.class, searchStudy);
                    Assert.isTrue(result.size() == 1);
                    study = result.get(0);

                    Collection<Patient> patients = study.getPatientCollection();
                    log4j.trace("performExpand: Study " + study.getName());

                    for (Patient patient: patients) {
                        log4j.trace("performExpand: Patient "
                                + patient.getId() + ": " + patient.getNumber());
                        
                        PatientAdapter node = 
                            new PatientAdapter(StudyAdapter.this, patient);
                        addChild(node);
                        SessionManager.getInstance().getTreeViewer().update(node, null);
                    }
                    SessionManager.getInstance().getTreeViewer().expandToLevel(
                        StudyAdapter.this, 1);
                }
                catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void popupMenu(TreeViewer tv, Tree tree,  Menu menu) {
        MenuItem mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Edit Study");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(StudyEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        });

        mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("View Study");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(StudyViewForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        }); 

        mi = new MenuItem (menu, SWT.PUSH);
        mi.setText ("Add Patient");
        mi.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                openForm(PatientEntryForm.ID);
            }

            public void widgetDefaultSelected(SelectionEvent e) {                    
            }
        }); 
    }
}
