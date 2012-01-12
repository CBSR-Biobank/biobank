package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchChangeStateAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchDeleteAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchGetInfoAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.DispatchReadInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ValueNotSetException;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.DispatchHelper;
import edu.ualberta.med.biobank.test.action.helper.ShipmentInfoHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestDispatch extends TestAction {

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
        name = testname.getMethodName() + r.nextInt();
        studyId =
            StudyHelper
                .createStudy(appService, name, ActivityStatusEnum.ACTIVE);
        siteId =
            SiteHelper.createSite(appService, name + "1", "Edmonton",
                ActivityStatusEnum.ACTIVE, new HashSet<Integer>(studyId));
        centerId =
            SiteHelper.createSite(appService, name + "2", "Calgary",
                ActivityStatusEnum.ACTIVE, new HashSet<Integer>(studyId));
        patientId =
            appService.doAction(new PatientSaveAction(null, studyId, name,
                Utils.getRandomDate())).getId();
    }

    @Test
    public void saveWithSpecs() throws Exception {
        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(appService, siteId,
                centerId, DispatchState.CREATION.getId(),
                Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(appService,
                patientId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(appService);
        Integer id =
            appService.doAction(new DispatchSaveAction(d, specs, shipsave))
                .getId();

        DispatchReadInfo info =
            appService.doAction(new DispatchGetInfoAction(id));

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
        specs = DispatchHelper.createSaveDispatchSpecimenInfoRandom(appService,
            patientId, centerId);
        Iterator<DispatchSpecimenInfo> it = specs.iterator();

        Integer specId = it.next().specimenId;
        it.next().specimenId = specId;

        id =
            appService.doAction(new DispatchSaveAction(d, specs, shipsave))
                .getId();

        // test null

        specId = null;
        it.next().specimenId = specId;

        try {
            id =
                appService.doAction(new DispatchSaveAction(d, specs, shipsave))
                    .getId();
            Assert.fail();
        } catch (ValueNotSetException e) {
        }

        // test empty

        specs = new HashSet<DispatchSpecimenInfo>();
        id =
            appService.doAction(new DispatchSaveAction(d, specs, shipsave))
                .getId();

        info =
            appService.doAction(new DispatchGetInfoAction(id));

        Assert.assertTrue(info.specimens.size() == 0);

    }

    @Test
    public void testStateChange() throws Exception {
        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(appService, siteId,
                centerId, DispatchState.CREATION.getId(),
                Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(appService,
                patientId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(appService);
        Integer id =
            appService.doAction(new DispatchSaveAction(d, specs, shipsave))
                .getId();

        appService.doAction(new DispatchChangeStateAction(id,
            DispatchState.IN_TRANSIT, shipsave));
        Assert
            .assertTrue(appService.doAction(new DispatchGetInfoAction(id)).dispatch.state
                .equals(DispatchState.IN_TRANSIT.getId()));

        appService.doAction(new DispatchChangeStateAction(id,
            DispatchState.LOST, shipsave));
        Assert
            .assertTrue(appService.doAction(new DispatchGetInfoAction(id)).dispatch.state
                .equals(DispatchState.LOST.getId()));

        appService.doAction(new DispatchChangeStateAction(id,
            DispatchState.CLOSED, shipsave));
        Assert
            .assertTrue(appService.doAction(new DispatchGetInfoAction(id)).dispatch.state
                .equals(DispatchState.CLOSED.getId()));

        appService.doAction(new DispatchChangeStateAction(id,
            DispatchState.RECEIVED, shipsave));
        Assert
            .assertTrue(appService.doAction(new DispatchGetInfoAction(id)).dispatch.state
                .equals(DispatchState.RECEIVED.getId()));

    }

    @Test
    public void testDelete() throws Exception {
        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(appService, siteId,
                centerId, DispatchState.IN_TRANSIT.getId(),
                Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(appService,
                patientId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(appService);
        Integer id =
            appService.doAction(new DispatchSaveAction(d, specs, shipsave))
                .getId();

        DispatchReadInfo info =
            appService.doAction(new DispatchGetInfoAction(id));
        DispatchDeleteAction delete = new DispatchDeleteAction(id);
        try {
            appService.doAction(delete);
            Assert.fail();
        } catch (ActionException e) {
        }

        DispatchChangeStateAction stateChange =
            new DispatchChangeStateAction(id, DispatchState.CREATION, shipsave);
        appService.doAction(stateChange);
        appService.doAction(delete);
    }

}
