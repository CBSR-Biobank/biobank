package edu.ualberta.med.biobank.test.action;

import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetPossibleTypesAction;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;

public class TestSpecimenGetPossibleTypes extends TestAction {

    @Test
    public void test() {
        SpecimenGetPossibleTypesAction action =
            new SpecimenGetPossibleTypesAction(4);
        List<AliquotedSpecimen> list = exec(action).getList();
    }
}
