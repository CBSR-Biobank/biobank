package edu.ualberta.med.biobank.action.csvimport.shipment;

import java.util.Date;

import edu.ualberta.med.biobank.action.csvimport.IImportInfo;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

/**
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public class ShipmentImportInfo implements IImportInfo {

    private final ShipmentCsvInfo csvInfo;
    private ShippingMethod shippingMethod;
    private Center originCenter;
    private Site currentSite;
    private OriginInfo originInfo;
    private Patient patient;
    private Specimen specimen;
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OriginInfo getNewOriginInfo() {
        if (user == null) {
            throw new IllegalStateException("user is null");
        }

        originInfo = new OriginInfo();
        originInfo.setCenter(originCenter);
        originInfo.setReceiverCenter(currentSite);

        Date timeNow = new Date();

        Comment comment = new Comment();
        comment.setMessage(csvInfo.getComment());
        comment.setUser(user);
        comment.setTimeCreated(timeNow);
        originInfo.getComments().add(comment);

        ShipmentInfo shipmentInfo = new ShipmentInfo();
        shipmentInfo.setPackedAt(csvInfo.getDateReceived());
        shipmentInfo.setReceivedAt(timeNow);
        shipmentInfo.setWaybill(csvInfo.getWaybill());

        shipmentInfo.setShippingMethod(getShippingMethod());

        originInfo.setShipmentInfo(shipmentInfo);

        return originInfo;
    }

}
