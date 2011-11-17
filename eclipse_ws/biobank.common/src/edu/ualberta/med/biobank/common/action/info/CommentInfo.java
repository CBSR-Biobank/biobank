package edu.ualberta.med.biobank.common.action.info;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.User;

public class CommentInfo implements Info {

    private static final long serialVersionUID = -7537167935539051938L;

    public Integer id;
    public String message;
    public Integer userId;
    public Date createdAt;

    public CommentInfo(String message, Date createdAt, Integer userId) {
        this.message = message;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public Comment getCommentModel(Session session) {
        Comment dbComment;
        if (id == null)
            dbComment = new Comment();
        else
            dbComment = ActionUtil.sessionGet(session, Comment.class, id);
        dbComment.setMessage(message);
        User user = ActionUtil.sessionGet(session, User.class, userId);
        dbComment.setUser(user);
        return dbComment;
    }

    public static void setCommentModelCollection(Session session,
        Collection<Comment> modelCommentList, Collection<CommentInfo> newList) {
        if (newList != null) for (CommentInfo info : newList) {
            Comment commentModel = info.getCommentModel(session);
            modelCommentList.add(commentModel);
            // FIXME add a hibernate cascade?
            session.saveOrUpdate(commentModel);
        }
    }

    public static CommentInfo createFromModel(Comment c) {
        CommentInfo ci =
            new CommentInfo(c.getMessage(), c.getCreatedAt(), c.getUser()
                .getId());
        ci.id = c.getId();
        return ci;
    }
}
