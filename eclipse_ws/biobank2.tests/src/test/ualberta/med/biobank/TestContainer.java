package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Container;

public class TestContainer extends TestDatabase {
    private SiteWrapper site;
    private Map<String, ContainerTypeWrapper> containerTypeMap;
    private Map<String, ContainerWrapper> containerMap;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        List<SiteWrapper> sites = SiteWrapper.getAllSites(appService);
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();
        containerMap = new HashMap<String, ContainerWrapper>();

        if (sites.size() > 0) {
            site = sites.get(0);
        } else {
            site = new SiteWrapper(appService);
            site.setName("Site - Container Test");
            site.setStreet1("street");
            site.persist();
        }

        deleteContainers();
        deleteContainerTypes();
        addContainerTypes();
        addContainers();
    }

    @After
    public void tearDown() throws Exception {
        deleteContainers();
        deleteContainerTypes();
    }

    private void deleteContainers() throws Exception {
        site.reload();
        while (true) {
            List<ContainerWrapper> containerList = site
                .getContainerCollection();
            if (containerList != null) {
                for (ContainerWrapper container : containerList) {
                    if (container.getChildren().size() == 0) {
                        container.delete();
                    }
                }
            }
            site.reload();
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

        childType = new ContainerTypeWrapper(appService);
        childType.setSite(site);
        childType.setName("Child L3 Container Type");
        childType.setNameShort("CCTL3");
        childType.setChildLabelingScheme(4);
        childType.setRowCapacity(1);
        childType.setColCapacity(10);
        childType.setTopLevel(false);
        childType.persist();
        containerTypeMap.put("ChildCtL3", childType);

        childType = new ContainerTypeWrapper(appService);
        childType.setSite(site);
        childType.setName("Child L2 Container Type");
        childType.setNameShort("CCTL2");
        childType.setChildLabelingScheme(1);
        childType.setRowCapacity(1);
        childType.setColCapacity(10);
        childType.setTopLevel(false);
        childType
            .setChildContainerTypeCollection(new ArrayList<ContainerTypeWrapper>(
                Arrays.asList(containerTypeMap.get("ChildCtL3"))));
        childType.persist();
        containerTypeMap.put("ChildCtL2", childType);

        childType = new ContainerTypeWrapper(appService);
        childType.setSite(site);
        childType.setName("Child L1 Container Type");
        childType.setNameShort("CCTL1");
        childType.setChildLabelingScheme(3);
        childType.setRowCapacity(1);
        childType.setColCapacity(10);
        childType.setTopLevel(false);
        childType
            .setChildContainerTypeCollection(new ArrayList<ContainerTypeWrapper>(
                Arrays.asList(containerTypeMap.get("ChildCtL2"))));
        childType.persist();
        containerTypeMap.put("ChildCtL1", childType);

        topType = new ContainerTypeWrapper(appService);
        topType.setSite(site);
        topType.setName("Top Container Type");
        topType.setNameShort("TCT");
        topType.setChildLabelingScheme(2);
        topType.setRowCapacity(5);
        topType.setColCapacity(9);
        topType.setTopLevel(true);
        topType
            .setChildContainerTypeCollection(new ArrayList<ContainerTypeWrapper>(
                Arrays.asList(containerTypeMap.get("ChildCtL1"))));
        topType.persist();
        containerTypeMap.put("TopCT", topType);
    }

    private void addContainers() throws BiobankCheckException, Exception {
        ContainerWrapper top = createContainer("01", "barcode1", site,
            containerTypeMap.get("TopCT"));
        top.persist();
        containerMap.put("Top", top);
    }

    private ContainerWrapper createContainer(String label, String barcode,
        SiteWrapper site, ContainerTypeWrapper type) throws Exception {
        ContainerWrapper container;

        container = new ContainerWrapper(appService);
        container.setLabel(label);
        container.setProductBarcode(barcode);
        if (site != null) {
            container.setSite(site);
        }
        container.setContainerType(type);
        return container;
    }

    private ContainerWrapper createContainer(String label, String barcode,
        SiteWrapper site, ContainerTypeWrapper type, Integer row, Integer col)
        throws Exception {
        ContainerWrapper container = createContainer(label, barcode, site, type);
        container.setPosition(row, col);
        return container;
    }

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        ContainerWrapper container = createContainer(null, null, site,
            containerTypeMap.get("TopCT"));
        container.persist();
        testGettersAndSetters(container);
    }

    @Test
    public void createValidContainer() throws Exception {
        ContainerWrapper container = createContainer("05", null, site,
            containerTypeMap.get("TopCT"));
        container.persist();

        Integer id = container.getId();
        Assert.assertNotNull(id);
        Container containerInDB = ModelUtils.getObjectWithId(appService,
            Container.class, id);
        Assert.assertNotNull(containerInDB);
        container.delete();
    }

    @Test(expected = BiobankCheckException.class)
    public void createNoSite() throws Exception {
        ContainerWrapper container = createContainer("05", null, null,
            containerTypeMap.get("TopCT"));
        container.persist();
    }

    @Test
    public void testLabelUnique() throws Exception {
        ContainerWrapper container1, container2;
        container1 = createContainer("05", null, site, containerTypeMap
            .get("TopCT"));

        try {
            container1.persist();
        } catch (Exception e) {
            Assert.fail("adding first container failed" + e);
        }

        container2 = createContainer("05", null, site, containerTypeMap
            .get("TopCT"));

        try {
            container2.persist();
            Assert
                .fail("should not be allowed to add container because of duplicate label");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        container1.delete();
    }

    @Test
    public void testProductBarcodeUnique() throws Exception {
        ContainerWrapper container1, container2;

        container1 = createContainer("05", "abcdef", site, containerTypeMap
            .get("TopCT"));

        try {
            container1.persist();
        } catch (Exception e) {
            Assert.fail("adding first container failed");
        }

        container2 = createContainer("06", "abcdef", site, containerTypeMap
            .get("TopCT"));

        try {
            container2.persist();
            Assert
                .fail("should not be allowed to add container because of duplicate product barcode");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        container1.delete();
    }

    @Test
    public void testReset() throws Exception {
        ContainerWrapper container;

        container = createContainer("06", "uvwxyz", site, containerTypeMap
            .get("TopCT"));
        container.persist();
        container.reset();
    }

    @Test
    public void testReload() throws Exception {
        ContainerWrapper container;

        container = createContainer("06", "uvwxyz", site, containerTypeMap
            .get("TopCT"));
        container.persist();
        container.reload();
    }

    @Test(expected = BiobankCheckException.class)
    public void testSetPositionOnTopLevel() throws Exception {
        ContainerWrapper container;

        container = createContainer("01", "uvwxyz", site, containerTypeMap
            .get("TopCT"), 0, 0);
        container.persist();
        container.delete();
    }

    @Test
    public void testSetPositionOnChild() throws Exception {
        ContainerWrapper child;

        child = createContainer("AA", "uvwxyz", site, containerTypeMap
            .get("ChildCtL1"), 0, 0);
        child.setParent(containerMap.get("Top"));
        child.persist();
        child.delete();
    }

    @Test
    public void testSetInvalidPositionOnChild() throws Exception {
        ContainerWrapper top, child;

        top = containerMap.get("Top");

        child = createContainer("AA", "uvwxyz", site, containerTypeMap
            .get("ChildCtL1"));
        child.setParent(top);

        try {
            child.setPosition(top.getRowCapacity(), top.getColCapacity());
            child.persist();
            Assert.fail("should not be allowed to set an invalid position");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        try {
            child.setPosition(top.getRowCapacity() + 1,
                top.getColCapacity() + 1);
            child.persist();
            Assert.fail("should not be allowed to set an invalid position");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        try {
            child.setPosition(-1, -1);
            child.persist();
            Assert.fail("should not be allowed to set an invalid position");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testUniquePosition() throws Exception {
        ContainerWrapper top, container1, container2;

        top = containerMap.get("Top");
        container1 = createContainer("AA", "uvwxyz", site, containerTypeMap
            .get("ChildCtL1"), 0, 0);
        container1.setParent(top);

        try {
            container1.persist();
        } catch (Exception e) {
            Assert.fail("adding first container failed");
        }

        container2 = createContainer("AB", "uvwxyz", site, containerTypeMap
            .get("ChildCtL1"), 0, 0);
        container2.setParent(top);

        try {
            container2.persist();
            Assert
                .fail("should not be allowed to add container because of duplicate product barcode");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        container1.delete();
    }

    @Test
    public void testLabelingSchemeL1() throws Exception {
        ContainerWrapper top, child;
        top = containerMap.get("Top");
        child = createContainer("01", "uvwxyz", site, containerTypeMap
            .get("ChildCtL1"), 0, 0);
        child.setParent(top);
        try {
            child.persist();
            Assert
                .fail("should not be allowed to set an invalid labeling scheme");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        child = createContainer("A1", "uvwxyz", site, containerTypeMap
            .get("ChildCtL1"), 0, 0);
        child.setParent(top);
        try {
            child.persist();
            Assert
                .fail("should not be allowed to set an invalid labeling scheme");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        child = createContainer("AA", "uvwxyz", site, containerTypeMap
            .get("ChildCtL1"), 0, 0);
        child.setParent(top);
        try {
            child.persist();
        } catch (BiobankCheckException e) {
            Assert.fail("failed adding L1 child with correct labeling scheme");
        }
        child.delete();
    }

    @Test
    public void testLabelingSchemeL2() throws Exception {
        ContainerWrapper top, childL1, childL2;
        top = containerMap.get("Top");
        childL1 = createContainer("AA", "uvwxyz", site, containerTypeMap
            .get("ChildCtL1"), 0, 0);
        childL1.setParent(top);
        try {
            childL1.persist();
        } catch (BiobankCheckException e) {
            Assert.fail("failed adding L1 child container");
        }

        childL2 = createContainer("A1", "0001", site, containerTypeMap
            .get("ChildCtL2"), 0, 0);
        childL2.setParent(childL1);
        try {
            childL2.persist();
            Assert
                .fail("should not be allowed to set an invalid labeling scheme");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        childL2 = createContainer("AA", "0001", site, containerTypeMap
            .get("ChildCtL2"), 0, 0);
        childL2.setParent(childL1);
        try {
            childL2.persist();
            Assert
                .fail("should not be allowed to set an invalid labeling scheme");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        childL2 = createContainer("01", "0001", site, containerTypeMap
            .get("ChildCtL2"), 0, 0);
        childL2.setParent(top);
        try {
            childL2.persist();
        } catch (BiobankCheckException e) {
            Assert.fail("failed adding L2 child with correct labeling scheme");
        }

        childL2.delete();
        childL1.delete();
    }

    @Test
    public void testLabelingSchemeL3() throws Exception {
        ContainerWrapper top, childL1, childL2, childL3;
        top = containerMap.get("Top");
        childL1 = createContainer("AA", "uvwxyz", site, containerTypeMap
            .get("ChildCtL1"), 0, 0);
        childL1.setParent(top);
        try {
            childL1.persist();
        } catch (BiobankCheckException e) {
            Assert.fail("failed adding L1 child container");
        }

        childL2 = createContainer("01", "0001", site, containerTypeMap
            .get("ChildCtL2"), 0, 0);
        childL2.setParent(top);
        try {
            childL2.persist();
        } catch (BiobankCheckException e) {
            Assert.fail("failed adding L2 child container");
        }

        childL3 = createContainer("01", "0002", site, containerTypeMap
            .get("ChildCtL3"), 0, 0);
        childL3.setParent(childL1);
        try {
            childL3.persist();
            Assert
                .fail("should not be allowed to set an invalid labeling scheme");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        childL3 = createContainer("AA", "0002", site, containerTypeMap
            .get("ChildCtL3"), 0, 0);
        childL3.setParent(childL1);
        try {
            childL3.persist();
            Assert
                .fail("should not be allowed to set an invalid labeling scheme");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        childL3 = createContainer("A1", "0002", site, containerTypeMap
            .get("ChildCtL3"), 0, 0);
        childL3.setParent(top);
        try {
            childL3.persist();
        } catch (BiobankCheckException e) {
            Assert.fail("failed adding L3 child with correct labeling scheme");
        }

        childL3.delete();
        childL2.delete();
        childL1.delete();
    }

    @Test
    public void testGetContainer() throws Exception {
        ContainerWrapper top, childL1, childL2, childL3, result;

        top = containerMap.get("Top");
        childL1 = createContainer("AA", "uvwxyz", site, containerTypeMap
            .get("ChildCtL1"), 0, 0);
        childL1.setParent(top);
        try {
            childL1.persist();
        } catch (BiobankCheckException e) {
            Assert.fail("failed adding L1 child container");
        }

        childL2 = createContainer("01", "0001", site, containerTypeMap
            .get("ChildCtL2"), 0, 0);
        childL2.setParent(top);
        try {
            childL2.persist();
        } catch (BiobankCheckException e) {
            Assert.fail("failed adding L2 child container");
        }

        childL3 = createContainer("A1", "0002", site, containerTypeMap
            .get("ChildCtL3"), 0, 0);
        childL3.setParent(top);
        try {
            childL3.persist();
        } catch (BiobankCheckException e) {
            Assert.fail("failed adding L3 child with correct labeling scheme");
        }

        result = top.getContainer("01AA", containerTypeMap.get("ChildCtL1"));
        Assert.assertEquals(childL1, result);

        result = top.getContainer("01AA01", containerTypeMap.get("ChildCtL2"));
        Assert.assertEquals(childL2, result);

        result = top
            .getContainer("01AA01A1", containerTypeMap.get("ChildCtL3"));
        Assert.assertEquals(childL3, result);

        childL3.delete();
        childL2.delete();
        childL1.delete();
    }
}
