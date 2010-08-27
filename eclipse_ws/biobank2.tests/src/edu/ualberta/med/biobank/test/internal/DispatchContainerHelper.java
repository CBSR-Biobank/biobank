package edu.ualberta.med.biobank.test.internal;

import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class DispatchContainerHelper extends DbHelper {

    public static DispatchContainerWrapper newContainer(String barcode,
        DispatchShipmentWrapper shipment, ContainerTypeWrapper type)
        throws Exception {
        DispatchContainerWrapper container;

        container = new DispatchContainerWrapper(appService);
        container.setProductBarcode(barcode);
        if (shipment != null) {
            container.setShipment(shipment);
        }
        container.setContainerType(type);
        container.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
            appService, "Active"));
        return container;
    }

    public static DispatchContainerWrapper newContainerRandom(SiteWrapper site,
        DispatchShipmentWrapper shipment, String name) throws Exception {
        ContainerTypeWrapper type = ContainerTypeHelper.addContainerTypeRandom(
            site, name, false);
        return newContainer(name, shipment, type);

    }

    public static DispatchContainerWrapper addContainer(String barcode,
        DispatchShipmentWrapper shipment, ContainerTypeWrapper type)
        throws Exception {
        DispatchContainerWrapper container = newContainer(barcode, shipment,
            type);
        container.persist();
        return container;
    }

    public static DispatchContainerWrapper addContainerRandom(SiteWrapper site,
        DispatchShipmentWrapper shipment, String name) throws Exception {
        DispatchContainerWrapper container = newContainerRandom(site, shipment,
            name);
        container.persist();
        return container;
    }

    public static void addAliquots(DispatchContainerWrapper container,
        List<AliquotWrapper> aliquots, int startRow, int startCol)
        throws Exception {

        int maxCols = container.getContainerType().getColCapacity();
        int count = 0;
        for (AliquotWrapper aliquot : aliquots) {
            container.addAliquot(startRow + count / maxCols, startCol + count
                % maxCols, aliquot);
            count++;
        }
        container.persist();
        container.reload();
    }

}
