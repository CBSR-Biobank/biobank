package edu.ualberta.med.biobank.common.action.study;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.info.StudyInfo;
import edu.ualberta.med.biobank.common.permission.study.StudyReadPermission;
import edu.ualberta.med.biobank.model.Study;

public class StudyGetInfoAction implements Action<StudyInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String STUDY_INFO_HQL =
        "SELECT DISTINCT study"
            + " FROM " + Study.class.getName() + " study"
            + " LEFT JOIN FETCH study.commentCollection comments"
            + " LEFT JOIN FETCH comments.user"
            + " WHERE study.id = ?";

    @SuppressWarnings("nls")
    private static final String STUDY_COUNT_INFO_HQL =
        "SELECT study,COUNT(DISTINCT patients),COUNT(DISTINCT cevents)"
            + " FROM " + Study.class.getName() + " study"
            + " INNER JOIN FETCH study.activityStatus"
            + " LEFT JOIN study.patientCollection as patients"
            + " LEFT JOIN patients.collectionEventCollection AS cevents"
            + " WHERE study.id = ?";

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
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new StudyReadPermission(studyId).isAllowed(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StudyInfo run(ActionContext context) throws ActionException {
        Query query = context.getSession().createQuery(STUDY_INFO_HQL);
        query.setParameter(0, studyId);

        List<Study> studies = query.list();

        if (studies.size() != 1) {
            throw new ModelNotFoundException(Study.class, studyId);
        }

        StudyInfo studyInfo = new StudyInfo();
        studyInfo.study = studies.get(0);

        query = context.getSession().createQuery(STUDY_COUNT_INFO_HQL);
        query.setParameter(0, studyId);

        List<Object[]> rows = query.list();
        Object[] row = rows.get(0);

        studyInfo.patientCount = (Long) row[1];
        studyInfo.collectionEventCount = (Long) row[2];
        studyInfo.clinicInfos = getClinicInfo.run(context).getList();
        studyInfo.sourceSpcs = getSourceSpecimens.run(context).getList();
        studyInfo.aliquotedSpcs =
            getAliquotedSpecimens.run(context).getList();
        studyInfo.studyEventAttrs =
            getStudyEventAttrs.run(context).getList();

        return studyInfo;
    }
}
