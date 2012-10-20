package edu.ualberta.med.biobank.action.request;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.study.RequestSpecimen;
import edu.ualberta.med.biobank.permission.request.RequestReadPermission;

public class RequestGetSpecimenInfosAction implements
    Action<ListResult<Object[]>> {

    @SuppressWarnings("nls")
    public static final String TREE_QUERY =
        "select ra, concat(c.path, concat('/', c.id)), c.id, a.id, st.id, sp.id "
            + "from " + RequestSpecimen.class.getName() + " ra " +
            "inner join fetch ra.specimen a " +
            "inner join fetch a.collectionEvent ce " +
            "inner join fetch ce.patient " +
            "inner join fetch a.specimenType st " +
            "inner join fetch a.specimenPosition sp " +
            "inner join fetch sp.container c " +
            "inner join fetch c.position cp " +
            "inner join fetch c.topContainer top " +
            "inner join fetch top.containerType ct " +
            "where ra.request.id=? order by ra.state";

    private static final long serialVersionUID = 1L;
    private Integer requestId;

    public RequestGetSpecimenInfosAction(Integer requestId) {
        this.requestId = requestId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new RequestReadPermission().isAllowed(context);
    }

    @Override
    public ListResult<Object[]> run(ActionContext context)
        throws ActionException {
        ArrayList<Object[]> specInfos =
            new ArrayList<Object[]>();

        Query query =
            context.getSession().createQuery(TREE_QUERY);
        query.setParameter(0, requestId);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        for (Object row : rows) {
            specInfos.add((Object[]) row);
        }
        return new ListResult<Object[]>(specInfos);
    }
}
