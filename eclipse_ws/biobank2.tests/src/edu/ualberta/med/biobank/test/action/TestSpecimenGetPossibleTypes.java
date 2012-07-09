package edu.ualberta.med.biobank.test.action;

import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetPossibleTypesAction;
import edu.ualberta.med.biobank.model.AbstractVersionedModel;

public class TestSpecimenGetPossibleTypes extends ActionTest {

    @Test
    public void test() {
        SpecimenGetPossibleTypesAction action =
            new SpecimenGetPossibleTypesAction(4);
        List<AbstractVersionedModel> list = exec(action).getList();
    }
}
