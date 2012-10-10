package edu.ualberta.med.biobank.common.action.processingEvent;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventReadPermission;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventGetBriefInfoAction implements
    Action<ProcessingEventBriefInfo> {
    private static final long serialVersionUID = 1L;

    private static final String QRY =
        "SELECT pe, study.nameShort, COUNT(DISTINCT sourceSpecs),"
            + "COUNT(DISTINCT allAlqs) FROM "
            + ProcessingEvent.class.getName() + " pe"
            + " LEFT JOIN pe.specimens sourceSpecs"
            + " LEFT JOIN sourceSpecs.childSpecimens allAlqs"
            + " LEFT JOIN sourceSpecs.collectionEvent.patient.study study"
            + " WHERE pe.id=?";

    private final Integer id;

    public ProcessingEventGetBriefInfoAction(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ProcessingEventReadPermission(id).isAllowed(context);
    }

    @Override
    public ProcessingEventBriefInfo run(ActionContext context)
        throws ActionException {
        Query q = context.getSession().createQuery(QRY);
        q.setParameter(0, id);
        Object[] values = (Object[]) q.list().get(0);
        return new ProcessingEventBriefInfo((ProcessingEvent) values[0],
            (String) values[1], (Long) values[2],
            (Long) values[3]);
    }

}
