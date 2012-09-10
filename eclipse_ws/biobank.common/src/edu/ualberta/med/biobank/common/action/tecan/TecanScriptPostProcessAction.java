package edu.ualberta.med.biobank.common.action.tecan;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.csv.CsvActionUtil;
import edu.ualberta.med.biobank.common.action.csv.CsvErrorList;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.CsvException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.util.CompressedReference;

/**
 * This action takes a TECAN CSV output file as input and creates a processing
 * event and adds the aliquoted specimens contained in the file to the database.
 * 
 * @author loyola
 * 
 */
public class TecanScriptPostProcessAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    private static final Bundle bundle = new CommonBundle();

    private static Logger log = LoggerFactory
        .getLogger(TecanScriptPostProcessAction.class.getName());

    private static final I18n i18n = I18nFactory
        .getI18n(TecanScriptPostProcessAction.class);

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

    public static final Tr CSV_CEVENT_ERROR =
        bundle.tr("collection event with visit number \"{0}\" does not exist");

    public static final Tr CSV_PARENT_SPECIMEN_ERROR =
        bundle
            .tr("parent specimen in CSV file with inventory id \"{0}\" does not exist");

    public static final Tr CSV_PARENT_SPECIMEN_NO_PEVENT_ERROR =
        bundle
            .tr("the parent specimen of specimen with inventory id \"{0}\", parent specimen with inventory id \"{0}\", does not have a processing event");

    public static final Tr CSV_WAYBILL_ERROR =
        bundle.tr("waybill \"{0}\" does not exist");

    public static final Tr CSV_SPECIMEN_TYPE_ERROR =
        bundle.tr("specimen type in CSV file with name \"{0}\" does not exist");

    public static final Tr CSV_CONTAINER_LABEL_ERROR =
        bundle.tr("container in CSV file with label \"{0}\" does not exist");

    public static final Tr CSV_SPECIMEN_LABEL_ERROR =
        bundle
            .tr("specimen position in CSV file with label \"{0}\" is invalid");

    // @formatter:off
    private static final CellProcessor[] PROCESSORS = new CellProcessor[] {
        new StrNotNullOrEmpty(),            // rackId          
        new StrNotNullOrEmpty(),            // cavityId        
        new StrNotNullOrEmpty(),            // position        
        new Unique(),                       // sampleId        
        new ParseInt(),                     // concentration   
        new StrNotNullOrEmpty(),            // concentrationUnit
        new ParseInt(),                     // volume          
        new StrNotNullOrEmpty(),            // userDefined1    
        new StrNotNullOrEmpty(),            // userDefined2    
        new StrNotNullOrEmpty(),            // userDefined3    
        new StrNotNullOrEmpty(),            // userDefined4    
        new StrNotNullOrEmpty(),            // userDefined5    
        new StrNotNullOrEmpty(),            // plateErrors     
        new StrNotNullOrEmpty(),            // samplEerrors    
        new ParseInt(),                     // sampleInstanceId
        new ParseInt(),                     // sampleId2
    }; 
    // @formatter:on    

    private final Center workingCenter;

    private final CsvErrorList errorList = new CsvErrorList();

    private CompressedReference<ArrayList<CbsrTecanCsvRow>> compressedList =
        null;

    private final Set<CbsrTecanSpecimenHelper> specimenImportInfos =
        new HashSet<CbsrTecanSpecimenHelper>(0);

    public TecanScriptPostProcessAction(Center workingCenter, String filename)
        throws IOException {
        this.workingCenter = workingCenter;
        setCsvFile(filename);
    }

    private void setCsvFile(String filename) throws IOException {
        ICsvBeanReader reader = new CsvBeanReader(
            new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

        final String[] header = new String[] {
            "rackId",
            "cavityId",
            "position",
            "sourceId",
            "concentration",
            "concentrationUnit",
            "volume",
            "userDefined1",
            "userDefined2",
            "userDefined3",
            "userDefined4",
            "userDefined5",
            "plateErrors",
            "samplEerrors",
            "sampleInstanceId",
            "sampleId",
        };

        try {
            ArrayList<CbsrTecanCsvRow> csvInfos =
                new ArrayList<CbsrTecanCsvRow>(0);

            CbsrTecanCsvRow csvInfo;
            reader.getCSVHeader(true);
            while ((csvInfo = reader.read(CbsrTecanCsvRow.class, header,
                PROCESSORS)) != null) {
                csvInfo.setLineNumber(reader.getLineNumber());
                csvInfos.add(csvInfo);
            }

            if (!errorList.isEmpty()) {
                throw new CsvException(errorList.getErrors());
            }

            compressedList =
                new CompressedReference<ArrayList<CbsrTecanCsvRow>>(
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
        return PermissionEnum.TECAN_PROCESSING.isAllowed(context.getUser());
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        if (compressedList == null) {
            throw new LocalizedException(CsvActionUtil.CSV_FILE_ERROR);
        }

        boolean result = false;

        ArrayList<CbsrTecanCsvRow> specimenCsvInfos = compressedList.get();
        context.getSession().getTransaction();

        for (CbsrTecanCsvRow csvInfo : specimenCsvInfos) {
            CbsrTecanSpecimenHelper info = getDbInfo(context, csvInfo);
            specimenImportInfos.add(info);
        }

        // find aliquoted specimens and ensure the source specimen is listed in
        // the CSV file
        for (CbsrTecanSpecimenHelper info : specimenImportInfos) {
        }

        if (!errorList.isEmpty()) {
            throw new CsvException(errorList.getErrors());
        }

        // TODO: complete this method

        result = true;
        return new BooleanResult(result);
    }

    // get referenced items that exist in the database
    private CbsrTecanSpecimenHelper getDbInfo(ActionContext context,
        CbsrTecanCsvRow csvInfo) {
        CbsrTecanSpecimenHelper info = new CbsrTecanSpecimenHelper(csvInfo);

        // TODO: complete this method

        return info;
    }

    private Specimen addSpecimen(ActionContext context,
        CbsrTecanSpecimenHelper info) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }

        /*
         * OriginInfo originInfo = info.getOriginInfo(); if (originInfo == null)
         * { originInfo = info.getNewOriginInfo(workingCenter); }
         * 
         * CollectionEvent cevent = info.getCevent(); if (cevent == null) { //
         * see if this collection event was created for a previous specimen for
         * (CollectionEvent patientCevent : info.getPatient()
         * .getCollectionEvents()) { if (patientCevent.getVisitNumber().equals(
         * info.getCsvRow().getVisitNumber())) { cevent = patientCevent;
         * 
         * log.debug("collection event found: pt={} v#={} invId={}", new
         * Object[] { info.getCsvRow().getPatientNumber(),
         * info.getCsvRow().getVisitNumber(), info.getCsvRow().getInventoryId()
         * });
         * 
         * info.setCevent(cevent); } }
         * 
         * // if still not found create one if (cevent == null) { cevent =
         * info.getNewCollectionEvent();
         * context.getSession().saveOrUpdate(cevent); } }
         * 
         * Specimen spc = info.getNewSpecimen();
         * context.getSession().save(spc.getOriginInfo());
         * context.getSession().save(spc);
         * 
         * return spc;
         */

        return null;
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