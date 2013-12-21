package edu.ualberta.med.biobank.common.action.batchoperation.ceventattr;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpGetResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationEventAttr;
import edu.ualberta.med.biobank.model.EventAttr;

public class CeventAttrBatchOpGetAction implements Action<BatchOpGetResult<EventAttr>> {
    private static final long serialVersionUID = 1L;

    private final Integer batchOperationId;

    public CeventAttrBatchOpGetAction(Integer batchOperationId) {
        this.batchOperationId = batchOperationId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    public BatchOpGetResult<EventAttr> run(ActionContext context) throws ActionException {
        Session session = context.getSession();

        BatchOperation batch = context.load(BatchOperation.class, batchOperationId);

        @SuppressWarnings("unchecked")
        List<BatchOperationEventAttr> batchOpEventAttrs =
            session.createCriteria(BatchOperationEventAttr.class, "boea")
                .add(Restrictions.eq("boea.batch.id", this.batchOperationId))
                .list();

        List<EventAttr> eventAttrs = new ArrayList<EventAttr>();
        for (BatchOperationEventAttr batchOpEventAttr : batchOpEventAttrs) {
            EventAttr eventAttr = batchOpEventAttr.getEventAttr();

            // load other required objects
            eventAttr.getCollectionEvent().getVisitNumber();
            eventAttr.getCollectionEvent().getPatient().getPnumber();
            eventAttr.getStudyEventAttr().getGlobalEventAttr().getLabel();

            eventAttrs.add(eventAttr);
        }

        BatchOpGetResult<EventAttr> result = new BatchOpGetResult<EventAttr>(
            batch, BatchOpActionUtil.getFileMetaData(session, batchOperationId), eventAttrs);

        return result;
    }
}
