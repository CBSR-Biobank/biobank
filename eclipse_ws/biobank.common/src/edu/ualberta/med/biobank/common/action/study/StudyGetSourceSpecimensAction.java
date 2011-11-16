package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.SourceSpecimenPeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.User;

public class StudyGetSourceSpecimensAction implements
    Action<ListResult<SourceSpecimen>> {

    private static final long serialVersionUID = 1L;
    private Integer studyId;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String SELECT_SOURCE_SPCS_HQL =
        " FROM " + SourceSpecimen.class.getName() + " AS srce"
        + " INNER JOIN FETCH srce."
        + SourceSpecimenPeer.SPECIMEN_TYPE.getName()
        + " WHERE srce."
        + Property.concatNames(SourceSpecimenPeer.STUDY, StudyPeer.ID)
        + " =?";
    // @formatter:on

    public StudyGetSourceSpecimensAction(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public ListResult<SourceSpecimen> run(User user, Session session)
        throws ActionException {
        ArrayList<SourceSpecimen> result = new ArrayList<SourceSpecimen>();

        Query query = session.createQuery(SELECT_SOURCE_SPCS_HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<SourceSpecimen> srcspcs = query.list();
        if (srcspcs != null) {
            result.addAll(srcspcs);
        }

        return new ListResult<SourceSpecimen>(result);
    }
}
