package edu.ualberta.med.biobank.common.wrappers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.AbstractShipmentPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.AbstractShipment;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.Shipment;
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
        return wrappedObject.getDeparted();
    }

    public String getFormattedDeparted() {
        return DateFormatter.formatAsDateTime(getDeparted());
    }

    public void setDeparted(Date date) {
        Date oldDate = getDeparted();
        wrappedObject.setDeparted(date);
        propertyChangeSupport.firePropertyChange("departed", oldDate, date);
    }

    public Date getDateReceived() {
        return wrappedObject.getDateReceived();
    }

    public String getFormattedDateReceived() {
        // date received is not supposed to be null
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
        if (ship instanceof Dispatch) {
            return new DispatchWrapper(appService, (Dispatch) ship);
        }
        if (ship instanceof Shipment) {
            return new ShipmentWrapper(appService, (Shipment) ship);
        }
        return null;
    }

}
