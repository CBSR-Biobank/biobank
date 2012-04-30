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

public class RequestGetSpecimenInfosAction implements
    Action<ListResult<Object[]>> {

    @SuppressWarnings("nls")
    public static final String TREE_QUERY =
        "select ra, concat(c.path, concat('/', c.id)), c.id, a.id, st.id, sp.id from " //$NON-NLS-1$
            + RequestSpecimen.class.getName()
            + " ra inner join fetch ra." //$NON-NLS-1$
            + RequestSpecimenPeer.SPECIMEN.getName()
            + " a inner join fetch a.collectionEvent" //$NON-NLS-1$
            + " ce inner join fetch ce.patient inner join fetch a."
            + SpecimenPeer.SPECIMEN_TYPE.getName()
            + " st inner join fetch a." //$NON-NLS-1$
            + SpecimenPeer.SPECIMEN_POSITION.getName()
            + " sp inner join fetch sp." //$NON-NLS-1$
            + SpecimenPositionPeer.CONTAINER.getName()
            + " c inner join fetch c."
            + ContainerPeer.POSITION.getName()
            + " cp inner join fetch c.topContainer "
            + "top inner join fetch top.containerType ct where ra." //$NON-NLS-1$
            + RequestSpecimenPeer.REQUEST.getName() + ".id=? order by ra." //$NON-NLS-1$
            + RequestSpecimenPeer.STATE.getName();

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
