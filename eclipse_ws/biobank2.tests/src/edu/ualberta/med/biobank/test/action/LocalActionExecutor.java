package edu.ualberta.med.biobank.test.action;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.User;

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
        // to behave like we sent this action to the server, we must open a new
        // session and close it before and after this action is executed, every
        // time. The User object should also be loaded fresh from the database.
        Session session = sessionProvider.openSession();
        try {
            Transaction transaction = session.beginTransaction();

            User user = (User) session.createCriteria(User.class)
                .add(Restrictions.eq("id", userId))
                .list().iterator().next();

            ActionContext context = new ActionContext(user, session);

            if (!action.isAllowed(context))
                throw new AccessDeniedException();

            T result = action.run(context);

            transaction.commit();

            return result;
        } catch (RuntimeException e) {
            throw e;
        } finally {
            session.close();
        }
    }
}