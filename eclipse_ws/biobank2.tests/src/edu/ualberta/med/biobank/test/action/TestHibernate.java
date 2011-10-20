package edu.ualberta.med.biobank.test.action;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.model.Patient;

public class TestHibernate {

    private SessionFactory sessionFactory;

    @Before
    public void setUp() throws Exception {
        // A SessionFactory is set up once for an application
        sessionFactory = new Configuration().configure() // configures
                                                         // settings
            // from
            // hibernate.cfg.xml
            .buildSessionFactory();
    }

    @After
    public void tearDown() throws Exception {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    public void testBasicUsage() {
        // create a couple of events...
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Patient p = (Patient) session.get(Patient.class, 4);
        System.out.println(p);
        session.getTransaction().commit();
        session.close();

        // now lets pull events from the database and list them
        // session = sessionFactory.openSession();
        // session.beginTransaction();
        // List result = session.createQuery( "from Event" ).list();
        // for ( Event event : (List<Event>) result ) {
        // System.out.println( "Event (" + event.getDate() + ") : " +
        // event.getTitle() );
        // }
        // session.getTransaction().commit();
        // session.close();
    }

}
