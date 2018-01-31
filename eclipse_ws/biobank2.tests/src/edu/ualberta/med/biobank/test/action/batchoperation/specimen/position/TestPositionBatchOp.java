package edu.ualberta.med.biobank.test.action.batchoperation.specimen.position;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpActionErrors.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.PositionBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.PositionBatchOpPojo;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.SpecimenPositionBatchOpGetAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.SpecimenPositionBatchOpGetResult;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.action.TestAction;
import edu.ualberta.med.biobank.test.action.batchoperation.AssertBatchOpException;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;

public class TestPositionBatchOp extends TestAction {

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
            List<PositionBatchOpPojo> pojos =
                PositionBatchOpPojoHelper.createPositionPojos(factory.getDefaultStudy(),
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

            checkCsvInfoAgainstDb(pojos);
        }
    }

    @Test
    public void moveSpecimens() throws Exception {
        for (boolean useProductBarcode : Arrays.asList(true, false)) {
            TestFixture fixture = createFixture(3);

            Set<Container> containers = fixture.createContainers(fixture.getAllSpecimenTypes());
            Set<Container> unfilledContainers = fixture.createContainers(fixture.getAllSpecimenTypes());
            fixture.fillContainers(containers, fixture.getSpecimens());

            List<PositionBatchOpPojo> pojos =
                PositionBatchOpPojoHelper.createPositionPojos(factory.getDefaultStudy(),
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

            checkCsvInfoAgainstDb(pojos);
        }
    }

    @Test
    public void containersAlreadyOccupied() throws Exception {
        for (boolean useProductBarcode : Arrays.asList(true, false)) {
            TestFixture fixture = createFixture(3);

            Set<Container> containers = fixture.createContainers(fixture.getAllSpecimenTypes());
            fixture.fillContainers(containers, fixture.getSpecimens());

            List<PositionBatchOpPojo> pojos =
                PositionBatchOpPojoHelper.createPositionPojos(factory.getDefaultStudy(),
                                                              fixture.getPatients(),
                                                              containers,
                                                              useProductBarcode);
            Assert.assertTrue("no CSV data", pojos.size() > 0);

            // write out to CSV file so we can view data
            PositionBatchOpCsvWriter.write(CSV_NAME, pojos);

            try {
                exec(createAction(pojos));
                Assert.fail("should fail");
            } catch (BatchOpErrorsException e) {
                CsvUtil.showErrorsInLog(log, e);
                LString message;
                if (useProductBarcode) {
                    message = CSV_CONTAINER_POS_OCCUPIED_ERROR.format();
                } else {
                    message = CSV_LABEL_POS_OCCUPIED_ERROR.format();
                }
                new AssertBatchOpException().withMessage(message).assertIn(e);
            }
        }
    }

    @Test
    public void invalidPositionInfoWithBarcodes() throws Exception {
        List<LString> errors = Arrays.asList(CSV_PALLET_POS_ERROR,
                                             CSV_PROD_BARCODE_NO_POS_ERROR);

        for (LString error : errors) {
            TestFixture fixture = createFixture(3);

            Set<Container> containers = fixture.createContainers(fixture.getAllSpecimenTypes());
            List<PositionBatchOpPojo> pojos =
                PositionBatchOpPojoHelper.createPositionPojosWithBarcodes(factory.getDefaultStudy(),
                                                                          fixture.getPatients(),
                                                                          containers);
            Assert.assertTrue("no CSV data", pojos.size() > 0);

            for (PositionBatchOpPojo pojo : pojos) {
                if (error.equals(CSV_PALLET_POS_ERROR)) {
                    pojo.setPalletProductBarcode(StringUtil.EMPTY_STRING);
                } else {
                    pojo.setPalletPosition(StringUtil.EMPTY_STRING);
                }
            }

            // write out to CSV file so we can view data
            PositionBatchOpCsvWriter.write(CSV_NAME, pojos);

            try {
                exec(createAction(pojos));
                Assert.fail("should fail");
            } catch (BatchOpErrorsException e) {
                CsvUtil.showErrorsInLog(log, e);
                new AssertBatchOpException()
                    .withMessage(error)
                    .assertIn(e);
            }
        }

    }

    @Test
    public void invalidPositionInfoWithLabels() throws Exception {
        List<LString> errors = Arrays.asList(CSV_PALLET_POS_ERROR,
                                             CSV_PALLET_POS_INFO_INALID_ERROR,
                                             CSV_PALLET_LABEL_NO_CTYPE_ERROR);

        for (LString error : errors) {
            TestFixture fixture = createFixture(3);

            Set<Container> containers = fixture.createContainers(fixture.getAllSpecimenTypes());
            List<PositionBatchOpPojo> pojos =
                PositionBatchOpPojoHelper.createPositionPojosWithLabels(factory.getDefaultStudy(),
                                                                        fixture.getPatients(),
                                                                        containers);
            Assert.assertTrue("no CSV data", pojos.size() > 0);

            for (PositionBatchOpPojo pojo : pojos) {
                if (error.equals(CSV_PALLET_POS_ERROR)) {
                    pojo.setPalletPosition(StringUtil.EMPTY_STRING);
                } else if (error.equals(CSV_PALLET_POS_INFO_INALID_ERROR)) {
                    pojo.setPalletLabel(StringUtil.EMPTY_STRING);
                    pojo.setPalletPosition(StringUtil.EMPTY_STRING);
                } else {
                    pojo.setRootContainerType(StringUtil.EMPTY_STRING);
                }
            }

            // write out to CSV file so we can view data
            PositionBatchOpCsvWriter.write(CSV_NAME, pojos);

            try {
                exec(createAction(pojos));
                Assert.fail("should fail");
            } catch (BatchOpErrorsException e) {
                CsvUtil.showErrorsInLog(log, e);
                new AssertBatchOpException()
                    .withMessage(error)
                    .assertIn(e);
            }
        }

    }

    @Test
    public void positionBatchOpGetAction() throws Exception {
        TestFixture fixture = createFixture(3);

        Set<Container> containers = fixture.createContainers(fixture.getAllSpecimenTypes());
        List<PositionBatchOpPojo> pojos =
            PositionBatchOpPojoHelper.createPositionPojosWithLabels(factory.getDefaultStudy(),
                                                                    fixture.getPatients(),
                                                                    containers);

        // write out to CSV file so we can view data
        PositionBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            Integer bachOpId = exec(createAction(pojos)).getId();
            checkCsvInfoAgainstDb(pojos);
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

    private PositionBatchOpAction createAction(List<PositionBatchOpPojo> pojos)
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

    private void checkCsvInfoAgainstDb(List<PositionBatchOpPojo> pojos) {
        for (PositionBatchOpPojo pojo : pojos) {
            checkCsvInfoAgainstDb(pojo);
        }
    }

    private void checkCsvInfoAgainstDb(PositionBatchOpPojo pojo) {
        log.trace("checking specimen against db: inventory id {}", pojo.getInventoryId());

        Specimen specimen = (Specimen) session.createCriteria(Specimen.class)
            .add(Restrictions.eq("inventoryId", pojo.getInventoryId()))
            .uniqueResult();
        Assert.assertNotNull(specimen);
        session.refresh(specimen);

        SpecimenPosition specimenPosition = specimen.getSpecimenPosition();
        Assert.assertNotNull(specimenPosition);
        Assert.assertEquals(specimen.getId(), specimenPosition.getSpecimen().getId());
        Assert.assertEquals(pojo.getPalletPosition(), specimenPosition.getPositionString());

        String label = pojo.getPalletLabel();
        boolean hasLabel = (label != null) && !label.isEmpty();
        Container container = specimenPosition.getContainer();

        if (hasLabel) {
            Assert.assertEquals(label, container.getLabel());
        } else {
            Assert.assertEquals(pojo.getPalletProductBarcode(), container.getProductBarcode());
        }

        if ((pojo.getComment() != null) && !pojo.getComment().isEmpty()) {
            Set<Comment> comments = specimen.getComments();
            Assert.assertTrue(comments.size() > 0);
            boolean commentFound = false;
            for (Comment comment : comments) {
                if (comment.getMessage().equals(pojo.getComment())) {
                    commentFound = true;
                }
            }
            Assert.assertTrue(commentFound);
        }
    }

}
