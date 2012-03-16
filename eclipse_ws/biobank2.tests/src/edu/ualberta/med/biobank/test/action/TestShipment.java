package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentReadInfo;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.originInfo.OriginInfoDeleteAction;
import edu.ualberta.med.biobank.common.action.originInfo.OriginInfoSaveAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.action.shipment.ShipmentGetInfoAction;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.IdSetMutator;
import edu.ualberta.med.biobank.test.action.helper.OriginInfoHelper;
import edu.ualberta.med.biobank.test.action.helper.ShipmentInfoHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestShipment extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Integer studyId;
    private Integer patientId;
    private Integer siteId;
    private Integer centerId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + R.nextInt();
        studyId =
            StudyHelper
                .createStudy(EXECUTOR, name, ActivityStatus.ACTIVE);
        siteId =
            SiteHelper.createSite(EXECUTOR, name + "1", "Edmonton",
                ActivityStatus.ACTIVE, new HashSet<Integer>(studyId));
        centerId =
            SiteHelper.createSite(EXECUTOR, name + "2", "Calgary",
                ActivityStatus.ACTIVE, new HashSet<Integer>(studyId));
        patientId =
            EXECUTOR.exec(new PatientSaveAction(null, studyId, name,
                Utils.getRandomDate(), null)).getId();
    }

    @Test
    public void saveWithSpecs() throws Exception {
        OriginInfoSaveInfo oisave =
            OriginInfoHelper.createSaveOriginInfoSpecimenInfoRandom(EXECUTOR,
                patientId, siteId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(EXECUTOR);

        for (Integer spec : oisave.addedSpecIds) {
            Assert.assertTrue(((Specimen) session.load(Specimen.class, spec))
                .getOriginInfo().getCenter().getId()
                .equals(siteId));
        }

        Integer id =
            EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();

        ShipmentReadInfo info =
            EXECUTOR.exec(new ShipmentGetInfoAction(id));

        Assert.assertTrue(info.originInfo.getCenter().getId()
            .equals(oisave.centerId));
        Assert.assertTrue(info.originInfo.getReceiverSite().getId()
            .equals(oisave.siteId));
        for (SpecimenInfo spec : info.specimens) {
            Assert.assertTrue(oisave.addedSpecIds.contains(spec.specimen
                .getId()));
            Assert.assertTrue(spec.specimen.getOriginInfo().getCenter().getId()
                .equals(centerId));
            Assert.assertTrue(spec.specimen.getOriginInfo().getReceiverSite()
                .getId()
                .equals(siteId));
            Assert.assertTrue(spec.specimen.getCurrentCenter().getId()
                .equals(siteId));
        }
        for (SpecimenInfo spec : info.specimens)
            Assert.assertTrue(!oisave.removedSpecIds.contains(spec.specimen
                .getId()));

        oisave.removedSpecIds = oisave.addedSpecIds;
        oisave.addedSpecIds = new HashSet<Integer>();
        id =
            EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();

        info =
            EXECUTOR.exec(new ShipmentGetInfoAction(id));

        Assert.assertTrue(info.specimens.size() == 0);

        IdSetMutator mut = new IdSetMutator();

        // Empty
        oisave.addedSpecIds = mut.getEmpty();
        EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
            .getId();

        // Null
        oisave.addedSpecIds = mut.getNull();
        EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
            .getId();

        // Set of null
        try {
            oisave.addedSpecIds = mut.getSetWithNull();
            EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();
            Assert.fail();
        } catch (ActionException e) {
            // cool
        }

        // Out of Bounds
        try {
            oisave.addedSpecIds = mut.getOutOfBounds();
            EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();
            Assert.fail();
        } catch (ModelNotFoundException e) {
        }
    }

    @Test
    public void testDelete() throws Exception {
        OriginInfoSaveInfo oisave =
            OriginInfoHelper.createSaveOriginInfoSpecimenInfoRandom(EXECUTOR,
                patientId, siteId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(EXECUTOR);
        Integer id =
            EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();

        for (Integer spec : oisave.addedSpecIds) {
            Assert.assertTrue(((Specimen) session.get(Specimen.class, spec))
                .getOriginInfo()
                .getCenter().getId()
                .equals(centerId));
            Assert.assertTrue(((Specimen) session.get(Specimen.class, spec))
                .getOriginInfo().getReceiverSite().getId()
                .equals(siteId));
            Assert.assertTrue(((Specimen) session.get(Specimen.class, spec))
                .getCurrentCenter().getId()
                .equals(siteId));
        }

        ShipmentReadInfo info = EXECUTOR.exec(new ShipmentGetInfoAction(id));
        SiteInfo siteInfo = EXECUTOR.exec(new SiteGetInfoAction(siteId));
        OriginInfoDeleteAction action =
            new OriginInfoDeleteAction(info.originInfo, siteInfo.getSite());
        EXECUTOR.exec(action);

        session.close();
        session = SESSION_PROVIDER.openSession();

        for (Integer spec : oisave.addedSpecIds) {
            Assert.assertTrue(((Specimen) session.get(Specimen.class, spec))
                .getOriginInfo()
                .getCenter().getId()
                .equals(siteId));
            Assert.assertTrue(((Specimen) session.get(Specimen.class, spec))
                .getCurrentCenter().getId()
                .equals(siteId));
        }

    }

    @Test
    public void testComment() throws Exception {
        OriginInfoSaveInfo oisave =
            OriginInfoHelper.createSaveOriginInfoSpecimenInfoRandom(EXECUTOR,
                patientId, siteId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(EXECUTOR);
        Integer id =
            EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();

        oisave.oiId = id;

        ShipmentReadInfo info =
            EXECUTOR.exec(new ShipmentGetInfoAction(id));

        Assert.assertEquals(1, info.originInfo.getComments().size());
        EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
            .getId();
        info =
            EXECUTOR.exec(new ShipmentGetInfoAction(id));
        Assert.assertEquals(2, info.originInfo.getComments().size());
        EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
            .getId();
        info =
            EXECUTOR.exec(new ShipmentGetInfoAction(id));
        Assert.assertEquals(3, info.originInfo.getComments().size());
    }
}
