package edu.ualberta.med.biobank.model.provider;

import java.util.Date;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;

import edu.ualberta.med.biobank.dao.CollectionEventTypeDao;
import edu.ualberta.med.biobank.dao.UserDao;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.study.CollectionEventType;
import edu.ualberta.med.biobank.model.study.Study;

public class CollectionEventTypeProvider
    extends AbstractProvider<CollectionEventType> {

    @Autowired
    UserDao userDao;

    @Autowired
    private CollectionEventTypeDao collectionEventTypeDao;

    private int name = 1;

    @Autowired
    public CollectionEventTypeProvider(Mother mother) {
        super(mother);
        mother.bind(CollectionEventType.class, this);
    }

    @Override
    public CollectionEventType onCreate() {
        User superadmin = userDao.get(1L);
        Assert.assertEquals("superadmin", superadmin.getLogin());

        Date date = new Date();

        CollectionEventType type = new CollectionEventType();
        Study study = mother.getProvider(Study.class).get();
        type.setStudy(study);
        type.setName(mother.getName() + "_" + name++);
        type.setDescription("no description");
        type.setRecurring(Boolean.TRUE);
        type.setInsertedAndUpdated(superadmin, date.getTime());
        return type;
    }

    @Override
    public CollectionEventType save(CollectionEventType ceType) {
        collectionEventTypeDao.save(ceType);
        return ceType;
    }
}
