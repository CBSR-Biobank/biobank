package edu.ualberta.med.biobank.forms.input;

import edu.ualberta.med.biobank.model.Clinic;

public class ClinicInput extends FormInput {      
    private Clinic clinic;
    
    public ClinicInput(String sessionName, Clinic clinic) {
        super(sessionName);
        this.clinic = clinic;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }
    
    @Override
    public String getName() {
        return clinic.getName();
    }

    @Override
    public String getToolTipText() {
        return clinic.getName();
    }

}
