package edu.ualberta.med.biobank.test.model;

import org.junit.Test;

import edu.ualberta.med.biobank.test.TestDb;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;

public class TestPrincipal extends TestDb {
    @Test
    public void expectedActivityStatusIds() {
        HasXHelper.checkExpectedActivityStatusIds(session,
            factory.createGroup());
    }
}
