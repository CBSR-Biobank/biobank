package edu.ualberta.med.biobank.common.action.search;

import java.util.ArrayList;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenStudyCenterReadPermission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenByPositionSearchAction implements Action<ListResult<Integer>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    protected static final String SPEC_BASE_QRY =
        "SELECT distinct spec.id FROM " + Specimen.class.getName() + " spec "
            + "WHERE spec.specimenPosition.container.label=? "
            + "AND spec.specimenPosition.positionString=? "
            + "AND spec.currentCenter.id=?";

    private final String positionString;

    private final Integer centerId;

    public SpecimenByPositionSearchAction(String positionString, Center center) {
        this.positionString = positionString;
        this.centerId = center.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenStudyCenterReadPermission(centerId).isAllowed(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<Integer> run(ActionContext context) throws ActionException {
        if (positionString.length() <= 2) {
            return new ListResult<Integer>(new ArrayList<Integer>());
        }

        String container = positionString.substring(0, positionString.length() - 2);
        String specimen = positionString.substring(positionString.length() - 2);
        Query q = context.getSession().createQuery(SPEC_BASE_QRY);
        q.setParameter(0, container);
        q.setParameter(1, specimen);
        q.setParameter(2, centerId);
        return new ListResult<Integer>(q.list());
    }
}
