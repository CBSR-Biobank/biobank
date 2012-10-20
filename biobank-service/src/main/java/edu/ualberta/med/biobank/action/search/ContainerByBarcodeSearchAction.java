package edu.ualberta.med.biobank.action.search;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ListResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.model.center.Container;

public class ContainerByBarcodeSearchAction implements
    Action<ListResult<Object>> {

    @SuppressWarnings("nls")
    protected static final String CONTAINER_BASE_QRY =
        "SELECT c FROM "
            + Container.class.getName()
            + " c inner join fetch c.site"
            + " where c.productBarcode=? and c.site.id=?";

    private static final long serialVersionUID = 1L;
    private String barcode;

    private Integer currentCenter;

    public ContainerByBarcodeSearchAction(String barcode,
        Integer currentCenter) {
        this.barcode = barcode;
        this.currentCenter = currentCenter;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ContainerReadPermission(currentCenter).isAllowed(context);
    }

    @Override
    public ListResult<Object> run(ActionContext context)
        throws ActionException {
        Query q =
            context.getSession().createQuery(CONTAINER_BASE_QRY);
        q.setParameter(0, barcode);
        q.setParameter(1, currentCenter);
        @SuppressWarnings("unchecked")
        List<Object> rows = q.list();
        return new ListResult<Object>(rows);
    }
}
