package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShptSampleSourceWrapper;

public class ShptSampleSourceHelper extends DbHelper {

    public static ShptSampleSourceWrapper newShptSampleSource(String name,
        ShipmentWrapper shipment) throws Exception {
        ShptSampleSourceWrapper sss = new ShptSampleSourceWrapper(appService);
        SampleSourceWrapper ssw = SampleSourceHelper.addSampleSource(name);
        sss.setSampleSource(ssw);
        sss.setShipment(shipment);
        return sss;
    }

    public static ShptSampleSourceWrapper addShptSampleSource(String name,
        ShipmentWrapper shipment) throws Exception {
        ShptSampleSourceWrapper sss = newShptSampleSource(name, shipment);
        sss.persist();
        return sss;
    }

    public static void addShptSampleSources(String name,
        ShipmentWrapper shipment, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            addShptSampleSource(name + i, shipment);
        }
    }
}
