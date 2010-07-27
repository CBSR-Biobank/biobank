package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.DateCompare;
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
    private AddressWrapper address;
    private ActivityStatusWrapper activityStatus;

    public ClinicWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ClinicWrapper(WritableApplicationService appService,
        Clinic wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "nameShort", "activityStatus",
            "sendsShipments", "comment", "address", "site",
            "contactCollection", "shipmentCollection", "street1", "street2",
            "city", "province", "postalCode", "patientVisitCollection" };
    }

    private AddressWrapper getAddress() {
        if (address == null) {
            Address a = wrappedObject.getAddress();
            if (a == null)
                return null;
            address = new AddressWrapper(appService, a);
        }
        return address;
    }

    private void setAddress(Address address) {
        if (address == null)
            this.address = null;
        else
            this.address = new AddressWrapper(appService, address);
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

    public void setSendsShipments(Boolean sendsShipments) {
        Boolean oldSendsShipments = wrappedObject.getSendsShipments();
        wrappedObject.setSendsShipments(sendsShipments);
        propertyChangeSupport.firePropertyChange("sendsShipments",
            oldSendsShipments, sendsShipments);
    }

    public Boolean getSendsShipments() {
        return wrappedObject.getSendsShipments();
    }

    public ActivityStatusWrapper getActivityStatus() {
        if (activityStatus == null) {
            ActivityStatus a = wrappedObject.getActivityStatus();
            if (a == null)
                return null;
            activityStatus = new ActivityStatusWrapper(appService, a);
        }
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatusWrapper activityStatus) {
        this.activityStatus = activityStatus;
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
        if (getAddress() == null) {
            return null;
        }
        return address.getStreet1();
    }

    public void setStreet1(String street1) {
        String old = getStreet1();
        if (getAddress() == null) {
            address = initAddress();
        }
        wrappedObject.getAddress().setStreet1(street1);
        propertyChangeSupport.firePropertyChange("street1", old, street1);
    }

    public String getStreet2() {
        if (getAddress() == null) {
            return null;
        }
        return address.getStreet2();
    }

    public void setStreet2(String street2) {
        String old = getStreet2();
        if (getAddress() == null) {
            address = initAddress();
        }
        wrappedObject.getAddress().setStreet2(street2);
        propertyChangeSupport.firePropertyChange("street2", old, street2);
    }

    public String getCity() {
        if (getAddress() == null) {
            return null;
        }
        return address.getCity();
    }

    public void setCity(String city) {
        String old = getCity();
        if (getAddress() == null) {
            address = initAddress();
        }
        wrappedObject.getAddress().setCity(city);
        propertyChangeSupport.firePropertyChange("city", old, city);
    }

    public String getProvince() {
        if (getAddress() == null) {
            return null;
        }
        return address.getProvince();
    }

    public void setProvince(String province) {
        String old = getProvince();
        if (getAddress() == null) {
            address = initAddress();
        }
        wrappedObject.getAddress().setProvince(province);
        propertyChangeSupport.firePropertyChange("province", old, province);
    }

    public String getPostalCode() {
        if (getAddress() == null) {
            return null;
        }
        return address.getPostalCode();
    }

    public void setPostalCode(String postalCode) {
        String old = getPostalCode();
        if (getAddress() == null) {
            address = initAddress();
        }
        wrappedObject.getAddress().setPostalCode(postalCode);
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
    public ContactWrapper getContact(String contactName) {
        List<ContactWrapper> contacts = getContactCollection();
        if (contacts != null)
            for (ContactWrapper contact : contacts)
                if (contact.getName().equals(contactName))
                    return contact;
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
        if (getShipmentCount() > 0) {
            throw new BiobankCheckException("Unable to delete clinic "
                + getName() + ". All defined shipments must be removed first.");
        }
        List<StudyWrapper> studies = getStudyCollection();
        if (studies != null && studies.size() > 0) {
            throw new BiobankCheckException("Unable to delete clinic "
                + getName() + ". No more study reference should exist.");
        }
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
            } else
                return new ArrayList<ShipmentWrapper>();
        }
        return shipmentCollection;
    }

    public long getShipmentCount() {
        return getShipmentCollection().size();
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
    public ShipmentWrapper getShipment(Date dateReceived) {
        List<ShipmentWrapper> shipments = getShipmentCollection();
        if (shipments != null) {
            for (ShipmentWrapper ship : shipments) {
                if (DateCompare.compare(ship.getDateReceived(), dateReceived) == 0)
                    return ship;
            }
        }
        return null;
    }

    /**
     * Search for a shipment in the clinic with the given date received and
     * patient number.
     */
    public ShipmentWrapper getShipment(Date dateReceived, String patientNumber) {
        List<ShipmentWrapper> shipments = getShipmentCollection();
        if (shipments != null)
            for (ShipmentWrapper ship : shipments)
                if (DateCompare.compare(ship.getDateReceived(), dateReceived) == 0) {
                    List<PatientWrapper> patients = ship.getPatientCollection();
                    for (PatientWrapper p : patients)
                        if (p.getPnumber().equals(patientNumber))
                            return ship;
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

    public long getPatientCount() {
        HashSet<PatientWrapper> uniquePatients = new HashSet<PatientWrapper>();
        List<ShipmentWrapper> ships = getShipmentCollection();
        if (ships != null)
            for (ShipmentWrapper ship : ships) {
                if (ship.getPatientCollection() != null)
                    uniquePatients.addAll(ship.getPatientCollection());
            }
        return uniquePatients.size();
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
                + " as pv where shipment.clinic.id = ?",
                Arrays.asList(new Object[] { getId() }));
            List<PatientVisit> collection = appService.query(c);
            for (PatientVisit pv : collection) {
                pvCollection.add(new PatientVisitWrapper(appService, pv));
            }
            propertiesMap.put("patientVisitCollection", pvCollection);
        }
        return pvCollection;
    }

    public long getPatientVisitCount() throws ApplicationException {
        if (getPatientVisitCollection() == null)
            return 0;
        else
            return getPatientVisitCollection().size();
    }

    public static List<ClinicWrapper> getAllClinics(
        WritableApplicationService appService) throws ApplicationException {
        List<Clinic> clinics = new ArrayList<Clinic>();
        List<ClinicWrapper> wrappers = new ArrayList<ClinicWrapper>();
        HQLCriteria c = new HQLCriteria("from " + Clinic.class.getName());
        clinics = appService.query(c);
        for (Clinic clinic : clinics)
            wrappers.add(new ClinicWrapper(appService, clinic));
        return wrappers;
    }

    @Override
    protected void resetInternalField() {
        deletedContacts.clear();
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        address = null;
        activityStatus = null;
    }

}
