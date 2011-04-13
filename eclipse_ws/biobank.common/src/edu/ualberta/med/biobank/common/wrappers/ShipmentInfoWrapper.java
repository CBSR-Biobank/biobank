package edu.ualberta.med.biobank.common.wrappers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ShipmentInfoBaseWrapper;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ShipmentInfoWrapper extends ShipmentInfoBaseWrapper {

    public ShipmentInfoWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShipmentInfoWrapper(WritableApplicationService appService,
        ShipmentInfo ship) {
        super(appService, ship);
    }

    @Override
    protected List<Property<?, ? super ShipmentInfo>> getProperties() {
        return ShipmentInfoPeer.PROPERTIES;
    }

    public String getFormattedDateReceived() {
        // date received is not supposed to be null
        return DateFormatter.formatAsDateTime(getReceivedAt());
    }

    public String getFormattedDateSent() {
        // date received is not supposed to be null
        return DateFormatter.formatAsDateTime(getSentAt());
    }

    @Override
    public int compareTo(ModelWrapper<ShipmentInfo> wrapper) {
        if (wrapper instanceof ShipmentInfoWrapper) {
            Date v1Date = wrappedObject.getReceivedAt();
            Date v2Date = wrapper.wrappedObject.getReceivedAt();
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
        Date dateReveived = getReceivedAt();
        return dateReveived.compareTo(startDate) >= 0
            && dateReveived.compareTo(endDate) <= 0;
    }

}
