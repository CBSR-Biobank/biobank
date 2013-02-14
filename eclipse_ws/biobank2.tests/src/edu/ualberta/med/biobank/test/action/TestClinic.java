package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.center.CenterGetStudyListAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicDeleteAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetAllAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetInfoAction.ClinicInfo;
import edu.ualberta.med.biobank.common.action.clinic.ClinicGetStudyInfoAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction;
import edu.ualberta.med.biobank.common.action.clinic.ClinicSaveAction.ContactSaveInfo;
import edu.ualberta.med.biobank.common.action.clinic.ContactsGetAllAction;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.ClinicHelper;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;

public class TestClinic extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;

    private ClinicSaveAction clinicSaveAction;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();

        clinicSaveAction = ClinicHelper.getSaveAction(name, name,
            ActivityStatus.ACTIVE, getR().nextBoolean());
    }

    @Test
    public void saveNew() throws Exception {
        clinicSaveAction.setName(name);

        Address address = new Address();
        address.setCity(name);
        clinicSaveAction.setAddress(address);
        clinicSaveAction.setContactSaveInfos(null);
        try {
            exec(clinicSaveAction);
            Assert.fail(
                "should not be allowed to add site with null site ids");
        } catch (NullPointerException e) {
            Assert.assertTrue(true);
        }

        // success path
        clinicSaveAction
            .setContactSaveInfos(new HashSet<ContactSaveInfo>());
        exec(clinicSaveAction);
    }

    @Test
    public void checkGetAction() throws Exception {
        session.beginTransaction();
        Provisioning provisioning = new Provisioning(session, factory);
        session.getTransaction().commit();

        CollectionEventHelper.createCEventWithSourceSpecimens(getExecutor(),
            provisioning.patientIds.get(0), provisioning.getClinic());

        ClinicInfo clinicInfo =
            exec(new ClinicGetInfoAction(provisioning.clinicId));

        Assert.assertEquals(ActivityStatus.ACTIVE,
            clinicInfo.clinic.getActivityStatus());
        Assert.assertEquals(new Long(1), clinicInfo.patientCount);
        Assert.assertEquals(new Long(1), clinicInfo.collectionEventCount);
        Assert.assertEquals(1, clinicInfo.contacts.size());
        Assert.assertEquals(1, clinicInfo.studyInfos.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getAllAction() {
        // ensure at least one clinic in the DB
        session.beginTransaction();
        factory.createClinic();
        session.getTransaction().commit();

        List<Clinic> expectedClinics = session.createCriteria(Clinic.class).list();
        List<Clinic> actionClinics = exec(new ClinicGetAllAction()).getList();
        Assert.assertEquals(expectedClinics, actionClinics);

    }

    @Test
    public void comments() {
        // save with no comments
        Integer clinicId = exec(clinicSaveAction).getId();
        ClinicInfo clinicInfo =
            exec(new ClinicGetInfoAction(clinicId));
        Assert.assertEquals(0, clinicInfo.clinic.getComments().size());

        clinicInfo = addComment(clinicId);
        Assert.assertEquals(1, clinicInfo.clinic.getComments().size());

        clinicInfo = addComment(clinicId);
        Assert.assertEquals(2, clinicInfo.clinic.getComments().size());

        // TODO: check full name on each comment's user
        // for (Comment comment : clinicInfo.clinic.getCommentCollection()) {
        //
        // }
    }

    private ClinicInfo addComment(Integer clinicId) {
        ClinicSaveAction clinicSaveAction = ClinicHelper.getSaveAction(
            exec(new ClinicGetInfoAction(clinicId)));
        clinicSaveAction.setCommentText(Utils.getRandomString(20, 30));
        exec(clinicSaveAction).getId();
        return exec(new ClinicGetInfoAction(clinicId));
    }

    @Test
    public void getInfoActionContacts() throws Exception {
        session.beginTransaction();
        Clinic clinic = factory.createClinic();
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(factory.createContact());
        contacts.add(factory.createContact());
        session.getTransaction().commit();

        ClinicInfo clinicInfo = exec(new ClinicGetInfoAction(clinic.getId()));
        Assert.assertEquals(contacts.size(), clinicInfo.contacts.size());
        Assert.assertTrue(clinicInfo.contacts.containsAll(contacts));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void contactsGetAll() throws Exception {
        List<Contact> contactsBeforeTest = session.createCriteria(Contact.class).list();

        Set<Clinic> clinics = new HashSet<Clinic>();
        Set<Contact> contacts = new HashSet<Contact>();
        session.beginTransaction();
        for (int i = 0; i < 3; ++i) {
            Clinic clinic = factory.createClinic();
            for (int j = 0; j < 2; ++j) {
                Contact contact = factory.createContact();
                clinic.getContacts().add(contact);
                contacts.add(contact);
            }
            clinics.add(clinic);
        }
        session.getTransaction().commit();

        List<Contact> actionContacts = exec(new ContactsGetAllAction()).getList();
        Assert.assertEquals(contactsBeforeTest.size() + contacts.size(), actionContacts.size());
        Assert.assertTrue(actionContacts.containsAll(contacts));

        // remove all clinics
        session.beginTransaction();
        for (Clinic clinic : clinics) {
            session.delete(clinic);
        }
        session.getTransaction().commit();
        Assert.assertEquals(contactsBeforeTest.size(),
            session.createCriteria(Contact.class).list().size());
    }

    @Test
    public void delete() {
        // delete a study with no patients and no other associations
        Integer clinicId = exec(clinicSaveAction).getId();
        ClinicInfo clinicInfo =
            exec(new ClinicGetInfoAction(clinicId));
        exec(new ClinicDeleteAction(clinicInfo.clinic));

        // hql query for clinic should return empty
        Query q = session.createQuery("SELECT COUNT(*) FROM "
            + Clinic.class.getName() + " WHERE id=?");
        q.setParameter(0, clinicId);
        Long result = HibernateUtil.getCountFromQuery(q);

        Assert.assertTrue(result.equals(0L));
    }

    @Test
    public void getStudyList() {
        session.beginTransaction();
        Clinic clinic = factory.createClinic();
        Set<Study> studies = new HashSet<Study>();

        for (int i = 0; i < 3; ++i) {
            Study study = factory.createStudy();
            Contact contact = factory.createContact();
            contact.getStudies().add(study);
            study.getContacts().add(contact);
            studies.add(study);
            clinic.getContacts().add(contact);
        }
        session.getTransaction().commit();

        List<Study> actionStudies = exec(new CenterGetStudyListAction(clinic)).getList();

        Assert.assertEquals(studies.size(), actionStudies.size());
        Assert.assertTrue(actionStudies.containsAll(studies));

        // remove last study from contacts
        session.beginTransaction();
        Study study = factory.getDefaultStudy();
        Contact contact = study.getContacts().iterator().next();
        contact.getStudies().remove(study);
        study.getContacts().remove(contact);
        session.update(contact);
        session.update(study);
        session.getTransaction().commit();

        actionStudies = exec(new CenterGetStudyListAction(clinic)).getList();
        Assert.assertEquals(studies.size() - 1, actionStudies.size());
    }

    @Test
    public void getStudyInfoNoStudies() {
        session.beginTransaction();
        Clinic clinic = factory.createClinic();
        session.getTransaction().commit();

        List<StudyCountInfo> countData = exec(
            new ClinicGetStudyInfoAction(clinic.getId())).getList();
        Assert.assertEquals(0, countData.size());

    }

    @Test
    public void getStudyInfoStudiesNoPatients() {
        session.beginTransaction();
        Clinic clinic = factory.createClinic();
        Contact contact = factory.createContact();
        Study study = factory.createStudy();
        study.getContacts().add(contact);
        session.update(study);
        session.getTransaction().commit();

        List<StudyCountInfo> countData = exec(
            new ClinicGetStudyInfoAction(clinic.getId())).getList();
        Assert.assertEquals(1, countData.size());

        StudyCountInfo countInfo = countData.get(0);

        Assert.assertEquals(study, countInfo.getStudy());
        Assert.assertEquals(0L, countInfo.getPatientCount().longValue());
        Assert.assertEquals(0L, countInfo.getCollectionEventCount().longValue());

    }

    @Test
    public void getStudyInfoStudiesWithSpecimens() {
        session.beginTransaction();
        Clinic clinic = factory.createClinic();
        Contact contact = factory.createContact();
        Study study = factory.createStudy();
        study.getContacts().add(contact);
        factory.createCollectionEvent();
        factory.createParentSpecimen();
        session.update(study);
        session.getTransaction().commit();

        List<StudyCountInfo> countData = exec(
            new ClinicGetStudyInfoAction(clinic.getId())).getList();
        Assert.assertEquals(1, countData.size());

        StudyCountInfo countInfo = countData.get(0);

        Assert.assertEquals(study, countInfo.getStudy());
        Assert.assertEquals(1L, countInfo.getPatientCount().longValue());
        Assert.assertEquals(1L, countInfo.getCollectionEventCount().longValue());

    }
}
