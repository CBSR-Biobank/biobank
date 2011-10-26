package edu.ualberta.med.biobank.common.action.specimen;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.User;

public class GetSpecimenTypeInfosAction implements
    Action<ArrayList<SpecimenTypeInfo>> {
    private static final long serialVersionUID = 1L;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String SPEC_TYPE_QRY = "from "
        + SpecimenType.class.getName();

    // @formatter:on

    public GetSpecimenTypeInfosAction() {
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return true;
    }

    @Override
    public ArrayList<SpecimenTypeInfo> run(User user, Session session)
        throws ActionException {
        ArrayList<SpecimenTypeInfo> specs = new ArrayList<SpecimenTypeInfo>();

        Query query = session.createQuery(SPEC_TYPE_QRY);

        @SuppressWarnings("unchecked")
        List<SpecimenType> rows = query.list();
        for (SpecimenType row : rows) {
            SpecimenTypeInfo specInfo = new SpecimenTypeInfo();
            specInfo.type = row;
            specs.add(specInfo);
        }

        return specs;
    }
}
