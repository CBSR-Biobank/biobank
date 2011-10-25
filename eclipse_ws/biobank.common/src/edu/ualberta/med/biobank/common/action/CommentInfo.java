package edu.ualberta.med.biobank.common.action;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.util.NotAProxy;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.User;

public class CommentInfo implements NotAProxy, Serializable {

    private static final long serialVersionUID = -7537167935539051938L;

    public Integer id;
    public String value;
    public Integer userId;
    public Date createAt;

    public Comment getCommentModel(Session session) {
        Comment dbComment;
        if (id == null)
            dbComment = new Comment();
        else
            dbComment = ActionUtil.sessionGet(session, Comment.class,
                id);
        dbComment.setText(value);
        User user = ActionUtil.sessionGet(session, User.class, userId);
        dbComment.setUser(user);
        return dbComment;
    }

    public static void setCommentModelCollection(Session session,
        Collection<Comment> modelCommentList, Collection<CommentInfo> newList) {
        if (newList != null)
            for (CommentInfo info : newList) {
                Comment commentModel = info.getCommentModel(session);
                modelCommentList.add(commentModel);
                // FIXME add a hibernate cascade?
                session.saveOrUpdate(commentModel);
            }
    }

    public static CommentInfo createFromModel(Comment c) {
        CommentInfo ci = new CommentInfo();
        ci.id = c.getId();
        ci.userId = c.getUser().getId();
        ci.value = c.getText();
        return ci;
    }

}
