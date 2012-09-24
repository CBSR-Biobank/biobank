package edu.ualberta.med.biobank.batchoperation.specimen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.exception.SuperCSVReflectionException;
import org.supercsv.io.ICsvBeanReader;

import edu.ualberta.med.biobank.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.batchoperation.ClientBatchOpInputErrorList;
import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;

/**
 * Reads a CSV file containing specimen information and returns the file as a
 * list of SpecimenBatchOpInputPojo.
 * 
 * @author Nelson Loyola
 * 
 */
@SuppressWarnings("nls")
public class SpecimenBatchOpPojoReader implements
    IBatchOpPojoReader<SpecimenBatchOpInputPojo> {

    private static final String CSV_FIRST_HEADER = "Inventory ID";

    private static final String[] NAME_MAPPINGS = new String[] {
        "inventoryId",
        "parentInventoryId",
        "volume",
        "specimenType",
        "createdAt",
        "patientNumber",
        "visitNumber",
        "waybill",
        "sourceSpecimen",
        "worksheet",
        "palletProductBarcode",
        "rootContainerType",
        "palletLabel",
        "palletPosition",
        "comment"
    };

    private ICsvBeanReader reader;

    private final ClientBatchOpInputErrorList errorList =
        new ClientBatchOpInputErrorList();

    private final List<SpecimenBatchOpInputPojo> csvInfos =
        new ArrayList<SpecimenBatchOpInputPojo>(0);

    @Override
    public void setReader(ICsvBeanReader reader) {
        this.reader = reader;
    }

    // cell processors have to be recreated every time the file is read
    public CellProcessor[] getCellProcessors() {
        Map<String, CellProcessor> aMap =
            new LinkedHashMap<String, CellProcessor>();

        aMap.put("inventoryId", new Unique());
        aMap.put("parentInventoryId", new Optional());
        aMap.put("volume", new Optional(new ParseBigDecimal()));
        aMap.put("specimenType", new StrNotNullOrEmpty());
        aMap.put("createdAt", new ParseDate("yyyy-MM-dd HH:mm"));
        aMap.put("patientNumber", new Optional());
        aMap.put("visitNumber", new Optional(new ParseInt()));
        aMap.put("waybill", new Optional());
        aMap.put("sourceSpecimen", new ParseBool());
        aMap.put("worksheet", new Optional(new Unique()));
        aMap.put("palletProductBarcode", new Optional());
        aMap.put("rootContainerType", new Optional());
        aMap.put("palletLabel", new Optional());
        aMap.put("palletPosition", new Optional());
        aMap.put("comment", new Optional());

        if (aMap.size() != NAME_MAPPINGS.length) {
            throw new IllegalStateException(
                "the number of name mappings do match the cell processors");
        }

        return aMap.values().toArray(new CellProcessor[0]);
    }

    public static boolean isHeaderValid(String[] csvHeaders) {
        return csvHeaders[0].equals(CSV_FIRST_HEADER)
            && (csvHeaders.length == NAME_MAPPINGS.length);
    }

    @Override
    public List<SpecimenBatchOpInputPojo> getPojos()
        throws ClientBatchOpErrorsException {

        SpecimenBatchOpInputPojo csvPojo;

        CellProcessor[] cellProcessors = getCellProcessors();

        try {
            while ((csvPojo =
                reader.read(SpecimenBatchOpInputPojo.class,
                    NAME_MAPPINGS, cellProcessors)) != null) {

                csvPojo.setLineNumber(reader.getLineNumber());
                csvInfos.add(csvPojo);
            }

            return csvInfos;
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
    public void preExecution() {
        // does nothing
    }

    @Override
    public void postExecution() {
        // does nothing
    }
}
