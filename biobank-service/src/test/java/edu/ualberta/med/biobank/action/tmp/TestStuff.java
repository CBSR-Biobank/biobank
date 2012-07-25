package edu.ualberta.med.biobank.action.tmp;

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

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.type.ActivityStatus;

public class TestStuff {
    private final SessionFactory sessionFactory;
    private final Session session;

    private TestStuff() {
        Configuration configuration = new Configuration().configure();
        System.out.println(Arrays.toString(configuration.getEventListeners()
            .getPreUpdateEventListeners()));
        configuration.getEventListeners().setPreUpdateEventListeners(
            new PreUpdateEventListener[] { new Handler() });
        // configuration.setProperty("hibernate.show_sql", "true");
        // configuration.setProperty("hibernate.format_sql", "true");
        // configuration.setProperty("hibernate.use_sql_comments", "true");
        // configuration.setProperty("javax.persistence.validation.mode",
        // "none");

        configuration.setProperty("hibernate.check_nullability", "false");

        sessionFactory = configuration.buildSessionFactory();

        session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.COMMIT);
    }

    public void run() {
        Site s1 = null;

        List<?> results =
            session
                .createQuery(
                    "select o from " + Site.class.getName()
                        + " o where name = 'a'").list();
        if (!results.isEmpty()) {
            s1 = (Site) results.get(0);
        } else {
            s1 = new Site();
            s1.setAddress(new Address());
            s1.setName("a");
            s1.setNameShort("b");
        }

        ActivityStatus active = ActivityStatus.ACTIVE;

        Address address = s1.getAddress();
        address.setCity(new BigInteger(10, new Random()).toString(32));

        // s1.setAddress(null);

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
            caught.printStackTrace();
        } catch (ConstraintViolationException caught) {
            System.out.println(caught.getConstraintName());
            System.out.println(caught.getErrorCode());
            caught.printStackTrace();
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
            // Session session = event.getSession();

            // int[] dirty =
            // event.getPersister().findDirty(event.getState(),
            // event.getOldState(), event.getEntity(), session);
            // int[] modified =
            // event.getPersister().findModified(event.getState(),
            // event.getOldState(), event.getEntity(), event.getSource());

            System.out.println("propertyNames:");
            String[] propertyNames = event.getPersister().getPropertyNames();
            for (int i = 0, n = propertyNames.length; i < n; i++) {
                System.out.println(i + ": " + propertyNames[i]);
            }

            // if (event.getEntity() instanceof Site) {
            // Site site = (Site) event.getEntity();
            // EntityPersister addressEntityPersister =
            // event.getSource().getEntityPersister(
            // Address.class.getName(), site.getAddress());
            // // Object[] propertyValues =
            // // addressEntityPersister.getPropertyValues(site.getAddress(),
            // // EntityMode.POJO);
            //
            // Object[] databaseSnapshot =
            // addressEntityPersister.getDatabaseSnapshot(site
            // .getAddress().getId(), event.getSource());
            //
            // Map<?, ?> map = new HashMap<Object, Object>();
            //
            // Object[] propertyValuesToInsert =
            // addressEntityPersister.getPropertyValuesToInsert(
            // site.getAddress(), map,
            // event.getSource());
            //
            // // System.out.println("propertyValues: "
            // // + Arrays.toString(propertyValues));
            // System.out.println("databaseSnapshot: "
            // + Arrays.toString(databaseSnapshot));
            // System.out.println("propertyValuesToInsert: "
            // + Arrays.toString(propertyValuesToInsert));
            //
            // for (Entry<?, ?> entry : map.entrySet()) {
            // System.out.println("entry: " + entry.getKey() + " -> "
            // + entry.getValue());
            // }
            // }

            // System.out.println("dirty: " + Arrays.toString(dirty));
            // System.out.println("modified: " + Arrays.toString(modified));
            System.out.println("state: " + Arrays.toString(event.getState()));
            System.out.println("oldState: "
                + Arrays.toString(event.getOldState()));

            return false;
        }
    }
}
