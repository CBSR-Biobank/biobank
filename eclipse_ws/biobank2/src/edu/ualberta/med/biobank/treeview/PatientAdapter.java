package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.forms.PatientEntryForm;
import edu.ualberta.med.biobank.forms.PatientViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Patient;

public class PatientAdapter extends Node {
    
    private Patient patient;

    public PatientAdapter(Node parent, Patient patient) {
        super(parent);
        this.patient = patient;
    }
    
    public Patient getPatient() {
        return patient;
    }

    @Override
    public int getId() {
        Assert.isNotNull(patient, "patient is null");
        Object o = (Object) patient.getId();
        if (o == null) return 0;
        return patient.getId();
    }

    @Override
    public String getName() {
        Assert.isNotNull(patient, "storage type is null");
        Object o = (Object) patient.getNumber();
        if (o == null) return null;
        return patient.getNumber();
    }
    
    public void performDoubleClick() {
        openForm(new FormInput(this), PatientViewForm.ID);
    }
    
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
    }
}
