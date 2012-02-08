package edu.ualberta.med.biobank.test.wrappers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.TestDatabase;

@Deprecated
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
        s1.setQuantity(new BigDecimal(0.1));

        Specimen s2 = new Specimen();
        s2.setId(1);
        s1.setQuantity(new BigDecimal(0.2));

        ce.setAllSpecimenCollection(new HashSet<Specimen>(Arrays.asList(s1)));
        ce.setOriginalSpecimenCollection(new HashSet<Specimen>(Arrays.asList(s2)));

        appService.search(CollectionEvent.class, ce);

        Specimen after1 = ce.getAllSpecimenCollection().iterator().next();
        Specimen after2 = ce.getOriginalSpecimenCollection().iterator().next();

        Assert.assertTrue(after1 != after2);
        Assert.assertTrue(!after1.getQuantity().equals(after2.getQuantity()));
    }
}
