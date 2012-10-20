package edu.ualberta.med.biobank.action;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.action.info.ShipmentReadInfo;
import edu.ualberta.med.biobank.action.info.SiteInfo;
import edu.ualberta.med.biobank.action.originInfo.OriginInfoDeleteAction;
import edu.ualberta.med.biobank.action.originInfo.OriginInfoSaveAction;
import edu.ualberta.med.biobank.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.action.shipment.ShipmentGetInfoAction;
import edu.ualberta.med.biobank.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.model.type.ActivityStatus;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.action.helper.IdSetMutator;
import edu.ualberta.med.biobank.action.helper.OriginInfoHelper;
import edu.ualberta.med.biobank.action.helper.ShipmentInfoHelper;
import edu.ualberta.med.biobank.action.helper.SiteHelper;
import edu.ualberta.med.biobank.action.helper.StudyHelper;

public class TestShipment extends ActionTest {

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
        name = testname.getMethodName() + getR().nextInt();
        studyId =
            StudyHelper
                .createStudy(getExecutor(), name, ActivityStatus.ACTIVE);
        siteId =
            SiteHelper.createSite(getExecutor(), name + "1", "Edmonton",
                ActivityStatus.ACTIVE, new HashSet<Integer>(studyId));
        centerId =
            SiteHelper.createSite(getExecutor(), name + "2", "Calgary",
                ActivityStatus.ACTIVE, new HashSet<Integer>(studyId));
        patientId =
            exec(new PatientSaveAction(null, studyId, name,
                Utils.getRandomDate(), null)).getId();
    }

    @Test
    public void saveWithSpecs() throws Exception {
        OriginInfoSaveInfo oisave =
            OriginInfoHelper.createSaveOriginInfoSpecimenInfoRandom(
                getExecutor(),
                patientId, siteId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());

        for (Integer spec : oisave.addedSpecIds) {
            Assert.assertTrue(((Specimen) session.load(Specimen.class, spec))
                .getOriginInfo().getCenter().getId()
                .equals(siteId));
        }

        Integer id =
            exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();

        ShipmentReadInfo info =
            exec(new ShipmentGetInfoAction(id));

        Assert.assertTrue(info.originInfo.getCenter().getId()
            .equals(oisave.centerId));
        Assert.assertTrue(info.originInfo.getReceiverCenter().getId()
            .equals(oisave.siteId));
        for (SpecimenInfo spec : info.specimens) {
            Assert.assertTrue(oisave.addedSpecIds.contains(spec.specimen
                .getId()));
            Assert.assertTrue(spec.specimen.getOriginInfo().getCenter().getId()
                .equals(centerId));
            Assert.assertTrue(spec.specimen.getOriginInfo().getReceiverCenter()
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
            exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();

        info =
            exec(new ShipmentGetInfoAction(id));

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
            exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();
            Assert.fail();
        } catch (ActionException e) {
            // cool
        }

        // Out of Bounds
        try {
            oisave.addedSpecIds = mut.getOutOfBounds();
            exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();
            Assert.fail();
        } catch (ModelNotFoundException e) {
        }
    }

    @Test
    public void testDelete() throws Exception {
        OriginInfoSaveInfo oisave =
            OriginInfoHelper.createSaveOriginInfoSpecimenInfoRandom(
                getExecutor(),
                patientId, siteId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id =
            exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();

        for (Integer spec : oisave.addedSpecIds) {
            Assert.assertTrue(((Specimen) session.get(Specimen.class, spec))
                .getOriginInfo()
                .getCenter().getId()
                .equals(centerId));
            Assert.assertTrue(((Specimen) session.get(Specimen.class, spec))
                .getOriginInfo().getReceiverCenter().getId()
                .equals(siteId));
            Assert.assertTrue(((Specimen) session.get(Specimen.class, spec))
                .getCurrentCenter().getId()
                .equals(siteId));
        }

        ShipmentReadInfo info = exec(new ShipmentGetInfoAction(id));
        SiteInfo siteInfo = exec(new SiteGetInfoAction(siteId));
        OriginInfoDeleteAction action =
            new OriginInfoDeleteAction(info.originInfo, siteInfo.getSite());
        exec(action);

        session.close();
        session = openSession();

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
            OriginInfoHelper.createSaveOriginInfoSpecimenInfoRandom(
                getExecutor(),
                patientId, siteId, centerId);
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
}
