package edu.ualberta.med.biobank.common.action.center;

import java.text.MessageFormat;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionCheckException;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.User;

public abstract class CenterSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    protected Integer centerId = null;

    // Specific properties force the programmer only to modify the intended
    // data. A little faster. But disregards version checks. Version checks
    // might apply, but they might not, up to the individual action (e.g.
    // "incrementCountAction" shouldn't care).

    private String name;
    private String nameShort;
    private Address address;
    private Integer aStatusId;
    private Session session = null;

    public void setId(Integer id) {
        this.centerId = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setActivityStatusId(Integer activityStatusId) {
        this.aStatusId = activityStatusId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

    protected IdResult run(User user, Session session,
        SessionUtil sessionUtil, Center center) throws ActionException {
        if (name == null) {
            throw new NullPointerException("name not specified");
        }
        if (nameShort == null) {
            throw new NullPointerException("name short not specified");
        }

        this.session = session;

        performChecks(session);

        // TODO: check permission? (can edit site?)

        // TODO: error checks
        // TODO: version check?

        // TODO: LocalizedMessage in Exception?

        center.setName(name);
        center.setNameShort(nameShort);

        ActivityStatus aStatus = sessionUtil.get(ActivityStatus.class,
            aStatusId);
        center.setActivityStatus(aStatus);

        // TODO: remember to check the address
        center.setAddress(address);

        session.saveOrUpdate(center);
        session.flush();

        // TODO: SHOULD NOT require a flush so that we can get the inserted id
        // if this was an insert, try using a callback that sets the response
        // value instead?

        return new IdResult(center.getId());
    }

    private static final String CENTER_UNIQUE_ATTR_HQL =
        "SELECT COUNT(*) FROM " + Center.class.getName()
            + " s WHERE {0}=? {1}"; //$NON-NLS-1$

    private void performChecks(Session session) throws ActionException {
        if (session == null) {
            throw new NullPointerException("session not initialized");
        }

        if (!peformUniqueQuery("name", name).equals(0L)) {
            throw new ActionCheckException("duplicate name");
        }

        if (!peformUniqueQuery("nameShort", nameShort).equals(0L)) {
            throw new ActionCheckException("duplicate name short");
        }
    }

    private Long peformUniqueQuery(String attribute, String value) {
        String msg;

        if (centerId == null) {
            msg =
                MessageFormat
                    .format(CENTER_UNIQUE_ATTR_HQL, attribute, "");
        } else {
            msg = MessageFormat.format(CENTER_UNIQUE_ATTR_HQL, attribute,
                "AND id<>" + centerId);
        }

        Query query = session.createQuery(msg);
        query.setParameter(0, value);
        return HibernateUtil.getCountFromQuery(query);
    }
}
