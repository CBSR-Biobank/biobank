package edu.ualberta.med.biobank.common.action.request;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.RequestSpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.permission.request.RequestReadPermission;
import edu.ualberta.med.biobank.model.RequestSpecimen;

public class RequestGetSpecimenInfosAction implements Action<ListResult<Object[]>> {

    // @formatter:off
    @SuppressWarnings("nls")
    public static final String TREE_QUERY =
        "SELECT ra, CONCAT(c.path, CONCAT('/', c.id)), c.id, a.id, st.id, sp.id "
            + "FROM " + RequestSpecimen.class.getName() + " ra "
            + "INNER JOIN FETCH ra." + RequestSpecimenPeer.SPECIMEN.getName() + " a "
            + "INNER JOIN FETCH a.collectionEvent ce "
            + "INNER JOIN FETCH ce.patient inner join fetch a." + SpecimenPeer.SPECIMEN_TYPE.getName() + " st "
            + "INNER JOIN FETCH a." + SpecimenPeer.SPECIMEN_POSITION.getName() + " sp "
            + "LEFT JOIN FETCH sp." + SpecimenPositionPeer.CONTAINER.getName() + " c "
            + "LEFT JOIN FETCH c." + ContainerPeer.POSITION.getName() + " cp "
            + "LEFT JOIN FETCH c.topContainer top "
            + "LEFT JOIN FETCH top.containerType ct "
            + "WHERE ra." + RequestSpecimenPeer.REQUEST.getName() + ".id=? "
            + "ORDER BY ra." + RequestSpecimenPeer.STATE.getName();
    // @formatter:on

    private static final long serialVersionUID = 1L;
    private final Integer requestId;

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
        ArrayList<Object[]> specInfos = new ArrayList<Object[]>();

        Query query = context.getSession().createQuery(TREE_QUERY);
        query.setParameter(0, requestId);

        @SuppressWarnings("unchecked")
        List<Object> rows = query.list();
        for (Object row : rows) {
            specInfos.add((Object[]) row);
        }
        return new ListResult<Object[]>(specInfos);
    }
}
