package edu.ualberta.med.biobank.test.model;

import org.junit.Test;

import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;

public class TestGroup extends DbTest {
    @Test
    public void duplicateName() {
        HasXHelper.checkDuplicateName(session,
            factory.createGroup(),
            factory.createGroup());
    }
}
