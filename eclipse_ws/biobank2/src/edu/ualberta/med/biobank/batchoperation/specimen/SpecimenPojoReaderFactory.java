package edu.ualberta.med.biobank.batchoperation.specimen;

import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;

/**
 * Creates a IBatchOpPojoReader instance based on the headers in the CSV file.
 * 
 * @param csvHeaders the column names contained in the first row of the CSV file
 *            as an array of String.
 * 
 * @author Nelson Loyola
 * 
 */
public class SpecimenPojoReaderFactory {

    public static IBatchOpPojoReader<SpecimenBatchOpInputPojo> createPojoReader(
        String[] csvHeaders) {
        IBatchOpPojoReader<SpecimenBatchOpInputPojo> pojoReader = null;

        if (SpecimenBatchOpPojoReader.isHeaderValid(csvHeaders)) {
            pojoReader = new SpecimenBatchOpPojoReader();
        } else if (CbsrTecanSpecimenPojoReader.isHeaderValid(csvHeaders)) {
            pojoReader = new CbsrTecanSpecimenPojoReader();
        } else if (OhsTecanSpecimenPojoReader.isHeaderValid(csvHeaders)) {
            pojoReader = new OhsTecanSpecimenPojoReader();
        } else {
            throw new IllegalStateException("no batchOp pojo reader found");
        }

        return pojoReader;
    }

}
