package edu.ualberta.med.biobank.test.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventDeleteAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetEventAttrInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.CEventAttrSaveInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.EventAttrInfo;
import edu.ualberta.med.biobank.common.action.eventattr.EventAttrTypeEnum;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.util.MathUtil;
import edu.ualberta.med.biobank.test.Factory;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;

public class TestCollectionEvent extends TestAction {

    private static Logger log = LoggerFactory.getLogger(TestCollectionEvent.class);

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

        // Save the same cevent with only one kept from previous list (and modified) and with a new
        // one
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
     * Creates a colleciton event with parent specimens and child specimens, then re-saves the
     * collection event with the action and then verifies that the current centre on the specimens
     * has not changed.
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
        session.beginTransaction();
        CollectionEvent cevent = factory.createCollectionEvent();
        cevent.getComments().add(factory.createComment());
        factory.createParentSpecimen();
        factory.createChildSpecimen();
        factory.createCeventEventAttr();
        session.getTransaction().commit();

        // Call get infos action
        CEventInfo info = exec(new CollectionEventGetInfoAction(cevent.getId()));

        // no aliquoted specimens added
        Assert.assertNotNull(info.cevent);
        Assert.assertEquals(cevent.getVisitNumber(), info.cevent.getVisitNumber());
        Assert.assertEquals(ActivityStatus.ACTIVE, info.cevent.getActivityStatus());

        Assert.assertEquals(cevent.getPatient().getPnumber(), info.cevent.getPatient().getPnumber());

        Assert.assertEquals(cevent.getPatient().getStudy().getName(),
            info.cevent.getPatient().getStudy().getName());

