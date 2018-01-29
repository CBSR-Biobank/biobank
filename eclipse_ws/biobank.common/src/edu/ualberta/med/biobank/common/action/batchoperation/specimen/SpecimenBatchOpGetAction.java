package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpGetResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationSpecimen;
import edu.ualberta.med.biobank.model.Specimen;

/**
 * Returns the specimens that were added by a specimen CSV import.
 *
 * <p>
 * Works for both {@link SpecimenBatchOpAction} and GrandchildSpecimenBatchOpAction.
 *
 * @author nelson
 *
 */
public class SpecimenBatchOpGetAction
    implements Action<BatchOpGetResult<Specimen>> {
    private static final long serialVersionUID = 1L;

    private final Integer id;

    public SpecimenBatchOpGetAction(Integer batchOperationId) {
        this.id = batchOperationId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public BatchOpGetResult<Specimen> run(ActionContext context)
        throws ActionException {
        Session session = context.getSession();
        BatchOperation batch = context.load(BatchOperation.class, id);
        List<Specimen> specimens = getSpecimens(id, session);
        BatchOpGetResult<Specimen> result = new BatchOpGetResult<Specimen>(
            batch, BatchOpActionUtil.getFileMetaData(session, id), specimens);

        return result;
    }

    @SuppressWarnings("nls")
    public static List<Specimen> getSpecimens(Integer batchOperationId, Session session) {
        @SuppressWarnings("unchecked")
        List<Specimen> specimens = session
            .createCriteria(BatchOperationSpecimen.class, "batchOpSpecimen")
            .setProjection(Projections.property("specimen"))
            .add(Restrictions.eq("batchOpSpecimen.batch.id", batchOperationId))
            .list();
        return specimens;
    }
}
