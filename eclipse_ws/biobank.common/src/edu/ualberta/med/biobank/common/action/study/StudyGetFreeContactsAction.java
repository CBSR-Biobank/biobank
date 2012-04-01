package edu.ualberta.med.biobank.common.action.study;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.study.StudyCreatePermission;
import edu.ualberta.med.biobank.common.permission.study.StudyUpdatePermission;
import edu.ualberta.med.biobank.model.Contact;

public class StudyGetFreeContactsAction implements Action<ListResult<Contact>> {

    @SuppressWarnings("nls")
    private static final String ALL_CONTACTS =
        "FROM " + Contact.class.getName() + " contact"
            + " INNER JOIN FETCH contact.clinic cl"
            + " INNER JOIN FETCH cl.contacts";

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new StudyCreatePermission().isAllowed(context)
            || new StudyUpdatePermission().isAllowed(context);
    }

    @Override
    public ListResult<Contact> run(ActionContext context)
        throws ActionException {
        Query q = context.getSession().createQuery(ALL_CONTACTS);
        @SuppressWarnings("unchecked")
        List<Contact> list = q.list();
        return new ListResult<Contact>(list);
    }
}
