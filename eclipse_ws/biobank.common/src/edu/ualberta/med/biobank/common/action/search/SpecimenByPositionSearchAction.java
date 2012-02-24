package edu.ualberta.med.biobank.common.action.search;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
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
        return true;
        // FIXME: ??? what to do
    }

    @Override
    public ListResult<Integer> run(ActionContext context)
        throws ActionException {
        String container =
            positionString.substring(0, positionString.length() - 2);
        String specimen = positionString.substring(positionString.length() - 2);
        Query q =
            context.getSession().createQuery(SPEC_BASE_QRY);
        q.setParameter(0, container);
        q.setParameter(1, specimen);
        q.setParameter(2, currentCenter);
        @SuppressWarnings("unchecked")
        List<Integer> rows = q.list();
        return new ListResult<Integer>(rows);
    }
}
