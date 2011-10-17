package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.wrappers.base.ShipmentTempLoggerBaseWrapper;
import edu.ualberta.med.biobank.model.ShipmentTempLogger;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ShipmentTempLoggerWrapper extends ShipmentTempLoggerBaseWrapper {

    private byte[] ufile;

    public ShipmentTempLoggerWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShipmentTempLoggerWrapper(WritableApplicationService appService,
        ShipmentTempLogger shipmentTempLogger) {
        super(appService, shipmentTempLogger);
    }

    public void setFileIdForCurrentFile() throws ApplicationException {
        String fileId = ((BiobankApplicationService) appService).uploadFile(
            ufile, getDeviceId());
        setReport(fileId);
    }

    public byte[] getFile() {
        return ufile;
    }

    public void setFile(byte[] lufile) {
        ufile = lufile;
    }

}
