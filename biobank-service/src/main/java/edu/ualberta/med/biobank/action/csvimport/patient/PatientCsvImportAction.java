package edu.ualberta.med.biobank.action.csvimport.patient;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.BooleanResult;
import edu.ualberta.med.biobank.action.csvimport.CsvActionUtil;
import edu.ualberta.med.biobank.action.csvimport.CsvErrorList;
import edu.ualberta.med.biobank.action.csvimport.specimen.SpecimenCsvImportAction;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.exception.CsvImportException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;

/**
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public class PatientCsvImportAction implements Action<BooleanResult> {
    private static final long serialVersionUID = 1L;

    private static final Bundle bundle = new CommonBundle();

    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenCsvImportAction.class);

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

    private final CsvErrorList csvErrorList = new CsvErrorList();

    private CompressedReference<ArrayList<PatientCsvInfo>> compressedList =
        null;

    private final Set<PatientImportInfo> patientImportInfos =
        new HashSet<PatientImportInfo>(0);

    public PatientCsvImportAction(String filename) throws IOException {
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
            ArrayList<PatientCsvInfo> patientCsvInfos =
                new ArrayList<PatientCsvInfo>(0);

            PatientCsvInfo patientCsvInfo;
            reader.getCSVHeader(true);
            while ((patientCsvInfo =
                reader.read(PatientCsvInfo.class, header, PROCESSORS)) != null) {
                patientCsvInfos.add(patientCsvInfo);
            }

            compressedList =
                new CompressedReference<ArrayList<PatientCsvInfo>>(
                    patientCsvInfos);

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
            throw new LocalizedException(CSV_FILE_ERROR);
        }

        boolean result = false;

        ArrayList<PatientCsvInfo> patientCsvInfos = compressedList.get();
        for (PatientCsvInfo csvInfo : patientCsvInfos) {
            Study study =
                CsvActionUtil.getStudy(context, csvInfo.getStudyName());

            if (study == null) {
                csvErrorList.addError(csvInfo.getLineNumber(),
                    CSV_STUDY_ERROR.format(csvInfo.getStudyName()));
                continue;
            }

            PatientImportInfo importInfo = new PatientImportInfo(csvInfo);
            importInfo.setStudy(study);
            patientImportInfos.add(importInfo);
        }

        if (!csvErrorList.isEmpty()) {
            throw new CsvImportException(csvErrorList.getErrors());
        }

        for (PatientImportInfo importInfo : patientImportInfos) {
            addPatient(context, importInfo);
        }

        return new BooleanResult(result);
    }

    private void addPatient(ActionContext context, PatientImportInfo importInfo) {
        Patient patient = importInfo.getNewPatient();
        context.getSession().saveOrUpdate(patient);
    }

}
