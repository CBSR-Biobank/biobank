package edu.ualberta.med.biobank.model.provider;

import edu.ualberta.med.biobank.model.study.Patient;
import edu.ualberta.med.biobank.model.study.Study;

public class PatientProvider
    extends AbstractProvider<Patient> {

    private int pnumber = 1;

    protected PatientProvider(Mother mother) {
        super(mother);
    }

    @Override
    public Patient create() {
        Patient patient = new Patient();
        patient.setStudy(mother.getProvider(Study.class).get());
        patient.setPnumber(mother.getName() + "_" + pnumber++);
        return patient;
    }
}
