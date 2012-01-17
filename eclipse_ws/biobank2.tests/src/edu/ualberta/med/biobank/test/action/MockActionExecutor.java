package edu.ualberta.med.biobank.test.action;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.User;

public class MockActionExecutor implements IActionExecutor {
    private final Session session;
    private User user;

    public MockActionExecutor() {
        this(false);
    }

    public MockActionExecutor(boolean debug) {
        // configure() configures settings from hibernate.cfg.xml found into the
        // biobank-orm jar
        Configuration configuration = new Configuration().configure();

        if (debug) {
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.format_sql", "true");
            configuration.setProperty("hibernate.use_sql_comments", "true");
        }

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.AUTO);
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public <T extends ActionResult> T exec(Action<T> action)
        throws ActionException {
        FlushMode flushMode = session.getFlushMode();
        try {
            Transaction t = session.getTransaction();
            if (t.isActive()) session.getTransaction().commit();

            session.beginTransaction();
            session.setFlushMode(FlushMode.COMMIT);

            ActionContext context = new ActionContext(user, session);

            T result = action.run(context);

            session.getTransaction().commit();
            session.flush();

            return result;
        } finally {
            session.setFlushMode(flushMode);
            session.beginTransaction();
        }
    }
}