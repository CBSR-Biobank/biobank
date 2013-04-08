package edu.ualberta.med.biobank.model;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import edu.ualberta.med.biobank.DbTest;
import edu.ualberta.med.biobank.dao.StudyDao;
import edu.ualberta.med.biobank.dao.UserDao;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.study.Study;

@TransactionConfiguration(transactionManager = "transactionManager",
    defaultRollback = false)
@Transactional()
public class TestStudyDao extends DbTest {

    @Autowired
    StudyDao studyDao;

    @Autowired
    UserDao userDao;

    @Test
    public void get() {
        Date date = new Date();

        User superadmin = userDao.get(1L);
        Assert.assertEquals("superadmin", superadmin.getLogin());

        Study study = new Study();
        study.setName("test");
        study.setDescription("test");
        study.setEnabled(Boolean.TRUE);
        study.setTimeInserted(date.getTime());
        study.setTimeUpdated(date.getTime());
        study.setInsertedBy(superadmin);
        study.setUpdatedBy(superadmin);
        studyDao.save(study);

    }
}
