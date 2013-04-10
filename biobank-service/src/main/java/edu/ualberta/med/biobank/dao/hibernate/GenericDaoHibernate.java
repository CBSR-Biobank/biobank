package edu.ualberta.med.biobank.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ualberta.med.biobank.dao.GenericDao;
import edu.ualberta.med.biobank.model.VersionedLongIdModel;

public class GenericDaoHibernate<T extends VersionedLongIdModel>
    implements GenericDao<T> {

    private SessionFactory sessionFactory;

    private final Class<T> type;

    public GenericDaoHibernate(Class<T> type) {
        super();
        this.type = type;
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(Long id) {
        return (T) sessionFactory.getCurrentSession().get(type, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> getAll() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(type);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    public void save(T object) {
        sessionFactory.getCurrentSession().save(object);
        sessionFactory.getCurrentSession().flush();
    }

    @Override
    public void update(T object) {
        sessionFactory.getCurrentSession().update(object);
        sessionFactory.getCurrentSession().flush();
    }

    @Override
    public void delete(T object) {
        sessionFactory.getCurrentSession().delete(object);
    }

    @Override
    public void indexEntity(T object) {
        FullTextSession fullTextSession = Search.getFullTextSession(
            sessionFactory.getCurrentSession());
        fullTextSession.index(object);
    }

    @Override
    public void indexAllItems() {
        FullTextSession fullTextSession = Search.getFullTextSession(
            sessionFactory.getCurrentSession());
        ScrollableResults results = fullTextSession.createCriteria(this.type).scroll(ScrollMode.FORWARD_ONLY);
        int counter = 0, numItemsInGroup = 10;
        while (results.next()) {
            fullTextSession.index(results.get(0));
            if (counter++ % numItemsInGroup == 0) {
                fullTextSession.flushToIndexes();
                fullTextSession.clear();
            }
        }
    }

    @Autowired
    public void setupSessionFactory(SessionFactory sessionFactory) {
        this.setSessionFactory(sessionFactory);
    }

}
