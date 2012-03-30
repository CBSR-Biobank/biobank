package edu.ualberta.med.biobank.common.action.processingEvent;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction.PEventInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventReadPermission;
import edu.ualberta.med.biobank.model.ProcessingEvent;

public class ProcessingEventGetInfoAction implements Action<PEventInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String PEVENT_INFO_QRY =
        "SELECT distinct pevent"
            + " FROM " + ProcessingEvent.class.getName() + " pevent"
            + " INNER JOIN FETCH pevent.center"
            + " LEFT JOIN FETCH pevent.comments comments"
            + " LEFT JOIN FETCH comments.user"
            + " WHERE pevent.id=?";

    public static class PEventInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        public ProcessingEvent pevent;
        public List<SpecimenInfo> sourceSpecimenInfos;
    }

    private final Integer peventId;

    public ProcessingEventGetInfoAction(Integer peventId) {
        this.peventId = peventId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ProcessingEventReadPermission(null).isAllowed(context);
    }

    @Override
    public PEventInfo run(ActionContext context) throws ActionException {
        PEventInfo peventInfo = new PEventInfo();

        Query query = context.getSession().createQuery(PEVENT_INFO_QRY);
        query.setParameter(0, peventId);

        @SuppressWarnings("unchecked")
        List<ProcessingEvent> rows = query.list();
        if (rows.size() != 1) {
            throw new ModelNotFoundException(ProcessingEvent.class, peventId);
        }

        peventInfo.pevent = rows.get(0);
        peventInfo.sourceSpecimenInfos =
            new ProcessingEventGetSourceSpecimenListInfoAction(peventId).run(
                context).getList();

        return peventInfo;
    }

}
