package edu.ualberta.med.biobank.common.action;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.model.User;

// TODO: for now extend SessionUtil only because I eventually want to delete SessionUtil and replace it with this context :-)
public class ActionContext extends SessionUtil {
    private final User user;

    public ActionContext(User user, Session session) {
        super(session);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
