package edu.ualberta.med.biobank.action.csvimport.shipment;

import java.util.Date;

import edu.ualberta.med.biobank.action.csvimport.IImportInfo;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;

public class ShipmentImportInfo implements IImportInfo {

    private final ShipmentCsvInfo csvInfo;
    private ShippingMethod shippingMethod;
    private Center originCenter;
    private Site currentSite;
    private OriginInfo originInfo;
    private Patient patient;
    private Specimen specimen;

    ShipmentImportInfo(ShipmentCsvInfo csvInfo) {
        this.csvInfo = csvInfo;
    }

    @Override
    public int getCsvLineNumber() {
        return csvInfo.getLineNumber();
    }

    public Center getOriginCenter() {
        return originCenter;
    }

    public void setOriginCenter(Center originCenter) {
        this.originCenter = originCenter;
    }

    public Site getCurrentSite() {
        return currentSite;
    }

    public void setCurrentSite(Site currentSite) {
        this.currentSite = currentSite;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    public ShippingMethod getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public OriginInfo createOriginInfo() {
        originInfo = new OriginInfo();
        originInfo.setCenter(originCenter);
        originInfo.setReceiverSite(currentSite);

        return originInfo;
    }

    public ShipmentInfo getNewShipmentInfo() {
        ShipmentInfo shipmentInfo = new ShipmentInfo();
        shipmentInfo.setPackedAt(csvInfo.getDateReceived());
        shipmentInfo.setReceivedAt(new Date());
        shipmentInfo.setWaybill(csvInfo.getWaybill());

        shipmentInfo.setShippingMethod(getShippingMethod());

        originInfo.setShipmentInfo(shipmentInfo);

        return shipmentInfo;
    }

}
