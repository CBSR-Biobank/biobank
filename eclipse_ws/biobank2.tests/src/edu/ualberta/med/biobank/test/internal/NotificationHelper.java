package edu.ualberta.med.biobank.test.internal;

import java.util.Date;

import edu.ualberta.med.biobank.common.wrappers.NotificationWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class NotificationHelper extends DbHelper {

    public static NotificationWrapper newNotification(SiteWrapper site,
        Date dateSent, String message) throws Exception {
        NotificationWrapper notification = new NotificationWrapper(appService);
        notification.setDateSent(dateSent);
        notification.setMessage(message);
        notification.setSite(site);
        return notification;
    }

    public static NotificationWrapper addNotification(SiteWrapper site,
        Date dateSent, String message) throws Exception {
        NotificationWrapper notification = newNotification(site, dateSent,
            message);
        return notification;
    }

}
