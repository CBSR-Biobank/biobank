package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ContactHelper;
import test.ualberta.med.biobank.internal.ContainerHelper;
import test.ualberta.med.biobank.internal.ContainerTypeHelper;
import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.PatientVisitHelper;
import test.ualberta.med.biobank.internal.SampleHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Container;

public class TestContainer extends TestDatabase {
    private static final int CONTAINER_TOP_ROWS = 5;

    private static final int CONTAINER_TOP_COLS = 9;

    private static final int CONTAINER_CHILD_L3_ROWS = 8;

    private static final int CONTAINER_CHILD_L3_COLS = 12;

    private static List<String> usedBarcodes;

    private Map<String, ContainerWrapper> containerMap;

    private SiteWrapper site;

    private Map<String, ContainerTypeWrapper> containerTypeMap;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        usedBarcodes = new ArrayList<String>();
        containerMap = new HashMap<String, ContainerWrapper>();
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();
        site = SiteHelper.addSite("Site - Container Test"
            + Utils.getRandomString(10));
        addContainerTypes(site);
        addContainers();
    }

    private static String getNewBarcode() {
        String newBarcode;
        do {
            newBarcode = Utils.getRandomString(10);
        } while (usedBarcodes.contains(newBarcode));
        return newBarcode;
    }

    private void addContainerTypes(SiteWrapper site)
        throws BiobankCheckException, Exception {
        ContainerTypeWrapper topType, childType;

        childType = ContainerTypeHelper.addContainerType(site,
            "Child L4 Container Type", "CCTL4", 3, 10, 10, false);
        containerTypeMap.put("ChildCtL4", childType);

        childType = ContainerTypeHelper.addContainerType(site,
            "Child L3 Container Type", "CCTL3", 4, CONTAINER_CHILD_L3_ROWS,
            CONTAINER_CHILD_L3_COLS, false);
        childType.setChildContainerTypeCollection(Arrays
            .asList(containerTypeMap.get("ChildCtL4")));
        childType.persist();
        containerTypeMap.put("ChildCtL3", childType);

        childType = ContainerTypeHelper.newContainerType(site,
            "Child L2 Container Type", "CCTL2", 1, 1, 10, false);
        childType.setChildContainerTypeCollection(Arrays
            .asList(containerTypeMap.get("ChildCtL3")));
        childType.persist();
        containerTypeMap.put("ChildCtL2", childType);

        childType = ContainerTypeHelper.newContainerType(site,
            "Child L1 Container Type", "CCTL1", 3, 1, 10, false);
        childType.setChildContainerTypeCollection(Arrays
            .asList(containerTypeMap.get("ChildCtL2")));
        childType.persist();
        containerTypeMap.put("ChildCtL1", childType);

        topType = ContainerTypeHelper.newContainerType(site,
            "Top Container Type", "TCT", 2, CONTAINER_TOP_ROWS,
            CONTAINER_TOP_COLS, true);
        topType.setChildContainerTypeCollection(Arrays.asList(containerTypeMap
            .get("ChildCtL1")));
        topType.persist();
        containerTypeMap.put("TopCT", topType);
    }

    private void addContainers() throws BiobankCheckException, Exception {
        ContainerWrapper top = ContainerHelper.addContainer("01",
            getNewBarcode(), null, site, containerTypeMap.get("TopCT"));
        containerMap.put("Top", top);
    }

    public List<SampleTypeWrapper> getRandomSampleTypeList(
        List<SampleTypeWrapper> list) {
        List<SampleTypeWrapper> result = new ArrayList<SampleTypeWrapper>();
        for (SampleTypeWrapper st : list) {
            if (r.nextBoolean()) {
                result.add(st);
            }
        }
        return result;
    }

    public ContainerTypeWrapper addSampleTypes(ContainerTypeWrapper ct,
        List<SampleTypeWrapper> sampleTypes) throws Exception {
        Assert.assertTrue("not enough sample types for test", (sampleTypes
            .size() > 10));
        ct.setSampleTypeCollection(sampleTypes);
        ct.persist();
        ct.reload();
        return ct;
    }

    private void addContainerHierarchy(ContainerWrapper parent, String mapPrefix)
        throws Exception {
        ContainerWrapper childL1, childL2, childL3, childL4;
        Collection<ContainerWrapper> children;

        childL1 = ContainerHelper.addContainer(null, getNewBarcode(), parent,
            site, containerTypeMap.get("ChildCtL1"), 0, 0);
        parent.reload();
        children = parent.getChildren().values();
        Assert.assertTrue((children.size() == 1) && children.contains(childL1));

        childL2 = ContainerHelper.addContainer(null, getNewBarcode(), childL1,
            site, containerTypeMap.get("ChildCtL2"), 0, 0);
        childL1.reload();
        children = childL1.getChildren().values();
        Assert.assertTrue((children.size() == 1) && children.contains(childL2));

        childL3 = ContainerHelper.addContainer(null, getNewBarcode(), childL2,
            site, containerTypeMap.get("ChildCtL3"), 0, 0);
        childL2.reload();
        children = childL2.getChildren().values();
        Assert.assertTrue((children.size() == 1) && children.contains(childL3));

        childL4 = ContainerHelper.addContainer(null, getNewBarcode(), childL3,
            site, containerTypeMap.get("ChildCtL4"), 0, 0);
        childL3.reload();
        children = childL3.getChildren().values();
        Assert.assertTrue((children.size() == 1) && children.contains(childL4));

        containerMap.put(mapPrefix + "ChildL1", childL1);
        containerMap.put(mapPrefix + "ChildL2", childL2);
        containerMap.put(mapPrefix + "ChildL3", childL3);
        containerMap.put(mapPrefix + "ChildL4", childL4);
    }

    public void addDupLabelledHierarchy() throws Exception {
        ContainerTypeWrapper freezerType, hotelType, cabinetType, drawerType;

        freezerType = ContainerTypeHelper.addContainerType(site, "freezer3x10",
            "F3x10", 2, 3, 10, true);
        containerTypeMap.put("frezer3x10", freezerType);
        hotelType = ContainerTypeHelper.addContainerType(site, "Hotel13",
            "H-13", 2, 1, 13, false);
        List<ContainerTypeWrapper> childContainerTypes = new ArrayList<ContainerTypeWrapper>();
        childContainerTypes.add(hotelType);
        freezerType.setChildContainerTypeCollection(childContainerTypes);
        freezerType.persist();
        containerTypeMap.put("hotel13", hotelType);

        cabinetType = ContainerTypeHelper.addContainerType(site, "Cabinet",
            "C", 2, 1, 4, true);
        containerTypeMap.put("cabinet", cabinetType);
        drawerType = ContainerTypeHelper.addContainerType(site, "Drawer36",
            "D36", 2, 1, 36, false);
        childContainerTypes = new ArrayList<ContainerTypeWrapper>();
        childContainerTypes.add(drawerType);
        cabinetType.setChildContainerTypeCollection(childContainerTypes);
        cabinetType.persist();
        containerTypeMap.put("drawer36", drawerType);

        ContainerWrapper freezer, cabinet, hotel, drawer;

        freezer = ContainerHelper.addContainer("02", getNewBarcode(), null,
            site, freezerType);
        hotel = ContainerHelper.addContainer(null, getNewBarcode(), freezer,
            site, hotelType, 0, 0);
        freezer.reload();
        containerMap.put("freezer02", freezer);
        containerMap.put("H02AA", hotel);

        cabinet = ContainerHelper.addContainer("02", getNewBarcode(), null,
            site, cabinetType);
        drawer = ContainerHelper.addContainer(null, getNewBarcode(), cabinet,
            site, drawerType, 0, 0);
        cabinet.reload();
        containerMap.put("cabinet", cabinet);
        containerMap.put("D02AA", drawer);
    }

    private void addContainerHierarchy(ContainerWrapper parent)
        throws Exception {
        addContainerHierarchy(parent, "");
    }

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        ContainerWrapper container = ContainerHelper.addContainer(null, null,
            null, site, containerTypeMap.get("TopCT"));
        testGettersAndSetters(container);
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        ContainerWrapper container = ContainerHelper.addContainer(null, null,
            null, site, containerTypeMap.get("TopCT"));
        Assert.assertEquals(Container.class, container.getWrappedClass());
    }

    @Test
    public void createValidContainer() throws Exception {
        ContainerWrapper container = ContainerHelper.addContainer("05", null,
            null, site, containerTypeMap.get("TopCT"));

        Integer id = container.getId();
        Assert.assertNotNull(id);
        Container containerInDB = ModelUtils.getObjectWithId(appService,
            Container.class, id);
        Assert.assertNotNull(containerInDB);
    }

    @Test(expected = BiobankCheckException.class)
    public void createNoSite() throws Exception {
        ContainerHelper.addContainer("05", null, null, null, containerTypeMap
            .get("TopCT"));
    }

    @Test
    public void testLabelUnique() throws Exception {
        ContainerWrapper container2;
        ContainerHelper.addContainer("05", null, null, site, containerTypeMap
            .get("TopCT"));
        container2 = ContainerHelper.newContainer("05", null, null, site,
            containerTypeMap.get("TopCT"));

        try {
            container2.persist();
            Assert
                .fail("should not be allowed to add container because of duplicate label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testProductBarcodeUnique() throws Exception {
        ContainerWrapper container2;

        String barcode = getNewBarcode();

        ContainerHelper.addContainer("05", barcode, null, site,
            containerTypeMap.get("TopCT"));
        container2 = ContainerHelper.newContainer("06", barcode, null, site,
            containerTypeMap.get("TopCT"));

        try {
            container2.persist();
            Assert
                .fail("should not be allowed to add container because of duplicate product barcode");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testReset() throws Exception {
        ContainerWrapper container = ContainerHelper.addContainer("05",
            getNewBarcode(), null, site, containerTypeMap.get("TopCT"));
        container.reset();
    }

    @Test
    public void testReload() throws Exception {
        ContainerWrapper container = ContainerHelper.newContainer("05",
            "uvwxyz", null, site, containerTypeMap.get("TopCT"));
        container.reload();
    }

    @Test(expected = BiobankCheckException.class)
    public void testSetPositionOnTopLevel() throws Exception {
        ContainerHelper.addContainer("05", "uvwxyz", null, site,
            containerTypeMap.get("TopCT"), 0, 0);
    }

    @Test
    public void testSetPositionOnChild() throws Exception {
        ContainerHelper.addContainer(null, "uvwxyz", containerMap.get("Top"),
            site, containerTypeMap.get("ChildCtL1"), 0, 0);
    }

    @Test
    public void testSetInvalidPositionOnChild() throws Exception {
        ContainerWrapper top, child;

        top = containerMap.get("Top");

        child = ContainerHelper.newContainer(null, "uvwxyz", top, site,
            containerTypeMap.get("ChildCtL1"), top.getRowCapacity(), top
                .getColCapacity());

        try {
            child.persist();
            Assert.fail("should not be allowed to set an invalid position");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        child.setPosition(top.getRowCapacity() + 1, top.getColCapacity() + 1);
        try {
            child.persist();
            Assert.fail("should not be allowed to set an invalid position");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        child.setPosition(-1, -1);
        try {
            child.persist();
            Assert.fail("should not be allowed to set an invalid position");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testUniquePosition() throws Exception {
        ContainerWrapper top;

        top = containerMap.get("Top");
        ContainerHelper.addContainer(null, "uvwxyz", top, site,
            containerTypeMap.get("ChildCtL1"), 0, 0);

        try {
            ContainerHelper.addContainer(null, "uvwxyz", top, site,
                containerTypeMap.get("ChildCtL1"), 0, 0);
            Assert
                .fail("should not be allowed to add container because of duplicate product barcode");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetContainer() throws Exception {
        ContainerWrapper top, result;

        top = containerMap.get("Top");
        addContainerHierarchy(top);

        // success cases
        result = top.getContainer("01AA", containerTypeMap.get("ChildCtL1"));
        Assert.assertEquals(containerMap.get("ChildL1"), result);

        result = top.getContainer("01AA01", containerTypeMap.get("ChildCtL2"));
        Assert.assertEquals(containerMap.get("ChildL2"), result);

        result = top
            .getContainer("01AA01A1", containerTypeMap.get("ChildCtL3"));
        Assert.assertEquals(containerMap.get("ChildL3"), result);

        // fail cases
        result = top.getContainer("01AB", containerTypeMap.get("ChildCtL1"));
        Assert.assertNull(result);

        result = top.getContainer("01AA02", containerTypeMap.get("ChildCtL1"));
        Assert.assertNull(result);

        result = top
            .getContainer("01AA01A2", containerTypeMap.get("ChildCtL3"));
        Assert.assertNull(result);
    }

    @Test
    public void testGetContainersHoldingContainerType() throws Exception {
        ContainerWrapper freezer, hotel, cabinet, drawer;

        addDupLabelledHierarchy();

        freezer = containerMap.get("freezer02");
        hotel = containerMap.get("H02AA");
        cabinet = containerMap.get("cabinet");
        drawer = containerMap.get("D02AA");

        List<ContainerWrapper> list = ContainerWrapper
            .getContainersHoldingContainerType(appService, "02", site, hotel
                .getContainerType());
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(freezer));

        list = ContainerWrapper.getContainersHoldingContainerType(appService,
            "02", site, drawer.getContainerType());
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(cabinet));
    }

    @Test
    public void testGetContainersInSite() throws Exception {
        ContainerWrapper freezer, hotel, cabinet, drawer;

        addDupLabelledHierarchy();

        freezer = containerMap.get("freezer02");
        hotel = containerMap.get("H02AA");
        cabinet = containerMap.get("cabinet");
        drawer = containerMap.get("D02AA");

        List<ContainerWrapper> list = ContainerWrapper.getContainersInSite(
            appService, site, "02AA");
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(hotel));
        Assert.assertTrue(list.contains(drawer));

        list = ContainerWrapper.getContainersInSite(appService, site, "02");
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(freezer));
        Assert.assertTrue(list.contains(cabinet));
    }

    @Test
    public void testGetPossibleParents() throws Exception {
        ContainerWrapper top1, top2, childL1, childL2;

        ContainerTypeWrapper topType2 = ContainerTypeHelper.addContainerType(
            site, "Top Container Type 2", "TCT2", 2, 3, 10, true);

        top2 = ContainerHelper.addContainer("02", getNewBarcode(), null, site,
            topType2);

        top1 = ContainerHelper.addContainer("02", getNewBarcode(), null, site,
            containerTypeMap.get("TopCT"));
        childL1 = ContainerHelper.addContainer(null, "0001", top1, site,
            containerTypeMap.get("ChildCtL1"), 0, 0);
        top1.reload();

        List<ContainerWrapper> parents = childL1.getPossibleParents("02");
        Assert.assertTrue(parents.contains(top1));
        Assert.assertFalse(parents.contains(top2));

        addContainerHierarchy(containerMap.get("Top"));
        childL1 = containerMap.get("ChildL1");
        childL2 = containerMap.get("ChildL2");
        parents = childL2.getPossibleParents("01AA");
        Assert.assertTrue((parents.size() == 1) && parents.contains(childL1));
        parents = childL2.getPossibleParents("01");
        Assert.assertEquals(0, parents.size());

        parents = top1.getPossibleParents("");
        Assert.assertEquals(0, parents.size());
    }

    @Test
    public void testGetChildWithLabel() throws Exception {
        ContainerWrapper top, child;

        top = containerMap.get("Top");
        ContainerTypeWrapper type = top.getContainerType();
        for (int row = 0; row < type.getRowCapacity(); ++row) {
            for (int col = 0; col < type.getColCapacity(); ++col) {
                child = ContainerHelper.addContainer(null, "0001_" + row + "_"
                    + col, top, site, containerTypeMap.get("ChildCtL1"), row,
                    col);
                top.reload();

                String label = "01"
                    + LabelingScheme.getPositionString(new RowColPos(row, col),
                        type);

                ContainerWrapper result = top.getChildWithLabel(label);
                Assert.assertEquals(child, result);
            }
        }
    }

    private void testGetPositionFromLabelingScheme(ContainerWrapper container)
        throws Exception {
        ContainerTypeWrapper type = container.getContainerType();

        for (int row = 0, m = type.getRowCapacity(); row < m; ++row) {
            for (int col = 0, n = type.getColCapacity(); col < n; ++col) {
                int index = n * row + col;
                int len = LabelingScheme.CBSR_LABELLING_PATTERN.length();
                String label = null;

                switch (type.getChildLabelingScheme()) {
                case 1:
                    label = String.format(container.getParent().getLabel()
                        + "%c%c", LabelingScheme.SBS_ROW_LABELLING_PATTERN
                        .charAt(row), LabelingScheme.CBSR_LABELLING_PATTERN
                        .charAt(col));
                    break;
                case 2:
                    label = String.format(container.getParent().getLabel()
                        + "%c%c", LabelingScheme.CBSR_LABELLING_PATTERN
                        .charAt(index / len),
                        LabelingScheme.CBSR_LABELLING_PATTERN.charAt(index
                            % len));
                    break;
                case 3:
                    label = new Integer(index).toString();
                    break;
                default:
                    Assert.fail("labeling scheme not used");
                }
                Assert.assertNotNull(label);

                RowColPos result = container
                    .getPositionFromLabelingScheme(label);
                Assert.assertNotNull(result);
                Assert.assertEquals(row, result.row.intValue());
                Assert.assertEquals(col, result.col.intValue());
            }
        }
    }

    @Test
    public void testGetPositionFromLabelingScheme() throws Exception {
        ContainerWrapper top = containerMap.get("Top");
        addContainerHierarchy(top);

        for (ContainerWrapper container : containerMap.values()) {
            testGetPositionFromLabelingScheme(container);
        }
    }

    @Test
    public void testGetCapacity() throws Exception {
        ContainerWrapper top = containerMap.get("Top");
        Assert.assertEquals(new Integer(CONTAINER_TOP_ROWS), top
            .getRowCapacity());
        Assert.assertEquals(new Integer(CONTAINER_TOP_COLS), top
            .getColCapacity());

    }

    @Test
    public void testGetParent() throws Exception {
        ContainerWrapper top = containerMap.get("Top");
        addContainerHierarchy(top);

        Assert.assertEquals(null, containerMap.get("Top").getParent());
        Assert.assertEquals(containerMap.get("Top"), containerMap
            .get("ChildL1").getParent());
        Assert.assertEquals(containerMap.get("ChildL1"), containerMap.get(
            "ChildL2").getParent());
        Assert.assertEquals(containerMap.get("ChildL2"), containerMap.get(
            "ChildL3").getParent());
    }

    @Test
    public void testHasParent() throws Exception {
        ContainerWrapper top = containerMap.get("Top");
        addContainerHierarchy(top);
        Assert.assertFalse(containerMap.get("Top").hasParent());
        Assert.assertTrue(containerMap.get("ChildL1").hasParent());
        Assert.assertTrue(containerMap.get("ChildL2").hasParent());
        Assert.assertTrue(containerMap.get("ChildL3").hasParent());
    }

    @Test
    public void testCanHoldSample() throws Exception {
        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        List<SampleTypeWrapper> selectedSampleTypes = getRandomSampleTypeList(allSampleTypes);
        ContainerTypeWrapper childTypeL3 = addSampleTypes(containerTypeMap
            .get("ChildCtL3"), selectedSampleTypes);
        containerTypeMap.put("ChildCtL3", childTypeL3);

        StudyWrapper study = StudyHelper.addStudy(site, "Study1");
        ContactHelper.addContactsToStudy(study, "contactsStudy1");
        ClinicWrapper clinic = study.getContactCollection().get(0)
            .getClinicWrapper();

        PatientWrapper patient = PatientHelper.addPatient("1000", study);
        PatientVisitWrapper pv = PatientVisitHelper.addPatientVisit(patient,
            clinic, Utils.getRandomDate(), Utils.getRandomDate(), Utils
                .getRandomDate());

        ContainerWrapper top = containerMap.get("Top");
        addContainerHierarchy(top);
        ContainerWrapper childL3 = containerMap.get("ChildL3");

        // reload because we changed container type
        childL3.reload();
        for (SampleTypeWrapper st : allSampleTypes) {
            SampleWrapper sample = SampleHelper
                .newSample(st, childL3, pv, 0, 0);
            if (selectedSampleTypes.contains(st)) {
                Assert.assertTrue(childL3.canHoldSample(sample));
            } else {
                Assert.assertTrue(!childL3.canHoldSample(sample));
            }
        }
    }

    @Test
    public void testGetSamples() throws Exception {
        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        List<SampleTypeWrapper> selectedSampleTypes = getRandomSampleTypeList(allSampleTypes);
        ContainerTypeWrapper childTypeL3 = addSampleTypes(containerTypeMap
            .get("ChildCtL3"), selectedSampleTypes);
        containerTypeMap.put("ChildCtL3", childTypeL3);

        StudyWrapper study = StudyHelper.addStudy(site, "Study1");
        ContactHelper.addContactsToStudy(study, "contactsStudy1");
        ClinicWrapper clinic = study.getContactCollection().get(0)
            .getClinicWrapper();

        PatientWrapper patient = PatientHelper.addPatient("1000", study);
        PatientVisitWrapper pv = PatientVisitHelper.addPatientVisit(patient,
            clinic, Utils.getRandomDate(), Utils.getRandomDate(), Utils
                .getRandomDate());

        ContainerWrapper top = containerMap.get("Top");
        addContainerHierarchy(top);
        ContainerWrapper childL3 = containerMap.get("ChildL3");
        for (int i = 0, n = selectedSampleTypes.size(); i < n; ++i) {
            SampleHelper.addSample(selectedSampleTypes.get(i), childL3, pv, i
                / CONTAINER_CHILD_L3_COLS, i % CONTAINER_CHILD_L3_COLS);
        }

        childL3.reload();
        Collection<SampleWrapper> samples = childL3.getSamples().values();
        Assert.assertEquals(selectedSampleTypes.size(), samples.size());
        for (SampleWrapper sample : samples) {
            Assert.assertTrue(selectedSampleTypes.contains(sample
                .getSampleType()));
        }
    }

    @Test
    public void testGetContainersHoldingSampleType() throws Exception {
        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        List<SampleTypeWrapper> selectedSampleTypes = getRandomSampleTypeList(allSampleTypes);
        ContainerTypeWrapper childTypeL3 = addSampleTypes(containerTypeMap
            .get("ChildCtL3"), selectedSampleTypes);
        containerTypeMap.put("ChildCtL3", childTypeL3);

        ContainerWrapper top = containerMap.get("Top");
        addContainerHierarchy(top);
        ContainerWrapper childL3 = containerMap.get("ChildL3");
        childL3.reload();

        List<ContainerWrapper> containers;

        for (SampleTypeWrapper st : allSampleTypes) {
            containers = ContainerWrapper.getContainersHoldingSampleType(
                appService, top.getSite(), "01AA01A1", st);

            if (selectedSampleTypes.contains(st)) {
                Assert.assertEquals(1, containers.size());
                Assert.assertTrue(containers.contains(childL3));

                // containers higher in the hierarchy should not container the
                // sample type
                containers = ContainerWrapper.getContainersHoldingSampleType(
                    appService, top.getSite(), "01AA01", st);
                Assert.assertEquals(0, containers.size());

                containers = ContainerWrapper.getContainersHoldingSampleType(
                    appService, top.getSite(), "01AA", st);
                Assert.assertEquals(0, containers.size());

                containers = ContainerWrapper.getContainersHoldingSampleType(
                    appService, top.getSite(), "01", st);
                Assert.assertEquals(0, containers.size());
            } else {
                Assert.assertEquals(0, containers.size());
            }
        }
    }

    @Test
    public void testGetChildren() throws Exception {
        ContainerWrapper top, childL1, childL2, childL3, childL4, childL3_2;

        top = containerMap.get("Top");
        addContainerHierarchy(top);
        childL1 = containerMap.get("ChildL1");
        childL2 = containerMap.get("ChildL2");
        childL3 = containerMap.get("ChildL3");
        childL4 = containerMap.get("ChildL4");
        childL4.delete();
        childL3.reload();

        Collection<ContainerWrapper> childL2children = childL2.getChildren()
            .values();
        Assert.assertTrue(childL2children.size() == 1);
        Assert.assertTrue(childL2children.contains(childL3));

        Collection<ContainerWrapper> childL1children = childL1.getChildren()
            .values();
        Assert.assertTrue(childL1children.size() == 1);
        Assert.assertTrue(childL1children.contains(childL2));

        Collection<ContainerWrapper> topChildren = top.getChildren().values();
        Assert.assertTrue(topChildren.size() == 1);
        Assert.assertTrue(topChildren.contains(childL1));

        // remove childL3 from childL2
        childL3.delete();
        childL2.reload();
        Assert.assertTrue(childL2.getChildren().size() == 0);

        // add again
        childL3 = ContainerHelper.addContainer(null, "0003", childL2, site,
            containerTypeMap.get("ChildCtL3"), 0, 0);
        childL2.reload();
        childL2children = childL2.getChildren().values();
        Assert.assertTrue(childL2children.size() == 1);
        Assert.assertTrue(childL2children.contains(childL3));

        childL3_2 = ContainerHelper.addContainer(null, "0004", childL2, site,
            containerTypeMap.get("ChildCtL3"), 0, 1);
        childL2.reload();
        childL2children = childL2.getChildren().values();
        Assert.assertTrue(childL2children.size() == 2);
        Assert.assertTrue(childL2children.contains(childL3));
        Assert.assertTrue(childL2children.contains(childL3_2));

        // remove first child
        childL3.delete();
        childL2.reload();
        childL2children = childL2.getChildren().values();
        Assert.assertTrue(childL2children.size() == 1);
        Assert.assertTrue(childL2children.contains(childL3_2));
    }

    @Test
    public void testGetContainerWithProductBarcodeInSite() throws Exception {
        ContainerWrapper top, childL1, result;

        top = containerMap.get("Top");
        addContainerHierarchy(top);

        String barcode = getNewBarcode();
        childL1 = ContainerHelper.addContainer(null, barcode, top, site,
            containerTypeMap.get("ChildCtL1"), 1, 0);
        top.reload();

        result = ContainerWrapper.getContainerWithProductBarcodeInSite(
            appService, site, barcode);
        Assert.assertEquals(childL1, result);
    }

    @Test
    public void testInitChildrenWithType() throws Exception {
        ContainerWrapper top;

        ContainerTypeWrapper childType2 = ContainerTypeHelper.addContainerType(
            site, "Child L1 Container Type - 2", "CCTL1-2", 3, 1, 15, false);

        top = containerMap.get("Top");
        addContainerHierarchy(top);

        Assert.assertTrue(top.getChildren().size() == 1);
        top.initChildrenWithType(childType2);
        top.reload();

        Collection<ContainerWrapper> children = top.getChildren().values();
        Assert.assertTrue(children.size() == CONTAINER_TOP_ROWS
            * CONTAINER_TOP_COLS);
        for (ContainerWrapper container : children) {
            if (container.getPosition().equals(0, 0)) {
                Assert.assertTrue(container.getContainerType().equals(
                    containerTypeMap.get("ChildCtL1")));
            } else {
                Assert.assertTrue(container.getContainerType().equals(
                    childType2));
            }
        }
    }

    @Test
    public void testDeleteChildrenWithType() throws Exception {
        ContainerWrapper top, childL1, childL2, childL3;

        top = containerMap.get("Top");
        addContainerHierarchy(top);
        childL1 = containerMap.get("ChildL1");
        childL2 = containerMap.get("ChildL2");
        childL3 = containerMap.get("ChildL3");

        Assert.assertTrue(childL3.deleteChildrenWithType(containerTypeMap
            .get("ChildCtL4")));
        Assert.assertTrue(childL2.deleteChildrenWithType(containerTypeMap
            .get("ChildCtL3")));
        Assert.assertTrue(childL1.deleteChildrenWithType(containerTypeMap
            .get("ChildCtL2")));
        Assert.assertTrue(top.deleteChildrenWithType(containerTypeMap
            .get("ChildCtL1")));
        Assert.assertFalse(top.deleteChildrenWithType(containerTypeMap
            .get("TopCT")));
    }

    @Test
    public void testCompareTo() throws Exception {
        ContainerWrapper top, childL1, childL2, childL3;

        top = containerMap.get("Top");
        addContainerHierarchy(top);
        childL1 = containerMap.get("ChildL1");
        childL2 = containerMap.get("ChildL2");
        childL3 = containerMap.get("ChildL3");

        Assert.assertEquals(-1, top.compareTo(childL1));
        Assert.assertEquals(-1, childL1.compareTo(childL2));
        Assert.assertEquals(-1, childL2.compareTo(childL3));
        Assert.assertEquals(0, top.compareTo(top));
    }

    @Test(expected = BiobankCheckException.class)
    public void testContainerTypeSameSite() throws Exception {
        SiteWrapper altSite = SiteHelper.addSite("Site2 - Container Test"
            + Utils.getRandomString(10));

        ContainerTypeWrapper altTopType = ContainerTypeHelper.newContainerType(
            altSite, "Alt Top Container Type", "ATCT", 2, CONTAINER_TOP_ROWS,
            CONTAINER_TOP_COLS, true);
        altTopType.persist();

        ContainerHelper.addContainer("01", getNewBarcode(), null, site,
            containerTypeMap.get("TopCT"));
    }

    @Test(expected = BiobankCheckException.class)
    public void testParentSameSite() throws Exception {
        SiteWrapper altSite = SiteHelper.addSite("Site2 - Container Test"
            + Utils.getRandomString(10));

        ContainerTypeWrapper childType = ContainerTypeHelper.newContainerType(
            altSite, "Alt Child L1 Container Type", "ACCTL1", 3, 1, 10, false);
        childType.persist();

        ContainerTypeWrapper altTopType = ContainerTypeHelper.newContainerType(
            altSite, "Alt Top Container Type", "ATCT", 2, CONTAINER_TOP_ROWS,
            CONTAINER_TOP_COLS, true);
        altTopType.setChildContainerTypeCollection(Arrays
            .asList(new ContainerTypeWrapper[] { childType }));
        altTopType.persist();
        altTopType.reload();

        ContainerWrapper altTop = ContainerHelper.addContainer("01",
            getNewBarcode(), null, altSite, altTopType);

        ContainerHelper.addContainer(null, getNewBarcode(), altTop, site,
            childType, 0, 0);
    }

    @Test(expected = BiobankCheckException.class)
    public void testAddChildrenTooMany() throws Exception {
        ContainerWrapper top;

        top = containerMap.get("Top");
        for (int row = 0; row < CONTAINER_TOP_ROWS; ++row) {
            for (int col = 0; col < CONTAINER_TOP_COLS; ++col) {
                top.addChild(row, col, ContainerHelper.addContainer(null,
                    getNewBarcode(), top, site, containerTypeMap
                        .get("ChildCtL1")));
            }
        }
        top.reload();

        // now add one more
        top.addChild(0, 0, ContainerHelper.addContainer(null, getNewBarcode(),
            top, site, containerTypeMap.get("ChildCtL1")));
    }
}
