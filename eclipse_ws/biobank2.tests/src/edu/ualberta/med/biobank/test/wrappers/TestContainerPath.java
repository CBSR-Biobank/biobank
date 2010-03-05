package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerPathWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.ContainerPath;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;

public class TestContainerPath extends TestDatabase {

    @Test
    public void testGetWrappedClass() throws Exception {
        ContainerPathWrapper path = new ContainerPathWrapper(appService);
        Assert.assertEquals(ContainerPath.class, path.getWrappedClass());
    }

    @Test
    public void testSetNullContainer() throws Exception {
        ContainerPathWrapper path = new ContainerPathWrapper(appService);
        path.setContainer(null);
        try {
            path.persist();
            Assert
                .fail("should not be allowed to add path with null container");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testContainerNotInDb() throws Exception {
        String name = "testSetPath" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ContainerTypeWrapper topType, childType;
        ContainerWrapper top, child;

        childType = ContainerTypeHelper.addContainerType(site, name
            + "_childType", name + "_childType", 3, 4, 9, false);
        topType = ContainerTypeHelper.addContainerType(site, name + "_topType",
            name + "_topType", 1, 5, 5, true);
        topType.addChildContainerTypes(Arrays.asList(childType));
        topType.persist();
        topType.reload();

        top = ContainerHelper.addContainer("01", null, null, site, topType);
        child = ContainerHelper.newContainer(null, null, top, site, childType,
            0, 0);
        ContainerPathWrapper path = new ContainerPathWrapper(appService);
        path.setContainer(child);
        try {
            path.persist();
            Assert
                .fail("should not be allowed to add path with container not in database");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        path = ContainerPathWrapper.getContainerPath(appService, child);
        Assert.assertNull(path);
    }

    @Test
    public void testPathAlreadyInDb() throws Exception {
        String name = "testSetPath" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ContainerTypeWrapper topType, childType;
        ContainerWrapper top, child;

        childType = ContainerTypeHelper.addContainerType(site, name
            + "_childType", name + "_childType", 3, 4, 9, false);
        topType = ContainerTypeHelper.addContainerType(site, name + "_topType",
            name + "_topType", 1, 5, 5, true);
        topType.addChildContainerTypes(Arrays.asList(childType));
        topType.persist();
        topType.reload();

        top = ContainerHelper.addContainer("01", null, null, site, topType);
        child = ContainerHelper.addContainer(null, null, top, site, childType,
            0, 0);
        ContainerPathWrapper path = new ContainerPathWrapper(appService);
        path.setContainer(child);
        try {
            path.persist();
            Assert.fail("should not be allowed to add path more than once");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetPath() throws Exception {
        String name = "testSetPath" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        ContainerTypeWrapper topType, childType;
        ContainerWrapper top, child;

        childType = ContainerTypeHelper.addContainerType(site, name
            + "_childType", name + "_childType", 3, 4, 9, false);
        topType = ContainerTypeHelper.addContainerType(site, name + "_topType",
            name + "_topType", 1, 5, 5, true);
        topType.addChildContainerTypes(Arrays.asList(childType));
        topType.persist();
        topType.reload();

        top = ContainerHelper.addContainer("01", null, null, site, topType);
        child = ContainerHelper.addContainer(null, null, top, site, childType,
            0, 0);
        ContainerPathWrapper path = ContainerPathWrapper.getContainerPath(
            appService, child);
        String expectedPath = top.getId() + "/" + child.getId();
        Assert.assertEquals(expectedPath, path.getPath());
    }
}
