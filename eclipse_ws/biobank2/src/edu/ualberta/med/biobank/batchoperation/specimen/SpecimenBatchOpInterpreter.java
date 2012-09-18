package edu.ualberta.med.biobank.batchoperation.specimen;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.batchoperation.ClientBatchOpInputErrorList;
import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
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
            String[] csvHeader = reader.getCSVHeader(true);

            if (csvHeader.length < 1) {
                throw new LocalizedException(BatchOpActionUtil.CSV_HEADER_ERROR);
            }

            String[] csvHeaders = reader.getCSVHeader(true);

            IBatchOpPojoReader<SpecimenBatchOpInputPojo> pojoReader = null;

            if (SpecimenBatchOpPojoReader.isHeaderValid(csvHeaders)) {
                pojoReader = new SpecimenBatchOpPojoReader();
            } else if (CbsrTecanSpecimenPojoReader.isHeaderValid(csvHeaders)) {
                pojoReader = new CbsrTecanSpecimenPojoReader();
            } else if (false /* check for OHS TECAN file */) {

            } else {
                throw new IllegalStateException("no batchOp pojo reader found");
            }

            pojoReader.setReader(reader);
            errorList = pojoReader.getErrorList();

            if (!errorList.isEmpty()) {
                throw new ClientBatchOpErrorsException(errorList.getErrors());
            }

            return pojoReader.getPojos();

        } catch (SuperCSVException e) {
            throw new IllegalStateException(
                i18n.tr(BatchOpActionUtil.CSV_PARSE_ERROR, e.getMessage(),
                    e.getCsvContext()));
        } finally {
            reader.close();
        }
    }

}
