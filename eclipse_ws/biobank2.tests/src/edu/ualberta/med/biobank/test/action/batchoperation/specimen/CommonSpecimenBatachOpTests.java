package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpActionErrors.CSV_CURRENT_CENTER_SHORT_NAME_ERROR;
import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpActionErrors.CSV_ORIGIN_CENTER_SHORT_NAME_ERROR;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.specimen.IBatchOpSpecimenInputPojo;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.action.batchoperation.AssertBatchOpException;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;

public abstract class CommonSpecimenBatachOpTests<T extends IBatchOpSpecimenInputPojo>
    extends CommonSpecimenPositionBatchOpTests<T> {

    private static Logger log = LoggerFactory.getLogger(CommonSpecimenBatachOpTests.class);

    private Clinic defaultClinic;

    private Site defaultSite;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        session.beginTransaction();
        defaultSite = factory.createSite();
        defaultClinic = factory.createClinic();
        factory.createStudy();
    }

    protected void originCenterIsValid(IPojosCreator<T> pojosCreator) throws Exception {
        Set<T> pojos = pojosCreator.create();
        Assert.assertTrue("empty CSV data", pojos.size() > 0);

        // assign the origin center
        for (T pojo : pojos) {
            pojo.setOriginCenter(defaultClinic.getNameShort());
        }

        File file = writePojosToCsv(pojos);

        try {
            exec(createAction(pojos, file));
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    protected void originCenterIsInvalid(IPojosCreator<T> pojosCreator) throws Exception {
        for (String testCase : Arrays.asList("empty", "invalid")) {
            Set<T> pojos = pojosCreator.create();
            Assert.assertTrue("empty CSV data", pojos.size() > 0);

            // change the origin center to something invalid
            if (testCase.equals("empty")) {
                for (T pojo : pojos) {
                    pojo.setOriginCenter(StringUtil.EMPTY_STRING);
                }
            } else {
                for (T pojo : pojos) {
                    pojo.setOriginCenter(nameGenerator.next(Center.class));
                }
            }

            File file = writePojosToCsv(pojos);

            try {
                exec(createAction(pojos, file));
                Assert.fail("should fail");
            } catch (BatchOpErrorsException e) {
                CsvUtil.showErrorsInLog(log, e);
                new AssertBatchOpException()
                    .withMessage(CSV_ORIGIN_CENTER_SHORT_NAME_ERROR.format())
                    .assertIn(e);
            }
        }
    }

    protected void currentCenterIsValid(IPojosCreator<T> pojosCreator) throws Exception {
        Set<T> pojos = pojosCreator.create();
        Assert.assertTrue("empty CSV data", pojos.size() > 0);

        for (T pojo : pojos) {
            pojo.setCurrentCenter(defaultSite.getNameShort());
        }

        File file = writePojosToCsv(pojos);

        try {
            exec(createAction(pojos, file));
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);

    }

    protected void currentCenterIsInvalid(IPojosCreator<T> pojosCreator) throws Exception {
        for (String testCase : Arrays.asList("empty", "invalid")) {
            Set<T> pojos = pojosCreator.create();
            Assert.assertTrue("empty CSV data", pojos.size() > 0);

            if (testCase.equals("empty")) {
                for (T pojo : pojos) {
                    pojo.setCurrentCenter(StringUtil.EMPTY_STRING);
                }
            } else {
                for (T pojo : pojos) {
                    pojo.setCurrentCenter(nameGenerator.next(Center.class));
                }
            }

            File file = writePojosToCsv(pojos);

            try {
                exec(createAction(pojos, file));
                Assert.fail("should fail");
            } catch (BatchOpErrorsException e) {
                CsvUtil.showErrorsInLog(log, e);
                new AssertBatchOpException()
                    .withMessage(CSV_CURRENT_CENTER_SHORT_NAME_ERROR.format())
                    .assertIn(e);
            }
        }

    }

    @Override
    protected void checkAgainstDb(T pojo, Specimen specimen) {
        super.checkAgainstDb(pojo, specimen);
        if (pojo.getPatientNumber() != null) {
            Assert.assertEquals(pojo.getPatientNumber(),
                                specimen.getCollectionEvent().getPatient().getPnumber());
        }

        if (pojo.getParentInventoryId() != null) {
            Assert.assertEquals(pojo.getParentInventoryId(),
                                specimen.getParentSpecimen().getInventoryId());
        }
    }

}
