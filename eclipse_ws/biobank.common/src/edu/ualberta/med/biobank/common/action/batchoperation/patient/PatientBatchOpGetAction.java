package edu.ualberta.med.biobank.common.action.batchoperation.patient;

import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpGetResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationPatient;
import edu.ualberta.med.biobank.model.Patient;

public class PatientBatchOpGetAction
    implements Action<BatchOpGetResult<Patient>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String PATIENT_QRY = "SELECT bos.patient " +
        " FROM " + BatchOperationPatient.class.getName() + " bos" +
        " WHERE bos.batch.id = ?";

    private final Integer id;

    public PatientBatchOpGetAction(Integer batchOperationId) {
        this.id = batchOperationId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public BatchOpGetResult<Patient> run(ActionContext context)
        throws ActionException {
        Session session = context.getSession();

        BatchOperation batch = context.load(BatchOperation.class, id);

        @SuppressWarnings("unchecked")
        List<Patient> patients = session.createQuery(PATIENT_QRY).setParameter(0, id).list();

        BatchOpGetResult<Patient> result = new BatchOpGetResult<Patient>(
            batch, batch.getInput().getMetaData(), patients);

        return result;
    }
}
