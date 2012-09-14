package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

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
        bundle.tr("parent specimen in CSV file with inventory id " +
            "\"{0}\" does not exist");

    public static final Tr CSV_PARENT_SPECIMEN_NO_PEVENT_ERROR =
        bundle.tr("the parent specimen of specimen with "
            + "inventory id \"{0}\", parent specimen with "
            + "inventory id \"{0}\", does not have a processing event");

    public static final Tr CSV_WAYBILL_ERROR =
        bundle.tr("waybill \"{0}\" does not exist");

    public static final Tr CSV_SPECIMEN_TYPE_ERROR =
        bundle.tr("specimen type in CSV file with name \"{0}\" does not exist");

    public static final Tr CSV_CONTAINER_LABEL_ERROR =
        bundle.tr("container in CSV file with label \"{0}\" does not exist");

    public static final Tr CSV_SPECIMEN_LABEL_ERROR =
        bundle
            .tr("specimen position in CSV file with label \"{0}\" is invalid");

    public static final Tr CSV_PATIENT_ERROR =
        bundle.tr("patient in CSV file with number \"{0}\" not exist");

    public static final Tr CSV_CEVENT_ERROR =
        bundle.tr("collection event with visit number \"{0}\" does not exist");

    // private static final CsvReaderParams CBSR_TECAN_IMPORT =
    // new CsvReaderParams(
    // "Rack ID",
    // new String[] {
    // "rackId",
    // "cavityId",
    // "position",
    // "sourceId",
    // "concentration",
    // "concentrationUnit",
    // "volume",
    // "userDefined1",
    // "userDefined2",
    // "userDefined3",
    // "userDefined4",
    // "userDefined5",
    // "plateErrors",
    // "samplEerrors",
    // "sampleInstanceId",
    // "sampleId"
    // },
    //
//            // @formatter:off
//            new CellProcessor[] {
//                new StrNotNullOrEmpty(),            // rackId          
//                new StrNotNullOrEmpty(),            // cavityId        
//                new StrNotNullOrEmpty(),            // position        
//                new Unique(),                       // sampleId        
//                new ParseInt(),                     // concentration   
//                new StrNotNullOrEmpty(),            // concentrationUnit
//                new ParseInt(),                     // volume          
//                new StrNotNullOrEmpty(),            // userDefined1    
//                new StrNotNullOrEmpty(),            // userDefined2    
//                new StrNotNullOrEmpty(),            // userDefined3    
//                new StrNotNullOrEmpty(),            // userDefined4    
//                new StrNotNullOrEmpty(),            // userDefined5    
//                new StrNotNullOrEmpty(),            // plateErrors     
//                new StrNotNullOrEmpty(),            // samplEerrors    
//                new ParseInt(),                     // sampleInstanceId
//                new ParseInt()                      // sampleId2
//            }
//            // @formatter:on    
    // );
    //
    // private static final CsvReaderParams OHS_TECAN_IMPORT =
    // new CsvReaderParams(
    // "TECAN_Rack ID",
    // new String[] {
    // },
    //
//            // @formatter:off
//            new CellProcessor[] {
//            }
//            // @formatter:on    
    // );

    private final Center workingCenter;

    private CompressedReference<ArrayList<SpecimenBatchOpInputRow>> compressedList =
        null;

    private final Set<SpecimenBatchOpHelper> specimenImportInfos =
        new HashSet<SpecimenBatchOpHelper>(0);

    private final Map<String, SpecimenBatchOpHelper> parentSpcInvIds =
        new HashMap<String, SpecimenBatchOpHelper>(0);

    private final Map<String, Specimen> parentSpecimens =
        new HashMap<String, Specimen>(0);

    private final BatchOpInputErrorList errorList = new BatchOpInputErrorList();

    public SpecimenBatchOpAction(Center workingCenter,
        ArrayList<SpecimenBatchOpInputRow> batchOpSpecimens) {
        this.workingCenter = workingCenter;
        compressedList =
            new CompressedReference<ArrayList<SpecimenBatchOpInputRow>>(
                batchOpSpecimens);
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.BATCH_OPERATIONS.isAllowed(context.getUser());
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        if (compressedList == null) {
            throw new IllegalStateException("compressed list is null");
        }

        boolean result = false;

        ArrayList<SpecimenBatchOpInputRow> batchOpSpecimens =
            compressedList.get();
        context.getSession().getTransaction();

        for (SpecimenBatchOpInputRow batchOpSpecimen : batchOpSpecimens) {
            SpecimenBatchOpHelper info = getDbInfo(context, batchOpSpecimen);
            specimenImportInfos.add(info);

            if (info.isSourceSpecimen()) {
                parentSpcInvIds.put(batchOpSpecimen.getInventoryId(), info);
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
            }
        }

        if (!errorList.isEmpty()) {
            throw new BatchOpErrorsException(errorList.getErrors());
        }

        // add all source specimens first
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
        return new BooleanResult(result);
    }

    // get referenced items that exist in the database
    private SpecimenBatchOpHelper getDbInfo(ActionContext context,
        SpecimenBatchOpInputRow csvInfo) {
        SpecimenBatchOpHelper info = new SpecimenBatchOpHelper(csvInfo);
        info.setUser(context.getUser());

        Patient patient = loadPatient(context, csvInfo.getPatientNumber());
        info.setPatient(patient);

        CollectionEvent cevent = null;

        // find the collection event for this specimen
        for (CollectionEvent ce : patient.getCollectionEvents()) {
            if (ce.getVisitNumber().equals(csvInfo.getVisitNumber())) {
                cevent = ce;
                break;
            }
        }

        if ((cevent == null) && info.isAliquotedSpecimen()
            && !info.hasParentInventoryId()) {
            errorList.addError(csvInfo.getLineNumber(),
                CSV_CEVENT_ERROR.format(csvInfo.getVisitNumber()));
        }

        info.setCevent(cevent);

        if (info.isAliquotedSpecimen()) {
            Specimen parentSpecimen = BatchOpActionUtil.getSpecimen(context,
                csvInfo.getParentInventoryId());
            if (parentSpecimen != null) {
                if (parentSpecimen.getProcessingEvent() == null) {
                    errorList.addError(csvInfo.getLineNumber(),
                        CSV_PARENT_SPECIMEN_NO_PEVENT_ERROR.format(csvInfo
                            .getInventoryId(),
                            parentSpecimen.getInventoryId()));

                }
                info.setParentSpecimen(parentSpecimen);
            }
        }

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
            Container container =
                BatchOpActionUtil.getContainer(context,
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
            // see if this collection event was created for a previous specimen
            for (CollectionEvent patientCevent : info.getPatient()
                .getCollectionEvents()) {
                if (patientCevent.getVisitNumber().equals(
                    info.getCsvInfo().getVisitNumber())) {
                    cevent = patientCevent;

                    log.debug("collection event found: pt={} v#={} invId={}",
                        new Object[] {
                            info.getCsvInfo().getPatientNumber(),
                            info.getCsvInfo().getVisitNumber(),
                            info.getCsvInfo().getInventoryId()
                        });

                    info.setCevent(cevent);
                }
            }

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

    /*
     * Generates an action exception if patient does not exist.
     */
    private Patient loadPatient(ActionContext context, String pnumber) {
        // make sure patient exists
        Patient p = BatchOpActionUtil.getPatient(context, pnumber);
        if (p == null) {
            throw new LocalizedException(CSV_PATIENT_ERROR.format(pnumber));
        }
        return p;
    }

}
