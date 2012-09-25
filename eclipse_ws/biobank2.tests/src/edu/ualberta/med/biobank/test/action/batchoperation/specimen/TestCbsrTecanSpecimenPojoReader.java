package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import edu.ualberta.med.biobank.batchoperation.IBatchOpPojoReader;
import edu.ualberta.med.biobank.batchoperation.specimen.CbsrTecanSpecimenPojoReader;
import edu.ualberta.med.biobank.batchoperation.specimen.SpecimenPojoReaderFactory;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.test.action.TestAction;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;

/**
 * 
 * @author Nelson Loyola
 * 
 */
public class TestCbsrTecanSpecimenPojoReader extends TestAction {

    private static Logger log = LoggerFactory
        .getLogger(TestSpecimenBatchOp.class.getName());

    private Transaction tx;

    @Test
    public void processTecanFile() throws Exception {
        final String CSV_NAME = "tecan/cbsr_tecan.csv";
        ICsvBeanReader reader =
            new CsvBeanReader(new FileReader(CSV_NAME),
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

        createDbConfiguration();

        pojoReader.setReader(reader);
        pojoReader.preExecution();

        List<SpecimenBatchOpInputPojo> batchOpPojos =
            pojoReader.getPojos();

        try {
            SpecimenBatchOpAction importAction =
                new SpecimenBatchOpAction(factory.getDefaultSite(),
                    batchOpPojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        pojoReader.postExecution();
    }

    private void createSpecimenTypes() {
        final String[] spcTypeNames = { "Hemo", "Serum" };

        for (String spcTypeName : spcTypeNames) {
            // create specimen type Buffy if it does not already exist
            SpecimenType stype =
                (SpecimenType) session.createCriteria(SpecimenType.class)
                    .add(Restrictions.eq("name", spcTypeName)).uniqueResult();

            if (stype == null) {
                stype = new SpecimenType();
                stype.setName(spcTypeName);
                stype.setNameShort(spcTypeName);
                session.save(stype);
            }
        }
    }

    private void createPatientsAndParentSpecimens() {
        final Map<String, List<String>> inventoryIdPatientMap =
            new LinkedHashMap<String, List<String>>();

        inventoryIdPatientMap.put("0087", Arrays.asList("SCTCIUNW5RIT"));
        inventoryIdPatientMap.put("AA0667", Arrays.asList("L4QVCOOCILSU"));
        inventoryIdPatientMap.put("GR5096", Arrays.asList("8LQS3FXTJMOC"));
        inventoryIdPatientMap.put("KDED10001", Arrays.asList("NM8W18AYOUJO"));
        inventoryIdPatientMap.put("NoReadBC1", Arrays.asList("USB92UDRFV3O"));
        inventoryIdPatientMap.put("RF100 0001", Arrays.asList("MGEO25X4315P"));
        inventoryIdPatientMap.put("TT0001",
            Arrays.asList("AIJ02OLX15T1", "LMK0QMF94I1M"));
        inventoryIdPatientMap.put("TT0050", Arrays.asList("OU412T6W7GSI"));
        inventoryIdPatientMap.put("VA0984", Arrays.asList("H7X6AIU7F9PR"));

        for (Entry<String, List<String>> entry : inventoryIdPatientMap
            .entrySet()) {
            Patient patient =
                (Patient) session.createCriteria(Patient.class)
                    .add(Restrictions.eq("pnumber", entry.getKey()))
                    .uniqueResult();

            if (patient != null) {
                CsvUtil.deletePatient(session, patient);
            }

            patient = factory.createPatient();
            patient.setPnumber(entry.getKey());
            session.update(patient);

            factory.createCollectionEvent();

            for (String inventoryId : entry.getValue()) {
                Specimen parentSpecimen = factory.createParentSpecimen();
                parentSpecimen.setInventoryId(inventoryId);
                session.update(parentSpecimen);
            }
        }
    }

    private void createDbConfiguration() {
        tx = session.beginTransaction();

        factory.createSite();
        factory.createClinic();
        factory.createStudy();

        createSpecimenTypes();
        createPatientsAndParentSpecimens();
        tx.commit();
    }

}
