package edu.ualberta.med.biobank.batchoperation.specimen;

import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.model.Center;

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
        Center workingCenter, String filename, String[] csvHeaders) {
        IBatchOpPojoReader<SpecimenBatchOpInputPojo> pojoReader = null;

        if (SpecimenBatchOpPojoReader.isHeaderValid(csvHeaders)) {
            pojoReader = new SpecimenBatchOpPojoReader(workingCenter, filename);
        } else if (CbsrTecanSpecimenPojoReader.isHeaderValid(csvHeaders)) {
            pojoReader =
                new CbsrTecanSpecimenPojoReader(workingCenter, filename);
        } else if (OhsTecanSpecimenPojoReader.isHeaderValid(csvHeaders)) {
            pojoReader =
                new OhsTecanSpecimenPojoReader(workingCenter, filename);
        } else {
            throw new IllegalStateException("no batchOp pojo reader found");
        }

        return pojoReader;
    }

}
