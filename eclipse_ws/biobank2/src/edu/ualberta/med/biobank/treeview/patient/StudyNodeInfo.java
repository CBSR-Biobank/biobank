package edu.ualberta.med.biobank.treeview.patient;

import java.util.Map;

import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.model.Study;

public class StudyNodeInfo {
    public Study study;
    public Map<Integer, SearchedPatientInfo> patients;

    @Override
    public boolean equals(Object o) {
        if (o instanceof StudyNodeInfo) {
            return study.getId() == ((StudyNodeInfo) o).study.getId();
        }
        return false;
    }
}
