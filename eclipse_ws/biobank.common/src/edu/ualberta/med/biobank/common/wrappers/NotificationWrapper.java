package edu.ualberta.med.biobank.common.wrappers;

import java.util.Date;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.Notification;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class NotificationWrapper extends ModelWrapper<Notification> {

    public NotificationWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public NotificationWrapper(WritableApplicationService appService,
        Notification wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public int compareTo(ModelWrapper<Notification> o) {
        return 0;
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "dateSent", "message", "hasRead" };
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

}
