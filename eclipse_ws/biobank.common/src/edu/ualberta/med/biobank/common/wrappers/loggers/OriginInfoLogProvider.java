package edu.ualberta.med.biobank.common.wrappers.loggers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;

public class OriginInfoLogProvider implements WrapperLogProvider<OriginInfo> {
    private static final long serialVersionUID = 1L;

    @Override
    public Log getLog(OriginInfo originInfo) {
        ShipmentInfo shipmentInfo = originInfo.getShipmentInfo();

        if (shipmentInfo == null) {
            // nothing to log since origin info does not yet point to any
            // shipping information
            return null;
        }

        Log log = new Log();

        log.setCenter(originInfo.getCenter().getNameShort());

        List<String> detailsList = new ArrayList<String>();
        detailsList.add(new StringBuilder("waybill:").append( //$NON-NLS-1$
            shipmentInfo.getWaybill()).toString());

        log.setDetails(StringUtil.join(detailsList, ", ")); //$NON-NLS-1$

        return log;
    }

    @Override
    public Log getObjectLog(Object model) {
        return getLog((OriginInfo) model);
    }
}
