package edu.ualberta.med.biobank.model;

import org.junit.Test;

import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.model.util.HasXHelper;

public class TestSpecimenType extends DbTest {
    @Test
    public void duplicateName() {
        HasXHelper.checkDuplicateName(session,
            factory.createSpecimenType(),
            factory.createSpecimenType());
    }
}
