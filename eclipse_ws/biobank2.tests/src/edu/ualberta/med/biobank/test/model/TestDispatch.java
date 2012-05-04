package edu.ualberta.med.biobank.test.model;

import junit.framework.Assert;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.test.DbTest;
import edu.ualberta.med.biobank.test.model.util.HibernateHelper;

public class TestDispatch extends DbTest {
    @Test
    public void stateIds() {
        Transaction tx = session.beginTransaction();

        Dispatch dispatch = factory.createDispatch();

        Query query = HibernateHelper.getDehydratedPropertyQuery(
            session, dispatch, "state");

        try {
            for (DispatchState state : DispatchState.values()) {
                dispatch.setState(state);
                session.update(dispatch);
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
