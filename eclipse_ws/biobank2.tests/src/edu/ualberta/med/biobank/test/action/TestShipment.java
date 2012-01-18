package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentReadInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.action.shipment.OriginInfoSaveAction;
import edu.ualberta.med.biobank.common.action.shipment.ShipmentDeleteAction;
import edu.ualberta.med.biobank.common.action.shipment.ShipmentGetInfoAction;
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
                .createStudy(EXECUTOR, name, ActivityStatusEnum.ACTIVE);
        siteId =
            SiteHelper.createSite(EXECUTOR, name + "1", "Edmonton",
                ActivityStatusEnum.ACTIVE, new HashSet<Integer>(studyId));
        centerId =
            SiteHelper.createSite(EXECUTOR, name + "2", "Calgary",
                ActivityStatusEnum.ACTIVE, new HashSet<Integer>(studyId));
        patientId =
            EXECUTOR.exec(new PatientSaveAction(null, studyId, name,
                Utils.getRandomDate())).getId();
    }

    @Test
    public void saveWithSpecs() throws Exception {
        OriginInfoSaveInfo oisave =
            OriginInfoHelper.createSaveOriginInfoSpecimenInfoRandom(EXECUTOR,
                patientId, siteId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(EXECUTOR);
        Integer id =
            EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
                .getId();

        ShipmentReadInfo info =
            EXECUTOR.exec(new ShipmentGetInfoAction(id));

        Assert.assertTrue(info.oi.getCenter().getId().equals(oisave.centerId));
        Assert.assertTrue(info.oi.getReceiverSite().getId()
            .equals(oisave.siteId));
        for (Specimen spec : info.specimens)
            Assert.assertTrue(oisave.addedSpecIds.contains(spec.getId()));
        for (Specimen spec : info.specimens)
            Assert.assertTrue(!oisave.removedSpecIds.contains(spec.getId()));

        oisave.removedSpecIds = oisave.addedSpecIds;
        oisave.addedSpecIds = null;
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
        oisave.addedSpecIds = mut.getSetWithNull();
        EXECUTOR.exec(new OriginInfoSaveAction(oisave, shipsave))
            .getId();

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

        ShipmentDeleteAction action = new ShipmentDeleteAction(id);
        EXECUTOR.exec(action);
    }
}
