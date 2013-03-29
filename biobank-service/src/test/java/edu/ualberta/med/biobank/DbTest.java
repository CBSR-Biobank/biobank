package edu.ualberta.med.biobank;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.ualberta.med.biobank.model.provider.EntityProcessor;
import edu.ualberta.med.biobank.model.provider.EntityProvider;
import edu.ualberta.med.biobank.model.provider.Mother;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HibernateConfig.class)
@ActiveProfiles("dev")
public class DbTest
    extends BaseTest {
    protected SessionFactory sessionFactory;
    private Mother mother;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Before
    public void setUp() throws Exception {
        mother = new Mother(getMethodNameR());

        // automatically save objects whenever they're created
        mother.setEntityProcessor(new EntityProcessor<Object>() {
            @Override
            public void process(Object entity) {
                Session session = sessionFactory.getCurrentSession();
                session.save(entity);
                // session.flush();
            }
        });
    }

    protected <T> EntityProvider<T> getProvider(Class<T> klazz) {
        return mother.getProvider(klazz);
    }

    /**
     * Check an assertion when a certain type of entity is updated and when it is inserted.
     * 
     * @param klazz the type of entity to insert and update
     * @param processor run on the entity instance to "make it wrong"
     * @param assertion describes the expected ConstraintViolationException
     */
    protected <T> void persistEntity(Class<T> klazz,
        EntityProcessor<T> processor, ConstraintViolationAssertion assertion) {
        T entity = getProvider(klazz).create();

        Session session = sessionFactory.getCurrentSession();

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
