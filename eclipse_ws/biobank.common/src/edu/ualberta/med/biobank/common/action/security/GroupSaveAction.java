package edu.ualberta.med.biobank.common.action.security;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.peer.BbGroupPeer;
import edu.ualberta.med.biobank.model.BbGroup;
import edu.ualberta.med.biobank.model.User;

public class GroupSaveAction extends PrincipalSaveAction {

    private static final long serialVersionUID = 1L;

    private String description = null;

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        if (description == null) {
            throw new NullPropertyException(BbGroup.class,
                BbGroupPeer.DESCRIPTION);
        }

        SessionUtil sessionUtil = new SessionUtil(session);
        BbGroup group =
            sessionUtil.get(BbGroup.class, principalId, new BbGroup());

        group.setDescription(description);

        return run(user, session, group);
    }

}
