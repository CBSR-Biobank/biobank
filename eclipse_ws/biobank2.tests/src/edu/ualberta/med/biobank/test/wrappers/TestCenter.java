package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.test.TestDatabase;

public class TestCenter extends TestDatabase {

    @Test
    public void testCenter() {
        Assert
            .fail("Some tests are common to clinic, site and research group. "
                + "We might want them tested here ?");
    }

}
