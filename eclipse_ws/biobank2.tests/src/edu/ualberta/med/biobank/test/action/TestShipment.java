package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentReadInfo;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.originInfo.OriginInfoDeleteAction;
import edu.ualberta.med.biobank.common.action.originInfo.OriginInfoSaveAction;
import edu.ualberta.med.biobank.common.action.shipment.ShipmentGetInfoAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.action.helper.IdSetMutator;
import edu.ualberta.med.biobank.test.action.helper.OriginInfoHelper;
import edu.ualberta.med.biobank.test.action.helper.ShipmentInfoHelper;

public class TestShipment extends TestAction {

    private Patient patient;
    private Site site;
    private Center clinic;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        session.beginTransaction();
        site = factory.createSite();
        clinic = factory.createClinic();
        Contact contact = factory.createContact();
        factory.getDefaultStudy().getContacts().add(contact);
        patient = factory.createPatient();
        session.getTransaction().commit();
    }

    @Test
    public void saveWithSpecs() throws Exception {
        OriginInfoSaveInfo oisave =
            OriginInfoHelper.createSaveOriginInfoSpecimenInfoRandom(
                getExecutor(), patient.getId(), site, clinic);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());

        for (Integer specimenId : oisave.addedSpecIds) {
            Specimen spc = (Specimen) session.load(Specimen.class, specimenId);
            Assert.assertEquals(site.getId(), spc.getOriginInfo().getCenter().getId());
        }

        Integer id = exec(new OriginInfoSaveAction(oisave, shipsave)).getId();

        ShipmentReadInfo info =
            exec(new ShipmentGetInfoAction(id));

        Assert.assertEquals(oisave.centerId, info.originInfo.getCenter().getId());
        Assert.assertEquals(oisave.siteId, info.originInfo.getReceiverCenter().getId());
        for (SpecimenInfo spec : info.specimens) {
            Assert.assertTrue(oisave.addedSpecIds.contains(spec.specimen
                .getId()));
            Assert.assertEquals(clinic.getId(), spec.specimen.getOriginInfo().getCenter().getId());
            Assert.assertEquals(site.getId(),
                spec.specimen.getOriginInfo().getReceiverCenter().getId());
            Assert.assertEquals(site.getId(), spec.specimen.getCurrentCenter().getId());
        }
        for (SpecimenInfo spec : info.specimens) {
            Assert.assertTrue(!oisave.removedSpecIds.contains(spec.specimen.getId()));
        }

        oisave.removedSpecIds = oisave.addedSpecIds;
        oisave.addedSpecIds = new HashSet<Integer>();
        id = exec(new OriginInfoSaveAction(oisave, shipsave)).getId();

        info = exec(new ShipmentGetInfoAction(id));

        Assert.assertTrue(info.specimens.size() == 0);

        IdSetMutator mut = new IdSetMutator();

        // Empty
        oisave.addedSpecIds = mut.getEmpty();
        exec(new OriginInfoSaveAction(oisave, shipsave))
            .getId();

        // Null
        oisave.addedSpecIds = mut.getNull();
        exec(new OriginInfoSaveAction(oisave, shipsave))
            .getId();

        // Set of null
        try {
            oisave.addedSpecIds = mut.getSetWithNull();
            exec(new OriginInfoSaveAction(oisave, shipsave)).getId();
            Assert.fail("should throw exception");
        } catch (ActionException e) {
            // intentionally empty
        }

        // Out of Bounds
        try {
            oisave.addedSpecIds = mut.getOutOfBounds();
            exec(new OriginInfoSaveAction(oisave, shipsave)).getId();
            Assert.fail("should throw exception");
        } catch (ModelNotFoundException e) {
            // intentionally empty
        }
    }

    @Test
    public void testDelete() throws Exception {
        OriginInfoSaveInfo oisave =
            OriginInfoHelper.createSaveOriginInfoSpecimenInfoRandom(
                getExecutor(), patient.getId(), site, clinic);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id =
            exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();

        for (Integer specimenId : oisave.addedSpecIds) {
            Specimen spc = (Specimen) session.get(Specimen.class, specimenId);
            Assert.assertEquals(clinic.getId(), spc.getOriginInfo().getCenter().getId());
            Assert.assertEquals(site.getId(), spc.getOriginInfo().getReceiverCenter().getId());
            Assert.assertEquals(site.getId(), spc.getCurrentCenter().getId());
        }

        ShipmentReadInfo info = exec(new ShipmentGetInfoAction(id));
        SiteInfo siteInfo = exec(new SiteGetInfoAction(site));
        OriginInfoDeleteAction action =
            new OriginInfoDeleteAction(info.originInfo, siteInfo.getSite());
        exec(action);

        session.close();
        session = openSession();

        for (Integer specimenId : oisave.addedSpecIds) {
            Specimen spc = (Specimen) session.get(Specimen.class, specimenId);
            Assert.assertEquals(site.getId(), spc.getOriginInfo().getCenter().getId());
            Assert.assertEquals(site.getId(), spc.getCurrentCenter().getId());
        }

    }

    @Test
    public void testComment() throws Exception {
        OriginInfoSaveInfo oisave =
            OriginInfoHelper.createSaveOriginInfoSpecimenInfoRandom(
                getExecutor(), patient.getId(), site, clinic);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id =
            exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();

        oisave.oiId = id;

        ShipmentReadInfo info =
            exec(new ShipmentGetInfoAction(id));

        Assert.assertEquals(1, info.originInfo.getComments().size());
        exec(new OriginInfoSaveAction(oisave, shipsave))
            .getId();
        info =
            exec(new ShipmentGetInfoAction(id));
        Assert.assertEquals(2, info.originInfo.getComments().size());
        exec(new OriginInfoSaveAction(oisave, shipsave))
            .getId();
        info =
            exec(new ShipmentGetInfoAction(id));
        Assert.assertEquals(3, info.originInfo.getComments().size());
    }

    @Test
    public void shipmentsNotAllowed() {

    }
}
