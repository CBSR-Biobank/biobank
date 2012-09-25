package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.batchoperation.ClientBatchOpErrorsException;
import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.batchoperation.specimen.OhsTecanSpecimenPojoReader;
import edu.ualberta.med.biobank.batchoperation.specimen.SpecimenBatchOpInterpreter;
import edu.ualberta.med.biobank.batchoperation.specimen.SpecimenPojoReaderFactory;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpException;

public class TestOhsTecanSpecimenPojoReader {

    @Test
    public void parseTecanFile() throws Exception {
        ICsvBeanReader reader =
            new CsvBeanReader(new FileReader("tecan/ohs_tecan.csv"),
                CsvPreference.EXCEL_PREFERENCE);

        String[] csvHeaders = reader.getCSVHeader(true);

        Assert.assertTrue(csvHeaders.length > 0);

        IBatchOpPojoReader<SpecimenBatchOpInputPojo> pojoReader =
            SpecimenPojoReaderFactory.createPojoReader(csvHeaders);

        Assert.assertTrue(pojoReader instanceof OhsTecanSpecimenPojoReader);

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

    @Test
    public void processTecanFile() {
        final List<BatchOpException<?>> errors =
            new ArrayList<BatchOpException<?>>();

        try {
            SpecimenBatchOpInterpreter interpreter =
                new SpecimenBatchOpInterpreter("tecan/ohs_tecan.csv");
            interpreter.readPojos();
            Integer batchOpId = interpreter.savePojos();
        } catch (ClientBatchOpErrorsException e) {
            errors.addAll(e.getErrors());
        } catch (BatchOpErrorsException e) {
            errors.addAll(e.getErrors());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
