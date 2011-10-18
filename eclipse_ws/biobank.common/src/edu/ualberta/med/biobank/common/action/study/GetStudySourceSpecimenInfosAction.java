package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionException;
import edu.ualberta.med.biobank.common.peer.SourceSpecimenPeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.User;

public class GetStudySourceSpecimenInfosAction implements
    Action<ArrayList<SourceSpecimen>> {

    private static final long serialVersionUID = 1L;
    private Integer studyId;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String STUDY_SRCE_SPECIMEN_QRY = 
        "select srce"
        + " from " + SourceSpecimen.class.getName() + " as srce"
        + " inner join fetch srce." + SourceSpecimenPeer.SPECIMEN_TYPE.getName()
        + " where srce." + Property.concatNames(SourceSpecimenPeer.STUDY, StudyPeer.ID) + " =?";
    // @formatter:on

    public GetStudySourceSpecimenInfosAction(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public ArrayList<SourceSpecimen> run(User user, Session session)
        throws ActionException {
        Query query = session.createQuery(STUDY_SRCE_SPECIMEN_QRY);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<SourceSpecimen> rows = query.list();
        return new ArrayList<SourceSpecimen>(rows);
    }
}
