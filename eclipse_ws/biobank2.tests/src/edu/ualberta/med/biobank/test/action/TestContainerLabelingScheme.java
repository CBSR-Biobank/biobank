package edu.ualberta.med.biobank.test.action;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.action.containerType.ContainerLabelingSchemeGetInfoAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerLabelingSchemeGetInfoAction.ContainerLabelingSchemeInfo;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;

public class TestContainerLabelingScheme extends TestAction {

    @Test
    public void checkGetAction() {
        session.beginTransaction();
        ContainerLabelingScheme scheme = factory.createContainerLabelingScheme();
        session.getTransaction().commit();

        ContainerLabelingSchemeInfo info =
            exec(new ContainerLabelingSchemeGetInfoAction(scheme.getName()));

        Assert.assertEquals(scheme.getName(), info.getLabelingScheme().getName());
        Assert.assertEquals(scheme.getMinChars(), info.getLabelingScheme().getMinChars());
        Assert.assertEquals(scheme.getMaxChars(), info.getLabelingScheme().getMaxChars());
        Assert.assertEquals(scheme.getMaxCapacity(),
            info.getLabelingScheme().getMaxCapacity());
    }

}
