package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.DbTest;

public class TestShipment extends DbTest {
    // @Test
    // public void stateIds() {
    // Transaction tx = session.beginTransaction();
    //
    // Center sender = factory.createCenter();
    // Center receiver = factory.createCenter();
    // Shipment dispatch = factory.createDispatch(sender, receiver);
    //
    // Query query = HibernateHelper.getDehydratedPropertyQuery(
    // session, dispatch, "state");
    //
    // try {
    // for (ShipmentState state : ShipmentState.values()) {
    // dispatch.setState(state);
    // session.update(dispatch);
    // session.flush();
    //
    // int id = ((Number) query.uniqueResult()).intValue();
    // Assert.assertEquals("persisted id does not match enum's id",
    // state.getId(), new Integer(id));
    // }
    // } finally {
    // tx.rollback();
    // }
    // }
}
