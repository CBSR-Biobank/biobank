package edu.ualberta.med.biobank.common.action.center;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.User;

public abstract class CenterSaveAction implements Action<Integer> {
    private static final long serialVersionUID = 1L;

    protected final Integer centerId;

    // Specific properties force the programmer only to modify the intended
    // data. A little faster. But disregards version checks. Version checks
    // might apply, but they might not, up to the individual action (e.g.
    // "incrementCountAction" shouldn't care).

    private String name;
    private String nameShort;
    private Collection<Comment> comment;
    private Address address;
    private Integer aStatusId;

    public CenterSaveAction(Integer centerId) {
        this.centerId = centerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public void setCommentCollection(Collection<Comment> comment) {
        this.comment = comment;
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

    protected Integer runInternal(User user, Session session,
        SessionUtil sessionUtil, Center center) throws ActionException {
        // SessionUtil sessionUtil = new SessionUtil(session);
        // Site site = sessionUtil.get(Site.class, siteId, new Site());

        // TODO: check permission? (can edit site?)

        // TODO: error checks
        // TODO: version check?

        // TODO: LocalizedMessage in Exception?

        center.setName(name);
        center.setNameShort(nameShort);
        center.setCommentCollection(comment);

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

        return center.getId();
    }
}
