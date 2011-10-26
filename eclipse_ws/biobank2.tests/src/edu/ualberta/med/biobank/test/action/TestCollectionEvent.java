package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.hibernate.Query;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.CommentInfo;
import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventDeleteAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetEventAttrInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventAttrInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.EventAttrInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.EventAttrTypeWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.CollectionNotEmptyException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestCollectionEvent extends TestAction {

    private StudyWrapper study;
    private Integer patientId;
    private Integer siteId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String name = "CEventTest" + r.nextInt();
        // FIXME should not use wrappers for set up
        study = StudyHelper.addStudy(name);
        patientId = appService.doAction(new PatientSaveAction(null, study
            .getId(), name, Utils.getRandomDate()));

        siteId = SiteHelper.createSite(appService, name, "Edmonton",
            ActivityStatusEnum.ACTIVE,
            new HashSet<Integer>(study.getId()));
    }

    @Test
    public void testSaveNoSpecsNoAttrs() throws Exception {
        final Integer visitNumber = r.nextInt(20);
        final List<CommentInfo> comments = Utils
            .getRandomCommentInfos(currentUser.getId());
        final Integer statusId = 1;
        // test add
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId,
                visitNumber, statusId, comments, siteId, null, null));

        openHibernateSession();
        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent = (CollectionEvent) session.get(
            CollectionEvent.class, ceventId);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(statusId, cevent.getActivityStatus().getId());
        Assert.assertEquals(comments.size(), cevent.getCommentCollection()
            .size());
        closeHibernateSession();
    }

    @Test
    public void testSaveWithSpecs() throws Exception {
        String s = "testSaveWithSpecs" + r.nextInt();
        final Integer visitNumber = r.nextInt(20);
        final List<CommentInfo> comments = Utils
            .getRandomCommentInfos(currentUser.getId());
        final Integer statusId = 1;

        // add specimen type
        final Integer typeId = edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper
            .addSpecimenType(s).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId,
                currentUser.getId());

        // Save a new cevent
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId,
                visitNumber, statusId, comments, siteId,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));

        openHibernateSession();
        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent = (CollectionEvent) session.get(
            CollectionEvent.class, ceventId);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(statusId, cevent.getActivityStatus().getId());
        Assert.assertNotNull(cevent.getCommentCollection());
        Assert.assertEquals(comments.size(), cevent.getCommentCollection()
            .size());
        Assert.assertEquals(specs.size(), cevent.getAllSpecimenCollection()
            .size());
        Assert.assertEquals(specs.size(), cevent
            .getOriginalSpecimenCollection().size());
        for (Iterator<Specimen> iter = cevent.getOriginalSpecimenCollection()
            .iterator(); iter.hasNext();) {
            Specimen sp = iter.next();
            Assert.assertEquals(typeId, sp.getSpecimenType().getId());
            SaveCEventSpecimenInfo info = specs.get(sp.getInventoryId());
            Assert.assertNotNull(info);
            if (info != null) {
                Assert.assertEquals(info.comments.size(), sp
                    .getCommentCollection()
                    .size());
                Assert.assertEquals(info.inventoryId, sp.getInventoryId());
                Assert
                    .assertTrue(compareDouble(info.quantity, sp.getQuantity()));
                Assert.assertEquals(info.specimenTypeId, sp.getSpecimenType()
                    .getId());
                Assert.assertEquals(info.statusId, sp.getActivityStatus()
                    .getId());
                Assert.assertTrue(compareDateInHibernate(info.timeDrawn,
                    sp.getCreatedAt()));
                // set the id to make some modification tests after that.
                info.id = sp.getId();
            }
        }
        closeHibernateSession();

        // Save a same cevent with only one kept from previous list (and
        // modified) and with a new one
        List<SaveCEventSpecimenInfo> newSpecList = new ArrayList<SaveCEventSpecimenInfo>();
        SaveCEventSpecimenInfo modifiedSpec = specs.values().iterator().next();
        modifiedSpec.inventoryId += "Modified";
        modifiedSpec.quantity += 1;
        newSpecList.add(modifiedSpec);
        SaveCEventSpecimenInfo newSpec = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandom(typeId, currentUser.getId());
        newSpecList.add(newSpec);
        // modify cevent
        appService.doAction(new CollectionEventSaveAction(ceventId, patientId,
            visitNumber + 1, statusId, comments, siteId, newSpecList,
            null));

        openHibernateSession();
        // Check CollectionEvent is modified
        cevent = (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        session.refresh(cevent);
        Assert
            .assertEquals(visitNumber + 1, cevent.getVisitNumber().intValue());
        Assert.assertEquals(2, cevent.getAllSpecimenCollection().size());
        Assert.assertEquals(2, cevent.getOriginalSpecimenCollection().size());
        for (Iterator<Specimen> iter = cevent.getOriginalSpecimenCollection()
            .iterator(); iter.hasNext();) {
            Specimen sp = iter.next();
            Assert.assertEquals(typeId, sp.getSpecimenType().getId());
            Assert.assertTrue(sp.getInventoryId().equals(newSpec.inventoryId)
                || sp.getInventoryId().equals(modifiedSpec.inventoryId));
            if (sp.getInventoryId().equals(newSpec.inventoryId)) {
                Assert.assertNotNull(sp.getCommentCollection());
                Assert
                    .assertEquals(newSpec.comments.size(),
                        sp.getCommentCollection().size());
                Assert.assertEquals(newSpec.inventoryId, sp.getInventoryId());
                Assert.assertTrue(compareDouble(newSpec.quantity,
                    sp.getQuantity()));
                Assert.assertEquals(newSpec.specimenTypeId, sp
                    .getSpecimenType().getId());
                Assert.assertEquals(newSpec.statusId, sp.getActivityStatus()
                    .getId());
                Assert.assertTrue(compareDateInHibernate(newSpec.timeDrawn,
                    sp.getCreatedAt()));
            }
            if (sp.getInventoryId().equals(modifiedSpec.inventoryId)) {
                Assert.assertEquals(modifiedSpec.inventoryId,
                    sp.getInventoryId());
                Assert.assertTrue(compareDouble(modifiedSpec.quantity,
                    sp.getQuantity()));
            }
        }
        closeHibernateSession();
    }

    @Test
    public void testSaveWithAttrs() throws Exception {
        // FIXME should not use wrappers for set up
        addEventAttrs(study);
        List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        Assert.assertEquals(5, labels.size());
        StudyEventAttr studyAttr = null;
        for (StudyEventAttr o : study.getWrappedObject()
            .getStudyEventAttrCollection()) {
            if ("Worksheet".equals(o.getLabel()))
                studyAttr = o;
        }
        Assert.assertNotNull(studyAttr);

        final Integer visitNumber = r.nextInt(20);
        final List<CommentInfo> comments = Utils
            .getRandomCommentInfos(currentUser.getId());
        final Integer statusId = 1;

        List<SaveCEventAttrInfo> attrs = new ArrayList<CollectionEventSaveAction.SaveCEventAttrInfo>();
        String value1 = "abcdefghi";
        SaveCEventAttrInfo attrInfo = CollectionEventHelper
            .createSaveCEventAttrInfo(studyAttr.getId(), EventAttrTypeEnum
                .getEventAttrType(studyAttr.getEventAttrType().getName()),
                value1);

        attrs.add(attrInfo);

        // Save a new cevent
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId,
                visitNumber, statusId, comments, siteId, null, attrs));

        openHibernateSession();
        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent = (CollectionEvent) session.get(
            CollectionEvent.class, ceventId);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(statusId, cevent.getActivityStatus().getId());
        Assert.assertEquals(comments.size(), cevent.getCommentCollection()
            .size());
        Assert.assertEquals(1, cevent.getEventAttrCollection().size());
        EventAttr eventAttr = cevent.getEventAttrCollection().iterator().next();
        Assert.assertEquals(value1, eventAttr.getValue());
        Assert.assertEquals(studyAttr.getId(), eventAttr.getStudyEventAttr()
            .getId());
        Integer eventAttrId = eventAttr.getId();
        closeHibernateSession();

        String value2 = "jklmnopqr";
        attrInfo.value = value2;
        // Save with a different value for attrinfo
        appService.doAction(new CollectionEventSaveAction(ceventId, patientId,
            visitNumber, statusId, comments, siteId, null, attrs));

        openHibernateSession();
        cevent = (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        session.refresh(cevent);
        Assert.assertEquals(1, cevent.getEventAttrCollection().size());
        eventAttr = cevent.getEventAttrCollection().iterator().next();
        Assert.assertEquals(value2, eventAttr.getValue());
        Assert.assertEquals(eventAttrId, eventAttr.getId());
        closeHibernateSession();

        openHibernateSession();
        // make sure only one value in database
        Query q = session
            .createQuery("select eattr from "
                + CollectionEvent.class.getName()
                + " as ce "
                + "join ce.eventAttrCollection as eattr "
                + "join eattr.studyEventAttr as seattr where ce.id = ? and seattr.label= ?");
        q.setParameter(0, cevent.getId());
        q.setParameter(1, "Worksheet");
        @SuppressWarnings("unchecked")
        List<EventAttr> results = q.list();
        Assert.assertEquals(1, results.size());
        closeHibernateSession();
    }

    private void addEventAttrs(StudyWrapper study) throws Exception {
        // add Event Attr to study
        Collection<String> types = EventAttrTypeWrapper
            .getAllEventAttrTypesMap(appService).keySet();
        Assert.assertTrue("EventAttrTypes not initialized",
            types.contains("text"));
        study.setStudyEventAttr("PMBC Count", EventAttrTypeEnum.NUMBER);
        study.setStudyEventAttr("Worksheet", EventAttrTypeEnum.TEXT);
        study.setStudyEventAttr("Date", EventAttrTypeEnum.DATE_TIME);
        study.setStudyEventAttr("Consent", EventAttrTypeEnum.SELECT_MULTIPLE,
            new String[] { "c1", "c2", "c3" });
        study.setStudyEventAttr("Visit", EventAttrTypeEnum.SELECT_SINGLE,
            new String[] { "v1", "v2", "v3", "v4" });
        study.persist();
    }

    @Test
    public void testDeleteWithoutSpecimens() throws Exception {
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId, r
                .nextInt(20), 1, Utils.getRandomCommentInfos(currentUser
                .getId()), siteId,
                null, null));

        // test delete
        appService.doAction(new CollectionEventDeleteAction(ceventId));
        openHibernateSession();
        CollectionEvent cevent = (CollectionEvent) session.get(
            CollectionEvent.class, ceventId);
        Assert.assertNull(cevent);
        closeHibernateSession();
    }

    @Test
    public void testDeleteWithSpecimens() throws Exception {
        // add specimen type
        final Integer typeId = edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper
            .addSpecimenType("testSaveWithSpecs" + r.nextInt()).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId,
                currentUser.getId());

        // Save a new cevent
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId, r
                .nextInt(20), 1, null, siteId,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));

        // try delete this cevent:
        try {
            appService.doAction(new CollectionEventDeleteAction(ceventId));
            Assert
                .fail("should throw an exception because specimens are still in the cevent");
        } catch (CollectionNotEmptyException ae) {
            Assert.assertTrue(true);
        }
        openHibernateSession();
        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent = (CollectionEvent) session.get(
            CollectionEvent.class, ceventId);
        Assert.assertNotNull(cevent);
    }

    @Test
    public void testSaveNotUniqueVisitNumber() throws Exception {
        final Integer visitNumber = r.nextInt(20);
        final Integer statusId = 1;
        // add
        appService.doAction(new CollectionEventSaveAction(null, patientId,
            visitNumber, statusId, null, siteId, null, null));

        // try to add a second collection event with the same visit number
        try {
            appService.doAction(new CollectionEventSaveAction(null, patientId,
                visitNumber, statusId, null, siteId, null, null));
            Assert
                .fail("should throw an exception because the visit number is already used");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testGetInfos() throws Exception {
        // add specimen type
        final Integer typeId = edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper
            .addSpecimenType("testGetInfos" + r.nextInt()).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId,
                currentUser.getId());

        addEventAttrs(study);
        List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        Assert.assertEquals(5, labels.size());
        StudyEventAttr studyAttr = null;
        for (StudyEventAttr o : study.getWrappedObject()
            .getStudyEventAttrCollection()) {
            if ("Worksheet".equals(o.getLabel()))
                studyAttr = o;
        }
        Assert.assertNotNull(studyAttr);
        List<SaveCEventAttrInfo> attrs = new ArrayList<CollectionEventSaveAction.SaveCEventAttrInfo>();
        SaveCEventAttrInfo attrInfo = CollectionEventHelper
            .createSaveCEventAttrInfo(studyAttr.getId(), EventAttrTypeEnum
                .getEventAttrType(studyAttr.getEventAttrType().getName()),
                "abcdefghi");
        attrs.add(attrInfo);

        Integer visitNber = r.nextInt(20);
        Integer statusId = 1;
        List<CommentInfo> comments = Utils.getRandomCommentInfos(currentUser
            .getId());
        // Save a new cevent
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId, visitNber,
                statusId, comments, siteId,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()), attrs));

        // Call get infos action
        CEventInfo info = appService.doAction(new CollectionEventGetInfoAction(
            ceventId));
        // no aliquoted specimens added
        Assert.assertEquals(0, info.aliquotedSpecimenInfos.size());
        Assert.assertNotNull(info.cevent);
        Assert.assertEquals(visitNber, info.cevent.getVisitNumber());
        Assert.assertEquals(statusId, info.cevent.getActivityStatus().getId());
        Assert.assertNotNull(info.cevent.getCommentCollection());
        // FIXME sometimes size not correct !!??!!
        Assert.assertEquals(comments.size(), info.cevent.getCommentCollection()
            .size());
        Assert.assertEquals(attrs.size(), info.eventAttrs.size());
        Assert.assertEquals(specs.size(), info.sourceSpecimenInfos.size());

        // FIXME test with aliquoted specimens added
    }

    @Test
    public void testGetEventAttrInfos() throws Exception {
        // add specimen type
        addEventAttrs(study);
        List<String> labels = Arrays.asList(study.getStudyEventAttrLabels());
        Assert.assertEquals(5, labels.size());
        StudyEventAttr studyAttr = null;
        for (StudyEventAttr o : study.getWrappedObject()
            .getStudyEventAttrCollection()) {
            if ("Worksheet".equals(o.getLabel()))
                studyAttr = o;
        }
        Assert.assertNotNull(studyAttr);
        EventAttrTypeEnum eventAttrType = EventAttrTypeEnum
            .getEventAttrType(studyAttr.getEventAttrType().getName());
        List<SaveCEventAttrInfo> attrs = new ArrayList<CollectionEventSaveAction.SaveCEventAttrInfo>();
        String value = "abcdefghi";
        SaveCEventAttrInfo attrInfo = CollectionEventHelper
            .createSaveCEventAttrInfo(studyAttr.getId(), eventAttrType, value);
        attrs.add(attrInfo);

        Integer visitNber = r.nextInt(20);
        Integer statusId = 1;
        // Save a new cevent
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId, visitNber,
                statusId, null, siteId, null, attrs));

        // Call get eventAttr infos action
        HashMap<Integer, EventAttrInfo> infos = appService
            .doAction(new CollectionEventGetEventAttrInfoAction(ceventId));
        Assert.assertEquals(1, infos.size());
        EventAttrInfo info = infos.values().iterator().next();
        Assert.assertNotNull(info.attr);
        Assert.assertEquals(eventAttrType, info.type);
        Assert.assertEquals(ceventId, info.attr.getCollectionEvent().getId());
        Assert.assertEquals(value, info.attr.getValue());
        Assert.assertEquals(studyAttr.getId(), info.attr.getStudyEventAttr()
            .getId());

    }
}
