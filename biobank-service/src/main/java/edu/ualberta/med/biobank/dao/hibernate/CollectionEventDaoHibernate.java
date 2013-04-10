package edu.ualberta.med.biobank.dao.hibernate;

import org.springframework.stereotype.Repository;

import edu.ualberta.med.biobank.dao.CollectionEventDao;
import edu.ualberta.med.biobank.model.study.CollectionEvent;

@Repository("CollectionEventDao")
public class CollectionEventDaoHibernate
    extends GenericDaoHibernate<CollectionEvent>
    implements CollectionEventDao {

    public CollectionEventDaoHibernate() {
        super(CollectionEvent.class);
    }

}
