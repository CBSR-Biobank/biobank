package edu.ualberta.med.biobank.common.action.search;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Container;

public class ContainerByLabelSearchAction implements
    Action<ListResult<Container>> {

    @SuppressWarnings("nls")
    protected static final String CONTAINER_BASE_QRY =
    "SELECT c FROM "
        + Container.class.getName()
        + " c inner join fetch c.site"
        + " where c.label=? and c.site.id=?";

    private static final long serialVersionUID = 1L;
    private String label;

    private Integer currentCenter;

    public ContainerByLabelSearchAction(String label,
        Integer currentCenter) {
        this.label = label;
        this.currentCenter = currentCenter;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
        // FIXME: ??? what to do
    }

    @Override
    public ListResult<Container> run(ActionContext context)
        throws ActionException {
        Query q =
            context.getSession().createQuery(CONTAINER_BASE_QRY);
        q.setParameter(0, label);
        q.setParameter(1, currentCenter);
        @SuppressWarnings("unchecked")
        List<Container> rows = q.list();
        return new ListResult<Container>(rows);
    }
}
