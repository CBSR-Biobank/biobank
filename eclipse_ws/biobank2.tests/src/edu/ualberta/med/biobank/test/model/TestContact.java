package edu.ualberta.med.biobank.test.model;

import org.junit.Test;

import edu.ualberta.med.biobank.test.TestDb;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;

public class TestContact extends TestDb {
    @Test
    public void emptyName() {
        HasXHelper.checkEmptyName(session, factory.createContact());
    }
}
