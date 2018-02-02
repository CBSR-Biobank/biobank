package edu.ualberta.med.biobank.test.action.batchoperation.specimen.position;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.PositionBatchOpActionErrors.CSV_SPECIMEN_HAS_NO_POSITION_ERROR;
import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.PositionBatchOpActionErrors.CSV_SPECIMEN_PALLET_LABEL_INVALID_ERROR;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.PositionBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.PositionBatchOpPojo;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.SpecimenPositionBatchOpGetAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.SpecimenPositionBatchOpGetResult;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.action.batchoperation.AssertBatchOpException;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;
import edu.ualberta.med.biobank.test.action.batchoperation.specimen.CommonSpecimenPositionBatchOpTests;

public class TestPositionBatchOp extends CommonSpecimenPositionBatchOpTests<PositionBatchOpPojo> {

    private static Logger log = LoggerFactory.getLogger(TestPositionBatchOp.class);

    private static final String CSV_NAME = "import_specimen_positions.csv";

    private NameGenerator nameGenerator;

    private final Set<OriginInfo> originInfos = new HashSet<OriginInfo>();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        nameGenerator = new NameGenerator("test_" + getMethodNameR());

        // delete the CSV file if it exists
        File file = new File(CSV_NAME);
        file.delete();

        session.beginTransaction();
        factory.createStudy();

