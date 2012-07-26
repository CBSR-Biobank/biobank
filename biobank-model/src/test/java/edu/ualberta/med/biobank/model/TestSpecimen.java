package edu.ualberta.med.biobank.model;

import org.junit.Test;

import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.model.util.HasXHelper;

public class TestSpecimen extends DbTest {
    @Test
    public void expectedActivityStatusIds() {
        HasXHelper.checkExpectedActivityStatusIds(session,
            factory.createParentSpecimen());
    }
}
