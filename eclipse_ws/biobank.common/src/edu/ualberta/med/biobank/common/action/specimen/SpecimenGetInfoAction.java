package edu.ualberta.med.biobank.common.action.specimen;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenReadPermission;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenGetInfoAction implements Action<SpecimenBriefInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SPECIMEN_INFO_HQL =
        "FROM " + Specimen.class.getName() + " spc"
            + " WHERE spc.id=?";

    public static class SpecimenBriefInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public Specimen specimen;

        public SpecimenBriefInfo(Specimen specimen) {
            this.specimen = specimen;
        }

    }

    private final Integer specimenId;

    public SpecimenGetInfoAction(Integer specimenId) {
        this.specimenId = specimenId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenReadPermission(specimenId).isAllowed(context);
    }

    @Override
    public SpecimenBriefInfo run(ActionContext context) throws ActionException {
        Query query = context.getSession().createQuery(SPECIMEN_INFO_HQL);
        query.setParameter(0, specimenId);

        Specimen specimen = (Specimen) query.uniqueResult();
        return new SpecimenBriefInfo(specimen);
    }

}
