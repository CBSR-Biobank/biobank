package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.ShippingCompany;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class ShippingCompanyWrapper extends ModelWrapper<ShippingCompany> {

    public ShippingCompanyWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ShippingCompanyWrapper(WritableApplicationService appService,
        ShippingCompany sc) {
        super(appService, sc);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        List<ShipmentWrapper> shipments = getShipmentCollection();
        if (shipments != null && shipments.size() > 0) {
            throw new BiobankCheckException(
                "Cannot delete this shipping company: shipments are still using it");
        }

    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "shipmentCollection" };
    }

    @Override
    public Class<ShippingCompany> getWrappedClass() {
        return ShippingCompany.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String old = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", old, name);
    }

    @Override
    public int compareTo(ModelWrapper<ShippingCompany> o) {
        if (o instanceof ShippingCompanyWrapper) {
            return getName().compareTo(o.getWrappedObject().getName());
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public List<ShipmentWrapper> getShipmentCollection(boolean sort) {
        List<ShipmentWrapper> shipmentCollection = (List<ShipmentWrapper>) propertiesMap
            .get("shipmentCollection");
        if (shipmentCollection == null) {
            Collection<Shipment> children = wrappedObject
                .getShipmentCollection();
            if (children != null) {
                shipmentCollection = new ArrayList<ShipmentWrapper>();
                for (Shipment ship : children) {
                    shipmentCollection
                        .add(new ShipmentWrapper(appService, ship));
                }
                propertiesMap.put("shipmentCollection", shipmentCollection);
            }
        }
        if ((shipmentCollection != null) && sort)
            Collections.sort(shipmentCollection);
        return shipmentCollection;
    }

    public List<ShipmentWrapper> getShipmentCollection() {
        return getShipmentCollection(false);
    }

    public static List<ShippingCompanyWrapper> getShippingCompanies(
        WritableApplicationService appService) throws ApplicationException {
        List<ShippingCompany> objects = appService.search(
            ShippingCompany.class, new ShippingCompany());
        List<ShippingCompanyWrapper> wrappers = new ArrayList<ShippingCompanyWrapper>();
        for (ShippingCompany sc : objects) {
            wrappers.add(new ShippingCompanyWrapper(appService, sc));
        }
        return wrappers;
    }

    @Override
    public String toString() {
        return getName();
    }
}
