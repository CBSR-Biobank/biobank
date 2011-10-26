package edu.ualberta.med.biobank.common.action.security;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.UserPeer;
import edu.ualberta.med.biobank.model.User;

public class UserGetAction implements Action<User> {

    private static final long serialVersionUID = 1L;
    private String login;

    public UserGetAction(String login) {
        this.login = login;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public User run(User user, Session session) throws ActionException {
        Criteria c = session.createCriteria(User.class.getName()).add(
            Restrictions.eq(UserPeer.LOGIN.getName(), login));
        // FIXME need to fetch all the user graph of object?

        @SuppressWarnings("unchecked")
        List<User> list = c.list();
        if (list.size() == 0)
            throw new ActionException("Problem getting user with login=" //$NON-NLS-1$
                + login);
        return list.get(0);
    }
}
