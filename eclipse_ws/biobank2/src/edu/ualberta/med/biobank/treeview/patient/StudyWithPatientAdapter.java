package edu.ualberta.med.biobank.treeview.patient;

import java.util.List;

import edu.ualberta.med.biobank.common.action.patient.PatientInfo;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;

public class StudyWithPatientAdapter extends StudyAdapter {

    public StudyWithPatientAdapter(AdapterBase parent, StudyWrapper studyWrapper) {
        super(parent, studyWrapper);
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId, PatientInfo.class);
    }

}
