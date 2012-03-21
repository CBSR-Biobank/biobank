package edu.ualberta.med.biobank.test.action;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeDeleteAction;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeGetAllAction;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeSaveAction;
import edu.ualberta.med.biobank.model.SpecimenType;

public class TestSpecimenType extends TestAction {

    private String name;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();
    }

    @Test
    public void mangleTypes() {
        SpecimenTypeGetAllAction action = new SpecimenTypeGetAllAction();
        Integer size = EXECUTOR.exec(action).getList().size();

        final Integer typeId =
            EXECUTOR.exec(new SpecimenTypeSaveAction(name, name)).getId();

        Assert.assertTrue(size + 1 == EXECUTOR.exec(action).getList().size());

        SpecimenTypeDeleteAction delete =
            new SpecimenTypeDeleteAction((SpecimenType) session.load(
                SpecimenType.class,
                typeId));
        EXECUTOR.exec(delete);

        Assert.assertTrue(size == EXECUTOR.exec(action).getList().size());
    }
}
