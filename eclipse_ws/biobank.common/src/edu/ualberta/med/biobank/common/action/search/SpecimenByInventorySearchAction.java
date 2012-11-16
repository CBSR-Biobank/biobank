package edu.ualberta.med.biobank.common.action.search;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenReadPermission;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenByInventorySearchAction implements
    Action<ListResult<Integer>> {

    @SuppressWarnings("nls")
    protected static final String SPEC_BASE_QRY =
        "SELECT spec.id FROM " + Specimen.class.getName() + " spec"
            + " WHERE spec.inventoryId=? AND spec.currentCenter.id=?";

    private static final long serialVersionUID = 1L;
    private final String inventoryId;

    private final Integer currentCenter;

    public SpecimenByInventorySearchAction(String inventoryId,
        Integer currentCenter) {
        this.inventoryId = inventoryId;
        this.currentCenter = currentCenter;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenReadPermission(inventoryId).isAllowed(context);
    }

    @Override
    public ListResult<Integer> run(ActionContext context)
        throws ActionException {
        Query q = context.getSession().createQuery(SPEC_BASE_QRY);
        q.setParameter(0, inventoryId);
        q.setParameter(1, currentCenter);
        @SuppressWarnings("unchecked")
        List<Integer> rows = q.list();
        return new ListResult<Integer>(rows);
    }
}
