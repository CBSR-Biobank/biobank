package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorList;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperation.BatchAction;
import edu.ualberta.med.biobank.model.BatchOperation.BatchInputType;
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
 * This action takes a list of Specimen Batch Operation beans as input, verifies
 * that the data is valid, and if valid saves the data to the database.
 * 
 * @author Nelson Loyola
 * 
 */
@SuppressWarnings("nls")
public class SpecimenBatchOpAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private static final Bundle bundle = new CommonBundle();

    private static Logger log = LoggerFactory
        .getLogger(SpecimenBatchOpAction.class.getName());

    public static final LString CSV_PARENT_SPC_ERROR =
        bundle.tr("specimen declared a source specimen but parent " +
            "inventory ID present").format();

    public static final LString CSV_SPC_PATIENT_ERROR =
        bundle.tr("parent specimen and child specimen "
            + "do not have the same patient number").format();

    public static final LString CSV_SRC_SPC_PATIENT_CEVENT_MISSING_ERROR =
        bundle.tr("both patient number and visit number must be specified")
            .format();

    public static final LString CSV_ALIQ_SPC_PATIENT_CEVENT_WORKSHEET_MISSING_ERROR =
        bundle.tr("when parent inventory id is not specified, "
            + "patient number, visit number, and worksheet are required")
            .format();

    public static final LString CSV_PALLET_POS_ERROR =
        bundle.tr("pallet position defined but not product barcode or label")
            .format();

    public static final LString CSV_PROD_BARCODE_NO_POS_ERROR =
        bundle.tr("pallet product barcode defined but not position").format();

    public static final LString CSV_PALLET_LABEL_NO_POS_ERROR =
        bundle.tr("pallet label defined but not position").format();

    public static final LString CSV_PALLET_LABEL_NO_CTYPE_ERROR =
        bundle.tr("pallet label defined but not container type").format();

    public static final LString CSV_ALIQUOTED_SPC_ERROR =
        bundle.tr("specimen is not a source specimen but parent "
            + "inventory ID is not present").format();

    public static final Tr CSV_PARENT_SPECIMEN_ERROR =
        bundle.tr("parent specimen in CSV file with inventory id " +
            "\"{0}\" does not exist");

    public static final Tr CSV_PARENT_SPECIMEN_NO_PEVENT_ERROR =
        bundle.tr("the parent specimen of specimen with "
            + "inventory id \"{0}\", parent specimen with "
            + "inventory id \"{0}\", does not have a processing event");

    public static final Tr CSV_WAYBILL_ERROR =
        bundle.tr("waybill \"{0}\" does not exist");

    public static final Tr CSV_SPECIMEN_TYPE_ERROR =
        bundle.tr("specimen type with name \"{0}\" does not exist");

    public static final Tr CSV_CONTAINER_LABEL_ERROR =
        bundle.tr("container with label \"{0}\" does not exist");

    public static final Tr CSV_SPECIMEN_LABEL_ERROR =
        bundle
            .tr("specimen position with label \"{0}\" is invalid");

    public static final Tr CSV_PATIENT_ERROR =
        bundle.tr("patient number is missing");

    public static final Tr CSV_PATIENT_DOES_NOT_EXIST_ERROR =
        bundle.tr("patient with number \"{0}\" not exist");

    public static final Tr CSV_PATIENT_MATCH_ERROR =
        bundle.tr("patient with number \"{0}\" "
            + "does not match the source specimen's patient");

    public static final Tr CSV_CEVENT_ERROR =
        bundle
            .tr("collection event for patient {0} with visit number \"{1}\" does not exist");

    public static final Tr CSV_CEVENT_MATCH_ERROR =
        bundle.tr("collection event with visit number \"{0}\" "
            + "does match the source specimen's collection event");

    private final Integer workingCenterId;

    private CompressedReference<ArrayList<SpecimenBatchOpInputPojo>> compressedList =
        null;

    private FileData fileData = null;

    private final Map<String, Specimen> parentSpecimens =
        new HashMap<String, Specimen>(0);

    private final BatchOpInputErrorList errorList = new BatchOpInputErrorList();

    public SpecimenBatchOpAction(Center workingCenter,
        List<SpecimenBatchOpInputPojo> batchOpSpecimens, File inputFile)
        throws NoSuchAlgorithmException, IOException {
        this.workingCenterId = workingCenter.getId();
        this.fileData = FileData.fromFile(inputFile);

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
    public IdResult run(ActionContext context) throws ActionException {
        log.debug("SpecimenBatchOpAction:run");

        if (fileData == null) {
            throw new IllegalStateException("file data is null");
        }

        if (compressedList == null) {
            throw new IllegalStateException("compressed list is null");
        }

        ArrayList<SpecimenBatchOpInputPojo> pojos;

        try {
            pojos = compressedList.get();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

        for (SpecimenBatchOpInputPojo pojo : pojos) {
            validatePojo(pojo);
        }

        if (!errorList.isEmpty()) {
            throw new BatchOpErrorsException(errorList.getErrors());
        }

        // for improved performance, model objects of the same type are loaded
        // sequentially
        // getModelObjects(context, pojos);

        // split pojos into source and aliquoted specimens
        Map<String, SpecimenBatchOpPojoData> pojoDataMap =
            new HashMap<String, SpecimenBatchOpPojoData>(0);

        Set<SpecimenBatchOpPojoData> aliquotSpcPojoData =
            new HashSet<SpecimenBatchOpPojoData>();

        log.debug("SpecimenBatchOpAction: getting DB info");
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            SpecimenBatchOpPojoData pojoData = getDbInfo(context, pojo);
            if (pojoData != null) {
                pojoDataMap.put(pojo.getInventoryId(), pojoData);
                if (pojoData.isAliquotedSpecimen()) {
                    aliquotSpcPojoData.add(pojoData);
                }
            }
        }

        // assign the parent specimen for child specimens
        for (SpecimenBatchOpPojoData info : aliquotSpcPojoData) {
            SpecimenBatchOpPojoData parentPojoData =
                pojoDataMap.get(info.getParentInventoryId());

            if (parentPojoData != null) {
                info.setParentPojoData(parentPojoData);
            }
        }

        if (!errorList.isEmpty()) {
            throw new BatchOpErrorsException(errorList.getErrors());
        }

        BatchOperation batchOp = createBatchOperation(context);

        // add all source specimens first
        log.debug("SpecimenBatchOpAction: adding source specimens");
        for (SpecimenBatchOpPojoData info : pojoDataMap.values()) {
            if (!info.getPojo().getSourceSpecimen()) continue;

            Specimen spc = addSpecimen(context, batchOp, info);
            parentSpecimens.put(spc.getInventoryId(), spc);

            createProcessignEventIfRequired(context, info);

            // TODO: set activity status to closed?
        }

        // now add aliquoted specimens
        log.debug("SpecimenBatchOpAction: adding aliquot specimens");
        for (SpecimenBatchOpPojoData info : aliquotSpcPojoData) {
            if (info.getParentSpecimen() == null) {
                Specimen parentSpc =
                    parentSpecimens.get(info.getParentInventoryId());
                info.setParentSpecimen(parentSpc);
            }

            createProcessignEventIfRequired(context, info);
            addSpecimen(context, batchOp, info);
        }

        log.debug("SpecimenBatchOpAction:end");
        return new IdResult(batchOp.getId());
    }

    private BatchOperation createBatchOperation(ActionContext context) {
        BatchOperation batchOperation = new BatchOperation();

        batchOperation.setInput(fileData);
        batchOperation.setExecutedBy(context.getUser());
        batchOperation.setInputType(BatchInputType.SPECIMEN);
        batchOperation.setAction(BatchAction.INSERT);

        context.getSession().saveOrUpdate(batchOperation);
        return batchOperation;
    }

    private void validatePojo(SpecimenBatchOpInputPojo pojo) {
        if (pojo.getSourceSpecimen()) {
            if ((pojo.getParentInventoryId() != null)) {
                errorList.addError(pojo.getLineNumber(),
                    CSV_PARENT_SPC_ERROR);
            }

            checkForPatientAndCollectionEvent(pojo);
        } else {
            // this is an aliquoted specimen

            if (pojo.getParentInventoryId() == null) {
                checkForPatientAndCollectionEvent(pojo);
            }
        }

        // check if only position defined and no label and no product
        // barcode
        if ((pojo.getPalletProductBarcode() == null)
            && (pojo.getPalletLabel() == null)
            && (pojo.getPalletPosition() != null)) {
            errorList.addError(pojo.getLineNumber(),
                CSV_PALLET_POS_ERROR);
        }

        //
        if ((pojo.getPalletProductBarcode() != null)
            && (pojo.getPalletPosition() == null)) {
            errorList.addError(pojo.getLineNumber(),
                CSV_PROD_BARCODE_NO_POS_ERROR);
        }

        if ((pojo.getPalletLabel() != null)
            && (pojo.getPalletPosition() == null)) {
            errorList.addError(pojo.getLineNumber(),
                CSV_PALLET_POS_ERROR);
        }

        if ((pojo.getPalletLabel() == null)
            && (pojo.getRootContainerType() == null)
            && (pojo.getPalletPosition() != null)) {
            errorList.addError(pojo.getLineNumber(),
                CSV_PALLET_LABEL_NO_CTYPE_ERROR);
        }

    }

    private void checkForPatientAndCollectionEvent(
        SpecimenBatchOpInputPojo csvPojo) {

        if (!csvPojo.hasPatientAndCollectionEvent()) {
            // no parent inventory id and does not have patient number and visit
            // number
            errorList.addError(csvPojo.getLineNumber(),
                CSV_ALIQ_SPC_PATIENT_CEVENT_WORKSHEET_MISSING_ERROR);
        }
    }

    // get referenced items that exist in the database
    private SpecimenBatchOpPojoData getDbInfo(ActionContext context,
        SpecimenBatchOpInputPojo inputPojo) {

        SpecimenBatchOpPojoData pojoData =
            new SpecimenBatchOpPojoData(inputPojo);
        pojoData.setUser(context.getUser());

        Specimen parentSpecimen = null;

        if (inputPojo.getParentInventoryId() != null) {
            parentSpecimen = BatchOpActionUtil.getSpecimen(context,
                inputPojo.getParentInventoryId());

            if (parentSpecimen != null) {
                if (parentSpecimen.getProcessingEvent() == null) {
                    errorList.addError(inputPojo.getLineNumber(),
                        CSV_PARENT_SPECIMEN_NO_PEVENT_ERROR.format(
                            inputPojo.getInventoryId(),
                            parentSpecimen.getInventoryId()));
                }
                pojoData.setParentSpecimen(parentSpecimen);
            }
        }

        Patient patient = null;

        if (pojoData.isSourceSpecimen()) {
            patient = BatchOpActionUtil.getPatient(context,
                inputPojo.getPatientNumber());
            if (patient == null) {
                errorList.addError(inputPojo.getLineNumber(),
                    CSV_PATIENT_ERROR.format());
                return null;
            }
            log.debug("retrieving patient for specimen: invId={} pnumber={}",
                inputPojo.getInventoryId(), inputPojo.getPatientNumber());
            pojoData.setPatient(patient);
        }

        CollectionEvent cevent =
            getAndVerifyCollectionEvent(context, inputPojo, parentSpecimen);

        if (cevent == null) {
            // only aliquoted specimens with no parent require a collection
            // event
            if (pojoData.isAliquotedSpecimen()
                && (pojoData.getParentInventoryId() == null)) {
                errorList.addError(inputPojo.getLineNumber(),
                    CSV_CEVENT_ERROR.format(inputPojo.getPatientNumber(),
                        inputPojo.getVisitNumber()));
            }
        } else {
            pojoData.setCevent(cevent);
            pojoData.setPatient(cevent.getPatient());
        }

        if (inputPojo.getWaybill() != null) {
            OriginInfo originInfo = BatchOpActionUtil.getOriginInfo(context,
                inputPojo.getWaybill());
            if (originInfo == null) {
                errorList.addError(inputPojo.getLineNumber(),
                    CSV_WAYBILL_ERROR.format(inputPojo.getWaybill()));
            } else {
                pojoData.setOriginInfo(originInfo);
            }
        }

        if (inputPojo.getWorksheet() != null) {
            ProcessingEvent pevent = BatchOpActionUtil.getProcessingEvent(
                context, inputPojo.getWorksheet());
            if (pevent != null) {
                pojoData.setPevent(pevent);
                log.trace("found processing event: invId={} worksheet={}",
                    inputPojo.getInventoryId(), inputPojo.getWorksheet());
            }
        }

        SpecimenType spcType = BatchOpActionUtil.getSpecimenType(context,
            inputPojo.getSpecimenType());
        if (spcType == null) {
            errorList.addError(inputPojo.getLineNumber(),
                CSV_SPECIMEN_TYPE_ERROR.format(inputPojo.getSpecimenType()));
        } else {
            pojoData.setSpecimenType(spcType);
        }

        // TODO: replace with pallet product barcode?

        // only get container information if defined for this row
        if (pojoData.hasPosition()) {
            Container container = BatchOpActionUtil.getContainer(context,
                inputPojo.getPalletLabel());
            if (container == null) {
                errorList
                    .addError(inputPojo.getLineNumber(),
                        CSV_CONTAINER_LABEL_ERROR.format(inputPojo
                            .getPalletLabel()));
            } else {
                pojoData.setContainer(container);
            }

            try {
                RowColPos pos =
                    container.getPositionFromLabelingScheme(inputPojo
                        .getPalletPosition());
                pojoData.setSpecimenPos(pos);
            } catch (Exception e) {
                errorList
                    .addError(inputPojo.getLineNumber(),
                        CSV_SPECIMEN_LABEL_ERROR.format(inputPojo
                            .getPalletLabel()));
            }
        }

        return pojoData;
    }

    private Specimen addSpecimen(ActionContext context,
        BatchOperation batchOp, SpecimenBatchOpPojoData info) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }

        OriginInfo originInfo = info.getOriginInfo();
        if (originInfo == null) {
            Center center = (Center) context.getSession()
                .load(Center.class, workingCenterId);
            originInfo = info.getNewOriginInfo(center);
        }

        CollectionEvent cevent = info.getCevent();
        if (cevent == null) {
            // if this is a source specimen then see if the patient has the
            // collection event
            if (info.getPojo().getSourceSpecimen()) {
                cevent = BatchOpActionUtil.getCollectionEvent(context,
                    info.getPojo().getPatientNumber(),
                    info.getPojo().getVisitNumber());

                log.debug(
                    "collection event found: pt={} v#={} invId={}",
                    new Object[] {
                        info.getPojo().getPatientNumber(),
                        info.getPojo().getVisitNumber(),
                        info.getPojo().getInventoryId()
                    });
            } else {
                // if this is an aliquoted specimen, then get the collection
                // event from the source specimen
                cevent = info.getParentSpecimen().getCollectionEvent();
            }

            // if still not found create one
            if (cevent == null) {
                cevent = info.getNewCollectionEvent();
                context.getSession().saveOrUpdate(cevent);
            }

            info.setCevent(cevent);
            info.setPatient(cevent.getPatient());
        }

        Specimen spc = info.getNewSpecimen();

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

    // find the collection event for this specimen
    private CollectionEvent getAndVerifyCollectionEvent(ActionContext context,
        SpecimenBatchOpInputPojo inputPojo, Specimen parentSpecimen) {
        CollectionEvent cevent = null;

        if (inputPojo.getParentInventoryId() == null) {
            if (inputPojo.getPatientNumber() == null) {
                errorList.addError(inputPojo.getLineNumber(),
                    CSV_PATIENT_ERROR.format());
                return null;
            }

            cevent = BatchOpActionUtil.getCollectionEvent(context,
                inputPojo.getPatientNumber(), inputPojo.getVisitNumber());
            return cevent;
        }

        if (parentSpecimen != null) {
            cevent = parentSpecimen.getCollectionEvent();

            // if patient number and visit number present in the pojo
            // ensure they match with the cevent and patient
            if ((inputPojo.getPatientNumber() != null)
                && !cevent.getPatient().getPnumber()
                    .equals(inputPojo.getPatientNumber())) {
                errorList.addError(inputPojo.getLineNumber(),
                    CSV_PATIENT_MATCH_ERROR.format(
                        inputPojo.getPatientNumber()));
            }

            if ((inputPojo.getVisitNumber() != null)
                && !cevent.getVisitNumber().equals(inputPojo.getVisitNumber())) {
                errorList.addError(inputPojo.getLineNumber(),
                    CSV_CEVENT_MATCH_ERROR.format(
                        inputPojo.getVisitNumber()));
            }
        }

        // if parentSpecimen is null, then it comes from the CSV file
        return cevent;
    }

    private ProcessingEvent createProcessignEventIfRequired(
        ActionContext context,
        SpecimenBatchOpPojoData pojoData) {
        ProcessingEvent pevent = null;

        // add the processing event for this source specimen
        if ((pojoData.getPojo().getWorksheet() != null)
            && (pojoData.getPevent() == null)) {
            pevent = pojoData.getNewProcessingEvent();
            context.getSession().saveOrUpdate(pevent);
        }
        return pevent;
    }

    /*
     * Generates an action exception if patient does not exist.
     */
    /*
     * private Patient loadPatient(ActionContext context, String pnumber) { //
     * make sure patient exists Patient p =
     * BatchOpActionUtil.getPatient(context, pnumber); if (p == null) { throw
     * new LocalizedException(
     * CSV_PATIENT_DOES_NOT_EXIST_ERROR.format(pnumber)); } return p; }
     * 
     * private void getModelObjects(ActionContext context,
     * ArrayList<SpecimenBatchOpInputPojo> pojos) { Set<String> patientNumbers =
     * new HashSet<String>(); Set<String> parentInventoryIds = new
     * HashSet<String>();
     * 
     * for (SpecimenBatchOpInputPojo pojo : pojos) {
     * patientNumbers.add(pojo.getPatientNumber());
     * parentInventoryIds.add(pojo.getParentInventoryId()); } }
     */
}
