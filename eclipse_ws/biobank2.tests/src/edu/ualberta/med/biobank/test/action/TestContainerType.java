package edu.ualberta.med.biobank.test.action;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.constraint.ConstraintViolationException;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeSaveAction;
import edu.ualberta.med.biobank.test.action.helper.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;

public class TestContainerType extends TestAction {

    private String name;

    private Integer siteId;

    private ContainerTypeSaveAction containerTypeSaveAction;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();

        siteId = EXECUTOR.exec(SiteHelper.getSaveAction(
            name, name, ActivityStatusEnum.ACTIVE)).getId();

        containerTypeSaveAction = ContainerTypeHelper.getSaveAction(
            "FREEZER01", "FR01", siteId, true, 3, 10,
            getContainerLabelingSchemes().get("CBSR 2 char alphabetic")
                .getId());
    }

    @Test
    public void saveNew() throws Exception {
        containerTypeSaveAction.setName(null);
        try {
            EXECUTOR.exec(containerTypeSaveAction);
            Assert
                .fail("should not be allowed to add container type with no name");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        containerTypeSaveAction.setName(name);
        containerTypeSaveAction.setNameShort(null);
        try {
            EXECUTOR.exec(containerTypeSaveAction);
            Assert
                .fail("should not be allowed to add container type with no name short");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        containerTypeSaveAction.setNameShort(name);
        containerTypeSaveAction.setSiteId(null);
        try {
            EXECUTOR.exec(containerTypeSaveAction);
            Assert
                .fail("should not be allowed to add container type with no site");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        containerTypeSaveAction.setSiteId(siteId);
        containerTypeSaveAction.setTopLevel(null);
        try {
            EXECUTOR.exec(containerTypeSaveAction);
            Assert
                .fail("should not be allowed to add container type with null for top level");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        containerTypeSaveAction.setTopLevel(true);
        containerTypeSaveAction.setRowCapacity(null);
        try {
            EXECUTOR.exec(containerTypeSaveAction);
            Assert
                .fail("should not be allowed to add container type with null for row capacity");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        containerTypeSaveAction.setRowCapacity(3);
        containerTypeSaveAction.setColCapacity(null);
        try {
            EXECUTOR.exec(containerTypeSaveAction);
            Assert
                .fail("should not be allowed to add container type with null for column capacity");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        containerTypeSaveAction.setColCapacity(10);
        containerTypeSaveAction.setChildLabelingSchemeId(null);
        try {
            EXECUTOR.exec(containerTypeSaveAction);
            Assert
                .fail("should not be allowed to add container type with null for child labeling scheme");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        containerTypeSaveAction
            .setChildLabelingSchemeId(getContainerLabelingSchemes().get(
                "CBSR 2 char alphabetic")
                .getId());
        containerTypeSaveAction.setActivityStatusId(null);
        try {
            EXECUTOR.exec(containerTypeSaveAction);
            Assert
                .fail("should not be allowed to add container type with null for activity status");
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }

        // test success path
        containerTypeSaveAction.setActivityStatusId(ActivityStatusEnum.ACTIVE
            .getId());
        EXECUTOR.exec(containerTypeSaveAction);

    }

    @Test
    public void checkGetAction() throws Exception {
        Integer containerTypeId =
            EXECUTOR.exec(containerTypeSaveAction).getId();
        ContainerTypeInfo topContainerTypeInfo =
            EXECUTOR.exec(new ContainerTypeGetInfoAction(containerTypeId));

        Assert.assertEquals("FREEZER01",
            topContainerTypeInfo.containerType.getName());
        Assert.assertEquals("Active", topContainerTypeInfo.containerType
            .getActivityStatus().getName());
        Assert.assertEquals(3, topContainerTypeInfo.containerType
            .getCapacity().getRowCapacity().intValue());
        Assert.assertEquals(10, topContainerTypeInfo.containerType
            .getCapacity().getColCapacity().intValue());
        Assert.assertEquals(0, topContainerTypeInfo.containerType
            .getChildContainerTypeCollection().size());
        Assert.assertEquals(0, topContainerTypeInfo.containerType
            .getCommentCollection().size());

        containerTypeSaveAction = ContainerTypeHelper.getSaveAction(
            "HOTEL13", "H13", siteId, false, 13, 1,
            getContainerLabelingSchemes().get("2 char numeric").getId());
        EXECUTOR.exec(containerTypeSaveAction).getId();
        topContainerTypeInfo =
            EXECUTOR.exec(new ContainerTypeGetInfoAction(containerTypeId));

    }

    @Test
    public void nameChecks() throws Exception {
        // ensure we can change name on existing container type
    }
}
