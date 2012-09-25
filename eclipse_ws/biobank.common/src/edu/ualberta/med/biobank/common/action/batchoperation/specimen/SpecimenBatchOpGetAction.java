package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpGetAction.SpecimenBatchOpGetResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperation.BatchAction;
import edu.ualberta.med.biobank.model.BatchOperation.BatchInputType;
import edu.ualberta.med.biobank.model.BatchOperationSpecimen;
import edu.ualberta.med.biobank.model.FileMetaData;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenBatchOpGetAction
    implements Action<SpecimenBatchOpGetResult> {
    private static final long serialVersionUID = 1L;

    public static class SpecimenBatchOpGetResult
        implements ActionResult {
        private static final long serialVersionUID = 1L;

        private final BatchInputType type;
        private final BatchAction action;
        private final String executedBy;
        private final Date timeExecuted;
        private final FileMetaData input;
        private final List<Specimen> specimens = new ArrayList<Specimen>();

        private SpecimenBatchOpGetResult(BatchOperation batch,
            FileMetaData input, List<Specimen> specimens) {
            this.type = batch.getInputType();
            this.action = batch.getAction();
            this.executedBy = batch.getExecutedBy().getLogin();
            this.timeExecuted = batch.getTimeExecuted();
            this.input = input;
            this.specimens.addAll(specimens);
        }

        public BatchInputType getType() {
            return type;
        }

        public BatchAction getAction() {
            return action;
        }

        public String getExecutedBy() {
            return executedBy;
        }

        public Date getTimeExecuted() {
            return timeExecuted;
        }

        public FileMetaData getInput() {
            return input;
        }

        public List<Specimen> getSpecimens() {
            return specimens;
        }
    }

    private final Integer id;

    private SpecimenBatchOpGetAction(Integer batchOperationId) {
        this.id = batchOperationId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public SpecimenBatchOpGetResult run(ActionContext context)
        throws ActionException {
        Session session = context.getSession();

        BatchOperation batch = (BatchOperation) session
            .load(BatchOperation.class, id);

        FileMetaData meta = (FileMetaData) session
            .createQuery("select bo.input " +
                " from " + BatchOperation.class.getName() + " bo" +
                " where bo.id = ?")
            .setParameter(1, id)
            .uniqueResult();

        @SuppressWarnings("unchecked")
        List<Specimen> specimens = session
            .createQuery("select bos.specimen " +
                " from " + BatchOperationSpecimen.class.getName() + " bos" +
                " where bos.batch.id = ?")
            .setParameter(1, id)
            .list();

        SpecimenBatchOpGetResult result =
            new SpecimenBatchOpGetResult(batch, meta, specimens);

        return result;
    }
}
