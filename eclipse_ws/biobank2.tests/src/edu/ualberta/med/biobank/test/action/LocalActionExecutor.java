package edu.ualberta.med.biobank.test.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.test.SessionProvider;

public class LocalActionExecutor implements IActionExecutor {
    private final SessionProvider sessionProvider;
    private Integer userId;

    public LocalActionExecutor(SessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    @Override
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public Integer getUserId() {
        return userId;
    }

    @Override
    public <T extends ActionResult> T exec(Action<T> action)
        throws ActionException {
        action = simulateWire(action);

        // to behave like we sent this action to the server, we must open a new
        // session and close it before and after this action is executed, every
        // time. The User object should also be loaded fresh from the database.
        Session session = sessionProvider.openSession();

        Transaction tx = session.beginTransaction();
        try {
            User user = (User) session
                .createCriteria(User.class)
                .add(Restrictions.idEq(userId))
                .uniqueResult();

            ActionContext context = new ActionContext(user, session, null);

            if (!action.isAllowed(context)) throw new AccessDeniedException();

            T result = action.run(context);

            result = simulateWire(result);

            tx.commit();

            return result;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            session.clear();
            session.close();
        }
    }

    /**
     * Serialize and deserialize the given object to pretend like it was
     * transfered across the wire to a client or server.
     * 
     * @param object
     * @return
     */
    private static <T> T simulateWire(T object) {
        T obj = null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            out.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bos.toByteArray()));

            @SuppressWarnings("unchecked")
            T tmp = (T) in.readObject();

            obj = tmp;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }
}