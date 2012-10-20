package edu.ualberta.med.biobank.action.csvimport.shipment;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.NameGenerator;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.center.Center;

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
                csvInfo.setSendingCenter(sendingCenter.getName());
                csvInfo.setReceivingCenter(receivingCenter.getName());
                csvInfo.setShippingMethod(shippingMethod.getName());
                csvInfo.setWaybill(nameGenerator.next(String.class));
                csvInfo.setComment(nameGenerator.next(String.class));
                csvInfos.add(csvInfo);
            }
        }
        return csvInfos;
    }
}
