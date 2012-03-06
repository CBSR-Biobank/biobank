package edu.ualberta.med.biobank.common.action.specimenType;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.SpecimenType;

public class SpecimenTypeGetAllAction implements
    Action<ListResult<SpecimenType>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SPEC_TYPE_QRY =
        "SELECT DISTINCT stype FROM " + SpecimenType.class.getName() + " stype"
            + " LEFT JOIN FETCH stype.childSpecimenTypes";

    public SpecimenTypeGetAllAction() {
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public ListResult<SpecimenType> run(ActionContext context)
        throws ActionException {
        ArrayList<SpecimenType> specs = new ArrayList<SpecimenType>();

        Query query = context.getSession().createQuery(SPEC_TYPE_QRY);

        @SuppressWarnings("unchecked")
        List<SpecimenType> results = query.list();
        if (results != null) {
            specs.addAll(results);
        }

        return new ListResult<SpecimenType>(specs);
    }
}
