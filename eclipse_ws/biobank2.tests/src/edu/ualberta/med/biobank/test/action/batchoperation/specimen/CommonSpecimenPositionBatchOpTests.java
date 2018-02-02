package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpActionErrors.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.IBatchOpSpecimenPositionPojo;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.action.TestAction;
import edu.ualberta.med.biobank.test.action.batchoperation.AssertBatchOpException;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;

/**
 * This base class encapsulates all the tests dealing with assigning positions to specimens using
 * specimen batch operations.
 *
 * @author nelson
 *
 * @param <T> The class for the pojo used in the CSV file.
 */
public abstract class CommonSpecimenPositionBatchOpTests<T extends IBatchOpSpecimenPositionPojo>
    extends TestAction {

    private static Logger log = LoggerFactory.getLogger(CommonSpecimenPositionBatchOpTests.class);

    private final Set<OriginInfo> originInfos = new HashSet<OriginInfo>();

    protected NameGenerator nameGenerator;

    protected Site defaultSite;

    protected Clinic defaultClinic;

    protected interface IPojosCreator<T> {
        public Set<T> create();
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        nameGenerator = new NameGenerator("test_" + getMethodNameR());

        session.beginTransaction();
        defaultSite = factory.createSite();
        defaultClinic = factory.createClinic();
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

    protected void withComments(IPojosCreator<T> pojosCreator) throws Exception {
        Set<T> pojos = pojosCreator.create();
        SpecimenBatchOpPojoHelper.addComments(pojos, nameGenerator);
        File file = writePojosToCsv(pojos);

        try {
            exec(createAction(pojos, file));
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    protected void noErrorsWithContainers(IPojosCreator<T> pojosCreator) throws Exception {
        Set<T> pojos = pojosCreator.create();
        Assert.assertTrue("empty CSV data", pojos.size() > 0);

        Study study = factory.getDefaultStudy();
        Set<SpecimenType> specimenTypes =
            SpecimenBatchOpPojoHelper.getStudySourceAndAliquotSpecimentTypes(study);
        Set<Container> childL2Containers =
            SpecimenBatchOpPojoHelper.createContainers(session,
                                                       factory,
                                                       nameGenerator,
                                                       specimenTypes,
                                                       5);

        SpecimenBatchOpPojoHelper.assignPositionsToPojos(pojos, childL2Containers, false);
        File file = writePojosToCsv(pojos);

        try {
            exec(createAction(pojos, file));
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    protected void withProductBarcodesAndPositions(IPojosCreator<T> pojosCreator) throws Exception {
        Set<T> pojos = pojosCreator.create();
        Assert.assertTrue("empty CSV data", pojos.size() > 0);

        Study study = factory.getDefaultStudy();
        Set<SpecimenType> specimenTypes =
            SpecimenBatchOpPojoHelper.getStudySourceAndAliquotSpecimentTypes(study);
        Set<Container> childL2Containers =
            SpecimenBatchOpPojoHelper.createContainers(session,
                                                       factory,
                                                       nameGenerator,
                                                       specimenTypes,
                                                       5);

        SpecimenBatchOpPojoHelper.assignPositionsToPojos(pojos, childL2Containers, true);
        File file = writePojosToCsv(pojos);

        try {
            exec(createAction(pojos, file));
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkAgainstDb(pojos);
    }

    protected void withInvalidContainerInformation(IPojosCreator<T> pojosCreator,
                                                   boolean checkBothEmpty) throws Exception {
        Set<Container> childL2Containers =
            SpecimenBatchOpPojoHelper.createContainers(session,
                                                       factory,
                                                       nameGenerator,
                                                       factory.getDefaultSourceSpecimenType(),
                                                       5);


        LString expectedError;
        for (String testCase : Arrays.asList("bothEmpty", "empty", "invalid")) {
            if (testCase.equals("bothEmpty") && !checkBothEmpty) continue;

            for (boolean useProductBarcode : Arrays.asList(true, false)) {
                Set<T> pojos = pojosCreator.create();
                Assert.assertTrue("empty CSV data", pojos.size() > 0);
                SpecimenBatchOpPojoHelper.assignPositionsToPojos(pojos,
                                                                 childL2Containers,
                                                                 useProductBarcode);

                if (testCase.equals("bothEmpty")) {
                    expectedError = CSV_PALLET_POS_INFO_INALID_ERROR;
                    for (T pojo : pojos) {
                        pojo.setPalletProductBarcode(StringUtil.EMPTY_STRING);
                        pojo.setPalletLabel(StringUtil.EMPTY_STRING);
                        pojo.setPalletPosition(StringUtil.EMPTY_STRING);
                    }
                } else if (testCase.equals("empty")) {
                    expectedError = CSV_PALLET_POS_ERROR;
                    if (useProductBarcode) {
                        for (T pojo : pojos) {
                            pojo.setPalletProductBarcode(StringUtil.EMPTY_STRING);
                        }
                    } else {
                        for (T pojo : pojos) {
                            pojo.setPalletLabel(StringUtil.EMPTY_STRING);
                        }
                    }
                } else {
                    if (useProductBarcode) {
                        expectedError = CSV_CONTAINER_BARCODE_ERROR.format();
                        for (T pojo : pojos) {
                            pojo.setPalletProductBarcode(nameGenerator.next(Container.class));
                        }
                    } else {
                        expectedError = CSV_CONTAINER_LABEL_ROOT_CONTAINER_TYPE_ERROR.format();
                        for (T pojo : pojos) {
                            pojo.setPalletLabel(nameGenerator.next(Container.class));
                        }
                    }
                }

                File file = writePojosToCsv(pojos);
                try {
                    exec(createAction(pojos, file));
                    Assert.fail("should fail");
                } catch (BatchOpErrorsException e) {
                    CsvUtil.showErrorsInLog(log, e);
                    new AssertBatchOpException().withMessage(expectedError).assertIn(e);
                }
            }
        }
    }

    public void withInvalidSpecimenPositions(IPojosCreator<T> pojosCreator,
                                             Set<SpecimenType> specimenTypes) throws Exception {
        Set<Container> childL2Containers =
            SpecimenBatchOpPojoHelper.createContainers(session,
                                                       factory,
                                                       nameGenerator,
                                                       specimenTypes,
                                                       5);

        LString expectedError;
        for (String testCase : Arrays.asList("empty", "invalid")) {
            for (boolean useProductBarcode : Arrays.asList(true, false)) {
                Set<T> pojos = pojosCreator.create();
                Assert.assertTrue("empty CSV data", pojos.size() > 0);

                SpecimenBatchOpPojoHelper.assignPositionsToPojos(pojos,
                                                                 childL2Containers,
                                                                 useProductBarcode);
                if (testCase.equals("empty")) {
                    expectedError = useProductBarcode
                        ? CSV_PROD_BARCODE_NO_POS_ERROR
                        : CSV_PALLET_POS_ERROR;
                    for (T pojo : pojos) {
                        pojo.setPalletPosition(StringUtil.EMPTY_STRING);
                    }
                } else {
                    expectedError = CSV_SPECIMEN_LABEL_ERROR.format();
                    for (T pojo : pojos) {
                        pojo.setPalletPosition(nameGenerator.next(Container.class));
                    }
                }

                File file = writePojosToCsv(pojos);
                try {
                    exec(createAction(pojos, file));
                    Assert.fail("should fail");
                } catch (BatchOpErrorsException e) {
                    CsvUtil.showErrorsInLog(log, e);
                    new AssertBatchOpException().withMessage(expectedError).assertIn(e);
                }
            }
        }

    }

    public void withInvalidSpecimenTypes(IPojosCreator<T> pojosCreator) throws Exception {
        Set<SpecimenType> specimenTypes = new HashSet<SpecimenType>(0);
        Set<Container> childL2Containers =
            SpecimenBatchOpPojoHelper.createContainers(session,
                                                       factory,
                                                       nameGenerator,
                                                       specimenTypes,
                                                       5);
        for (boolean useProductBarcode : Arrays.asList(true, false)) {
            Set<T> pojos = pojosCreator.create();
            Assert.assertTrue("empty CSV data", pojos.size() > 0);

            SpecimenBatchOpPojoHelper.assignPositionsToPojos(pojos,
                                                             childL2Containers,
                                                             useProductBarcode);
            File file = writePojosToCsv(pojos);
            try {
                exec(createAction(pojos, file));
                Assert.fail("should fail");
            } catch (BatchOpErrorsException e) {
                CsvUtil.showErrorsInLog(log, e);
                new AssertBatchOpException()
                .withMessage(CSV_CONTAINER_SPC_TYPE_ERROR.format())
                .assertIn(e);
            }
        }
    }

    public void withInvalidContainerType(IPojosCreator<T> pojosCreator,
                                         Set<SpecimenType> specimenTypes) throws Exception {
        Set<Container> childL2Containers =
            SpecimenBatchOpPojoHelper.createContainers(session,
                                                       factory,
                                                       nameGenerator,
                                                       specimenTypes,
                                                       5);

        LString expectedError;
        for (String testCase : Arrays.asList("empty", "invalid")) {
            Set<T> pojos = pojosCreator.create();
            Assert.assertTrue("empty CSV data", pojos.size() > 0);

            SpecimenBatchOpPojoHelper.assignPositionsToPojos(pojos,
                                                             childL2Containers,
                                                             false);

            if (testCase.equals("empty")) {
                expectedError = CSV_PALLET_LABEL_NO_CTYPE_ERROR;
                for (T pojo : pojos) {
                    pojo.setRootContainerType(StringUtil.EMPTY_STRING);
                }
            } else {
                expectedError = CSV_CONTAINER_LABEL_ROOT_CONTAINER_TYPE_ERROR.format();
                for (T pojo : pojos) {
                    pojo.setRootContainerType(nameGenerator.next(ContainerType.class));
                }
            }

            File file = writePojosToCsv(pojos);
            try {
                exec(createAction(pojos, file));
                Assert.fail("should fail");
            } catch (BatchOpErrorsException e) {
                CsvUtil.showErrorsInLog(log, e);
                new AssertBatchOpException().withMessage(expectedError).assertIn(e);
            }
        }
    }

    public void withPositionsAlreadyOccupied(IPojosCreator<T> pojosCreator,
                                             Set<SpecimenType> specimenTypes,
                                             Patient patient) throws Exception {
        Set<Container> childL2Containers =
            SpecimenBatchOpPojoHelper.createContainers(session,
                                                       factory,
                                                       nameGenerator,
                                                       specimenTypes,
                                                       5);

        SpecimenBatchOpPojoHelper.fillContainerWithSecimens(session,
                                                            factory,
                                                            childL2Containers.iterator().next(),
                                                            patient);

        for (boolean useProductBarcode : Arrays.asList(true, false)) {
            Set<T> pojos = pojosCreator.create();
            Assert.assertTrue("empty CSV data", pojos.size() > 0);

            SpecimenBatchOpPojoHelper.assignPositionsToPojos(pojos,
                                                             childL2Containers,
                                                             useProductBarcode);

            File file = writePojosToCsv(pojos);
            try {
                exec(createAction(pojos, file));
                Assert.fail("should fail");
            } catch (BatchOpErrorsException e) {
                CsvUtil.showErrorsInLog(log, e);

                LString expectedError = useProductBarcode ? CSV_CONTAINER_POS_OCCUPIED_ERROR.format()
                                                         : CSV_LABEL_POS_OCCUPIED_ERROR.format();

                new AssertBatchOpException().withMessage(expectedError).assertIn(e);
            }
        }
    }

    protected void errorsIfInventoryIdFoundMoreThanOnce(IPojosCreator<T> pojosCreator) throws Exception {
        Set<T> pojos = pojosCreator.create();
        Assert.assertTrue("empty CSV data", pojos.size() > 0);

        T firstPojo = pojos.iterator().next();
        for (T pojo : pojos) {
            pojo.setInventoryId(firstPojo.getInventoryId());
        }

        File file = writePojosToCsv(pojos);
        exec(createAction(pojos, file));
    }

    protected abstract File writePojosToCsv(Set<T> pojos) throws IOException;

    protected abstract Action<IdResult> createAction(Set<T> pojos, File file)
        throws IOException, NoSuchAlgorithmException, ClassNotFoundException;

    protected void checkAgainstDb(Set<T> pojos) {
        for (T pojo : pojos) {
            Specimen specimen = (Specimen) session.createCriteria(Specimen.class)
                .add(Restrictions.eq("inventoryId", pojo.getInventoryId()))
                .uniqueResult();

            checkAgainstDb(pojo, specimen);
        }
    }

    protected void checkAgainstDb(T pojo, Specimen specimen) {
        log.trace("checking specimen against db: inventory id {}", pojo.getInventoryId());

        session.refresh(specimen);
        Assert.assertNotNull(specimen);

        if ((pojo.getPalletPosition() != null) && !pojo.getPalletPosition().isEmpty()) {
            SpecimenPosition specimenPosition = specimen.getSpecimenPosition();
            Assert.assertNotNull(specimenPosition);
            Assert.assertEquals(pojo.getPalletPosition(), specimenPosition.getPositionString());

            String label = pojo.getPalletLabel();
            boolean hasLabel = (label != null) && !label.isEmpty();

            if (hasLabel) {
                Assert.assertEquals(label, specimenPosition.getContainer().getLabel());
            } else {
                String barcode = pojo.getPalletProductBarcode();
                Assert.assertEquals(barcode,
                                    specimenPosition.getContainer().getProductBarcode());
            }

        }

        if ((pojo.getComment() != null) && !pojo.getComment().isEmpty()) {
            Assert.assertEquals(1, specimen.getComments().size());
            Assert.assertEquals(pojo.getComment(),
                                specimen.getComments().iterator().next().getMessage());

        }
    }

}