        // add 2 shipments
        //
        // source center on origin info will be the clinic created above
        factory.createShipmentInfo();
        originInfos.add(factory.createOriginInfo());
        factory.createShipmentInfo();
        originInfos.add(factory.createOriginInfo());
        session.getTransaction().commit();
    }

    @Test
    public void noErrors() throws Exception {
        // for positions use both barcodes and labels
        for (boolean useProductBarcode : Arrays.asList(true, false)) {
            TestFixture fixture = createFixture(3);
            Set<Container> containers = fixture.createContainers(fixture.getAllSpecimenTypes());
            Set<PositionBatchOpPojo> pojos =
                PositionBatchOpPojoHelper.createPojosWithPositions(factory.getDefaultStudy(),
                                                              fixture.getPatients(),
                                                              containers,
                                                              useProductBarcode);
            for (PositionBatchOpPojo pojo : pojos) {
                pojo.setComment(nameGenerator.next(String.class));
            }

            Assert.assertTrue("no CSV data", pojos.size() > 0);

            // write out to CSV file so we can view data
            PositionBatchOpCsvWriter.write(CSV_NAME, pojos);
            try {
                exec(createAction(pojos));
            } catch (BatchOpErrorsException e) {
                CsvUtil.showErrorsInLog(log, e);
                Assert.fail("errors in CVS data: " + e.getMessage());
            }

            checkAgainstDb(pojos);
        }
    }

    @Test
    public void moveSpecimens() throws Exception {
        for (boolean useProductBarcode : Arrays.asList(true, false)) {
            TestFixture fixture = createFixture(3);

            Set<Container> containers = fixture.createContainers(fixture.getAllSpecimenTypes());
            Set<Container> unfilledContainers = fixture.createContainers(fixture.getAllSpecimenTypes());
            fixture.fillContainers(containers, fixture.getSpecimens());

            Set<PositionBatchOpPojo> pojos =
                PositionBatchOpPojoHelper.createPojosWithPositions(factory.getDefaultStudy(),
                                                              fixture.getPatients(),
                                                              unfilledContainers,
                                                              useProductBarcode);
            Assert.assertTrue("no CSV data", pojos.size() > 0);

            // write out to CSV file so we can view data
            PositionBatchOpCsvWriter.write(CSV_NAME, pojos);

            try {
                exec(createAction(pojos));
            } catch (BatchOpErrorsException e) {
                CsvUtil.showErrorsInLog(log, e);
                Assert.fail("errors in CVS data: " + e.getMessage());
            }

            checkAgainstDb(pojos);
        }
    }

    @Test
    public void withInvalidCurrentPosition() throws Exception {
        LString expectedError;
        for (String testCase : Arrays.asList("empty", "invalid")) {
            for (boolean useProductBarcode : Arrays.asList(true, false)) {
                TestFixture fixture = createFixture(3);

                Set<Container> containers = fixture.createContainers(fixture.getAllSpecimenTypes());

                Set<PositionBatchOpPojo> pojos =
                    PositionBatchOpPojoHelper.createPojosWithPositions(factory.getDefaultStudy(),
                                                                  fixture.getPatients(),
                                                                  containers,
                                                                  useProductBarcode);
                Assert.assertTrue("no CSV data", pojos.size() > 0);

                if (testCase.equals("empty")) {
                    expectedError = CSV_SPECIMEN_HAS_NO_POSITION_ERROR;
                    for (PositionBatchOpPojo pojo : pojos) {
                        pojo.setCurrentPalletLabel(StringUtil.EMPTY_STRING);
                    }
                } else {
                    expectedError = CSV_SPECIMEN_PALLET_LABEL_INVALID_ERROR.format();
                    fixture.fillContainers(containers, fixture.getSpecimens());
                    for (PositionBatchOpPojo pojo : pojos) {
                        pojo.setCurrentPalletLabel(nameGenerator.next(Container.class));
                    }
                }

                // write out to CSV file so we can view data
                PositionBatchOpCsvWriter.write(CSV_NAME, pojos);

                try {
                    exec(createAction(pojos));
                    Assert.fail("should fail");
                } catch (BatchOpErrorsException e) {
                    CsvUtil.showErrorsInLog(log, e);
                    new AssertBatchOpException().withMessage(expectedError).assertIn(e);
                }
            }
        }
    }

    @Test
    public void withInvalidContainerInformation() throws Exception {
        final TestFixture fixture = createFixture(2);
        IPojosCreator<PositionBatchOpPojo> pojosCreator =
            new IPojosCreator<PositionBatchOpPojo>() {
                @Override
                public Set<PositionBatchOpPojo> create() {
                    return PositionBatchOpPojoHelper.createPojos(factory.getDefaultStudy(),
                                                                 fixture.getPatients());
                }
            };
        withInvalidContainerInformation(pojosCreator, true);
    }

    @Test
    public void withInvalidSpecimenPositions() throws Exception {
        final TestFixture fixture = createFixture(2);
        Set<SpecimenType> specimenTypes = fixture.getAllSpecimenTypes();

        IPojosCreator<PositionBatchOpPojo> pojosCreator =
            new IPojosCreator<PositionBatchOpPojo>() {
            @Override
            public Set<PositionBatchOpPojo> create() {
                return PositionBatchOpPojoHelper.createPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        };
        withInvalidSpecimenPositions(pojosCreator, specimenTypes);
    }

    @Test
    public void withInvalidSpecimenTypes() throws Exception {
        final TestFixture fixture = createFixture(2);
        withInvalidSpecimenTypes(new IPojosCreator<PositionBatchOpPojo>() {
            @Override
            public Set<PositionBatchOpPojo> create() {
                return PositionBatchOpPojoHelper.createPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        });
    }

    @Test
    public void withInvalidContainerType() throws Exception {
        final TestFixture fixture = createFixture(2);
        Set<SpecimenType> specimenTypes = fixture.getAllSpecimenTypes();

        IPojosCreator<PositionBatchOpPojo> pojosCreator =
            new IPojosCreator<PositionBatchOpPojo>() {
            @Override
            public Set<PositionBatchOpPojo> create() {
                return PositionBatchOpPojoHelper.createPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        };
        withInvalidContainerType(pojosCreator, specimenTypes);
    }

    @Test
    public void withPositionsAlreadyOccupied() throws Exception {
        final TestFixture fixture = createFixture(2);
        Set<SpecimenType> specimenTypes = fixture.getAllSpecimenTypes();

        IPojosCreator<PositionBatchOpPojo> pojosCreator =
            new IPojosCreator<PositionBatchOpPojo>() {
            @Override
            public Set<PositionBatchOpPojo> create() {
                return PositionBatchOpPojoHelper.createPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        };
        withPositionsAlreadyOccupied(pojosCreator,
                                     specimenTypes,
                                     fixture.getPatients().iterator().next());
    }

    @Test
    public void positionBatchOpGetAction() throws Exception {
        TestFixture fixture = createFixture(3);

        Set<Container> containers = fixture.createContainers(fixture.getAllSpecimenTypes());
        Set<PositionBatchOpPojo> pojos =
            PositionBatchOpPojoHelper.createPositionPojosWithLabels(factory.getDefaultStudy(),
                                                                    fixture.getPatients(),
                                                                    containers);

        // write out to CSV file so we can view data
        PositionBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            Integer bachOpId = exec(createAction(pojos)).getId();
            checkAgainstDb(pojos);
            SpecimenPositionBatchOpGetResult batchOpResult =
                exec(new SpecimenPositionBatchOpGetAction(bachOpId));
            Assert.assertEquals(pojos.size(), batchOpResult.getSpecimenData().size());
            Assert.assertEquals(getGlobalAdmin().getLogin(), batchOpResult.getExecutedBy());

            Date timeNow = new Date();
            Assert.assertTrue("Dates aren't close enough to each other!",
                              (timeNow.getTime() - batchOpResult.getTimeExecuted().getTime()) < 10000);
            Assert.assertEquals(CSV_NAME, batchOpResult.getInput().getName());
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void errorsIfInventoryIdFoundMoreThanOnce() throws Exception {
        final TestFixture fixture = createFixture(3);
        final Set<Container> containers = fixture.createContainers(fixture.getAllSpecimenTypes());
        errorsIfInventoryIdFoundMoreThanOnce(new IPojosCreator<PositionBatchOpPojo>() {
            @Override
            public Set<PositionBatchOpPojo> create() {
                return PositionBatchOpPojoHelper.createPositionPojosWithLabels(factory.getDefaultStudy(),
                                                                               fixture.getPatients(),
                                                                               containers);
            }
        });
    }

    private PositionBatchOpAction createAction(Set<PositionBatchOpPojo> pojos)
        throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        return new PositionBatchOpAction(factory.getDefaultSite(),
                                         new HashSet<PositionBatchOpPojo>(pojos),
                                         new File(CSV_NAME));
    }

    private TestFixture createFixture(int numPatients) {
        TestFixture fixture = new TestFixture(session, factory, nameGenerator, numPatients);
        Assert.assertTrue(fixture.valid());
        return fixture;
    }

    @Override
    protected File writePojosToCsv(Set<PositionBatchOpPojo> pojos) throws IOException {
        PositionBatchOpCsvWriter.write(CSV_NAME, pojos);
        return new File(CSV_NAME);
    }

    @Override
    protected Action<IdResult> createAction(Set<PositionBatchOpPojo> pojos, File file)
        throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        return new PositionBatchOpAction(factory.getDefaultSite(), pojos, file);
    }

}
