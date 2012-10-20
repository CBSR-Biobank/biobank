package edu.ualberta.med.biobank.action.processingEvent;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.processingEvent.ProcessingEventReadPermission;
import edu.ualberta.med.biobank.model.center.ProcessingEvent;

public class ProcessingEventGetBriefInfoAction implements
    Action<ProcessingEventBriefInfo> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String QRY =
        "SELECT pe, study.nameShort, COUNT(DISTINCT sourceSpecs)," //$NON-NLS-1$
            + "COUNT(DISTINCT allSpecs) - COUNT(DISTINCT sourceSpecs) FROM " //$NON-NLS-1$
            + ProcessingEvent.class.getName() + " pe" //$NON-NLS-1$
            + " LEFT JOIN pe.specimens sourceSpecs" //$NON-NLS-1$
            + " LEFT JOIN sourceSpecs.childSpecimens allSpecs" //$NON-NLS-1$
            + " LEFT JOIN sourceSpecs.collectionEvent.patient.study study" //$NON-NLS-1$
            + " where pe.id=?"; //$NON-NLS-1$
    private Integer id;

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
