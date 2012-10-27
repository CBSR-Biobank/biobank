package edu.ualberta.med.biobank.batchoperation.specimen;

import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.batchoperation.ClientBatchOpInputErrorList;
import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.forms.DecodeImageForm;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;

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

    private IBatchOpPojoReader<SpecimenBatchOpInputPojo> pojoReader;

    public Integer processFile(final String filename) throws IOException,
        NoSuchAlgorithmException, ApplicationException {
        ICsvBeanReader reader = new CsvBeanReader(
            new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

        try {
            String[] csvHeaders = reader.getCSVHeader(true);

            if ((csvHeaders == null) || (csvHeaders.length < 1)) {
                throw new IllegalStateException(
                    i18n.tr("Invalid headers in CSV file."));
            }

            Center currentWorkingCenter = SessionManager.getUser()
                .getCurrentWorkingCenter().getWrappedObject();

            pojoReader = SpecimenPojoReaderFactory.createPojoReader(
                currentWorkingCenter, filename, csvHeaders);
            pojoReader.readPojos(reader);

            ClientBatchOpInputErrorList errorList = pojoReader.getErrorList();

            if (!errorList.isEmpty()) {
                throw new ClientBatchOpErrorsException(errorList.getErrors());
            }

            Integer batchOpId = null;

            BiobankApplicationService service = SessionManager.getAppService();
            batchOpId = service.doAction(pojoReader.getAction()).getId();
            return batchOpId;
        } catch (SuperCSVException e) {
            throw new IllegalStateException(
                i18n.tr(BatchOpActionUtil.CSV_PARSE_ERROR, e.getMessage(),
                    e.getCsvContext()));
        } finally {
            reader.close();
        }
    }
}
