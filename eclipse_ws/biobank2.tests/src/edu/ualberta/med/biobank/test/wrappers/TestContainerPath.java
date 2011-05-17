package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ContainerPathWrapper;
import edu.ualberta.med.biobank.model.ContainerPath;
import edu.ualberta.med.biobank.test.TestDatabase;

public class TestContainerPath extends TestDatabase {

    @Test
    public void testOrphanedContainer() throws Exception {
        Assert.fail("need fixes to container wrapper for this test to work");
        // FIXME: Commenting out for now
        // String name = "testOrphanedContainer";
        // SiteWrapper site;
        // ContainerTypeWrapper topType, childType;
        // ContainerWrapper c1, c2;
        //
        // site = SiteHelper.addSite(name);
        //
        // topType = ContainerTypeHelper.addContainerType(site, name + "Top",
        // name
        // + "Top", 1, 5, 5, true);
        // childType = ContainerTypeHelper.addContainerType(site, name +
        // "Child",
        // name + "Child", 1, 5, 5, false);
        //
        // topType.addToChildContainerTypeCollection(Arrays.asList(childType));
        // topType.persist();
        // topType.reload();
        //
        // childType.addToChildContainerTypeCollection(Arrays.asList(childType));
        // childType.persist();
        // childType.reload();
        //
        // c1 = ContainerHelper.addContainer("1", null, null, site, topType);
        // c2 = ContainerHelper.addContainer("2", null, c1, site, childType, 0,
        // 0);
        //
        // c1.persist();
        // c1.reload();
        // c2.persist();
        // c2.reload();
        //
        // try {
        // c2.setParent(null);
        // c2.setPosition(null);
        // c2.setContainerType(topType);
        // c1.deleteChildrenWithType(null, null);
        //
        // c1.persist();
        // c2.persist();
        // c2.reload();
        // Assert.fail("Should not be able to orphan a container.");
        // } catch (Exception e) {
        // Assert.assertTrue(true);
        // }
    }

    @Test
    public void testContainerMoved() throws Exception {
        Assert.fail("need fixes to container wrapper for this test to work");
        // FIXME: Commenting out for now
        // String name = "testContainerMoved";
        // SiteWrapper site;
        // ContainerTypeWrapper topType, childType;
        // ContainerWrapper c1, c2, c3, c4;
        //
        // site = SiteHelper.addSite(name);
        //
        // topType = ContainerTypeHelper.addContainerType(site, name + "Top",
        // name
        // + "Top", 1, 5, 5, true);
        // childType = ContainerTypeHelper.addContainerType(site, name +
        // "Child",
        // name + "Child", 1, 5, 5, false);
        //
        // topType.addToChildContainerTypeCollection(Arrays.asList(childType));
        // topType.persist();
        // topType.reload();
        //
        // childType.addToChildContainerTypeCollection(Arrays.asList(childType));
        // childType.persist();
        // childType.reload();
        //
        // c1 = ContainerHelper.addContainer("1", null, null, site, topType);
        // c2 = ContainerHelper.addContainer("2", null, c1, site, childType, 0,
        // 0);
        // c3 = ContainerHelper.addContainer("3", null, c2, site, childType, 0,
        // 0);
        // c4 = ContainerHelper.addContainer("4", null, c3, site, childType, 0,
        // 0);
        //
        // String p4Before = c4.getPath();
        //
        // c2.setPositionAsRowCol(new RowColPos(1, 1));
        // c2.persist();
        // c4.reload();
        //
        // Assert.assertEquals(p4Before, c4.getPath());
        //
        // c3.setPositionAsRowCol(new RowColPos(1, 1));
        // c3.persist();
        // c3.reload();
        // c4.reload();
        // Assert.assertEquals(p4Before, c4.getPath());
        //
        // c1.reload();
        // Assert.assertEquals(1, c1.getChildren().size());
        //
        // c3.setParent(c1);
        // c3.setPositionAsRowCol(new RowColPos(0, 1));
        // c1.addChild(0, 1, c3);
        // c1.persist();
        // c3.persist();
        // c3.reload();
        // c4.persist();
        // c4.reload();
        // Assert.assertEquals(c1.getId() + "/" + c3.getId() + "/" + c4.getId(),
        // c4.getPath());
        //
        // c1.reload();
        // Assert.assertEquals(2, c1.getChildren().size());
        //
        // c4.setParent(c2);
        // c4.setPositionAsRowCol(new RowColPos(0, 1));
        // c4.persist();
        // c4.reload();
        // Assert.assertEquals(c1.getId() + "/" + c2.getId() + "/" + c4.getId(),
        // c4.getPath());
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        ContainerPathWrapper path = new ContainerPathWrapper(appService);
        Assert.assertEquals(ContainerPath.class, path.getWrappedClass());
    }

