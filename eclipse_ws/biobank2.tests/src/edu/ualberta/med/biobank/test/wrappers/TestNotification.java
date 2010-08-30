package edu.ualberta.med.biobank.test.wrappers;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.NotificationWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Notification;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.NotificationHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;

public class TestNotification extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        NotificationWrapper notification = NotificationHelper.addNotification(
            site, Utils.getRandomDate(), name);
        testGettersAndSetters(notification);
    }

    @Test
    public void testCompatreTo() throws Exception {
        String name = "testGettersAndSetters" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        NotificationWrapper notification1 = NotificationHelper.addNotification(
            site, DateFormatter.dateFormatter.parse("2010-02-01 23:00"), name);

        NotificationWrapper notification2 = NotificationHelper.addNotification(
            site, DateFormatter.dateFormatter.parse("2009-12-01 23:00"), name);

        Assert.assertTrue(notification1.compareTo(notification2) > 0);
        Assert.assertTrue(notification2.compareTo(notification1) < 0);

        Assert.assertTrue(notification1.compareTo(null) == 0);
        Assert.assertTrue(notification2.compareTo(null) == 0);
    }

    @Test
    public void testReset() throws Exception {
        String name = "testReset" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);

        // test reset for a new object
        NotificationWrapper notification = NotificationHelper.newNotification(
            site, Utils.getRandomDate(), name);
        notification.reset();
        Assert.assertEquals(null, notification.getDateSent());

        // test reset for an object already in database
        notification = NotificationHelper.addNotification(site,
            Utils.getRandomDate(), name);
        notification.reset();
        notification.setMessage(Utils.getRandomString(32));
        Assert.assertEquals(name, notification.getMessage());
    }

    @Test
    public void testReload() throws Exception {
        String name = "testReload" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        NotificationWrapper notification = NotificationHelper.addNotification(
            site, Utils.getRandomDate(), name);

        try {
            notification.reload();
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("cannot reload notification wrapper");
        }
    }

    @Test
    public void testGetWrappedClass() throws Exception {
        NotificationWrapper notification = NotificationHelper.newNotification(
            null, null, null);
        Assert.assertEquals(Notification.class, notification.getWrappedClass());
    }

    @Test
    public void testDelete() throws Exception {
        String name = "testDelete" + r.nextInt();
        SiteWrapper site = SiteHelper.addSite(name);
        NotificationWrapper notification = NotificationHelper.addNotification(
            site, Utils.getRandomDate(), name);

        int countBefore = appService.search(Notification.class,
            new Notification()).size();

        notification.delete();

        int countAfter = appService.search(Notification.class,
            new Notification()).size();

        Assert.assertEquals(countBefore - 1, countAfter);
    }

    @Test
    public void testPersist() throws Exception {
        String name = "testPersist" + r.nextInt();
    }
}
