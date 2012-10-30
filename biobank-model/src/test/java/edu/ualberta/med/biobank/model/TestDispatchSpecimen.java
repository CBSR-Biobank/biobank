package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.DbTest;

public class TestDispatchSpecimen extends DbTest {
    // @Test
    // public void stateIds() {
    // Transaction tx = session.beginTransaction();
    //
    // ShipmentSpecimen dispatchSpecimen = factory.createDispatchSpecimen();
    //
    // Query query = HibernateHelper.getDehydratedPropertyQuery(
    // session, dispatchSpecimen, "state");
    //
    // try {
    // for (ShipmentItemState state : ShipmentItemState.values()) {
    // dispatchSpecimen.setState(state);
    // session.update(dispatchSpecimen);
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
