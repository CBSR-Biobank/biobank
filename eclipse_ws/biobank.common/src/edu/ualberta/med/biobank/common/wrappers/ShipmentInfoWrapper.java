package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.ShipmentInfoPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ShipmentInfoBaseWrapper;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ShipmentInfoWrapper extends ShipmentInfoBaseWrapper {

    public ShipmentInfoWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShipmentInfoWrapper(WritableApplicationService appService,
        ShipmentInfo ship) {
        super(appService, ship);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return ShipmentInfoPeer.PROP_NAMES;
    }

    public String getFormattedDateReceived() {
        return DateFormatter.formatAsDateTime(DateFormatter.convertDate(
            DateFormatter.GMT, DateFormatter.LOCAL, getReceivedAt()));
    }

    public String getFormattedDatePacked() {
        return DateFormatter.formatAsDateTime(DateFormatter.convertDate(
            DateFormatter.GMT, DateFormatter.LOCAL, getPackedAt()));
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

    public static List<ShipmentInfoWrapper> getAllShipmentInfosByMethod(
        WritableApplicationService appService, ShippingMethodWrapper method)
        throws ApplicationException {
        List<ShipmentInfoWrapper> ships = new ArrayList<ShipmentInfoWrapper>();
        HQLCriteria c = new HQLCriteria("from " + ShipmentInfo.class.getName()
            + " ship where ship." + ShipmentInfoPeer.SHIPPING_METHOD.getName()
            + "=?", Arrays.asList(method.getWrappedObject()));
        ships = appService.query(c);
        return ships;
    }
}
