package edu.ualberta.med.biobank.action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.action.dispatch.DispatchChangeStateAction;
import edu.ualberta.med.biobank.action.dispatch.DispatchDeleteAction;
import edu.ualberta.med.biobank.action.dispatch.DispatchGetInfoAction;
import edu.ualberta.med.biobank.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.action.info.DispatchReadInfo;
import edu.ualberta.med.biobank.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.model.ShipmentSpecimen;
import edu.ualberta.med.biobank.model.type.ActivityStatus;
import edu.ualberta.med.biobank.model.type.ShipmentState;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.action.helper.DispatchHelper;
import edu.ualberta.med.biobank.action.helper.ShipmentInfoHelper;
import edu.ualberta.med.biobank.action.helper.SiteHelper;
import edu.ualberta.med.biobank.action.helper.StudyHelper;

public class TestDispatch extends ActionTest {

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

        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(siteId, centerId,
                ShipmentState.PACKED,
                name + Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(getExecutor(),
                patientId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id =
            exec(new DispatchSaveAction(d, specs, shipsave))
                .getId();

        DispatchReadInfo info =
            exec(new DispatchGetInfoAction(id));

        Assert.assertTrue(info.dispatch.getReceiverCenter().getId()
            .equals(d.receiverId));
        Assert.assertTrue(info.dispatch.getSenderCenter().getId()
            .equals(d.senderId));
        for (ShipmentSpecimen spec : info.specimens) {
            boolean found = false;
            for (DispatchSpecimenInfo spec2 : specs) {
                if (spec2.specimenId.equals(spec.getSpecimen().getId()))
                    found = true;
            }
            Assert.assertTrue(found);
        }

        // test duplicates
        specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(getExecutor(),
                patientId, centerId);
        Iterator<DispatchSpecimenInfo> it = specs.iterator();

        Integer specId = it.next().specimenId;
        it.next().specimenId = specId;

        id =
            exec(new DispatchSaveAction(d, specs, shipsave))
                .getId();

        // test null

        specId = null;
        it.next().specimenId = specId;

        try {
            id =
                exec(new DispatchSaveAction(d, specs, shipsave))
                    .getId();
            Assert.fail("test should fail");
        } catch (ModelNotFoundException e) {
            Assert.assertTrue(true);
        }

        // test empty

        specs = new HashSet<DispatchSpecimenInfo>();
        id =
            exec(new DispatchSaveAction(d, specs, shipsave))
                .getId();

        info =
            exec(new DispatchGetInfoAction(id));

        Assert.assertTrue(info.specimens.size() == 0);

    }

    @Test
    public void testStateChange() throws Exception {
        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(siteId, centerId,
                ShipmentState.PACKED,
                name + Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(getExecutor(),
                patientId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id =
            exec(new DispatchSaveAction(d, specs, shipsave))
                .getId();

        exec(new DispatchChangeStateAction(id,
            ShipmentState.IN_TRANSIT, shipsave));
        Assert
            .assertTrue(exec(new DispatchGetInfoAction(id)).dispatch
                .getState()
                .equals(ShipmentState.IN_TRANSIT));

        exec(new DispatchChangeStateAction(id,
            ShipmentState.LOST, shipsave));
        Assert
            .assertTrue(exec(new DispatchGetInfoAction(id)).dispatch
                .getState()
                .equals(ShipmentState.LOST));

        exec(new DispatchChangeStateAction(id,
            ShipmentState.CLOSED, shipsave));
        Assert
            .assertTrue(exec(new DispatchGetInfoAction(id)).dispatch
                .getState()
                .equals(ShipmentState.CLOSED));

        exec(new DispatchChangeStateAction(id,
            ShipmentState.RECEIVED, shipsave));
        Assert
            .assertTrue(exec(new DispatchGetInfoAction(id)).dispatch
                .getState()
                .equals(ShipmentState.RECEIVED));

    }

    @Test
    public void testDelete() throws Exception {
        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(siteId, centerId,
                ShipmentState.IN_TRANSIT,
                name + Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(getExecutor(),
                patientId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id =
            exec(new DispatchSaveAction(d, specs, shipsave))
                .getId();

        DispatchReadInfo info = exec(new DispatchGetInfoAction(id));
        try {
            exec(new DispatchDeleteAction(info.dispatch));
            Assert.fail();
        } catch (ActionException e) {
            Assert.assertTrue(true);
        }

        DispatchChangeStateAction stateChange =
            new DispatchChangeStateAction(id, ShipmentState.PACKED, shipsave);
        exec(stateChange);
        exec(new DispatchDeleteAction(info.dispatch));
    }

    @Test
    public void testComment() throws Exception {

        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(siteId, centerId,
                ShipmentState.IN_TRANSIT,
                name + Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(getExecutor(),
                patientId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id =
            exec(new DispatchSaveAction(d, specs, shipsave))
                .getId();
        d.id = id;

        DispatchReadInfo info = exec(new DispatchGetInfoAction(id));
        Assert.assertEquals(1, info.dispatch.getComments().size());
        exec(new DispatchSaveAction(d, specs, shipsave))
            .getId();
        info =
            exec(new DispatchGetInfoAction(id));
        Assert.assertEquals(2, info.dispatch.getComments().size());
        exec(new DispatchSaveAction(d, specs, shipsave))
            .getId();
        info =
            exec(new DispatchGetInfoAction(id));
        Assert.assertEquals(3, info.dispatch.getComments().size());
    }
}
