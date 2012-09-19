package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.Attachment;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
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
public class SpecimenBatchOpAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    private static final Bundle bundle = new CommonBundle();

    private static Logger log = LoggerFactory
        .getLogger(SpecimenBatchOpAction.class.getName());

    public static final LString CSV_ALIQUOTED_SPC_ERROR =
        bundle.tr("specimen is not a source specimen but parent "
            + "inventory ID is not present").format();

    public static final Tr CSV_PARENT_SPECIMEN_ERROR =
        bundle.tr("parent specimen with inventory id " +
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
        bundle.tr("collection event with visit number \"{0}\" does not exist");

    public static final Tr CSV_CEVENT_MATCH_ERROR =
        bundle.tr("collection event with visit number \"{0}\" "
            + "does match the source specimen's collection event");

    private final Center workingCenter;

    private Attachment attachment;

    private CompressedReference<ArrayList<SpecimenBatchOpInputPojo>> compressedList =
        null;

    private final Set<SpecimenBatchOpHelper> specimenImportInfos =
        new HashSet<SpecimenBatchOpHelper>(0);

    private final Map<String, SpecimenBatchOpHelper> parentSpcInvIds =
        new HashMap<String, SpecimenBatchOpHelper>(0);

    private final Map<String, Specimen> parentSpecimens =
        new HashMap<String, Specimen>(0);

    private final BatchOpInputErrorList errorList = new BatchOpInputErrorList();

    public SpecimenBatchOpAction(Center workingCenter,
        ArrayList<SpecimenBatchOpInputPojo> batchOpSpecimens) {
        this.workingCenter = workingCenter;
        // this.attachment = attachment;
        compressedList =
            new CompressedReference<ArrayList<SpecimenBatchOpInputPojo>>(
                batchOpSpecimens);
        log.debug("SpecimenBatchOpAction: constructor");
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.BATCH_OPERATIONS.isAllowed(context.getUser());
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        log.debug("SpecimenBatchOpAction:run");

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

        context.getSession().getTransaction();

        // for improved performance, model objects of the same type are loaded
        // sequentially
        // getModelObjects(context, pojos);

        log.debug("SpecimenBatchOpAction: getting DB info");
        for (SpecimenBatchOpInputPojo pojo : pojos) {
            SpecimenBatchOpHelper info = getDbInfo(context, pojo);
            if (info != null) {
                specimenImportInfos.add(info);

                if (info.isSourceSpecimen()) {
                    parentSpcInvIds.put(pojo.getInventoryId(), info);
                }
            }
        }

        // find aliquoted specimens and ensure the source specimen is listed in
        // the CSV file
        for (SpecimenBatchOpHelper info : specimenImportInfos) {
            if (info.isSourceSpecimen()) continue;

            SpecimenBatchOpHelper parentInfo =
                parentSpcInvIds.get(info.getParentInventoryId());

            if (parentInfo == null) {
                // if this specimen has a parent specimen that is not in DB,
                // is it in the CSV data?

                if (info.getPatient() == null) {
                    errorList.addError(info.getCsvLineNumber(),
                        CSV_PARENT_SPECIMEN_ERROR.format(info
                            .getParentInventoryId()));
                }
            } else {
                info.setParentInfo(parentInfo);
                info.setPatient(parentInfo.getPatient());
            }
        }

        if (!errorList.isEmpty()) {
            throw new BatchOpErrorsException(errorList.getErrors());
        }

        // add all source specimens first
        log.debug("SpecimenBatchOpAction: adding source specimens");
        for (SpecimenBatchOpHelper info : specimenImportInfos) {
            if (info.isAliquotedSpecimen()) continue;

            Specimen spc = addSpecimen(context, info);
            parentSpecimens.put(spc.getInventoryId(), spc);

            // add the processing event for this source specimen
            if (info.hasWorksheet()) {
                ProcessingEvent pevent = info.getNewProcessingEvent();
                context.getSession().saveOrUpdate(pevent);

                // TODO: set activity status to closed?
            }
        }

        // now add aliquoted specimens
        log.debug("SpecimenBatchOpAction: adding aliquot specimens");
        for (SpecimenBatchOpHelper info : specimenImportInfos) {
            if (info.isSourceSpecimen()) continue;

            if (info.getParentSpecimen() == null) {
                Specimen parentSpc =
                    parentSpecimens.get(info.getParentInventoryId());
                info.setParentSpecimen(parentSpc);
            }
            addSpecimen(context, info);
        }

        result = true;
        log.debug("SpecimenBatchOpAction:end");
        return new BooleanResult(result);
    }

    // get referenced items that exist in the database
    private SpecimenBatchOpHelper getDbInfo(ActionContext context,
        SpecimenBatchOpInputPojo csvInfo) {

        SpecimenBatchOpHelper info = new SpecimenBatchOpHelper(csvInfo);
        info.setUser(context.getUser());

        Specimen parentSpecimen = null;
        Patient patient = null;
        CollectionEvent cevent = null;

        if (info.isSourceSpecimen()) {
            patient = BatchOpActionUtil.getPatient(context,
                csvInfo.getPatientNumber());
            if (patient == null) {
                errorList.addError(csvInfo.getLineNumber(),
                    CSV_PATIENT_ERROR.format());
                return null;
            }
            info.setPatient(patient);

            cevent = findCeventByVisitNumber(csvInfo.getVisitNumber(),
                patient.getCollectionEvents());
        } else {
            // get the patient and collection event from the source specimen
            parentSpecimen = BatchOpActionUtil.getSpecimen(context,
                csvInfo.getParentInventoryId());

            if (parentSpecimen != null) {
                if (parentSpecimen.getProcessingEvent() == null) {
                    errorList.addError(csvInfo.getLineNumber(),
                        CSV_PARENT_SPECIMEN_NO_PEVENT_ERROR.format(
                            csvInfo.getInventoryId(),
                            parentSpecimen.getInventoryId()));
                }

                cevent = parentSpecimen.getCollectionEvent();
                patient = cevent.getPatient();

                info.setParentSpecimen(parentSpecimen);
                info.setPatient(patient);

                log.debug(
                    "setting patient on aliquoted specimen: invId={} pnumber={}",
                    new Object[] {
                        info.getPojo().getInventoryId(),
                        info.getPojo().getPatientNumber()
                    });

                // if patient number and visit number present in the pojo
                // ensure they match with the cevent and patient
                if (csvInfo.hasPatientAndCollectionEvent()) {
                    if (!csvInfo.getPatientNumber()
                        .equals(patient.getPnumber())) {
                        errorList.addError(csvInfo.getLineNumber(),
                            CSV_PATIENT_MATCH_ERROR.format(
                                csvInfo.getPatientNumber()));
                    }

                    if (!csvInfo.getVisitNumber().equals(
                        cevent.getVisitNumber())) {
                        errorList.addError(csvInfo.getLineNumber(),
                            CSV_CEVENT_MATCH_ERROR.format(
                                csvInfo.getVisitNumber()));
                    }

                }
            }
        }

        if ((cevent == null) && info.isAliquotedSpecimen()
            && !info.hasParentInventoryId()) {
            errorList.addError(csvInfo.getLineNumber(),
                CSV_CEVENT_ERROR.format(csvInfo.getVisitNumber()));
        }

        info.setCevent(cevent);

        if ((csvInfo.getWaybill() != null)
            && !csvInfo.getWaybill().isEmpty()) {
            OriginInfo originInfo =
                BatchOpActionUtil.getOriginInfo(context, csvInfo.getWaybill());
            if (originInfo == null) {
                errorList.addError(csvInfo.getLineNumber(),
                    CSV_WAYBILL_ERROR.format(csvInfo.getWaybill()));
            } else {
                info.setOriginInfo(originInfo);
            }
        }

        SpecimenType spcType =
            BatchOpActionUtil.getSpecimenType(context,
                csvInfo.getSpecimenType());
        if (spcType == null) {
            errorList.addError(csvInfo.getLineNumber(),
                CSV_SPECIMEN_TYPE_ERROR.format(csvInfo.getSpecimenType()));
        } else {
            info.setSpecimenType(spcType);
        }

        // only get container information if defined for this row
        if (info.hasPosition()) {
            Container container = BatchOpActionUtil.getContainer(context,
                csvInfo.getPalletLabel());
            if (container == null) {
                errorList.addError(csvInfo.getLineNumber(),
                    CSV_CONTAINER_LABEL_ERROR.format(csvInfo.getPalletLabel()));
            } else {
                info.setContainer(container);
            }

            try {
                RowColPos pos = container.getPositionFromLabelingScheme(csvInfo
                    .getPalletPosition());
                info.setSpecimenPos(pos);
            } catch (Exception e) {
                errorList.addError(csvInfo.getLineNumber(),
                    CSV_SPECIMEN_LABEL_ERROR.format(csvInfo.getPalletLabel()));
            }
        }

        return info;
    }

    private Specimen addSpecimen(ActionContext context,
        SpecimenBatchOpHelper info) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }

        OriginInfo originInfo = info.getOriginInfo();
        if (originInfo == null) {
            originInfo = info.getNewOriginInfo(workingCenter);
        }

        CollectionEvent cevent = info.getCevent();
        if (cevent == null) {
            // if this is a source specimen then see if the patient has the
            // collection event
            if (info.getPojo().getSourceSpecimen()) {
                for (CollectionEvent patientCevent : info.getPatient()
                    .getCollectionEvents()) {
                    if (patientCevent.getVisitNumber().equals(
                        info.getPojo().getVisitNumber())) {
                        cevent = patientCevent;

                        log.debug(
                            "collection event found: pt={} v#={} invId={}",
                            new Object[] {
                                info.getPojo().getPatientNumber(),
                                info.getPojo().getVisitNumber(),
                                info.getPojo().getInventoryId()
                            });

                    }
                }
            } else {
                // if this is an aliquoted specimen, then get the collection
                // event from the source specimen
                cevent = info.getParentSpecimen().getCollectionEvent();
            }

            info.setCevent(cevent);

            // if still not found create one
            if (cevent == null) {
                cevent = info.getNewCollectionEvent();
                context.getSession().saveOrUpdate(cevent);
            }
        }

        Specimen spc = info.getNewSpecimen();

        // check if this specimen has a comment and if so save it to DB
        if (!spc.getComments().isEmpty()) {
            Comment comment = spc.getComments().iterator().next();
            context.getSession().save(comment);
        }

        context.getSession().save(spc.getOriginInfo());
        context.getSession().save(spc);

        return spc;
    }

    // find the collection event for this specimen
    private CollectionEvent findCeventByVisitNumber(Integer visitNumber,
        Set<CollectionEvent> cevents) {
        for (CollectionEvent ce : cevents) {
            if (ce.getVisitNumber().equals(visitNumber)) {
                return ce;
            }
        }
        return null;
    }

    /*
     * Generates an action exception if patient does not exist.
     */
    private Patient loadPatient(ActionContext context, String pnumber) {
        // make sure patient exists
        Patient p = BatchOpActionUtil.getPatient(context, pnumber);
        if (p == null) {
            throw new LocalizedException(
                CSV_PATIENT_DOES_NOT_EXIST_ERROR.format(pnumber));
        }
        return p;
    }

    private void getModelObjects(ActionContext context,
        ArrayList<SpecimenBatchOpInputPojo> pojos) {
        Set<String> patientNumbers = new HashSet<String>();
        Set<String> parentInventoryIds = new HashSet<String>();

        for (SpecimenBatchOpInputPojo pojo : pojos) {
            patientNumbers.add(pojo.getPatientNumber());
            parentInventoryIds.add(pojo.getParentInventoryId());
        }

    }
}
