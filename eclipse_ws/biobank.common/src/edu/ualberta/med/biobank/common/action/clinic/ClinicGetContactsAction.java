package edu.ualberta.med.biobank.common.action.clinic;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.User;

public class ClinicGetContactsAction implements Action<ListResult<Contact>> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String HQL =
        "SELECT contact "
            + " FROM " + Contact.class.getName() + " contact"
            + " INNER JOIN contact.clinic clinic"
            + " WHERE clinic.id=?";

    private final Integer clinicId;

    public ClinicGetContactsAction(Integer clinicId) {
        this.clinicId = clinicId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public ListResult<Contact> run(User user, Session session)
        throws ActionException {
        ArrayList<Contact> result = new ArrayList<Contact>();

        Query query = session.createQuery(HQL);
        query.setParameter(0, clinicId);

        @SuppressWarnings("unchecked")
        List<Contact> rs = query.list();
        if (rs != null) {
            result.addAll(rs);
        }
        return new ListResult<Contact>(result);
    }
}
