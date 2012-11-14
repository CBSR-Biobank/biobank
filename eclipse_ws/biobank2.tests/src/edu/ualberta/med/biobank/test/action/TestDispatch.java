package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.dispatch.DispatchChangeStateAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchDeleteAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchGetInfoAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.DispatchReadInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.DispatchState;
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
    public void saveWithSpecimens() throws Exception {
        DispatchSaveInfo dispatch = DispatchHelper.createSaveDispatchInfoRandom(site, clinic,
            DispatchState.CREATION, getMethodNameR());
        Set<DispatchSpecimenInfo> dispatchSpecimens = DispatchHelper.createSaveDispatchSpecimenInfoRandom(
            getExecutor(), patientId, clinic);

        Set<Integer> specimenIds = new HashSet<Integer>(dispatchSpecimens.size());
        for (DispatchSpecimenInfo dsInfo : dispatchSpecimens) {
            specimenIds.add(dsInfo.specimenId);
        }

        ShipmentInfoSaveInfo shipmentInfo = ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer dispatchId = exec(new DispatchSaveAction(dispatch, dispatchSpecimens, shipmentInfo)).getId();

        Dispatch result = (Dispatch) session.createCriteria(Dispatch.class)
            .add(Restrictions.eq("id", dispatchId)).uniqueResult();

        Assert.assertEquals(dispatch.receiverId, result.getReceiverCenter().getId());
        Assert.assertEquals(dispatch.senderId, result.getSenderCenter().getId());

        Set<Integer> actionSpecimenIds = new HashSet<Integer>();
        Criteria c = session.createCriteria(DispatchSpecimen.class)
            .add(Restrictions.eq("dispatch.id", dispatchId))
            .setFetchMode("specimen", FetchMode.JOIN);

        @SuppressWarnings("unchecked")
        List<DispatchSpecimen> list = c.list();
        for (DispatchSpecimen dispatchSpecimen : list) {
            Specimen specimen = dispatchSpecimen.getSpecimen();
            actionSpecimenIds.add(specimen.getId());

            // ensure current center on specimens has not changed - it is only
            // updated when the dipatch goes into in_transit state
            Assert.assertEquals(clinic, specimen.getCurrentCenter());
        }

        Assert.assertEquals(specimenIds, actionSpecimenIds);
    }

    @Test
    public void saveWithDuplicateSpecimens() throws Exception {
        Dispatch dispatch = factory.createDispatch(clinic, site);
        DispatchSpecimen [] dispatchSpecimens = new DispatchSpecimen [] {
            factory.createDispatchSpecimen(),
            factory.createDispatchSpecimen()
        };
        Set<DispatchSpecimenInfo> dsInfos = new HashSet<DispatchSpecimenInfo>();
        for (DispatchSpecimen dispatchSpecimen : dispatchSpecimens) {
            dsInfos.add(new DispatchSpecimenInfo(null, dispatchSpecimen.getSpecimen().getId(),
                dispatchSpecimen.getState()));
        }

        // have both dispatch specimens contain the same specimen
        dispatchSpecimens[1].setSpecimen(dispatchSpecimens[0].getSpecimen());

        ShipmentInfo shipInfo = factory.createShipmentInfo();

        ShipmentInfoSaveInfo shipmentInfo = new ShipmentInfoSaveInfo(null,
            shipInfo.getBoxNumber(), shipInfo.getPackedAt(), shipInfo.getReceivedAt(),
            shipInfo.getWaybill(), shipInfo.getShippingMethod().getId());

        try {
            exec(new DispatchSaveAction(new DispatchSaveInfo(null,
                dispatch.getReceiverCenter(), dispatch.getSenderCenter(),
                DispatchState.CREATION, null), dsInfos, shipmentInfo)).getId();
            Assert.fail("should not be allowed to create dispatch");
        } catch (Exception e) {
            // intentionally empty
        }
    }

    @Test
    public void saveWithNoSpecimens() throws Exception {
        Dispatch dispatch = factory.createDispatch(clinic, site);
        Set<DispatchSpecimenInfo> dsInfos = new HashSet<DispatchSpecimenInfo>();
        ShipmentInfo shipInfo = factory.createShipmentInfo();

        ShipmentInfoSaveInfo shipmentInfo = new ShipmentInfoSaveInfo(null,
            shipInfo.getBoxNumber(), shipInfo.getPackedAt(), shipInfo.getReceivedAt(),
            shipInfo.getWaybill(), shipInfo.getShippingMethod().getId());

        try {
            exec(new DispatchSaveAction(new DispatchSaveInfo(null,
                dispatch.getReceiverCenter(), dispatch.getSenderCenter(),
                DispatchState.CREATION, null), dsInfos, shipmentInfo)).getId();
            Assert.fail("should not be allowed to create dispatch");
        } catch (Exception e) {
            // intentionally empty
        }
    }

    @Test
    public void testStateChange() throws Exception {
        DispatchSaveInfo d = DispatchHelper.createSaveDispatchInfoRandom(site, clinic,
            DispatchState.CREATION, getMethodNameR());
        Set<DispatchSpecimenInfo> specs = DispatchHelper.createSaveDispatchSpecimenInfoRandom(
            getExecutor(), patientId, clinic);
        ShipmentInfoSaveInfo shipsave = ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id = exec(new DispatchSaveAction(d, specs, shipsave)).getId();

        exec(new DispatchChangeStateAction(id, DispatchState.IN_TRANSIT, shipsave));
        Assert.assertEquals(DispatchState.IN_TRANSIT,
            exec(new DispatchGetInfoAction(id)).dispatch.getState());

        exec(new DispatchChangeStateAction(id, DispatchState.LOST, shipsave));
        Assert.assertEquals(DispatchState.LOST,
            exec(new DispatchGetInfoAction(id)).dispatch.getState());

        exec(new DispatchChangeStateAction(id, DispatchState.CLOSED, shipsave));
        Assert.assertEquals(DispatchState.CLOSED,
            exec(new DispatchGetInfoAction(id)).dispatch.getState());

        exec(new DispatchChangeStateAction(id, DispatchState.RECEIVED, shipsave));
        Assert.assertEquals(DispatchState.RECEIVED,
            exec(new DispatchGetInfoAction(id)).dispatch.getState());

    }

    @Test
    public void specimenCurrentCenter() throws Exception {
        Dispatch dispatch = factory.createDispatch(clinic, site);
        DispatchSpecimen [] dispatchSpecimens = new DispatchSpecimen [] {
            factory.createDispatchSpecimen(),
            factory.createDispatchSpecimen()
        };
        Set<DispatchSpecimenInfo> dsInfos = new HashSet<DispatchSpecimenInfo>();
        for (DispatchSpecimen dispatchSpecimen : dispatchSpecimens) {
            dsInfos.add(new DispatchSpecimenInfo(null, dispatchSpecimen.getSpecimen().getId(),
                dispatchSpecimen.getState()));
        }

        ShipmentInfo shipInfo = factory.createShipmentInfo();
        ShipmentInfoSaveInfo shipmentSaveInfo = new ShipmentInfoSaveInfo(null,
            shipInfo.getBoxNumber(), shipInfo.getPackedAt(), shipInfo.getReceivedAt(),
            shipInfo.getWaybill(), shipInfo.getShippingMethod().getId());

        Integer dispatchId = exec(new DispatchSaveAction(new DispatchSaveInfo(null,
            dispatch.getReceiverCenter(), dispatch.getSenderCenter(),
            DispatchState.CREATION, null), dsInfos, shipmentSaveInfo)).getId();

        exec(new DispatchChangeStateAction(dispatchId, DispatchState.IN_TRANSIT, shipmentSaveInfo));

        Criteria c = session.createCriteria(DispatchSpecimen.class)
            .add(Restrictions.eq("dispatch.id", dispatchId))
            .setFetchMode("specimen", FetchMode.JOIN);

        @SuppressWarnings("unchecked")
        List<DispatchSpecimen> list = c.list();
        for (DispatchSpecimen dispatchSpecimen : list) {
            // ensure current center on specimens has not changed - it is only
            // updated when the dipatch goes into in_transit state
            Specimen specimen = dispatchSpecimen.getSpecimen();
            Assert.assertEquals(site, specimen.getCurrentCenter());
        }
    }

    @Test
    public void getDispatchInfo() throws Exception {
        session.beginTransaction();
        Dispatch dispatch = factory.createDispatch(clinic, site);
        DispatchSpecimen [] dispatchSpecimens = new DispatchSpecimen [] {
            factory.createDispatchSpecimen(),
            factory.createDispatchSpecimen()
        };
        dispatch.setState(DispatchState.IN_TRANSIT);
        session.getTransaction().commit();

        DispatchReadInfo readInfo = exec(new DispatchGetInfoAction(dispatch.getId()));

        Assert.assertEquals(dispatch, readInfo.dispatch);

        Set<Integer> expectedSpecimenIds = new HashSet<Integer>();
        for (DispatchSpecimen ds : dispatchSpecimens) {
            expectedSpecimenIds.add(ds.getSpecimen().getId());
        }

        Set<Integer> actualSpecimenIds = new HashSet<Integer>();
        for (DispatchSpecimen ds : readInfo.dispatchSpecimens) {
            actualSpecimenIds.add(ds.getSpecimen().getId());
        }

        Assert.assertEquals(expectedSpecimenIds, actualSpecimenIds);
    }

    @Test
    public void testDelete() throws Exception {
        session.beginTransaction();
        Dispatch dispatch = factory.createDispatch(clinic, site);
        dispatch.setState(DispatchState.IN_TRANSIT);
        session.getTransaction().commit();

        try {
            exec(new DispatchDeleteAction(dispatch));
            Assert.fail("should not be allowed to delete dispatch that is IN_TRANSIT");
        } catch (ActionException e) {
            // intentionally empty
        }

        session.beginTransaction();
        dispatch.setState(DispatchState.CREATION);
        session.saveOrUpdate(dispatch);
        session.getTransaction().commit();

        // should be allowed to delete dispatch now
        exec(new DispatchDeleteAction(dispatch));
    }

    @Test
    public void testComment() throws Exception {
        DispatchSaveInfo d = DispatchHelper.createSaveDispatchInfoRandom(site, clinic,
            DispatchState.IN_TRANSIT, getMethodNameR());
        Set<DispatchSpecimenInfo> specs = DispatchHelper.createSaveDispatchSpecimenInfoRandom(
            getExecutor(), patientId, clinic);
        ShipmentInfoSaveInfo shipsave = ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id = exec(new DispatchSaveAction(d, specs, shipsave)).getId();
        d = new DispatchSaveInfo(d, id);

        DispatchReadInfo info = exec(new DispatchGetInfoAction(id));
        Assert.assertEquals(1, info.dispatch.getComments().size());
        exec(new DispatchSaveAction(d, specs, shipsave)).getId();
        info = exec(new DispatchGetInfoAction(id));
        Assert.assertEquals(2, info.dispatch.getComments().size());
        exec(new DispatchSaveAction(d, specs, shipsave)).getId();
        info = exec(new DispatchGetInfoAction(id));
        Assert.assertEquals(3, info.dispatch.getComments().size());
    }
}
