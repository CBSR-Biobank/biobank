package edu.ualberta.med.biobank.test.model;

import org.junit.Test;

import edu.ualberta.med.biobank.test.TestDb;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;

public class TestStudy extends TestDb {
    @Test
    public void duplicateName() {
        HasXHelper.checkDuplicateName(session,
            factory.createStudy(),
            factory.createStudy());
    }

    @Test
    public void duplicateNameShort() {
        HasXHelper.checkDuplicateNameShort(session,
            factory.createStudy(),
            factory.createStudy());
    }

    @Test
    public void expectedActivityStatusIds() {
        HasXHelper.checkExpectedActivityStatusIds(session,
            factory.createStudy());
    }
}
