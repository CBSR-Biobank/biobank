package edu.ualberta.med.biobank.common.action.study;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.Info;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction.StudyInfo;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;

public class StudyGetInfoAction implements Action<StudyInfo> {
    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL = 
        "SELECT study,COUNT(DISTINCT patients),COUNT(DISTINCT cevents)"
        + " FROM "+ Study.class.getName() + " study"
        + " LEFT JOIN study.patientCollection as patients"
        + " LEFT JOIN patients.collectionEventCollection AS cevents"
        + " WHERE study.id = ?";
    // @formatter:on

    private final Integer studyId;
    private final StudyGetClinicInfoAction getClinicInfo;
    private final StudyGetSourceSpecimensAction getSourceSpecimens;
    private final StudyGetAliquotedSpecimensAction getAliquotedSpecimens;
    private final StudyGetStudyEventAttrsAction getStudyEventAttrs;

    public StudyGetInfoAction(Integer studyId) {
        this.studyId = studyId;

        getClinicInfo = new StudyGetClinicInfoAction(studyId);
        getSourceSpecimens = new StudyGetSourceSpecimensAction(studyId);
        getAliquotedSpecimens = new StudyGetAliquotedSpecimensAction(studyId);
        getStudyEventAttrs = new StudyGetStudyEventAttrsAction(studyId);
    }

    public StudyGetInfoAction(Study study) {
        this(study.getId());
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // TODO: implement StudyReadPermission
        // return new StudyReadPermission(studyId).isAllowed(user, session);
        return true;
    }

    @Override
    public StudyInfo run(
        User user, Session session) throws ActionException {
        StudyInfo info = new StudyInfo();

        Query query = session.createQuery(STUDY_INFO_HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 1) {
            Object[] row = rows.get(0);

            info.study = (Study) row[0];
            info.patientCount = (Long) row[1];
            info.ceventCount = (Long) row[2];
            info.clinicInfos = getClinicInfo.run(user, session);
            info.sourceSpcs = getSourceSpecimens.run(user, session);
            info.aliquotedSpcs = getAliquotedSpecimens.run(user, session);
            info.studyEventAttrs = getStudyEventAttrs.run(user, session);
        }

        return info;
    }

    public static class StudyInfo implements Info {
        private static final long serialVersionUID = 1L;

        public Study study;
        public Long patientCount;
        public Long ceventCount;
        public List<ClinicInfo> clinicInfos;
        public List<SourceSpecimen> sourceSpcs;
        public List<AliquotedSpecimen> aliquotedSpcs;
        public List<StudyEventAttr> studyEventAttrs;

        public void setStudy(Study study) {
            this.study = study;
        }

        public void setStudyEventAttrs(List<StudyEventAttr> studyEventAttrs) {
            this.studyEventAttrs = studyEventAttrs;
        }

        public void setClinicInfos(List<ClinicInfo> clinicInfo) {
            this.clinicInfos = clinicInfo;
        }

        public void setSourceSpc(List<SourceSpecimen> sourceSpcs) {
            this.sourceSpcs = sourceSpcs;
        }

        public void setAliquotedSpcTypeIds(
            List<AliquotedSpecimen> aliquotedSpcs) {
            this.aliquotedSpcs = aliquotedSpcs;
        }

        public void setStudyEventattrs(List<StudyEventAttr> attrs) {
            this.studyEventAttrs = attrs;
        }
    }

}
