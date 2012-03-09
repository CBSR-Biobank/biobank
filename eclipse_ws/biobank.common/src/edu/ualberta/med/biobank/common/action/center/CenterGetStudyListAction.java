package edu.ualberta.med.biobank.common.action.center;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;

public class CenterGetStudyListAction implements Action<ListResult<Study>> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String STUDIES_QRY =
        "SELECT study"
            + " FROM " + Study.class.getName() + " study"
            + " LEFT JOIN study.sites site"
            + " where site.id=?";

    @SuppressWarnings("nls")
    private static final String CLINICS_QRY =
        "SELECT study"
            + " FROM " + Study.class.getName() + " study"
            + " LEFT JOIN study.contacts contacts"
            + " LEFT JOIN contacts.clinic clinic"
            + " where clinic.id=?";

    final private String queryStr;

    private Integer centerId;

    public CenterGetStudyListAction(Site site) {
        if (site == null) {
            throw new IllegalArgumentException();
        }
        this.centerId = site.getId();
        this.queryStr = STUDIES_QRY;
    }

    public CenterGetStudyListAction(Clinic clinic) {
        if (clinic == null) {
            throw new IllegalArgumentException();
        }
        this.centerId = clinic.getId();
        this.queryStr = CLINICS_QRY;
    }

    public CenterGetStudyListAction(CenterWrapper<?> center) {
        if (center == null) {
            throw new IllegalArgumentException();
        }

        if (center instanceof SiteWrapper) {
            this.queryStr = STUDIES_QRY;
        } else if (center instanceof ClinicWrapper) {
            this.queryStr = CLINICS_QRY;
        } else {
            throw new IllegalArgumentException();
        }

        this.centerId = center.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public ListResult<Study> run(ActionContext context)
        throws ActionException {
        ArrayList<Study> studies = new ArrayList<Study>(0);

        Query query = context.getSession().createQuery(queryStr);
        query.setParameter(0, centerId);

        @SuppressWarnings("unchecked")
        List<Study> results = query.list();
        if (results != null) {
            studies.addAll(results);
        }

        return new ListResult<Study>(studies);
    }
}
