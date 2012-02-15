package edu.ualberta.med.biobank.common.action.clinic;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetAllAction.ClinicsInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicReadPermission;
import edu.ualberta.med.biobank.model.Clinic;

/**
 * This action is meant to be used to get a quick listing of all the clinics
 * configured for a server. In version 3.2.0 it is used in the administration
 * tree where each node under the "All Clinics" node is a clinic.
 */
public class ClinicGetAllAction implements Action<ClinicsInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String CLINIC_INFO_HQL =
        " FROM " + Clinic.class.getName();

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ClinicReadPermission().isAllowed(context);
    }

    public static class ClinicsInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        private final ArrayList<Clinic> clinics;

        public ClinicsInfo(ArrayList<Clinic> clinics) {
            this.clinics = clinics;
        }

        public ArrayList<Clinic> getClinics() {
            return clinics;
        }

    }

    @Override
    public ClinicsInfo run(ActionContext context) throws ActionException {
        ArrayList<Clinic> clinics = new ArrayList<Clinic>(0);
        Query query = context.getSession().createQuery(CLINIC_INFO_HQL);
        @SuppressWarnings("unchecked")
        List<Clinic> results = query.list();
        if (results != null) {
            clinics.addAll(results);
        }
        return new ClinicsInfo(clinics);
    }

}
