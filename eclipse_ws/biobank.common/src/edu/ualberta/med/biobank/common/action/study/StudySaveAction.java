package edu.ualberta.med.biobank.common.action.study;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;

public class StudySaveAction implements Action<Integer> {
    private static final long serialVersionUID = 1L;

    private Integer id = null;
    private String name;
    private String nameShort;
    private Integer aStatusId;
    private List<StudyEventAttr> studyEventAttr;
    private Set<Integer> contactIds;
    private Set<Integer> sourceSpecimenIds;
    private Set<Integer> aliquotedSpcTypesIds;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public void setActivityStatusId(Integer activityStatusId) {
        this.aStatusId = activityStatusId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        Study study = sessionUtil.get(Study.class, id, new Study());

        // TODO: check permission? (can edit site?)

        // TODO: error checks
        // TODO: version check?

        // TODO: LocalizedMessage in Exception?

        study.setName(name);
        study.setNameShort(nameShort);

        ActivityStatus aStatus = sessionUtil.get(ActivityStatus.class,
            aStatusId);
        study.setActivityStatus(aStatus);

        session.saveOrUpdate(study);
        session.flush();

        return study.getId();
    }
}
