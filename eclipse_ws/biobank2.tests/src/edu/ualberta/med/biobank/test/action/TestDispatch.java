package edu.ualberta.med.biobank.test.action;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.ShippingMethod;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.test.action.helper.DispatchHelper;
import edu.ualberta.med.biobank.test.action.helper.ShipmentInfoHelper;

public class TestDispatch extends TestAction {

    private static final Logger LOG = LoggerFactory.getLogger(TestDispatch.class);

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

        @SuppressWarnings("unchecked")
        List<DispatchSpecimen> list = session.createCriteria(DispatchSpecimen.class)
            .add(Restrictions.eq("dispatch.id", dispatchId))
            .setFetchMode("specimen", FetchMode.JOIN).list();

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

    /*
     * Ensure current centre on specimens has not been updated after a dispatch goes into IN_TRANSIT
     * state.
     */
    @Test
    public void specimenCurrentCenter() throws Exception {
        session.beginTransaction();
        Dispatch dispatch = factory.createDispatch(clinic, site);
        DispatchSpecimen[] dispatchSpecimens = new DispatchSpecimen[] {
            factory.createDispatchSpecimen(),
            factory.createDispatchSpecimen()
        };
        ShippingMethod shippingMethod = factory.createShippingMethod();
        session.getTransaction().commit();

        for (DispatchSpecimen dispatchSpecimen : dispatchSpecimens) {
            Assert.assertEquals(clinic, dispatchSpecimen.getSpecimen().getCurrentCenter());
        }

        exec(new DispatchChangeStateAction(
            dispatch.getId(),
            DispatchState.IN_TRANSIT,
            new ShipmentInfoSaveInfo(
                null,
                getMethodNameR(),
                new Date(),
                new Date(),
                getMethodNameR(),
                shippingMethod.getId())));

        session.clear();

        @SuppressWarnings("unchecked")
        List<DispatchSpecimen> list = session.createCriteria(DispatchSpecimen.class)
            .add(Restrictions.eq("dispatch.id", dispatch.getId()))
            .setFetchMode("specimen", FetchMode.JOIN).list();

        for (DispatchSpecimen dispatchSpecimen : list) {
            Assert.assertEquals(site, dispatchSpecimen.getSpecimen().getCurrentCenter());
        }
    }

    /**
     * Ensure current centre on specimens has been updated after a dispatch goes into RECEIVED state
     * for extra specimens.
     */
    @Test
    public void specimenCurrentCenterOnReceive() throws Exception {
        session.beginTransaction();
        Dispatch dispatch = factory.createDispatch(clinic, site);
        DispatchSpecimen[] dispatchSpecimens = new DispatchSpecimen[] {
            factory.createDispatchSpecimen(),
            factory.createDispatchSpecimen()
        };

        // extra specimens
        Specimen[] extraSpecimens = new Specimen[] {
            factory.createParentSpecimen(),
            factory.createParentSpecimen()
        };

        ShippingMethod shippingMethod = factory.createShippingMethod();
        session.getTransaction().commit();

        for (DispatchSpecimen dispatchSpecimen : dispatchSpecimens) {
            Assert.assertEquals(clinic, dispatchSpecimen.getSpecimen().getCurrentCenter());
        }

        ShipmentInfoSaveInfo shipmentInfo = new ShipmentInfoSaveInfo(
            null,
            getMethodNameR(),
            new Date(),
            new Date(),
            getMethodNameR(),
            shippingMethod.getId());

        exec(new DispatchChangeStateAction(
            dispatch.getId(),
            DispatchState.IN_TRANSIT,
            shipmentInfo));

        session.clear();

        DispatchSaveInfo dispatchInfo = new DispatchSaveInfo(
            dispatch.getId(),
            site,
            clinic,
            DispatchState.RECEIVED,
            null);

        Set<DispatchSpecimenInfo> dsInfos =
            new HashSet<DispatchSpecimenInfo>(dispatchSpecimens.length);

        for (DispatchSpecimen dispatchSpecimen : dispatchSpecimens) {
            dsInfos.add(new DispatchSpecimenInfo(
                dispatchSpecimen.getId(),
                dispatchSpecimen.getSpecimen().getId(),
                DispatchSpecimenState.RECEIVED));
        }

        for (Specimen specimen : extraSpecimens) {
            dsInfos.add(new DispatchSpecimenInfo(
                null,
                specimen.getId(),
                DispatchSpecimenState.EXTRA));
        }

        // save dispatch as received
        exec(new DispatchSaveAction(dispatchInfo, dsInfos, shipmentInfo)).getId();

        @SuppressWarnings("unchecked")
        List<DispatchSpecimen> list = session.createCriteria(DispatchSpecimen.class)
            .add(Restrictions.eq("dispatch.id", dispatch.getId()))
            .setFetchMode("specimen", FetchMode.JOIN).list();

        for (DispatchSpecimen dispatchSpecimen : list) {
            LOG.info("specimen center: {}", dispatchSpecimen.getSpecimen().getCurrentCenter().getNameShort());
            Assert.assertEquals(site, dispatchSpecimen.getSpecimen().getCurrentCenter());
        }
    }

