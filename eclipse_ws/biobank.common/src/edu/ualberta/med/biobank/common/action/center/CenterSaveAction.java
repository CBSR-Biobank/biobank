package edu.ualberta.med.biobank.common.action.center;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.comment.CommentUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;

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
    private String commentText;

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

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

    protected IdResult run(ActionContext context, Center center)
        throws ActionException {
        if (name == null) {
            throw new NullPropertyException(Center.class, CenterPeer.NAME);
        }
        if (nameShort == null) {
            throw new NullPropertyException(Center.class, CenterPeer.NAME_SHORT);
        }
        if (address == null) {
            throw new NullPropertyException(Center.class, "address");
        }
        if (aStatusId == null) {
            throw new NullPropertyException(Center.class,
                CenterPeer.ACTIVITY_STATUS);
        }

        // check for duplicate name
        List<ValueProperty<Center>> uniqueValProps =
            new ArrayList<ValueProperty<Center>>();
        uniqueValProps.add(new ValueProperty<Center>(CenterPeer.NAME, name));
        new UniquePreCheck<Center>(Center.class, centerId, uniqueValProps).run(
            context);

        // check for duplicate name short
        uniqueValProps = new ArrayList<ValueProperty<Center>>();
        uniqueValProps.add(new ValueProperty<Center>(CenterPeer.NAME_SHORT,
            nameShort));
        new UniquePreCheck<Center>(Center.class, centerId, uniqueValProps).run(
            context);

        // TODO: check permission? (can edit site?)

        // TODO: error checks
        // TODO: version check?

        // TODO: LocalizedMessage in Exception?

        center.setName(name);
        center.setNameShort(nameShort);

        ActivityStatus aStatus = context.load(ActivityStatus.class, aStatusId);
        center.setActivityStatus(aStatus);

        // TODO: remember to check the address
        center.setAddress(address);
        saveComments(context, center);

        context.getSession().saveOrUpdate(center);
        context.getSession().flush();

        // TODO: SHOULD NOT require a flush so that we can get the inserted id
        // if this was an insert, try using a callback that sets the response
        // value instead?

        return new IdResult(center.getId());
    }

    protected void saveComments(ActionContext context, Center center) {
        Comment comment = CommentUtil.create(context.getUser(), commentText);
        if (comment != null) {
            context.getSession().save(comment);
            center.getCommentCollection().add(comment);
        }
    }
}
