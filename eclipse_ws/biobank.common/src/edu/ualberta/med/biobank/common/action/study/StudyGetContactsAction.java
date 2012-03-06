package edu.ualberta.med.biobank.common.action.study;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;

public class StudyGetContactsAction implements Action<ListResult<Contact>> {

    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String HQL =
        "SELECT contacts FROM " + Study.class.getName() + " study"
        + " INNER JOIN study.contacts contacts"
        + " WHERE study.id=?";
    // @formatter:on

    private final Integer studyId;

    public StudyGetContactsAction(Integer studyId) {
        this.studyId = studyId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public ListResult<Contact> run(ActionContext context)
        throws ActionException {
        ArrayList<Contact> result = new ArrayList<Contact>();

        Query query = context.getSession().createQuery(HQL);
        query.setParameter(0, studyId);

        @SuppressWarnings("unchecked")
        List<Contact> rs = query.list();
        if (rs != null) {
            result.addAll(rs);
        }

        return new ListResult<Contact>(result);
    }
}