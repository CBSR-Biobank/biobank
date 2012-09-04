package edu.ualberta.med.biobank.test.action;

import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetPossibleTypesAction;
import edu.ualberta.med.biobank.model.AbstractBiobankModel;

public class TestSpecimenGetPossibleTypes extends ActionTest {

    @SuppressWarnings("unused")
    @Test
    public void test() {
        SpecimenGetPossibleTypesAction action =
            new SpecimenGetPossibleTypesAction(4);
        List<AbstractBiobankModel> list = exec(action).getList();
    }
}
