package edu.ualberta.med.biobank.treeview.patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.NewAbstractSearchedNode;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientSearchedNode extends NewAbstractSearchedNode {

    /**
     * map the id of a study to a list of patient infos
     */
    private Map<Integer, StudyNodeInfo> studyPatientsMap;

    public PatientSearchedNode(AbstractAdapterBase parent, int id) {
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
        // need to retrieve the patients again in case they have been modified
        for (Entry<Integer, StudyNodeInfo> entry : studyPatientsMap.entrySet()) {
            for (Entry<Integer, SearchedPatientInfo> pEntry : entry.getValue().patients
                .entrySet()) {
                try {
                    SearchedPatientInfo patientres = SessionManager
                        .getAppService().doAction(
                            new PatientSearchAction(pEntry.getKey()));
                    pEntry.setValue(patientres);
                } catch (ApplicationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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

    public void removePatient(Integer patientId) {
        List<Integer> studyToRemove = new ArrayList<Integer>();
        for (Entry<Integer, StudyNodeInfo> sentry : studyPatientsMap.entrySet()) {
            SearchedPatientInfo i = sentry.getValue().patients
                .remove(patientId);
            if (sentry.getValue().patients.size() == 0)
                studyToRemove.add(sentry.getKey());
        }
        for (Integer sId : studyToRemove)
            studyPatientsMap.remove(sId);
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

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }

    @Override
    public void setValue(Object value) {
    }

    @Override
    public void clear() {
        removeAll();
        studyPatientsMap.clear();
        rebuild();
    }

    @Override
    protected void runDelete() throws Exception {
    }

}
