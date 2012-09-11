package edu.ualberta.med.biobank.test.model;

import org.junit.Test;

import edu.ualberta.med.biobank.test.TestDb;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;

public class TestSpecimenType extends TestDb {
    @Test
    public void duplicateName() {
        HasXHelper.checkDuplicateName(session,
            factory.createSpecimenType(),
            factory.createSpecimenType());
    }

    @Test
    public void duplicateNameShort() {
        HasXHelper.checkDuplicateNameShort(session,
            factory.createSpecimenType(),
            factory.createSpecimenType());
    }
}
