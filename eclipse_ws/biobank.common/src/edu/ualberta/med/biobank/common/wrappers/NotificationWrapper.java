package edu.ualberta.med.biobank.common.wrappers;

import java.util.Date;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.Notification;
import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class NotificationWrapper extends ModelWrapper<Notification> {

    private SiteWrapper site;

    public NotificationWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public NotificationWrapper(WritableApplicationService appService,
        Notification wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public int compareTo(ModelWrapper<Notification> wrapper) {
        if (wrapper instanceof NotificationWrapper) {
            Date v1Date = wrappedObject.getDateSent();
            Date v2Date = wrapper.wrappedObject.getDateSent();
            if (v1Date != null && v2Date != null) {
                return v1Date.compareTo(v2Date);
            }
        }
        return 0;
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "dateSent", "message", "hasRead", "site" };
    }

    @Override
    public Class<Notification> getWrappedClass() {
        return Notification.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

    public Date getDateSent() {
        return wrappedObject.getDateSent();
    }

    public void setDateSent(Date dateSent) {
        Date oldDate = getDateSent();
        wrappedObject.setDateSent(dateSent);
        propertyChangeSupport.firePropertyChange("dateSent", oldDate, dateSent);
    }

    public String getMessage() {
        return wrappedObject.getMessage();
    }

    public void setMessage(String message) {
        String oldMessage = getMessage();
        wrappedObject.setMessage(message);
        propertyChangeSupport
            .firePropertyChange("comment", oldMessage, message);
    }

    public boolean hasBeenRead() {
        Boolean b = wrappedObject.getHasRead();
        if (b == null) {
            return false;
        }
        return b;
    }

    public void setHasRead(Boolean hasRead) {
        Boolean oldRead = hasBeenRead();
        wrappedObject.setHasRead(hasRead);
        propertyChangeSupport.firePropertyChange("hasRead", oldRead, hasRead);
    }

    public SiteWrapper getSite() {
        if (site == null) {
            Site s = wrappedObject.getSite();
            if (s == null)
                return null;
            site = new SiteWrapper(appService, s);
        }
        return site;
    }

    public void setSite(SiteWrapper site) {
        this.site = site;
        Site oldSite = wrappedObject.getSite();
        Site newSite = null;
        if (site != null) {
            newSite = site.getWrappedObject();
        }
        wrappedObject.setSite(newSite);
        propertyChangeSupport.firePropertyChange("site", oldSite, newSite);
    }

}
