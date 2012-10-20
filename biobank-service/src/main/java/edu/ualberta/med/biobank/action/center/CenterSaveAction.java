package edu.ualberta.med.biobank.action.center;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.comment.CommentUtil;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.type.ActivityStatus;

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
    private ActivityStatus activityStatus;
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

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
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
        // TODO: version check?

        center.setDescription(name);
        center.setName(nameShort);

        center.setActivityStatus(activityStatus);

        center.setAddress(address);
        saveComments(context, center);

        context.getSession().saveOrUpdate(center);

        // TODO: SHOULD NOT require a flush so that we can get the inserted id
        // if this was an insert, try using a callback that sets the response
        // value instead?

        return new IdResult(center.getId());
    }

    protected void saveComments(ActionContext context, Center center) {
        Comment comment = CommentUtil.create(context.getUser(), commentText);
        if (comment != null) {
            context.getSession().save(comment);
            center.getComments().add(comment);
        }
    }
}
