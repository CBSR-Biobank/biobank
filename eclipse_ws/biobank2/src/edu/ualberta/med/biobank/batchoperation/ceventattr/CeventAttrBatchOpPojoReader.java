package edu.ualberta.med.biobank.batchoperation.ceventattr;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.supercsv.cellprocessor.ParseInt;
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
import edu.ualberta.med.biobank.common.action.batchoperation.ceventattr.CeventAttrBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.ceventattr.CeventAttrBatchOpInputPojo;
import edu.ualberta.med.biobank.model.Center;

public class CeventAttrBatchOpPojoReader implements
    IBatchOpPojoReader<CeventAttrBatchOpInputPojo> {

    @SuppressWarnings("nls")
    private static final String CSV_FIRST_HEADER = "Patient Number";

    @SuppressWarnings("nls")
    private static final String[] NAME_MAPPINGS = new String[] {
        "patientNumber",
        "visitNumber",
        "attrName",
        "attrValue"
    };

    private final Center workingCenter;

    private final String filename;

    private final ClientBatchOpInputErrorList errorList =
        new ClientBatchOpInputErrorList();

    private final Set<CeventAttrBatchOpInputPojo> pojos =
        new LinkedHashSet<CeventAttrBatchOpInputPojo>(0);

    public CeventAttrBatchOpPojoReader(Center workingCenter, String filename) {
        this.workingCenter = workingCenter;
        this.filename = filename;
    }

    // cell processors have to be recreated every time the file is read
    @SuppressWarnings("nls")
    public CellProcessor[] getCellProcessors() {
        Map<String, CellProcessor> aMap = new LinkedHashMap<String, CellProcessor>();

        aMap.put(NAME_MAPPINGS[0], new StrNotNullOrEmpty());
        aMap.put(NAME_MAPPINGS[1], new ParseInt());
        aMap.put(NAME_MAPPINGS[2], new StrNotNullOrEmpty());
        aMap.put(NAME_MAPPINGS[3], new StrNotNullOrEmpty());

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
    public Set<CeventAttrBatchOpInputPojo> readPojos(ICsvBeanReader reader)
        throws ClientBatchOpErrorsException, IOException {

        CeventAttrBatchOpInputPojo csvPojo;

        CellProcessor[] cellProcessors = getCellProcessors();

        try {
            while ((csvPojo = reader.read(CeventAttrBatchOpInputPojo.class,
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
        return new CeventAttrBatchOpAction(workingCenter, pojos,
            new File(filename));
    }

}
