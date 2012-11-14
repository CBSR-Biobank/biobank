package edu.ualberta.med.biobank.test.model;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.test.TestDb;
import edu.ualberta.med.biobank.test.model.util.HibernateHelper;

public class TestDispatchSpecimen extends TestDb {
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

    @Test
    public void dispatchSpecimensDuplicateSpecimen() {
        session.beginTransaction();
        DispatchSpecimen [] dispatchSpecimens = new DispatchSpecimen [] {
            factory.createDispatchSpecimen(),
            factory.createDispatchSpecimen()
        };

        // have both dispatch specimens contain the same specimen
        dispatchSpecimens[1].setSpecimen(dispatchSpecimens[0].getSpecimen());

        try {
            session.flush();
            Assert.fail("should not be allowed to create 2 dispatch specimens assoc to same specimen");
        } catch (ConstraintViolationException e) {
            // intentionally empty
        }
    }

    @Test
    public void saveWithNoSpecimens() {
        session.beginTransaction();
        DispatchSpecimen dispatchSpecimen = factory.createDispatchSpecimen();
        dispatchSpecimen.setSpecimen(null);

        // have both dispatch specimens contain the same specimen
        try {
            session.flush();
            Assert.fail("should not be allowed to create dispatch specimen with null specimen");
        } catch (ConstraintViolationException e) {
            // intentionally empty
        }
    }
}
