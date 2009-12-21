package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ClinicWrapper extends ModelWrapper<Clinic> {

    public ClinicWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ClinicWrapper(WritableApplicationService appService,
        Clinic wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "activityStatus", "comment", "address",
            "site", "contactCollection", "shipmentCollection", "street1",
            "street2", "city", "province", "postalCode",
            "patientVisitCollection" };
    }

    private AddressWrapper getAddress() {
        Address address = wrappedObject.getAddress();
        if (address == null) {
            return null;
        }
        return new AddressWrapper(appService, address);
    }

    private void setAddress(Address address) {
        Address oldAddress = wrappedObject.getAddress();
        wrappedObject.setAddress(address);
        propertyChangeSupport
            .firePropertyChange("address", oldAddress, address);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String oldName = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    public String getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public void setActivityStatus(String activityStatus) {
        String oldStatus = getActivityStatus();
        wrappedObject.setActivityStatus(activityStatus);
        propertyChangeSupport.firePropertyChange("activityStatus", oldStatus,
            activityStatus);
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    public void setComment(String comment) {
        String oldComment = getComment();
        wrappedObject.setComment(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    public SiteWrapper getSite() {
        Site site = wrappedObject.getSite();
        if (site == null) {
            return null;
        }
        return new SiteWrapper(appService, site);
    }

    public void setSite(SiteWrapper siteWrapper) {
        Site oldSite = wrappedObject.getSite();
        Site newSite = siteWrapper.getWrappedObject();
        wrappedObject.setSite(newSite);
        propertyChangeSupport.firePropertyChange("site", oldSite, newSite);
    }

    private AddressWrapper initAddress() {
        setAddress(new Address());
        return getAddress();
    }

    public String getStreet1() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getStreet1();
    }

    public void setStreet1(String street1) {
        String old = getStreet1();
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setStreet1(street1);
        propertyChangeSupport.firePropertyChange("street1", old, street1);
    }

    public String getStreet2() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getStreet2();
    }

    public void setStreet2(String street2) {
        String old = getStreet2();
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setStreet2(street2);
        propertyChangeSupport.firePropertyChange("street2", old, street2);
    }

    public String getCity() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getCity();
    }

    public void setCity(String city) {
        String old = getCity();
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setCity(city);
        propertyChangeSupport.firePropertyChange("city", old, city);
    }

    public String getProvince() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getProvince();
    }

    public void setProvince(String province) {
        String old = getProvince();
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setProvince(province);
        propertyChangeSupport.firePropertyChange("province", old, province);
    }

    public String getPostalCode() {
        AddressWrapper address = getAddress();
        if (address == null) {
            return null;
        }
        return address.getPostalCode();
    }

    public void setPostalCode(String postalCode) {
        String old = getPostalCode();
        AddressWrapper address = getAddress();
        if (address == null) {
            address = initAddress();
        }
        address.setPostalCode(postalCode);
        propertyChangeSupport.firePropertyChange("postalCode", old, postalCode);
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
        if (getAddress() == null) {
            throw new BiobankCheckException(
                "the clinic does not have an address");
        }

        if (getSite() == null) {
            throw new BiobankCheckException("the clinic does not have an site");
        }

        if (!checkClinicNameUnique()) {
            throw new BiobankCheckException("A clinic with name \"" + getName()
                + "\" already exists.");
        }
    }

    @Override
    protected void persistDependencies(Clinic origObject) throws Exception {
        // remove contacts which are not on the list anymore
        if (origObject != null) {
            List<ContactWrapper> contactsOrig = new ClinicWrapper(appService,
                origObject).getContactCollection();
            if (contactsOrig != null && contactsOrig.size() > 0) {
                Collection<ContactWrapper> contactsNew = getContactCollection();
                for (ContactWrapper cw : contactsOrig) {
                    if (!contactsNew.contains(cw)) {
                        cw.delete();
                    }
                }
            }
        }
    }

    public boolean checkClinicNameUnique() throws ApplicationException {
        HQLCriteria c;

        if (getWrappedObject().getId() == null) {
            c = new HQLCriteria("from " + Clinic.class.getName()
                + " where site.id = ? and name = ?", Arrays
                .asList(new Object[] { getSite().getId(), getName() }));
        } else {
            c = new HQLCriteria("from " + Clinic.class.getName()
                + " where site.id = ? and name = ? and id <> ?", Arrays
                .asList(new Object[] { getSite().getId(), getName(), getId() }));
        }

        List<Clinic> results = appService.query(c);
        return (results.size() == 0);
    }

    @Override
    public Class<Clinic> getWrappedClass() {
        return Clinic.class;
    }

    @SuppressWarnings("unchecked")
    public List<ContactWrapper> getContactCollection(boolean sort) {
        List<ContactWrapper> contactCollection = (List<ContactWrapper>) propertiesMap
            .get("contactCollection");
        if (contactCollection == null) {
            Collection<Contact> children = wrappedObject.getContactCollection();
            if (children != null) {
                contactCollection = new ArrayList<ContactWrapper>();
                for (Contact type : children) {
                    contactCollection.add(new ContactWrapper(appService, type));
                }
                propertiesMap.put("contactCollection", contactCollection);
            }
        }
        if ((contactCollection != null) && sort)
            Collections.sort(contactCollection);
        return contactCollection;
    }

    public List<ContactWrapper> getContactCollection() {
        return getContactCollection(false);
    }

    public void setContactCollection(Collection<Contact> contacts,
        boolean setNull) {
        Collection<Contact> oldContacts = wrappedObject.getContactCollection();
        wrappedObject.setContactCollection(contacts);
        propertyChangeSupport.firePropertyChange("contactCollection",
            oldContacts, contacts);
        if (setNull) {
            propertiesMap.put("contactCollection", null);
        }
    }

    public void setContactCollection(Collection<ContactWrapper> contacts) {
        Collection<Contact> collection = new HashSet<Contact>();
        for (ContactWrapper pv : contacts) {
            collection.add(pv.getWrappedObject());
        }
        setContactCollection(collection, false);
        propertiesMap.put("contactCollection", contacts);
    }

    @SuppressWarnings("unchecked")
    public List<StudyWrapper> getStudyCollection(boolean sort)
        throws ApplicationException {
        List<StudyWrapper> studyCollection = (List<StudyWrapper>) propertiesMap
            .get("studyCollection");

        if (studyCollection == null) {
            studyCollection = new ArrayList<StudyWrapper>();
            HQLCriteria c = new HQLCriteria("select distinct studies from "
                + Contact.class.getName() + " as contacts"
                + " inner join contacts.studyCollection as studies"
                + " where contacts.clinic = ?", Arrays
                .asList(new Object[] { wrappedObject }));
            List<Study> collection = appService.query(c);
            for (Study study : collection) {
                studyCollection.add(new StudyWrapper(appService, study));
            }
            if (sort)
                Collections.sort(studyCollection);
            propertiesMap.put("studyCollection", studyCollection);
        }
        return studyCollection;
    }

    public List<StudyWrapper> getStudyCollection() throws ApplicationException {
        return getStudyCollection(false);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if (hasShipments()) {
            throw new BiobankCheckException("Unable to delete clinic "
                + getName() + ". All defined shipments must be removed first.");
        }
        List<StudyWrapper> studies = getStudyCollection();
        if (studies != null && studies.size() > 0) {
            throw new BiobankCheckException("Unable to delete clinic "
                + getName() + ". No more study reference should exist.");
        }
    }

    public boolean hasShipments() throws ApplicationException,
        BiobankCheckException {
        HQLCriteria criteria = new HQLCriteria(
            "select count(shipment) from "
                + Clinic.class.getName()
                + " as clinic inner join clinic.shipmentCollection as shipment where clinic.id = ?",
            Arrays.asList(new Object[] { getId() }));
        List<Long> result = appService.query(criteria);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return result.get(0) > 0;
    }

    @SuppressWarnings("unchecked")
    public List<ShipmentWrapper> getShipmentCollection() {
        List<ShipmentWrapper> shipmentCollection = (List<ShipmentWrapper>) propertiesMap
            .get("shipmentCollection");
        if (shipmentCollection == null) {
            Collection<Shipment> children = wrappedObject
                .getShipmentCollection();
            if (children != null) {
                shipmentCollection = new ArrayList<ShipmentWrapper>();
                for (Shipment s : children) {
                    shipmentCollection.add(new ShipmentWrapper(appService, s));
                }
                propertiesMap.put("shipmentCollection", shipmentCollection);
            }
        }
        return shipmentCollection;
    }

    public void setShipmentCollection(Collection<Shipment> shipmentCollection,
        boolean setNull) {
        Collection<Shipment> oldCollection = wrappedObject
            .getShipmentCollection();
        wrappedObject.setShipmentCollection(shipmentCollection);
        propertyChangeSupport.firePropertyChange("shipmentCollection",
            oldCollection, shipmentCollection);
        if (setNull) {
            propertiesMap.put("shipmentCollection", null);
        }
    }

    public void setShipmentCollection(
        Collection<ShipmentWrapper> shipmentCollection) {
        Collection<Shipment> sCollection = new HashSet<Shipment>();
        for (ShipmentWrapper s : shipmentCollection) {
            sCollection.add(s.getWrappedObject());
        }
        setShipmentCollection(sCollection, false);
        propertiesMap.put("shipmentCollection", shipmentCollection);
    }

    /**
     * Search for a shipment in the clinic with the given date received and
     * patient number.
     */
    public ShipmentWrapper getShipment(Date dateReceived)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Shipment.class.getName()
            + " where clinic.id = ? and dateReceived = ?", Arrays
            .asList(new Object[] { getId(), dateReceived }));
        List<Shipment> shipments = appService.query(criteria);
        if (shipments.size() == 1) {
            return new ShipmentWrapper(appService, shipments.get(0));
        }
        return null;
    }

    /**
     * Search for a shipment in the clinic with the given date received and
     * patient number.
     */
    public ShipmentWrapper getShipment(Date dateReceived, String patientNumber)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("select shipment from "
            + Shipment.class.getName()
            + " as shipment join shipment.patientCollection as patients"
            + " where shipment.clinic.id = ? and shipment.dateReceived = ? "
            + "and patients.number = ?", Arrays.asList(new Object[] { getId(),
            dateReceived, patientNumber }));
        List<Shipment> shipments = appService.query(criteria);
        if (shipments.size() > 0) {
            return new ShipmentWrapper(appService, shipments.get(0));
        }
        return null;
    }

    @Override
    public int compareTo(ModelWrapper<Clinic> wrapper) {
        if (wrapper instanceof ClinicWrapper) {
            String myName = wrappedObject.getName();
            String wrapperName = wrapper.wrappedObject.getName();
            return ((myName.compareTo(wrapperName) > 0) ? 1 : (myName
                .equals(wrapperName) ? 0 : -1));
        }
        return 0;
    }

    @Override
    public String toString() {
        return getName();
    }

    @SuppressWarnings("unchecked")
    public List<PatientVisitWrapper> getPatientVisitCollection()
        throws ApplicationException {
        List<PatientVisitWrapper> pvCollection = (List<PatientVisitWrapper>) propertiesMap
            .get("patientVisitCollection");

        if (pvCollection == null) {
            pvCollection = new ArrayList<PatientVisitWrapper>();
            HQLCriteria c = new HQLCriteria("select distinct pv from "
                + PatientVisit.class.getName()
                + " as pv where shipment.clinic.id = ?", Arrays
                .asList(new Object[] { getId() }));
            List<PatientVisit> collection = appService.query(c);
            for (PatientVisit pv : collection) {
                pvCollection.add(new PatientVisitWrapper(appService, pv));
            }
            propertiesMap.put("patientVisitCollection", pvCollection);
        }
        return pvCollection;
    }

}
