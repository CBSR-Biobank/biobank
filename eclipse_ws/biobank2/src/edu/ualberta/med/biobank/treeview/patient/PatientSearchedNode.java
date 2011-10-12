package edu.ualberta.med.biobank.treeview.patient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.action.patient.SearchPatientAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.NewAbstractSearchedNode;

public class PatientSearchedNode extends NewAbstractSearchedNode {

    /**
     * map the id of a study to a list of patient infos
     */
    private Map<Integer, StudyNodeInfo> studyPatientsMap;

    public PatientSearchedNode(AdapterBase parent, int id) {
        super(parent, id);
        studyPatientsMap = new HashMap<Integer, StudyNodeInfo>();
    }

    @Override
    protected StudyWithPatientAdapter createChildNode(Object child) {
        return new StudyWithPatientAdapter(this, (StudyNodeInfo) child);
    }

    @Override
    protected StudyWithPatientAdapter createChildNode() {
        return new StudyWithPatientAdapter(this, null);
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId, Study.class);
    }

    @Override
    public void rebuild() {
        performExpand();
    }

    public void addPatient(SearchedPatientInfo pinfo) {
        StudyNodeInfo snodeInfo = studyPatientsMap.get(pinfo.study.getId());
        if (snodeInfo == null) {
            snodeInfo = new StudyNodeInfo();
            snodeInfo.study = pinfo.study;
            snodeInfo.patients = new HashMap<Integer, SearchedPatientInfo>();
            studyPatientsMap.put(pinfo.study.getId(), snodeInfo);
        }
        snodeInfo.patients.put(pinfo.patient.getId(), pinfo);

    }

    @Override
    protected String getLabelInternal() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
        return studyPatientsMap;
    }

    @Override
    protected int getChildrenCount() throws Exception {
        return studyPatientsMap.size();
    }

}
