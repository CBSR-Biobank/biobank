package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import test.ualberta.med.biobank.internal.ContainerTypeHelper;
import test.ualberta.med.biobank.internal.DbHelper;
import test.ualberta.med.biobank.internal.SampleTypeHelper;
import test.ualberta.med.biobank.internal.SiteHelper;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.SampleType;

public class TestSampleType extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(null, name);

        testGettersAndSetters(type);
    }

    @Test
    public void testGetSetSite() throws Exception {
        String name = "testGetSite" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(site, name);

        Assert.assertEquals(site, type.getSite());

        SiteWrapper site2 = SiteHelper.addSite(name + "SITE2");
        type.setSite(site2);

        Assert.assertEquals(site2, type.getSite());
        Assert.assertFalse(site.equals(type.getSite()));
    }

    @Test
    public void testGetContainerTypeCollection() throws Exception {
        String name = "testGetContainerTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(site, name);
        List<SampleTypeWrapper> sampleTypes = new ArrayList<SampleTypeWrapper>();
        sampleTypes.add(type);
        int containerTypeNber = 10;
        ContainerTypeHelper.addContainerTypesRandom(site, name,
            containerTypeNber);
        int nber = r.nextInt(containerTypeNber) + 1;
        List<ContainerTypeWrapper> containerTypes = site
            .getContainerTypeCollection();
        for (int i = 0; i < nber; i++) {
            ContainerTypeWrapper containerType = containerTypes.get(i);
            containerType.setSampleTypeCollection(sampleTypes);
            containerType.persist();
        }

        type.reload();
        Assert.assertEquals(nber, type.getContainerTypeCollection().size());
    }

    @Test
    public void testGetContainerTypeCollectionBoolean() throws Exception {
        String name = "testGetContainerTypeCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(site, name);
        List<SampleTypeWrapper> sampleTypes = new ArrayList<SampleTypeWrapper>();
        sampleTypes.add(type);
        int containerTypeNber = 10;
        ContainerTypeHelper.addContainerTypesRandom(site, name,
            containerTypeNber);
        int nber = r.nextInt(containerTypeNber) + 1;
        List<ContainerTypeWrapper> containerTypes = site
            .getContainerTypeCollection();
        for (int i = 0; i < nber; i++) {
            ContainerTypeWrapper containerType = containerTypes.get(i);
            containerType.setSampleTypeCollection(sampleTypes);
            containerType.persist();
        }

        type.reload();
        List<ContainerTypeWrapper> containerTypesSorted = type
            .getContainerTypeCollection(true);
        if (containerTypesSorted.size() > 1) {
            for (int i = 0; i < containerTypesSorted.size() - 1; i++) {
                ContainerTypeWrapper cType1 = containerTypesSorted.get(i);
                ContainerTypeWrapper cType2 = containerTypesSorted.get(i + 1);
                Assert.assertTrue(cType1.compareTo(cType2) <= 0);
            }
        }
    }

    @Test
    public void testGetSampleTypeForContainerTypes() throws Exception {
        String name = "testGetContainerTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ContainerTypeWrapper containerType1 = ContainerTypeHelper
            .addContainerTypeRandom(site, "TYPE1");
        ContainerTypeWrapper containerType2 = ContainerTypeHelper
            .addContainerTypeRandom(site, "TYPE2");

        List<SampleTypeWrapper> sampleTypes1 = new ArrayList<SampleTypeWrapper>();
        List<SampleTypeWrapper> sampleTypes2 = new ArrayList<SampleTypeWrapper>();
        SampleTypeWrapper sampleType1 = SampleTypeHelper.addSampleType(site,
            "ST1");
        sampleTypes1.add(sampleType1);
        SampleTypeWrapper sampleType2 = SampleTypeHelper.addSampleType(site,
            "ST2");
        sampleTypes1.add(sampleType2);
        sampleTypes2.add(sampleType2);
        SampleTypeWrapper sampleType3 = SampleTypeHelper.addSampleType(site,
            "ST3");
        sampleTypes1.add(sampleType3);
        SampleTypeWrapper sampleType4 = SampleTypeHelper.addSampleType(site,
            "ST4");
        sampleTypes2.add(sampleType4);
        containerType1.setSampleTypeCollection(sampleTypes1);
        int type1Size = sampleTypes1.size();
        containerType1.persist();
        containerType2.setSampleTypeCollection(sampleTypes2);
        int type2Size = sampleTypes2.size();
        containerType2.persist();

        List<SampleTypeWrapper> sampleTypesFound = SampleTypeWrapper
            .getSampleTypeForContainerTypes(appService, site, "YPE1");
        Assert.assertEquals(type1Size, sampleTypesFound.size());

        sampleTypesFound = SampleTypeWrapper.getSampleTypeForContainerTypes(
            appService, site, "YPE2");
        Assert.assertEquals(type2Size, sampleTypesFound.size());

        sampleTypesFound = SampleTypeWrapper.getSampleTypeForContainerTypes(
            appService, site, "YPE");
        // We've got a sample type in 2 different container. The method return a
        // set, so we have only one occurrence of this sample type
        Assert.assertEquals(type1Size + type2Size - 1, sampleTypesFound.size());
    }

    @Test
    public void testGetGlobalSampleTypes() throws Exception {
        List<SampleTypeWrapper> types = SampleTypeWrapper.getGlobalSampleTypes(
            appService, false);
        int startSize = types.size();

        String name = "testGetGlobalSampleTypes" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeHelper.addSampleType(site, name);
        Assert.assertEquals(startSize, SampleTypeWrapper.getGlobalSampleTypes(
            appService, false).size());

        SampleTypeHelper.addSampleType(null, name);
        Assert.assertEquals(startSize + 1, SampleTypeWrapper
            .getGlobalSampleTypes(appService, false).size());

    }

    @Test
    public void testPersistGlobalSampleTypes() throws Exception {
        List<SampleTypeWrapper> types = SampleTypeWrapper.getGlobalSampleTypes(
            appService, false);
        int startSize = types.size();

        String name = "testPersistGlobalSampleTypes" + r.nextInt();
        SampleTypeWrapper type = SampleTypeHelper.newSampleType(null, name);
        types.add(type);
        SampleTypeWrapper.persistGlobalSampleTypes(appService, types);
        Assert.assertEquals(startSize + 1, SampleTypeWrapper
            .getGlobalSampleTypes(appService, false).size());

        type = DbHelper.chooseRandomlyInList(types);
        types.remove(type);
        SampleTypeWrapper.persistGlobalSampleTypes(appService, types);
        Assert.assertEquals(startSize, SampleTypeWrapper.getGlobalSampleTypes(
            appService, false).size());

    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        int oldTotal = appService.search(SampleType.class, new SampleType())
            .size();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeHelper.addSampleType(site, name);

        int newTotal = appService.search(SampleType.class, new SampleType())
            .size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(site, name,
            false);

        // object is in database
        SampleType typeInDB = ModelUtils.getObjectWithId(appService,
            SampleType.class, type.getId());
        Assert.assertNotNull(typeInDB);

        type.delete();

        typeInDB = ModelUtils.getObjectWithId(appService, SampleType.class,
            type.getId());
        // object is not anymore in database
        Assert.assertNull(typeInDB);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(site, name);
        type.reload();
        String oldName = type.getName();
        type.setName("toto");
        type.reset();
        Assert.assertEquals(oldName, type.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeWrapper type = SampleTypeHelper.newSampleType(site, name);
        type.reset();
        Assert.assertEquals(null, type.getName());
    }

}
