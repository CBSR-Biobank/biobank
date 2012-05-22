package edu.ualberta.med.biobank.test.model;

import junit.framework.Assert;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.test.model.util.HibernateHelper;

public class TestDispatchSpecimen extends DbTest {
    @Test
    public void stateIds() {
        Transaction tx = session.beginTransaction();

        DispatchSpecimen dispatchSpecimen = factory.createDispatchSpecimen();

        Query query = HibernateHelper.getDehydratedPropertyQuery(
            session, dispatchSpecimen, "state");

        try {
            for (DispatchSpecimenState state : DispatchSpecimenState.values()) {
                dispatchSpecimen.setState(state);
                session.update(dispatchSpecimen);
                session.flush();

                int id = ((Number) query.uniqueResult()).intValue();
                Assert.assertEquals("persisted id does not match enum's id",
                    state.getId(), new Integer(id));
            }
        } finally {
            tx.rollback();
        }
    }
}
