package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ClinicBaseWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ClinicWrapper extends ClinicBaseWrapper {

    private Set<ContactWrapper> deletedContacts = new HashSet<ContactWrapper>();

    public ClinicWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ClinicWrapper(WritableApplicationService appService,
        Clinic wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return ClinicPeer.PROP_NAMES;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkNoDuplicates(Clinic.class, ClinicPeer.NAME.getName(), getName(),
            "A clinic with name");
        checkNoDuplicates(Clinic.class, ClinicPeer.NAME_SHORT.getName(),
            getNameShort(), "A clinic with name short");
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

    /**
     * Search for a contact in the clinic with the given name
     */
    public ContactWrapper getContact(String contactName) {
        List<ContactWrapper> contacts = getContactCollection(false);
        if (contacts != null)
            for (ContactWrapper contact : contacts)
                if (contact.getName().equals(contactName))
                    return contact;
        return null;
    }

    private static final String STUDY_COLLECTION_QUERY = "select distinct studies from "
        + Contact.class.getName()
        + " as contacts inner join contacts."
        + ContactPeer.STUDY_COLLECTION.getName()
        + " as studies where contacts."
        + Property.concatNames(ContactPeer.CLINIC, ClinicPeer.ID)
        + " = ? order by studies.nameShort";

    @SuppressWarnings("unchecked")
    public List<StudyWrapper> getStudyCollection() throws ApplicationException {
        List<StudyWrapper> studyCollection = (List<StudyWrapper>) propertiesMap
            .get("studyCollection");

        if (studyCollection == null) {
            studyCollection = new ArrayList<StudyWrapper>();
            HQLCriteria c = new HQLCriteria(STUDY_COLLECTION_QUERY,
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
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (getCollectionEventCount() > 0) {
            throw new BiobankCheckException("Unable to delete clinic "
                + getName() + ". All defined shipments must be removed first.");
        }
        List<StudyWrapper> studies = getStudyCollection();
        if (studies != null && studies.size() > 0) {
            throw new BiobankCheckException("Unable to delete clinic "
                + getName() + ". No more study reference should exist.");
        }
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

    public static final String PATIENT_COUNT_QRY = "select count(distinct svs.patient) from "
        + Clinic.class.getName()
        + " as clinic join clinic."
        + ClinicPeer.COLLECTION_EVENT_COLLECTION.getName()
        + " as shipments join shipments."
        + CollectionEventPeer.SOURCE_VESSEL_COLLECTION.getName()
        + " as svs"
        + " where clinic." + ClinicPeer.ID.getName() + " = ?";

    /**
     * fast = true will execute a hql query. fast = false will call the
     * getShipmentCollection() method and loop on it to get patients
     * 
     * @throws BiobankCheckException
     * @throws ApplicationException
     */
    public long getPatientCount(boolean fast) throws BiobankException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(PATIENT_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            List<Long> results = appService.query(criteria);
            if (results.size() != 1) {
                throw new BiobankQueryResultSizeException();
            }
            return results.get(0);
        }
        HashSet<PatientWrapper> uniquePatients = new HashSet<PatientWrapper>();
        List<CollectionEventWrapper> ships = getCollectionEventCollection(false);
        if (ships != null)
            for (CollectionEventWrapper ship : ships) {
                if (ship.getPatientCollection() != null) {
                    Collection<SourceVesselWrapper> svCollection = ship
                        .getSourceVesselCollection(false);
                    for (SourceVesselWrapper sv : svCollection)
                        uniquePatients.add(sv.getPatient());
                }
            }
        return uniquePatients.size();
    }

    public static List<ClinicWrapper> getAllClinics(
        WritableApplicationService appService) throws ApplicationException {
        List<ClinicWrapper> wrappers = new ArrayList<ClinicWrapper>();
        HQLCriteria c = new HQLCriteria("from " + Clinic.class.getName());
        List<Clinic> clinics = appService.query(c);
        for (Clinic clinic : clinics)
            wrappers.add(new ClinicWrapper(appService, clinic));
        return wrappers;
    }

    public static long getCount(WritableApplicationService appService)
        throws BiobankException, ApplicationException {
        HQLCriteria c = new HQLCriteria("select count (*) from "
            + Clinic.class.getName());
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankQueryResultSizeException();
        }
        return results.get(0);
    }

    @Override
    protected void resetInternalFields() {
        setAddress(null);
        deletedContacts.clear();
    }
}
