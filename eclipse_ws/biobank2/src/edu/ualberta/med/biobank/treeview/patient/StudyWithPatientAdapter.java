package edu.ualberta.med.biobank.treeview.patient;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class StudyWithPatientAdapter extends StudyAdapter {

    public StudyWithPatientAdapter(AdapterBase parent, StudyWrapper studyWrapper) {
        super(parent, studyWrapper);
    }

    @Override
    public AdapterBase search(Object searchedObject) {
        if (searchedObject instanceof PatientWrapper) {
            return getChild((ModelWrapper<?>) searchedObject, true);
        }
        return searchChildren(searchedObject);
    }

}
