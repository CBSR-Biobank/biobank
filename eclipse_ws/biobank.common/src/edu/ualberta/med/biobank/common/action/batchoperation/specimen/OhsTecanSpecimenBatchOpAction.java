package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorList;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventSaveAction;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperation.BatchAction;
import edu.ualberta.med.biobank.model.BatchOperation.BatchInputType;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.BatchOperationSpecimen;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.FileData;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * This action takes processing event info and a list of source specimen info
 * (aliquot specimens have already been verified and if valid saved to the database)
 * and creates a processing event and updates source specimens.
 * 
 * @author Brian Allen
 * 
 */
@SuppressWarnings("nls")
public class OhsTecanSpecimenBatchOpAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    private static final Bundle bundle = new CommonBundle();

    private static Logger log = LoggerFactory
        .getLogger(OhsTecanSpecimenBatchOpAction.class.getName());

    private final Integer workingCenterId;

    private final String worksheet;
    
    private final Date timestamp;

    private final String technician;

    private CompressedReference<ArrayList<SpecimenBatchOpInputPojo>> compressedList =
        null;

    private final BatchOpInputErrorList errorList = new BatchOpInputErrorList();

    public OhsTecanSpecimenBatchOpAction(Center workingCenter,
        List<SpecimenBatchOpInputPojo> batchOpSpecimens,
        String worksheet,
        Date timestamp,
        String technician)
        throws NoSuchAlgorithmException, IOException {
        this.worksheet = worksheet;
        this.timestamp = timestamp;
        this.technician = technician;

        this.workingCenterId = workingCenter.getId();
        
        compressedList =
            new CompressedReference<ArrayList<SpecimenBatchOpInputPojo>>(
                new ArrayList<SpecimenBatchOpInputPojo>(batchOpSpecimens));
        
        log.debug("SpecimenBatchOpAction: constructor");
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.BATCH_OPERATIONS.isAllowed(context.getUser());
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        log.debug("OhsTecanSpecimenBatchOpAction:run");
        if (compressedList == null) {
            throw new IllegalStateException("compressed list is null");
        }

        boolean result = false;

        ArrayList<SpecimenBatchOpInputPojo> pojos;

        try {
            pojos = compressedList.get();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

        //context.getSession().getTransaction();

        Set<Integer> addedSpecimenIds = new HashSet<Integer>();
        Set<Integer> removedSpecimenIds = new HashSet<Integer>();
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            Specimen specimen =
                BatchOpActionUtil.getSpecimen(context, pojo.getInventoryId());
            specimen.setQuantity(pojo.getVolume());
            specimen.setActivityStatus(ActivityStatus.CLOSED);
            context.getSession().saveOrUpdate(specimen);
            addedSpecimenIds.add(specimen.getId());
        }

        ProcessingEventSaveAction peventSaveAction =
            new ProcessingEventSaveAction(null, workingCenterId,
                timestamp, worksheet, ActivityStatus.ACTIVE,
                null, addedSpecimenIds, removedSpecimenIds);
        peventSaveAction.run(context);
        
        if (!errorList.isEmpty()) {
            throw new BatchOpErrorsException(errorList.getErrors());
        }

        result = true;
        log.debug("OhsTecanSpecimenBatchOpAction:end");
        return new BooleanResult(result);
    }
}
