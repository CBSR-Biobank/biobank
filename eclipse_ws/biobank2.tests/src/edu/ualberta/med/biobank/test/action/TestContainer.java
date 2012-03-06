package edu.ualberta.med.biobank.test.action;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction.ContainerInfo;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
import edu.ualberta.med.biobank.model.ActivityStatus;
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

        siteId = EXECUTOR.exec(SiteHelper.getSaveAction(
            name, name, ActivityStatus.ACTIVE)).getId();

        containerTypeId = EXECUTOR.exec(ContainerTypeHelper.getSaveAction(
            "FREEZER_3x10", "FR3x10", siteId, true, 3, 10,
            getContainerLabelingSchemes().get("CBSR 2 char alphabetic")
                .getId(), R.nextDouble())).getId();

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
        Integer containerId = EXECUTOR.exec(containerSaveAction).getId();
        ContainerInfo containerInfo =
            EXECUTOR.exec(new ContainerGetInfoAction(containerId));

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

}
