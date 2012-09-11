package edu.ualberta.med.biobank.test.model;

import org.junit.Test;

import edu.ualberta.med.biobank.test.TestDb;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;

public class TestGroup extends TestDb {
    @Test
    public void duplicateName() {
        HasXHelper.checkDuplicateName(session,
            factory.createGroup(),
            factory.createGroup());
    }
}
