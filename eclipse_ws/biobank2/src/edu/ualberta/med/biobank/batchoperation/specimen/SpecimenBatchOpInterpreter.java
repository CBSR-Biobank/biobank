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
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputRow;
import edu.ualberta.med.biobank.forms.DecodeImageForm;
import edu.ualberta.med.biobank.i18n.LocalizedException;

public class SpecimenBatchOpInterpreter {
    private static final I18n i18n = I18nFactory
        .getI18n(DecodeImageForm.class);

    private ClientBatchOpInputErrorList errorList =
        new ClientBatchOpInputErrorList();

    public SpecimenBatchOpInterpreter() {

    }

    public List<SpecimenBatchOpInputRow> setCsvFile(String filename)
        throws IOException {
        ICsvBeanReader reader = new CsvBeanReader(
            new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

        try {
            List<SpecimenBatchOpInputRow> csvInfos =
                new ArrayList<SpecimenBatchOpInputRow>(0);

            String[] csvHeader = reader.getCSVHeader(true);

            if (csvHeader.length < 1) {
                throw new LocalizedException(BatchOpActionUtil.CSV_HEADER_ERROR);
            }

            if (LegacyImportBeanReader.isHeaderValid(reader)) {
                LegacyImportBeanReader beanReader =
                    new LegacyImportBeanReader();
                csvInfos = beanReader.getBeans(reader);
                errorList = beanReader.getErrorList();

            } else if (false /* check for CBSR TECAN file */) {

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
