package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;

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
    public void testConstructors() throws Exception {
        new ActivityStatusWrapper(appService);
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Active");
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
        int before = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        ActivityStatusWrapper as = new ActivityStatusWrapper(appService);
        as.setName(name);
        as.persist();
        addedstatus.add(as);
        int after = ActivityStatusWrapper.getAllActivityStatuses(appService)
            .size();
        Assert.assertEquals(before + 1, after);

        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, name);

        // FIXME delete should test we can't remove a used activity status ?
        try {
            activeAs.delete();
            Assert.fail("should not be allowed to delete activity status");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Active");
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

        ActivityStatusWrapper.persistActivityStatuses(toAdd, toDelete);

        // now delete the ones previously added and add the new ones
        statuses = ActivityStatusWrapper.getAllActivityStatuses(appService);
        after = statuses.size();
        Assert.assertEquals(before - 5 + 3, after);
        Assert.assertTrue(statuses.containsAll(toAdd));
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
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetName() throws Exception {
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Active");
        Assert.assertEquals("Active", activeAs.getName());
    }

    @Test
    public void testCompareTo() throws Exception {
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Active");
        ActivityStatusWrapper closedAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Closed");

        Assert.assertTrue(activeAs.compareTo(closedAs) < 0);
        Assert.assertTrue(closedAs.compareTo(activeAs) > 0);
    }

    @Test
    public void testGetAllActivityStatuses() throws Exception {
        Collection<ActivityStatusWrapper> list = ActivityStatusWrapper
            .getAllActivityStatuses(appService);
        Assert.assertTrue(list.size() == 4);

        List<String> names = new ArrayList<String>();
        for (ActivityStatusWrapper as : list) {
            names.add(as.getName());
        }
        Assert.assertTrue(names.contains("Active"));
        Assert.assertTrue(names.contains("Closed"));
        Assert.assertTrue(names.contains("Disabled"));
        Assert.assertTrue(names.contains("Flagged"));

        // invoke one more time to make sure a database access is not made
        list = ActivityStatusWrapper.getAllActivityStatuses(appService);
    }

    @Test
    public void testGetActivityStatus() throws Exception {
        ActivityStatusWrapper.getActivityStatus(appService, "Active");

        try {
            ActivityStatusWrapper.getActivityStatus(appService,
                Utils.getRandomString(15, 20));
            Assert
                .fail("should not be allowed to retreive invalid activity status");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }
}
