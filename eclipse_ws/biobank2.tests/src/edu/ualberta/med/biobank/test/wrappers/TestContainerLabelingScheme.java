package edu.ualberta.med.biobank.test.wrappers;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

@Deprecated
public class TestContainerLabelingScheme extends TestDatabase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void TestGetAllLabelingSchemes() throws Exception {
        ContainerLabelingSchemeWrapper.getAllLabelingSchemesMap(appService)
            .values();
    }

    @Test
    public void testDelete() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testSite");
        ContainerHelper.addContainer("01AA",
            "asd", site, ContainerTypeHelper.addContainerType(site, "testCT",
                "tct", ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA, 1,
                1, true));

        ContainerLabelingSchemeWrapper newCLSW =
            new ContainerLabelingSchemeWrapper(
                appService);
        newCLSW.persist();
        newCLSW.delete();

        try {
            ContainerLabelingSchemeWrapper.getLabelingSchemeById(appService,
                ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA).delete();
            Assert.fail("Should not be able to delete schemes that are in use");
        } catch (BiobankSessionException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCompareTo() throws Exception {
        // fake test... this wrapper always returns 0
        ContainerLabelingSchemeWrapper.getLabelingSchemeById(appService,
            ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA).compareTo(
            ContainerLabelingSchemeWrapper.getLabelingSchemeById(appService,
                ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA));
    }

    @Test
    public void testErrors() throws Exception {
        try {
            ContainerLabelingSchemeWrapper.getLabelingSchemeById(appService,
                13123);
            Assert.fail("Should have received an exception.");
        } catch (ApplicationException e) {
            Assert.assertTrue(true);
        }

        String original;
        ContainerLabelingSchemeWrapper scheme;
        for (int i = 1; i < 7; i++) {
            Map<Integer, ContainerLabelingSchemeWrapper> map =
                ContainerLabelingSchemeWrapper
                    .getAllLabelingSchemesMap(appService);
            scheme = map.get(i);
            original = scheme.getName();
            try {
                scheme.setName("asds");
                scheme.persist();
                ContainerLabelingSchemeWrapper
                    .getAllLabelingSchemesMap(appService);
                Assert.fail("Should have thrown an exception");
            } catch (Exception e) {
                Assert.assertTrue(true);
            } finally {
                scheme.setName(original);
                scheme.persist();
            }
        }
    }
}
