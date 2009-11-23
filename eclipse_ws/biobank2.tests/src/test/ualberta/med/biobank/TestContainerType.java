package test.ualberta.med.biobank;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import test.ualberta.med.biobank.internal.ContainerHelper;
import test.ualberta.med.biobank.internal.ContainerTypeHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.ContainerType;

public class TestContainerType extends TestDatabase {
    private static final int CONTAINER_TOP_ROWS = 5;

    private static final int CONTAINER_TOP_COLS = 9;

    private static final int CONTAINER_CHILD_L3_ROWS = 8;

    private static final int CONTAINER_CHILD_L3_COLS = 12;

    private Map<String, ContainerTypeWrapper> containerTypeMap;

    private SiteWrapper site;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();
        site = SiteHelper.addSite("Site - Container Test"
            + Utils.getRandomString(10));
        addTopContainerType(site);
    }

    private void addTopContainerType(SiteWrapper site) throws Exception {
        ContainerTypeWrapper topType;
        topType = ContainerTypeHelper.addContainerType(site,
            "Top Container Type", "TCT", 2, CONTAINER_TOP_ROWS,
            CONTAINER_TOP_COLS, true);
        containerTypeMap.put("TopCT", topType);
    }

    private ContainerTypeWrapper addContainerTypeHierarchy(
        ContainerTypeWrapper topType, int level) throws Exception {
        ContainerTypeWrapper childType;

        if (level >= 3) {
            childType = ContainerTypeHelper.addContainerType(site,
                "Child L3 Container Type", "CCTL3", 1, CONTAINER_CHILD_L3_ROWS,
                CONTAINER_CHILD_L3_COLS, false);
            containerTypeMap.put("ChildCtL3", childType);
        }

        if (level >= 2) {
            childType = ContainerTypeHelper.newContainerType(site,
                "Child L2 Container Type", "CCTL2", 1, 1, 10, false);
            childType.setChildContainerTypeCollection(Arrays
                .asList(containerTypeMap.get("ChildCtL3")));
            childType.persist();
            containerTypeMap.put("ChildCtL2", childType);
        }

        if (level >= 1) {
            childType = ContainerTypeHelper.newContainerType(site,
                "Child L1 Container Type", "CCTL1", 3, 1, 10, false);
            childType.setChildContainerTypeCollection(Arrays
                .asList(containerTypeMap.get("ChildCtL2")));
            childType.persist();
            containerTypeMap.put("ChildCtL1", childType);

            topType.setChildContainerTypeCollection(Arrays
                .asList(containerTypeMap.get("ChildCtL1")));
            topType.persist();
            topType.reload();
        }
        return topType;
    }

    private ContainerTypeWrapper addContainerTypeHierarchy(
        ContainerTypeWrapper topType) throws Exception {
        return addContainerTypeHierarchy(topType, 3);
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        testGettersAndSetters(containerTypeMap.get("TopCT"));
    }

    @Test
    public void testCompareTo() throws Exception {
        ContainerTypeWrapper topType, childTypeL1, childTypeL2, childTypeL3;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        childTypeL1 = containerTypeMap.get("ChildCtL1");
        childTypeL2 = containerTypeMap.get("ChildCtL2");
        childTypeL3 = containerTypeMap.get("ChildCtL3");

        Assert.assertEquals(1, topType.compareTo(childTypeL1));
        Assert.assertEquals(-1, childTypeL1.compareTo(childTypeL2));
        Assert.assertEquals(-1, childTypeL2.compareTo(childTypeL3));
        Assert.assertEquals(0, topType.compareTo(topType));
    }

    @Test
    public void testReset() throws Exception {
        ContainerTypeWrapper topType = containerTypeMap.get("TopCT");
        topType.reset();
    }

    @Test
    public void testReload() throws Exception {
        ContainerTypeWrapper topType = containerTypeMap.get("TopCT");
        topType.reload();
    }

    @Test
    public void testGetWrappedClass() {
        ContainerTypeWrapper topType = containerTypeMap.get("TopCT");
        Assert.assertEquals(ContainerType.class, topType.getWrappedClass());
    }

    @Test
    public void testNameUnique() throws Exception {
        ContainerTypeWrapper topType2;

        // use same name as containerTypeMap.get("TopCT")
        topType2 = ContainerTypeHelper.newContainerType(site,
            "Top Container Type", "TCT", 3, CONTAINER_TOP_ROWS + 1,
            CONTAINER_TOP_COLS - 1, true);

        try {
            topType2.persist();
            Assert
                .fail("should not be allowed to add container type because of duplicate name");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetAllChildren() throws Exception {
        ContainerTypeWrapper topType, childTypeL1, childTypeL2, childTypeL3;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        childTypeL1 = containerTypeMap.get("ChildCtL1");
        childTypeL2 = containerTypeMap.get("ChildCtL2");
        childTypeL3 = containerTypeMap.get("ChildCtL3");

        Collection<ContainerTypeWrapper> children = topType.getAllChildren();
        Assert.assertEquals(3, children.size());
        Assert.assertTrue(children.contains(childTypeL1));
        Assert.assertTrue(children.contains(childTypeL2));
        Assert.assertTrue(children.contains(childTypeL3));
        Assert.assertFalse(children.contains(topType));

        children = childTypeL1.getAllChildren();
        Assert.assertEquals(2, children.size());
        Assert.assertTrue(children.contains(childTypeL2));
        Assert.assertTrue(children.contains(childTypeL3));
        Assert.assertFalse(children.contains(topType));
        Assert.assertFalse(children.contains(childTypeL1));

        children = childTypeL2.getAllChildren();
        Assert.assertEquals(1, children.size());
        Assert.assertTrue(children.contains(childTypeL3));
        Assert.assertFalse(children.contains(topType));
        Assert.assertFalse(children.contains(childTypeL1));
        Assert.assertFalse(children.contains(childTypeL2));
    }

    @Test
    public void testIsUsedByContainers() throws Exception {
        addContainerTypeHierarchy(containerTypeMap.get("TopCT"));

        String[] keys = new String[] { "TopCT", "ChildCtL1", "ChildCtL2",
            "ChildCtL3" };

        List<ContainerWrapper> containers = new ArrayList<ContainerWrapper>();

        for (String key : keys) {
            ContainerTypeWrapper ct = containerTypeMap.get(key);
            Assert.assertFalse(ct.isUsedByContainers());

            if (key.equals("TopCT")) {
                containers.add(ContainerHelper.addContainer("01", TestContainer
                    .getNewBarcode(), null, site, ct));
            } else {
                containers.add(ContainerHelper.addContainer(null, TestContainer
                    .getNewBarcode(), containers.get(containers.size() - 1),
                    site, ct, 0, 0));
            }

            ct.reload();
            Assert.assertTrue(ct.isUsedByContainers());

        }

        // now delete all containers
        for (ContainerWrapper container : containers) {
            container.delete();
        }
        containers.clear();

        for (String key : keys) {
            ContainerTypeWrapper ct = containerTypeMap.get(key);
            Assert.assertFalse(ct.isUsedByContainers());
        }
    }

    @Test
    public void testGetParentContainerTypes() throws Exception {
        ContainerTypeWrapper topType, childTypeL1, childTypeL2, childTypeL3, childTypeL2_2, childTypeL2_3;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        childTypeL1 = containerTypeMap.get("ChildCtL1");
        childTypeL2 = containerTypeMap.get("ChildCtL2");
        childTypeL3 = containerTypeMap.get("ChildCtL3");

        // each childTypeL1, childTypeL2, and childTypeL3 should have single
        // parent
        List<ContainerTypeWrapper> list = childTypeL1.getParentContainerTypes();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(topType));

        list = childTypeL2.getParentContainerTypes();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(childTypeL1));

        list = childTypeL3.getParentContainerTypes();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(childTypeL2));

        // add a second parent to childTypeL3
        childTypeL2_2 = ContainerTypeHelper.newContainerType(site,
            "Child L2 Container Type 2", "CCTL2_2", 1, 4, 4, false);
        childTypeL2_2.setChildContainerTypeCollection(Arrays
            .asList(childTypeL3));
        childTypeL2_2.persist();

        list = childTypeL3.getParentContainerTypes();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_2));

        // add a third parent to childTypeL3
        childTypeL2_3 = ContainerTypeHelper.newContainerType(site,
            "Child L2 Container Type 3", "CCTL2_2", 1, 5, 7, false);
        childTypeL2_3.setChildContainerTypeCollection(Arrays
            .asList(childTypeL3));
        childTypeL2_3.persist();

        list = childTypeL3.getParentContainerTypes();
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_2));
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2_2
        childTypeL2_2.delete();

        // test childTypeL3's parents again
        list = childTypeL3.getParentContainerTypes();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2
        childTypeL2.delete();

        // test childTypeL3's parents again
        list = childTypeL3.getParentContainerTypes();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2_3
        childTypeL2_3.delete();
        list = childTypeL3.getParentContainerTypes();
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testGetSampleTypeCollection() throws Exception {
        addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        ContainerTypeWrapper childTypeL3 = containerTypeMap.get("ChildCtL3");
        Collection<SampleTypeWrapper> collection = childTypeL3
            .getSampleTypeCollection();
        Assert.assertEquals(null, collection);

        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        List<SampleTypeWrapper> selectedSampleTypes = TestCommon
            .getRandomSampleTypeList(r, allSampleTypes);

        childTypeL3 = TestCommon.addSampleTypes(childTypeL3,
            selectedSampleTypes);
        childTypeL3.setSampleTypeCollection(selectedSampleTypes);
        collection = childTypeL3.getSampleTypeCollection();
        Assert.assertEquals(selectedSampleTypes.size(), collection.size());
        for (SampleTypeWrapper sample : selectedSampleTypes) {
            Assert.assertTrue(collection.contains(sample));
        }

        childTypeL3.setSampleTypeCollection(null);
        collection = childTypeL3.getSampleTypeCollection();
        Assert.assertEquals(null, collection);
    }

    @Test
    public void testGetSampleTypesRecursively() throws Exception {
        ContainerTypeWrapper topType, childTypeL3;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        childTypeL3 = containerTypeMap.get("ChildCtL3");
        Collection<SampleTypeWrapper> collection = topType
            .getSampleTypesRecursively();
        Assert.assertEquals(0, collection.size());

        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(appService, true);
        List<SampleTypeWrapper> selectedSampleTypes = TestCommon
            .getRandomSampleTypeList(r, allSampleTypes);

        childTypeL3 = TestCommon.addSampleTypes(childTypeL3,
            selectedSampleTypes);
        childTypeL3.setSampleTypeCollection(selectedSampleTypes);
        collection = topType.getSampleTypesRecursively();
        Assert.assertEquals(selectedSampleTypes.size(), collection.size());
        for (SampleTypeWrapper sample : selectedSampleTypes) {
            Assert.assertTrue(collection.contains(sample));
        }

        childTypeL3.setSampleTypeCollection(null);
        collection = topType.getSampleTypesRecursively();
        Assert.assertEquals(null, collection);
    }

    @Test
    public void testGetChildContainerTypeCollection() throws Exception {
        ContainerTypeWrapper topType, childTypeL1, childTypeL2, childTypeL3, childTypeL2_2, childTypeL2_3;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        childTypeL1 = containerTypeMap.get("ChildCtL1");
        childTypeL2 = containerTypeMap.get("ChildCtL2");
        childTypeL3 = containerTypeMap.get("ChildCtL3");

        // each childTypeL1, childTypeL2, and childTypeL3 should have single
        // child
        List<ContainerTypeWrapper> list = topType
            .getChildContainerTypeCollection();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(childTypeL1));

        list = childTypeL1.getChildContainerTypeCollection();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(childTypeL2));

        list = childTypeL2.getChildContainerTypeCollection();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(childTypeL3));

        // add a second child to childTypeL1
        childTypeL2_2 = ContainerTypeHelper.newContainerType(site,
            "Child L2 Container Type 2", "CCTL2_2", 1, 4, 4, false);
        childTypeL1.setChildContainerTypeCollection(Arrays.asList(childTypeL2,
            childTypeL2_2));
        childTypeL2_2.persist();

        list = childTypeL1.getChildContainerTypeCollection();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_2));

        // add a third child to childTypeL1
        childTypeL2_3 = ContainerTypeHelper.newContainerType(site,
            "Child L2 Container Type 3", "CCTL2_2", 1, 5, 7, false);
        childTypeL1.setChildContainerTypeCollection(Arrays.asList(childTypeL2,
            childTypeL2_2, childTypeL2_3));
        childTypeL2_3.persist();

        list = childTypeL1.getChildContainerTypeCollection();
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_2));
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2_2
        childTypeL2_2.delete();

        // test childTypeL1's children again
        list = childTypeL1.getChildContainerTypeCollection();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2
        childTypeL2.delete();

        // test childTypeL3's parents again
        list = childTypeL1.getChildContainerTypeCollection();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2_3
        childTypeL2_3.delete();
        list = childTypeL1.getChildContainerTypeCollection();
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testGetSite() throws Exception {
        ContainerTypeWrapper topType, childTypeL1, childTypeL2, childTypeL3;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        childTypeL1 = containerTypeMap.get("ChildCtL1");
        childTypeL2 = containerTypeMap.get("ChildCtL2");
        childTypeL3 = containerTypeMap.get("ChildCtL3");

        Assert.assertEquals(site, topType.getSite());
        Assert.assertEquals(site, childTypeL1.getSite());
        Assert.assertEquals(site, childTypeL2.getSite());
        Assert.assertEquals(site, childTypeL3.getSite());
    }

    @Test
    public void testGetCapacity() throws Exception {
        ContainerTypeWrapper topType, childTypeL1, childTypeL2, childTypeL3;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        childTypeL1 = containerTypeMap.get("ChildCtL1");
        childTypeL2 = containerTypeMap.get("ChildCtL2");
        childTypeL3 = containerTypeMap.get("ChildCtL3");

        Assert.assertEquals(CONTAINER_TOP_ROWS, topType.getRowCapacity()
            .intValue());
        Assert.assertEquals(CONTAINER_TOP_COLS, topType.getColCapacity()
            .intValue());

        Assert.assertEquals(1, childTypeL1.getRowCapacity().intValue());
        Assert.assertEquals(10, childTypeL1.getColCapacity().intValue());

        Assert.assertEquals(1, childTypeL2.getRowCapacity().intValue());
        Assert.assertEquals(10, childTypeL2.getColCapacity().intValue());

        Assert.assertEquals(CONTAINER_CHILD_L3_ROWS, childTypeL3
            .getRowCapacity().intValue());
        Assert.assertEquals(CONTAINER_CHILD_L3_COLS, childTypeL3
            .getColCapacity().intValue());

        childTypeL3.setRowCapacity(CONTAINER_CHILD_L3_ROWS - 1);
        childTypeL3.setColCapacity(CONTAINER_CHILD_L3_COLS - 1);

        Assert.assertEquals(CONTAINER_CHILD_L3_ROWS - 1, childTypeL3
            .getRowCapacity().intValue());
        Assert.assertEquals(CONTAINER_CHILD_L3_COLS - 1, childTypeL3
            .getColCapacity().intValue());
    }

    @Test
    public void testGetChildLabelingSchemeName() throws Exception {
        ContainerTypeWrapper topType, childTypeL1, childTypeL2, childTypeL3;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        childTypeL1 = containerTypeMap.get("ChildCtL1");
        childTypeL2 = containerTypeMap.get("ChildCtL2");
        childTypeL3 = containerTypeMap.get("ChildCtL3");

        Assert.assertEquals(CONTAINER_TOP_ROWS, topType.getRowCapacity()
            .intValue());
    }

    @Test
    public void testCanRemoveChildrenContainer() {
        fail("Not yet implemented");
    }

    @Test
    public void testCheckNewCapacity() {
        fail("Not yet implemented");
    }

    @Test
    public void testCheckTopLevel() {
        fail("Not yet implemented");
    }

    @Test
    public void testCheckLabelingScheme() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetTopContainerTypesInSite() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetContainerTypesInSite() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetAllLabelingSchemes() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetContainersCount() {
        fail("Not yet implemented");
    }
}
