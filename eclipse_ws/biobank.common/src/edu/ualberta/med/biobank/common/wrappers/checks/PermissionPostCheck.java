package edu.ualberta.med.biobank.common.wrappers.checks;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.BbRightPeer;
import edu.ualberta.med.biobank.common.peer.PermissionPeer;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.actions.LoadModelAction;
import edu.ualberta.med.biobank.model.Permission;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * Checks that the all Privileges of the Permission can be linked to the the
 * Right
 * 
 * @author delphine
 * 
 */
public class PermissionPostCheck extends LoadModelAction<Permission> {
    private static final long serialVersionUID = 1L;

    private static final String COUNT_PRIVILEGES_NOT_FOR_RIGHT = "select count(distinct privileges)"
        + " from "
        + Permission.class.getName()
        + " as perm join perm."
        + PermissionPeer.PRIVILEGE_COLLECTION.getName()
        + " as privileges join perm."
        + Property.concatNames(PermissionPeer.RIGHT,
            BbRightPeer.AVAILABLE_PRIVILEGE_COLLECTION)
        + " as rightPriv where perm."
        + PermissionPeer.ID.getName()
        + " = ? and privileges != rightPriv";

    /**
     * 
     * @param wrapper {@link ModelWrapper} which holds the model object
     * @param properties to ensure uniqueness on
     */
    public PermissionPostCheck(ModelWrapper<Permission> wrapper) {
        super(wrapper);
    }

    @Override
    public void doLoadModelAction(Session session, Permission loadedModel)
        throws BiobankSessionException {
        Query query = session.createQuery(COUNT_PRIVILEGES_NOT_FOR_RIGHT);
        query.setParameter(0, getModel().getId());
        Long count = HibernateUtil.getCountFromQuery(query);
        if (count != 0)
            throw new BiobankSessionException(
                "This permission contains privileges that are not supposed to be associated with this right");
    }

}