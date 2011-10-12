package edu.ualberta.med.biobank.treeview.patient;

import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.action.patient.SearchPatientAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.admin.NewStudyAdapter;

public class StudyWithPatientAdapter extends NewStudyAdapter {

    private StudyNodeInfo spInfo;

    public StudyWithPatientAdapter(AbstractAdapterBase parent,
        StudyNodeInfo spInfo) {
        super(parent, spInfo.study);
        this.spInfo = spInfo;
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId, Patient.class);
    }

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
        return spInfo.patients;
    }

    @Override
    protected int getChildrenCount() throws Exception {
        return spInfo.patients.size();
    }

    @Override
    protected PatientAdapter createChildNode() {
        return new PatientAdapter(this, null);
    }

    @Override
    protected PatientAdapter createChildNode(Object child) {
        return new PatientAdapter(this, (SearchedPatientInfo) child);
    }
}
