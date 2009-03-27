package edu.ualberta.med.biobank.forms.input;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;

public class ClinicInput extends FormInput {    
    private Site parentSite;
    
    private Clinic clinic;
    
    public ClinicInput(String sessionName, Site parentSite, Clinic clinic) {
        super(sessionName);
        this.parentSite = parentSite;
        this.clinic = clinic;
    }
    
    public Site getParentSite() {
        return parentSite;
    }

    public void setParentSite(Site parentSite) {
        this.parentSite = parentSite;
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
