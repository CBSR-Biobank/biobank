package edu.ualberta.med.biobank.test.action;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.action.labelPrinter.GetSourceSpecimenUniqueInventoryIdSetAction;

public class TestPatientLabels extends TestAction {

    @Test
    public void getSourceSpecimenUniqueInventoryIdSetAction() {

        List<String> specimenInventoryIds = exec(
            new GetSourceSpecimenUniqueInventoryIdSetAction(0)).getList();

        Assert.assertEquals(0, specimenInventoryIds.size());

        specimenInventoryIds = exec(
            new GetSourceSpecimenUniqueInventoryIdSetAction(10)).getList();

        Assert.assertEquals(10, specimenInventoryIds.size());

    }

}
