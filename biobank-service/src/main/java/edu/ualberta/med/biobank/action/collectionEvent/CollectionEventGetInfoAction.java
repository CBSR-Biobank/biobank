package edu.ualberta.med.biobank.action.collectionEvent;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.permission.collectionEvent.CollectionEventReadPermission;

public class CollectionEventGetInfoAction implements Action<CEventInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String CEVENT_INFO_QRY =
        "SELECT distinct cevent"
            + " FROM " + CollectionEvent.class.getName() + " cevent"
            + " INNER JOIN FETCH cevent.patient patient"
            + " LEFT JOIN FETCH cevent.comments comments"
            + " LEFT JOIN FETCH comments.user commentsUser"
            + " INNER JOIN FETCH patient.study study"
            + " WHERE cevent.id=?";

    private final Integer ceventId;

    public static class CEventInfo implements ActionResult {

        private static final long serialVersionUID = 1L;
        public CollectionEvent cevent;
        public List<SpecimenInfo> sourceSpecimenInfos;
        public List<SpecimenInfo> aliquotedSpecimenInfos;
        /**
         * Key is the studyeventAttr key this eventAttr refers to
         */
        public Map<Integer, EventAttrInfo> eventAttrs;

    }

    public CollectionEventGetInfoAction(Integer ceventId) {
        this.ceventId = ceventId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new CollectionEventReadPermission(ceventId).isAllowed(context);
    }

    @Override
    public CEventInfo run(ActionContext context) throws ActionException {
        CEventInfo ceventInfo = new CEventInfo();

        Query query = context.getSession().createQuery(CEVENT_INFO_QRY);
        query.setParameter(0, ceventId);

        @SuppressWarnings("unchecked")
        List<CollectionEvent> rows = query.list();
        if (rows.size() != 1) {
            throw new ModelNotFoundException(CollectionEvent.class, ceventId);
        }

        ceventInfo.cevent = rows.get(0);
        ceventInfo.sourceSpecimenInfos =
            new CollectionEventGetSourceSpecimenListInfoAction(ceventId).run(
                context).getList();
        ceventInfo.aliquotedSpecimenInfos =
            new CollectionEventGetAliquotedSpecimenListInfoAction(ceventId)
                .run(
                    context).getList();
        ceventInfo.eventAttrs = new CollectionEventGetEventAttrInfoAction(
            ceventId).run(context).getMap();

        return ceventInfo;
    }

}
