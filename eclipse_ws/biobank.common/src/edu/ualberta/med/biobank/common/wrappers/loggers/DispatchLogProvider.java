package edu.ualberta.med.biobank.common.wrappers.loggers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.ShipmentInfo;

public class DispatchLogProvider implements WrapperLogProvider<Dispatch> {
    private static final long serialVersionUID = 1L;

    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm"); //$NON-NLS-1$

    @Override
    public Log getLog(Dispatch dispatch) {
        Log log = new Log();

        Integer state = dispatch.getState();
        DispatchState dispatchState = DispatchState.getState(state);

        if (dispatchState != null) {
            if (dispatchState.equals(DispatchState.CREATION)
                || dispatchState.equals(DispatchState.IN_TRANSIT)) {
                log.setCenter(dispatch.getSenderCenter().getNameShort());
            } else {
                log.setCenter(dispatch.getReceiverCenter().getNameShort());
            }
        }

        List<String> detailsList = new ArrayList<String>();

        if (dispatchState != null) {
            detailsList.add(new StringBuilder("state: ").append( //$NON-NLS-1$
                dispatchState.getLabel()).toString());

            if (dispatchState.equals(DispatchState.CREATION)
                || dispatchState.equals(DispatchState.IN_TRANSIT)
                || dispatchState.equals(DispatchState.LOST)) {

                ShipmentInfo shipmentInfo = dispatch.getShipmentInfo();
                if (shipmentInfo != null && shipmentInfo.getPackedAt() != null) {
                    String packedAt = DATE_FORMATTER.format(shipmentInfo
                        .getPackedAt());
                    detailsList.add(new StringBuilder("packed at: ").append( //$NON-NLS-1$
                        packedAt).toString());
                }
            }
        }

        ShipmentInfo shipmentInfo = dispatch.getShipmentInfo();
        if (shipmentInfo != null) {
            Date receivedAt = shipmentInfo.getReceivedAt();
            if (receivedAt != null) {
                String receivedAtString = DATE_FORMATTER.format(receivedAt);
                detailsList.add(new StringBuilder("received at: ").append( //$NON-NLS-1$
                    receivedAtString).toString());
            }

            String waybill = shipmentInfo.getWaybill();
            if (waybill != null) {
                detailsList.add(new StringBuilder(", waybill: ") //$NON-NLS-1$
                    .append(waybill).toString());
            }
        }
        log.setDetails(StringUtil.join(detailsList, ", ")); //$NON-NLS-1$

        return log;
    }

    @Override
    public Log getObjectLog(Object model) {
        return getLog((Dispatch) model);
    }
}
