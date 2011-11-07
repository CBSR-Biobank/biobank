package edu.ualberta.med.biobank.common.action.clinic;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.User;

public class ClinicGetInfoAction  implements Action<ClinicInfo> {
    private static final long serialVersionUID = 1L;
    
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String CLINIC_INFO_HQL = 
        "SELECT clinic,COUNT(DISTINCT patients),COUNT(DISTINCT cevents)"
        + " FROM "+ Clinic.class.getName() + " clinic"
        + " INNER JOIN FETCH clinic.contactCollection"
        + " LEFT JOIN clinic.originInfoCollection oi"
        + " LEFT JOIN oi.specimenCollection spcs"
        + " LEFT JOIN spcs.collectionEvent cevents"
        + " LEFT JOIN cevents.patient patients"
        + " WHERE clinic.id=?";
    // @formatter:on

    private final Integer clinicId;
    private final ClinicGetStudyInfoAction getStudyInfo;
    
    public ClinicGetInfoAction(Integer clinicId) {
        this.clinicId = clinicId;
        this.getStudyInfo = new ClinicGetStudyInfoAction(clinicId);
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public ClinicInfo run(
        User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return null;
    }
   
    
    public static class ClinicInfo implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;

        public Clinic clinic;        
        public Long patientCount;
        public Long ceventCount;
        public List<StudyInfo> studyInfos;
    
        public Clinic getClinic() {
            return clinic;
        }
        public Long getPatientCount() {
            return patientCount;
        }
        public Long getCeventCount() {
            return ceventCount;
        }
        public List<StudyInfo> getStudyInfos() {
            return studyInfos;
        }
        
}
    

}
