package edu.ualberta.med.biobank.batchoperation.patient;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.exception.SuperCSVReflectionException;
import org.supercsv.io.ICsvBeanReader;

import edu.ualberta.med.biobank.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.batchoperation.ClientBatchOpInputErrorList;
import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.patient.PatientBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.patient.PatientBatchOpInputPojo;
import edu.ualberta.med.biobank.model.Center;

public class PatientBatchOpPojoReader implements
    IBatchOpPojoReader<PatientBatchOpInputPojo> {

    @SuppressWarnings("nls")
    private static final String CSV_FIRST_HEADER = "Study";

    @SuppressWarnings("nls")
    private static final String[] NAME_MAPPINGS = new String[] {
        "studyName",
        "patientNumber",
        "enrollmentDate",
        "comment"
    };

    private final Center workingCenter;

    private final String filename;

    private final ClientBatchOpInputErrorList errorList =
        new ClientBatchOpInputErrorList();

    private final Set<PatientBatchOpInputPojo> pojos =
        new LinkedHashSet<PatientBatchOpInputPojo>(0);

    public PatientBatchOpPojoReader(Center workingCenter, String filename) {
        this.workingCenter = workingCenter;
        this.filename = filename;
    }

    // cell processors have to be recreated every time the file is read
    @SuppressWarnings("nls")
    public CellProcessor[] getCellProcessors() {
        Map<String, CellProcessor> aMap = new LinkedHashMap<String, CellProcessor>();

        aMap.put(NAME_MAPPINGS[0], new StrNotNullOrEmpty());
        aMap.put(NAME_MAPPINGS[1], new StrNotNullOrEmpty());
        aMap.put(NAME_MAPPINGS[2], new Optional(new ParseDate("yyyy-MM-dd HH:mm")));
        aMap.put(NAME_MAPPINGS[3], new Optional());

        if (aMap.size() != NAME_MAPPINGS.length) {
            throw new IllegalStateException(
                "the number of name mappings do not match the cell processors");
        }

        return aMap.values().toArray(new CellProcessor[0]);
    }

    public static boolean isHeaderValid(String[] csvHeaders) {
        return csvHeaders[0].equals(CSV_FIRST_HEADER)
            && (csvHeaders.length == NAME_MAPPINGS.length);
    }

    @Override
    public Set<PatientBatchOpInputPojo> readPojos(ICsvBeanReader reader)
        throws ClientBatchOpErrorsException, IOException {

        PatientBatchOpInputPojo csvPojo;

        CellProcessor[] cellProcessors = getCellProcessors();

        try {
            while ((csvPojo = reader.read(PatientBatchOpInputPojo.class,
                NAME_MAPPINGS, cellProcessors)) != null) {

                csvPojo.setLineNumber(reader.getLineNumber());
                pojos.add(csvPojo);
            }

            return pojos;
        } catch (SuperCSVReflectionException e) {
            throw new ClientBatchOpErrorsException(e);
        } catch (SuperCSVException e) {
            throw new ClientBatchOpErrorsException(e);
        } catch (IOException e) {
            throw new ClientBatchOpErrorsException(e);
        }
    }

    @Override
    public ClientBatchOpInputErrorList getErrorList() {
        return errorList;
    }

    @Override
    public Action<IdResult> getAction() throws NoSuchAlgorithmException, IOException {
        return new PatientBatchOpAction(workingCenter, pojos,
            new File(filename));
    }

}
