package edu.ualberta.med.biobank.common.action.specimen;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenListPermission;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenListNonActiveAction implements
    Action<ListResult<Specimen>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SPECIMENS_NON_ACTIVE_QRY =
        "FROM " + Specimen.class.getName() + " spec"
            + " WHERE spec.currentCenter.id=?"
            + " AND spec.activityStatus.name!=?";

    private final Integer centerId;

    public SpecimenListNonActiveAction(Integer centerId) {
        this.centerId = centerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenListPermission(centerId).isAllowed(context);
    }

    @Override
    public ListResult<Specimen> run(ActionContext context)
        throws ActionException {
        ArrayList<Specimen> specs = new ArrayList<Specimen>();

        Query query =
            context.getSession().createQuery(SPECIMENS_NON_ACTIVE_QRY);

        @SuppressWarnings("unchecked")
        List<Specimen> results = query.list();
        if (results != null) {
            specs.addAll(results);
        }

        return new ListResult<Specimen>(specs);
    }

}
