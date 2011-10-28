package edu.ualberta.med.biobank.common.action.others;

import java.util.Date;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.util.SessionUtil;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.User;

public class CommentSaveAction implements Action<Integer> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Integer commentId;
    private String message;

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return true;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        SessionUtil sessionUtil = new SessionUtil(session);
        Comment comment = sessionUtil.get(Comment.class, commentId,
            new Comment());

        // FIXME Version check?

        comment.setMessage(message);

        if (comment.getId() == null) {
            comment.setUser(user);
            comment.setCreatedAt(new Date());
        }

        session.saveOrUpdate(comment);

        return comment.getId();
    }
}
