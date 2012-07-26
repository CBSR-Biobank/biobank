package edu.ualberta.med.biobank.model;

import org.junit.Test;

import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.model.util.HasXHelper;

public class TestContact extends DbTest {
    @Test
    public void emptyName() {
        HasXHelper.checkEmptyName(session, factory.createContact());
    }
}
