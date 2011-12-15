package edu.ualberta.med.biobank.test.action.tmp;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.FlushMode;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.type.Type;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;

public class TestStuff {
    private final SessionFactory sessionFactory;
    private final Session session;

    private TestStuff() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        // new Configuration().configure().getEventListeners().set
        session = sessionFactory.openSession(new Interceptor());
        session.setFlushMode(FlushMode.COMMIT);
    }

    public void run() {
        ActivityStatus active =
            (ActivityStatus) session
                .createQuery(
                    "select o from " + ActivityStatus.class.getName()
                        + " o where name = 'Active'").list().get(0);

        Site s1 = new Site();
        s1.setName("a");
        s1.setNameShort("b");
        s1.setAddress(new Address());
        s1.setActivityStatus(active);

        System.out.println("start");

        session.beginTransaction();

        try {
            session.saveOrUpdate(s1);
            session.getTransaction().commit();
            session.flush();
        } catch (PropertyValueException caught) {
            System.out.println(caught.getEntityName());
            System.out.println(caught.getPropertyName());
        } catch (ConstraintViolationException caught) {
            System.out.println(caught.getConstraintName());
            System.out.println(caught.getErrorCode());
        }

        System.out.println("finish");
    }

    public static void main(String[] args) {
        new TestStuff().run();
    }

    public static class Interceptor extends EmptyInterceptor {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean onSave(Object entity, Serializable id, Object[] state,
            String[] propertyNames, Type[] types) {
            System.out.println("onSave");
            return true;
        }

        @Override
        public boolean onFlushDirty(Object entity, Serializable id,
            Object[] currentState, Object[] previousState,
            String[] propertyNames, Type[] types) {
            System.out.println("onFlushDirty");
            return true;
        }
    }
}
