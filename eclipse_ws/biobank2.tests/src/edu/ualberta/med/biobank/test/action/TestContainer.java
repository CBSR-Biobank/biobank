package edu.ualberta.med.biobank.test.action;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction.ContainerInfo;
import edu.ualberta.med.biobank.common.action.container.ContainerMoveAction;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;

public class TestContainer extends TestAction {

    private String name;

    private Integer siteId;

    private Integer containerTypeId;

    private ContainerSaveAction containerSaveAction;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();

        siteId = exec(SiteHelper.getSaveAction(
            name, name, ActivityStatus.ACTIVE)).getId();

        containerTypeId = exec(ContainerTypeHelper.getSaveAction(
            "FREEZER_3x10", "FR3x10", siteId, true, 3, 10,
            getContainerLabelingSchemes().get("CBSR 2 char alphabetic")
                .getId(), getR().nextDouble())).getId();

        containerSaveAction = new ContainerSaveAction();
        containerSaveAction.setActivityStatus(ActivityStatus.ACTIVE);
        containerSaveAction.setBarcode(Utils.getRandomString(5, 10));
        containerSaveAction.setLabel("01");
        containerSaveAction.setSiteId(siteId);
        containerSaveAction.setTypeId(containerTypeId);
    }

    @Test
    public void saveNew() throws Exception {

    }

    @Test
    public void checkGetAction() throws Exception {
        Integer containerId = exec(containerSaveAction).getId();
        ContainerInfo containerInfo =
            exec(new ContainerGetInfoAction(containerId));

        Assert.assertEquals("01", containerInfo.container.getLabel());
        Assert.assertEquals("FREEZER_3x10", containerInfo.container
            .getContainerType().getName());
        Assert.assertEquals(ActivityStatus.ACTIVE, containerInfo.container
            .getActivityStatus());
        Assert.assertEquals(name, containerInfo.container.getSite().getName());
        Assert.assertEquals(0, containerInfo.container
            .getChildPositions().size());
        Assert.assertEquals(0, containerInfo.container
            .getSpecimenPositions().size());
        Assert.assertEquals(0, containerInfo.container.getComments()
            .size());
    }

    // TODO: need tests for container labels

    @Test
    public void testMoveContainer() {
        Transaction tx = session.beginTransaction();

        factory.createTopContainer();
        Container childL1Container = factory.createParentContainer();
        Container[] childL2Containers = new Container[] {
            factory.createContainer(),
            factory.createContainer(),
            factory.createContainer(),
        };

        Container topContainer2 = factory.createTopContainer();
        tx.commit();

        exec(new ContainerMoveAction(childL1Container, topContainer2,
            topContainer2.getLabel() + "A1"));

        ContainerInfo containerInfo =
            exec(new ContainerGetInfoAction(childL1Container.getId()));
        childL1Container = containerInfo.container;

        Assert.assertEquals(topContainer2.getId(),
            childL1Container.getTopContainer());
        Assert.assertEquals(topContainer2.getId(),
            childL1Container.getParentContainer());
    }

    @Test
    public void testMoveContainerOccupiedLocation() {

    }

    @Test
    public void testMoveContainerDiffSites() {
        // containers are not allowed to be moved between sites
        Transaction tx = session.beginTransaction();

        factory.createTopContainer();
        Container childContainer = factory.createContainer();

        factory.createSite();
        factory.createTopContainerType();
        Container topContainerS2 = factory.createTopContainer();

        tx.commit();

        try {
            // use default labeling scheme
            exec(new ContainerMoveAction(childContainer, topContainerS2,
                topContainerS2.getLabel() + "A1"));
            Assert
                .fail("should not be allowed to move containers between sites");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

    }
}
