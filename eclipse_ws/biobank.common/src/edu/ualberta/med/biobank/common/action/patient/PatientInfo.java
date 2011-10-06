package edu.ualberta.med.biobank.common.action.patient;

import java.io.Serializable;
import java.util.List;

import edu.ualberta.med.biobank.common.action.cevent.CollectionEventInfo;
import edu.ualberta.med.biobank.common.action.util.InfoUtil;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Patient;

public class PatientInfo implements Serializable, NotAProxy,
    Comparable<PatientInfo> {
    private static final long serialVersionUID = 1L;

    public Patient patient;
    public List<CollectionEventInfo> cevents;
    public Long sourceSpecimenCount;
    public Long aliquotedSpecimenCount;

    @Override
    public int compareTo(PatientInfo info) {
        String nber1 = patient.getPnumber();
        String nber2 = info.patient.getPnumber();
        if (nber1 != null && nber2 != null) {
            return nber1.compareTo(nber2);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PatientInfo) {
            PatientInfo pInfo = (PatientInfo) o;
            if (this == pInfo)
                return true;
            return InfoUtil.equals(patient, pInfo.patient);
        }
        return false;
    }
}
