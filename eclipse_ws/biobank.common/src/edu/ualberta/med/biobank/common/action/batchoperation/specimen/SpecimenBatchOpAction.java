package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationSpecimen;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * This action takes a list of Specimen Batch Operation beans as input, verifies that the data is
 * valid, and if valid saves the data to the database.
 *
 * @author Nelson Loyola
 *
 */
@SuppressWarnings("nls")
public class SpecimenBatchOpAction extends CommonSpecimenBatchOpAction<SpecimenBatchOpInputPojo> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(SpecimenBatchOpAction.class);

    public static final int SIZE_LIMIT = 1000;

    private final Map<String, Specimen> parentSpecimens = new HashMap<String, Specimen>(0);

    private final Map<String, ProcessingEvent> createdProcessingEvents =
        new HashMap<String, ProcessingEvent>(0);

    private final BatchOpInputErrorSet errorSet = new BatchOpInputErrorSet();

    public SpecimenBatchOpAction(Center                        workingCenter,
                                 Set<SpecimenBatchOpInputPojo> batchOpSpecimens,
                                 File                          inputFile)
        throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        super(workingCenter,
              new CompressedReference<ArrayList<SpecimenBatchOpInputPojo>>(
                new ArrayList<SpecimenBatchOpInputPojo>(batchOpSpecimens)),
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

        for (SpecimenBatchOpInputPojo pojo : pojos) {
            validatePojo(pojo);
        }

        if (!errorSet.isEmpty()) {
            throw new BatchOpErrorsException(errorSet.getErrors());
        }

        // for improved performance, model objects of the same type are loaded sequentially
        // getModelObjects(context, pojos);

        Map<String, SpecimenBatchOpInputPojo> pojoMap = new HashMap<String, SpecimenBatchOpInputPojo>(0);
        Map<String, SpecimenBatchOpDbInfo> pojoDataMap = new HashMap<String, SpecimenBatchOpDbInfo>(0);
        Set<SpecimenBatchOpDbInfo> aliquotSpcPojoData = new HashSet<SpecimenBatchOpDbInfo>();

        log.debug("SpecimenBatchOpAction: getting DB info");
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            String inventoryId = pojo.getInventoryId();
            if (pojoMap.containsKey(inventoryId)) {
                throw new IllegalStateException("inventory ID found more than once: " + inventoryId);
            }
            pojoMap.put(inventoryId, pojo);
        }

        for (SpecimenBatchOpInputPojo pojo : pojos) {
            SpecimenBatchOpDbInfo pojoData = getDbInfo(context, pojo,
                pojoMap.get(pojo.getParentInventoryId()));

            if (pojoData != null) {
                if (pojoData.isAliquotedSpecimen()) {
                    aliquotSpcPojoData.add(pojoData);
                }
                pojoDataMap.put(pojo.getInventoryId(), pojoData);
            }
        }

        // assign the parent specimen for child specimens
        for (SpecimenBatchOpDbInfo pojoData : aliquotSpcPojoData) {
            SpecimenBatchOpDbInfo parentPojoData =
                pojoDataMap.get(pojoData.getParentInventoryId());

            if (parentPojoData != null) {
                pojoData.setParentPojoData(parentPojoData);
            }
        }

        for (SpecimenBatchOpDbInfo pojoData : pojoDataMap.values()) {
            Pair<BatchOpInputErrorSet, Boolean> valid = pojoData.validate();
            errorSet.addAll(valid.getLeft());

            if (pojoData.getParentInventoryId() != null) {
                SpecimenBatchOpInputPojo pojo = pojoData.getPojo();

                // ensure that aliquoted specimens with parent specimens already
                // in the database have a collection event
                if ((pojoData.getParentSpecimen() != null)
                    && (pojoData.getParentSpecimen().getCollectionEvent() == null)) {
                    errorSet.addError(pojo.getLineNumber(),
                        SpecimenBatchOpActionErrors.CSV_CEVENT_ERROR.format(pojo.getPatientNumber(), pojo.getVisitNumber()));
                }
            }
        }

        if (!errorSet.isEmpty()) {
            throw new BatchOpErrorsException(errorSet.getErrors());
        }

        BatchOperation batchOp = BatchOpActionUtil.createBatchOperation(context.getSession(),
                                                                        context.getUser(),
                                                                        fileData);

        // add all source specimens first
        log.debug("SpecimenBatchOpAction: adding source specimens");
        for (SpecimenBatchOpDbInfo info : pojoDataMap.values()) {
            if (!info.getPojo().getSourceSpecimen()) continue;

            ProcessingEvent pevent = info.getPevent();
            if (pevent == null) {
                pevent = createProcessignEventIfRequired(context, info);
            }

            Specimen spc = addSpecimen(context, batchOp, info, pevent);
            parentSpecimens.put(spc.getInventoryId(), spc);
            // TODO: set activity status to closed?
        }

        // now add aliquoted specimens
        log.debug("SpecimenBatchOpAction: adding aliquot specimens");
        for (SpecimenBatchOpDbInfo info : aliquotSpcPojoData) {
            if ((info.getParentInventoryId() != null) && (info.getParentSpecimen() == null)) {
                Specimen parentSpc = parentSpecimens.get(info.getParentInventoryId());
                if (parentSpc == null) {
                    errorSet.addError(info.getPojo().getLineNumber(),
                                      SpecimenBatchOpActionErrors.CSV_PARENT_SPC_INV_ID_ERROR
                                          .format(info.getPojo().getParentInventoryId()));
                } else {
                    info.setParentSpecimen(parentSpc);
                }
            }

            if (errorSet.isEmpty()) {
                addSpecimen(context, batchOp, info, createProcessignEventIfRequired(context, info));
            }
        }

        if (!errorSet.isEmpty()) {
            throw new BatchOpErrorsException(errorSet.getErrors());
        }

        log.debug("SpecimenBatchOpAction:end");
        return new IdResult(batchOp.getId());
    }

    private void validatePojo(SpecimenBatchOpInputPojo pojo) {
        if (pojo.getSourceSpecimen()) {
            if ((pojo.getParentInventoryId() != null)) {
                errorSet.addError(pojo.getLineNumber(), SpecimenBatchOpActionErrors.CSV_PARENT_SPC_ERROR);
            }

            checkForPatientAndCollectionEvent(pojo);
        } else {
            // this is an aliquoted specimen

            if (pojo.getParentInventoryId() == null) {
                checkForPatientAndCollectionEvent(pojo);
            }
        }

        String productBarcode = pojo.getPalletProductBarcode();
        String label = pojo.getPalletLabel();
        String position = pojo.getPalletPosition();
        String rootContainerType = pojo.getRootContainerType();

        boolean hasLabel = (label != null) && !label.isEmpty();
        boolean hasProductBarcode = (productBarcode != null) && !productBarcode.isEmpty();
        boolean hasPosition = (position != null) && !position.isEmpty();
        boolean hasRootContainerType = (rootContainerType != null) && !rootContainerType.isEmpty();

        if (hasPosition && !hasProductBarcode && !hasLabel) {
            errorSet.addError(pojo.getLineNumber(), SpecimenBatchOpActionErrors.CSV_PALLET_POS_ERROR);
        }

        if (hasProductBarcode && !hasLabel && !hasPosition) {
            errorSet.addError(pojo.getLineNumber(), SpecimenBatchOpActionErrors.CSV_PROD_BARCODE_NO_POS_ERROR);
        }

        if (hasLabel && !hasProductBarcode && !hasPosition) {
            errorSet.addError(pojo.getLineNumber(), SpecimenBatchOpActionErrors.CSV_PALLET_POS_ERROR);
        }

        if (hasLabel && hasPosition && !hasRootContainerType) {
            errorSet.addError(pojo.getLineNumber(), SpecimenBatchOpActionErrors.CSV_PALLET_LABEL_NO_CTYPE_ERROR);
        }

    }

    // get referenced items that exist in the database
    private SpecimenBatchOpDbInfo getDbInfo(ActionContext context,
                                             SpecimenBatchOpInputPojo inputPojo,
                                             SpecimenBatchOpInputPojo parentInputPojo) {
        Specimen spc = BatchOpActionUtil.getSpecimen(context.getSession(), inputPojo.getInventoryId());
        if (spc != null) {
            errorSet.addError(inputPojo.getLineNumber(),
                SpecimenBatchOpActionErrors.SPC_ALREADY_EXISTS_ERROR);
            return null;
        }

        SpecimenBatchOpDbInfo pojoData = new SpecimenBatchOpDbInfo(inputPojo, parentInputPojo);
        Specimen parentSpecimen = null;

        Patient patient = null;

        if (inputPojo.getParentInventoryId() != null) {
            parentSpecimen = BatchOpActionUtil.getSpecimen(context.getSession(),
                inputPojo.getParentInventoryId());

            if (parentSpecimen != null) {
                pojoData.setParentSpecimen(parentSpecimen);
                patient = parentSpecimen.getCollectionEvent().getPatient();
            }
        }

        SpecimenType spcType = BatchOpActionUtil.getSpecimenType(context.getSession(),
            inputPojo.getSpecimenType());
        if (spcType == null) {
            errorSet.addError(inputPojo.getLineNumber(),
                SpecimenBatchOpActionErrors.CSV_SPECIMEN_TYPE_ERROR.format(inputPojo.getSpecimenType()));
        } else {
            pojoData.setSpecimenType(spcType);
        }

        if (pojoData.isSourceSpecimen()) {
            patient = BatchOpActionUtil.getPatient(context.getSession(), inputPojo.getPatientNumber());
            if (patient == null) {
                errorSet.addError(inputPojo.getLineNumber(),
                    SpecimenBatchOpActionErrors.CSV_PATIENT_NUMBER_INVALID_ERROR.format());
                return null;
            }
            pojoData.setPatient(patient);

            log.debug("retrieving patient for specimen: invId={} pnumber={}",
                inputPojo.getInventoryId(), inputPojo.getPatientNumber());

            Set<SpecimenType> siteSourceSpecimenTypes =
                BatchOpActionUtil.getSiteSourceSpecimenTypes(patient.getStudy());

            if ((spcType != null) && !siteSourceSpecimenTypes.contains(spcType)) {
                errorSet.addError(inputPojo.getLineNumber(),
                    SpecimenBatchOpActionErrors.CSV_STUDY_SOURCE_SPC_TYPE_ERROR.format(
                        spcType.getName(), patient.getStudy().getNameShort()));
                return null;
            }
        } else {
            // ensure child specimen type is allowed for study
            if (inputPojo.getPatientNumber() != null) {
                patient = BatchOpActionUtil.getPatient(
                    context.getSession(), inputPojo.getPatientNumber());
            } else if (parentSpecimen != null) {
                patient = parentSpecimen.getCollectionEvent().getPatient();
            } else if (parentInputPojo != null) {
                patient = BatchOpActionUtil.getPatient(
                    context.getSession(), parentInputPojo.getPatientNumber());
            }

            if (patient != null) {
                Set<SpecimenType> siteAliquotedSpecimenTypes =
                    BatchOpActionUtil.getSiteAliquotedSpecimenTypes(patient.getStudy());

                if ((spcType != null) && !siteAliquotedSpecimenTypes.contains(spcType)) {
                    errorSet.addError(inputPojo.getLineNumber(),
                        SpecimenBatchOpActionErrors.CSV_STUDY_ALIQUOTED_SPC_TYPE_ERROR.format(
                            spcType.getName(), patient.getStudy().getNameShort()));
                    return null;
                }
            }
        }

        CollectionEvent cevent = getAndVerifyCollectionEvent(context, inputPojo, parentSpecimen);

        if (cevent == null) {
            // only aliquoted specimens with no parent require a collection
            // event
            if (pojoData.isAliquotedSpecimen()) {
                if (pojoData.getParentInventoryId() == null) {
                    errorSet.addError(
                        inputPojo.getLineNumber(),
                        SpecimenBatchOpActionErrors.CSV_CEVENT_ERROR.format(inputPojo.getPatientNumber(),
                            inputPojo.getVisitNumber()));
                }
            }
        } else {
            pojoData.setCevent(cevent);
            pojoData.setPatient(cevent.getPatient());
        }

        if (inputPojo.getWaybill() != null) {
            OriginInfo originInfo =
                BatchOpActionUtil.getOriginInfo(context.getSession(), inputPojo.getWaybill());
            if (originInfo == null) {
                errorSet.addError(inputPojo.getLineNumber(),
                    SpecimenBatchOpActionErrors.CSV_WAYBILL_ERROR.format(inputPojo.getWaybill()));
            } else {
                pojoData.setOriginInfo(originInfo);
            }
        }

        if (inputPojo.getWorksheet() != null) {
            ProcessingEvent pevent = BatchOpActionUtil.getProcessingEvent(
                context.getSession(), inputPojo.getWorksheet());
            if (pevent != null) {
                pojoData.setPevent(pevent);
                log.trace("found processing event: invId={} worksheet={}",
                    inputPojo.getInventoryId(), inputPojo.getWorksheet());
            }
        }

        if (inputPojo.getOriginCenter() != null) {
            Center center = BatchOpActionUtil.getCenter(context.getSession(), inputPojo.getOriginCenter());
            if (center != null) {
                pojoData.setOriginCenter(center);
                log.trace("found origin center: center={}", inputPojo.getOriginCenter());
            } else {
                errorSet.addError(inputPojo.getLineNumber(),
                    SpecimenBatchOpActionErrors.CSV_ORIGIN_CENTER_SHORT_NAME_ERROR.format(inputPojo.getOriginCenter()));
                return null;
            }
        }

        if (inputPojo.getCurrentCenter() != null) {
            Center center = BatchOpActionUtil.getCenter(context.getSession(), inputPojo.getCurrentCenter());
            if (center != null) {
                pojoData.setCurrentCenter(center);
                log.trace("found current center: center={}", inputPojo.getCurrentCenter());
            } else {
                errorSet.addError(inputPojo.getLineNumber(),
                    SpecimenBatchOpActionErrors.CSV_CURRENT_CENTER_SHORT_NAME_ERROR.format(inputPojo.getCurrentCenter()));
                return null;
            }
        }

        // only get container information if defined for this row
        if (pojoData.hasPosition()) {
            Pair<BatchOpInputErrorSet, SpecimenPositionPojoData> validation =
                validatePositionInfo(context.getSession(),
                                     inputPojo,
                                     inputPojo.getSpecimenType());

            BatchOpInputErrorSet validationErrors = validation.getLeft();
            errorSet.addAll(validationErrors);
            if ((validationErrors != null) && !validationErrors.isEmpty()) return null;

            SpecimenPositionPojoData info = validation.getRight();
            pojoData.setContainer(info.container);
            pojoData.setSpecimenPos(info.specimenPosition);
        }

        return pojoData;
    }

    private Specimen addSpecimen(ActionContext          context,
                                 BatchOperation         batchOp,
                                 SpecimenBatchOpDbInfo pojoData,
                                 ProcessingEvent        pevent) {
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
        if (cevent == null) {
            // if this is a source specimen then see if the patient has the
            // collection event
            if (pojoData.getPojo().getSourceSpecimen()) {
                cevent = BatchOpActionUtil.getCollectionEvent(context.getSession(),
                    pojoData.getPojo().getPatientNumber(),
                    pojoData.getPojo().getVisitNumber());

                log.debug(
                    "collection event found: pt={} v#={} invId={}",
                    new Object[] {
                        pojoData.getPojo().getPatientNumber(),
                        pojoData.getPojo().getVisitNumber(),
                        pojoData.getPojo().getInventoryId()
                    });
            } else if (pojoData.getParentSpecimen() != null) {
                // if this is an aliquoted specimen, then get the collection
                // event from the source specimen
                cevent = pojoData.getParentSpecimen().getCollectionEvent();
            }

            // if still not found create one
            if (cevent == null) {
                cevent = pojoData.createNewCollectionEvent();
                context.getSession().saveOrUpdate(cevent);
            }

            pojoData.setCevent(cevent);
            pojoData.setPatient(cevent.getPatient());
        }

        if ((pojoData.getPojo().getParentInventoryId() != null)
            && (pojoData.getParentSpecimen() == null)) {
            throw new IllegalStateException(
                "parent specimen for specimen with " + pojoData.getPojo().getInventoryId()
                    + " has not be created yet");
        }

        Specimen spc = createSpecimen(context,
                                      pojoData.getPojo().getInventoryId(),
                                      pojoData.getParentSpecimen(),
                                      cevent,
                                      pevent,
                                      pojoData.getSpecimenType(),
                                      pojoData.getPojo().getSourceSpecimen(),
                                      pojoData.getOriginCenter(),
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

    protected void checkForPatientAndCollectionEvent(SpecimenBatchOpInputPojo csvPojo) {

        if (!csvPojo.hasPatientAndCollectionEvent()) {
            // no parent inventory id and does not have patient number and visit
            // number
            errorSet.addError(csvPojo.getLineNumber(),
                SpecimenBatchOpActionErrors.CSV_ALIQ_SPC_PATIENT_CEVENT_MISSING_ERROR);
        }
    }

    // find the collection event for this specimen
    private CollectionEvent getAndVerifyCollectionEvent(ActionContext context,
        SpecimenBatchOpInputPojo inputPojo, Specimen parentSpecimen) {
        CollectionEvent cevent = null;

        if (inputPojo.getParentInventoryId() == null) {
            if (inputPojo.getPatientNumber() == null) {
                errorSet.addError(inputPojo.getLineNumber(),
                    SpecimenBatchOpActionErrors.CSV_PATIENT_NUMBER_INVALID_ERROR.format());
                return null;
            }

            cevent = BatchOpActionUtil.getCollectionEvent(context.getSession(),
                inputPojo.getPatientNumber(), inputPojo.getVisitNumber());
            return cevent;
        }

        if (parentSpecimen != null) {
            cevent = parentSpecimen.getCollectionEvent();

            // if patient number and visit number present in the pojo
            // ensure they match with the cevent and patient
            if ((inputPojo.getPatientNumber() != null) && !inputPojo.getPatientNumber().isEmpty()
                && !cevent.getPatient().getPnumber().equals(inputPojo.getPatientNumber())) {
                errorSet.addError(inputPojo.getLineNumber(), SpecimenBatchOpActionErrors.CSV_PATIENT_MATCH_ERROR.format(
                    inputPojo.getPatientNumber(), cevent.getPatient().getPnumber()));
            }

            if ((inputPojo.getVisitNumber() != null)
                && !cevent.getVisitNumber().equals(inputPojo.getVisitNumber())) {
                errorSet.addError(inputPojo.getLineNumber(),
                    SpecimenBatchOpActionErrors.CSV_CEVENT_MATCH_ERROR.format(
                        inputPojo.getVisitNumber()));
            }
        }

        // if parentSpecimen is null, then it comes from the CSV file
        return cevent;
    }

    protected ProcessingEvent createProcessignEventIfRequired(ActionContext context,
                                                              SpecimenBatchOpDbInfo pojoData) {
        ProcessingEvent pevent = null;

        // add the processing event for this source specimen
        if (pojoData.getPojo().getWorksheet() != null) {
            pevent = createdProcessingEvents.get(pojoData.getPojo().getWorksheet());

            if (pojoData.getPevent() == null) {
                if (pevent != null) {
                    pojoData.setPevent(pevent);
                    log.debug("createProcessignEventIfRequired: processing event created previously");
                } else {
                    Center center = pojoData.getOriginCenter();
                    if (center == null) {
                        // origin center not assigned to pojo, default to working center
                        center = workingCenterOnServerSide;
                    }
                    OriginInfo originInfo = pojoData.createNewOriginInfo(center);
                    originInfo.setReceiverCenter(center);
                    pevent = createNewProcessingEvent(pojoData.getParentSpecimen(),
                                                      pojoData.getPojo().getWorksheet(),
                                                      pojoData.getPojo().getCreatedAt(),
                                                      pojoData.getPojo().getWaybill(),
                                                      pojoData.getOriginInfo());
                    context.getSession().saveOrUpdate(pevent);
                    log.debug("createProcessignEventIfRequired: created new processing event");

                }
            }
        }
        return pevent;
    }

    /**
     * Creates a new processing event for the specimen stored into this builder.
     *
     * @return a new processing event.
     */
    ProcessingEvent createNewProcessingEvent(Specimen   parentSpecimen,
                                             String     worksheet,
                                             Date       createdAt,
                                             String     waybill,
                                             OriginInfo originInfo) {
        if (parentSpecimen != null) {
            throw new IllegalStateException(
                "this specimen has a parent specimen and cannot have a processing event");
        }
        ProcessingEvent pevent = new ProcessingEvent();
        pevent.setWorksheet(worksheet);
        pevent.setCreatedAt(createdAt);

        Center peventCenter = (waybill == null)
            ? originInfo.getCenter() : originInfo.getReceiverCenter();
        pevent.setCenter(peventCenter);
        pevent.setActivityStatus(ActivityStatus.ACTIVE);
        createdProcessingEvents.put(worksheet, pevent);
        return pevent;
    }

}
