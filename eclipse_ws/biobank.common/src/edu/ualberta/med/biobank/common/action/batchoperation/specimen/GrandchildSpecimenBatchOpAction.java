package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpActionErrors.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorSet;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationSpecimen;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * This action takes a list of grandchild Specimen Batch Operation beans as input, verifies that the
 * data is valid, and if valid saves the data to the database.
 *
 * @author Nelson Loyola
 *
 */
@SuppressWarnings("nls")
public class GrandchildSpecimenBatchOpAction
    extends CommonSpecimenBatchOpAction<GrandchildSpecimenBatchOpInputPojo> {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(SpecimenBatchOpAction.class);

    public static final int SIZE_LIMIT = 1000;

    private final BatchOpInputErrorSet errorSet = new BatchOpInputErrorSet();

    public GrandchildSpecimenBatchOpAction(Center                                  workingCenter,
                                           Set<GrandchildSpecimenBatchOpInputPojo> batchOpSpecimens,
                                           File                                    inputFile)
        throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        super(workingCenter,
            new CompressedReference<ArrayList<GrandchildSpecimenBatchOpInputPojo>>(
              new ArrayList<GrandchildSpecimenBatchOpInputPojo>(batchOpSpecimens)),
            inputFile);
    }

    @Override
    protected void decompressData() {
        super.decompressData();

        try {
            pojos = compressedList.get();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        log.debug("SpecimenBatchOpAction:run");

        if (fileData == null) {
            throw new IllegalStateException("file data is null");
        }
        if (pojos == null) {
            throw new IllegalStateException("pojos were not decompressed");
        }
        if (pojos.isEmpty()) {
            throw new IllegalStateException("pojo list is empty");
        }

        Map<String, GrandchildSpecimenBatchOpInputPojo> pojoMap =
            new HashMap<String, GrandchildSpecimenBatchOpInputPojo>(0);

        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            String inventoryId = pojo.getInventoryId();

            if (pojoMap.containsKey(inventoryId)) {
                throw new IllegalStateException("inventory ID found more than once: " + inventoryId);
            }
            errorSet.addAll(validatePojo(pojo));
            pojoMap.put(inventoryId, pojo);
        }

        if (!errorSet.isEmpty()) {
            throw new BatchOpErrorsException(errorSet.getErrors());
        }

        Map<String, GrandchildSpecimenBatchOpDbInfo> pojoDataMap =
            new HashMap<String, GrandchildSpecimenBatchOpDbInfo>(0);

        log.debug("SpecimenBatchOpAction: getting DB info");
        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            String inventoryId = pojo.getInventoryId();

            Pair<BatchOpInputErrorSet, GrandchildSpecimenBatchOpDbInfo> result =
                getDbInfo(context, pojo);

            BatchOpInputErrorSet errors = result.getLeft();
            if (errors != null) {
                errorSet.addAll(errors);
            }

            GrandchildSpecimenBatchOpDbInfo pojoData = result.getRight();
            if (pojoData != null) {
                pojoDataMap.put(inventoryId, pojoData);
            }
        }

        for (GrandchildSpecimenBatchOpDbInfo pojoData : pojoDataMap.values()) {
            errorSet.addAll(pojoData.validate());
        }

        if (!errorSet.isEmpty()) {
            throw new BatchOpErrorsException(errorSet.getErrors());
        }

        BatchOperation batchOp = BatchOpActionUtil.createBatchOperation(context.getSession(),
                                                                        context.getUser(),
                                                                        fileData);

        // now add aliquoted specimens
        log.debug("SpecimenBatchOpAction: adding aliquot specimens");
        for (GrandchildSpecimenBatchOpDbInfo info : pojoDataMap.values()) {
            addSpecimen(context, batchOp, info);
        }

        log.debug("SpecimenBatchOpAction:end");
        return new IdResult(batchOp.getId());
    }

    private BatchOpInputErrorSet validatePojo(GrandchildSpecimenBatchOpInputPojo pojo) {
        BatchOpInputErrorSet errors = new BatchOpInputErrorSet();

        String parentInventoryId = pojo.getParentInventoryId();
        String patientNumber = pojo.getPatientNumber();

        boolean hasParentInventoryId = (parentInventoryId != null) && !parentInventoryId.isEmpty();
        boolean hasPatientNumberId = (patientNumber != null) && !patientNumber.isEmpty();

        if (!hasParentInventoryId) {
            // should be checked in the Pojo Reader
            errors.addError(pojo.getLineNumber(), CSV_PARENT_SPECIMEN_INVENTORY_ID_REQUIRED_ERROR);
        }

        if (!hasPatientNumberId) {
            // should be checked in the Pojo Reader
            errors.addError(pojo.getLineNumber(), CSV_PATIENT_NUMBER_REQUIRED_ERROR);
        }

        return errors;
    }

    // get referenced items that exist in the database
    private Pair<BatchOpInputErrorSet, GrandchildSpecimenBatchOpDbInfo>
    getDbInfo(ActionContext                      context,
              GrandchildSpecimenBatchOpInputPojo inputPojo) {
        Specimen spc = BatchOpActionUtil.getSpecimen(context.getSession(),
                                                     inputPojo.getInventoryId());
        if (spc != null) {
            return errorResult(inputPojo, SPC_ALREADY_EXISTS_ERROR);
        }

        GrandchildSpecimenBatchOpDbInfo pojoData = new GrandchildSpecimenBatchOpDbInfo(inputPojo);
        String parentInventoryId = inputPojo.getParentInventoryId();
        Specimen parentSpecimen = BatchOpActionUtil.getSpecimen(context.getSession(),
                                                                parentInventoryId);
        if (parentSpecimen == null) {
            return errorResult(inputPojo,
                               CSV_PARENT_SPC_INV_ID_ERROR.format(parentInventoryId));
        }

        SpecimenType spcType = BatchOpActionUtil.getSpecimenType(context.getSession(),
                                                                 inputPojo.getSpecimenType());
        if (spcType == null) {
            return errorResult(inputPojo,
                               CSV_SPECIMEN_TYPE_ERROR.format(inputPojo.getSpecimenType()));
        }

        pojoData.setParentSpecimen(parentSpecimen);
        Patient patient = parentSpecimen.getCollectionEvent().getPatient();

        if (!inputPojo.getPatientNumber().equals(patient.getPnumber())) {
            return errorResult(inputPojo, CSV_PATIENT_NUMBER_MISMATCH_ERROR);
        }

        pojoData.setPatient(patient);
        pojoData.setSpecimenType(spcType);

        Set<SpecimenType> siteAliquotedSpecimenTypes =
            BatchOpActionUtil.getSiteAliquotedSpecimenTypes(patient.getStudy());

        if (!siteAliquotedSpecimenTypes.contains(spcType)) {
            return errorResult(inputPojo,
                               CSV_STUDY_ALIQUOTED_SPC_TYPE_ERROR
                               .format(spcType.getName(),
                                       patient.getStudy().getNameShort()));
        }

        CollectionEvent cevent = parentSpecimen.getCollectionEvent();
        if (cevent == null) {
            throw new IllegalStateException("collection event is NULL");
        }

        if (inputPojo.getOriginCenter() != null) {
            Center center = BatchOpActionUtil.getCenter(context.getSession(),
                                                        inputPojo.getOriginCenter());
            if (center != null) {
                pojoData.setOriginCenter(center);
                log.trace("found origin center: center={}", inputPojo.getOriginCenter());
            } else {
                return errorResult(inputPojo,
                                   CSV_ORIGIN_CENTER_SHORT_NAME_ERROR
                                   .format(inputPojo.getOriginCenter()));
            }
        }

        if (inputPojo.getCurrentCenter() != null) {
            Center center = BatchOpActionUtil.getCenter(context.getSession(), inputPojo.getCurrentCenter());
            if (center != null) {
                pojoData.setCurrentCenter(center);
                log.trace("found current center: center={}", inputPojo.getCurrentCenter());
            } else {
                return errorResult(inputPojo,
                                   CSV_CURRENT_CENTER_SHORT_NAME_ERROR
                                   .format(inputPojo.getCurrentCenter()));
            }
        }

        // only get container information if defined for this row

        if (inputPojo.hasPositionInfo()) {
            Pair<BatchOpInputErrorSet, SpecimenPositionPojoData> validation =
                validatePositionInfo(context.getSession(),
                                     inputPojo,
                                     inputPojo.getSpecimenType());

            BatchOpInputErrorSet validationErrors = validation.getLeft();
            if ((validationErrors != null) && !validationErrors.isEmpty()) {
                return Pair.of(validationErrors, null);
            }

            SpecimenPositionPojoData info = validation.getRight();
            pojoData.setContainer(info.container);
            pojoData.setSpecimenPos(info.specimenPosition);
        }

        return Pair.of(null, pojoData);
    }

    private Specimen addSpecimen(ActionContext                    context,
                                 BatchOperation                   batchOp,
                                 GrandchildSpecimenBatchOpDbInfo pojoData) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }

        if (workingCenterOnServerSide == null) {
            // workingCenterOnServerSide is assigned when isAllowed() is called
            throw new IllegalStateException("workingCenterOnServerSide is null");
        }

        Center originCenter = (pojoData.getOriginCenter() != null)
            ? pojoData.getOriginCenter() : workingCenterOnServerSide;

        OriginInfo originInfo = pojoData.getOriginInfo();
        if (originInfo == null) {
            originInfo = pojoData.createNewOriginInfo(originCenter);
        }

        CollectionEvent cevent = pojoData.getCevent();
        pojoData.setPatient(cevent.getPatient());

        Specimen spc = createSpecimen(context,
                                      pojoData.getPojo().getInventoryId(),
                                      pojoData.getParentSpecimen(),
                                      cevent,
                                      null,
                                      pojoData.getSpecimenType(),
                                      false,
                                      pojoData.getCurrentCenter(),
                                      pojoData.getOriginInfo(),
                                      pojoData.getPojo().getCreatedAt(),
                                      pojoData.getPojo().getVolume(),
                                      pojoData.getPojo().getComment(),
                                      pojoData.getContainer(),
                                      pojoData.getSpecimenPos());

        context.getSession().save(spc.getOriginInfo());
        context.getSession().save(spc);

        BatchOperationSpecimen batchOpSpc = new BatchOperationSpecimen();
        batchOpSpc.setBatch(batchOp);
        batchOpSpc.setSpecimen(spc);
        context.getSession().save(batchOpSpc);

        return spc;
    }

    //
    // Used by getDbInfo to return a result.
    //
    private static Pair<BatchOpInputErrorSet, GrandchildSpecimenBatchOpDbInfo>
    errorResult(GrandchildSpecimenBatchOpInputPojo pojo, LString error) {
        BatchOpInputErrorSet errors = new BatchOpInputErrorSet();
        errors.addError(pojo.getLineNumber(), error);
        return Pair.of(errors, null);

    }

}
