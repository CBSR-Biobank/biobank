package edu.ualberta.med.biobank.test.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.hibernate.Query;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

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
import edu.ualberta.med.biobank.common.action.info.StudyInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudyEventAttrSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.CollectionNotEmptyException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;

public class TestCollectionEvent extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Provisioning provisioning;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();
        provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);
    }

    @Test
    public void saveNoSpecsNoAttrs() throws Exception {
        final Integer visitNumber = R.nextInt(20) + 1;
        final String commentText = Utils.getRandomString(20, 30);

        // test add
        final Integer ceventId =
            EXECUTOR
                .exec(
                    new CollectionEventSaveAction(null, provisioning.patientIds
                        .get(0),
                        visitNumber, ActivityStatus.ACTIVE, commentText, null,
                        null)).getId();

        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent =
            (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(ActivityStatus.ACTIVE, cevent.getActivityStatus());
        Assert.assertEquals(1, cevent.getCommentCollection().size());
    }

    @Test
    public void saveWithSpecs() throws Exception {
        final Integer visitNumber = R.nextInt(20) + 1;
        final String commentText = Utils.getRandomString(20, 30);

        final Integer typeId = getSpecimenTypes().get(0).getId();

        final Map<String, SaveCEventSpecimenInfo> specs =
            CollectionEventHelper.createSaveCEventSpecimenInfoRandomList(5,
                typeId, EXECUTOR.getUserId(), provisioning.siteId);

        // Save a new cevent
        final Integer ceventId = EXECUTOR.exec(
            new CollectionEventSaveAction(null, provisioning.patientIds
                .get(0),
                visitNumber, ActivityStatus.ACTIVE, commentText,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()),
                null))
            .getId();

        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent =
            (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(ActivityStatus.ACTIVE, cevent.getActivityStatus());
        Assert.assertNotNull(cevent.getCommentCollection());
        Assert.assertEquals(1, cevent.getCommentCollection().size());
        Assert.assertEquals(specs.size(), cevent.getAllSpecimenCollection()
            .size());
        Assert.assertEquals(specs.size(), cevent
            .getOriginalSpecimenCollection().size());
        for (Iterator<Specimen> iter =
            cevent.getOriginalSpecimenCollection().iterator(); iter.hasNext();) {
            Specimen sp = iter.next();
            Assert.assertEquals(typeId, sp.getSpecimenType().getId());
            SaveCEventSpecimenInfo info = specs.get(sp.getInventoryId());
            Assert.assertNotNull(info);
            if (info != null) {
                Assert.assertEquals(1, sp.getCommentCollection().size());
                Assert.assertEquals(info.inventoryId, sp.getInventoryId());
                Assert
                    .assertTrue(info.quantity.equals(sp.getQuantity()));
                Assert.assertEquals(info.specimenTypeId, sp.getSpecimenType()
                    .getId());
                Assert
                    .assertEquals(info.activityStatus, sp.getActivityStatus());
                Assert.assertEquals(info.createdAt, sp.getCreatedAt());
                // set the id to make some modification tests after that.
                info.id = sp.getId();
            }
        }

        // Save the same cevent with only one kept from previous list (and
        // modified) and with a new one
        List<SaveCEventSpecimenInfo> newSpecList =
            new ArrayList<SaveCEventSpecimenInfo>();
        SaveCEventSpecimenInfo modifiedSpec = specs.values().iterator().next();
        modifiedSpec.inventoryId += "Modified";
        modifiedSpec.quantity = modifiedSpec.quantity.add(BigDecimal.ONE);
        newSpecList.add(modifiedSpec);
        SaveCEventSpecimenInfo newSpec =
            CollectionEventHelper.createSaveCEventSpecimenInfoRandom(typeId,
                EXECUTOR.getUserId(), provisioning.siteId);
        newSpecList.add(newSpec);
        // modify cevent
        EXECUTOR.exec(new CollectionEventSaveAction(ceventId,
            provisioning.patientIds.get(0), visitNumber + 1,
            ActivityStatus.ACTIVE,
            commentText, newSpecList, null));

        // Check CollectionEvent is modified
        session.clear();
        cevent =
            (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        session.refresh(cevent);
        Assert
            .assertEquals(visitNumber + 1, cevent.getVisitNumber().intValue());
        Assert.assertEquals(2, cevent.getAllSpecimenCollection().size());
        Assert.assertEquals(2, cevent.getOriginalSpecimenCollection().size());
        for (Iterator<Specimen> iter =
            cevent.getOriginalSpecimenCollection().iterator(); iter.hasNext();) {
            Specimen sp = iter.next();
            Assert.assertEquals(typeId, sp.getSpecimenType().getId());
            Assert.assertTrue(sp.getInventoryId().equals(newSpec.inventoryId)
                || sp.getInventoryId().equals(modifiedSpec.inventoryId));
            if (sp.getInventoryId().equals(newSpec.inventoryId)) {
                Assert.assertNotNull(sp.getCommentCollection());
                Assert.assertEquals(1, sp.getCommentCollection().size());
                Assert.assertEquals(newSpec.inventoryId, sp.getInventoryId());
                Assert.assertTrue(newSpec.quantity.equals(sp.getQuantity()));
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
                    .assertTrue(modifiedSpec.quantity.equals(sp.getQuantity()));
            }
        }
    }

    @Test
    public void checkGetAction() throws Exception {
        // add specimen type
        final Integer typeId =
            EXECUTOR.exec(new SpecimenTypeSaveAction(name, name)).getId();

        final Map<String, SaveCEventSpecimenInfo> specs =
            CollectionEventHelper.createSaveCEventSpecimenInfoRandomList(5,
                typeId, EXECUTOR.getUserId(), provisioning.siteId);

        setEventAttrs(provisioning.studyId);
        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(provisioning.studyId));
        Assert.assertEquals(5, studyInfo.studyEventAttrs.size());

        StudyEventAttr phlebotomistStudyAttr = null;
        for (StudyEventAttr attr : studyInfo.studyEventAttrs) {
            if ("Phlebotomist".equals(attr.getGlobalEventAttr().getLabel())) {
                phlebotomistStudyAttr = attr;
            }
        }
        Assert.assertNotNull(phlebotomistStudyAttr);

        List<CEventAttrSaveInfo> attrs =
            new ArrayList<CollectionEventSaveAction.CEventAttrSaveInfo>();
        CEventAttrSaveInfo attrInfo =
            CollectionEventHelper.createSaveCEventAttrInfo(
                phlebotomistStudyAttr.getId(),
                EventAttrTypeEnum.getEventAttrType(phlebotomistStudyAttr
                    .getGlobalEventAttr().getEventAttrType().getName()),
                "abcdefghi");
        attrs.add(attrInfo);

        Integer visitNber = R.nextInt(20) + 1;
        String commentText = Utils.getRandomString(20, 30);
        // Save a new cevent
        final Integer ceventId = EXECUTOR.exec(
            new CollectionEventSaveAction(null, provisioning.patientIds.get(0),
                visitNber, ActivityStatus.ACTIVE, commentText,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()),
                attrs)).getId();

        // Call get infos action
        PatientInfo patientInfo =
            EXECUTOR.exec(new PatientGetInfoAction(provisioning.patientIds
                .get(0)));
        CEventInfo info =
            EXECUTOR.exec(new CollectionEventGetInfoAction(ceventId));
        // no aliquoted specimens added
        Assert.assertEquals(0, info.aliquotedSpecimenInfos.size());
        Assert.assertNotNull(info.cevent);
        Assert.assertEquals(visitNber, info.cevent.getVisitNumber());
        Assert.assertEquals(ActivityStatus.ACTIVE,
            info.cevent.getActivityStatus());

        Assert.assertEquals(patientInfo.patient.getPnumber(), info.cevent
            .getPatient().getPnumber());

        Assert.assertEquals(patientInfo.patient.getStudy().getName(),
            info.cevent
                .getPatient().getStudy().getName());

        // TODO: add test to check if the comments' user is fetched

        Assert.assertNotNull(info.cevent.getCommentCollection());
        // FIXME sometimes size not correct !!??!!
        Assert.assertEquals(1, info.cevent.getCommentCollection()
            .size());
        Assert.assertEquals(attrs.size(), info.eventAttrs.size());
        Assert.assertEquals(specs.size(), info.sourceSpecimenInfos.size());

        // FIXME need to add test with aliquoted specimens
    }

    @Test
    public void saveWithAttrs() throws Exception {
        setEventAttrs(provisioning.studyId);
        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(provisioning.studyId));
        Assert.assertEquals(5, studyInfo.studyEventAttrs.size());

        StudyEventAttr phlebotomistStudyAttr = null;
        for (StudyEventAttr attr : studyInfo.studyEventAttrs) {
            if ("Phlebotomist".equals(attr.getGlobalEventAttr().getLabel())) {
                phlebotomistStudyAttr = attr;
            }
        }
        Assert.assertNotNull(phlebotomistStudyAttr);

        final Integer visitNumber = R.nextInt(20) + 1;
        final String commentText = Utils.getRandomString(20, 30);

        List<CEventAttrSaveInfo> attrs =
            new ArrayList<CollectionEventSaveAction.CEventAttrSaveInfo>();
        String value1 = name + "abcdefghi";
        CEventAttrSaveInfo attrInfo =
            CollectionEventHelper.createSaveCEventAttrInfo(
                phlebotomistStudyAttr
                    .getId(),
                EventAttrTypeEnum.getEventAttrType(phlebotomistStudyAttr
                    .getGlobalEventAttr()
                    .getEventAttrType()
                    .getName()), value1);

        attrs.add(attrInfo);

        // Save a new cevent
        final Integer ceventId =
            EXECUTOR.exec(
                new CollectionEventSaveAction(null, provisioning.patientIds
                    .get(0),
                    visitNumber, ActivityStatus.ACTIVE, commentText, null,
                    attrs)).getId();

        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent =
            (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(ActivityStatus.ACTIVE, cevent.getActivityStatus());
        Assert.assertEquals(1, cevent.getCommentCollection()
            .size());
        Assert.assertEquals(1, cevent.getEventAttrCollection().size());
        EventAttr eventAttr = cevent.getEventAttrCollection().iterator().next();
        Assert.assertEquals(value1, eventAttr.getValue());
        Assert.assertEquals(phlebotomistStudyAttr.getId(), eventAttr
            .getStudyEventAttr()
            .getId());
        Integer eventAttrId = eventAttr.getId();
        String value2 = name + "jklmnopqr";
        attrInfo.value = value2;
        // Save with a different value for attrinfo
        EXECUTOR.exec(new CollectionEventSaveAction(ceventId,
            provisioning.patientIds.get(0), visitNumber, ActivityStatus.ACTIVE,
            commentText,
            null, attrs));

        session.clear();
        cevent = (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        session.refresh(cevent);
        Assert.assertEquals(1, cevent.getEventAttrCollection().size());
        eventAttr = cevent.getEventAttrCollection().iterator().next();
        Assert.assertEquals(value2, eventAttr.getValue());
        Assert.assertEquals(eventAttrId, eventAttr.getId());

        // make sure only one value in database
        Query q = session.createQuery(
            "select eattr from "
                + CollectionEvent.class.getName()
                + " as ce "
                + "join ce.eventAttrCollection as eattr "
                + "join eattr.studyEventAttr as seattr "
                + "join seattr.globalEventAttr as geattr "
                + "where ce.id = ? and geattr.label= ?");
        q.setParameter(0, cevent.getId());
        q.setParameter(1, "Phlebotomist");
        @SuppressWarnings("unchecked")
        List<EventAttr> results = q.list();
        Assert.assertEquals(1, results.size());
    }

    /*
     * add Event Attr to study
     */
    private void setEventAttrs(Integer studyId)
        throws Exception {

        Map<Integer, GlobalEventAttrInfo> globalEattrs =
            EXECUTOR.exec(new GlobalEventAttrInfoGetAction()).getMap();
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
        EXECUTOR.exec(seAttrSave);

        seAttrSave = new StudyEventAttrSaveAction();
        seAttrSave.setGlobalEventAttrId(globalEattrsByLabel
            .get("Consent").attr.getId());
        seAttrSave.setRequired(false);
        seAttrSave.setPermissible("c1;c2;c3");
        seAttrSave.setActivityStatus(ActivityStatus.ACTIVE);
        seAttrSave.setStudyId(studyId);
        EXECUTOR.exec(seAttrSave);

        seAttrSave = new StudyEventAttrSaveAction();
        seAttrSave.setGlobalEventAttrId(globalEattrsByLabel
            .get("Patient Type").attr.getId());
        seAttrSave.required = true;
        seAttrSave.setActivityStatus(ActivityStatus.ACTIVE);
        seAttrSave.setStudyId(studyId);
        EXECUTOR.exec(seAttrSave);

        seAttrSave = new StudyEventAttrSaveAction();
        seAttrSave.setGlobalEventAttrId(globalEattrsByLabel
            .get("Visit Type").attr.getId());
        seAttrSave.required = false;
        seAttrSave.setPermissible("v1;v2;v3;v4");
        seAttrSave.setActivityStatus(ActivityStatus.ACTIVE);
        seAttrSave.setStudyId(studyId);
        EXECUTOR.exec(seAttrSave);

        seAttrSave = new StudyEventAttrSaveAction();
        seAttrSave.setGlobalEventAttrId(globalEattrsByLabel
            .get("Phlebotomist").attr.getId());
        seAttrSave.required = false;
        seAttrSave.setActivityStatus(ActivityStatus.ACTIVE);
        seAttrSave.setStudyId(studyId);
        EXECUTOR.exec(seAttrSave);
    }

    @Test
    public void deleteWithoutSpecimens() throws Exception {
        final Integer ceventId =
            EXECUTOR.exec(
                new CollectionEventSaveAction(null, provisioning.patientIds
                    .get(0), R.nextInt(20) + 1, ActivityStatus.ACTIVE, Utils
                    .getRandomString(20, 30),
                    null, null)).getId();

        // test delete
        EXECUTOR.exec(new CollectionEventDeleteAction(ceventId));
        CollectionEvent cevent =
            (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        Assert.assertNull(cevent);
    }

    @Test
    public void deleteWithSpecimens() throws Exception {
        // add specimen type
        final Integer typeId =
            EXECUTOR.exec(new SpecimenTypeSaveAction(name, name)).getId();

        final Map<String, SaveCEventSpecimenInfo> specs =
            CollectionEventHelper.createSaveCEventSpecimenInfoRandomList(5,
                typeId, EXECUTOR.getUserId(), provisioning.siteId);

        // Save a new cevent
        final Integer ceventId = EXECUTOR.exec(
            new CollectionEventSaveAction(null, provisioning.patientIds
                .get(0), R.nextInt(20) + 1, ActivityStatus.ACTIVE, null,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null))
            .getId();

        // try delete this cevent:
        try {
            EXECUTOR.exec(new CollectionEventDeleteAction(ceventId));
            Assert
                .fail("should throw an exception because specimens are still in the cevent");
        } catch (CollectionNotEmptyException ae) {
            Assert.assertTrue(true);
        }

        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent =
            (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        Assert.assertNotNull(cevent);
    }

    @Test
    public void saveNotUniqueVisitNumber() throws Exception {
        final Integer visitNumber = R.nextInt(20) + 1;
        // add
        EXECUTOR.exec(new CollectionEventSaveAction(null,
            provisioning.patientIds.get(0), visitNumber, ActivityStatus.ACTIVE,
            null, null,
            null));

        // try to add a second collection event with the same visit number
        try {
            EXECUTOR.exec(new CollectionEventSaveAction(null,
                provisioning.patientIds.get(0), visitNumber,
                ActivityStatus.ACTIVE, null,
                null, null));
            Assert
                .fail("should throw an exception because the visit number is already used");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void gsetEventAttrInfos() throws Exception {
        // add specimen type
        setEventAttrs(provisioning.studyId);
        StudyInfo studyInfo =
            EXECUTOR.exec(new StudyGetInfoAction(provisioning.studyId));
        Assert.assertEquals(5, studyInfo.studyEventAttrs.size());

        StudyEventAttr phlebotomistStudyAttr = null;
        for (StudyEventAttr attr : studyInfo.studyEventAttrs) {
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
        CEventAttrSaveInfo attrInfo =
            CollectionEventHelper.createSaveCEventAttrInfo(
                phlebotomistStudyAttr.getId(), eventAttrType, value);
        attrs.add(attrInfo);

        Integer visitNber = R.nextInt(20) + 1;
        // Save a new cevent
        final Integer ceventId =
            EXECUTOR.exec(
                new CollectionEventSaveAction(null, provisioning.patientIds
                    .get(0), visitNber, ActivityStatus.ACTIVE, null, null,
                    attrs)).getId();

        // Call get eventAttr infos action
        Map<Integer, EventAttrInfo> infos =
            EXECUTOR.exec(new CollectionEventGetEventAttrInfoAction(
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
