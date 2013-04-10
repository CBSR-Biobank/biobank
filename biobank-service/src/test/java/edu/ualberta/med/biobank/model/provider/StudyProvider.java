package edu.ualberta.med.biobank.model.provider;

import java.util.Date;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;

import edu.ualberta.med.biobank.dao.StudyDao;
import edu.ualberta.med.biobank.dao.UserDao;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.study.Study;

public class StudyProvider
    extends AbstractProvider<Study> {

    @Autowired
    UserDao userDao;

    @Autowired
    private StudyDao studyDao;

    private int name = 1;

    @Autowired
    public StudyProvider(Mother mother) {
        super(mother);
        mother.bind(Study.class, this);
    }

    @Override
    public Study onCreate() {
        User superadmin = userDao.get(1L);
        Assert.assertEquals("superadmin", superadmin.getLogin());

        Date date = new Date();

        Study study = new Study();
        study.setName(mother.getName() + "_" + name++);
        study.setDescription("no description.");
        study.setEnabled(Boolean.TRUE);
        study.setInsertedAndUpdated(superadmin, date.getTime());
        return study;
    }

    @Override
    public Study save(Study study) {
        studyDao.save(study);
        return study;
    }
}
