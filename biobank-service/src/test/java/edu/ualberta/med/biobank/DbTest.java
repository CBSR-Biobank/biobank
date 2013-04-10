package edu.ualberta.med.biobank;

import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.ualberta.med.biobank.dao.GenericDao;
import edu.ualberta.med.biobank.model.VersionedLongIdModel;
import edu.ualberta.med.biobank.model.provider.EntityProcessor;
import edu.ualberta.med.biobank.model.provider.EntityProvider;
import edu.ualberta.med.biobank.model.provider.Mother;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HibernateConfigDev.class)
@ActiveProfiles("dev")
public class DbTest
    extends BaseTest {

    @Autowired
    private Mother mother;

    @Before
    public void setUp() throws Exception {
        mother.setName(getMethodNameR());
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
    protected <T extends VersionedLongIdModel> void persistEntity(Class<T> klazz,
        GenericDao<T> dao, EntityProcessor<T> processor, ConstraintViolationAssertion assertion) {
        T entity = getProvider(klazz).create();

        try {
            processor.process(entity);
            dao.update(entity);
            Assert.fail("update should not be allowed");
        } catch (ConstraintViolationException e) {
            assertion.withRootBean(entity).assertIn(e);
        }

        try {
            entity = getProvider(klazz).create();
            processor.process(entity);
            dao.save(entity);
            Assert.fail("insert should not be allowed");
        } catch (ConstraintViolationException e) {
            assertion.withRootBean(entity).assertIn(e);
        }
    }
}
