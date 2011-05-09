package edu.ualberta.med.biobank.test.wrappers;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.TestDatabase;

public class TestCaCore extends TestDatabase {
    /**
     * Check whether ApplicationService method() arguments are incorrectly
     * modified when unwrapped/ wrapped.
     * 
     * @throws Exception
     */
    @Test
    public void epicFail() throws Exception {
        CollectionEvent ce = new CollectionEvent();
        ce.setId(1);

        Specimen s1 = new Specimen();
        s1.setId(1);
        s1.setComment("hi");

        Specimen s2 = new Specimen();
        s2.setId(1);
        s2.setComment("bye");

        ce.setAllSpecimenCollection(Arrays.asList(s1));
        ce.setOriginalSpecimenCollection(Arrays.asList(s2));

        appService.search(CollectionEvent.class, ce);

        Specimen after1 = ce.getAllSpecimenCollection().iterator().next();
        Specimen after2 = ce.getOriginalSpecimenCollection().iterator().next();

        Assert.assertTrue(after1 != after2);
        Assert.assertTrue(!after1.getComment().equals(after2.getComment()));
    }
}
