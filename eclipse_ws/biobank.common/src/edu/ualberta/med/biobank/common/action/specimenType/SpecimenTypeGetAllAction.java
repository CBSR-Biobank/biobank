package edu.ualberta.med.biobank.common.action.specimenType;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.SpecimenType;

public class SpecimenTypeGetInfosAction implements
    Action<ListResult<SpecimenTypeInfo>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SPEC_TYPE_QRY = "from "
        + SpecimenType.class.getName();

    public SpecimenTypeGetInfosAction() {
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return true;
    }

    @Override
    public ListResult<SpecimenTypeInfo> run(ActionContext context)
        throws ActionException {
        ArrayList<SpecimenTypeInfo> specs = new ArrayList<SpecimenTypeInfo>();

        Query query = context.getSession().createQuery(SPEC_TYPE_QRY);

        @SuppressWarnings("unchecked")
        List<SpecimenType> rows = query.list();
        for (SpecimenType row : rows) {
            SpecimenTypeInfo specInfo = new SpecimenTypeInfo();
            specInfo.type = row;
            specs.add(specInfo);
        }

        return new ListResult<SpecimenTypeInfo>(specs);
    }
}
