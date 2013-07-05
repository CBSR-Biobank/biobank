package edu.ualberta.med.biobank.common.action.search;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.InventoryIdUtil;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenByMicroplateSearchAction implements
    Action<ListResult<String>> {

    @SuppressWarnings("nls")
    protected static final String SPEC_BASE_QRY =
        "SELECT spec.inventoryId FROM " + Specimen.class.getName() + " spec"
            + " WHERE spec.inventoryId LIKE ?";

    private static final long serialVersionUID = 1L;
    private final String microplateId;

    public SpecimenByMicroplateSearchAction(String microplateId) {
        this.microplateId = microplateId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public ListResult<String> run(ActionContext context)
        throws ActionException {
        Query q = context.getSession().createQuery(SPEC_BASE_QRY);
        q.setParameter(0, InventoryIdUtil.patternFromMicroplateId(microplateId));
        @SuppressWarnings("unchecked")
        List<String> rows = q.list();
        return new ListResult<String>(rows);
    }
}
