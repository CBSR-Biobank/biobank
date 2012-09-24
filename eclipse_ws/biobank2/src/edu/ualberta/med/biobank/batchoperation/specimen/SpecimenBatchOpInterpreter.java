package edu.ualberta.med.biobank.batchoperation.specimen;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
import edu.ualberta.med.biobank.i18n.LocalizedException;
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

    private final String filename;
    private IBatchOpPojoReader<SpecimenBatchOpInputPojo> pojoReader;
    private List<SpecimenBatchOpInputPojo> pojos;

    public SpecimenBatchOpInterpreter(String filename) {
        this.filename = filename;
    }

    public void readPojos() throws IOException {
        ICsvBeanReader reader = new CsvBeanReader(
            new FileReader(filename), CsvPreference.EXCEL_PREFERENCE);

        try {
            String[] csvHeaders = reader.getCSVHeader(true);

            if (csvHeaders.length < 1) {
                throw new LocalizedException(BatchOpActionUtil.CSV_HEADER_ERROR);
            }

            pojoReader = SpecimenPojoReaderFactory.createPojoReader(csvHeaders);
            pojoReader.setReader(reader);
            pojos = pojoReader.getPojos();
            ClientBatchOpInputErrorList errorList = pojoReader.getErrorList();

            if (!errorList.isEmpty()) {
                throw new ClientBatchOpErrorsException(errorList.getErrors());
            }
        } catch (SuperCSVException e) {
            throw new IllegalStateException(
                i18n.tr(BatchOpActionUtil.CSV_PARSE_ERROR, e.getMessage(),
                    e.getCsvContext()));
        } finally {
            reader.close();
        }
    }

    public Integer savePojos()
        throws NoSuchAlgorithmException, ApplicationException, IOException {
        if (pojos == null || pojoReader == null) {
            throw new IllegalStateException();
        }

        Integer batchOpId = null;

        pojoReader.preExecution();

        Center currentWorkingCenter = SessionManager.getUser()
            .getCurrentWorkingCenter().getWrappedObject();
        BiobankApplicationService service = SessionManager.getAppService();
        batchOpId = service.doAction(
            new SpecimenBatchOpAction(currentWorkingCenter, pojos,
                new File(filename))).getId();

        pojoReader.postExecution();

        return batchOpId;
    }
}