    @Test
    public void testSetNullContainer() throws Exception {
        Assert.fail("need fixes to container wrapper for this test to work");
        // FIXME: Commenting out for now
        // ContainerPathWrapper path = new ContainerPathWrapper(appService);
        // path.setContainer(null);
        // try {
        // path.persist();
        // Assert
        // .fail("should not be allowed to add path with null container");
        // } catch (BiobankCheckException e) {
        // Assert.assertTrue(true);
        // }
    }

    @Test
    public void testContainerNotInDb() throws Exception {
        Assert.fail("need fixes to container wrapper for this test to work");
        // FIXME: Commenting out for now
        // String name = "testSetPath" + r.nextInt();
        // SiteWrapper site = SiteHelper.addSite(name);
        //
        // ContainerTypeWrapper topType, childType;
        // ContainerWrapper top, child;
        //
        // childType = ContainerTypeHelper.addContainerType(site, name
        // + "_childType", name + "_childType", 3, 4, 9, false);
        // topType = ContainerTypeHelper.addContainerType(site, name +
        // "_topType",
        // name + "_topType", 1, 5, 5, true);
        // topType.addToChildContainerTypeCollection(Arrays.asList(childType));
        // topType.persist();
        // topType.reload();
        //
        // top = ContainerHelper.addContainer("01", null, null, site, topType);
        // child = ContainerHelper.newContainer(null, null, top, site,
        // childType,
        // 0, 0);
        // ContainerPathWrapper path = new ContainerPathWrapper(appService);
        // path.setContainer(child);
        // try {
        // path.persist();
        // Assert
        // .fail("should not be allowed to add path with container not in database");
        // } catch (BiobankCheckException e) {
        // Assert.assertTrue(true);
        // }
        //
        // path = ContainerPathWrapper.getContainerPath(appService, child);
        // Assert.assertNull(path);
    }

    @Test
    public void testPathAlreadyInDb() throws Exception {
        Assert.fail("need fixes to container wrapper for this test to work");
        // FIXME: Commenting out for now
        // String name = "testSetPath" + r.nextInt();
        // SiteWrapper site = SiteHelper.addSite(name);
        //
        // ContainerTypeWrapper topType, childType;
        // ContainerWrapper top, child;
        //
        // childType = ContainerTypeHelper.addContainerType(site, name
        // + "_childType", name + "_childType", 3, 4, 9, false);
        // topType = ContainerTypeHelper.addContainerType(site, name +
        // "_topType",
        // name + "_topType", 1, 5, 5, true);
        // topType.addToChildContainerTypeCollection(Arrays.asList(childType));
        // topType.persist();
        // topType.reload();
        //
        // top = ContainerHelper.addContainer("01", null, null, site, topType);
        // child = ContainerHelper.addContainer(null, null, top, site,
        // childType,
        // 0, 0);
        // ContainerPathWrapper path = new ContainerPathWrapper(appService);
        // path.setContainer(child);
        // try {
        // path.persist();
        // Assert.fail("should not be allowed to add path more than once");
        // } catch (BiobankCheckException e) {
        // Assert.assertTrue(true);
        // }
    }

    @Test
    public void testGetPath() throws Exception {
        Assert.fail("need fixes to container wrapper for this test to work");
        // FIXME: Commenting out for now
        // String name = "testSetPath" + r.nextInt();
        // SiteWrapper site = SiteHelper.addSite(name);
        //
        // ContainerTypeWrapper topType, childType;
        // ContainerWrapper top, child;
        //
        // childType = ContainerTypeHelper.addContainerType(site, name
        // + "_childType", name + "_childType", 3, 4, 9, false);
        // topType = ContainerTypeHelper.addContainerType(site, name +
        // "_topType",
        // name + "_topType", 1, 5, 5, true);
        // topType.addToChildContainerTypeCollection(Arrays.asList(childType));
        // topType.persist();
        // topType.reload();
        //
        // top = ContainerHelper.addContainer("01", null, null, site, topType);
        // child = ContainerHelper.addContainer(null, null, top, site,
        // childType,
        // 0, 0);
        // ContainerPathWrapper path = ContainerPathWrapper.getContainerPath(
        // appService, child);
        // String expectedPath = top.getId() + "/" + child.getId();
        // Assert.assertEquals(expectedPath, path.getPath());
    }
}
