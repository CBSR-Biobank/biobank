package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpActionErrors.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpGetResult;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.GrandchildSpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.GrandchildSpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpGetAction;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.test.Factory;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.action.TestAction;
import edu.ualberta.med.biobank.test.action.batchoperation.AssertBatchOpException;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;

/**
 *
 * @author Nelson Loyola
 *
 */
public class TestGrandchildSpecimenBatchOp extends TestAction {

    private static Logger log = LoggerFactory.getLogger(TestGrandchildSpecimenBatchOp.class);

    private static final String CSV_NAME = "import_grandchild_specimens.csv";

    private GrandchildSpecimenBatchOpPojoHelper specimenCsvHelper;

    private final Set<OriginInfo> originInfos = new HashSet<OriginInfo>();

    private NameGenerator nameGenerator;

    private TestFixture fixture;

    private Clinic defaultClinic;

    private Site defaultSite;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        nameGenerator = new NameGenerator("test_" + getMethodNameR());
        specimenCsvHelper = new GrandchildSpecimenBatchOpPojoHelper(nameGenerator);

        // delete the CSV file if it exists
        File file = new File(CSV_NAME);
        file.delete();

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
        fixture = new TestFixture(session, factory, 3, 3);
        session.getTransaction().commit();

        Assert.assertTrue(fixture.validate());
    }

    @Test
    public void noErrorsNoContainers() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(), fixture.getPatients());

        Assert.assertTrue("no CSV data", pojos.size() > 0);

        // write out to CSV file so we can view data
        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(pojos);
    }

    /**
     * Specimens should not be imported if the specimen type is not in the list of aliquoted
     * specimens for a study.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void studyAliquotedSpecimens() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        // change the specimen type to something invalid
        SpecimenType invalidSpecimenType = fixture.getParentSpecimenTypes().iterator().next();

        for (GrandchildSpecimenBatchOpInputPojo inputPojo : pojos) {
            inputPojo.setSpecimenType(invalidSpecimenType.getNameShort());
        }

        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_STUDY_ALIQUOTED_SPC_TYPE_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void patientNumberInvalidOrForOtherSpecimen() throws Exception {
        session.beginTransaction();
        Patient patient = factory.createPatient();
        session.getTransaction().commit();

        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        // set all patient numbers to null
        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setPatientNumber(patient.getPnumber());
        }
        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should not be allowed to import spcecimens when patient number is missing");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_PATIENT_NUMBER_MISMATCH_ERROR)
                .assertIn(e);
        }
    }

    @Test
    public void missingPatientNumber() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        // set all patient numbers to null
        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setPatientNumber("");
        }
        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should not be allowed to import spcecimens when patient number is missing");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_PATIENT_NUMBER_REQUIRED_ERROR)
                .assertIn(e);
        }
    }

    @Test
    public void invalidSpecimenType() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        // change the specimen type to something invalid
        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setSpecimenType(pojo.getSpecimenType() + "_x");
        }
        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should not be allowed to invalid specimen types");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_SPECIMEN_TYPE_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void missingSpecimenType() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        // change the specimen type to something invalid
        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setSpecimenType("");
        }
        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should not be allowed to invalid specimen types");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_SPECIMEN_TYPE_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void withComments() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        specimenCsvHelper.addComments(pojos);
        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(pojos);
    }

    @Test
    public void noErrorsWithContainers() throws Exception {
        session.beginTransaction();
        Set<Container> childL2Containers = createContainers(fixture.getGrandchildSpecimenTypes());
        session.getTransaction().commit();

        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        List<GrandchildSpecimenBatchOpInputPojo> pojosAsList =
            new ArrayList<GrandchildSpecimenBatchOpInputPojo>(pojos);

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(pojosAsList,
                                                                 childL2Containers,
                                                                 false);

        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(pojos);
    }

    @Test
    public void withProductBarcodesAndPositions() throws Exception {
        session.beginTransaction();
        Set<Container> childL2Containers = createContainers(fixture.getGrandchildSpecimenTypes());
        session.getTransaction().commit();

        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        List<GrandchildSpecimenBatchOpInputPojo> pojosAsList =
            new ArrayList<GrandchildSpecimenBatchOpInputPojo>(pojos);

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(pojosAsList,
                                                                 childL2Containers,
                                                                 true);

        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(pojos);
    }

    @Test
    public void onlyPalletPositions() throws Exception {
        session.beginTransaction();
        Set<Container> childL2Containers = createContainers(fixture.getGrandchildSpecimenTypes());
        session.getTransaction().commit();

        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        List<GrandchildSpecimenBatchOpInputPojo> pojosAsList =
            new ArrayList<GrandchildSpecimenBatchOpInputPojo>(pojos);

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(pojosAsList,
                                                                 childL2Containers,
                                                                 true);

        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setPalletProductBarcode("");
        }

        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_PALLET_POS_ERROR)
                .assertIn(e);
        }
    }

    @Test
    public void positionsByLabelAlreadyOccupied() throws Exception {
        Set<SpecimenType> specimenTypes = fixture.getParentSpecimenTypes();
        specimenTypes.addAll(fixture.getGrandchildSpecimenTypes());

        session.beginTransaction();
        Set<Container> childL2Containers = createContainers(specimenTypes);

        Patient patient = fixture.getPatients().iterator().next();
        factory.setDefaultPatient(patient);
        factory.createSourceSpecimen();
        Specimen specimenWithPosition = factory.createParentSpecimen();

        Container container = childL2Containers.iterator().next();

        SpecimenPosition pos = new SpecimenPosition();
        pos.setSpecimen(specimenWithPosition);
        pos.setRow(0);
        pos.setCol(0);
        pos.setContainer(container);

        ContainerType type = container.getContainerType();
        RowColPos rcp = new RowColPos(0, 0);
        String positionString =
            ContainerLabelingScheme.getPositionString(rcp,
                                                      type.getChildLabelingScheme().getId(),
                                                      type.getCapacity().getRowCapacity(),
                                                      type.getCapacity().getColCapacity(),
                                                      type.getLabelingLayout());
        pos.setPositionString(positionString);
        session.save(pos);
        session.flush();
        session.getTransaction().commit();

        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        List<GrandchildSpecimenBatchOpInputPojo> pojosAsList =
            new ArrayList<GrandchildSpecimenBatchOpInputPojo>(pojos);

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(pojosAsList,
                                                                 childL2Containers,
                                                                 false);

        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_CONTAINER_POS_OCCUPIED_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void positionsByBarcodeAlreadyOccupied() throws Exception {
        Set<SpecimenType> specimenTypes = fixture.getParentSpecimenTypes();
        specimenTypes.addAll(fixture.getGrandchildSpecimenTypes());

        session.beginTransaction();
        Set<Container> childL2Containers = createContainers(specimenTypes);

        Patient patient = fixture.getPatients().iterator().next();
        factory.setDefaultPatient(patient);
        factory.createSourceSpecimen();
        Specimen specimenWithPosition = factory.createParentSpecimen();

        Container container = childL2Containers.iterator().next();

        SpecimenPosition pos = new SpecimenPosition();
        pos.setSpecimen(specimenWithPosition);
        pos.setRow(0);
        pos.setCol(0);
        pos.setContainer(container);

        ContainerType type = container.getContainerType();
        RowColPos rcp = new RowColPos(0, 0);
        String positionString =
            ContainerLabelingScheme.getPositionString(rcp,
                                                      type.getChildLabelingScheme().getId(),
                                                      type.getCapacity().getRowCapacity(),
                                                      type.getCapacity().getColCapacity(),
                                                      type.getLabelingLayout());
        pos.setPositionString(positionString);
        session.save(pos);
        session.flush();
        session.getTransaction().commit();

        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        List<GrandchildSpecimenBatchOpInputPojo> pojosAsList =
            new ArrayList<GrandchildSpecimenBatchOpInputPojo>(pojos);

        specimenCsvHelper.fillContainersWithSpecimenBatchOpPojos(pojosAsList,
                                                                 childL2Containers,
                                                                 true);

        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_LABEL_POS_OCCUPIED_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void specimenBatchOpGetAction() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(), fixture.getPatients());

        // write out to CSV file so we can view data
        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        GrandchildSpecimenBatchOpAction importAction =
            new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                pojos,
                                                new File(CSV_NAME));
        Integer bachOpId = exec(importAction).getId();
        checkCsvInfoAgainstDb(pojos);
        BatchOpGetResult<Specimen> batchOpResult = exec(new SpecimenBatchOpGetAction(bachOpId));
        Assert.assertEquals(pojos.size(), batchOpResult.getModelObjects().size());
    }

    @Test
    public void originCenterIsInvalid() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        // change the origin center to something invalid
        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setOriginCenter(nameGenerator.next(Center.class));
        }

        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_ORIGIN_CENTER_SHORT_NAME_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void originCenterIsValid() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        // assign the origin center
        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setOriginCenter(defaultClinic.getNameShort());
        }

        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(pojos);
    }

    @Test
    public void currentCenterIsInvalid() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        // change the current center to something invalid
        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setCurrentCenter(nameGenerator.next(Center.class));
        }

        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
            Assert.fail("should fail");
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            new AssertBatchOpException()
                .withMessage(CSV_CURRENT_CENTER_SHORT_NAME_ERROR.format())
                .assertIn(e);
        }
    }

    @Test
    public void currentCenterIsValid() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimens(factory.getDefaultStudy(),
                                              fixture.getPatients());

        // assign the origin center
        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            pojo.setCurrentCenter(defaultSite.getNameShort());
        }

        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        try {
            GrandchildSpecimenBatchOpAction importAction =
                new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                    pojos,
                                                    new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkCsvInfoAgainstDb(pojos);
    }

    // need to add allowed specimen types to the leaf containers' container types
    private Set<Container> createContainers(Set<SpecimenType> specimenTypes) {
        factory.createTopContainer();
        factory.createParentContainer();

        ContainerType ctype = factory.createContainerType();
        ctype.getChildContainerTypes().clear();
        ctype.getSpecimenTypes().clear();
        ctype.getSpecimenTypes().addAll(specimenTypes);
        session.save(ctype);

        Set<Container> result = new HashSet<Container>();
        for (int i = 0; i < 3; ++i) {
            Container container = factory.createContainer();
            container.setProductBarcode(nameGenerator.next(Container.class));
            result.add(container);
        }

        return result;
    }

    @SuppressWarnings("unused")
    private void logSpecimenTypes() {
        @SuppressWarnings("unchecked")
        List<SpecimenType> specimenTypes = session.createCriteria(SpecimenType.class).list();
        Map<String, SpecimenType> byNames = new HashMap<String, SpecimenType>();
        for (SpecimenType specimenType : specimenTypes) {
            byNames.put(specimenType.getName(), specimenType);
        }

        log.info("Specimen Types");
        for (SpecimenType specimenType : specimenTypes) {
            if (specimenType.getParentSpecimenTypes().isEmpty()) {
                logSpecimenType("   ", specimenType, byNames);
            }
        }
    }

    private void logSpecimenType(String indent,
                                 SpecimenType specimenType,
                                 Map<String, SpecimenType> byNames) {
        log.info(indent + specimenType.getName());
        for (SpecimenType childType : specimenType.getChildSpecimenTypes()) {
            logSpecimenType(indent + "   ", childType, byNames);
        }
    }

    private void checkCsvInfoAgainstDb(Set<GrandchildSpecimenBatchOpInputPojo> pojos) {
        for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
            log.trace("checking specimen against db: inventory id {}", pojo.getInventoryId());

            Specimen specimen = (Specimen) session.createCriteria(Specimen.class)
                .add(Restrictions.eq("inventoryId", pojo.getInventoryId()))
                .uniqueResult();

            Assert.assertNotNull(specimen);
            Assert.assertEquals(pojo.getSpecimenType(), specimen.getSpecimenType().getName());
            Assert.assertEquals(0, DateCompare.compare(pojo.getCreatedAt(),
                                                       specimen.getCreatedAt()));

            if (pojo.getPatientNumber() != null) {
                Assert.assertEquals(pojo.getPatientNumber(),
                                    specimen.getCollectionEvent().getPatient().getPnumber());
            }

            Assert.assertNotNull(specimen.getCollectionEvent());

            if (pojo.getOriginCenter() != null) {
                Assert.assertEquals(pojo.getOriginCenter(),
                                    specimen.getOriginInfo().getCenter().getNameShort());
            }

            if (pojo.getCurrentCenter() != null) {
                Assert.assertEquals(pojo.getCurrentCenter(),
                                    specimen.getCurrentCenter().getNameShort());
            }

            Assert.assertNull(specimen.getOriginalCollectionEvent());

            if (pojo.getParentInventoryId() != null) {
                Assert.assertEquals(pojo.getParentInventoryId(),
                                    specimen.getParentSpecimen().getInventoryId());
            }

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

}

final class TestFixture {

    private final Session session;

    // private final Factory factory;

    private final Set<Patient> patients;

    private final HashSet<SpecimenType> parentSpecimenTypes;

    private final HashSet<SpecimenType> childSpecimenTypes;

    private final HashSet<SpecimenType> grandchildSpecimenTypes;

    public TestFixture(Session session,
                       Factory factory,
                       int numPatients,
                       int numGrandchildSpecimenTypes) {
        this.session = session;
        // this.factory = factory;

        patients = new HashSet<Patient>();
        parentSpecimenTypes = new HashSet<SpecimenType>();
        childSpecimenTypes = new HashSet<SpecimenType>();
        grandchildSpecimenTypes = new HashSet<SpecimenType>();

        List<SourceSpecimen> sourceSpecimens = new ArrayList<SourceSpecimen>();
        List<AliquotedSpecimen> aliquotedSpecimens = new ArrayList<AliquotedSpecimen>();

        for (int i = 0; i < 3; ++i) {
            SpecimenType parentSpecimenType = factory.createSpecimenType();
            parentSpecimenTypes.add(parentSpecimenType);

            factory.setDefaultSourceSpecimenType(parentSpecimenType);
            sourceSpecimens.add(factory.createSourceSpecimen());
        }

        for (SourceSpecimen sourceSpecimen : sourceSpecimens) {
            SpecimenType parentSpecimenType = sourceSpecimen.getSpecimenType();
            factory.setDefaultSourceSpecimen(sourceSpecimen);

            SpecimenType childSpecimenType = factory.createSpecimenType();
            childSpecimenTypes.add(parentSpecimenType);

            factory.setDefaultAliquotedSpecimenType(childSpecimenType);
            addChildSpecimenType(parentSpecimenType, childSpecimenType);
            aliquotedSpecimens.add(factory.createAliquotedSpecimen());
        }

        // create a grandchild specimen type
        for (AliquotedSpecimen aliquotedSpecimen : aliquotedSpecimens) {
            factory.setDefaultAliquotedSpecimen(aliquotedSpecimen);
            SpecimenType childSpecimenType = aliquotedSpecimen.getSpecimenType();

            // create a grandchild specimen type for the aliquoted specimens
            for (int i = 0; i < numGrandchildSpecimenTypes; ++i) {
                SpecimenType grandchildSpecimenType = factory.createSpecimenType();
                grandchildSpecimenTypes.add(grandchildSpecimenType);

                addChildSpecimenType(childSpecimenType, grandchildSpecimenType);
                factory.setDefaultAliquotedSpecimenType(grandchildSpecimenType);
                factory.createAliquotedSpecimen();
            }
        }

        // create specimens for each patient now
        for (int p = 0; p < numPatients; p++) {
            Patient patient = factory.createPatient();
            patients.add(patient);
            factory.createCollectionEvent();

            for (SourceSpecimen sourceSpecimen : sourceSpecimens) {
                SpecimenType parentSpecimenType = sourceSpecimen.getSpecimenType();
                factory.setDefaultSourceSpecimenType(parentSpecimenType);
                factory.createParentSpecimen();

                for (AliquotedSpecimen aliquotedSpecimen : aliquotedSpecimens) {
                    SpecimenType childSpecimenType = aliquotedSpecimen.getSpecimenType();
                    factory.setDefaultAliquotedSpecimenType(childSpecimenType);
                    addChildSpecimenType(parentSpecimenType, childSpecimenType);
                    factory.createChildSpecimen();
                }
            }
        }
    }

    public Set<Patient> getPatients() {
        return patients;
    }

    public Set<SpecimenType> getParentSpecimenTypes() {
        return parentSpecimenTypes;
    }

    public Set<SpecimenType> getChildSpecimenTypes() {
        return childSpecimenTypes;
    }

    public Set<SpecimenType> getGrandchildSpecimenTypes() {
        return grandchildSpecimenTypes;
    }

    public boolean validate() {
        return !parentSpecimenTypes.isEmpty()
            && !childSpecimenTypes.isEmpty()
            && !grandchildSpecimenTypes.isEmpty();
    }

    private void addChildSpecimenType(SpecimenType parent, SpecimenType child) {
        Set<SpecimenType> parentSpecimenTypes = new HashSet<SpecimenType>(Arrays.asList(parent));
        Set<SpecimenType> childSpecimenTypes = new HashSet<SpecimenType>(Arrays.asList(child));
        parent.getChildSpecimenTypes().addAll(childSpecimenTypes);
        child.getParentSpecimenTypes().addAll(parentSpecimenTypes);
        session.save(parent);
        session.save(child);
        session.flush();
    }
}
