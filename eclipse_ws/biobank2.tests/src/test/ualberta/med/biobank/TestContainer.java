package test.ualberta.med.biobank;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.PatientHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import test.ualberta.med.biobank.internal.StudyHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
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
    private static final int CONTAINER_CHILD_L3_ROWS = 8;

    private static final int CONTAINER_CHILD_L3_COLS = 12;

    private SiteWrapper site;

    private Map<String, ContainerTypeWrapper> containerTypeMap;

    private Map<String, ContainerWrapper> containerMap;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        List<SiteWrapper> sites = SiteWrapper.getAllSites(appService);
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();
        containerMap = new HashMap<String, ContainerWrapper>();

        if (sites.size() > 0) {
            site = sites.get(0);
        } else {
            site = SiteHelper.addSite("Site - Container Test");
        }

        deleteContainers();
        deleteContainerTypes();
        addContainerTypes();
        addContainers();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        deleteContainers();
        deleteContainerTypes();
    }

    private void deleteContainers() throws Exception {
        site.reload();
        deleteContainers(site.getTopContainerCollection());
    }

    // recursive method to delete child containers
    private void deleteContainers(List<ContainerWrapper> containerList)
        throws BiobankCheckException, Exception {
        if ((containerList == null) || (containerList.size() == 0))
            return;

        Iterator<ContainerWrapper> it = containerList.iterator();
        while (it.hasNext()) {
            ContainerWrapper container = it.next();
            if (container.getChildren().size() > 0) {
                deleteContainers(container.getChildren());
            }
            container.reload();
            container.delete();
        }
    }

    private void deleteContainerTypes() throws Exception {
        List<ContainerTypeWrapper> containerTypeList = site
            .getContainerTypeCollection();
        if (containerTypeList != null) {
            for (ContainerTypeWrapper containerType : containerTypeList) {
                containerType.delete();
            }
        }
    }

    private void addContainerTypes() throws BiobankCheckException, Exception {
        ContainerTypeWrapper topType, childType;

        childType = newContainerType(site, "Child L3 Container Type", "CCTL3",
            4, CONTAINER_CHILD_L3_ROWS, CONTAINER_CHILD_L3_COLS, false);
        childType.persist();
        containerTypeMap.put("ChildCtL3", childType);

        childType = newContainerType(site, "Child L2 Container Type", "CCTL2",
            1, 1, 10, false);
        childType
            .setChildContainerTypeCollection(new ArrayList<ContainerTypeWrapper>(
                Arrays.asList(containerTypeMap.get("ChildCtL3"))));
        childType.persist();
        containerTypeMap.put("ChildCtL2", childType);

        childType = newContainerType(site, "Child L1 Container Type", "CCTL1",
            3, 1, 10, false);
        childType
            .setChildContainerTypeCollection(new ArrayList<ContainerTypeWrapper>(
                Arrays.asList(containerTypeMap.get("ChildCtL2"))));
        childType.persist();
        containerTypeMap.put("ChildCtL1", childType);

        topType = newContainerType(site, "Top Container Type", "TCT", 2, 5, 9,
            true);
        topType
            .setChildContainerTypeCollection(new ArrayList<ContainerTypeWrapper>(
                Arrays.asList(containerTypeMap.get("ChildCtL1"))));
        topType.persist();
        containerTypeMap.put("TopCT", topType);
    }

    private void addContainers() throws BiobankCheckException, Exception {
        ContainerWrapper top = addContainer("01", "barcode1", null, site,
            containerTypeMap.get("TopCT"));
        containerMap.put("Top", top);
    }

    private void addContainerHierarchy() throws Exception {
        ContainerWrapper top, childL1, childL2, childL3;

        top = containerMap.get("Top");
        childL1 = addContainer(null, "0001", top, site, containerTypeMap
            .get("ChildCtL1"), 0, 0);
        childL2 = addContainer(null, "0002", childL1, site, containerTypeMap
            .get("ChildCtL2"), 0, 0);
        childL3 = addContainer(null, "0003", childL2, site, containerTypeMap
            .get("ChildCtL3"), 0, 0);
        containerMap.put("ChildL1", childL1);
        containerMap.put("ChildL2", childL2);
        containerMap.put("ChildL3", childL3);

    }

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        addContainer(null, null, null, site, containerTypeMap.get("TopCT"));
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        ContainerWrapper container = addContainer(null, null, null, site,
            containerTypeMap.get("TopCT"));
        Assert.assertEquals(Container.class, container.getWrappedClass());
    }

    @Test
    public void createValidContainer() throws Exception {
        ContainerWrapper container = addContainer("05", null, null, site,
            containerTypeMap.get("TopCT"));

        Integer id = container.getId();
        Assert.assertNotNull(id);
        Container containerInDB = ModelUtils.getObjectWithId(appService,
            Container.class, id);
        Assert.assertNotNull(containerInDB);
    }

    @Test(expected = BiobankCheckException.class)
    public void createNoSite() throws Exception {
        addContainer("05", null, null, null, containerTypeMap.get("TopCT"));
    }

    @Test
    public void testLabelUnique() throws Exception {
        ContainerWrapper container2;
        addContainer("05", null, null, site, containerTypeMap.get("TopCT"));
        container2 = newContainer("05", null, null, site, containerTypeMap
            .get("TopCT"));

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

        addContainer("05", "abcdef", null, site, containerTypeMap.get("TopCT"));
        container2 = newContainer("06", "abcdef", null, site, containerTypeMap
            .get("TopCT"));

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
        ContainerWrapper container = addContainer("05", "uvwxyz", null, site,
            containerTypeMap.get("TopCT"));
        container.reset();
    }

    @Test
    public void testReload() throws Exception {
        ContainerWrapper container = newContainer("05", "uvwxyz", null, site,
            containerTypeMap.get("TopCT"));
        container.reload();
    }

    @Test(expected = BiobankCheckException.class)
    public void testSetPositionOnTopLevel() throws Exception {
        addContainer("05", "uvwxyz", null, site, containerTypeMap.get("TopCT"),
            0, 0);
    }

    @Test
    public void testSetPositionOnChild() throws Exception {
        addContainer(null, "uvwxyz", containerMap.get("Top"), site,
            containerTypeMap.get("ChildCtL1"), 0, 0);
    }

    @Test
    public void testSetInvalidPositionOnChild() throws Exception {
        ContainerWrapper top, child;

        top = containerMap.get("Top");

        child = newContainer(null, "uvwxyz", top, site, containerTypeMap
            .get("ChildCtL1"), top.getRowCapacity(), top.getColCapacity());

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
        addContainer(null, "uvwxyz", top, site, containerTypeMap
            .get("ChildCtL1"), 0, 0);

        try {
            addContainer(null, "uvwxyz", top, site, containerTypeMap
                .get("ChildCtL1"), 0, 0);
            Assert
                .fail("should not be allowed to add container because of duplicate product barcode");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetContainer() throws Exception {
        ContainerWrapper top, result;

        addContainerHierarchy();
        top = containerMap.get("Top");

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
        Assert.assertEquals(false, result);

        result = top.getContainer("01AA02", containerTypeMap.get("ChildCtL1"));
        Assert.assertEquals(false, result);

        result = top
            .getContainer("01AA01A2", containerTypeMap.get("ChildCtL3"));
        Assert.assertEquals(false, result);
    }

    @Test
    public void testGetContainersHoldingContainerType() throws Exception {
        ContainerWrapper top1, top2, child1, child2;

        top1 = addContainer("02", "barcode2", null, site, containerTypeMap
            .get("TopCT"));
        child1 = addContainer(null, "0001", top1, site, containerTypeMap
            .get("ChildCtL1"), 0, 0);

        ContainerTypeWrapper topType2 = addContainerType(site,
            "Top Container Type 2", "TCT2", 2, 3, 10, true);

        top2 = addContainer("02", "barcode3", null, site, topType2);

        child2 = addContainer(null, "0002", top2, site, containerTypeMap
            .get("ChildCtL1"), 0, 0);

        List<ContainerWrapper> list = top1
            .getContainersHoldingContainerType("02AA");
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(child1));

        list = top2.getContainersHoldingContainerType("02AA");
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(child2));
    }

    @Test
    public void testGetChildWithLabel() throws Exception {
        ContainerWrapper top, child;

        top = containerMap.get("Top");
        for (int row = 0; row < 5; ++row) {
            for (int col = 0; col < 9; ++col) {
                child = newContainer(null, "0001", top, site, containerTypeMap
                    .get("ChildCtL1"), row, col);
                child.setParent(top);
                child.persist();

                int index = 9 * row + col;
                int len = LabelingScheme.CBSR_LABELLING_PATTERN.length();
                String label = String.format("01%c%c",
                    LabelingScheme.CBSR_LABELLING_PATTERN.charAt(index / len),
                    LabelingScheme.CBSR_LABELLING_PATTERN.charAt(index % len));

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
                case 2:
                    label = String.format(container.getParent().getLabel()
                        + "%c%c", LabelingScheme.CBSR_LABELLING_PATTERN
                        .charAt(index / len),
                        LabelingScheme.CBSR_LABELLING_PATTERN.charAt(index
                            % len));
                case 3:
                    label = new Integer(index).toString();
                }

                RowColPos result = container
                    .getPositionFromLabelingScheme(label);
                Assert.assertEquals(row, result.row.intValue());
                Assert.assertEquals(col, result.col.intValue());
            }
        }
    }

    @Test
    public void testGetPositionFromLabelingScheme() throws Exception {
        addContainerHierarchy();
        for (ContainerWrapper container : containerMap.values()) {
            testGetPositionFromLabelingScheme(container);
        }
    }

    @Test
    public void testGetCapacity() throws Exception {
        ContainerWrapper top = containerMap.get("Top");
        Assert.assertEquals(new Integer(5), top.getRowCapacity());
        Assert.assertEquals(new Integer(9), top.getColCapacity());

    }

    @Test
    public void testGetParent() throws Exception {
        addContainerHierarchy();
        Assert.assertEquals(containerMap.get("Top"), containerMap
            .get("ChildL1").getParent());
        Assert.assertEquals(containerMap.get("ChildL1"), containerMap.get(
            "ChildL2").getParent());
        Assert.assertEquals(containerMap.get("ChildL2"), containerMap.get(
            "ChildL3").getParent());
    }

    @Test
    public void testHasParent() throws Exception {
        addContainerHierarchy();
        Assert.assertEquals(false, containerMap.get("Top").hasParent());
        Assert.assertEquals(true, containerMap.get("ChildL1").hasParent());
        Assert.assertEquals(true, containerMap.get("ChildL2").hasParent());
        Assert.assertEquals(true, containerMap.get("ChildL3").hasParent());
    }

    @Test
    public void testCanHoldSample() throws Exception {
        List<SampleTypeWrapper> sampleTypeList = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        Assert.assertTrue("not enough sample types for test", (sampleTypeList
            .size() > 10));

        // assign all but first 10 sample types to container type
        List<SampleTypeWrapper> removedList = new ArrayList<SampleTypeWrapper>();
        for (int i = 0; i < 10; ++i) {
            removedList.add(sampleTypeList.get(0));
            sampleTypeList.remove(0);
        }
        ContainerTypeWrapper childTypeL3 = containerTypeMap.get("ChildCtL3");
        childTypeL3.setSampleTypeCollection(sampleTypeList);
        childTypeL3.persist();

        StudyWrapper study = StudyHelper.addStudy("Study1", "S1", site);

        PatientWrapper patient = PatientHelper.addPatient("1000", study);
        PatientVisitWrapper pv = PatientHelper
            .addPatientVisit(patient, null, Utils.getRandomDate(), Utils
                .getRandomDate(), Utils.getRandomDate());
        addContainerHierarchy();
        ContainerWrapper childL3 = containerMap.get("ChildL3");
        for (int i = 0, n = sampleTypeList.size(); i < n; ++i) {
            SampleWrapper sample = newSample(sampleTypeList.get(i), childL3,
                pv, 0, 0);
            Assert.assertTrue(childL3.canHoldSample(sample));
        }
    }

    @Test
    public void testGetSamples() throws Exception {
        List<SampleTypeWrapper> sampleTypeList = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        Assert.assertTrue("not enough sample types for test", (sampleTypeList
            .size() > 10));

        // assign all but first 10 sample types to container type
        List<SampleTypeWrapper> removedList = new ArrayList<SampleTypeWrapper>();
        for (int i = 0; i < 10; ++i) {
            removedList.add(sampleTypeList.get(0));
            sampleTypeList.remove(0);
        }
        ContainerTypeWrapper childTypeL3 = containerTypeMap.get("ChildCtL3");
        childTypeL3.setSampleTypeCollection(sampleTypeList);
        childTypeL3.persist();

        StudyWrapper study = StudyHelper.addStudy("Study1", "S1", site);

        PatientWrapper patient = PatientHelper.addPatient("1000", study);
        PatientVisitWrapper pv = PatientHelper
            .addPatientVisit(patient, null, Utils.getRandomDate(), Utils
                .getRandomDate(), Utils.getRandomDate());
        addContainerHierarchy();
        ContainerWrapper childL3 = containerMap.get("ChildL3");
        for (int i = 0, n = sampleTypeList.size(); i < n; ++i) {
            addSample(sampleTypeList.get(i), childL3, pv, i
                / CONTAINER_CHILD_L3_COLS, i % CONTAINER_CHILD_L3_COLS);
        }

        List<SampleWrapper> samples = childL3.getSamples();
        Assert.assertEquals(sampleTypeList.size(), samples.size());
        for (SampleWrapper sample : samples) {
            Assert.assertTrue(sampleTypeList.contains(sample.getSampleType()));
        }
    }

    @Test
    public void testGetChildren() throws Exception {
        ContainerWrapper top, childL1, childL2, childL3, childL3_2;

        addContainerHierarchy();
        top = containerMap.get("Top");
        childL1 = containerMap.get("ChildL1");
        childL2 = containerMap.get("ChildL2");
        childL3 = containerMap.get("ChildL3");

        List<ContainerWrapper> childL2children = childL2.getChildren();
        Assert.assertTrue(childL2children.size() == 1);
        Assert.assertTrue(childL2children.contains(childL3));

        List<ContainerWrapper> childL1children = childL1.getChildren();
        Assert.assertTrue(childL1children.size() == 1);
        Assert.assertTrue(childL1children.contains(childL2));

        List<ContainerWrapper> topChildren = top.getChildren();
        Assert.assertTrue(topChildren.size() == 1);
        Assert.assertTrue(topChildren.contains(childL1));

        // remove childL3 from childL2
        childL3.delete();
        Assert.assertTrue(childL2.getChildren().size() == 0);

        // add again
        childL3 = addContainer(null, "0003", childL2, site, containerTypeMap
            .get("ChildCtL3"), 0, 0);
        childL2children = childL2.getChildren();
        Assert.assertTrue(childL2children.size() == 1);
        Assert.assertTrue(childL2children.contains(childL3));

        childL3_2 = addContainer(null, "0004", childL2, site, containerTypeMap
            .get("ChildCtL3"), 0, 0);
        childL2children = childL2.getChildren();
        Assert.assertTrue(childL2children.size() == 2);
        Assert.assertTrue(childL2children.contains(childL3));
        Assert.assertTrue(childL2children.contains(childL3_2));

        // remove first child
        childL3.delete();
        childL2children = childL2.getChildren();
        Assert.assertTrue(childL2children.size() == 1);
        Assert.assertTrue(childL2children.contains(childL3_2));

    }

    @Test
    public void testAssignNewParent() {
        fail("Not yet implemented");
    }

    @Test
    public void testAssignChildLabels() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetAllParents() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetPossibleParents() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetContainersHoldingSampleType() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetContainersInSite() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetContainerWithProductBarcodeInSite() {
        fail("Not yet implemented");
    }

    @Test
    public void testInitChildrenWithType() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeketeChildrenWithType() {
        fail("Not yet implemented");
    }

    @Test
    public void testCompareTo() {
        fail("Not yet implemented");
    }

}
