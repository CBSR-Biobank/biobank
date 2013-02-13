package edu.ualberta.med.biobank.common.action.clinic;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicReadPermission;
import edu.ualberta.med.biobank.model.Clinic;

/**
 * This action is meant to be used to get a quick listing of all the clinics configured for a
 * server. In version 3.2.0 it is used in the administration tree where each node under the
 * "All Clinics" node is a clinic.
 */
public class ClinicGetAllAction implements Action<ListResult<Clinic>> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ClinicReadPermission().isAllowed(context);
    }

    @Override
    public ListResult<Clinic> run(ActionContext context) throws ActionException {
        @SuppressWarnings("unchecked")
        List<Clinic> clinics = context.getSession().createCriteria(Clinic.class).list();
        List<Clinic> readableClinics = new ArrayList<Clinic>(0);
        for (Clinic c : clinics) {
            if (new ClinicReadPermission(c).isAllowed(context)) {
                readableClinics.add(c);
            }
        }
        return new ListResult<Clinic>(readableClinics);
    }

}
