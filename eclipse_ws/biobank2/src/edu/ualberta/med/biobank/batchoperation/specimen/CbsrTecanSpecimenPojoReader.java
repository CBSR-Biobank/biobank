package edu.ualberta.med.biobank.batchoperation.specimen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.ParseBigDecimal;
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
 * Reads a TECAN CSV file containing specimen information and returns the file
 * as a list of SpecimenBatchOpInputPojo.
 * 
 * @author Nelson Loyola
 * 
 */
@SuppressWarnings("nls")
public class CbsrTecanSpecimenPojoReader implements
    IBatchOpPojoReader<SpecimenBatchOpInputPojo> {

    private static final String CSV_FIRST_HEADER = "Inventory ID";

    // @formatter:off
    private static final String[] NAME_MAPPINGS = new String[] {
        null,                 // rackId - don't care
        "inventoryId",        // cavityId
        null,                 // position - don't care
        "parentInventoryId",  // sourceId
        null,                 // concentration - don't care
        null,                 // concentrationUnit - don't care
        "volume",             // volume
        "patientNumber",      // userDefined1
        "createdAt",          // userDefined2
        "",                   // userDefined3 - TBD
        "",                   // userDefined4 - TBD
        "",                   // userDefined5 - TBD
        "plateErrors",        // plateErrors
        "sampleErrors",       // samplEerrors
        null,                 // sampleInstanceId
        null                  // sampleId
    };
    // @formatter:on

    // @formatter:off
    private static final CellProcessor[] CELL_PROCESSORS =  new CellProcessor[] {
        null,                               // rackId          
        new Unique(),                       // cavityId        
        null,                               // position        
        new StrNotNullOrEmpty(),            // sourceId        
        null,                               // concentration   
        null,                               // concentrationUnit
        new ParseBigDecimal(),              // volume          
        new StrNotNullOrEmpty(),            // userDefined1    
        new StrNotNullOrEmpty(),            // userDefined2    
        new StrNotNullOrEmpty(),            // userDefined3    
        new StrNotNullOrEmpty(),            // userDefined4    
        new StrNotNullOrEmpty(),            // userDefined5    
        new StrNotNullOrEmpty(),            // plateErrors     
        new StrNotNullOrEmpty(),            // samplEerrors    
        null,                               // sampleInstanceId
        null,                               // sampleId2
        };
    // @formatter:on

    private ICsvBeanReader reader;

    private final ClientBatchOpInputErrorList errorList =
        new ClientBatchOpInputErrorList();

    private final List<SpecimenBatchOpInputPojo> csvInfos =
        new ArrayList<SpecimenBatchOpInputPojo>(0);

    @Override
    public void setReader(ICsvBeanReader reader) {
        this.reader = reader;
    }

    @Override
    public ClientBatchOpInputErrorList getErrorList() {
        return errorList;
    }

    public static boolean isHeaderValid(String[] csvHeaders) {
        if (csvHeaders == null) {
            throw new NullPointerException("csvHeaders is null");
        }
        return csvHeaders[0].equals(CSV_FIRST_HEADER)
            && (csvHeaders.length == NAME_MAPPINGS.length);
    }

    @Override
    public List<SpecimenBatchOpInputPojo> getPojos()
        throws ClientBatchOpErrorsException {
        List<SpecimenBatchOpInputPojo> result =
            new ArrayList<SpecimenBatchOpInputPojo>();

        final Map<String, SpecimenBatchOpInputPojo> parentSpcMap =
            new HashMap<String, SpecimenBatchOpInputPojo>();

        SpecimenBatchOpInputPojo csvPojo;

        try {
            while ((csvPojo =
                reader.read(SpecimenBatchOpInputPojo.class,
                    NAME_MAPPINGS, CELL_PROCESSORS)) != null) {

                result.add(csvPojo);

            }
            return result;
        } catch (SuperCSVReflectionException e) {
            throw new ClientBatchOpErrorsException(e);
        } catch (SuperCSVException e) {
            throw new ClientBatchOpErrorsException(e);
        } catch (IOException e) {
            throw new ClientBatchOpErrorsException(e);
        }
    }

    @Override
    public void preExecution() {
        // TODO Auto-generated method stub

    }

    @Override
    public void postExecution() {
        // TODO Auto-generated method stub

    }

}