    /**
     * The positions on the specimens must be cleared when the dispatch state is set to IN_TRANSIT.
     * 
     */
    @Test
    public void specimenPosition() throws Exception {
        session.beginTransaction();

        Center receivingSite = factory.createSite();
        Center sendingSite = factory.createSite();
        Container container = factory.createContainer();

        Dispatch dispatch = factory.createDispatch(sendingSite, receivingSite);
        DispatchSpecimen[] dispatchSpecimens = new DispatchSpecimen[] {
            factory.createDispatchSpecimen(),
            factory.createDispatchSpecimen()
        };

        int row = 0;
        ContainerType containerType = container.getContainerType();
        for (DispatchSpecimen dispatchSpecimen : dispatchSpecimens) {
            Specimen spc = dispatchSpecimen.getSpecimen();

            containerType.getSpecimenTypes().add(spc.getSpecimenType());
            session.update(containerType);
            session.flush();

            SpecimenPosition pos = new SpecimenPosition();
            pos.setContainer(container);
            pos.setSpecimen(spc);
            pos.setRow(row);
            pos.setCol(0);
            pos.setPositionString("A1");

            spc.setSpecimenPosition(pos);
            ++row;
        }

        ShippingMethod shippingMethod = factory.createShippingMethod();
        session.getTransaction().commit();

        for (DispatchSpecimen dispatchSpecimen : dispatchSpecimens) {
            Assert.assertEquals(sendingSite, dispatchSpecimen.getSpecimen().getCurrentCenter());
            Assert.assertNotNull(dispatchSpecimen.getSpecimen().getSpecimenPosition());
        }

        exec(new DispatchChangeStateAction(dispatch.getId(), DispatchState.IN_TRANSIT,
            new ShipmentInfoSaveInfo(null, getMethodNameR(), new Date(), new Date(),
                getMethodNameR(), shippingMethod.getId())));
        session.clear();

        @SuppressWarnings("unchecked")
        List<DispatchSpecimen> list = session.createCriteria(DispatchSpecimen.class)
            .add(Restrictions.eq("dispatch.id", dispatch.getId()))
            .setFetchMode("specimen", FetchMode.JOIN).list();

        for (DispatchSpecimen dispatchSpecimen : list) {
            Assert.assertEquals(receivingSite, dispatchSpecimen.getSpecimen().getCurrentCenter());
            Assert.assertEquals(null, dispatchSpecimen.getSpecimen().getSpecimenPosition());
        }
    }

    @Test
    public void getDispatchInfo() throws Exception {
        session.beginTransaction();
        Dispatch dispatch = factory.createDispatch(clinic, site);
        DispatchSpecimen[] dispatchSpecimens = new DispatchSpecimen[] {
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
        DispatchSaveInfo dispatchSaveInfo = DispatchHelper.createSaveDispatchInfoRandom(site, clinic,
            DispatchState.IN_TRANSIT, getMethodNameR());
        Set<DispatchSpecimenInfo> dsInfo = DispatchHelper.createSaveDispatchSpecimenInfoRandom(
            getExecutor(), patientId, clinic);
        ShipmentInfoSaveInfo shipsave = ShipmentInfoHelper.createRandomShipmentInfo(getExecutor());
        Integer id = exec(new DispatchSaveAction(dispatchSaveInfo, dsInfo, shipsave)).getId();
        dispatchSaveInfo = new DispatchSaveInfo(dispatchSaveInfo, id);

        DispatchReadInfo info = exec(new DispatchGetInfoAction(id));
        Assert.assertEquals(1, info.dispatch.getComments().size());

        // update the dispatch specimen information
        dsInfo = new HashSet<DispatchSpecimenInfo>();
        for (DispatchSpecimen dispatchSpecimen : info.dispatchSpecimens) {
            dsInfo.add(new DispatchSpecimenInfo(dispatchSpecimen.getId(),
                dispatchSpecimen.getSpecimen().getId(), dispatchSpecimen.getState()));
        }

        exec(new DispatchSaveAction(dispatchSaveInfo, dsInfo, shipsave)).getId();
        info = exec(new DispatchGetInfoAction(id));
        Assert.assertEquals(2, info.dispatch.getComments().size());

        exec(new DispatchSaveAction(dispatchSaveInfo, dsInfo, shipsave)).getId();
        info = exec(new DispatchGetInfoAction(id));
        Assert.assertEquals(3, info.dispatch.getComments().size());
    }
}
