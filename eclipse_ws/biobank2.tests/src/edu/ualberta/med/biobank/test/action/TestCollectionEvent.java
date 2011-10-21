package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestCollectionEvent extends TestAction {

    private StudyWrapper study;
    private Integer patientId;
    private SiteWrapper site;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String name = "CEventTest" + r.nextInt();
        site = SiteHelper.addSite(name);
        study = StudyHelper.addStudy(name);
        patientId = appService.doAction(new PatientSaveAction(null, study
            .getId(), name, Utils.getRandomDate()));
    }

    @Test
    public void testSaveNewNoSpecsNoAttrs() throws Exception {
        final Integer visitNumber = r.nextInt(20);
        final String comments = Utils.getRandomString(8, 50);
        final Integer statusId = 1;
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId,
                visitNumber, statusId, comments, site.getId(), null, null));

        openHibernateSession();
        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent = (CollectionEvent) session.get(
            CollectionEvent.class, ceventId);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(statusId, cevent.getActivityStatus().getId());
        Assert.assertEquals(comments, cevent.getComment());
        closeHibernateSession();
    }

    @Test
    public void testSaveWithSpecs() throws Exception {
        String s = "testSaveWithSpecs" + r.nextInt();
        final Integer visitNumber = r.nextInt(20);
        final String comments = Utils.getRandomString(8, 50);
        final Integer statusId = 1;

        // add specimen type
        final Integer typeId = edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper
            .addSpecimenType(s).getId();

        final Map<String, SaveCEventSpecimenInfo> specs = CollectionEventHelper
            .createSaveCEventSpecimenInfoRandomList(5, typeId);

        // Save a new cevent
        final Integer ceventId = appService
            .doAction(new CollectionEventSaveAction(null, patientId,
                visitNumber, statusId, comments, site.getId(),
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null));

        openHibernateSession();
        // Check CollectionEvent is in database with correct values
        CollectionEvent cevent = (CollectionEvent) session.get(
            CollectionEvent.class, ceventId);
        Assert.assertEquals(visitNumber, cevent.getVisitNumber());
        Assert.assertEquals(statusId, cevent.getActivityStatus().getId());
        Assert.assertEquals(comments, cevent.getComment());
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
                Assert.assertEquals(info.comment, sp.getComment());
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
            .createSaveCEventSpecimenInfoRandom(typeId);
        newSpecList.add(newSpec);
        // modify cevent
        appService.doAction(new CollectionEventSaveAction(ceventId, patientId,
            visitNumber + 1, statusId, comments, site.getId(), newSpecList,
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
                Assert.assertEquals(newSpec.comment, sp.getComment());
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
        String s = "testSaveWithAttrs" + r.nextInt();
        final Integer visitNumber = r.nextInt(20);
        final String comments = Utils.getRandomString(8, 50);
        final Integer statusId = 1;

        // TODO
        // List<SaveCEventAttrInfo> attrs = new
        // ArrayList<CollectionEventSaveAction.SaveCEventAttrInfo>();
        // SaveCEventAttrInfo attrInfo = CollectionEventHelper
        // .createSaveCEventAttrInfo(null, null, null);
        // attrs.add(attrInfo);
        //
        // // Save a new cevent
        // final Integer ceventId = appService
        // .doAction(new CollectionEventSaveAction(null, patientId,
        // visitNumber, statusId, comments, site.getId(), null, attrs));

    }
}
