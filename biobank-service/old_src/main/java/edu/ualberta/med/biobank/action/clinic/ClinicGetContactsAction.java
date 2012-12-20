package edu.ualberta.med.biobank.action.clinic;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Contact;

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
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public ListResult<Contact> run(ActionContext context)
        throws ActionException {
        ArrayList<Contact> result = new ArrayList<Contact>();

        Query query = context.getSession().createQuery(HQL);
        query.setParameter(0, clinicId);

        @SuppressWarnings("unchecked")
        List<Contact> rs = query.list();
        if (rs != null) {
            result.addAll(rs);
        }
        return new ListResult<Contact>(result);
    }
}
