package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpActionErrors.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpGetResult;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.GrandchildSpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.GrandchildSpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpGetAction;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.util.DateCompare;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.test.Factory;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.action.batchoperation.AssertBatchOpException;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;

/**
 *
 * @author Nelson Loyola
 *
 */
public class TestGrandchildSpecimenBatchOp
    extends CommonSpecimenBatachOpTests<GrandchildSpecimenBatchOpInputPojo> {

    private static Logger log = LoggerFactory.getLogger(TestGrandchildSpecimenBatchOp.class);

    private static final String CSV_NAME = "import_grandchild_specimens.csv";

    private GrandchildSpecimenBatchOpPojoHelper specimenCsvHelper;

    private final Set<OriginInfo> originInfos = new HashSet<OriginInfo>();

    private NameGenerator nameGenerator;

    private TestFixture fixture;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        nameGenerator = new NameGenerator("test_" + getMethodNameR());
        specimenCsvHelper = new GrandchildSpecimenBatchOpPojoHelper(nameGenerator);

        // delete the CSV file if it exists
        File file = new File(CSV_NAME);
        file.delete();

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
            specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(), fixture.getPatients());

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

        checkAgainstDb(pojos);
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
            specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
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
    public void specimenAlreadyExists() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                              fixture.getPatients());

        // add first pojo to database
        GrandchildSpecimenBatchOpInputPojo pojo = pojos.iterator().next();
        addGrandchildSpecimen(pojo.getInventoryId(), pojo.getParentInventoryId());

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
                .withMessage(SPC_ALREADY_EXISTS_ERROR)
                .assertIn(e);
        }
    }

    @Test
    public void parentSpecimenDoesInvalid() throws Exception {
        LString expectedError;

        for (String testCase : Arrays.asList("empty", "invalid")) {
            Set<GrandchildSpecimenBatchOpInputPojo> pojos =
                specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                      fixture.getPatients());

            // set parent specimen to invalid inventory ID
            if (testCase.equals("empty")) {
                expectedError = CSV_PARENT_SPECIMEN_INVENTORY_ID_REQUIRED_ERROR;
                for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
                    pojo.setParentInventoryId(StringUtil.EMPTY_STRING);
                }
            } else {
                expectedError = CSV_PARENT_SPC_INV_ID_ERROR.format();
                for (GrandchildSpecimenBatchOpInputPojo pojo : pojos) {
                    pojo.setParentInventoryId(nameGenerator.next(String.class));
                }
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
                CsvUtil.showErrorsInLog(log, e);
                new AssertBatchOpException().withMessage(expectedError).assertIn(e);
            }
        }
    }

    @Test
    public void patientNumberInvalidOrForOtherSpecimen() throws Exception {
        session.beginTransaction();
        Patient patient = factory.createPatient();
        session.getTransaction().commit();

        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                              fixture.getPatients());

        // set all patient numbers to other patient
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
            specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
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
            Assert.fail("should not be allowed to import spcecimensfixture when patient number is missing");
        } catch (BatchOpErrorsException e) {
            new AssertBatchOpException()
                .withMessage(CSV_PATIENT_NUMBER_REQUIRED_ERROR)
                .assertIn(e);
        }
    }

    @Test
    public void invalidSpecimenType() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
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
            specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
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
            specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                              fixture.getPatients());

        SpecimenBatchOpPojoHelper.addComments(pojos, nameGenerator);
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

        checkAgainstDb(pojos);
    }

    @Test
    public void noErrorsWithContainers() throws Exception {
        Set<Container> childL2Containers =
            SpecimenBatchOpPojoHelper.createContainers(session,
                                                       factory,
                                                       nameGenerator,
                                                       fixture.getGrandchildSpecimenTypes(),
                                                       5);
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                  fixture.getPatients());
        SpecimenBatchOpPojoHelper.assignPositionsToPojos(pojos, childL2Containers, false);

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

        checkAgainstDb(pojos);
    }

    @Test
    public void withProductBarcodesAndPositions() throws Exception {
        withProductBarcodesAndPositions(new IPojosCreator<GrandchildSpecimenBatchOpInputPojo>() {
            @Override
            public Set<GrandchildSpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        });
    }

    @Test
    public void withInvalidContainerInformation() throws Exception {
        IPojosCreator<GrandchildSpecimenBatchOpInputPojo> pojosCreator =
            new IPojosCreator<GrandchildSpecimenBatchOpInputPojo>() {
                @Override
                public Set<GrandchildSpecimenBatchOpInputPojo> create() {
                    return specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                                 fixture.getPatients());
                }
            };
        withInvalidContainerInformation(pojosCreator, false);
    }

    @Test
    public void withInvalidSpecimenPositions() throws Exception {
        Set<SpecimenType> specimenTypes = fixture.getParentSpecimenTypes();
        specimenTypes.addAll(fixture.getGrandchildSpecimenTypes());

        IPojosCreator<GrandchildSpecimenBatchOpInputPojo> pojosCreator =
            new IPojosCreator<GrandchildSpecimenBatchOpInputPojo>() {
            @Override
            public Set<GrandchildSpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        };
        withInvalidSpecimenPositions(pojosCreator, specimenTypes);
    }

    @Test
    public void withInvalidSpecimenTypes() throws Exception {
        withInvalidSpecimenTypes(new IPojosCreator<GrandchildSpecimenBatchOpInputPojo>() {
            @Override
            public Set<GrandchildSpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        });
    }

    @Test
    public void withInvalidContainerType() throws Exception {
        Set<SpecimenType> specimenTypes = fixture.getParentSpecimenTypes();
        specimenTypes.addAll(fixture.getGrandchildSpecimenTypes());

        IPojosCreator<GrandchildSpecimenBatchOpInputPojo> pojosCreator =
            new IPojosCreator<GrandchildSpecimenBatchOpInputPojo>() {
            @Override
            public Set<GrandchildSpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        };
        withInvalidContainerType(pojosCreator, specimenTypes);
    }

    @Test
    public void positionsAlreadyOccupied() throws Exception {
        Set<SpecimenType> specimenTypes = fixture.getParentSpecimenTypes();
        specimenTypes.addAll(fixture.getGrandchildSpecimenTypes());

        IPojosCreator<GrandchildSpecimenBatchOpInputPojo> pojosCreator =
            new IPojosCreator<GrandchildSpecimenBatchOpInputPojo>() {
            @Override
            public Set<GrandchildSpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        };
        withPositionsAlreadyOccupied(pojosCreator,
                                     specimenTypes,
                                     fixture.getPatients().iterator().next());
    }

    @Test
    public void originCenterIsValid() throws Exception {
        originCenterIsValid(new IPojosCreator<GrandchildSpecimenBatchOpInputPojo>() {
            @Override
            public Set<GrandchildSpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        });
    }

    @Test
    public void originCenterIsInvalid() throws Exception {
        originCenterIsInvalid(new IPojosCreator<GrandchildSpecimenBatchOpInputPojo>() {
            @Override
            public Set<GrandchildSpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        });
    }

    @Test
    public void currentCenterIsValid() throws Exception {
        currentCenterIsValid(new IPojosCreator<GrandchildSpecimenBatchOpInputPojo>() {
            @Override
            public Set<GrandchildSpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        });
    }

    @Test
    public void currentCenterIsInvalid() throws Exception {
        currentCenterIsInvalid(new IPojosCreator<GrandchildSpecimenBatchOpInputPojo>() {
            @Override
            public Set<GrandchildSpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        });
    }

    @Test
    public void specimenBatchOpGetAction() throws Exception {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos =
            specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(), fixture.getPatients());

        // write out to CSV file so we can view data
        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);

        GrandchildSpecimenBatchOpAction importAction =
            new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(),
                                                pojos,
                                                new File(CSV_NAME));
        Integer bachOpId = exec(importAction).getId();
        checkAgainstDb(pojos);
        BatchOpGetResult<Specimen> batchOpResult = exec(new SpecimenBatchOpGetAction(bachOpId));
        Assert.assertEquals(pojos.size(), batchOpResult.getModelObjects().size());
        Assert.assertEquals(getGlobalAdmin().getLogin(), batchOpResult.getExecutedBy());

        Date timeNow = new Date();
        Assert.assertTrue("Dates aren't close enough to each other!",
                          (timeNow.getTime() - batchOpResult.getTimeExecuted().getTime()) < 10000);
        Assert.assertEquals(CSV_NAME, batchOpResult.getInput().getName());
    }

    @Test(expected = IllegalStateException.class)
    public void errorsIfInventoryIdFoundMoreThanOnce() throws Exception {
        errorsIfInventoryIdFoundMoreThanOnce(new IPojosCreator<GrandchildSpecimenBatchOpInputPojo>() {
            @Override
            public Set<GrandchildSpecimenBatchOpInputPojo> create() {
                return specimenCsvHelper.createSpecimenPojos(factory.getDefaultStudy(),
                                                             fixture.getPatients());
            }
        });
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

    private void addGrandchildSpecimen(String inventoryId, String parentInventoryId) {
        session.beginTransaction();
        Specimen parentSpecimen = (Specimen) session.createCriteria(Specimen.class)
            .add(Restrictions.eq("inventoryId", parentInventoryId))
            .uniqueResult();
        factory.setDefaultParentSpecimen(parentSpecimen);

        SpecimenType specimenType = fixture.getChildSpecimenTypes().iterator().next();
        factory.setDefaultAliquotedSpecimenType(specimenType);
        Specimen specimen = factory.createChildSpecimen();
        specimen.setInventoryId(inventoryId);
        session.getTransaction().commit();
    }

    @Override
    protected File writePojosToCsv(Set<GrandchildSpecimenBatchOpInputPojo> pojos)
        throws IOException {
        GrandchildSpecimenBatchOpCsvWriter.write(CSV_NAME, pojos);
        return new File(CSV_NAME);
    }

    @Override
    protected Action<IdResult> createAction(Set<GrandchildSpecimenBatchOpInputPojo> pojos, File file)
        throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        return new GrandchildSpecimenBatchOpAction(factory.getDefaultSite(), pojos, file);
    }

    @Override
    protected void checkAgainstDb(GrandchildSpecimenBatchOpInputPojo pojo, Specimen specimen) {
        super.checkAgainstDb(pojo, specimen);
        Assert.assertEquals(pojo.getSpecimenType(), specimen.getSpecimenType().getName());
        Assert.assertEquals(0, DateCompare.compare(pojo.getCreatedAt(), specimen.getCreatedAt()));
        Assert.assertNotNull(specimen.getCollectionEvent());
        Assert.assertNull(specimen.getOriginalCollectionEvent());

        if (pojo.getOriginCenter() != null) {
            Assert.assertEquals(pojo.getOriginCenter(),
                                specimen.getOriginInfo().getCenter().getNameShort());
        }

        if (pojo.getCurrentCenter() != null) {
            Assert.assertEquals(pojo.getCurrentCenter(),
                                specimen.getCurrentCenter().getNameShort());
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
            childSpecimenTypes.add(childSpecimenType);

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
