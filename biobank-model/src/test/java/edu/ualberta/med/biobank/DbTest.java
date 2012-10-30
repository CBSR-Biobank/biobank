package edu.ualberta.med.biobank;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;

import edu.ualberta.med.biobank.model.provider.EntityProcessor;
import edu.ualberta.med.biobank.model.provider.EntityProvider;
import edu.ualberta.med.biobank.model.provider.Mother;

public class DbTest
    extends BaseTest {

    protected static final SessionFactory sessionFactory;

    static {
        // configure() configures settings from hibernate.cfg.xml found in the
        // resources directory
        Configuration configuration = new Configuration().configure();
        ServiceRegistry reg = new ServiceRegistryBuilder().applySettings(
            configuration.getProperties()).buildServiceRegistry();

        sessionFactory = configuration.buildSessionFactory(reg);
    }

    private Mother mother;
    protected Session session;

    @Before
    public void setUp() throws Exception {
        session = sessionFactory.openSession();
        mother = new Mother(getMethodNameR());

        // automatically save objects whenever they're created
        mother.setEntityProcessor(new EntityProcessor<Object>() {
            @Override
            public void process(Object entity) {
                session.save(entity);
            }
        });

        session.beginTransaction();
    }

    @After
    public void tearDown() throws Exception {
        Transaction tx = session.getTransaction();
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }

        mother = null;
        session.close();
    }

    protected <T> EntityProvider<T> getProvider(Class<T> klazz) {
        return mother.getProvider(klazz);
    }

    /**
     * Check an assertion when a certain type of entity is updated and when it
     * is inserted.
     * 
     * @param klazz the type of entity to insert and update
     * @param processor run on the entity instance to "make it wrong"
     * @param assertion describes the expected ConstraintViolationException
     */
    protected <T> void persistEntity(Class<T> klazz,
        EntityProcessor<T> processor,
        ConstraintViolationAssertion assertion) {
        T entity = getProvider(klazz).create();

        try {
            processor.process(entity);
            session.update(entity);
            session.flush();
            Assert.fail("update should not be allowed");
        } catch (ConstraintViolationException e) {
            assertion.withRootBean(entity).assertIn(e);
        }

        try {
            entity = getProvider(klazz).create();
            processor.process(entity);
            session.save(entity);
            session.flush();
            Assert.fail("insert should not be allowed");
        } catch (ConstraintViolationException e) {
            assertion.withRootBean(entity).assertIn(e);
        }
    }
}
