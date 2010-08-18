package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.model.AbstractShipment;
import edu.ualberta.med.biobank.model.ClinicShipment;
import edu.ualberta.med.biobank.model.DispatchShipment;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractShipmentWrapper<E extends AbstractShipment>
    extends ModelWrapper<E> {

    public AbstractShipmentWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public AbstractShipmentWrapper(WritableApplicationService appService, E ship) {
        super(appService, ship);
    }

    public static AbstractShipmentWrapper<?> createInstance(
        WritableApplicationService appService, AbstractShipment ship) {
        if (ship instanceof DispatchShipment) {
            return new DispatchShipmentWrapper(appService,
                (DispatchShipment) ship);
        }
        if (ship instanceof ClinicShipment) {
            return new ClinicShipmentWrapper(appService, (ClinicShipment) ship);
        }
        return null;
    }

}