        Assert.assertNotNull(info.cevent.getComments());
        // FIXME sometimes size not correct !!??!!
        Assert.assertEquals(1, info.cevent.getComments().size());
        Assert.assertEquals(1, info.eventAttrs.size());
        Assert.assertEquals(1, info.sourceSpecimenInfos.size());
        Assert.assertEquals(1, info.aliquotedSpecimenInfos.size());
    }

    @Test
    public void getEventAttrInfos() throws Exception {
        session.beginTransaction();
        CollectionEvent cevent = factory.createCollectionEvent();
        factory.setDefaultEventAttrTypeEnum(EventAttrTypeEnum.SELECT_SINGLE);
        EventAttr eventAttr = factory.createCeventEventAttr();
        session.getTransaction().commit();

        // Call get eventAttr infos action
        Map<Integer, EventAttrInfo> infos = exec(
            new CollectionEventGetEventAttrInfoAction(cevent.getId())).getMap();
        Assert.assertEquals(1, infos.size());
        EventAttrInfo info = infos.values().iterator().next();
        Assert.assertNotNull(info.attr);
        Assert.assertEquals(factory.getDefaultEventAttrTypeEnum(), info.type);
        Assert.assertEquals(cevent.getId(), info.attr.getCollectionEvent().getId());
        Assert.assertEquals(eventAttr.getValue(), info.attr.getValue());
        Assert.assertEquals(eventAttr.getStudyEventAttr().getId(),
            info.attr.getStudyEventAttr().getId());

    }

    public void saveWithEventAttr(EventAttrTypeEnum eventAttrTypeEnum) throws Exception {
        session.beginTransaction();
        factory.setDefaultEventAttrTypeEnum(eventAttrTypeEnum);
        StudyEventAttr studyEventAttr = factory.createStudyEventAttr();
        Patient patient = factory.createPatient();
        session.getTransaction().commit();

        List<CEventAttrSaveInfo> attrs = new ArrayList<CEventAttrSaveInfo>();
        String value = getMethodNameR();

        switch (eventAttrTypeEnum) {
        case SELECT_SINGLE:
            value = Factory.STUDY_EVENT_ATTR_SELECT_PERMISSIBLE.split(";")[0];
            break;
        case SELECT_MULTIPLE:
            String[] options = Factory.STUDY_EVENT_ATTR_SELECT_PERMISSIBLE.split(";");
            value = options[1] + ";" + options[2];
            break;
        case NUMBER:
            value = "12.34";
            break;
        case DATE_TIME:
            value = "2000-01-01 12:00";
            break;
        case TEXT:
            value = getMethodNameR();
            break;
        default:
            throw new IllegalStateException("invalid event attribute type: " + eventAttrTypeEnum);
        }

        CEventAttrSaveInfo attrInfo = new CEventAttrSaveInfo(studyEventAttr.getId(),
            factory.getDefaultEventAttrTypeEnum(), value);
        attrs.add(attrInfo);

        // Save a new cevent
        Integer visitNumber = 1;
        final Integer ceventId = exec(new CollectionEventSaveAction(
            null, patient.getId(), visitNumber, ActivityStatus.ACTIVE, getMethodNameR(), null,
            attrs, factory.getDefaultCenter())).getId();

        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent = (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(ActivityStatus.ACTIVE, cevent.getActivityStatus());
        Assert.assertEquals(1, cevent.getComments().size());
        Assert.assertEquals(1, cevent.getEventAttrs().size());
        EventAttr eventAttr = cevent.getEventAttrs().iterator().next();
        Assert.assertEquals(value, eventAttr.getValue());
        Assert.assertEquals(studyEventAttr.getId(), eventAttr.getStudyEventAttr().getId());

        attrs.remove(attrInfo);
        Integer eventAttrId = eventAttr.getId();

        switch (eventAttrTypeEnum) {
        case SELECT_SINGLE:
            value = Factory.STUDY_EVENT_ATTR_SELECT_PERMISSIBLE.split(";")[1];
            break;
        case SELECT_MULTIPLE:
            value = Factory.STUDY_EVENT_ATTR_SELECT_PERMISSIBLE.split(";")[0] + ";"
                + Factory.STUDY_EVENT_ATTR_SELECT_PERMISSIBLE.split(";")[2];
            break;
        case NUMBER:
            value = "56.78";
            break;
        case DATE_TIME:
            value = "2010-12-31 23:59";
            break;
        case TEXT:
            value = getMethodNameR();
            break;
        default:
            throw new IllegalStateException("invalid event attribute type: " + eventAttrTypeEnum);
        }

        attrInfo = new CEventAttrSaveInfo(attrInfo.studyEventAttrId, attrInfo.type, value);
        attrs.add(attrInfo);

        // Save with a different value for attrinfo
        exec(new CollectionEventSaveAction(ceventId, patient.getId(), visitNumber,
            ActivityStatus.ACTIVE, null, null, attrs, factory.getDefaultCenter()));

        session.clear();
        cevent = (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        session.refresh(cevent);
        Assert.assertEquals(1, cevent.getEventAttrs().size());
        eventAttr = cevent.getEventAttrs().iterator().next();
        Assert.assertEquals(value, eventAttr.getValue());
        Assert.assertEquals(eventAttrId, eventAttr.getId());

        // make sure only one value is present in database
        @SuppressWarnings("unchecked")
        List<EventAttr> results = session.createCriteria(EventAttr.class, "ea")
            .createAlias("ea.studyEventAttr", "seattr")
            .createAlias("seattr.globalEventAttr", "geattr")
            .add(Restrictions.eq("ea.collectionEvent.id", ceventId))
            .add(Restrictions.eq("geattr.label", studyEventAttr.getGlobalEventAttr().getLabel()))
            .list();

        Assert.assertEquals(1, results.size());
    }

    @Test
    public void saveWithEventAttr() throws Exception {
        saveWithEventAttr(EventAttrTypeEnum.TEXT);
        saveWithEventAttr(EventAttrTypeEnum.SELECT_SINGLE);
        saveWithEventAttr(EventAttrTypeEnum.SELECT_MULTIPLE);
        saveWithEventAttr(EventAttrTypeEnum.NUMBER);

        // no global event attribute types of type DATE_TIME defined yet
        // badEventAttrValue(EventAttrTypeEnum.DATE_TIME);
    }

    /*
     * EventAttrTypeEnum.TEXT does not have invalid input, therefore not tested.
     */
    public void badEventAttrValue(EventAttrTypeEnum eventAttrTypeEnum) throws Exception {
        session.beginTransaction();
        factory.setDefaultEventAttrTypeEnum(eventAttrTypeEnum);
        StudyEventAttr studyEventAttr = factory.createStudyEventAttr();
        Patient patient = factory.createPatient();
        session.getTransaction().commit();

        List<CEventAttrSaveInfo> attrs = new ArrayList<CEventAttrSaveInfo>();
        String value = null;

        switch (eventAttrTypeEnum) {
        case SELECT_MULTIPLE:
            value = getMethodNameR() + ";" + getMethodNameR();
            break;
        case SELECT_SINGLE:
        case NUMBER:
        case DATE_TIME:
            value = getMethodNameR();
            break;
        default:
            throw new IllegalStateException("invalid event attribute type: " + eventAttrTypeEnum);
        }

        CEventAttrSaveInfo attrInfo = new CEventAttrSaveInfo(studyEventAttr.getId(),
            factory.getDefaultEventAttrTypeEnum(), value);
        attrs.add(attrInfo);

        exec(new CollectionEventSaveAction(
            null, patient.getId(), 1, ActivityStatus.ACTIVE, getMethodNameR(), null,
            attrs, factory.getDefaultCenter())).getId();
    }

    /*
     * EventAttrTypeEnum.TEXT does not have invalid input, therefore not tested.
     */
    @Test
    public void badEventAttrValue() throws Exception {
        try {
            badEventAttrValue(EventAttrTypeEnum.SELECT_SINGLE);
            Assert.fail("should not be allowed to save event attributes with invalid values");
        } catch (LocalizedException e) {
            log.debug(e.getLocalizedMessage());
        }

        try {
            badEventAttrValue(EventAttrTypeEnum.SELECT_MULTIPLE);
            Assert.fail("should not be allowed to save event attributes with invalid values");
        } catch (LocalizedException e) {
            log.debug(e.getLocalizedMessage());
        }

        try {
            badEventAttrValue(EventAttrTypeEnum.NUMBER);
            Assert.fail("should not be allowed to save event attributes with invalid values");
        } catch (NumberFormatException e) {
            log.debug(e.getMessage());
        }

        // no global event attribute types of type DATE_TIME defined yet
        // badEventAttrValue(EventAttrTypeEnum.DATE_TIME);
    }

    @Test
    public void noStudyEventAttr() throws Exception {
        session.beginTransaction();
        Patient patient = factory.createPatient();
        StudyEventAttr studyEventAttr = factory.createStudyEventAttr();
        session.getTransaction().commit();

        // assign a bad study event attribute ID
        String value = getMethodNameR();
        List<CEventAttrSaveInfo> attrs = new ArrayList<CEventAttrSaveInfo>();
        CEventAttrSaveInfo attrInfo = new CEventAttrSaveInfo(studyEventAttr.getId() + 1,
            factory.getDefaultEventAttrTypeEnum(), value);
        attrs.add(attrInfo);

        try {
            exec(new CollectionEventSaveAction(
                null, patient.getId(), 1, ActivityStatus.ACTIVE, getMethodNameR(), null,
                attrs, factory.getDefaultCenter())).getId();
            Assert.fail("should not be allowed to save with an invalid study event attribute");
        } catch (LocalizedException e) {
            log.debug(e.getLocalizedMessage());
        }
    }

    @Test
    public void lockedStudyEventAttr() throws Exception {
        session.beginTransaction();
        Patient patient = factory.createPatient();
        StudyEventAttr studyEventAttr = factory.createStudyEventAttr();
        studyEventAttr.setActivityStatus(ActivityStatus.CLOSED);
        session.getTransaction().commit();

        String value = getMethodNameR();
        List<CEventAttrSaveInfo> attrs = new ArrayList<CEventAttrSaveInfo>();
        CEventAttrSaveInfo attrInfo = new CEventAttrSaveInfo(studyEventAttr.getId(),
            factory.getDefaultEventAttrTypeEnum(), value);
        attrs.add(attrInfo);

        try {
            exec(new CollectionEventSaveAction(
                null, patient.getId(), 1, ActivityStatus.ACTIVE, getMethodNameR(), null,
                attrs, factory.getDefaultCenter())).getId();
            Assert.fail("should not be allowed to save with an locked study event attribute");
        } catch (LocalizedException e) {
            log.debug(e.getLocalizedMessage());
        }
    }

    @Test
    public void multipleOnSelectSingle() throws Exception {
        session.beginTransaction();
        Patient patient = factory.createPatient();
        factory.setDefaultEventAttrTypeEnum(EventAttrTypeEnum.SELECT_SINGLE);
        StudyEventAttr studyEventAttr = factory.createStudyEventAttr();
        session.getTransaction().commit();

        String[] options = Factory.STUDY_EVENT_ATTR_SELECT_PERMISSIBLE.split(";");
        String value = options[1] + ";" + options[2];
        List<CEventAttrSaveInfo> attrs = new ArrayList<CEventAttrSaveInfo>();
        CEventAttrSaveInfo attrInfo = new CEventAttrSaveInfo(studyEventAttr.getId(),
            factory.getDefaultEventAttrTypeEnum(), value);
        attrs.add(attrInfo);

        try {
            exec(new CollectionEventSaveAction(
                null, patient.getId(), 1, ActivityStatus.ACTIVE, getMethodNameR(), null,
                attrs, factory.getDefaultCenter())).getId();
            Assert.fail("should not be allowed to select multiple values");
        } catch (LocalizedException e) {
            log.debug(e.getLocalizedMessage());
        }
    }

    @Test
    public void deleteWithoutSpecimens() throws Exception {
        final Integer ceventId = exec(
            new CollectionEventSaveAction(null, provisioning.patientIds.get(0),
                getR().nextInt(20) + 1, ActivityStatus.ACTIVE,
                Utils.getRandomString(20, 30), null, null, provisioning.getClinic())).getId();

        // test delete
        CEventInfo info = exec(new CollectionEventGetInfoAction(ceventId));
        exec(new CollectionEventDeleteAction(info.cevent));
        CollectionEvent cevent = (CollectionEvent) session.get(CollectionEvent.class, ceventId);
        Assert.assertNull(cevent);
    }
}
