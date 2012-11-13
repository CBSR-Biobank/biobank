package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.DbTest;

public class TestRequestSpecimen extends DbTest {
    // @Test
    // public void stateIds() {
    // Transaction tx = session.beginTransaction();
    //
    // RequestSpecimen requestSpecimen = factory.createRequestSpecimen();
    //
    // Query query = HibernateHelper.getDehydratedPropertyQuery(
    // session, requestSpecimen, "state");
    //
    // try {
    // for (RequestSpecimenState state : RequestSpecimenState.values()) {
    // requestSpecimen.setState(state);
    // session.update(requestSpecimen);
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
