package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public class StudyContactInfo implements ITableInfo {

    public ContactWrapper contact;

    @Override
    public ModelWrapper<?> getDisplayedWrapper() {
        if (contact != null) {
            return contact.getClinic();
        }
        return null;
    }

}
