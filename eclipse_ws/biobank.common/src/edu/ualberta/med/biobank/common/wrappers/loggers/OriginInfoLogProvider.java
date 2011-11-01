package edu.ualberta.med.biobank.common.wrappers.loggers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.Specimen;

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
        detailsList.add(new StringBuilder("specimens:").append( //$NON-NLS-1$
            getSpecimenCollectionSize(originInfo)).toString());

        log.setDetails(StringUtil.join(detailsList, ", ")); //$NON-NLS-1$

        return log;
    }

    private int getSpecimenCollectionSize(OriginInfo originInfo) {
        Collection<Specimen> specimens = originInfo.getSpecimenCollection();
        return specimens == null ? 0 : specimens.size();
    }

    @Override
    public Log getObjectLog(Object model) {
        return getLog((OriginInfo) model);
    }
}
