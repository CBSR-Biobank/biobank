package edu.ualberta.med.biobank.test.wrappers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankFailedQueryException;
import edu.ualberta.med.biobank.common.util.ClassUtils;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyEventAttrWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.AliquotedSpecimenHelper;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.ProcessingEventHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestActivityStatus extends TestDatabase {

    private List<ActivityStatusWrapper> addedstatus = new ArrayList<ActivityStatusWrapper>();

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        for (ActivityStatusWrapper a : addedstatus) {
            a.delete();
        }
    }

    @Test
    public void testGettersAndSetters() throws BiobankCheckException, Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        ActivityStatusWrapper as = new ActivityStatusWrapper(appService);
        as.setName(name);
        as.persist();
        addedstatus.add(as);
        testGettersAndSetters(as);
    }

    @Test
    public void testConstructors() throws Exception {
        new ActivityStatusWrapper(appService);
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActiveActivityStatus(appService);
        new ActivityStatusWrapper(appService, activeAs.getWrappedObject());
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        ActivityStatusWrapper as = new ActivityStatusWrapper(appService);
        as.setName(name);
        as.persist();
        int before = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        as.delete();
        List<ActivityStatusWrapper> allActivityStatuses = ActivityStatusWrapper
            .getAllActivityStatuses(appService);
        int after = allActivityStatuses.size();
        Assert.assertEquals(before - 1, after);
        Assert.assertFalse(allActivityStatuses.contains(as));

    }

    @Test
    public void testDeleteFail() throws Exception {
        String name = "testDeleteFail" + r.nextInt();

        // should not be allowed to remove an activity status that is used

        SiteWrapper site = SiteHelper.addSite("center" + name, false);
        ContainerWrapper topContainer = ContainerHelper.addTopContainerRandom(
            site, name, 2, 2);
        ContainerTypeWrapper topContainerType = topContainer.getContainerType();
        topContainerType.addToSpecimenTypeCollection(SpecimenTypeWrapper
            .getAllSpecimenTypes(appService, false));
        topContainerType.persist();

        ClinicWrapper clinic = ClinicHelper.addClinic("clinic" + name, false,
            true);
        ContactWrapper contact = ContactHelper.addContact(clinic, name);

        // Study
        StudyWrapper study = StudyHelper.newStudy("study" + name);
        // StudyEventAttr
        study.setStudyEventAttr("worksheet", EventAttrTypeEnum.TEXT);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
        StudyEventAttrWrapper studyEventAttr = StudyEventAttrWrapper
            .getStudyEventAttrCollection(study).get(0);
        // AliquotedSpecimen
        AliquotedSpecimenWrapper aliquotedSpecimenType = AliquotedSpecimenHelper
            .addAliquotedSpecimen(study, SpecimenTypeWrapper
                .getAllSpecimenTypes(appService, false).get(0));

        PatientWrapper patient = PatientHelper.addPatient(name, study);
        // Specimen
        SpecimenWrapper originSpecimen = SpecimenHelper
            .newSpecimen(SpecimenTypeHelper.addSpecimenType(name));

        // Collection Event
        CollectionEventWrapper cevent = CollectionEventHelper
            .addCollectionEvent(site, patient, 1, originSpecimen);
        originSpecimen = cevent.getOriginalSpecimenCollection(false).get(0);

        // ProcessingEvent
        ProcessingEventWrapper pevent = ProcessingEventHelper
            .addProcessingEvent(site, Utils.getRandomDate());

        ModelWrapper<?>[] wrappers = new ModelWrapper<?>[] { originSpecimen,
            studyEventAttr, topContainer, topContainerType, cevent,
            aliquotedSpecimenType, pevent, study, site };
        for (ModelWrapper<?> wrapper : wrappers) {
            try {
                testDeleteFail(wrapper,
                    name + ClassUtils.getClassName(wrapper.getClass()));
            } catch (Exception e) {
                throw e;
            }

        }
    }

    private void testDeleteFail(ModelWrapper<?> wrapper, String asName)
        throws Exception {
        wrapper.reload();
        ActivityStatusWrapper as = new ActivityStatusWrapper(appService);
        as.setName(asName);
        as.persist();
        as.reload();

        try {
            Method setActivityMethod = wrapper.getClass().getMethod(
                "setActivityStatus", ActivityStatusWrapper.class);
            setActivityMethod.invoke(wrapper, as);
        } catch (NoSuchMethodException e) {
            return;
        }
        wrapper.persist();
        wrapper.reload();

        try {
            as.delete();
            Assert.fail("should not be allowed to delete activity status");
        } catch (BiobankDeleteException bce) {
            Assert.assertTrue(true);
        }

        if (wrapper instanceof StudyWrapper)
            StudyHelper.deleteStudyDependencies();
        else if (wrapper instanceof SiteWrapper) {
            SiteHelper.deleteSiteAndDependencies(((SiteWrapper) wrapper));
        } else {
            wrapper.delete();
        }

        try {
            as.delete();
        } catch (Exception e) {
            Assert
                .fail("object deleted, should be allowed to delete activity status");
        }
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActiveActivityStatus(appService);
        Assert.assertEquals(ActivityStatus.class, activeAs.getWrappedClass());
    }

    @Test
    public void testPersist() throws Exception {
        int before = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        String name = "testPersist" + r.nextInt();
        ActivityStatusWrapper as = new ActivityStatusWrapper(appService);
        as.setName(name);
        as.persist();
        addedstatus.add(as);

        List<ActivityStatusWrapper> statuses = ActivityStatusWrapper
            .getAllActivityStatuses(appService);
        int after = statuses.size();
        Assert.assertEquals(before + 1, after);
        Assert.assertTrue(statuses.contains(as));

        // add 5 activity status that will eventually be deleted
        before = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        List<ActivityStatusWrapper> toDelete = new ArrayList<ActivityStatusWrapper>();
        for (int i = 0; i < 5; ++i) {
            name = "testPersist" + i + r.nextInt();
            as = new ActivityStatusWrapper(appService);
            as.setName(name);
            as.persist();
            as.reload();
            toDelete.add(as);
        }

        statuses = ActivityStatusWrapper.getAllActivityStatuses(appService);
        after = statuses.size();
        Assert.assertEquals(before + 5, after);
        Assert.assertTrue(statuses.containsAll(toDelete));

        // create 3 new activity statuses
        before = after;
        List<ActivityStatusWrapper> toAdd = new ArrayList<ActivityStatusWrapper>();
        for (int i = 0; i < 3; ++i) {
            name = "testPersist" + i + r.nextInt();
            as = new ActivityStatusWrapper(appService);
            as.setName(name);
            toAdd.add(as);
        }

        // now delete the ones previously added and add the new ones
        ActivityStatusWrapper.persistActivityStatuses(toAdd, toDelete);

        statuses = ActivityStatusWrapper.getAllActivityStatuses(appService);
        after = statuses.size();
        Assert.assertEquals((before - 5) + 3, after);
        Assert.assertTrue(statuses.containsAll(toAdd));

        addedstatus.addAll(toAdd);
    }

    @Test
    public void testPersistFail() throws Exception {
        String name = "testPersistFail" + r.nextInt();
        int before = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        ActivityStatusWrapper as = new ActivityStatusWrapper(appService);
        as.setName(name);
        as.persist();
        addedstatus.add(as);
        int after = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        Assert.assertEquals(before + 1, after);

        ActivityStatusWrapper newAs = new ActivityStatusWrapper(appService);
        newAs.setName(name);
        try {
            newAs.persist();
            Assert.fail("Cannot have 2 statuses with same name");
            addedstatus.add(newAs);
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCompareTo() throws Exception {
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActiveActivityStatus(appService);
        ActivityStatusWrapper closedAs = ActivityStatusWrapper
            .getActivityStatus(appService,
                ActivityStatusWrapper.CLOSED_STATUS_STRING);

        Assert.assertTrue(activeAs.compareTo(closedAs) < 0);
        Assert.assertTrue(closedAs.compareTo(activeAs) > 0);
    }

    @Test
    public void testGetAllActivityStatuses() throws Exception {
        Collection<ActivityStatusWrapper> list = ActivityStatusWrapper
            .getAllActivityStatuses(appService);
        Assert.assertTrue(list.size() >= 3);

        List<String> names = new ArrayList<String>();
        for (ActivityStatusWrapper as : list) {
            names.add(as.getName());
        }
        Assert.assertTrue(names
            .contains(ActivityStatusWrapper.ACTIVE_STATUS_STRING));
        Assert.assertTrue(names
            .contains(ActivityStatusWrapper.CLOSED_STATUS_STRING));
        Assert.assertTrue(names
            .contains(ActivityStatusWrapper.FLAGGED_STATUS_STRING));
    }

    @Test
    public void testGetActivityStatus() throws Exception {
        // success
        ActivityStatusWrapper.getActiveActivityStatus(appService);

        // fail
        try {
            ActivityStatusWrapper.getActivityStatus(appService,
                Utils.getRandomString(15, 20));
            Assert
                .fail("should not be allowed to retreive invalid activity status");
        } catch (BiobankFailedQueryException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testActiveClosedFlagged() throws Exception {
        ActivityStatusWrapper active = ActivityStatusWrapper
            .getActiveActivityStatus(appService);
        Assert.assertTrue(active.isActive());
        Assert.assertFalse(active.isClosed());
        Assert.assertFalse(active.isFlagged());

        ActivityStatusWrapper closed = ActivityStatusWrapper.getActivityStatus(
            appService, ActivityStatusWrapper.CLOSED_STATUS_STRING);
        Assert.assertTrue(closed.isClosed());
        Assert.assertFalse(closed.isActive());
        Assert.assertFalse(closed.isFlagged());

        ActivityStatusWrapper flagged = ActivityStatusWrapper
            .getActivityStatus(appService,
                ActivityStatusWrapper.FLAGGED_STATUS_STRING);
        Assert.assertTrue(flagged.isFlagged());
        Assert.assertFalse(flagged.isClosed());
        Assert.assertFalse(flagged.isActive());
    }
}
