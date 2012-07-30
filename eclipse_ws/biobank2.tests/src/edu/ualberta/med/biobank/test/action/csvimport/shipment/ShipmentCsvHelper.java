package edu.ualberta.med.biobank.test.action.csvimport.shipment;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.csvimport.shipment.ShipmentCsvInfo;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.test.NameGenerator;

class ShipmentCsvHelper {
    private final NameGenerator nameGenerator;

    public ShipmentCsvHelper(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    Set<ShipmentCsvInfo> createShipments(Center sendingCenter,
        Center receivingCenter, Set<ShippingMethod> shippingMethods,
        int maxShipments) {
        Set<ShipmentCsvInfo> csvInfos = new HashSet<ShipmentCsvInfo>();
        for (int i = 0; i < maxShipments; ++i) {
            for (ShippingMethod shippingMethod : shippingMethods) {
                ShipmentCsvInfo csvInfo = new ShipmentCsvInfo();
                csvInfo.setDateReceived(new Date());
                csvInfo.setSendingCenter(sendingCenter.getNameShort());
                csvInfo.setReceivingCenter(receivingCenter.getNameShort());
                csvInfo.setShippingMethod(shippingMethod.getName());
                csvInfo.setWaybill(nameGenerator.next(String.class));
                csvInfo.setComment(nameGenerator.next(String.class));
                csvInfos.add(csvInfo);
            }
        }
        return csvInfos;
    }

    ShippingMethod getNewShippingMethod() {
        String name = nameGenerator.next(ShippingMethod.class);
        ShippingMethod shippingMethod = new ShippingMethod();
        shippingMethod.setName(name);
        return shippingMethod;
    }
}
