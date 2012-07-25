package edu.ualberta.med.biobank.action.csvimport.specimen;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.BooleanResult;
import edu.ualberta.med.biobank.action.csvimport.CsvActionUtil;
import edu.ualberta.med.biobank.action.csvimport.CsvErrorList;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.exception.CsvImportException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * This action takes a CSV file as input and import the specimens contained in
 * the file.
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public class SpecimenCsvImportAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    private static final Bundle bundle = new CommonBundle();

    private static Logger log = LoggerFactory
        .getLogger(SpecimenCsvImportAction.class.getName());

    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenCsvImportAction.class);

    public static final LString CSV_SPC_PATIENT_ERROR =
        bundle
            .tr(
                "parent specimen and child specimen do not have the same patient number")
            .format();

    public static final LString CSV_PARENT_SPC_ERROR =
        bundle.tr(
            "specimen is a source specimen but parent inventory ID present")
            .format();

    public static final LString CSV_ALIQUOTED_SPC_ERROR =
        bundle
            .tr("specimen is not a source specimen but parent inventory ID is not present")
            .format();

    public static final LString CSV_PALLET_POS_ERROR =
        bundle.tr("pallet label defined but not position").format();

    public static final LString CSV_PALLET_LABEL_ERROR =
        bundle.tr("pallet position defined but not label").format();

    public static final LString CSV_UNCOMPRESS_ERROR =
        bundle.tr("CVS file could not be uncompressed").format();

    public static final Tr CSV_PATIENT_ERROR =
        bundle.tr("patient in CSV file with number \"{0}\" not exist");

    public static final Tr CSV_PARENT_SPECIMEN_ERROR =
        bundle
            .tr("parent specimen in CSV file with inventory id \"{0}\" does not exist");

    public static final Tr CSV_PARENT_SPECIMEN_NO_PEVENT_ERROR =
        bundle
            .tr("the parent specimen of specimen with inventory id \"{0}\", parent specimen with inventory id \"{0}\", does not have a processing event");

    public static final Tr CSV_ORIGIN_CENTER_ERROR =
        bundle.tr("origin center in CSV file with name \"{0}\" does not exist");

    public static final Tr CSV_CURRENT_CENTER_ERROR =
        bundle
            .tr("current center in CSV file with name \"{0}\" does not exist");

    public static final Tr CSV_SPECIMEN_TYPE_ERROR =
        bundle.tr("specimen type in CSV file with name \"{0}\" does not exist");

    public static final Tr CSV_CONTAINER_LABEL_ERROR =
        bundle.tr("container in CSV file with label \"{0}\" does not exist");

    public static final Tr CSV_SPECIMEN_LABEL_ERROR =
        bundle
            .tr("specimen position in CSV file with label \"{0}\" is invalid");

    // @formatter:off
    private static final CellProcessor[] PROCESSORS = new CellProcessor[] {
        new Unique(),                       // inventoryId,
        new Optional(),                     // parentInventoryID,
        new StrNotNullOrEmpty(),            // specimenType,
        new ParseDate("yyyy-MM-dd HH:mm"),  // createdAt,
        new StrNotNullOrEmpty(),            // patientNumber,
        new ParseInt(),                     // visitNumber,
        new StrNotNullOrEmpty(),            // currentCenter,
        new StrNotNullOrEmpty(),            // originCenter,
        new ParseBool(),                    // sourceSpecimen,
        new Optional(new Unique()),         // worksheet,
        new Optional(),                     // palletProductBarcode,
        new Optional(),                     // rootContainerType,
        new Optional(),                     // palletLabel,
        new Optional()                      // palletPosition
    }; 
    // @formatter:on    

    private final CsvErrorList errorList = new CsvErrorList();

    private CompressedReference<ArrayList<SpecimenCsvInfo>> compressedList =
        null;

    private final Set<SpecimenImportInfo> specimenImportInfos =
        new HashSet<SpecimenImportInfo>(0);

    private final Map<String, SpecimenImportInfo> parentSpcInvIds =
        new HashMap<String, SpecimenImportInfo>(0);

    private final Map<String, Specimen> parentSpecimens =
        new HashMap<String, Specimen>(0);

    public SpecimenCsvImportAction(String filename) throws IOException {
        setCsvFile(filename);
    }

    private void setCsvFile(String filename) throws IOException {
        ICsvBeanReader reader = new CsvBeanReader(
            new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

        final String[] header = new String[] {
            "inventoryId",
            "parentInventoryId",
            "specimenType",
            "createdAt",
            "patientNumber",
            "visitNumber",
            "currentCenter",
            "originCenter",
            "sourceSpecimen",
            "worksheet",
            "palletProductBarcode",
            "rootContainerType",
            "palletLabel",
            "palletPosition"
        };

        try {
            ArrayList<SpecimenCsvInfo> csvInfos =
                new ArrayList<SpecimenCsvInfo>(0);

            Map<String, SpecimenCsvInfo> parentSpcMap =
                new HashMap<String, SpecimenCsvInfo>();

            SpecimenCsvInfo csvInfo;
            reader.getCSVHeader(true);
            while ((csvInfo =
                reader.read(SpecimenCsvInfo.class, header, PROCESSORS)) != null) {

                if (csvInfo.getSourceSpecimen()) {
                    if ((csvInfo.getParentInventoryId() != null)
                        && !csvInfo.getParentInventoryId().isEmpty()) {
                        errorList.addError(reader.getLineNumber(),
                            CSV_ALIQUOTED_SPC_ERROR);
                    }
                    parentSpcMap.put(csvInfo.getInventoryId(), csvInfo);
                } else {
                    if ((csvInfo.getParentInventoryId() == null)
                        || csvInfo.getParentInventoryId().isEmpty()) {
                        errorList.addError(reader.getLineNumber(),
                            CSV_ALIQUOTED_SPC_ERROR);
                    }

                    // check that parent and child specimens have the same
                    // patient number
                    SpecimenCsvInfo parentCsvInfo =
                        parentSpcMap.get(csvInfo.getParentInventoryId());

                    if ((parentCsvInfo != null)
                        && !csvInfo.getPatientNumber().equals(
                            parentCsvInfo.getPatientNumber())) {
                        errorList.addError(reader.getLineNumber(),
                            CSV_SPC_PATIENT_ERROR);
                    }
                }

                if ((csvInfo.getPalletLabel() != null)
                    && (csvInfo.getPalletPosition() == null)) {
                    errorList.addError(reader.getLineNumber(),
                        CSV_PALLET_POS_ERROR);
                }

                if ((csvInfo.getPalletLabel() == null)
                    && (csvInfo.getPalletPosition() != null)) {
                    errorList.addError(reader.getLineNumber(),
                        CSV_PALLET_LABEL_ERROR);
                }

                csvInfo.setLineNumber(reader.getLineNumber());
                csvInfos.add(csvInfo);
            }

            if (!errorList.isEmpty()) {
                throw new CsvImportException(errorList.getErrors());
            }

            compressedList =
                new CompressedReference<ArrayList<SpecimenCsvInfo>>(
                    csvInfos);

        } catch (SuperCSVException e) {
            throw new IllegalStateException(
                i18n.tr(CsvActionUtil.CSV_PARSE_ERROR, e.getMessage(),
                    e.getCsvContext()));
        } finally {
            reader.close();
        }
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.LEGACY_IMPORT_CSV.isAllowed(context.getUser());
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        if (compressedList == null) {
            throw new LocalizedException(CsvActionUtil.CSV_FILE_ERROR);
        }

        boolean result = false;

        ArrayList<SpecimenCsvInfo> specimenCsvInfos = compressedList.get();
        context.getSession().getTransaction();

        for (SpecimenCsvInfo csvInfo : specimenCsvInfos) {
            SpecimenImportInfo info = getDbInfo(context, csvInfo);
            specimenImportInfos.add(info);

            if (info.isSourceSpecimen()) {
                parentSpcInvIds.put(csvInfo.getInventoryId(), info);
            }
        }

        // find aliquoted specimens and ensure the source specimen is listed in
        // the CSV file
        for (SpecimenImportInfo info : specimenImportInfos) {
            if (info.isSourceSpecimen()) continue;

            SpecimenImportInfo parentInfo =
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
            throw new CsvImportException(errorList.getErrors());
        }

        // add all source specimens first
        for (SpecimenImportInfo info : specimenImportInfos) {
            if (info.isAliquotedSpecimen()) continue;

            Specimen spc = addSpecimen(context, info);
            parentSpecimens.put(spc.getInventoryId(), spc);

            // add the processing event for this source specimen
            if (info.hasWorksheet()) {
                ProcessingEvent pevent = info.createProcessingEvent();
                context.getSession().saveOrUpdate(pevent);

                // TODO: set activity status to closed?
            }
        }

        // now add aliquoted specimens
        for (SpecimenImportInfo info : specimenImportInfos) {
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
    private SpecimenImportInfo getDbInfo(ActionContext context,
        SpecimenCsvInfo csvInfo) {
        SpecimenImportInfo info = new SpecimenImportInfo(csvInfo);

        Patient patient = loadPatient(context, csvInfo.getPatientNumber());
        info.setPatient(patient);

        if (info.isSourceSpecimen()) {
            // find the collection event for this specimen
            for (CollectionEvent ce : patient.getCollectionEvents()) {
                if (ce.getVisitNumber().equals(csvInfo.getVisitNumber())) {
                    log.debug("setting collection event: pt={} numCevents={}",
                        csvInfo.getPatientNumber(), patient
                            .getCollectionEvents()
                            .size());
                    info.setCevent(ce);
                    break;
                }
            }
        } else {
            Specimen parentSpecimen =
                CsvActionUtil.getSpecimen(context,
                    csvInfo.getParentInventoryId());
            if (parentSpecimen != null) {
                if (parentSpecimen.getProcessingEvent() == null) {
                    errorList
                        .addError(
                            csvInfo.getLineNumber(),
                            CSV_PARENT_SPECIMEN_NO_PEVENT_ERROR.format(csvInfo
                                .getInventoryId(),
                                parentSpecimen.getInventoryId()));

                }
                info.setParentSpecimen(parentSpecimen);
            }
        }

        Center originCenter =
            CsvActionUtil.getCenter(context, csvInfo.getOriginCenter());
        if (originCenter == null) {
            errorList.addError(csvInfo.getLineNumber(),
                CSV_ORIGIN_CENTER_ERROR.format(csvInfo.getOriginCenter()));
        } else {
            info.setOriginCenter(originCenter);
        }

        Center currentCenter =
            CsvActionUtil.getCenter(context, csvInfo.getCurrentCenter());
        if (originCenter == null) {
            errorList.addError(csvInfo.getLineNumber(),
                CSV_CURRENT_CENTER_ERROR.format(csvInfo.getCurrentCenter()));
        } else {
            info.setCurrentCenter(currentCenter);
        }

        SpecimenType spcType =
            CsvActionUtil.getSpecimenType(context, csvInfo.getSpecimenType());
        if (spcType == null) {
            errorList.addError(csvInfo.getLineNumber(),
                CSV_SPECIMEN_TYPE_ERROR.format(csvInfo.getSpecimenType()));
        } else {
            info.setSpecimenType(spcType);
        }

        // only get container information if defined for this row
        if (info.hasPosition()) {
            Container container =
                CsvActionUtil.getContainer(context, csvInfo.getPalletLabel());
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

    private Specimen addSpecimen(ActionContext context, SpecimenImportInfo info) {
        if (context == null) {
            throw new IllegalStateException(
                "should only be called once the context is initialized");
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
                cevent = info.createCollectionEvent();
                context.getSession().saveOrUpdate(cevent);
            }
        }

        Specimen spc = info.getNewSpecimen();
        context.getSession().save(spc.getOriginInfo());
        context.getSession().save(spc);

        return spc;
    }

    /*
     * Generates an action exception if patient does not exist.
     */
    private Patient loadPatient(ActionContext context, String pnumber) {
        // make sure patient exists
        Patient p = CsvActionUtil.getPatient(context, pnumber);
        if (p == null) {
            throw new LocalizedException(CSV_PATIENT_ERROR.format(pnumber));
        }
        return p;
    }

}
