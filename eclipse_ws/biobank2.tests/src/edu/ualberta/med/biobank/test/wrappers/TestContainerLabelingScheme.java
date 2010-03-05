package edu.ualberta.med.biobank.test.wrappers;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;

public class TestContainerLabelingScheme extends TestDatabase {

    private ContainerLabelingSchemeWrapper clsw;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        clsw = ContainerHelper.newContainerLabelingScheme();
    }

    @Test
    public void TestGetAllLabelingSchemes() throws BiobankCheckException,
        Exception {
        // check after add
        Collection<ContainerLabelingSchemeWrapper> beforeWraps = ContainerLabelingSchemeWrapper
            .getAllLabelingSchemes(appService);
        clsw.persist();
        Collection<ContainerLabelingSchemeWrapper> afterWraps = ContainerLabelingSchemeWrapper
            .getAllLabelingSchemes(appService);

        Iterator<ContainerLabelingSchemeWrapper> b = beforeWraps.iterator();
        Iterator<ContainerLabelingSchemeWrapper> a = afterWraps.iterator();
        while (b.hasNext() && a.hasNext()) {
            Assert.assertTrue(b.next().equals(a.next()));
        }
        Assert.assertTrue(!b.hasNext() && a.hasNext());
        Assert.assertTrue(clsw.equals(a.next()));

        // check after delete
        clsw.delete();
        afterWraps = ContainerLabelingSchemeWrapper
            .getAllLabelingSchemes(appService);
        b = beforeWraps.iterator();
        a = afterWraps.iterator();
        while (b.hasNext() && a.hasNext()) {
            Assert.assertTrue(b.next().equals(a.next()));
        }
        Assert.assertTrue(!b.hasNext() && !a.hasNext());
    }

    @Test
    public void TestBasicGettersAndSetters() throws BiobankCheckException,
        Exception {
        clsw.persist();
        testGettersAndSetters(clsw);
        clsw.delete();
    }

}
