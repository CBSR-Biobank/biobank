package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ModelIsUsedException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.NullPropertyException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class TestContainerType extends TestDatabase {
    private static final int CONTAINER_TOP_ROWS = 5;

    private static final int CONTAINER_TOP_COLS = 9;

    private static final int CONTAINER_CHILD_L3_ROWS = 8;

    private static final int CONTAINER_CHILD_L3_COLS = 12;

    private Map<String, ContainerTypeWrapper> containerTypeMap;

    private SiteWrapper site;

    // the methods to skip in the getters and setters test
    private static final List<String> GETTER_SKIP_METHODS = Arrays.asList(
        "getChildLabelingSchemeId", "getChildLabelingSchemeName",
        "getRowCapacity", "getColCapacity");

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();
        site = SiteHelper.addSite("Site - Container Test"
            + Utils.getRandomString(10));
        addTopContainerType(site);
    }

    private ContainerTypeWrapper addTopContainerType(SiteWrapper site)
        throws Exception {
        ContainerTypeWrapper topType = ContainerTypeHelper.addContainerType(
            site, "Top Container Type", "TCT", 2, CONTAINER_TOP_ROWS,
            CONTAINER_TOP_COLS, true);
        containerTypeMap.put("TopCT", topType);
        return topType;
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
                "Child L2 Container Type", "CCTL2", 3, 1, 10, false);
            if (containerTypeMap.get("ChildCtL3") != null) {
                childType.addToChildContainerTypeCollection(Arrays
                    .asList(containerTypeMap.get("ChildCtL3")));
            }
            childType.persist();
            containerTypeMap.put("ChildCtL2", childType);
        }

        if (level >= 1) {
            childType = ContainerTypeHelper.newContainerType(site,
                "Child L1 Container Type", "CCTL1", 3, 1, 10, false);
            if (containerTypeMap.get("ChildCtL2") != null) {
                childType.addToChildContainerTypeCollection(Arrays
                    .asList(containerTypeMap.get("ChildCtL2")));
            }
            childType.persist();
            containerTypeMap.put("ChildCtL1", childType);

            if (containerTypeMap.get("ChildCtL1") != null) {
                topType.addToChildContainerTypeCollection(Arrays
                    .asList(containerTypeMap.get("ChildCtL1")));
            }
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
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        ContainerTypeWrapper topType = containerTypeMap.get("TopCT");
        testGettersAndSetters(topType, GETTER_SKIP_METHODS);
    }

    @Test
    public void testLabelingSchemeCapacity() throws Exception {
        Integer maxRows = null;
        Integer maxCols = null;
        Integer maxCapacity = null;

        Collection<ContainerLabelingSchemeWrapper> schemeWrappers = ContainerLabelingSchemeWrapper
            .getAllLabelingSchemesMap(appService).values();

        ContainerTypeWrapper cTWrapper = ContainerTypeHelper.newContainerType(
            site, "Bogus Top Container Type", "BTCT", 2, 1, 1, true);

        for (ContainerLabelingSchemeWrapper schemeWrapper : schemeWrappers) {
            cTWrapper.setChildLabelingSchemeById(schemeWrapper.getId());

            maxRows = schemeWrapper.getMaxRows();
            maxCols = schemeWrapper.getMaxCols();
            maxCapacity = schemeWrapper.getMaxCapacity();

            Assert.assertNotNull(
                "Missing maximum capacity for container labeling scheme "
                    + schemeWrapper.getName(), maxCapacity);

            maxRows = maxRows != null ? maxRows : maxCapacity;
            maxCols = maxCols != null ? maxCols : maxCapacity;

            Assert.assertTrue("Max rows should not exceed max capacity.",
                maxRows <= maxCapacity);
            Assert.assertTrue("Max cols should not exceed max capacity.",
                maxCols <= maxCapacity);

            checkIllgealCapacities(cTWrapper, maxCapacity, maxRows, maxCols);

            // TODO: the follow check takes _forever_ skip it for now
            // checkLegalCapacities(cTWrapper, maxCapacity, maxRows, maxCols);
        }
    }

    private void checkIllgealCapacities(ContainerTypeWrapper wrapper,
        Integer maxCapacity, Integer maxRows, Integer maxCols) {

        try {
            wrapper.setRowCapacity(maxRows + 1);
            wrapper.setColCapacity(1);
            wrapper.persist();
            Assert.fail("Not allowed to set row capacity over maximum.");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        try {
            wrapper.setRowCapacity(1);
            wrapper.setColCapacity(maxCols + 1);
            wrapper.persist();
            Assert.fail("Not allowed to set col capacity over maximum.");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        try {
            wrapper.setColCapacity(-1);
            wrapper.setRowCapacity(-1);
            wrapper.persist();
            Assert.fail("Not allowed to set negative row or col capacity.");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        try {
            wrapper.setColCapacity(0);
            wrapper.setRowCapacity(0);
            wrapper.persist();
            Assert.fail("Not allowed to set row or col capacity to zero.");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    private void checkLegalCapacities(ContainerTypeWrapper wrapper,
        Integer maxCapacity, Integer maxRows, Integer maxCols) throws Exception {

        int numRows, numCols;

        for (numRows = 1; numRows <= maxRows; numRows++) {
            numCols = maxCapacity / numRows;
            if (numCols <= maxCols) {
                checkCapacity(wrapper, numRows, numCols);
            }
        }

        for (numCols = 1; numCols <= maxCols; numCols++) {
            numRows = maxCapacity / numCols;
            if (numRows <= maxRows) {
                checkCapacity(wrapper, numRows, numCols);
            }
        }
    }

    private void checkCapacity(ContainerTypeWrapper wrapper, Integer numRows,
        Integer numCols) throws Exception {
        wrapper.setRowCapacity(numRows);
        wrapper.setColCapacity(numCols);

        wrapper.persist();
        wrapper.reload();

        Assert.assertEquals("[gs]etRowCapacity() failed.", numRows,
            wrapper.getRowCapacity());

        Assert.assertEquals("[gs]etColCapacity() failed.", numCols,
            wrapper.getColCapacity());
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
    public void testSite() throws Exception {
        ContainerTypeWrapper topType2;

        // use same name as containerTypeMap.get("TopCT")
        topType2 = ContainerTypeHelper.newContainerType(null,
            "Top Container Type 2", "TCT 2", 3, CONTAINER_TOP_ROWS + 1,
            CONTAINER_TOP_COLS - 1, true);

        try {
            topType2.persist();
            Assert
                .fail("should not be allowed to add container type because of site is not set");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }
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
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCapacity() throws Exception {
        ContainerTypeWrapper topType2;

        topType2 = ContainerTypeHelper.newContainerType(site,
            "Top Container Type 2", "TCT2", 3, null, 1, true);

        try {
            topType2.persist();
            Assert
                .fail("should not be allowed to add container with null rows");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        topType2 = ContainerTypeHelper.newContainerType(site,
            "Top Container Type 2", "TCT2", 3, 1, null, true);

        try {
            topType2.persist();
            Assert
                .fail("should not be allowed to add container with null columns");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        topType2 = ContainerTypeHelper.newContainerType(site,
            "Top Container Type 2", "TCT2", 3, null, null, true);

        try {
            topType2.persist();
            Assert
                .fail("should not be allowed to add container with null capacity");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testChangeLabelingScheme() throws Exception {

        // test null labeling scheme
        try {
            ContainerTypeHelper.newContainerType(site, "Top Container Type 2",
                "TCT2", null, null, null, true);
            Assert
                .fail("should not be allowed to add container with null capacity");
        } catch (ApplicationException e) {
            Assert.assertTrue(true);
        }

        ContainerTypeWrapper topType;

        // test changing labeling scheme
        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        ContainerHelper.addContainer(String.valueOf(r.nextInt()),
            TestCommon.getNewBarcode(r), site, topType);
        topType.setChildLabelingSchemeById(3);

        try {
            topType.persist();
            Assert.fail("should not be allowed to change labeling scheme");
        } catch (BiobankSessionException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testChangeTopLevel() throws Exception {
        ContainerTypeWrapper topType;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        ContainerHelper.addContainer(String.valueOf(r.nextInt()),
            TestCommon.getNewBarcode(r), site, topType);
        topType.setTopLevel(false);

        try {
            topType.persist();
            Assert.fail("should not be allowed to change top level setting");
        } catch (BiobankSessionException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testChangeCapacity() throws Exception {
        ContainerTypeWrapper topType;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        ContainerHelper.addContainer(String.valueOf(r.nextInt()),
            TestCommon.getNewBarcode(r), site, topType);
        topType.setRowCapacity(topType.getRowCapacity() + 1);

        try {
            topType.persist();
            Assert.fail("should not be allowed to change capacity");
        } catch (BiobankSessionException e) {
            topType.setRowCapacity(topType.getRowCapacity() - 1);
            Assert.assertTrue(true);
        }

        topType.setColCapacity(topType.getColCapacity() + 1);

        try {
            topType.persist();
            Assert.fail("should not be allowed to change capacity");
        } catch (BiobankSessionException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDelete() throws Exception {
        ContainerTypeWrapper topType = addContainerTypeHierarchy(containerTypeMap
            .get("TopCT"));
        ContainerHelper.addContainer(String.valueOf(r.nextInt()),
            TestCommon.getNewBarcode(r), site, topType);
        try {
            topType.delete();
            Assert.fail("cannot delete, one container is using this type");
        } catch (ModelIsUsedException e) {
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

        Collection<ContainerTypeWrapper> children = topType
            .getChildrenRecursively();
        Assert.assertEquals(3, children.size());
        Assert.assertTrue(children.contains(childTypeL1));
        Assert.assertTrue(children.contains(childTypeL2));
        Assert.assertTrue(children.contains(childTypeL3));
        Assert.assertFalse(children.contains(topType));

        children = childTypeL1.getChildrenRecursively();
        Assert.assertEquals(2, children.size());
        Assert.assertTrue(children.contains(childTypeL2));
        Assert.assertTrue(children.contains(childTypeL3));
        Assert.assertFalse(children.contains(topType));
        Assert.assertFalse(children.contains(childTypeL1));

        children = childTypeL2.getChildrenRecursively();
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
                ContainerWrapper top = ContainerHelper.addContainer("01",
                    TestCommon.getNewBarcode(r), site, ct);
                containers.add(top);
            } else {
                containers.add(ContainerHelper.addContainer(null,
                    TestCommon.getNewBarcode(r),
                    containers.get(containers.size() - 1), site, ct, 0, 0));
            }

            ct.reload();
            Assert.assertTrue(ct.isUsedByContainers());

        }

        // now delete all containers
        containers.get(3).delete();
        containers.get(2).delete();
        containers.get(1).delete();
        containers.get(0).delete();
        containers.clear();

        for (String key : keys) {
            ContainerTypeWrapper ct = containerTypeMap.get(key);
            ct.reload();
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
        List<ContainerTypeWrapper> list = childTypeL1
            .getParentContainerTypeCollection();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(topType));

        list = childTypeL2.getParentContainerTypeCollection();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(childTypeL1));

        list = childTypeL3.getParentContainerTypeCollection();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(childTypeL2));

        // add a second parent to childTypeL3
        childTypeL2_2 = ContainerTypeHelper.newContainerType(site,
            "Child L2 Container Type 2", "CCTL2_2", 1, 4, 4, false);
        childTypeL2_2.addToChildContainerTypeCollection(Arrays
            .asList(childTypeL3));
        childTypeL2_2.persist();

        list = childTypeL3.getParentContainerTypeCollection();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_2));

        // add a third parent to childTypeL3
        childTypeL2_3 = ContainerTypeHelper.newContainerType(site,
            "Child L2 Container Type 3", "CCTL2_3", 1, 5, 7, false);
        childTypeL2_3.addToChildContainerTypeCollection(Arrays
            .asList(childTypeL3));
        childTypeL2_3.persist();

        list = childTypeL3.getParentContainerTypeCollection();
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_2));
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2_2
        childTypeL2_2.delete();

        // test childTypeL3's parents again
        childTypeL3.reload(); // TODO: shouldn't this work w/o reload?
        list = childTypeL3.getParentContainerTypeCollection();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2
        childTypeL2.delete();

        // test childTypeL3's parents again
        childTypeL3.reload();
        list = childTypeL3.getParentContainerTypeCollection();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2_3
        childTypeL2_3.delete();

        childTypeL3.reload();
        list = childTypeL3.getParentContainerTypeCollection();
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testGetSpecimenTypeCollection() throws Exception {
        addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        ContainerTypeWrapper childTypeL3 = containerTypeMap.get("ChildCtL3");
        Collection<SpecimenTypeWrapper> childTypeL3SampleTypes = childTypeL3
            .getSpecimenTypeCollection(false);
        Assert.assertTrue((childTypeL3SampleTypes == null)
            || (childTypeL3SampleTypes.size() == 0));

        List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        List<SpecimenTypeWrapper> selectedSampleTypes = TestCommon
            .getRandomSampleTypeList(r, allSampleTypes);
        // get list of unselected sample types
        List<SpecimenTypeWrapper> unselectedSampleTypes = new ArrayList<SpecimenTypeWrapper>();
        for (SpecimenTypeWrapper sampleType : allSampleTypes) {
            if (!selectedSampleTypes.contains(sampleType)) {
                unselectedSampleTypes.add(sampleType);
            }
        }
        childTypeL3.addToSpecimenTypeCollection(selectedSampleTypes);
        childTypeL3.persist();
        childTypeL3.reload();
        childTypeL3SampleTypes = childTypeL3.getSpecimenTypeCollection(false);
        Assert.assertEquals(selectedSampleTypes.size(),
            childTypeL3SampleTypes.size());
        for (SpecimenTypeWrapper type : selectedSampleTypes) {
            Assert.assertTrue(childTypeL3SampleTypes.contains(type));
        }

        childTypeL3.removeFromSpecimenTypeCollection(childTypeL3
            .getSpecimenTypeCollection());
        childTypeL3SampleTypes = childTypeL3.getSpecimenTypeCollection();
        Assert.assertTrue((childTypeL3SampleTypes == null)
            || (childTypeL3SampleTypes.size() == 0));
    }

    @Test
    public void testAddSampleTypes() throws Exception {
        addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        ContainerTypeWrapper childTypeL3 = containerTypeMap.get("ChildCtL3");

        List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        List<SpecimenTypeWrapper> selectedSampleTypes = TestCommon
            .getRandomSampleTypeList(r, allSampleTypes);

        childTypeL3.addToSpecimenTypeCollection(selectedSampleTypes);
        childTypeL3.persist();
        childTypeL3.reload();
        List<SpecimenTypeWrapper> childTypeL3SampleTypes = childTypeL3
            .getSpecimenTypeCollection(false);
        Assert.assertEquals(selectedSampleTypes.size(),
            childTypeL3SampleTypes.size());
        for (SpecimenTypeWrapper type : selectedSampleTypes) {
            Assert.assertTrue(childTypeL3SampleTypes.contains(type));
        }

        childTypeL3.removeFromSpecimenTypeCollection(childTypeL3
            .getSpecimenTypeCollection());
        childTypeL3SampleTypes = childTypeL3.getSpecimenTypeCollection();
        Assert.assertTrue((childTypeL3SampleTypes == null)
            || (childTypeL3SampleTypes.size() == 0));
    }

    @Test
    public void testRemoveSpecimenTypes() throws Exception {
        addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        ContainerTypeWrapper childTypeL3 = containerTypeMap.get("ChildCtL3");

        List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        List<SpecimenTypeWrapper> selectedSampleTypes = TestCommon
            .getRandomSampleTypeList(r, allSampleTypes);

        childTypeL3.addToSpecimenTypeCollection(selectedSampleTypes);
        childTypeL3.persist();
        childTypeL3.reload();

        // add containers
        ContainerWrapper top = ContainerHelper.addContainer("01",
            TestCommon.getNewBarcode(r), site, containerTypeMap.get("TopCT"));
        ContainerWrapper cont1 = ContainerHelper.addContainer(null,
            TestCommon.getNewBarcode(r), top, site,
            containerTypeMap.get("ChildCtL1"), 0, 0);
        ContainerWrapper cont2 = ContainerHelper.addContainer(null,
            TestCommon.getNewBarcode(r), cont1, site,
            containerTypeMap.get("ChildCtL2"), 0, 0);
        ContainerWrapper cont3 = ContainerHelper.addContainer(null,
            TestCommon.getNewBarcode(r), cont2, site,
            containerTypeMap.get("ChildCtL3"), 0, 0);

        SpecimenWrapper parentSpc = SpecimenHelper.addParentSpecimen();

        List<SpecimenWrapper> spcs = SpecimenHelper.addSpecimens(parentSpc,
            cont3, 0, 0, 2, selectedSampleTypes);

        childTypeL3.removeFromSpecimenTypeCollection(Arrays.asList(spcs.get(1)
            .getSpecimenType()));
        try {
            childTypeL3.persist();
            Assert
                .fail("Cannot remove a sample type if one container of this type contains this sample type");
        } catch (BiobankSessionException bce) {
            Assert.assertTrue(true);
        }

        // remove specimens and try and remove specimen type again
        for (SpecimenWrapper spc : spcs) {
            spc.delete();
        }

        childTypeL3.removeFromSpecimenTypeCollection(Arrays
            .asList(selectedSampleTypes.get(1)));
        childTypeL3.persist();
    }

    @Test
    public void testGetSpecimenTypesRecursively() throws Exception {
        ContainerTypeWrapper topType, childTypeL3;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        childTypeL3 = containerTypeMap.get("ChildCtL3");
        Collection<SpecimenTypeWrapper> collection = topType
            .getSpecimenTypesRecursively();
        Assert.assertEquals(0, collection.size());

        List<SpecimenTypeWrapper> allSampleTypes = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        List<SpecimenTypeWrapper> selectedSampleTypes = TestCommon
            .getRandomSampleTypeList(r, allSampleTypes);

        childTypeL3 = TestCommon.addSampleTypes(childTypeL3,
            selectedSampleTypes);
        childTypeL3.addToSpecimenTypeCollection(selectedSampleTypes);
        childTypeL3.persist();
        topType.reload();
        collection = topType.getSpecimenTypesRecursively();
        Assert.assertEquals(selectedSampleTypes.size(), collection.size());
        for (SpecimenTypeWrapper type : selectedSampleTypes) {
            Assert.assertTrue(collection.contains(type));
        }

        childTypeL3.removeFromSpecimenTypeCollection(childTypeL3
            .getSpecimenTypeCollection());
        childTypeL3.persist();
        topType.reload();
        collection = topType.getSpecimenTypesRecursively();
        Assert.assertTrue((collection == null) || (collection.size() == 0));
    }

    @Test
    public void testAddRemoveChildContainerTypes() throws Exception {
        ContainerTypeWrapper topType, childType1, childType2, childType3, childType3_2;

        topType = containerTypeMap.get("TopCT");

        childType3 = ContainerTypeHelper.addContainerType(site,
            "Child L3 Container Type", "CCTL3", 1, CONTAINER_CHILD_L3_ROWS,
            CONTAINER_CHILD_L3_COLS, false);

        // add childType3
        childType2 = ContainerTypeHelper.newContainerType(site,
            "Child L2 Container Type", "CCTL2", 3, 1, 10, false);
        childType2.addToChildContainerTypeCollection(Arrays.asList(childType3));
        childType2.persist();
        childType2.reload();
        Assert.assertEquals(1, childType2.getChildContainerTypeCollection()
            .size());

        // now add childType3_2
        childType3_2 = ContainerTypeHelper.addContainerType(site,
            "Child L3_2 Container Type", "CCTL3_2", 1,
            CONTAINER_CHILD_L3_ROWS - 1, CONTAINER_CHILD_L3_COLS - 1, false);
        childType2.addToChildContainerTypeCollection(Arrays
            .asList(childType3_2));
        childType2.persist();
        childType2.reload();
        Assert.assertEquals(2, childType2.getChildContainerTypeCollection()
            .size());

        // now remove childType3_2
        childType2.removeFromChildContainerTypeCollection(Arrays
            .asList(childType3_2));
        childType2.persist();
        childType2.reload();
        Assert.assertEquals(1, childType2.getChildContainerTypeCollection()
            .size());

        childType1 = ContainerTypeHelper.newContainerType(site,
            "Child L1 Container Type", "CCTL1", 3, 1, 10, false);
        childType1.addToChildContainerTypeCollection(Arrays.asList(childType2));
        childType1.persist();
        childType1.reload();
        Assert.assertEquals(1, childType1.getChildContainerTypeCollection()
            .size());

        topType.addToChildContainerTypeCollection(Arrays.asList(childType1));
        topType.persist();
        topType.reload();
        Assert
            .assertEquals(1, topType.getChildContainerTypeCollection().size());

        // now add childType1_2 and childType1_3 to topType and add a containers
        ContainerTypeWrapper childType1_2, childType1_3;
        childType1_2 = ContainerTypeHelper.addContainerType(site,
            "Child L1_2 Container Type", "CCTL1_2", 1, 3, 10, false);
        childType1_3 = ContainerTypeHelper.addContainerType(site,
            "Child L1_3 Container Type", "CCTL1_3", 1, 2, 18, false);
        topType.addToChildContainerTypeCollection(Arrays.asList(childType1_2,
            childType1_3));
        topType.persist();
        topType.reload();
        Assert
            .assertEquals(3, topType.getChildContainerTypeCollection().size());

        ContainerWrapper top = ContainerHelper.addContainer("01",
            TestCommon.getNewBarcode(r), site, containerTypeMap.get("TopCT"));
        top.addChild(0, 0, ContainerHelper.newContainer(null,
            TestCommon.getNewBarcode(r), site, childType1));
        top.addChild(0, 1, ContainerHelper.newContainer(null,
            TestCommon.getNewBarcode(r), site, childType1_2));
        top.addChild(0, 2, ContainerHelper.newContainer(null,
            TestCommon.getNewBarcode(r), site, childType1_3));
        top.persist();
        top.reload();

        // now attempt to remove childType1_2 and childType1_3
        topType.removeFromChildContainerTypeCollection(Arrays.asList(
            childType1_2, childType1_3));
        try {
            topType.persist();
            Assert.fail("cannot remove used child container types");
        } catch (BiobankSessionException e) {
            Assert.assertTrue(true);
        }

        // remove the container child and then remove types
        top.deleteChildrenWithType(null, null);
        // type has not been reseted, so the removed types are still there.
        topType.persist();
        Assert
            .assertEquals(1, topType.getChildContainerTypeCollection().size());
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
        childTypeL2_2 = ContainerTypeHelper.addContainerType(site,
            "Child L2 Container Type 2", "CCTL2_2", 1, 4, 4, false);
        childTypeL1.addToChildContainerTypeCollection(Arrays.asList(
            childTypeL2, childTypeL2_2));
        childTypeL1.persist();

        list = childTypeL1.getChildContainerTypeCollection();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_2));

        // add a third child to childTypeL1
        childTypeL2_3 = ContainerTypeHelper.addContainerType(site,
            "Child L2 Container Type 3", "CCTL2_3", 1, 5, 7, false);
        childTypeL1.addToChildContainerTypeCollection(Arrays.asList(
            childTypeL2, childTypeL2_2, childTypeL2_3));
        childTypeL1.persist();

        list = childTypeL1.getChildContainerTypeCollection();
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_2));
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2_2
        childTypeL2_2.delete();

        // test childTypeL1's children again
        childTypeL1.reload();
        list = childTypeL1.getChildContainerTypeCollection();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2
        childTypeL2.delete();

        // test childTypeL3's parents again
        childTypeL1.reload();
        list = childTypeL1.getChildContainerTypeCollection();
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(childTypeL2_3));

        // now delete childTypeL2_3
        childTypeL2_3.delete();
        childTypeL1.reload();
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

        Assert.assertEquals(2, topType.getChildLabelingSchemeId().intValue());
        Assert.assertTrue(topType.getChildLabelingSchemeName().equals(
            "CBSR 2 char alphabetic"));

        Assert.assertEquals(3, childTypeL1.getChildLabelingSchemeId()
            .intValue());
        Assert.assertTrue(childTypeL1.getChildLabelingSchemeName().equals(
            "2 char numeric"));

        Assert.assertEquals(3, childTypeL2.getChildLabelingSchemeId()
            .intValue());
        Assert.assertTrue(childTypeL2.getChildLabelingSchemeName().equals(
            "2 char numeric"));

        Assert.assertEquals(1, childTypeL3.getChildLabelingSchemeId()
            .intValue());
        Assert.assertTrue(childTypeL3.getChildLabelingSchemeName().equals(
            "SBS Standard"));
    }

    @Test
    public void testActivityStatus() throws Exception {
        ContainerTypeWrapper topType;

        // its important that topType2 is not saved to the database
        topType = ContainerTypeHelper.newContainerType(site,
            "Top Container Type 2", "TCT2", 2, CONTAINER_TOP_ROWS,
            CONTAINER_TOP_COLS, true);
        topType.setActivityStatus(null);

        try {
            topType.persist();
            Assert.fail("Should not be allowed: no activity status");
        } catch (ValueNotSetException bce) {
            Assert.assertTrue(true);
        }

        topType.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, ActivityStatusWrapper.ACTIVE_STATUS_STRING));
        topType.persist();
    }

    @Test
    public void testGetTopContainerTypesInSite() throws Exception {
        ContainerTypeWrapper topType, topType2, childType;

        topType = containerTypeMap.get("TopCT");

        topType2 = ContainerTypeHelper.addContainerType(site,
            "Top Container Type 2", "TCT 2", 2, CONTAINER_TOP_ROWS - 1,
            CONTAINER_TOP_COLS + 1, true);

        childType = ContainerTypeHelper.addContainerType(site,
            "Child L1 Container Type", "CCTL1", 3, 1, 10, false);

        topType.addToChildContainerTypeCollection(Arrays.asList(childType));
        topType.persist();
        topType.reload();

        List<ContainerTypeWrapper> list = ContainerTypeWrapper
            .getTopContainerTypesInSite(appService, site);
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(topType));
        Assert.assertTrue(list.contains(topType2));
        Assert.assertFalse(list.contains(childType));
    }

    @Test
    public void testGetContainerTypesInSite() throws Exception {
        ContainerTypeWrapper topType, childTypeL1, childTypeL2, childTypeL3, childTypeL2_2;

        topType = addContainerTypeHierarchy(containerTypeMap.get("TopCT"));
        childTypeL1 = containerTypeMap.get("ChildCtL1");
        childTypeL2 = containerTypeMap.get("ChildCtL2");
        childTypeL3 = containerTypeMap.get("ChildCtL3");

        // add a second child to childTypeL1
        childTypeL2_2 = ContainerTypeHelper.addContainerType(site,
            "Child L2 Container Type 2", "CCTL2_2", 1, 4, 4, false);
        childTypeL1.addToChildContainerTypeCollection(Arrays.asList(
            childTypeL2, childTypeL2_2));
        childTypeL1.persist();

        List<ContainerTypeWrapper> list = ContainerTypeWrapper
            .getContainerTypesInSite(appService, site, "Container", false);
        Assert.assertEquals(5, list.size());
        Assert.assertTrue(list.contains(topType));
        Assert.assertTrue(list.contains(childTypeL1));
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_2));
        Assert.assertTrue(list.contains(childTypeL3));

        list = ContainerTypeWrapper.getContainerTypesInSite(appService, site,
            "Top Container Type", true);
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(list.contains(topType));

        childTypeL3.delete();

        list = ContainerTypeWrapper.getContainerTypesInSite(appService, site,
            "Container", false);
        Assert.assertEquals(4, list.size());
        Assert.assertTrue(list.contains(topType));
        Assert.assertTrue(list.contains(childTypeL1));
        Assert.assertTrue(list.contains(childTypeL2));
        Assert.assertTrue(list.contains(childTypeL2_2));
    }

    @Test
    public void testGetAllLabelingSchemes() throws ApplicationException {
        Map<Integer, ContainerLabelingSchemeWrapper> map = ContainerLabelingSchemeWrapper
            .getAllLabelingSchemesMap(appService);

        Assert.assertEquals(6, map.size());
    }

    @Test
    public void testGetContainersCount() throws Exception {
        ContainerTypeWrapper topType = addContainerTypeHierarchy(containerTypeMap
            .get("TopCT"));
        ContainerTypeWrapper childTypeL1 = containerTypeMap.get("ChildCtL1");

        ContainerWrapper top = ContainerHelper.addContainer("01", "01", site,
            topType);
        ContainerHelper.addContainer(null, "1stChild", top, site, childTypeL1,
            0, 0);
        ContainerHelper.addContainer(null, "2ndChild", top, site, childTypeL1,
            0, 1);
        ContainerHelper.addContainer(null, "3rdChild", top, site, childTypeL1,
            0, 2);

        topType.reload();
        childTypeL1.reload();
        Assert.assertEquals(1, topType.getContainersCount());
        Assert.assertEquals(3, childTypeL1.getContainersCount());
    }

    @Test(expected = Exception.class)
    public void testSetChildLabelingSchemeById() throws Exception {
        ContainerTypeWrapper type = new ContainerTypeWrapper(appService);
        type.setChildLabelingSchemeById(-1);
    }

    @Test(expected = Exception.class)
    public void testSetChildLabelingSchemeByNameMissing() throws Exception {
        ContainerTypeWrapper type = new ContainerTypeWrapper(appService);
        type.setChildLabelingSchemeName(null);
    }

    @Test
    public void testSetChildLabelingSchemeByName() throws Exception {
        ContainerTypeWrapper type = new ContainerTypeWrapper(appService);

        for (ContainerLabelingSchemeWrapper scheme : ContainerLabelingSchemeWrapper
            .getAllLabelingSchemesMap(appService).values()) {
            type.setChildLabelingSchemeName(scheme.getName());
        }
    }

    @Test
    public void testGetContainerTypesByCapacity() throws BiobankCheckException,
        Exception {
        ContainerTypeWrapper topType = containerTypeMap.get("TopCT");

        ContainerTypeWrapper childType = ContainerTypeHelper.addContainerType(
            site, "Some Pallet 96 Thing", "SP96T", 1,
            RowColPos.PALLET_96_ROW_MAX, RowColPos.PALLET_96_COL_MAX, false);

        SpecimenTypeWrapper specimenType = SpecimenTypeHelper
            .addSpecimenType("asdf");

        childType.addToSpecimenTypeCollection(Arrays.asList(specimenType));
        childType.persist();

        topType.addToChildContainerTypeCollection(Arrays.asList(childType));
        topType.persist();

        List<ContainerTypeWrapper> types = ContainerTypeWrapper
            .getContainerTypesByCapacity(appService, topType.getSite(),
                childType.getRowCapacity(), childType.getColCapacity());

        Assert.assertTrue(types.contains(childType));
    }

    @Test
    public void testGetContainerTypesPallet96() throws BiobankCheckException,
        Exception {
        ContainerTypeWrapper topType = containerTypeMap.get("TopCT");

        ContainerTypeWrapper childType = ContainerTypeHelper.addContainerType(
            site, "Some Pallet 96 Thing", "SP96T", 1,
            RowColPos.PALLET_96_ROW_MAX, RowColPos.PALLET_96_COL_MAX, false);

        SpecimenTypeWrapper specimenType = SpecimenTypeHelper
            .addSpecimenType("asdf");

        childType.addToSpecimenTypeCollection(Arrays.asList(specimenType));
        childType.persist();

        topType.addToChildContainerTypeCollection(Arrays.asList(childType));
        topType.persist();

        Assert.assertTrue(childType.isPallet96());

        List<ContainerTypeWrapper> pallet96s = ContainerTypeWrapper
            .getContainerTypesPallet96(appService, site);
        Assert.assertTrue(pallet96s.contains(childType));
        Assert.assertTrue(!pallet96s.contains(topType));
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

        ContainerTypeWrapper2 newType = new ContainerTypeWrapper2(appService);
        Assert.assertTrue(topType.compareTo(newType) == 0);
    }

    private static final class ContainerTypeWrapper2 extends
        ModelWrapper<ContainerType> {

        public ContainerTypeWrapper2(WritableApplicationService appService) {
            super(appService);
        }

        @Override
        public Property<Integer, ? super ContainerType> getIdProperty() {
            return ContainerTypePeer.ID;
        }

        @Override
        protected List<Property<?, ? super ContainerType>> getProperties() {
            return new ArrayList<Property<?, ? super ContainerType>>();
        }

        @Override
        public Class<ContainerType> getWrappedClass() {
            return ContainerType.class;
        }

    }
}
