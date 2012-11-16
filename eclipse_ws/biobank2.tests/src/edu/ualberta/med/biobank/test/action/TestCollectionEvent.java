package edu.ualberta.med.biobank.test.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventDeleteAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetEventAttrInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.CEventAttrSaveInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.EventAttrInfo;
import edu.ualberta.med.biobank.common.action.eventattr.GlobalEventAttrInfo;
import edu.ualberta.med.biobank.common.action.eventattr.GlobalEventAttrInfoGetAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudyEventAttrSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyInfo;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.util.MathUtil;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;

public class TestCollectionEvent extends TestAction {

    private Provisioning provisioning;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        session.beginTransaction();
        provisioning = new Provisioning(session, factory);
        session.getTransaction().commit();
    }

    @Test
    public void saveNoSpecsNoAttrs() throws Exception {
        final Integer visitNumber = getR().nextInt(20) + 1;
        final String commentText = Utils.getRandomString(20, 30);

        // test add
        final Integer ceventId = exec(
            new CollectionEventSaveAction(null, provisioning.patientIds.get(0),
                visitNumber, ActivityStatus.ACTIVE, commentText, null,
                null, provisioning.getClinic())).getId();

        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent =
            (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(ActivityStatus.ACTIVE, cevent.getActivityStatus());
        Assert.assertEquals(1, cevent.getComments().size());
    }

    @Test
    public void saveWithSpecs() throws Exception {
        final Integer visitNumber = getR().nextInt(20) + 1;
        final String commentText = getMethodNameR();

        final Integer typeId = getSpecimenTypes().get(0).getId();

        final Map<String, SaveCEventSpecimenInfo> specs =
            CollectionEventHelper.createSaveCEventSpecimenInfoRandomList(
                5, typeId, getExecutor().getUserId());

        // Save a new cevent
        final Integer ceventId = exec(
            new CollectionEventSaveAction(null, provisioning.patientIds.get(0),
                visitNumber, ActivityStatus.ACTIVE, commentText,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null,
                provisioning.getClinic()))
                .getId();

        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent = (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        Assert.assertNotNull(cevent);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(ActivityStatus.ACTIVE, cevent.getActivityStatus());
        Assert.assertNotNull(cevent.getComments());
        Assert.assertEquals(1, cevent.getComments().size());
        Assert.assertEquals(specs.size(), cevent.getAllSpecimens().size());
        Assert.assertEquals(specs.size(), cevent.getOriginalSpecimens().size());

        for (Specimen sp : cevent.getOriginalSpecimens()) {
            Assert.assertEquals(typeId, sp.getSpecimenType().getId());
            SaveCEventSpecimenInfo info = specs.get(sp.getInventoryId());
            Assert.assertNotNull(info);
            if (info != null) {
                Assert.assertEquals(1, sp.getComments().size());
                Assert.assertEquals(info.inventoryId, sp.getInventoryId());
                Assert.assertTrue(MathUtil.equals(info.quantity, sp.getQuantity()));
                Assert.assertEquals(info.specimenTypeId, sp.getSpecimenType().getId());
                Assert.assertEquals(info.activityStatus, sp.getActivityStatus());
                Assert.assertEquals(info.createdAt, sp.getCreatedAt());

                // set the id to make some modification tests after that.
                info = new SaveCEventSpecimenInfo(info, sp.getId());
            }
        }

        // Save the same cevent with only one kept from previous list (and modified) and with a new one
        List<SaveCEventSpecimenInfo> newSpecList = new ArrayList<SaveCEventSpecimenInfo>();

        SaveCEventSpecimenInfo spcInfoToCopy = specs.values().iterator().next();
        SaveCEventSpecimenInfo modifiedSpec = new SaveCEventSpecimenInfo(spcInfoToCopy.id,
            spcInfoToCopy.inventoryId + "Modified", spcInfoToCopy.createdAt, spcInfoToCopy.activityStatus,
            spcInfoToCopy.specimenTypeId, null, spcInfoToCopy.quantity.add(BigDecimal.ONE));
        newSpecList.add(modifiedSpec);
        SaveCEventSpecimenInfo newSpec =
            CollectionEventHelper.createSaveCEventSpecimenInfoRandom(typeId,
                getExecutor().getUserId());
        newSpecList.add(newSpec);
        // modify cevent
        exec(new CollectionEventSaveAction(ceventId, provisioning.patientIds.get(0),
            visitNumber + 1, ActivityStatus.ACTIVE, commentText, newSpecList, null,
            provisioning.getClinic()));

        // Check CollectionEvent is modified
        session.clear();
        cevent =
            (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        session.refresh(cevent);
        Assert
        .assertEquals(visitNumber + 1, cevent.getVisitNumber().intValue());
        Assert.assertEquals(2, cevent.getAllSpecimens().size());
        Assert.assertEquals(2, cevent.getOriginalSpecimens().size());
        for (Iterator<Specimen> iter =
            cevent.getOriginalSpecimens().iterator(); iter.hasNext();) {
            Specimen sp = iter.next();
            Assert.assertEquals(typeId, sp.getSpecimenType().getId());
            Assert.assertTrue(sp.getInventoryId().equals(newSpec.inventoryId)
                || sp.getInventoryId().equals(modifiedSpec.inventoryId));
            if (sp.getInventoryId().equals(newSpec.inventoryId)) {
                Assert.assertNotNull(sp.getComments());
                Assert.assertEquals(1, sp.getComments().size());
                Assert.assertEquals(newSpec.inventoryId, sp.getInventoryId());
                Assert.assertTrue(MathUtil.equals(newSpec.quantity,
                    sp.getQuantity()));
                Assert.assertEquals(newSpec.specimenTypeId, sp
                    .getSpecimenType().getId());
                Assert.assertEquals(newSpec.activityStatus,
                    sp.getActivityStatus());
                Assert.assertEquals(newSpec.createdAt, sp.getCreatedAt());
            }
            if (sp.getInventoryId().equals(modifiedSpec.inventoryId)) {
                Assert.assertEquals(modifiedSpec.inventoryId,
                    sp.getInventoryId());
                Assert
                .assertTrue(MathUtil.equals(modifiedSpec.quantity,
                    sp.getQuantity()));
            }
        }
    }

    /*
     * Creates a colleciton event with parent specimens and child specimens, then re-saves
     * the collection event with the action and then verifies that the current centre on the
     * specimens has not changed.
     */
    @Test
    public void checkSpecimenCurrentCenter() throws Exception {
        session.beginTransaction();
        final Site site = factory.createSite();
        final Clinic clinic = factory.createClinic();
        CollectionEvent cevent = factory.createCollectionEvent();

        Set<Specimen> parentSpecimens = new HashSet<Specimen>();
        parentSpecimens.add(factory.createParentSpecimen());
        parentSpecimens.add(factory.createParentSpecimen());
        parentSpecimens.add(factory.createParentSpecimen());
        cevent.getOriginalSpecimens().addAll(parentSpecimens);

        Set<Specimen> childSpecimens = new HashSet<Specimen>();
        childSpecimens.add(factory.createChildSpecimen());
        childSpecimens.add(factory.createChildSpecimen());
        childSpecimens.add(factory.createChildSpecimen());
        cevent.getAllSpecimens().addAll(childSpecimens);

        session.getTransaction().commit();

        Set<Specimen> allSpecimens = new HashSet<Specimen>();
        allSpecimens.addAll(parentSpecimens);
        allSpecimens.addAll(childSpecimens);

        for (Specimen specimen : allSpecimens) {
            Assert.assertEquals(clinic, specimen.getCurrentCenter());
        }

        CEventInfo info = exec(new CollectionEventGetInfoAction(cevent.getId()));

        Assert.assertEquals(cevent, info.cevent);
        Assert.assertEquals(parentSpecimens.size(), info.sourceSpecimenInfos.size());
        Assert.assertEquals(childSpecimens.size(), info.aliquotedSpecimenInfos.size());

        Set<SaveCEventSpecimenInfo> spcInfo = new HashSet<SaveCEventSpecimenInfo>();
        for (Specimen specimen : parentSpecimens) {
            spcInfo.add(new SaveCEventSpecimenInfo(specimen.getId(), specimen.getInventoryId(),
                specimen.getCreatedAt(), specimen.getActivityStatus(),
                specimen.getSpecimenType().getId(), null, specimen.getQuantity()));
        }

        // re-save collection event using action
        exec(new CollectionEventSaveAction(cevent.getId(), cevent.getPatient().getId(),
            cevent.getVisitNumber(), cevent.getActivityStatus(), null,
            spcInfo, null, site));

        @SuppressWarnings("unchecked")
        List<Specimen> list = session.createCriteria(Specimen.class)
        .add(Restrictions.eq("collectionEvent.id", cevent.getId())).list();

        Assert.assertEquals(allSpecimens.size(), list.size());

        for (Specimen specimen : list) {
            Assert.assertEquals(clinic, specimen.getCurrentCenter());
        }
    }

    @Test
    public void checkGetAction() throws Exception {
        // add specimen type
        final Integer typeId = exec(new SpecimenTypeSaveAction(getMethodNameR(), getMethodNameR())).getId();

        final Map<String, SaveCEventSpecimenInfo> specs =
            CollectionEventHelper.createSaveCEventSpecimenInfoRandomList(
                5, typeId, getExecutor().getUserId());

        setEventAttrs(provisioning.studyId);
        StudyInfo studyInfo = exec(new StudyGetInfoAction(provisioning.studyId));
        Assert.assertEquals(5, studyInfo.getStudyEventAttrs().size());

        StudyEventAttr phlebotomistStudyAttr = null;
        for (StudyEventAttr attr : studyInfo.getStudyEventAttrs()) {
            if ("Phlebotomist".equals(attr.getGlobalEventAttr().getLabel())) {
                phlebotomistStudyAttr = attr;
            }
        }
        Assert.assertNotNull(phlebotomistStudyAttr);

        List<CEventAttrSaveInfo> attrs =
            new ArrayList<CollectionEventSaveAction.CEventAttrSaveInfo>();
        attrs.add(new CEventAttrSaveInfo(phlebotomistStudyAttr.getId(),
            EventAttrTypeEnum.getEventAttrType(phlebotomistStudyAttr.getGlobalEventAttr().getEventAttrType().getName()),
            "abcdefghi"));

        Integer visitNber = getR().nextInt(20) + 1;
        String commentText = Utils.getRandomString(20, 30);
        // Save a new cevent
        final Integer ceventId = exec(
            new CollectionEventSaveAction(null, provisioning.patientIds.get(0), visitNber,
                ActivityStatus.ACTIVE, commentText,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()),
                attrs, provisioning.getClinic())).getId();

        // Call get infos action
        PatientInfo patientInfo = exec(new PatientGetInfoAction(provisioning.patientIds.get(0)));
        CEventInfo info = exec(new CollectionEventGetInfoAction(ceventId));

        // no aliquoted specimens added
        Assert.assertEquals(0, info.aliquotedSpecimenInfos.size());
        Assert.assertNotNull(info.cevent);
        Assert.assertEquals(visitNber, info.cevent.getVisitNumber());
        Assert.assertEquals(ActivityStatus.ACTIVE, info.cevent.getActivityStatus());

        Assert.assertEquals(patientInfo.patient.getPnumber(), info.cevent.getPatient().getPnumber());

        Assert.assertEquals(patientInfo.patient.getStudy().getName(),
            info.cevent.getPatient().getStudy().getName());

        // TODO: add test to check if the comments' user is fetched

        Assert.assertNotNull(info.cevent.getComments());
        // FIXME sometimes size not correct !!??!!
        Assert.assertEquals(1, info.cevent.getComments().size());
        Assert.assertEquals(attrs.size(), info.eventAttrs.size());
        Assert.assertEquals(specs.size(), info.sourceSpecimenInfos.size());

        // FIXME need to add test with aliquoted specimens
    }

    @SuppressWarnings("nls")
    @Test
    public void saveWithAttrs() throws Exception {
        setEventAttrs(provisioning.studyId);
        StudyInfo studyInfo =
            exec(new StudyGetInfoAction(provisioning.studyId));
        Assert.assertEquals(5, studyInfo.getStudyEventAttrs().size());

        StudyEventAttr phlebotomistStudyAttr = null;
        for (StudyEventAttr attr : studyInfo.getStudyEventAttrs()) {
            if ("Phlebotomist".equals(attr.getGlobalEventAttr().getLabel())) {
                phlebotomistStudyAttr = attr;
            }
        }
        Assert.assertNotNull(phlebotomistStudyAttr);

        final Integer visitNumber = getR().nextInt(20) + 1;
        final String commentText = Utils.getRandomString(20, 30);

        List<CEventAttrSaveInfo> attrs =
            new ArrayList<CollectionEventSaveAction.CEventAttrSaveInfo>();
        String value1 = getMethodNameR() + "abcdefghi";

        CEventAttrSaveInfo attrInfo = new CEventAttrSaveInfo(phlebotomistStudyAttr.getId(),
            EventAttrTypeEnum.getEventAttrType(
                phlebotomistStudyAttr.getGlobalEventAttr().getEventAttrType().getName()),
                value1);
        attrs.add(attrInfo);

        // Save a new cevent
        final Integer ceventId = exec(
            new CollectionEventSaveAction(null, provisioning.patientIds.get(0),
                visitNumber, ActivityStatus.ACTIVE, commentText, null,
                attrs, provisioning.getClinic())).getId();

        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent =
            (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(ActivityStatus.ACTIVE, cevent.getActivityStatus());
        Assert.assertEquals(1, cevent.getComments()
            .size());
        Assert.assertEquals(1, cevent.getEventAttrs().size());
        EventAttr eventAttr = cevent.getEventAttrs().iterator().next();
        Assert.assertEquals(value1, eventAttr.getValue());
        Assert.assertEquals(phlebotomistStudyAttr.getId(), eventAttr
            .getStudyEventAttr().getId());

        attrs.remove(attrInfo);
        Integer eventAttrId = eventAttr.getId();
        String value2 = getMethodNameR() + "jklmnopqr";
        attrInfo = new CEventAttrSaveInfo(attrInfo.studyEventAttrId, attrInfo.type, value2);
        attrs.add(attrInfo);

        // Save with a different value for attrinfo
        exec(new CollectionEventSaveAction(ceventId,
            provisioning.patientIds.get(0), visitNumber, ActivityStatus.ACTIVE,
            commentText, null, attrs, provisioning.getClinic()));

        session.clear();
        cevent = (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        session.refresh(cevent);
        Assert.assertEquals(1, cevent.getEventAttrs().size());
        eventAttr = cevent.getEventAttrs().iterator().next();
        Assert.assertEquals(value2, eventAttr.getValue());
        Assert.assertEquals(eventAttrId, eventAttr.getId());

        // make sure only one value in database
        Query q = session.createQuery(
            "SELECT eattr FROM " + CollectionEvent.class.getName() + " ce "
                + "JOIN ce.eventAttrs eattr "
                + "JOIN eattr.studyEventAttr seattr "
                + "JOIN seattr.globalEventAttr geattr "
                + "WHERE ce.id = ? AND geattr.label= ?");
        q.setParameter(0, cevent.getId());
        q.setParameter(1, "Phlebotomist");
        @SuppressWarnings("unchecked")
        List<EventAttr> results = q.list();
        Assert.assertEquals(1, results.size());
    }

    /*
     * add Event Attr to study
     */
    @SuppressWarnings("nls")
    private void setEventAttrs(Integer studyId)
        throws Exception {

        Map<Integer, GlobalEventAttrInfo> globalEattrs =
            exec(new GlobalEventAttrInfoGetAction()).getMap();
        Assert.assertFalse("EventAttrTypes not initialized",
            globalEattrs.isEmpty());

        HashMap<String, GlobalEventAttrInfo> globalEattrsByLabel =
            new HashMap<String, GlobalEventAttrInfo>();
        for (GlobalEventAttrInfo gEattr : globalEattrs.values()) {
            globalEattrsByLabel.put(gEattr.attr.getLabel(), gEattr);
        }

        // make sure the global event attributes are present
        Assert.assertTrue(
            "Missing global event attribute",
            globalEattrsByLabel.keySet().containsAll(
                Arrays.asList("PBMC Count (x10^6)", "Consent", "Patient Type",
                    "Visit Type")));

        StudyEventAttrSaveAction seAttrSave = new StudyEventAttrSaveAction();
        seAttrSave.setGlobalEventAttrId(globalEattrsByLabel
            .get("PBMC Count (x10^6)").attr
            .getId());
        seAttrSave.setRequired(true);
        seAttrSave.setActivityStatus(ActivityStatus.ACTIVE);
        seAttrSave.setStudyId(studyId);
        exec(seAttrSave);

        seAttrSave = new StudyEventAttrSaveAction();
        seAttrSave.setGlobalEventAttrId(globalEattrsByLabel
            .get("Consent").attr.getId());
        seAttrSave.setRequired(false);
        seAttrSave.setPermissible("c1;c2;c3");
        seAttrSave.setActivityStatus(ActivityStatus.ACTIVE);
        seAttrSave.setStudyId(studyId);
        exec(seAttrSave);

        seAttrSave = new StudyEventAttrSaveAction();
        seAttrSave.setGlobalEventAttrId(globalEattrsByLabel
            .get("Patient Type").attr.getId());
        seAttrSave.required = true;
        seAttrSave.setActivityStatus(ActivityStatus.ACTIVE);
        seAttrSave.setStudyId(studyId);
        exec(seAttrSave);

        seAttrSave = new StudyEventAttrSaveAction();
        seAttrSave.setGlobalEventAttrId(globalEattrsByLabel
            .get("Visit Type").attr.getId());
        seAttrSave.required = false;
        seAttrSave.setPermissible("v1;v2;v3;v4");
        seAttrSave.setActivityStatus(ActivityStatus.ACTIVE);
        seAttrSave.setStudyId(studyId);
        exec(seAttrSave);

        seAttrSave = new StudyEventAttrSaveAction();
        seAttrSave.setGlobalEventAttrId(globalEattrsByLabel
            .get("Phlebotomist").attr.getId());
        seAttrSave.required = false;
        seAttrSave.setActivityStatus(ActivityStatus.ACTIVE);
        seAttrSave.setStudyId(studyId);
        exec(seAttrSave);
    }

    @Test
    public void deleteWithoutSpecimens() throws Exception {
        final Integer ceventId =
            exec(
                new CollectionEventSaveAction(null, provisioning.patientIds
                    .get(0), getR().nextInt(20) + 1, ActivityStatus.ACTIVE,
                    Utils.getRandomString(20, 30), null, null, provisioning.getClinic())).getId();

        // test delete
        CEventInfo info =
            exec(new CollectionEventGetInfoAction(ceventId));
        exec(new CollectionEventDeleteAction(info.cevent));
        CollectionEvent cevent =
            (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        Assert.assertNull(cevent);
    }

    @SuppressWarnings("nls")
    @Test
    public void gsetEventAttrInfos() throws Exception {
        // add specimen type
        setEventAttrs(provisioning.studyId);
        StudyInfo studyInfo =
            exec(new StudyGetInfoAction(provisioning.studyId));
        Assert.assertEquals(5, studyInfo.getStudyEventAttrs().size());

        StudyEventAttr phlebotomistStudyAttr = null;
        for (StudyEventAttr attr : studyInfo.getStudyEventAttrs()) {
            if ("Phlebotomist".equals(attr.getGlobalEventAttr().getLabel())) {
                phlebotomistStudyAttr = attr;
            }
        }
        Assert.assertNotNull(phlebotomistStudyAttr);

        EventAttrTypeEnum eventAttrType =
            EventAttrTypeEnum.getEventAttrType(phlebotomistStudyAttr
                .getGlobalEventAttr().getEventAttrType().getName());
        List<CEventAttrSaveInfo> attrs =
            new ArrayList<CollectionEventSaveAction.CEventAttrSaveInfo>();
        String value = "abcdefghi";
        attrs.add(new CEventAttrSaveInfo(phlebotomistStudyAttr.getId(), eventAttrType, value));

        Integer visitNber = getR().nextInt(20) + 1;
        // Save a new cevent
        final Integer ceventId = exec(new CollectionEventSaveAction(
            null, provisioning.patientIds.get(0), visitNber, ActivityStatus.ACTIVE,
            null, null, attrs, provisioning.getClinic())).getId();

        // Call get eventAttr infos action
        Map<Integer, EventAttrInfo> infos =
            exec(new CollectionEventGetEventAttrInfoAction(
                ceventId)).getMap();
        Assert.assertEquals(1, infos.size());
        EventAttrInfo info = infos.values().iterator().next();
        Assert.assertNotNull(info.attr);
        Assert.assertEquals(eventAttrType, info.type);
        Assert.assertEquals(ceventId, info.attr.getCollectionEvent().getId());
        Assert.assertEquals(value, info.attr.getValue());
        Assert.assertEquals(phlebotomistStudyAttr.getId(), info.attr
            .getStudyEventAttr().getId());

    }
}
