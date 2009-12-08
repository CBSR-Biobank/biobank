package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public class StudyContactAndPatientInfo implements ITableInfo {

    public String clinicName;

    public long patients;

    public long patientVisits;

    public ContactWrapper contact;

    @Override
    public ModelWrapper<?> getDisplayedWrapper() {
        if (contact != null) {
            return contact.getClinic();
        }
        return null;
    }
}
