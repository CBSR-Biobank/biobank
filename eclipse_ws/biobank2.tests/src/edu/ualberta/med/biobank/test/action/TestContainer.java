package edu.ualberta.med.biobank.test.action;

import org.junit.Before;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
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
            name, name, ActivityStatusEnum.ACTIVE)).getId();

        containerTypeId = EXECUTOR.exec(ContainerTypeHelper.getSaveAction(
            "FREEZER_3x10", "FR3x10", siteId, true, 3, 10,
            getContainerLabelingSchemes().get("CBSR 2 char alphabetic")
                .getId(), R.nextDouble())).getId();
    }

}
