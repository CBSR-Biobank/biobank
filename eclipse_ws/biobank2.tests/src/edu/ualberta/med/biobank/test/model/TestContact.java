package edu.ualberta.med.biobank.test.model;

import org.junit.Test;

import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.test.model.util.HasXHelper;

public class TestContact extends DbTest {
    @Test
    public void emptyName() {
        HasXHelper.checkEmptyName(session, factory.createContact());
    }
}
