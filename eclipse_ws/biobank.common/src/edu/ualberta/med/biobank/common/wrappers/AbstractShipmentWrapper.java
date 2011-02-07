package edu.ualberta.med.biobank.common.wrappers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.AbstractShipmentPeer;
import edu.ualberta.med.biobank.model.AbstractShipment;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.Source;
import edu.ualberta.med.biobank.model.ShippingMethod;
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
    protected List<String> getPropertyChangeNames() {
        return AbstractShipmentPeer.PROP_NAMES;
    }

    public Date getDeparted() {
        return getProperty(AbstractShipmentPeer.DEPARTED);
    }

    public String getFormattedDeparted() {
        return DateFormatter.formatAsDateTime(getDeparted());
    }

    public void setDeparted(Date date) {
        setProperty(AbstractShipmentPeer.DEPARTED, date);
    }

    public Date getDateReceived() {
        return getProperty(AbstractShipmentPeer.DATE_RECEIVED);
    }

    public String getFormattedDateReceived() {
        // date received is not supposed to be null
        return DateFormatter.formatAsDateTime(getDateReceived());
    }

    public void setDateReceived(Date date) {
        setProperty(AbstractShipmentPeer.DATE_RECEIVED, date);
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
        return getProperty(AbstractShipmentPeer.COMMENT);
    }

    public void setComment(String comment) {
        setProperty(AbstractShipmentPeer.COMMENT, comment);
    }

    public String getWaybill() {
        return getProperty(AbstractShipmentPeer.WAYBILL);
    }

    public void setWaybill(String waybill) {
        setProperty(AbstractShipmentPeer.WAYBILL, waybill);
    }

    public String getBoxNumber() {
        return getProperty(AbstractShipmentPeer.BOX_NUMBER);
    }

    public void setBoxNumber(String boxNumber) {
        setProperty(AbstractShipmentPeer.BOX_NUMBER, boxNumber);
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
        return getWrappedProperty(AbstractShipmentPeer.SHIPPING_METHOD,
            ShippingMethodWrapper.class);
    }

    public void setShippingMethod(ShippingMethodWrapper method) {
        setWrappedProperty(AbstractShipmentPeer.SHIPPING_METHOD, method);
    }

    public ActivityStatusWrapper getActivityStatus() {
        return getWrappedProperty(AbstractShipmentPeer.ACTIVITY_STATUS,
            ActivityStatusWrapper.class);
    }

    public void getActivityStatus(ActivityStatusWrapper activityStatus) {
        setWrappedProperty(AbstractShipmentPeer.ACTIVITY_STATUS, activityStatus);
    }

    public static AbstractShipmentWrapper<?> createInstance(
        WritableApplicationService appService, AbstractShipment ship) {
        if (ship instanceof Dispatch) {
            return new DispatchWrapper(appService, (Dispatch) ship);
        }
        if (ship instanceof Source) {
            return new SourceWrapper(appService, (Source) ship);
        }
        return null;
    }
}
