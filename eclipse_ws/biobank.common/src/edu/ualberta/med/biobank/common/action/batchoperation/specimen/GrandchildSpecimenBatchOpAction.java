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
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationSpecimen;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;
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

        Map<String, GrandchildSpecimenBatchOpBuilder> pojoDataMap =
            new HashMap<String, GrandchildSpecimenBatchOpBuilder>(0);

        log.debug("SpecimenBatchOpAction: getting DB info");
        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            String inventoryId = pojo.getInventoryId();

            Pair<BatchOpException<LString>, GrandchildSpecimenBatchOpBuilder> result =
                getDbInfo(context, pojo);

            BatchOpException<LString> error = result.getLeft();
            if (error != null) {
                errorSet.addError(error.getLineNumber(), error.getMessage());

            }

            GrandchildSpecimenBatchOpBuilder pojoData = result.getRight();
            if (pojoData != null) {
                pojoDataMap.put(inventoryId, pojoData);
            }
        }

        for (GrandchildSpecimenBatchOpBuilder pojoData : pojoDataMap.values()) {
            boolean valid = pojoData.validate();
            if (!valid) {
                errorSet.addAll(pojoData.getErrorList());
            }
        }

        if (!errorSet.isEmpty()) {
            throw new BatchOpErrorsException(errorSet.getErrors());
        }

        BatchOperation batchOp = BatchOpActionUtil.createBatchOperation(context.getSession(),
                                                                        context.getUser(),
                                                                        fileData);

        // now add aliquoted specimens
        log.debug("SpecimenBatchOpAction: adding aliquot specimens");
        for (GrandchildSpecimenBatchOpBuilder info : pojoDataMap.values()) {
            addSpecimen(context, batchOp, info);
        }

        log.debug("SpecimenBatchOpAction:end");
        return new IdResult(batchOp.getId());
    }

    private BatchOpInputErrorSet validatePojo(GrandchildSpecimenBatchOpInputPojo pojo) {
        BatchOpInputErrorSet errors = new BatchOpInputErrorSet();

        String parentInventoryId = pojo.getParentInventoryId();
        String patientNumber = pojo.getPatientNumber();
        String productBarcode = pojo.getPalletProductBarcode();
        String label = pojo.getPalletLabel();
        String position = pojo.getPalletPosition();
        String rootContainerType = pojo.getRootContainerType();

        boolean hasParentInventoryId = (parentInventoryId != null) && !parentInventoryId.isEmpty();
        boolean hasPatientNumberId = (patientNumber != null) && !patientNumber.isEmpty();
        boolean hasLabel = (label != null) && !label.isEmpty();
        boolean hasProductBarcode = (productBarcode != null) && !productBarcode.isEmpty();
        boolean hasPosition = (position != null) && !position.isEmpty();
        boolean hasRootContainerType = (rootContainerType != null) && !rootContainerType.isEmpty();

        if (!hasParentInventoryId) {
            // should be checked in the Pojo Reader
            errors.addError(pojo.getLineNumber(), CSV_PARENT_SPECIMEN_INVENTORY_ID_REQUIRED_ERROR);
        }

        if (!hasPatientNumberId) {
            // should be checked in the Pojo Reader
            errors.addError(pojo.getLineNumber(), CSV_PATIENT_NUMBER_REQUIRED_ERROR);
        }

        if (hasPosition && !hasProductBarcode && !hasLabel) {
            errors.addError(pojo.getLineNumber(), CSV_PALLET_POS_ERROR);
        }

        if (hasProductBarcode && !hasLabel && !hasPosition) {
            errors.addError(pojo.getLineNumber(), CSV_PROD_BARCODE_NO_POS_ERROR);
        }

        if (hasLabel && !hasProductBarcode && !hasPosition) {
            errors.addError(pojo.getLineNumber(), CSV_PALLET_POS_ERROR);
        }

        if (hasLabel && hasPosition && !hasRootContainerType) {
            errors.addError(pojo.getLineNumber(), CSV_PALLET_LABEL_NO_CTYPE_ERROR);
        }

        return errors;
    }

    // get referenced items that exist in the database
    private Pair<BatchOpException<LString>, GrandchildSpecimenBatchOpBuilder>
    getDbInfo(ActionContext                      context,
              GrandchildSpecimenBatchOpInputPojo inputPojo) {
        Specimen spc = BatchOpActionUtil.getSpecimen(context.getSession(),
                                                     inputPojo.getInventoryId());
        if (spc != null) {
            return errorResult(inputPojo, SPC_ALREADY_EXISTS_ERROR);
        }

        GrandchildSpecimenBatchOpBuilder pojoData = new GrandchildSpecimenBatchOpBuilder(inputPojo);
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
        pojoData.setUser(context.getUser());
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

        if (pojoData.hasPosition()) {
            String position = inputPojo.getPalletPosition();
            String label = inputPojo.getPalletLabel();
            String barcode = inputPojo.getPalletProductBarcode();
            boolean hasLabel = (label != null) && !label.isEmpty();
            Container container;

            if (hasLabel) {
                container = BatchOpActionUtil.getContainer(context.getSession(), label);

                if (container == null) {
                    return errorResult(inputPojo, CSV_CONTAINER_LABEL_ERROR.format(label));
                }
            } else {
                container = BatchOpActionUtil.getContainerByBarcode(context.getSession(), barcode);

                if (container == null) {
                    return errorResult(inputPojo, CSV_CONTAINER_BARCODE_ERROR.format(barcode));
                }
            }

            pojoData.setContainer(container);

            if (!container.getContainerType().getSpecimenTypes().contains(spcType)) {
                return errorResult(inputPojo,
                                   CSV_CONTAINER_SPC_TYPE_ERROR.format(spcType.getName()));
            }

            try {
                RowColPos pos = container.getPositionFromLabelingScheme(position);

                // is container position empty?
                if (!container.isPositionFree(pos)) {
                    if (hasLabel) {
                        return errorResult(inputPojo,
                                           CSV_LABEL_POS_OCCUPIED_ERROR.format(position, label));
                    }
                    return errorResult(inputPojo,
                                       CSV_CONTAINER_POS_OCCUPIED_ERROR.format(position, barcode));
                }

                pojoData.setSpecimenPos(pos);
            } catch (Exception e) {
                if (hasLabel) {
                    return errorResult(inputPojo,
                                       CSV_LABEL_POS_OCCUPIED_ERROR.format(position, label));
                }
                return errorResult(inputPojo,
                                   CSV_CONTAINER_POS_OCCUPIED_ERROR.format(position, barcode));
            }

        }

        return Pair.of(null, pojoData);
    }

    private Specimen addSpecimen(ActionContext                    context,
                                 BatchOperation                   batchOp,
                                 GrandchildSpecimenBatchOpBuilder pojoData) {
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

        Specimen spc = pojoData.createNewSpecimen();

        // check if this specimen has a comment and if so save it to DB
        if (!spc.getComments().isEmpty()) {
            Comment comment = spc.getComments().iterator().next();
            context.getSession().save(comment);
        }

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
    private static Pair<BatchOpException<LString>, GrandchildSpecimenBatchOpBuilder>
    errorResult(GrandchildSpecimenBatchOpInputPojo pojo, LString error) {
        return Pair.of(new BatchOpException<LString>(pojo.getLineNumber(), error), null);

    }

}
