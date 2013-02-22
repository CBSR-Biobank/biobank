package edu.ualberta.med.biobank.test.action;

import java.util.List;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.container.ContainerGetChildrenAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetContainerOrParentsByLabelAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetContainerOrParentsByLabelAction.ContainerData;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction.ContainerInfo;
import edu.ualberta.med.biobank.common.action.container.ContainerMoveAction;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class TestContainer extends TestAction {

    @Test
    public void createTopContainer() throws Exception {
        session.beginTransaction();
        ContainerType ctype = factory.createTopContainerType();
        session.getTransaction().commit();

        ActivityStatus astatus = ActivityStatus.FLAGGED;

        String uniqueString = getMethodNameR();

        ContainerSaveAction saveAction = new ContainerSaveAction();
        saveAction.setLabel(uniqueString);
        saveAction.setBarcode(uniqueString);
        saveAction.setTypeId(ctype.getId());
        saveAction.setSiteId(ctype.getSite().getId());
        saveAction.setActivityStatus(astatus);
        saveAction.setCommentText(uniqueString);
        Integer containerId = exec(saveAction).getId();

        session.clear();

        Container container = (Container) session.get(Container.class, containerId);

        Assert.assertEquals(uniqueString, container.getLabel());
        Assert.assertEquals(uniqueString, container.getProductBarcode());
        Assert.assertEquals(ctype.getName(), container.getContainerType().getName());
        Assert.assertEquals(astatus, container.getActivityStatus());
        Assert.assertEquals(ctype.getSite().getName(), container.getSite().getName());
        Assert.assertEquals(0, container.getChildPositions().size());
        Assert.assertEquals(0, container.getSpecimenPositions().size());

        Assert.assertEquals(1, container.getComments().size());
        Assert.assertEquals(uniqueString, container.getComments().iterator().next().getMessage());
    }

    @Test
    public void createChildContainer() throws Exception {
        session.beginTransaction();

        ContainerLabelingScheme labeling = (ContainerLabelingScheme)
            session.createCriteria(ContainerLabelingScheme.class)
                .add(Restrictions.eq("name", "SBS Standard")).uniqueResult();

        Capacity capacity = new Capacity();
        capacity.setRowCapacity(4);
        capacity.setColCapacity(4);
        factory.setDefaultCapacity(capacity);

        Container topContainer = factory.createTopContainer();
        ContainerType ctype = factory.createContainerType();
        topContainer.getContainerType().setChildLabelingScheme(labeling);
        topContainer.getContainerType().getChildContainerTypes().add(ctype);
        session.getTransaction().commit();

        ActivityStatus astatus = ActivityStatus.FLAGGED;

        String uniqueString = getMethodNameR();

        ContainerSaveAction saveAction = new ContainerSaveAction();
        saveAction.setParentId(topContainer.getId());
        saveAction.setPosition(new RowColPos(0, 0));
        saveAction.setBarcode(uniqueString);
        saveAction.setTypeId(ctype.getId());
        saveAction.setSiteId(ctype.getSite().getId());
        saveAction.setActivityStatus(astatus);
        saveAction.setCommentText(uniqueString);
        Integer containerId = exec(saveAction).getId();

        session.clear();

        Container container = (Container) session.get(Container.class, containerId);

        Assert.assertEquals(topContainer, container.getParentContainer());
        Assert.assertEquals(0, container.getPosition().getRow().intValue());
        Assert.assertEquals(0, container.getPosition().getCol().intValue());
        Assert.assertEquals(topContainer, container.getTopContainer());
        Assert.assertEquals(topContainer.getLabel() + "A1", container.getLabel());
        Assert.assertEquals(uniqueString, container.getProductBarcode());
        Assert.assertEquals(ctype.getName(), container.getContainerType().getName());
        Assert.assertEquals(astatus, container.getActivityStatus());
        Assert.assertEquals(ctype.getSite().getName(), container.getSite().getName());
        Assert.assertEquals(0, container.getChildPositions().size());
        Assert.assertEquals(0, container.getSpecimenPositions().size());

        Assert.assertEquals(1, container.getComments().size());
        Assert.assertEquals(uniqueString, container.getComments().iterator().next().getMessage());
    }

    @Test
    public void checkGetAction() throws Exception {
        session.beginTransaction();
        Container container = factory.createContainer();
        session.getTransaction().commit();

        ContainerInfo containerInfo = exec(new ContainerGetInfoAction(container.getId()));

        Assert.assertEquals(container.getLabel(), containerInfo.container.getLabel());
        Assert.assertEquals(container.getContainerType().getName(), containerInfo.container
            .getContainerType().getName());
        Assert.assertEquals(ActivityStatus.ACTIVE, containerInfo.container.getActivityStatus());
        Assert.assertEquals(container.getSite().getName(),
            containerInfo.container.getSite().getName());
        Assert.assertEquals(0, containerInfo.container.getChildPositions().size());
        Assert.assertEquals(0, containerInfo.container.getSpecimenPositions().size());
        Assert.assertEquals(0, containerInfo.container.getComments().size());
    }

    // TODO: need tests for container labels

    @Test
    public void getChildren() throws Exception {
        session.beginTransaction();

        ContainerLabelingScheme labeling = (ContainerLabelingScheme)
            session.createCriteria(ContainerLabelingScheme.class)
                .add(Restrictions.eq("name", "SBS Standard")).uniqueResult();

        Capacity capacity = new Capacity();
        capacity.setRowCapacity(4);
        capacity.setColCapacity(4);
        factory.setDefaultCapacity(capacity);

        Container topContainer = factory.createTopContainer();
        Container childContainer = factory.createContainer();
        topContainer.getContainerType().setChildLabelingScheme(labeling);
        topContainer.getContainerType().getChildContainerTypes().add(
            childContainer.getContainerType());

        SpecimenType specimenType = factory.createSpecimenType();
        childContainer.getContainerType().getSpecimenTypes().add(specimenType);

        session.getTransaction().commit();

        List<Container> containers = exec(new ContainerGetChildrenAction(
            topContainer.getId())).getList();
        Assert.assertEquals(1, containers.size());

        Container container = containers.iterator().next();
        Assert.assertEquals(1, container.getContainerType().getSpecimenTypes().size());
    }

    @Test
    public void moveContainer() {
        session.beginTransaction();

        factory.createTopContainer();
        Container childL1Container = factory.createParentContainer();
        Container[] childL2Containers = new Container[] {
            factory.createContainer(),
            factory.createContainer(),
            factory.createContainer(),
        };

        Container topContainer2 = factory.createTopContainer();
        session.getTransaction().commit();

        exec(new ContainerMoveAction(childL1Container, topContainer2,
            topContainer2.getLabel() + "A1"));

        ContainerInfo containerInfo =
            exec(new ContainerGetInfoAction(childL1Container.getId()));
        childL1Container = containerInfo.container;

        Assert.assertEquals(topContainer2.getId(),
            childL1Container.getTopContainer().getId());
        Assert.assertEquals(topContainer2.getId(),
            childL1Container.getParentContainer().getId());

        String expectedL2Path =
            topContainer2.getId() + "/" + childL1Container.getId();

        // check that children have correct settings too
        for (Container child : childL2Containers) {
            containerInfo = exec(new ContainerGetInfoAction(child.getId()));
            Assert.assertEquals(topContainer2.getId(),
                containerInfo.container.getTopContainer().getId());

            // ensure parent is still childL1Container
            Assert.assertEquals(childL1Container.getId(),
                containerInfo.container.getParentContainer().getId());

            // check the path
            Assert
                .assertEquals(expectedL2Path, containerInfo.container.getPath());
        }
    }

    @Test
    public void moveContainerOccupiedLocation() {
        Transaction tx = session.beginTransaction();

        factory.createTopContainer();
        Container child1Container = factory.createContainer();

        Container topContainer2 = factory.createTopContainer();
        // create child at position A1
        factory.createContainer();
        tx.commit();

        try {
            exec(new ContainerMoveAction(child1Container, topContainer2,
                topContainer2.getLabel() + "A1"));
            Assert
                .fail("should not be allowed to move a containers to occupied spot");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void moveContainerDiffSites() {
        // containers are not allowed to be moved between sites
        session.beginTransaction();

        factory.createTopContainer();
        Container childContainer = factory.createContainer();

        factory.createSite();
        factory.createTopContainerType();
        Container topContainerS2 = factory.createTopContainer();

        session.getTransaction().commit();

        try {
            // use default labeling scheme
            exec(new ContainerMoveAction(childContainer, topContainerS2,
                topContainerS2.getLabel() + "A1"));
            Assert.fail("should not be allowed to move containers between sites");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

    }

    /**
     * Containers of different types can have the same labels
     */
    @Test
    public void getParentsByChildLabelSingleContainer() {
        session.beginTransaction();
        factory.createTopContainerType();
        Container topContainer = factory.createTopContainer();
        topContainer.setLabel("C01");
        factory.createContainerType();
        Container childContainer = factory.createContainer();
        childContainer.setLabel("C01A1");
        session.getTransaction().commit();

        ContainerData containerData = exec(new ContainerGetContainerOrParentsByLabelAction(
            childContainer.getLabel() + "A1", childContainer.getSite(),
            childContainer.getContainerType()));

        Assert.assertEquals(1, containerData.getPossibleParentContainers().size());
    }

    /**
     * Containers of different types can have the same labels
     */
    @Test
    public void getParentsByChildLabelMultipleContainers() {
        session.beginTransaction();
        Container topContainer = factory.createTopContainer();
        topContainer.setLabel("C02");
        Container childContainer = factory.createContainer();
        childContainer.setLabel("C02A1");

        factory.createTopContainerType();
        Container topContainer2 = factory.createTopContainer();
        topContainer2.setLabel("C02");
        factory.createContainerType();
        Container childContainer2 = factory.createContainer();
        childContainer2.setLabel("C02A1");
        session.getTransaction().commit();

        ContainerData containerData = exec(new ContainerGetContainerOrParentsByLabelAction(
            childContainer.getLabel() + "A1", childContainer.getSite()));

        Assert.assertEquals(2, containerData.getPossibleParentContainers().size());
    }
}
