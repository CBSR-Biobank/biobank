package edu.ualberta.med.biobank.common.wrappers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.AbstractShipmentPeer;
import edu.ualberta.med.biobank.common.wrappers.base.AbstractShipmentBaseWrapper;
import edu.ualberta.med.biobank.model.AbstractShipment;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Dispatch;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractShipmentWrapper<E extends AbstractShipment>
    extends AbstractShipmentBaseWrapper<E> {

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

    public String getFormattedDeparted() {
        return DateFormatter.formatAsDateTime(getDeparted());
    }

    public String getFormattedDateReceived() {
        // date received is not supposed to be null
        return DateFormatter.formatAsDateTime(getDateReceived());
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

    public void getActivityStatus(ActivityStatusWrapper activityStatus) {
        setWrappedProperty(AbstractShipmentPeer.ACTIVITY_STATUS, activityStatus);
    }

    public static AbstractShipmentWrapper<?> createInstance(
        WritableApplicationService appService, AbstractShipment ship) {
        if (ship instanceof Dispatch) {
            return new DispatchWrapper(appService, (Dispatch) ship);
        }
        if (ship instanceof CollectionEvent) {
            return new CollectionEventWrapper(appService,
                (CollectionEvent) ship);
        }
        return null;
    }
}
