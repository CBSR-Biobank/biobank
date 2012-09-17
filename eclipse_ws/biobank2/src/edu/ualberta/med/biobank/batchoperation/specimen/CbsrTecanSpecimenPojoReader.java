package edu.ualberta.med.biobank.batchoperation.specimen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.exception.SuperCSVReflectionException;
import org.supercsv.io.ICsvBeanReader;

import edu.ualberta.med.biobank.batchoperation.ClientBatchOpInputErrorList;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;

public class CbsrTecanSpecimenPojoReader {

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
        new ParseInt(),                     // volume          
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

    private final ClientBatchOpInputErrorList errorList =
        new ClientBatchOpInputErrorList();

    private final List<SpecimenBatchOpInputPojo> csvInfos =
        new ArrayList<SpecimenBatchOpInputPojo>(0);

    public static boolean isHeaderValid(String[] csvHeaders) {
        if (csvHeaders == null) {
            throw new NullPointerException("csvHeaders is null");
        }
        return csvHeaders[0].equals(CSV_FIRST_HEADER)
            && (csvHeaders.length == NAME_MAPPINGS.length);
    }

    public List<SpecimenBatchOpInputPojo> getBeans(ICsvBeanReader reader)
        throws SuperCSVReflectionException, SuperCSVException, IOException {
        final Map<String, SpecimenBatchOpInputPojo> parentSpcMap =
            new HashMap<String, SpecimenBatchOpInputPojo>();

        SpecimenBatchOpInputPojo csvPojos;

        while ((csvPojos =
            reader.read(SpecimenBatchOpInputPojo.class,
                NAME_MAPPINGS, CELL_PROCESSORS)) != null) {

        }
        return csvInfos;
    }

    public ClientBatchOpInputErrorList getErrorList() {
        // TODO Auto-generated method stub
        return null;
    }

}
