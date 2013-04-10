package edu.ualberta.med.biobank.dao.hibernate;

import org.springframework.stereotype.Repository;

import edu.ualberta.med.biobank.dao.CollectionEventTypeDao;
import edu.ualberta.med.biobank.model.study.CollectionEventType;

@Repository("CollectionEventTypeDao")
public class CollectionEventTypeDaoHibernate
    extends GenericDaoHibernate<CollectionEventType>
    implements CollectionEventTypeDao {

    public CollectionEventTypeDaoHibernate() {
        super(CollectionEventType.class);
    }

}