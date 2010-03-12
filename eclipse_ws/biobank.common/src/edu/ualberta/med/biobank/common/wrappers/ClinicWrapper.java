package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;
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

    private Set<ContactWrapper> deletedContacts = new HashSet<ContactWrapper>();

    public ClinicWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ClinicWrapper(WritableApplicationService appService,
        Clinic wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "nameShort", "activityStatus", "comment",
            "address", "site", "contactCollection", "shipmentCollection",
            "street1", "street2", "city", "province", "postalCode",
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

    public String getNameShort() {
        return wrappedObject.getNameShort();
    }

    public void setNameShort(String nameShort) {
        String oldNameShort = getNameShort();
        wrappedObject.setNameShort(nameShort);
        propertyChangeSupport.firePropertyChange("nameShort", oldNameShort,
            nameShort);
    }

    public ActivityStatusWrapper getActivityStatus() {
        ActivityStatus activityStatus = wrappedObject.getActivityStatus();
        if (activityStatus == null)
            return null;
        return new ActivityStatusWrapper(appService, activityStatus);
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        ActivityStatus oldActivityStatus = wrappedObject.getActivityStatus();
        ActivityStatus rawObject = null;
        if (activityStatus != null) {
            rawObject = activityStatus.getWrappedObject();
        }
        wrappedObject.setActivityStatus(rawObject);
        propertyChangeSupport.firePropertyChange("activityStatus",
            oldActivityStatus, activityStatus);
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
        if (getActivityStatus() == null) {
            throw new BiobankCheckException(
                "the clinic does not have an activity status");
        }
        if (getSite() == null) {
            throw new BiobankCheckException("the clinic does not have a site");
        }
        checkNotEmpty(getName(), "Name");
        checkNoDuplicatesInSite(Clinic.class, "name", getName(), getSite()
            .getId(), "A clinic with name \"" + getName()
            + "\" already exists.");
        checkNotEmpty(getNameShort(), "Short Name");
        checkNoDuplicatesInSite(Clinic.class, "nameShort", getNameShort(),
            getSite().getId(), "A clinic with short name \"" + getNameShort()
                + "\" already exists.");
    }

    @Override
    protected void persistDependencies(Clinic origObject) throws Exception {
        for (ContactWrapper cw : deletedContacts) {
            if (!cw.isNew()) {
                cw.delete();
            }
        }
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
        return getContactCollection(true);
    }

    private void setContacts(Collection<Contact> allContactsObjects,
        List<ContactWrapper> allContactsWrappers) {
        Collection<Contact> oldContacts = wrappedObject.getContactCollection();
        wrappedObject.setContactCollection(allContactsObjects);
        propertyChangeSupport.firePropertyChange("contactCollection",
            oldContacts, allContactsObjects);
        propertiesMap.put("contactCollection", allContactsWrappers);
    }

    public void addContacts(List<ContactWrapper> newContacts) {
        if (newContacts != null && newContacts.size() > 0) {
            Collection<Contact> allContactsObjects = new HashSet<Contact>();
            List<ContactWrapper> allContactsWrappers = new ArrayList<ContactWrapper>();
            // already added contacts
            List<ContactWrapper> currentList = getContactCollection();
            if (currentList != null) {
                for (ContactWrapper contact : currentList) {
                    allContactsObjects.add(contact.getWrappedObject());
                    allContactsWrappers.add(contact);
                }
            }
            // new contacts added
            for (ContactWrapper contact : newContacts) {
                allContactsObjects.add(contact.getWrappedObject());
                allContactsWrappers.add(contact);
                deletedContacts.remove(contact);
            }
            setContacts(allContactsObjects, allContactsWrappers);
        }
    }

    public void removeContacts(List<ContactWrapper> contactsToDelete) {
        if (contactsToDelete != null && contactsToDelete.size() > 0) {
            deletedContacts.addAll(contactsToDelete);
            Collection<Contact> allContactsObjects = new HashSet<Contact>();
            List<ContactWrapper> allContactsWrappers = new ArrayList<ContactWrapper>();
            // already added contacts
            List<ContactWrapper> currentList = getContactCollection();
            if (currentList != null) {
                for (ContactWrapper contact : currentList) {
                    if (!deletedContacts.contains(contact)) {
                        allContactsObjects.add(contact.getWrappedObject());
                        allContactsWrappers.add(contact);
                    }
                }
            }
            setContacts(allContactsObjects, allContactsWrappers);
        }
    }

    /**
     * Search for a contact in the clinic with the given name
     */
    public ContactWrapper getContact(String contactName)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria("from "
            + Contact.class.getName() + " where clinic.id = ? and name = ?",
            Arrays.asList(new Object[] { getId(), contactName }));
        List<Contact> contacts = appService.query(criteria);
        if (contacts.size() == 1) {
            return new ContactWrapper(appService, contacts.get(0));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<StudyWrapper> getStudyCollection() throws ApplicationException {
        List<StudyWrapper> studyCollection = (List<StudyWrapper>) propertiesMap
            .get("studyCollection");

        if (studyCollection == null) {
            studyCollection = new ArrayList<StudyWrapper>();
            HQLCriteria c = new HQLCriteria("select distinct studies from "
                + Contact.class.getName() + " as contacts"
                + " inner join contacts.studyCollection as studies"
                + " where contacts.clinic.id = ? order by studies.nameShort",
                Arrays.asList(new Object[] { getId() }));
            List<Study> collection = appService.query(c);
            for (Study study : collection) {
                studyCollection.add(new StudyWrapper(appService, study));
            }
            propertiesMap.put("studyCollection", studyCollection);
        }
        return studyCollection;
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

    public void addShipments(Collection<ShipmentWrapper> newShipments) {
        if (newShipments != null && newShipments.size() > 0) {
            Collection<Shipment> allShipmentObjects = new HashSet<Shipment>();
            List<ShipmentWrapper> allShipmentWrappers = new ArrayList<ShipmentWrapper>();
            // already added shipments
            List<ShipmentWrapper> currentList = getShipmentCollection();
            if (currentList != null) {
                for (ShipmentWrapper ship : currentList) {
                    allShipmentObjects.add(ship.getWrappedObject());
                    allShipmentWrappers.add(ship);
                }
            }
            for (ShipmentWrapper ship : newShipments) {
                allShipmentObjects.add(ship.getWrappedObject());
                allShipmentWrappers.add(ship);
            }
            Collection<Shipment> oldCollection = wrappedObject
                .getShipmentCollection();
            wrappedObject.setShipmentCollection(allShipmentObjects);
            propertyChangeSupport.firePropertyChange("shipmentCollection",
                oldCollection, allShipmentObjects);
            propertiesMap.put("shipmentCollection", allShipmentWrappers);
        }
    }

    /**
     * Search for a shipment in the clinic with the given date received
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
            + "and patients.pnumber = ?", Arrays.asList(new Object[] { getId(),
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

    public long getPatientCount() throws ApplicationException,
        BiobankCheckException {
        HQLCriteria c = new HQLCriteria("select count(distinct patients) from "
            + Clinic.class.getName() + " as clinic"
            + " join clinic.shipmentCollection as shipments"
            + " join shipments.patientCollection as patients"
            + " where clinic.id=?", Arrays.asList(new Object[] { getId() }));

        List<Long> result = appService.query(c);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return result.get(0);
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

    @Override
    protected void resetInternalField() {
        deletedContacts.clear();
    }

}
