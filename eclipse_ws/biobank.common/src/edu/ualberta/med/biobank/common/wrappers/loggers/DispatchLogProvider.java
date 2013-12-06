package edu.ualberta.med.biobank.common.wrappers.loggers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.type.DispatchState;

public class DispatchLogProvider implements WrapperLogProvider<Dispatch> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @SuppressWarnings("nls")
    @Override
    public Log getLog(Dispatch dispatch) {
        Log log = new Log();

        DispatchState dispatchState = dispatch.getState();
        StringBuilder sb;

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
            sb = new StringBuilder();
            sb.append("state: ");
            sb.append(dispatchState.getLabel());
            detailsList.add(sb.toString());

            if (dispatchState.equals(DispatchState.CREATION)
                || dispatchState.equals(DispatchState.IN_TRANSIT)
                || dispatchState.equals(DispatchState.LOST)) {

                ShipmentInfo shipmentInfo = dispatch.getShipmentInfo();
                if (shipmentInfo != null && shipmentInfo.getPackedAt() != null) {
                    sb = new StringBuilder();
                    sb.append("packed at: ");
                    sb.append(DATE_FORMATTER.format(shipmentInfo.getPackedAt()));
                    detailsList.add(sb.toString());
                }
            }
        }

        ShipmentInfo shipmentInfo = dispatch.getShipmentInfo();
        if (shipmentInfo != null) {
            Date receivedAt = shipmentInfo.getReceivedAt();
            if (receivedAt != null) {
                sb = new StringBuilder();
                sb.append("received at: ");
                sb.append(DATE_FORMATTER.format(receivedAt));
                detailsList.add(sb.toString());
            }

            String waybill = shipmentInfo.getWaybill();
            if (waybill != null) {
                sb = new StringBuilder();
                sb.append("waybill: ");
                sb.append(waybill);
                detailsList.add(sb.toString());
            }
        }
        log.setDetails(StringUtil.join(detailsList, ", "));

        return log;
    }

    @Override
    public Log getObjectLog(Object model) {
        return getLog((Dispatch) model);
    }
}
