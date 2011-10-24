package edu.ualberta.med.biobank.common.permission;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.util.SessionUtil;

public abstract class AbstractSessionPermission implements Permission {
    private static final long serialVersionUID = 1L;

    protected final SessionUtil session;

    public AbstractSessionPermission(Session session) {
        this.session = new SessionUtil(session);
    }
}
