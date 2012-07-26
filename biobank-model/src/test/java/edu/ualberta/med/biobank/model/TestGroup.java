package edu.ualberta.med.biobank.model;

import org.junit.Test;

import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.model.util.HasXHelper;

public class TestGroup extends DbTest {
    @Test
    public void duplicateName() {
        HasXHelper.checkDuplicateName(session,
            factory.createGroup(),
            factory.createGroup());
    }
}
