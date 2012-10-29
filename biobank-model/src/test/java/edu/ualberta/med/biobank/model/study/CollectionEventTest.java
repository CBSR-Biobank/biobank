package edu.ualberta.med.biobank.model.study;

import org.junit.Test;

import edu.ualberta.med.biobank.DbTest;

public class CollectionEventTest
    extends DbTest {
    @Test
    public void persist() {
    }

    @Test
    public void patientNotNull() {
    }

    @Test
    public void typeNotNull() {
    }

    @Test
    public void visitNumberNotNull() {
    }

    @Test
    public void visitNumberMin() {
    }

    @Test
    public void timeDoneNotNull() {
    }

    @Test
    public void timeDonePast() {
    }

    @Test
    public void naturalIdUnique() {
    }

    @Test
    public void studiesMatch() {
        // check that the Patient and CollectionEventType are in the same Study
        // this could be done at insert time and also at update time only if the
        // properties have changed, to avoid lazy-loading
    }
}
