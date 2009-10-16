package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.Position;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Container;

public class TestContainer extends TestDatabase {
    private SiteWrapper site;
    private Map<String, ContainerTypeWrapper> containerTypeMap;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        List<SiteWrapper> sites = SiteWrapper.getAllSites(appService);
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();

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
    }

    private void deleteContainers() throws Exception {
        List<ContainerWrapper> containerList = site.getContainerCollection();
        if (containerList != null) {
            for (ContainerWrapper container : containerList) {
                container.delete();
            }
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
        childType.setName("Child Container Type");
        childType.setNameShort("CCT");
        childType.setRowCapacity(1);
        childType.setColCapacity(10);
        childType.setTopLevel(false);
        childType.persist();
        containerTypeMap.put("Child", childType);

        topType = new ContainerTypeWrapper(appService);
        topType.setSite(site);
        topType.setName("Top Container Type");
        topType.setNameShort("TCT");
        // topType.setChildLabelingScheme(scheme);
        topType.setRowCapacity(5);
        topType.setColCapacity(9);
        topType.setTopLevel(true);
        topType
            .setChildContainerTypeCollection(new ArrayList<ContainerTypeWrapper>(
                containerTypeMap.values()));
        topType.persist();
        containerTypeMap.put("Top", topType);
    }

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        ContainerWrapper container = new ContainerWrapper(appService);
        container.setSite(site);
        container.setContainerType(containerTypeMap.get("Top"));
        container.persist();
        testGettersAndSetters(container);
    }

    @Test
    public void createValidContainer() throws Exception {
        ContainerWrapper container = new ContainerWrapper(appService);
        container.setLabel("05");
        container.setContainerType(containerTypeMap.get("Top"));
        container.setSite(site);
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
        ContainerWrapper container = new ContainerWrapper(appService);
        container.setLabel("05");
        container.setContainerType(containerTypeMap.get("Top"));
        container.persist();
    }

    @Test
    public void testLabelUnique() throws Exception {
        ContainerWrapper container1, container2;
        container1 = new ContainerWrapper(appService);
        container1.setLabel("05");
        container1.setContainerType(containerTypeMap.get("Top"));

        try {
            container1.persist();
        } catch (Exception e) {
            Assert.fail("adding first container failed");
        }

        container2 = new ContainerWrapper(appService);
        container2.setLabel("05");
        container2.setContainerType(containerTypeMap.get("Top"));

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

        container1 = new ContainerWrapper(appService);
        container1.setLabel("05");
        container1.setProductBarcode("abcdef");
        container1.setContainerType(containerTypeMap.get("Top"));

        try {
            container1.persist();
        } catch (Exception e) {
            Assert.fail("adding first container failed");
        }

        container2 = new ContainerWrapper(appService);
        container2.setLabel("06");
        container2.setProductBarcode("abcdef");
        container2.setContainerType(containerTypeMap.get("Top"));

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
    public void testUniquePosition() throws Exception {
        ContainerWrapper container1, container2;

        container1 = new ContainerWrapper(appService);
        container1.setLabel("05");
        container1.setProductBarcode("abcdef");
        container1.setContainerType(containerTypeMap.get("Top"));
        container1.setPosition(new Position(0, 0));

        try {
            container1.persist();
        } catch (Exception e) {
            Assert.fail("adding first container failed");
        }

        container2 = new ContainerWrapper(appService);
        container2.setLabel("06");
        container2.setProductBarcode("uvwxyz");
        container2.setContainerType(containerTypeMap.get("Top"));
        container2.setPosition(new Position(0, 0));

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

        container = new ContainerWrapper(appService);
        container.setLabel("06");
        container.setProductBarcode("uvwxyz");
        container.setContainerType(containerTypeMap.get("Top"));
        container.setPosition(new Position(0, 0));
        container.persist();
        container.reset();
    }

    @Test
    public void testReload() throws Exception {
        ContainerWrapper container;

        container = new ContainerWrapper(appService);
        container.setLabel("06");
        container.setProductBarcode("uvwxyz");
        container.setContainerType(containerTypeMap.get("Top"));
        container.setPosition(new Position(0, 0));
        container.persist();
        container.reload();
    }

    @Test(expected = BiobankCheckException.class)
    public void testSetPositionOnTopLevel() throws Exception {
        ContainerWrapper container;

        container = new ContainerWrapper(appService);
        container.setLabel("01");
        container.setProductBarcode("uvwxyz");
        container.setPosition(new Position(0, 0));
        container.persist();
    }

    @Test
    public void testSetPositionOnChild() throws Exception {
        ContainerWrapper top, child;

        top = new ContainerWrapper(appService);
        top.setLabel("01");
        top.setProductBarcode("uvwxyz");
        top.setPosition(new Position(0, 0));
        top.persist();

        child = new ContainerWrapper(appService);
        child.setLabel("01");
        child.setProductBarcode("uvwxyz");
        child.setPosition(new Position(0, 0));
        child.setParent(top);
        child.persist();
    }

    @Test(expected = BiobankCheckException.class)
    public void testSetInvalidPositionOnChild() throws Exception {
        ContainerWrapper top, child;

        top = new ContainerWrapper(appService);
        top.setLabel("01");
        top.setProductBarcode("uvwxyz");
        top.setPosition(new Position(0, 0));
        top.persist();

        child = new ContainerWrapper(appService);
        child.setLabel("01");
        child.setProductBarcode("uvwxyz");
        child.setPosition(new Position(100, 100));
        child.setParent(top);
        child.persist();
    }

    @Test
    public void testGetChildContainer() throws Exception {

    }
}
