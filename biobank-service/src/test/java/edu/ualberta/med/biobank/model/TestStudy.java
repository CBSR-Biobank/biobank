package edu.ualberta.med.biobank.model;

import java.util.Date;

import org.hibernate.Session;
import org.junit.Test;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.study.Study;

// @RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional()
public class TestStudy extends DbTest {

    @Test
    public void test() {
        Session session = sessionFactory.getCurrentSession();

        Date date = new Date();

        // User user = new User();
        // user.setAdmin(Boolean.TRUE);
        // user.setEnabled(Boolean.TRUE);
        // user.setLogin("tester");
        // user.setFullName("tester");
        // user.setEmail("tester@biobank.com");
        // user.setTimeInserted(date.getTime());
        // user.setTimeUpdated(date.getTime());
        // user.setPasswordChangeNeeded(Boolean.FALSE);
        // user.setMailingListSubscriber(Boolean.TRUE);
        // user.setInsertedBy(user);
        // user.setUpdatedBy(user);
        // session.save(user);

        User superadmin = (User) session.load(User.class, 1);

        Study study = new Study();
        study.setName("test");
        study.setDescription("test");
        study.setEnabled(Boolean.TRUE);
        study.setTimeInserted(date.getTime());
        study.setTimeUpdated(date.getTime());
        study.setInsertedBy(superadmin);
        study.setUpdatedBy(superadmin);
        session.save(study);

    }
}
