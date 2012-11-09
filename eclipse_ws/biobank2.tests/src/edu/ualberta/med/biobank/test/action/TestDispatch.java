package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.dispatch.DispatchChangeStateAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchDeleteAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchGetInfoAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.action.info.DispatchReadInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.DispatchHelper;
import edu.ualberta.med.biobank.test.action.helper.ShipmentInfoHelper;

public class TestDispatch extends TestAction {

    private Integer patientId;
    private Center site;
    private Center clinic;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        session.beginTransaction();
        site = factory.createSite();
        clinic = factory.createClinic();
        patientId = factory.createPatient().getId();
        session.getTransaction().commit();
    }

    @Test
    public void saveWithSpecs() throws Exception {

        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(site, clinic,
                DispatchState.CREATION,
                testName + Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(getExecutor(),
                patientId, clinic);
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
        for (DispatchSpecimen spec : info.specimens) {
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
                patientId, clinic);
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
            DispatchHelper.createSaveDispatchInfoRandom(site, clinic,
                DispatchState.CREATION,
                testName + Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(getExecutor(),
                patientId, clinic);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id =
            exec(new DispatchSaveAction(d, specs, shipsave))
                .getId();

        exec(new DispatchChangeStateAction(id,
            DispatchState.IN_TRANSIT, shipsave));
        Assert
            .assertTrue(exec(new DispatchGetInfoAction(id)).dispatch
                .getState()
                .equals(DispatchState.IN_TRANSIT));

        exec(new DispatchChangeStateAction(id,
            DispatchState.LOST, shipsave));
        Assert
            .assertTrue(exec(new DispatchGetInfoAction(id)).dispatch
                .getState()
                .equals(DispatchState.LOST));

        exec(new DispatchChangeStateAction(id,
            DispatchState.CLOSED, shipsave));
        Assert
            .assertTrue(exec(new DispatchGetInfoAction(id)).dispatch
                .getState()
                .equals(DispatchState.CLOSED));

        exec(new DispatchChangeStateAction(id,
            DispatchState.RECEIVED, shipsave));
        Assert
            .assertTrue(exec(new DispatchGetInfoAction(id)).dispatch
                .getState()
                .equals(DispatchState.RECEIVED));

    }

    @Test
    public void testDelete() throws Exception {
        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(site, clinic,
                DispatchState.IN_TRANSIT,
                testName + Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(getExecutor(),
                patientId, clinic);
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
            new DispatchChangeStateAction(id, DispatchState.CREATION, shipsave);
        exec(stateChange);
        exec(new DispatchDeleteAction(info.dispatch));
    }

    @Test
    public void testComment() throws Exception {

        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(site, clinic,
                DispatchState.IN_TRANSIT,
                testName + Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(getExecutor(),
                patientId, clinic);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id =
            exec(new DispatchSaveAction(d, specs, shipsave))
                .getId();
        d.dispatchId = id;

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
