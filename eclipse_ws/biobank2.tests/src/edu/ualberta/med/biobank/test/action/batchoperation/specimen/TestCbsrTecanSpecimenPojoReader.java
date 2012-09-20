package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import java.io.FileReader;

import org.junit.Assert;
import org.junit.Test;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.batchoperation.specimen.CbsrTecanSpecimenPojoReader;
import edu.ualberta.med.biobank.batchoperation.specimen.SpecimenPojoReaderFactory;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.test.action.TestAction;

/**
 * 
 * @author Nelson Loyola
 * 
 */
public class TestCbsrTecanSpecimenPojoReader extends TestAction {

    @Test
    public void parseTestFile() throws Exception {
        ICsvBeanReader reader =
            new CsvBeanReader(new FileReader("tecan_test.csv"),
                CsvPreference.EXCEL_PREFERENCE);

        String[] csvHeaders = reader.getCSVHeader(true);

        Assert.assertTrue(csvHeaders.length > 0);

        IBatchOpPojoReader<SpecimenBatchOpInputPojo> pojoReader =
            SpecimenPojoReaderFactory.createPojoReader(csvHeaders);

        Assert.assertTrue(pojoReader instanceof CbsrTecanSpecimenPojoReader);

        try {
            pojoReader.getPojos();
            Assert.fail("reader not set on pojo reader and no exception");
        } catch (IllegalStateException e) {
            // intentionally empty
        }

        pojoReader.setReader(reader);
        // List<SpecimenBatchOpInputPojo> batchOpPojos =
        pojoReader.getPojos();
    }

}
