package edu.ualberta.med.biobank.common.wrappers;

import java.util.Calendar;
import java.util.Date;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.model.AbstractShipment;
import edu.ualberta.med.biobank.model.ClinicShipment;
import edu.ualberta.med.biobank.model.DispatchShipment;
import edu.ualberta.med.biobank.model.ShippingMethod;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractShipmentWrapper<E extends AbstractShipment>
    extends ModelWrapper<E> {

    public AbstractShipmentWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public AbstractShipmentWrapper(WritableApplicationService appService, E ship) {
        super(appService, ship);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "dateReceived", "comment", "waybill",
            "dateShipped", "boxNumber", "shippingMethod" };
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        checkDateReceivedNotNull();
    }

    private void checkDateReceivedNotNull() throws BiobankCheckException {
        if (getDateReceived() == null)
            throw new BiobankCheckException(
                "'Date Received' is a required field. You must set this value before saving a shipment.");
    }

    public Date getDateShipped() {
        return wrappedObject.getDateShipped();
    }

    public String getFormattedDateShipped() {
        return DateFormatter.formatAsDateTime(getDateShipped());
    }

    public void setDateShipped(Date date) {
        Date oldDate = getDateShipped();
        wrappedObject.setDateShipped(date);
        propertyChangeSupport.firePropertyChange("dateShipped", oldDate, date);
    }

    public Date getDateReceived() {
        return wrappedObject.getDateReceived();
    }

    public String getFormattedDateReceived() {
        return DateFormatter.formatAsDateTime(getDateReceived());
    }

    public void setDateReceived(Date date) {
        Date oldDate = getDateReceived();
        wrappedObject.setDateReceived(date);
        propertyChangeSupport.firePropertyChange("dateReceived", oldDate, date);
    }

    @Override
    public int compareTo(ModelWrapper<E> wrapper) {
        if (wrapper instanceof AbstractShipmentWrapper) {
            Date v1Date = wrappedObject.getDateReceived();
            Date v2Date = wrapper.wrappedObject.getDateReceived();
            if (v1Date != null && v2Date != null) {
                return v1Date.compareTo(v2Date);
            }
        }
        return 0;
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public void setComment(String comment) {
        String oldComment = getComment();
        wrappedObject.setComment(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    public String getWaybill() {
        return wrappedObject.getWaybill();
    }

    public void setWaybill(String waybill) {
        String old = getWaybill();
        wrappedObject.setWaybill(waybill);
        propertyChangeSupport.firePropertyChange("waybill", old, waybill);
    }

    public String getBoxNumber() {
        return wrappedObject.getBoxNumber();
    }

    public void setBoxNumber(String boxNumber) {
        String old = getBoxNumber();
        wrappedObject.setBoxNumber(boxNumber);
        propertyChangeSupport.firePropertyChange("boxNumber", old, boxNumber);
    }

    @Override
    public String toString() {
        String s = getFormattedDateReceived();
        if (getWaybill() != null) {
            s += " (" + getWaybill() + ")";
        }
        return s;
    }

    public boolean isReceivedToday() {
        Calendar cal = Calendar.getInstance();
        // yesterday midnight
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        // today midnight
        cal.add(Calendar.DATE, 1);
        Date endDate = cal.getTime();
        Date dateReveived = getDateReceived();
        return dateReveived.compareTo(startDate) >= 0
            && dateReveived.compareTo(endDate) <= 0;
    }

    public ShippingMethodWrapper getShippingMethod() {
        ShippingMethod sc = wrappedObject.getShippingMethod();
        if (sc == null) {
            return null;
        }
        return new ShippingMethodWrapper(appService, sc);
    }

    public void setShippingMethod(ShippingMethodWrapper sc) {
        ShippingMethod old = wrappedObject.getShippingMethod();
        ShippingMethod newSh = null;
        if (sc != null) {
            newSh = sc.getWrappedObject();
        }
        wrappedObject.setShippingMethod(newSh);
        propertyChangeSupport.firePropertyChange("shippingMethod", old, newSh);
    }

    public static AbstractShipmentWrapper<?> createInstance(
        WritableApplicationService appService, AbstractShipment ship) {
        if (ship instanceof DispatchShipment) {
            return new DispatchShipmentWrapper(appService,
                (DispatchShipment) ship);
        }
        if (ship instanceof ClinicShipment) {
            return new ClinicShipmentWrapper(appService, (ClinicShipment) ship);
        }
        return null;
    }

}
