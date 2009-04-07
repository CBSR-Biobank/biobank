package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.model.PatientVisit;

public class PatientVisitAdapter extends Node {
    
    private PatientVisit patientVisit;

    public PatientVisitAdapter(Node parent, PatientVisit patientVisit) {
        super(parent);
        this.patientVisit = patientVisit;
    }
    
    public PatientVisit getPatientVisit() {
        return patientVisit;
    }

    @Override
    public int getId() {
        Assert.isNotNull(patientVisit, "patientVisit is null");
        Object o = (Object) patientVisit.getId();
        if (o == null) return 0;
        return patientVisit.getId();
    }

}
