package edu.ualberta.med.biobank.model;

import java.util.Date;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.Test;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.study.Study;

@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional()
public class TestStudy extends DbTest {

    @Test
    public void test() {
        Session session = sessionFactory.getCurrentSession();

        Date date = new Date();

        User superadmin = (User) session.load(User.class, 1);
        Assert.assertEquals("superadmin", superadmin.getLogin());

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
