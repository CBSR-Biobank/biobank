package edu.ualberta.med.biobank.common.action.search;

import java.util.ArrayList;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenSiteReadPermission;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenByPositionSearchAction implements
    Action<ListResult<Integer>> {

    @SuppressWarnings("nls")
    protected static final String SPEC_BASE_QRY =
        "SELECT distinct spec.id FROM "
            + Specimen.class.getName()
            + " spec"
            + " where spec.specimenPosition.container.label=? and spec.specimenPosition.positionString=? and spec.currentCenter.id=?";

    private static final long serialVersionUID = 1L;
    private String positionString;

    private Integer currentCenter;

    public SpecimenByPositionSearchAction(String positionString,
        Integer currentCenter) {
        this.positionString = positionString;
        this.currentCenter = currentCenter;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenSiteReadPermission(currentCenter).isAllowed(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<Integer> run(ActionContext context)
        throws ActionException {
        if (positionString.length() > 2) {
            String container =
                positionString.substring(0, positionString.length() - 2);
            String specimen =
                positionString.substring(positionString.length() - 2);
            Query q =
                context.getSession().createQuery(SPEC_BASE_QRY);
            q.setParameter(0, container);
            q.setParameter(1, specimen);
            q.setParameter(2, currentCenter);
            return new ListResult<Integer>(q.list());
        }
        return new ListResult<Integer>(new ArrayList<Integer>());
    }
}
