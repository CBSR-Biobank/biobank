package edu.ualberta.med.biobank.batchoperation.specimen;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.batchoperation.ClientBatchOpInputErrorList;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.forms.DecodeImageForm;
import edu.ualberta.med.biobank.i18n.LocalizedException;

/**
 * Reads a CSV file containing specimen information and returns a list of
 * {@link SpecimenBatchOpInputPojo} that can be passed to the action
 * {@link SpecimenBatchOpAction} to persist to the database.
 * 
 * @author Nelson Loyola
 * 
 */
public class SpecimenBatchOpInterpreter {
    private static final I18n i18n = I18nFactory
        .getI18n(DecodeImageForm.class);

    private ClientBatchOpInputErrorList errorList =
        new ClientBatchOpInputErrorList();

    public SpecimenBatchOpInterpreter() {

    }

    public List<SpecimenBatchOpInputPojo> setCsvFile(String filename)
        throws IOException {
        ICsvBeanReader reader = new CsvBeanReader(
            new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

        try {
            List<SpecimenBatchOpInputPojo> csvInfos =
                new ArrayList<SpecimenBatchOpInputPojo>(0);

            String[] csvHeader = reader.getCSVHeader(true);

            if (csvHeader.length < 1) {
                throw new LocalizedException(BatchOpActionUtil.CSV_HEADER_ERROR);
            }

            String[] csvHeaders = reader.getCSVHeader(true);

            if (LegacyImportSpecimenPojoReader.isHeaderValid(csvHeaders)) {
                LegacyImportSpecimenPojoReader beanReader =
                    new LegacyImportSpecimenPojoReader();
                csvInfos = beanReader.getBeans(reader);
                errorList = beanReader.getErrorList();

            } else if (CbsrTecanSpecimenPojoReader.isHeaderValid(csvHeaders)) {
                CbsrTecanSpecimenPojoReader beanReader =
                    new CbsrTecanSpecimenPojoReader();
                csvInfos = beanReader.getBeans(reader);
                errorList = beanReader.getErrorList();
            } else if (false /* check for OHS TECAN file */) {

            } else {
                // TODO: throw exception stating that file is an invalid format
            }

            if (!errorList.isEmpty()) {
                throw new ClientBatchOpErrorsException(errorList.getErrors());
            }

            return csvInfos;

        } catch (SuperCSVException e) {
            throw new IllegalStateException(
                i18n.tr(BatchOpActionUtil.CSV_PARSE_ERROR, e.getMessage(),
                    e.getCsvContext()));
        } finally {
            reader.close();
        }
    }

}
