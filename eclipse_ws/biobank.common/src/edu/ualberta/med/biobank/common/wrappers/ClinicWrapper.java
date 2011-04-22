package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.common.peer.ClinicPeer;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.ContactPeer;
import edu.ualberta.med.biobank.common.peer.OriginInfoPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ClinicBaseWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ClinicWrapper extends ClinicBaseWrapper {

    private static final String STUDY_COLLECTION_CACHE_KEY = "studyCollection";
    private Set<ContactWrapper> deletedContacts = new HashSet<ContactWrapper>();

    public ClinicWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public ClinicWrapper(WritableApplicationService appService,
        Clinic wrappedObject) {
        super(appService, wrappedObject);
        setSendsShipments(getSendsShipments() == null ? false
            : getSendsShipments());
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
        List<StudyWrapper> studyCollection = (List<StudyWrapper>) cache
            .get(STUDY_COLLECTION_CACHE_KEY);

        if (studyCollection == null) {
            studyCollection = new ArrayList<StudyWrapper>();
            HQLCriteria c = new HQLCriteria(STUDY_COLLECTION_QUERY,
                Arrays.asList(new Object[] { getId() }));
            List<Study> collection = appService.query(c);
            for (Study study : collection) {
                studyCollection.add(new StudyWrapper(appService, study));
            }
            cache.put(STUDY_COLLECTION_CACHE_KEY, studyCollection);
        }
        return studyCollection;
    }

    @Override
    protected void deleteChecks() throws BiobankDeleteException,
        ApplicationException {
        List<StudyWrapper> studies = getStudyCollection();
        if (studies != null && studies.size() > 0) {
            throw new BiobankDeleteException("Unable to delete clinic "
                + getName() + ". No more study reference should exist.");
        }
    }

    @Override
    public int compareTo(ModelWrapper<Clinic> wrapper) {
        if (wrapper instanceof ClinicWrapper) {
            String myName = wrappedObject.getName();
            String wrapperName = wrapper.wrappedObject.getName();
            return myName.compareTo(wrapperName);
        }
        return 0;
    }

    public static final String PATIENT_COUNT_QRY = "select count(distinct patients) from "
        + Clinic.class.getName()
        + " as clinic join clinic."
        + ClinicPeer.ORIGIN_INFO_COLLECTION.getName()
        + " as oi join oi."
        + OriginInfoPeer.SPECIMEN_COLLECTION.getName()
        + " as spcs join spcs."
        + SpecimenPeer.COLLECTION_EVENT.getName()
        + " as cevents join cevents."
        + CollectionEventPeer.PATIENT.getName()
        + " as patients where clinic."
        + ClinicPeer.ID.getName() + "=?";

    /**
     * return number of patients that came for a visit in this clinic
     */
    @Override
    public Long getPatientCount() throws BiobankException, ApplicationException {
        HQLCriteria criteria = new HQLCriteria(PATIENT_COUNT_QRY,
            Arrays.asList(new Object[] { getId() }));
        return getCountResult(appService, criteria);
    }

    public static final String PATIENT_COUNT_FOR_STUDY_QRY = "select count(distinct patients) from "
        + Clinic.class.getName()
        + " as clinic join clinic."
        + ClinicPeer.ORIGIN_INFO_COLLECTION.getName()
        + " as oi join oi."
        + OriginInfoPeer.SPECIMEN_COLLECTION.getName()
        + " as spcs join spcs."
        + SpecimenPeer.COLLECTION_EVENT.getName()
        + " as cevents join cevents."
        + CollectionEventPeer.PATIENT.getName()
        + " as patients where clinic."
        + ClinicPeer.ID.getName()
        + "=? and patients."
        + Property.concatNames(PatientPeer.STUDY, StudyPeer.ID) + "=?";

    @Override
    public long getPatientCountForStudy(StudyWrapper study)
        throws ApplicationException, BiobankException {
        HQLCriteria c = new HQLCriteria(PATIENT_COUNT_FOR_STUDY_QRY,
            Arrays.asList(new Object[] { getId(), study.getId() }));
        return getCountResult(appService, c);
    }

    private static final String ALL_CLINICS_QRY = "from "
        + Clinic.class.getName();

    public static List<ClinicWrapper> getAllClinics(
        WritableApplicationService appService) throws ApplicationException {
        List<ClinicWrapper> wrappers = new ArrayList<ClinicWrapper>();
        HQLCriteria c = new HQLCriteria(ALL_CLINICS_QRY);
        List<Clinic> clinics = appService.query(c);
        for (Clinic clinic : clinics)
            wrappers.add(new ClinicWrapper(appService, clinic));
        return wrappers;
    }

    private static final String CLINIC_COUNT_QRY = "select count (*) from "
        + Clinic.class.getName();

    public static long getCount(WritableApplicationService appService)
        throws BiobankException, ApplicationException {
        return getCountResult(appService, new HQLCriteria(CLINIC_COUNT_QRY));
    }

    @Override
    protected void resetInternalFields() {
        deletedContacts.clear();
    }

    public static final String COLLECTION_EVENT_COUNT_QRY = "select count(cevent) from "
        + Clinic.class.getName()
        + " as clinic join clinic."
        + ClinicPeer.ORIGIN_INFO_COLLECTION.getName()
        + " as origins join origins."
        + OriginInfoPeer.SPECIMEN_COLLECTION.getName()
        + " as spcs join spcs."
        + SpecimenPeer.COLLECTION_EVENT.getName()
        + " as cevent where clinic."
        + CenterPeer.ID.getName() + "=?";

    @Override
    public long getCollectionEventCount() throws ApplicationException,
        BiobankException {
        HQLCriteria criteria = new HQLCriteria(COLLECTION_EVENT_COUNT_QRY,
            Arrays.asList(new Object[] { getId() }));
        return getCountResult(appService, criteria);
    }

    private static final String COLLECTION_EVENT_COUNT_FOR_STUDY_QRY = "select count(distinct cEvent) from "
        + Clinic.class.getName()
        + " as clinic join clinic."
        + ClinicPeer.ORIGIN_INFO_COLLECTION.getName()
        + " as origins join origins."
        + OriginInfoPeer.SPECIMEN_COLLECTION.getName()
        + " as specimens join specimens."
        + SpecimenPeer.COLLECTION_EVENT.getName()
        + " as cEvent where clinic."
        + ClinicPeer.ID.getName()
        + "=? and "
        + "cEvent."
        + Property.concatNames(CollectionEventPeer.PATIENT, PatientPeer.STUDY,
            StudyPeer.ID) + "=?";

    /**
     * Count events for specimen that are been drawn at this clinic
     */
    @Override
    public long getCollectionEventCountForStudy(StudyWrapper study)
        throws ApplicationException, BiobankException {
        HQLCriteria c = new HQLCriteria(COLLECTION_EVENT_COUNT_FOR_STUDY_QRY,
            Arrays.asList(new Object[] { getId(), study.getId() }));
        return getCountResult(appService, c);
    }

    @Override
    public void removeFromContactCollection(
        List<ContactWrapper> contactCollection) {
        super.removeFromContactCollection(contactCollection);
        deletedContacts.addAll(contactCollection);
    }

    @Override
    public void removeFromContactCollectionWithCheck(
        List<ContactWrapper> contactCollection) throws BiobankCheckException {
        super.removeFromContactCollectionWithCheck(contactCollection);
        deletedContacts.addAll(contactCollection);
    }

}
