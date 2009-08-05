package edu.ualberta.med.biobank.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class PatientAdministrationView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.views.patientsAdmin";

    public PatientAdministrationView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(Composite parent) {
        // TODO Auto-generated method stub
        Label label = new Label(parent, SWT.NONE);
        label.setText("view for patients, patients visits, scans...");
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

}
