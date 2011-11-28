package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.permission.study.StudyReadPermission;
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
        + " LEFT JOIN FETCH study.activityStatus"
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
        return new StudyReadPermission(studyId).isAllowed(user, session);
    }

    @Override
    public StudyInfo run(
        User user, Session session) throws ActionException {
        Query query = session.createQuery(STUDY_INFO_HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 1) {
            Object[] row = rows.get(0);

            StudyInfo info = new StudyInfo(
                (Study) row[0], (Long) row[1], (Long) row[2],
                getClinicInfo.run(user, session).getList(),
                getSourceSpecimens.run(user, session).getList(),
                getAliquotedSpecimens.run(user, session).getList(),
                getStudyEventAttrs.run(user, session).getList());

            return info;
        }
        return null;
    }

    public static class StudyInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public final Study study;
        public final Long patientCount;
        public final Long ceventCount;
        public final List<ClinicInfo> clinicInfos;
        public final List<SourceSpecimen> sourceSpcs;
        public final List<AliquotedSpecimen> aliquotedSpcs;
        public final List<StudyEventAttr> studyEventAttrs;

        public StudyInfo() {
            this.study = null;
            this.patientCount = 0L;
            this.ceventCount = 0L;
            this.clinicInfos = new ArrayList<ClinicInfo>();
            this.sourceSpcs = new ArrayList<SourceSpecimen>();
            this.aliquotedSpcs = new ArrayList<AliquotedSpecimen>();
            this.studyEventAttrs = new ArrayList<StudyEventAttr>();
        }

        public StudyInfo(Study study, Long patientCount, Long ceventCount,
            List<ClinicInfo> clinicInfos,
            List<SourceSpecimen> sourceSpcs,
            List<AliquotedSpecimen> aliquotedSpcs,
            List<StudyEventAttr> studyEventAttrs) {
            this.study = study;
            this.patientCount = patientCount;
            this.ceventCount = ceventCount;
            this.clinicInfos = clinicInfos;
            this.sourceSpcs = sourceSpcs;
            this.aliquotedSpcs = aliquotedSpcs;
            this.studyEventAttrs = studyEventAttrs;
        }

        public Study getStudy() {
            return study;
        }

        public List<ClinicInfo> getClinicInfos() {
            return clinicInfos;
        }

        public List<SourceSpecimen> getSourceSpcs() {
            return sourceSpcs;
        }

        public List<AliquotedSpecimen> getAliquotedSpcs() {
            return aliquotedSpcs;
        }

        public List<StudyEventAttr> getStudyEventAttrs() {
            return studyEventAttrs;
        }
    }

}
