package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.NullPropertyException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.AliquotedSpecimenHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SourceSpecimenHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

@Deprecated
public class TestSpecimenType extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SpecimenTypeWrapper type = SpecimenTypeHelper.addSpecimenType(name);

        testGettersAndSetters(type);
    }

    @Test
    public void testGetContainerTypeCollection() throws Exception {
        String name = "testGetContainerTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        SpecimenTypeWrapper type = SpecimenTypeHelper.addSpecimenType(name);
        List<SpecimenTypeWrapper> sampleTypes =
            new ArrayList<SpecimenTypeWrapper>();
        sampleTypes.add(type);
        int containerTypeNber = 10;
        ContainerTypeHelper.addContainerTypesRandom(site, name,
            containerTypeNber);
        int nber = r.nextInt(containerTypeNber) + 1;
        List<ContainerTypeWrapper> containerTypes = site
            .getContainerTypeCollection(false);
        for (int i = 0; i < nber; i++) {
            ContainerTypeWrapper containerType = containerTypes.get(i);
            containerType.addToSpecimenTypeCollection(sampleTypes);
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
        SpecimenTypeWrapper type = SpecimenTypeHelper.addSpecimenType(name);
        List<SpecimenTypeWrapper> sampleTypes =
            new ArrayList<SpecimenTypeWrapper>();
        sampleTypes.add(type);
        int containerTypeNber = 10;
        ContainerTypeHelper.addContainerTypesRandom(site, name,
            containerTypeNber);
        int nber = r.nextInt(containerTypeNber) + 1;
        List<ContainerTypeWrapper> containerTypes = site
            .getContainerTypeCollection(false);
        for (int i = 0; i < nber; i++) {
            ContainerTypeWrapper containerType = containerTypes.get(i);
            containerType.addToSpecimenTypeCollection(sampleTypes);
            containerType.persist();
        }

        type.reload();
        List<ContainerTypeWrapper> containerTypesSorted = type
            .getContainerTypeCollection(true);
        if (containerTypesSorted.size() > 1) {
            for (int i = 0; i < (containerTypesSorted.size() - 1); i++) {
                ContainerTypeWrapper cType1 = containerTypesSorted.get(i);
                ContainerTypeWrapper cType2 = containerTypesSorted.get(i + 1);
                Assert.assertTrue(cType1.compareTo(cType2) <= 0);
            }
        }
    }

    @Test
    public void testGetSpecimenTypeForContainerTypes() throws Exception {
        String name = "testGetContainerTypeCollection" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        ContainerTypeWrapper containerType1 = ContainerTypeHelper
            .addContainerTypeRandom(site, "TYPE1");
        ContainerTypeWrapper containerType2 = ContainerTypeHelper
            .addContainerTypeRandom(site, "TYPE2");

        List<SpecimenTypeWrapper> sampleTypes1 =
            new ArrayList<SpecimenTypeWrapper>();
        List<SpecimenTypeWrapper> sampleTypes2 =
            new ArrayList<SpecimenTypeWrapper>();
        SpecimenTypeWrapper sampleType1 = SpecimenTypeHelper
            .addSpecimenType("ST1");
        sampleTypes1.add(sampleType1);
        SpecimenTypeWrapper sampleType2 = SpecimenTypeHelper
            .addSpecimenType("ST2");
        sampleTypes1.add(sampleType2);
        sampleTypes2.add(sampleType2);
        SpecimenTypeWrapper sampleType3 = SpecimenTypeHelper
            .addSpecimenType("ST3");
        sampleTypes1.add(sampleType3);
        SpecimenTypeWrapper sampleType4 = SpecimenTypeHelper
            .addSpecimenType("ST4");
        sampleTypes2.add(sampleType4);
        containerType1.addToSpecimenTypeCollection(sampleTypes1);
        int type1Size = sampleTypes1.size();
        containerType1.persist();
        containerType2.addToSpecimenTypeCollection(sampleTypes2);
        int type2Size = sampleTypes2.size();
        containerType2.persist();

        List<SpecimenTypeWrapper> sampleTypesFound = SpecimenTypeWrapper
            .getSpecimenTypeForContainerTypes(appService, site, "YPE1");
        Assert.assertEquals(type1Size, sampleTypesFound.size());

        sampleTypesFound = SpecimenTypeWrapper
            .getSpecimenTypeForContainerTypes(appService, site, "YPE2");
        Assert.assertEquals(type2Size, sampleTypesFound.size());

        sampleTypesFound = SpecimenTypeWrapper
            .getSpecimenTypeForContainerTypes(appService, site, "YPE");
        // We've got a sample type in 2 different container. The method return a
        // set, so we have only one occurrence of this sample type
        Assert.assertEquals((type1Size + type2Size) - 1,
            sampleTypesFound.size());
    }

    @Test
    public void testGetSpecimenTypes() throws Exception {
        int startSize = SpecimenTypeWrapper.getAllSpecimenTypes(appService,
            false).size();

        String name = "testGetSpecimenTypes" + r.nextInt();
        SpecimenTypeHelper.addSpecimenType(name);
        Assert.assertEquals(startSize + 1, SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false).size());

        SpecimenTypeHelper.addSpecimenType(name + "_2");
        Assert.assertEquals(startSize + 2, SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false).size());

        SpecimenTypeHelper.addSpecimenType("QWERTY" + name);
        SpecimenTypeHelper.addSpecimenType("ASDFG" + name);
        List<SpecimenTypeWrapper> types = SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, true);
        if (types.size() > 1) {
            for (int i = 0; i < (types.size() - 1); i++) {
                SpecimenTypeWrapper cType1 = types.get(i);
                SpecimenTypeWrapper cType2 = types.get(i + 1);
                Assert.assertTrue(cType1.compareTo(cType2) <= 0);
            }
        }
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
        int oldTotal = appService
            .search(SpecimenType.class, new SpecimenType()).size();
        SpecimenTypeHelper.addSpecimenType(name);

        int newTotal = appService
            .search(SpecimenType.class, new SpecimenType()).size();
        Assert.assertEquals(oldTotal + 1, newTotal);
    }

    @Test
    public void testPersistFailNoName() throws Exception {
        String name = "testPersistFailNoName" + r.nextInt();
        SpecimenTypeWrapper type = SpecimenTypeHelper.newSpecimenType(name);
        type.setName(null);
        try {
            type.persist();
            Assert.fail("name should be set");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        type.setName(name);
        type.persist();
        SpecimenTypeHelper.createdSpecimenTypes.add(type);
    }

    @Test
    public void testPersistFailNoNameShort() throws Exception {
        String name = "testPersistFailNoNameShort" + r.nextInt();
        SpecimenTypeWrapper type = SpecimenTypeHelper.newSpecimenType(name);
        type.setNameShort(null);
        try {
            type.persist();
            Assert.fail("nameshort should be set");
        } catch (NullPropertyException e) {
            Assert.assertTrue(true);
        }

        type.setNameShort(name);
        type.persist();
        SpecimenTypeHelper.createdSpecimenTypes.add(type);
    }

    @Test
    public void testPersistFailNameUnique() throws Exception {
        String name = "testPersistFailNameUnique" + r.nextInt();
        SpecimenTypeHelper.addSpecimenType(name);

        SpecimenTypeWrapper type = SpecimenTypeHelper.newSpecimenType(name
            + "_2");
        type.setName(name);
        try {
            type.persist();
            Assert.fail("name should be unique");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }

        type.setName(name + "_2");
        type.persist();
        SpecimenTypeHelper.createdSpecimenTypes.add(type);
    }

    @Test
    public void testPersistFailNameShortUnique() throws Exception {
        String name = "testPersistFailNameShortUnique" + r.nextInt();
        SpecimenTypeHelper.addSpecimenType(name);

        SpecimenTypeWrapper type = SpecimenTypeHelper.newSpecimenType(name);
        type.setName(name + "_2");
        try {
            type.persist();
            Assert.fail("name short should be unique");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }

        type.setNameShort(name + "_2");
        type.persist();
        SpecimenTypeHelper.createdSpecimenTypes.add(type);
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SpecimenTypeWrapper type = SpecimenTypeHelper.addSpecimenType(name,
            false);

        // object is in database
        SpecimenType typeInDB = ModelUtils.getObjectWithId(appService,
            SpecimenType.class, type.getId());
        Assert.assertNotNull(typeInDB);

        Integer id = type.getId();

        type.delete();

        typeInDB = ModelUtils.getObjectWithId(appService, SpecimenType.class,
            id);
        // object is not anymore in database
        Assert.assertNull(typeInDB);
    }

    @Test
    public void testDeleteUsedInSourceSpecimen() throws Exception {
        String name = "testDeleteUsedInSourceSpecimen" + r.nextInt();
        SpecimenTypeWrapper type = SpecimenTypeHelper.addSpecimenType(name,
            false);

        StudyWrapper study = StudyHelper.addStudy(name);

        // test used with Source Specimen
        SourceSpecimenWrapper ssw = SourceSpecimenHelper.addSourceSpecimen(
            study, type, true, true);
        type.reload();
        try {
            type.delete();
            Assert
                .fail("Should not be able to delete it - deleteCheck should fail");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        ssw.delete();
        // can remove once it is not used anymore
        type.reload();
        type.delete();
    }

    @Test
    public void testDeleteUsedInAliquotedSpecimen() throws Exception {
        String name = "testDeleteUsedInAliquotedSpecimen" + r.nextInt();
        SpecimenTypeWrapper type = SpecimenTypeHelper.addSpecimenType(name,
            false);

        StudyWrapper study = StudyHelper.addStudy(name);

        // test used with Aliquoted Specimen
        AliquotedSpecimenWrapper asw = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, type);
        type.reload();
        try {
            type.delete();
            Assert
                .fail("Should not be able to delete it - deleteCheck should fail");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        asw.delete();
        // can remove once it is not used anymore
        type.reload();
        type.delete();
    }

    @Test
    public void testDeleteUsedInSpecimen() throws Exception {
        String name = "testDeleteUsedInSpecimen" + r.nextInt();
        SpecimenTypeWrapper type = SpecimenTypeHelper.addSpecimenType(name,
            false);

        ClinicWrapper clinic = ClinicHelper.addClinic(name);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        StudyWrapper study = StudyHelper.newStudy(name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        StudyHelper.createdStudies.add(study);
        PatientWrapper patient = PatientHelper.addPatient(name, study);

        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(clinic, patient, 1,
                SpecimenHelper.newSpecimen(type));

        // test used with Specimen
        type.reload();
        try {
            type.delete();
            Assert
                .fail("Should not be able to delete it - deleteCheck should fail");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

        cevent.getOriginalSpecimenCollection(false).get(0).delete();
        // can remove once it is not used anymore
        type.reload();
        type.delete();
    }

    @Test
    public void testDeleteHasChildren() throws Exception {
        String name = "testDeleteHasChildren" + r.nextInt();
        SpecimenTypeWrapper parent = SpecimenTypeHelper.addSpecimenType(name
            + "_parent", false);
        SpecimenTypeWrapper child = SpecimenTypeHelper.addSpecimenType(name
            + "_child", true);

        parent.addToChildSpecimenTypeCollection(Arrays.asList(child));
        parent.persist();

        child.reload();
        parent.reload();
        Integer parentId = parent.getId();
        Integer childId = child.getId();
        parent.delete();

        // parent should be deleted but not the child
        SpecimenType parentInDB = ModelUtils.getObjectWithId(appService,
            SpecimenType.class, parentId);
        Assert.assertNull(parentInDB);
        SpecimenType childInDB = ModelUtils.getObjectWithId(appService,
            SpecimenType.class, childId);
        Assert.assertNotNull(childInDB);
    }

    @Test
    public void testDeleteHasParent() throws Exception {
        String name = "testDeleteHasParent" + r.nextInt();
        SpecimenTypeWrapper parent = SpecimenTypeHelper.addSpecimenType(name
            + "_parent", true);
        SpecimenTypeWrapper child = SpecimenTypeHelper.addSpecimenType(name
            + "_child", false);

        parent.addToChildSpecimenTypeCollection(Arrays.asList(child));
        parent.persist();

        child.reload();
        parent.reload();
        Integer parentId = parent.getId();
        Integer childId = child.getId();
        child.delete();

        // child should be deleted but not the parent
        SpecimenType parentInDB = ModelUtils.getObjectWithId(appService,
            SpecimenType.class, parentId);
        Assert.assertNotNull(parentInDB);
        SpecimenType childInDB = ModelUtils.getObjectWithId(appService,
            SpecimenType.class, childId);
        Assert.assertNull(childInDB);
    }

    @Test
    public void testResetAlreadyInDatabase() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SpecimenTypeWrapper type = SpecimenTypeHelper.addSpecimenType(name);
        type.reload();
        String oldName = type.getName();
        type.setName("toto");
        type.reset();
        Assert.assertEquals(oldName, type.getName());
    }

    @Test
    public void testResetNew() throws Exception {
        String name = "testResetAlreadyInDatabase" + r.nextInt();
        SpecimenTypeWrapper type = SpecimenTypeHelper.newSpecimenType(name);
        type.reset();
        Assert.assertEquals(null, type.getName());
    }

    @Test
    public void testCompareTo() throws Exception {
        String name = "testCompareTo" + r.nextInt();
        SpecimenTypeWrapper type = SpecimenTypeHelper.addSpecimenType("QWERTY"
            + name);
        SpecimenTypeWrapper type2 = SpecimenTypeHelper.addSpecimenType("ASDFG"
            + name);
        Assert.assertTrue(type.compareTo(type2) > 0);
        Assert.assertTrue(type2.compareTo(type) < 0);
    }
}
