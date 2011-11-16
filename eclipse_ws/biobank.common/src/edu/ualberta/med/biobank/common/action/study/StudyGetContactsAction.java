package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;

public class StudyGetContactsAction implements Action<ArrayList<Contact>> {

    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String HQL =
        "SELECT contacts FROM " + Study.class.getName() + " study"
        + " INNER JOIN study.contactCollection contacts"
        + " WHERE study.id=?";
    // @formatter:on

    private final Integer studyId;

    public StudyGetContactsAction(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public ArrayList<Contact> run(User user, Session session)
        throws ActionException {
        ArrayList<Contact> result = new ArrayList<Contact>();

        Query query = session.createQuery(HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<Contact> rs = query.list();
        if (rs != null) {
            result.addAll(rs);
        }

        return result;
    }
}