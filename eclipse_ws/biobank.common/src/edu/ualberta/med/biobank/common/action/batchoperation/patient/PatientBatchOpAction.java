package edu.ualberta.med.biobank.common.action.batchoperation.patient;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.supercsv.cellprocessor.ParseDate;
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
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorList;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public class PatientBatchOpAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    private static final Bundle bundle = new CommonBundle();

    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenBatchOpAction.class);

    public static final LString CSV_FILE_ERROR =
        bundle.tr("CVS file not loaded").format();

    public static final Tr CSV_STUDY_ERROR =
        bundle.tr("CSV study {0} does not exist");

    // @formatter:off
    private static final CellProcessor[] PROCESSORS = new CellProcessor[] {
        new StrNotNullOrEmpty(),            // studyName
        new Unique(),                       // patientNumber
        new ParseDate("yyyy-MM-dd HH:mm")   // createdAt
    };
    // @formatter:on    

    private final BatchOpInputErrorList csvErrorList =
        new BatchOpInputErrorList();

    private CompressedReference<ArrayList<PatientBatchOpInputRow>> compressedList =
        null;

    private final Set<PatientBatchOpHelper> patientImportInfos =
        new HashSet<PatientBatchOpHelper>(0);

    public PatientBatchOpAction(String filename) throws IOException {
        setCsvFile(filename);
    }

    private void setCsvFile(String filename) throws IOException {
        ICsvBeanReader reader = new CsvBeanReader(
            new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

        final String[] header = new String[] {
            "studyName",
            "patientNumber",
            "createdAt"
        };

        try {
            ArrayList<PatientBatchOpInputRow> patientCsvInfos =
                new ArrayList<PatientBatchOpInputRow>(0);

            PatientBatchOpInputRow patientCsvInfo;
            reader.getCSVHeader(true);
            while ((patientCsvInfo =
                reader.read(PatientBatchOpInputRow.class, header, PROCESSORS)) != null) {
                patientCsvInfos.add(patientCsvInfo);
            }

            compressedList =
                new CompressedReference<ArrayList<PatientBatchOpInputRow>>(
                    patientCsvInfos);

        } catch (SuperCSVException e) {
            throw new IllegalStateException(
                i18n.tr(BatchOpActionUtil.CSV_PARSE_ERROR, e.getMessage(),
                    e.getCsvContext()));
        } finally {
            reader.close();
        }
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return PermissionEnum.BATCH_OPERATIONS.isAllowed(context.getUser());
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        if (compressedList == null) {
            throw new LocalizedException(CSV_FILE_ERROR);
        }

        boolean result = false;

        ArrayList<PatientBatchOpInputRow> patientCsvInfos;

        try {
            patientCsvInfos = compressedList.get();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

        for (PatientBatchOpInputRow csvInfo : patientCsvInfos) {
            Study study =
                BatchOpActionUtil.getStudy(context, csvInfo.getStudyName());

            if (study == null) {
                csvErrorList.addError(csvInfo.getLineNumber(),
                    CSV_STUDY_ERROR.format(csvInfo.getStudyName()));
                continue;
            }

            PatientBatchOpHelper importInfo = new PatientBatchOpHelper(csvInfo);
            importInfo.setStudy(study);
            patientImportInfos.add(importInfo);
        }

        if (!csvErrorList.isEmpty()) {
            throw new BatchOpErrorsException(csvErrorList.getErrors());
        }

        for (PatientBatchOpHelper importInfo : patientImportInfos) {
            addPatient(context, importInfo);
        }

        return new BooleanResult(result);
    }

    private void addPatient(ActionContext context,
        PatientBatchOpHelper importInfo) {
        Patient patient = importInfo.getNewPatient();
        context.getSession().saveOrUpdate(patient);
    }

}
