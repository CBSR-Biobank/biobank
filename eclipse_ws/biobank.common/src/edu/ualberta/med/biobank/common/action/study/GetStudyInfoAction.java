package edu.ualberta.med.biobank.common.action.study;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class GetStudyInfoAction implements Action<Study> {

    private static final long serialVersionUID = 1L;
    private Integer studyId;

    public GetStudyInfoAction(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public Study doAction(Session session) throws ActionException {
        return (Study) session.load(Study.class, studyId);
    }

}
