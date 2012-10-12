package edu.ualberta.med.biobank.action;

import java.util.List;

import org.junit.Test;

import edu.ualberta.med.biobank.action.specimen.SpecimenGetPossibleTypesAction;
import edu.ualberta.med.biobank.model.VersionedLongIdModel;

public class TestSpecimenGetPossibleTypes extends ActionTest {

    @Test
    public void test() {
        SpecimenGetPossibleTypesAction action =
            new SpecimenGetPossibleTypesAction(4);
        List<VersionedLongIdModel> list = exec(action).getList();
    }
}
