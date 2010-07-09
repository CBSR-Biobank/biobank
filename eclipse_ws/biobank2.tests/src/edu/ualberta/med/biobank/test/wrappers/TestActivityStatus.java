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

    @Test
    public void testConstructors() throws Exception {
        new ActivityStatusWrapper(appService);
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Active");
        new ActivityStatusWrapper(appService, activeAs.getWrappedObject());
    }

    @Test
    public void testDelete() throws Exception {
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Active");

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
        ActivityStatusWrapper activeAs = ActivityStatusWrapper
            .getActivityStatus(appService, "Active");
        ActivityStatusWrapper as = new ActivityStatusWrapper(appService,
            activeAs.getWrappedObject());

        try {
            as.persist();
            Assert.fail("should not be allowed to add objects to the database");
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
            ActivityStatusWrapper.getActivityStatus(appService, Utils
                .getRandomString(15, 20));
            Assert
                .fail("should not be allowed to retreive invalid activity status");
        } catch (BiobankCheckException bce) {
            Assert.assertTrue(true);
        }
    }
}
