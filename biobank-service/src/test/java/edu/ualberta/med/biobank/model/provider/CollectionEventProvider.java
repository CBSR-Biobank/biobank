package edu.ualberta.med.biobank.model.provider;

import java.util.Date;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;

import edu.ualberta.med.biobank.dao.CollectionEventDao;
import edu.ualberta.med.biobank.dao.UserDao;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.study.CollectionEvent;
import edu.ualberta.med.biobank.model.study.CollectionEventType;
import edu.ualberta.med.biobank.model.study.Patient;

public class CollectionEventProvider
    extends AbstractProvider<CollectionEvent> {

    @Autowired
    UserDao userDao;

    @Autowired
    private CollectionEventDao collectionEventDao;

    private int visitNumber = 1;

    @Autowired
    public CollectionEventProvider(Mother mother) {
        super(mother);
        mother.bind(CollectionEvent.class, this);
    }

    @Override
    public CollectionEvent onCreate() {
        User superadmin = userDao.get(1L);
        Assert.assertEquals("superadmin", superadmin.getLogin());

        Date date = new Date();

        CollectionEvent ce = new CollectionEvent();
        ce.setType(mother.getProvider(CollectionEventType.class).get());
        ce.setPatient(mother.getProvider(Patient.class).get());
        ce.setVisitNumber(visitNumber++);
        ce.setTimeDone(date);
        ce.setInsertedAndUpdated(superadmin, date.getTime());
        return ce;
    }

    @Override
    public CollectionEvent save(CollectionEvent cevent) {
        collectionEventDao.save(cevent);
        return cevent;
    }
}
