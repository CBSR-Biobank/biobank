package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.DuplicateEntryException;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValidationException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.SampleTypeHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;

public class TestSampleType extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(name);

        testGettersAndSetters(type);
    }

    @Test
    public void testGetContainerTypeCollection() throws Exception {
        String name = "testGetContainerTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(name);
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
            containerType.addToSampleTypeCollection(sampleTypes);
            containerType.persist();
        }

        type.reload();
        Assert
            .assertEquals(nber, type.getContainerTypeCollection(false).size());
    }

    @Test
    public void testGetContainerTypeCollectionBoolean() throws Exception {
        String name = "testGetContainerTypeCollectionBoolean" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(name);
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
            containerType.addToSampleTypeCollection(sampleTypes);
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
        SampleTypeWrapper sampleType1 = SampleTypeHelper.addSampleType("ST1");
        sampleTypes1.add(sampleType1);
        SampleTypeWrapper sampleType2 = SampleTypeHelper.addSampleType("ST2");
        sampleTypes1.add(sampleType2);
        sampleTypes2.add(sampleType2);
        SampleTypeWrapper sampleType3 = SampleTypeHelper.addSampleType("ST3");
        sampleTypes1.add(sampleType3);
        SampleTypeWrapper sampleType4 = SampleTypeHelper.addSampleType("ST4");
        sampleTypes2.add(sampleType4);
        containerType1.addToSampleTypeCollection(sampleTypes1);
        int type1Size = sampleTypes1.size();
        containerType1.persist();
        containerType2.addToSampleTypeCollection(sampleTypes2);
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
    public void testGetSampleTypes() throws Exception {
        int startSize = SampleTypeWrapper.getAllSampleTypes(appService, false)
            .size();

        String name = "testGetSampleTypes" + r.nextInt();
        SampleTypeHelper.addSampleType(name);
        Assert.assertEquals(startSize + 1,
            SampleTypeWrapper.getAllSampleTypes(appService, false).size());

        SampleTypeHelper.addSampleType(name + "_2");
        Assert.assertEquals(startSize + 2,
            SampleTypeWrapper.getAllSampleTypes(appService, false).size());

        SampleTypeHelper.addSampleType("QWERTY" + name);
        SampleTypeHelper.addSampleType("ASDFG" + name);
        List<SampleTypeWrapper> types = SampleTypeWrapper.getAllSampleTypes(
            appService, true);
        if (types.size() > 1) {
            for (int i = 0; i < types.size() - 1; i++) {
                SampleTypeWrapper cType1 = types.get(i);
                SampleTypeWrapper cType2 = types.get(i + 1);
                Assert.assertTrue(cType1.compareTo(cType2) <= 0);
            }
        }
    }

    @Test
    public void testPersistSampleTypes() throws Exception {
        List<SampleTypeWrapper> types = SampleTypeWrapper.getAllSampleTypes(
            appService, false);
        int startSize = types.size();

        String name = "testPersistSampleTypes" + r.nextInt();
        SampleTypeWrapper type = SampleTypeHelper.newSampleType(name);
        SampleTypeWrapper.persistSampleTypes(Arrays.asList(type), null);
        Assert.assertEquals(startSize + 1,
            SampleTypeWrapper.getAllSampleTypes(appService, false).size());

        SampleTypeWrapper.persistSampleTypes(null, Arrays.asList(type));
        Assert.assertEquals(startSize,
            SampleTypeWrapper.getAllSampleTypes(appService, false).size());
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        int oldTotal = appService.search(SampleType.class, new SampleType())
            .size();
        SampleTypeHelper.addSampleType(name);

        int newTotal = appService.search(SampleType.class, new SampleType())
            .size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testPersistFailNoName() throws Exception {
        String name = "testPersistFailNoName" + r.nextInt();
        SampleTypeWrapper type = SampleTypeHelper.newSampleType(name);
        type.setName(null);
        try {
            type.persist();
            Assert.fail("name should be set");
        } catch (ValueNotSetException e) {
            Assert.assertTrue(true);
        } catch (ValidationException e) {
            Assert.assertTrue(true);
        }

        type.setName(name);
        type.persist();
        SampleTypeHelper.createdSampleTypes.add(type);
    }

    @Test
    public void testPersistFailNoNameShort() throws Exception {
        String name = "testPersistFailNoNameShort" + r.nextInt();
        SampleTypeWrapper type = SampleTypeHelper.newSampleType(name);
        type.setNameShort(null);
        try {
            type.persist();
            Assert.fail("nameshort should be set");
        } catch (ValueNotSetException e) {
            Assert.assertTrue(true);
        } catch (ValidationException e) {
            Assert.assertTrue(true);
        }

        type.setNameShort(name);
        type.persist();
        SampleTypeHelper.createdSampleTypes.add(type);
    }

    @Test
    public void testPersistFailNameUnique() throws Exception {
        String name = "testPersistFailNameUnique" + r.nextInt();
        SampleTypeHelper.addSampleType(name);

        SampleTypeWrapper type = SampleTypeHelper.newSampleType(name + "_2");
        type.setName(name);
        try {
            type.persist();
            Assert.fail("name should be unique");
        } catch (DuplicateEntryException e) {
            Assert.assertTrue(true);
        }

        type.setName(name + "_2");
        type.persist();
        SampleTypeHelper.createdSampleTypes.add(type);
    }

    @Test
    public void testPersistFailNameShortUnique() throws Exception {
        String name = "testPersistFailNameShortUnique" + r.nextInt();
        SampleTypeHelper.addSampleType(name);

        SampleTypeWrapper type = SampleTypeHelper.newSampleType(name);
        type.setName(name + "_2");
        try {
            type.persist();
            Assert.fail("name short should be unique");
        } catch (DuplicateEntryException e) {
            Assert.assertTrue(true);
        }

        type.setNameShort(name + "_2");
        type.persist();
        SampleTypeHelper.createdSampleTypes.add(type);
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(name, false);

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
        SampleTypeWrapper type = SampleTypeHelper.addSampleType(name);
        type.reload();
        String oldName = type.getName();
        type.setName("toto");
        type.reset();
        Assert.assertEquals(oldName, type.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SampleTypeWrapper type = SampleTypeHelper.newSampleType(name);
        type.reset();
        Assert.assertEquals(null, type.getName());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SampleTypeWrapper type = SampleTypeHelper
            .addSampleType("QWERTY" + name);
        SampleTypeWrapper type2 = SampleTypeHelper
            .addSampleType("ASDFG" + name);
        Assert.assertTrue(type.compareTo(type2) > 0);
        Assert.assertTrue(type2.compareTo(type) < 0);
    }
}
