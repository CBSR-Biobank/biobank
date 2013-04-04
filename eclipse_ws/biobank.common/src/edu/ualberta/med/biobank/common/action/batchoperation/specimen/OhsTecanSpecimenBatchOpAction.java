package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorSet;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventSaveAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * This action creates aliquot specimens, creates a processing event and updates source specimens.
 * 
 * @author Brian Allen
 * 
 */
public class OhsTecanSpecimenBatchOpAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory
        .getLogger(OhsTecanSpecimenBatchOpAction.class.getName());

    @SuppressWarnings("nls")
    private static final String PROCESSING_EVENT_COUNT_HQL =
        "SELECT COUNT(*)"
            + " FROM " + ProcessingEvent.class.getName() + " pe"
            + " WHERE pe.worksheet = ?";

    @SuppressWarnings("nls")
    private static final String SPECIMEN_PROCESSED_COUNT_HQL =
        "SELECT COUNT(*)"
            + " FROM " + Specimen.class.getName() + " spec"
            + " WHERE spec.inventoryId = ? AND spec.processingEvent IS NOT NULL";

    private final Integer workingCenterId;

    private final String worksheet;

    private final Date timestamp;

    private final String technician;

    private final CompressedReference<ArrayList<SpecimenBatchOpInputPojo>> sourceCompressedList;

    private final SpecimenBatchOpAction coreAction;

    private final BatchOpInputErrorSet errorSet = new BatchOpInputErrorSet();

    public OhsTecanSpecimenBatchOpAction(Center workingCenter,
        Set<SpecimenBatchOpInputPojo> aliquotBatchOpSpecimens,
        File importFile,
        Set<SpecimenBatchOpInputPojo> sourceBatchOpSpecimens,
        String worksheet,
        Date timestamp,
        String technician) throws NoSuchAlgorithmException, IOException {
        this.worksheet = worksheet;
        this.timestamp = timestamp;
        this.technician = technician;

        this.workingCenterId = workingCenter.getId();

        sourceCompressedList =
            new CompressedReference<ArrayList<SpecimenBatchOpInputPojo>>(
                new ArrayList<SpecimenBatchOpInputPojo>(sourceBatchOpSpecimens));

        this.coreAction = new SpecimenBatchOpAction(workingCenter,
            aliquotBatchOpSpecimens, importFile);

        log.debug("SpecimenBatchOpAction: constructor"); //$NON-NLS-1$
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return this.coreAction.isAllowed(context);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        log.debug("OhsTecanSpecimenBatchOpAction:run"); //$NON-NLS-1$
        if (sourceCompressedList == null) {
            throw new IllegalStateException("source compressed list is null"); //$NON-NLS-1$
        }
        ArrayList<SpecimenBatchOpInputPojo> sourcePojos;
        try {
            sourcePojos = sourceCompressedList.get();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

        // was preExecution
        Query query = null;
        log.debug("run: worksheet={}", worksheet); //$NON-NLS-1$
        query = context.getSession().createQuery(PROCESSING_EVENT_COUNT_HQL);
        query.setParameter(0, worksheet);
        if ((Long) (query.list().get(0)) > 0) {
            throw new ActionException("File name has been used - probably already imported"); //$NON-NLS-1$
        }

        for (SpecimenBatchOpInputPojo sourcePojo : sourcePojos) {
            log.debug("run: inventoryId={}", sourcePojo.getInventoryId()); //$NON-NLS-1$
            query = context.getSession().createQuery(SPECIMEN_PROCESSED_COUNT_HQL);
            query.setParameter(0, sourcePojo.getInventoryId());
            if ((Long) (query.list().get(0)) > 0) {
                throw new ActionException("Source specimen " //$NON-NLS-1$
                    + sourcePojo.getInventoryId() + " has already been processed"); //$NON-NLS-1$
            }
        }
        // end preExecution

        IdResult coreIdResult = coreAction.run(context);

        // was postExecution
        Set<Integer> addedSpecimenIds = new HashSet<Integer>();
        Set<Integer> removedSpecimenIds = new HashSet<Integer>();
        for (SpecimenBatchOpInputPojo sourcePojo : sourcePojos) {
            Specimen specimen = BatchOpActionUtil.getSpecimen(
                context.getSession(), sourcePojo.getInventoryId());
            specimen.setQuantity(sourcePojo.getVolume());
            specimen.setActivityStatus(ActivityStatus.CLOSED);
            context.getSession().saveOrUpdate(specimen);
            addedSpecimenIds.add(specimen.getId());
        }

        Center workingCenter = context.load(Center.class, workingCenterId);

        ProcessingEventSaveAction peventSaveAction =
            new ProcessingEventSaveAction(null, workingCenter,
                timestamp, worksheet, ActivityStatus.ACTIVE,
                null, addedSpecimenIds, removedSpecimenIds, technician);
        peventSaveAction.run(context);
        // end postExecution

        if (!errorSet.isEmpty()) {
            throw new BatchOpErrorsException(errorSet.getErrors());
        }

        log.debug("OhsTecanSpecimenBatchOpAction:end"); //$NON-NLS-1$
        return coreIdResult;
    }
}
