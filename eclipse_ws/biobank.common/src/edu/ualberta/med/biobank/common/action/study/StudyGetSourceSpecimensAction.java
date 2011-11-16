package edu.ualberta.med.biobank.common.action.study;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.study.StudyGetSourceSpecimensAction.Response;
import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.User;

public class StudyGetSourceSpecimensAction implements Action<Response> {

    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String SELECT_SOURCE_SPCS_HQL =
        " FROM " + SourceSpecimen.class.getName() + " AS srce"
        + " INNER JOIN FETCH srce.specimenType"
        + " WHERE srce.study.id=?";
    // @formatter:on

    private final Integer studyId;

    public StudyGetSourceSpecimensAction(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public Response run(User user, Session session)
        throws ActionException {
        Response response = new Response();

        Query query = session.createQuery(SELECT_SOURCE_SPCS_HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<SourceSpecimen> srcspcs = query.list();
        if (srcspcs != null) {
            response.sourceSpecimens.addAll(srcspcs);
        }

        return response;
    }

    public static class Response implements Serializable, NotAProxy {
        private static final long serialVersionUID = 1L;

        private List<SourceSpecimen> sourceSpecimens;

        public Response() {
            sourceSpecimens = new ArrayList<SourceSpecimen>();
        }

        public Response(List<SourceSpecimen> sourceSpecimens) {
            this.sourceSpecimens = sourceSpecimens;
        }

        public List<SourceSpecimen> getSourceSpecimens() {
            return sourceSpecimens;
        }
    }
}
