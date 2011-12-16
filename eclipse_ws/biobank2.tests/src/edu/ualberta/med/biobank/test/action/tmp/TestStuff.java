package edu.ualberta.med.biobank.test.action.tmp;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.hibernate.FlushMode;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.exception.ConstraintViolationException;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;

public class TestStuff {
    private final SessionFactory sessionFactory;
    private final Session session;

    private TestStuff() {
        Configuration configuration = new Configuration().configure();
        configuration.getEventListeners().setPreUpdateEventListeners(new PreUpdateEventListener[] {new Handler()});
//        configuration.setProperty("hibernate.show_sql", "true");
//        configuration.setProperty("hibernate.format_sql", "true");
//        configuration.setProperty("hibernate.use_sql_comments", "true");
        
        sessionFactory = configuration.buildSessionFactory();
        
        session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.COMMIT);
    }

    public void run() {
        Site s1 = null;
        
        List<?> results =  session.createQuery("select o from " + Site.class.getName() + " o where name = 'a'").list();
        if (!results.isEmpty()) {
            s1 = (Site) results.get(0);
        } else {            
            s1 = new Site();
            s1.setAddress(new Address());
            s1.setName("a");
            s1.setNameShort("b");
        }

        ActivityStatus active =
            (ActivityStatus) session
                .createQuery(
                    "select o from " + ActivityStatus.class.getName()
                        + " o where name = 'Active'").list().get(0);
        
        Address address = s1.getAddress();
        address.setCity(new BigInteger(10, new Random()).toString(32));
        
        s1.setActivityStatus(active);
        s1.setNameShort(new BigInteger(130, new Random()).toString(32));

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
    
    public static class Handler implements PreUpdateEventListener {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean onPreUpdate(PreUpdateEvent event) {
            System.out.println(Arrays.toString(event.getPersister().getPropertyNames()));
            System.out.println(Arrays.toString(event.getState()));
            System.out.println(Arrays.toString(event.getOldState()));
            
            return false;
        }
    }
}
